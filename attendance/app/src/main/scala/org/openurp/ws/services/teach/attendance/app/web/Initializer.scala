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

import org.beangle.commons.lang.Strings.replace
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
    addServlet(sc, "/sync")
    addServlet(sc, "/device")
    addServlet(sc, "/activity")
    addServlet(sc, "/coursetable")
    addServlet(sc, "/rate")
    addServlet(sc, "/signin")
    addServlet(sc, "/upload")
    addServlet(sc, "/detail")
    addServlet(sc, "/notice")
    addServlet(sc, "/importer")
    sc.addFilter("characterEncoding", new CharacterEncodingFilter()).addMappingForUrlPatterns(
      EnumSet.of(REQUEST), true, "/*");
    sc.addListener(classOf[ContextListener])
  }
  private def addServlet(sc: ServletContext, mapping: String) {
    val prefix = "/teach/attendance"
    sc.addServlet("app." + replace(prefix + mapping, "/", "."), classOf[DelegatingServletProxy]).addMapping(prefix + mapping)
  }
}