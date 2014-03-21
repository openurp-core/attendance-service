package org.openurp.ws.services.teach.attendance.app.web

import javax.servlet.http.HttpServlet
import org.openurp.ws.services.teach.attendance.app.impl.DataImporter
import org.beangle.commons.logging.Logging
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import java.util.Calendar
import org.openurp.ws.services.teach.attendance.app.util.JsonBuilder
import org.openurp.ws.services.teach.attendance.app.util.DateUtils._
import org.openurp.ws.services.teach.attendance.app.util.Render
import java.sql.Date
import com.google.gson.JsonObject
import com.google.gson.JsonArray

/**
 * 数据导入
 */
class ImporterServlet extends HttpServlet with Logging {
  var dataImporter: DataImporter = _

  override def service(req: ServletRequest, res: ServletResponse) {
    var from: Calendar = null
    var to: Calendar = null
    val fromDate = req.getParameter("from")
    val toDate = req.getParameter("to")
    if (null != fromDate && null != toDate) {
      from = toCalendar(fromDate)
      to = toCalendar(toDate)
    } else {
      val now = req.getParameter("date")
      if (null != now) {
        from = toCalendar(now)
        to = toCalendar(now)
      }
    }
    val json = new JsonObject
    val array = new JsonArray()
    json.add("list", array)
    if (null != from && null != to) {
      to.add(Calendar.DAY_OF_YEAR, 1)
      while (from.before(to)) {
        val rs = dataImporter.importData(from)
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