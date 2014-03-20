package org.openurp.ws.services.teach.attendance.app.web

import java.util.Date
import org.beangle.commons.lang.time.Stopwatch
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.impl.{ DeviceRegistry, ActivityService }
import org.openurp.ws.services.teach.attendance.app.util.Consts._
import org.openurp.ws.services.teach.attendance.app.util.DateUtils._
import org.openurp.ws.services.teach.attendance.app.util.Params
import org.openurp.ws.services.teach.attendance.app.util.Render._
import com.google.gson.{ JsonArray, JsonObject }
import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy
import java.util.Calendar

/**
 * 本次考勤的出勤明细
 */
class DetailServlet extends HttpServlet with Logging {
  var deviceRegistry: DeviceRegistry = _
  var activityService: ActivityService = _
  var jdbcExecutor: JdbcExecutor = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val watch = new Stopwatch(true)
    var retcode, devid = 0
    var retmsg, classname = ""
    val json = new JsonObject()
    val array = new JsonArray()

    val params = Params.require(DeviceId).get(req, Rule)
    if (!params.ok) {
      retmsg = params.msg.values.mkString(";")
      retcode = -1
    } else {
      devid = params(DeviceId)
      deviceRegistry.get(devid) foreach { device =>
        val now = Calendar.getInstance
        val activity = activityService.getActivity(device.room, now.getTime)
        activity.foreach { l => classname = l.className }
        val datas = jdbcExecutor.query("select xs.xh,xs.xm,d.signin_at from " + ShardPolicy.detailTableName(now) + " d,xsxx_t xs,t_attend_activities aa where " +
          "d.dev_id=? and to_char(aa.course_date,'yyyyMMdd')=?" +
          " and ? between aa.begin_time and aa.end_time and xs.id=d.std_id and aa.id=d.activity_id", devid, toDateStr(now.getTime), toCourseTime(now))
        datas foreach { data =>
          val attendJson = new JsonObject()
          attendJson.addProperty("stuempno", data(0).toString)
          attendJson.addProperty("custname", data(1).toString)
          val signinAt = data(2).asInstanceOf[Date]
          attendJson.addProperty("signindate", toDateStr(signinAt))
          attendJson.addProperty("signintime", toTimeStr(signinAt))
          array.add(attendJson)
        }
      }
    }
    json.addProperty("retcode", retcode)
    json.addProperty("retmsg", retmsg)
    json.addProperty("devid", devid)
    json.addProperty("classname", classname)
    json.add("list", array)
    render(res, json)
    logger.debug("app.signin using {}", watch)
  }

}