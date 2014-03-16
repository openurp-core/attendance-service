package org.openurp.ws.services.teach.attendance.app.web

import javax.servlet.http.HttpServlet
import org.beangle.commons.logging.Logging
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import org.openurp.ws.services.teach.attendance.app.service.DeviceRegistry
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import org.openurp.ws.services.teach.attendance.app.util.DateFormatUtils.toDateTime
import org.openurp.ws.services.teach.attendance.app.util.Render

class DeviceServlet extends HttpServlet with Logging {

  var deviceRegistry: DeviceRegistry = _

  override def service(req: ServletRequest, res: ServletResponse) {

    val devices = deviceRegistry.loadAll()
    val json = new JsonObject()
    val array = new JsonArray()
    json.add("devices", array)
    for (device <- devices) {
      val deviceJson = new JsonObject()
      deviceJson.addProperty("id", device.id)
      deviceJson.addProperty("room", device.room)
      deviceJson.addProperty("syncAt", toDateTime(device.syncAt))
      array.add(deviceJson)
    }
    Render.render(res, json)
  }
}