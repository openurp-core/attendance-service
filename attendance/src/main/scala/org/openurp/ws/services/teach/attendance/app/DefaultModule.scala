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
package org.openurp.ws.services.teach.attendance.app

import org.beangle.commons.inject.bind.AbstractBindModule
import org.beangle.commons.jndi.JndiObjectFactory
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.AttendTypePolicy
import org.openurp.ws.services.teach.attendance.app.impl.{ ActivityService, AppConfig, BaseDataService, DataImporter, DeviceRegistry, EhcacheManager, ShardDaemon, SigninService }
import org.openurp.ws.services.teach.attendance.app.web.{ ActivityServlet, CourseTableServlet, DetailServlet, DeviceServlet, ImporterServlet, NoticeServlet, RateServlet, SigninServlet, SyncServlet, UploadServlet }

/**
 * 缺省绑定
 * 
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class DefaultModule extends AbstractBindModule {

  protected def doBinding() {
    bind("app.sync", classOf[SyncServlet])
    bind("app.device", classOf[DeviceServlet])
    bind("app.signin", classOf[SigninServlet])
    bind("app.coursetable", classOf[CourseTableServlet])
    bind("app.activity", classOf[ActivityServlet])
    bind("app.upload", classOf[UploadServlet])
    bind("app.rate", classOf[RateServlet])
    bind("app.detail", classOf[DetailServlet])
    bind("app.notice", classOf[NoticeServlet])
    bind("app.importer", classOf[ImporterServlet])

    bind("dataSource", classOf[JndiObjectFactory]).property("jndiName", "jdbc/teach").property("resourceRef", "true")
    bind(classOf[JdbcExecutor]).constructor(ref("dataSource")) //.property("showSql", "true")
    bind(classOf[DeviceRegistry])
    bind(classOf[EhcacheManager])
    bind(classOf[ShardDaemon]).lazyInit(false)
    bind(classOf[AttendTypePolicy])
    bind(classOf[ActivityService], classOf[SigninService])
    bind(classOf[AppConfig], classOf[BaseDataService])
    bind(classOf[DataImporter])
  }
}