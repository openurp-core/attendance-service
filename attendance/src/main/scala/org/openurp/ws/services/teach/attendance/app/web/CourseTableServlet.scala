package org.openurp.ws.services.teach.attendance.app.web

import org.beangle.commons.lang.Strings.{ isEmpty, replace }
import org.beangle.commons.lang.time.Stopwatch
import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.app.impl.{ AppConfig, DeviceRegistry, BaseDataService }
import org.openurp.ws.services.teach.attendance.app.util.Consts.{ DeviceId, Rule }
import org.openurp.ws.services.teach.attendance.app.util.Params
import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse
import java.util.Date

/**
 * 发送教室表地址
 */
class CourseTableServlet extends HttpServlet with Logging {

  var baseDataService: BaseDataService = _

  var deviceRegistry: DeviceRegistry = _

  var appConfig: AppConfig = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val params = Params.require(DeviceId).get(req, Rule)
    var rs = ""
    if (!params.ok) {
      //rs = params.msg.values.mkString(";")
      rs = "devid needed!"
    } else {
      val devid: Int = params(DeviceId)
      deviceRegistry.get(devid) foreach { d =>
        var url = appConfig.courseURL
        baseDataService.getSemesterId(new Date()) foreach { semesterId =>
          url = replace(url, "${semesterId}", String.valueOf(semesterId))
          rs = replace(url, "${roomId}", String.valueOf(d.room.id))
        }
      }
    }
    if (!isEmpty(rs)) res.getWriter().append(rs)
  }
}