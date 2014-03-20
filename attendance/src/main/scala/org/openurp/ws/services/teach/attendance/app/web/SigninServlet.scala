package org.openurp.ws.services.teach.attendance.app.web

import java.util.Calendar

import scala.annotation.migration

import org.beangle.commons.lang.time.Stopwatch
import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.app.impl.SigninService
import org.openurp.ws.services.teach.attendance.app.model.SigninBean
import org.openurp.ws.services.teach.attendance.app.util.{JsonBuilder, Params}
import org.openurp.ws.services.teach.attendance.app.util.Consts.{CardId, DeviceId, Rule, SigninDate, SigninTime}
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.{join, toDateStr, toTimeStr}
import org.openurp.ws.services.teach.attendance.app.util.Render.render

import com.google.gson.JsonObject

import javax.servlet.{ServletRequest, ServletResponse}
import javax.servlet.http.HttpServlet
/**
 * 刷卡签到服务
 */
class SigninServlet extends HttpServlet with Logging {
  var signinService: SigninService = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val watch = new Stopwatch(true)
    var json: JsonObject = null
    val params = Params.require(DeviceId, CardId, SigninDate, SigninTime).get(req, Rule)
    if (!params.ok) {
      val signinAt = Calendar.getInstance
      val rs = new JsonBuilder
      rs.add("retcode", -1).add("retmsg", params.msg.values.mkString(";"))
      rs.add("classname", "").add("stuempno", "")
      rs.add("custname", "").add("signindate", toDateStr(signinAt.getTime))
      rs.add("signintime", toTimeStr(signinAt.getTime))
      json = rs.mkJson
    } else {
      json = signinService.signin(new SigninBean(params(DeviceId), params(CardId), join(params(SigninDate), params(SigninTime))))
    }
    render(res, json)
    logger.debug("app.signin using {}", watch)
  }

}