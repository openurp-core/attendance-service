package org.openurp.ws.services.teach.attendance.app.service

import org.beangle.commons.bean.Initializing
import org.beangle.commons.cache.{ Cache, CacheManager }
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.model.Device
import java.util.Date
import org.beangle.commons.logging.Logging
/**
 * 设备注册表
 */
class DeviceRegistry extends Initializing with Logging {
  var executor: JdbcExecutor = _
  var cacheManager: CacheManager = _
  private var cache: Cache[Int, Device] = _

  def get(devid: Int): Option[Device] = {
    var rs = cache.get(devid)
    val rooms = executor.query("select t.jsmc from  DEVICE_JS dc inner join JCXX_JS_T t on  dc.jsid=t.id  where dc.devid =?", devid)
    if (rs.isEmpty) {
      for (names <- rooms; name <- names) {
        val device = new Device(devid, name.toString)
        logger.info("register device {}", devid)
        cache.put(devid, device)
        rs = Some(device)
      }
    }
    rs
  }

  def unregister(devId: Int) {
    cache.evict(devId)
  }

  def loadAll(): Seq[Device] = {
    val stats = executor.query("select dc.devid,t.jsmc,dc.qdsj from  DEVICE_JS dc inner join JCXX_JS_T t on  dc.jsid=t.id order by dc.qdsj")
    val devices = for (stat <- stats) yield new Device(stat(0).asInstanceOf[Number].intValue(), stat(1).asInstanceOf[String], stat(2).asInstanceOf[Date])
    for (device <- devices if (cache.get(device.id).isEmpty)) {
      logger.info("register device {}", device.id)
      cache.put(device.id, device)
    }
    devices
  }

  def init() {
    cache = cacheManager.getCache("device")
  }

}