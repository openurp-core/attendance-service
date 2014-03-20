package org.openurp.ws.services.teach.attendance.app.web

import java.util.EnumSet

import org.beangle.commons.web.filter.CharacterEncodingFilter
import org.beangle.commons.web.servlet.DelegatingServletProxy
import org.beangle.spring.web.ContextListener

import javax.servlet.{ ServletContext, ServletException }
import javax.servlet.DispatcherType.REQUEST

class Initializer extends org.beangle.commons.web.init.Initializer {

  @throws(classOf[ServletException])
  def onStartup(sc: ServletContext) {
    sc.addServlet("app.sync", classOf[DelegatingServletProxy]).addMapping("/app/synctime")
    sc.addServlet("app.device", classOf[DelegatingServletProxy]).addMapping("/app/device")
    sc.addServlet("app.activity", classOf[DelegatingServletProxy]).addMapping("/app/currcourseinfo")
    sc.addServlet("app.coursetable", classOf[DelegatingServletProxy]).addMapping("/app/courseinfo")
    sc.addServlet("app.rate", classOf[DelegatingServletProxy]).addMapping("/app/attrate")
    sc.addServlet("app.signin", classOf[DelegatingServletProxy]).addMapping("/app/signin")
    sc.addServlet("app.upload", classOf[DelegatingServletProxy]).addMapping("/app/dataupload")
    sc.addServlet("app.detail", classOf[DelegatingServletProxy]).addMapping("/app/signindetail")
    sc.addServlet("app.notice", classOf[DelegatingServletProxy]).addMapping("/app/scrollingmsg")

    sc.addFilter("characterEncoding", new CharacterEncodingFilter()).addMappingForUrlPatterns(
      EnumSet.of(REQUEST), true, "/*");

    sc.addListener(classOf[ContextListener])
  }
}