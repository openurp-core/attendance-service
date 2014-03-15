package org.openurp.service.attendance.device

import org.beangle.commons.inject.bind.AbstractBindModule
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.service.attendance.device.web.SyncTimeServlet
import org.beangle.commons.jndi.JndiObjectFactory
import org.openurp.service.attendance.device.service.DeviceRegistry
import org.beangle.commons.cache.concurrent.ConcurrentMapCacheManager

class DefaultModule extends AbstractBindModule {

  protected def doBinding() {
    bind(classOf[SyncTimeServlet]);
    bind("dataSource", classOf[JndiObjectFactory]).property("jndiName", "jdbc/attendance")
    bind(classOf[JdbcExecutor]).constructor(ref("dataSource"))
    bind(classOf[DeviceRegistry])
    bind(classOf[ConcurrentMapCacheManager])
  }
}