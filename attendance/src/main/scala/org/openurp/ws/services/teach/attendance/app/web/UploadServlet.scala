package org.openurp.ws.services.teach.attendance.app.web

import org.beangle.commons.lang.time.Stopwatch
import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.app.impl.SigninService
import org.openurp.ws.services.teach.attendance.app.model.SigninBean
import org.openurp.ws.services.teach.attendance.app.util.{JsonBuilder, Params}
import org.openurp.ws.services.teach.attendance.app.util.Consts.{CardId, DeviceId, Rule, SigninDate, SigninTime}
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.join

import javax.servlet.{ServletRequest, ServletResponse}
import javax.servlet.http.HttpServlet

/**
 * 数据上传服务
 */
class UploadServlet extends HttpServlet with Logging {
  var signinService: SigninService = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val watch = new Stopwatch(true)
    val params = Params.require(DeviceId, CardId, SigninDate, SigninTime).get(req, Rule)
    val json =
      if (!params.ok) {
        new JsonBuilder().add("retcode", -1).mkJson
      } else {
        signinService.signin(new SigninBean(params(DeviceId), params(CardId), join(params(SigninDate), params(SigninTime))))
      }
    res.getWriter().append(json.get("retcode").getAsString())
    logger.debug("app.signin using {}", watch)
  }

}