package org.openurp.service.attendance.device.model

import java.util.Date

class Device(val id: Int, val room: String) {
  var syncAt: Date = _
}
