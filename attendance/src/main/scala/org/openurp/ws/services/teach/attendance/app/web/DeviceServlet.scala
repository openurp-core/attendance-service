package org.openurp.ws.services.teach.attendance.app.web

import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.app.impl.DeviceRegistry
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.toDateTimeStr
import org.openurp.ws.services.teach.attendance.app.util.Render

import com.google.gson.{JsonArray, JsonObject}

import javax.servlet.{ServletRequest, ServletResponse}
import javax.servlet.http.HttpServlet

/**
 * 查看所有设备情况
 */
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
      deviceJson.addProperty("room", device.room.toString)
      deviceJson.addProperty("syncAt", toDateTimeStr(device.syncAt))
      array.add(deviceJson)
    }
    Render.render(res, json)
  }
}