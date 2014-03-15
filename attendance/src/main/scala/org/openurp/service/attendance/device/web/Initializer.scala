package org.openurp.service.attendance.device.web

import javax.servlet.ServletContext
import javax.servlet.ServletException

class Initializer extends org.beangle.commons.web.init.Initializer {

  @throws(classOf[ServletException])
  def onStartup(servletContext: ServletContext) {
    servletContext.addServlet("SyncTimeServlet", classOf[SyncTimeServlet]).addMapping("/synctime")
  }
}