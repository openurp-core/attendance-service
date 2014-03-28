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
package org.openurp.ws.services.teach.attendance.web.app

import org.beangle.commons.lang.Dates
import org.beangle.commons.lang.Strings.{isEmpty, replace}
import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.impl.{AppConfig, BaseDataService, DeviceRegistry}
import org.openurp.ws.services.teach.attendance.web.util.Consts.{DeviceId, Rule}
import org.openurp.ws.services.teach.attendance.web.util.Params
import javax.servlet.{ServletRequest, ServletResponse}
import javax.servlet.http.HttpServlet


/**
 * 发送教室表地址
 * 
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class CourseTableServlet extends HttpServlet with Logging {

  var baseDataService: BaseDataService = _

  var deviceRegistry: DeviceRegistry = _

  var appConfig: AppConfig = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val params = Params.require(DeviceId).get(req, Rule)
    var rs = ""
    if (!params.ok) {
      //rs = params.msg.values.mkString(";")
      rs = "devid needed!"
    } else {
      val devid: Int = params(DeviceId)
      deviceRegistry.get(devid) foreach { d =>
        var url = appConfig.courseURL
        baseDataService.getSemesterId(Dates.today) foreach { semesterId =>
          url = replace(url, "${semesterId}", String.valueOf(semesterId))
          rs = replace(url, "${roomId}", String.valueOf(d.room.id))
        }
      }
    }
    if (!isEmpty(rs)) res.getWriter().append(rs)
  }
}