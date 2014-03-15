package org.openurp.ws.teach.attendance.app.web

import javax.servlet.ServletContext
import javax.servlet.ServletException
import org.beangle.commons.web.servlet.DelegatingServletProxy

class Initializer extends org.beangle.commons.web.init.Initializer {

  @throws(classOf[ServletException])
  def onStartup(servletContext: ServletContext) {
    servletContext.addServlet("app.synctime", classOf[DelegatingServletProxy]).addMapping("/app/synctime")
    servletContext.addServlet("app.device", classOf[DelegatingServletProxy]).addMapping("/app/device")
  }
}