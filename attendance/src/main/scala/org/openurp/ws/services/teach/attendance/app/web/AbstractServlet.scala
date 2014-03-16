package org.openurp.ws.services.teach.attendance.app.web

import javax.servlet.http.HttpServlet
import org.beangle.commons.logging.Logging
import javax.servlet.ServletRequest

abstract class AbstractServlet extends HttpServlet with Logging {

  def getDeviceId(req: ServletRequest): String = req.getParameter("devid")
  
}