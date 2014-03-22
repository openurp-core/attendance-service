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
 * 
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 1.0
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