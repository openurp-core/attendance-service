package org.openurp.service.attendance.device.web

import org.beangle.commons.lang.Numbers
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.service.attendance.device.service.DeviceRegistry
import org.openurp.service.attendance.device.util.{JsonBuilder, Render}
import org.openurp.service.attendance.device.util.DateFormatUtils.{toDate, toTime}
import javax.servlet.{ServletRequest, ServletResponse}
import javax.servlet.http.HttpServlet

class SyncTimeServlet extends HttpServlet with Logging {

  var executor: JdbcExecutor = _

  var deviceRegistry: DeviceRegistry = _

  override def service(req: ServletRequest, res: ServletResponse) {
    var retcode: Int = -1
    var retmsg, room = ""
    val devid = Numbers.toInt(req.getParameter("devid"))
    if (devid <= 0) {
      retmsg = "无效的设备编号" + req.getParameter("devid")
    } else {
      deviceRegistry.get(devid) match {
        case Some(d) => {
          if (executor.update("update DEVICE_JS set qdsj=sysdate where devid=?", devid) < 1) {
            deviceRegistry.unregister(devid)
            retmsg = "无法连接，没有对应的教室信息";
          } else {
            room = d.room
            retcode = 0
          }
        }
        case None =>
          retmsg = "无法连接，没有对应的教室信息";
      }
    }
    //FIXME rename classname to roomname
    val rs = new JsonBuilder
    rs.add("retcode", retcode).add("retmsg", retmsg);
    rs.add("classname", room).add("devid", devid).add("serverdate", toDate()).add("servertime", toTime())
    Render.render(res, rs)
  }
}