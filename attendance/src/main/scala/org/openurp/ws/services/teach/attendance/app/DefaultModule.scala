package org.openurp.ws.services.teach.attendance.app

import org.beangle.commons.inject.bind.AbstractBindModule
import org.beangle.commons.jndi.JndiObjectFactory
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.AttendTypePolicy
import org.openurp.ws.services.teach.attendance.app.impl.{ ActivityService, AppConfig, BaseDataService, DeviceRegistry, EhcacheManager, ShardDaemon, SigninService }
import org.openurp.ws.services.teach.attendance.app.web.{ ActivityServlet, CourseTableServlet, DetailServlet, DeviceServlet, NoticeServlet, RateServlet, SigninServlet, SyncServlet, UploadServlet }
import org.openurp.ws.services.teach.attendance.app.impl.DataImporter
import org.openurp.ws.services.teach.attendance.app.impl.DataImporterDaemon
import org.openurp.ws.services.teach.attendance.app.web.ImporterServlet

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
    bind(classOf[JdbcExecutor]).constructor(ref("dataSource"))//.property("showSql", "true")
    bind(classOf[DeviceRegistry])
    bind(classOf[EhcacheManager])
    bind(classOf[ShardDaemon]).lazyInit(false)
    bind(classOf[AttendTypePolicy])
    bind(classOf[ActivityService], classOf[SigninService])
    bind(classOf[AppConfig], classOf[BaseDataService])
    bind(classOf[DataImporter], classOf[DataImporterDaemon])
  }
}