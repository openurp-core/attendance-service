package org.openurp.ws.services.teach.attendance.app

import org.beangle.commons.inject.bind.AbstractBindModule
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.web.SyncTimeServlet
import org.beangle.commons.jndi.JndiObjectFactory
import org.openurp.ws.services.teach.attendance.app.service.DeviceRegistry
import org.beangle.commons.cache.concurrent.ConcurrentMapCacheManager
import org.openurp.ws.services.teach.attendance.app.web.DeviceServlet

class DefaultModule extends AbstractBindModule {

  protected def doBinding() {
    bind("app.synctime", classOf[SyncTimeServlet])
    bind("app.device", classOf[DeviceServlet])
    bind("dataSource", classOf[JndiObjectFactory]).property("jndiName", "jdbc/teach").property("resourceRef", "true")
    bind(classOf[JdbcExecutor]).constructor(ref("dataSource"))
    bind(classOf[DeviceRegistry])
    bind(classOf[ConcurrentMapCacheManager])
  }
}