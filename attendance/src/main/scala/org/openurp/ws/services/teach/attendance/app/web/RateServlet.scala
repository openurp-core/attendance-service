package org.openurp.ws.services.teach.attendance.app.web

import java.{ util => ju }
import java.util.{ Calendar, Date }

import org.beangle.commons.lang.Strings.isEmpty
import org.beangle.commons.lang.time.Stopwatch
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.AttendTypePolicy
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy.{ detailTableName, logTableName }
import org.openurp.ws.services.teach.attendance.app.impl.{ DeviceRegistry, ActivityService, SigninService }
import org.openurp.ws.services.teach.attendance.app.model.SigninBean
import org.openurp.ws.services.teach.attendance.app.util.{ JsonBuilder, Params }
import org.openurp.ws.services.teach.attendance.app.util.Consts.{ CardId, DeviceId, Rule, SigninDate, SigninTime }
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.{ join, toCourseTime, toDateStr, toTimeStr }
import org.openurp.ws.services.teach.attendance.app.util.Render.render

import com.google.gson.JsonObject

import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet
/**
 * 该教室目前这次出勤的统计
 */
class RateServlet extends HttpServlet with Logging {

  var jdbcExecutor: JdbcExecutor = _

  var activityService: ActivityService = _

  var deviceRegistry: DeviceRegistry = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val watch = new Stopwatch(true)
    //返回码，总数，出席人数，出勤率
    var retcode, totlenum, attendnum, attendance = 0
    val now = Calendar.getInstance()
    var retmsg, className = ""
    val params = Params.require(DeviceId).get(req, Rule)
    var devid: Int = 0
    if (!params.ok) {
      retmsg = params.msg.values.mkString(";")
      retcode = -1
    } else {
      devid = params(DeviceId)
      deviceRegistry.get(devid) foreach { device =>
        activityService.getActivity(device.room, new Date()) foreach { l =>
          className = l.className
        }
      }
      if (isEmpty(className)) {
        retcode = -1
        retmsg = "该时间没有课程"
      } else {
        // 根据教室id,考勤时间来获取该教室已打卡人数
        val datas = jdbcExecutor.query("select count(*),count(case when attend_type_id<>2 then 1 else 0 end) from " + detailTableName(now) +
          " d,t_attend_activities a where a.id=d.activity_id and d.dev_id=?" +
          " and to_char(a.course_date,'yyyyMMdd')=? " +
          " and ? between a.begin_time and a.end_time", devid, toDateStr(now.getTime()), toCourseTime(now))
        datas.foreach { data =>
          totlenum = data(0).asInstanceOf[Number].intValue()
          attendnum = data(1).asInstanceOf[Number].intValue()
        }
      }
    }

    if (totlenum > 0) attendance = Math.round(100.0f * attendnum / totlenum)
    val rs = new JsonBuilder
    rs.add("retcode", retcode).add("retmsg", retmsg)
    rs.add("devid", devid).add("classname", className)
    rs.add("totlenum", totlenum).add("attendnum", attendnum)
    rs.add("attendnum", attendnum).add("attendance", attendance)
    render(res, rs.mkJson)
    logger.debug("app.signin using {}", watch)
  }

}