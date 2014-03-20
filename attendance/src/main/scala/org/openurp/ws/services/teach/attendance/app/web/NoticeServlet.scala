package org.openurp.ws.services.teach.attendance.app.web

import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.app.impl.SigninService
import org.openurp.ws.services.teach.attendance.app.util.{ JsonBuilder, Params }
import org.openurp.ws.services.teach.attendance.app.util.Consts.{ DeviceId, Rule }
import org.openurp.ws.services.teach.attendance.app.util.Render.render

import com.google.gson.JsonObject

import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet
/**
 * 滚动的通知公告
 * FIXME 未实现
 */
class NoticeServlet extends HttpServlet with Logging {
  var signinService: SigninService = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val params = Params.require(DeviceId).get(req, Rule)
    var devid: Int = 0
    var retmsg: String = ""
    if (!params.ok) params.msg.values.mkString(";")

    devid = params(DeviceId)
    val rs = new JsonBuilder
    rs.add("retcode", -1).add("retmsg", params.msg.values.mkString(";"))
    rs.add("devid", devid)
    rs.add("classname", "").add("msgtype", 1)
    rs.add("msg", "").add("msgdata", "")
    render(res, rs.mkJson)
  }

}