/*
 * OpenURP, Open University Resouce Planning
 *
 * Copyright (c) 2013-2014, OpenURP Software.
 *
 * OpenURP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenURP is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Beangle.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.ws.services.teach.attendance.app.web

import java.util.EnumSet

import org.beangle.commons.web.filter.CharacterEncodingFilter
import org.beangle.commons.web.servlet.DelegatingServletProxy
import org.beangle.spring.web.ContextListener

import javax.servlet.{ ServletContext, ServletException }
import javax.servlet.DispatcherType.REQUEST
/**
 * 符合Servlet3规范的web初始化
 * 
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class Initializer extends org.beangle.commons.web.init.Initializer {

  @throws(classOf[ServletException])
  def onStartup(sc: ServletContext) {
    sc.addServlet("app.sync", classOf[DelegatingServletProxy]).addMapping("/app/synctime")
    sc.addServlet("app.device", classOf[DelegatingServletProxy]).addMapping("/app/device")
    sc.addServlet("app.activity", classOf[DelegatingServletProxy]).addMapping("/app/activity")
    sc.addServlet("app.coursetable", classOf[DelegatingServletProxy]).addMapping("/app/coursetable")
    sc.addServlet("app.rate", classOf[DelegatingServletProxy]).addMapping("/app/rate")
    sc.addServlet("app.signin", classOf[DelegatingServletProxy]).addMapping("/app/signin")
    sc.addServlet("app.upload", classOf[DelegatingServletProxy]).addMapping("/app/upload")
    sc.addServlet("app.detail", classOf[DelegatingServletProxy]).addMapping("/app/detail")
    sc.addServlet("app.notice", classOf[DelegatingServletProxy]).addMapping("/app/notice")
    sc.addServlet("app.importer", classOf[DelegatingServletProxy]).addMapping("/app/importer")

    sc.addFilter("characterEncoding", new CharacterEncodingFilter()).addMappingForUrlPatterns(
      EnumSet.of(REQUEST), true, "/*");

    sc.addListener(classOf[ContextListener])
  }
}