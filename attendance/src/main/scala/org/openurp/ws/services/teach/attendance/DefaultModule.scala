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
package org.openurp.ws.services.teach.attendance

import org.beangle.commons.inject.bind.AbstractBindModule
import org.beangle.commons.jndi.JndiObjectFactory
import org.beangle.commons.lang.Strings.{ replace, lowerCase, substringBefore }
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.domain.AttendTypePolicy
import org.openurp.ws.services.teach.attendance.impl.{ ActivityService, AppConfig, BaseDataService, DataImporter, DaySigninCache, DeviceRegistry, EhcacheManager, ShardDaemon, SigninService }
import org.openurp.ws.services.teach.attendance.web.app.{ ActivityServlet, CourseTableServlet, DetailServlet, DeviceServlet, ImporterServlet, NoticeServlet, RateServlet, SigninServlet, SyncServlet, UploadServlet }

/**
 * 缺省绑定
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class DefaultModule extends AbstractBindModule {

  protected def doBinding() {
    bindServlet(classOf[SyncServlet], classOf[DeviceServlet], classOf[SigninServlet], classOf[CourseTableServlet])
    bindServlet(classOf[ActivityServlet], classOf[UploadServlet], classOf[RateServlet], classOf[DetailServlet])
    bindServlet(classOf[NoticeServlet], classOf[ImporterServlet])

    bind("dataSource", classOf[JndiObjectFactory]).property("jndiName", "jdbc/ws-services-teach-attendance").property("resourceRef", "true")
    bind(classOf[JdbcExecutor]).constructor(ref("dataSource")) //.property("showSql", "true")
    bind(classOf[DeviceRegistry])
    bind(classOf[EhcacheManager])
    bind(classOf[ShardDaemon], classOf[DaySigninCache]).lazyInit(false)
    bind(classOf[AttendTypePolicy])
    bind(classOf[ActivityService], classOf[SigninService])
    bind(classOf[AppConfig], classOf[BaseDataService])
    bind(classOf[DataImporter])
  }

  private def bindServlet(classes: Class[_]*) {
    classes foreach { clazz =>
      val packageName = clazz.getPackage().getName()
      var name = replace(packageName, "org.openurp.ws.services.", "")
      name = replace(name, ".web", "") + "." + lowerCase(substringBefore(clazz.getSimpleName(), "Servlet"))
      bind(name, clazz)
    }
  }
}