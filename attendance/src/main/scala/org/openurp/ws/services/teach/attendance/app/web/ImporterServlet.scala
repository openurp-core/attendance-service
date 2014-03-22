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

import java.util.Calendar

import org.beangle.commons.lang.Dates.{ toCalendar, toDate }
import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.app.impl.DataImporter
import org.openurp.ws.services.teach.attendance.app.util.{ JsonBuilder, Render }
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.toDateStr

import com.google.gson.{ JsonArray, JsonObject }

import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet

/**
 * 数据导入
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class ImporterServlet extends HttpServlet with Logging {
  var dataImporter: DataImporter = _

  override def service(req: ServletRequest, res: ServletResponse) {
    var from: Calendar = null
    var to: Calendar = null
    val fromStr = req.getParameter("from")
    val toStr = req.getParameter("to")
    if (null != fromStr && null != toStr) {
      from = toCalendar(fromStr)
      to = toCalendar(toStr)
    } else {
      val date = req.getParameter("date")
      if (null != date) {
        from = toCalendar(date)
        to = toCalendar(date)
      }
    }
    val json = new JsonObject
    val array = new JsonArray()
    json.add("list", array)
    if (null != from && null != to) {
      to.add(Calendar.DAY_OF_YEAR, 1)
      while (from.before(to)) {
        val rs = dataImporter.importData(toDate(from))
        val jb = new JsonBuilder()
        jb.add("date", toDateStr(from.getTime))
        jb.add("activityCnt", rs._1)
        jb.add("detailCnt", rs._2)
        array.add(jb.mkJson)
        from.add(Calendar.DAY_OF_YEAR, 1)
      }
    }
    Render.render(res, json)
  }
}