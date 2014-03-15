package org.openurp.service.attendance.device.service

import org.beangle.commons.bean.Initializing
import org.beangle.commons.cache.{ Cache, CacheManager }
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.service.attendance.device.model.Device
/**
 * 设备注册表
 */
class DeviceRegistry extends Initializing {
  var executor: JdbcExecutor = _
  var cacheManager: CacheManager = _
  private var cache: Cache[Int, Device] = _

  def get(devid: Int): Option[Device] = {
    var rs = cache.get(devid)
    if (rs.isEmpty) {
      val rooms = executor.query("select t.jsmc from  DEVICE_JS dc inner join JCXX_JS_T t on  dc.jsid=t.id  where dc.devid =?", devid)
      for (names <- rooms; name <- names)
        rs = Some(new Device(devid, name.toString))
      rs
    } else rs
  }

  def unregister(devId: Int) {
    cache.evict(devId)
  }

  def init() {
    cache = cacheManager.getCache("device")
  }

}