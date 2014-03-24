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

import java.sql.{ Date, Time }

import org.beangle.commons.lang.Dates.{ join, now, today }
import org.beangle.commons.lang.Strings.isEmpty
import org.beangle.commons.lang.time.Stopwatch
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy.detailTableName
import org.openurp.ws.services.teach.attendance.app.impl.{ ActivityService, DeviceRegistry }
import org.openurp.ws.services.teach.attendance.app.model.AttendType
import org.openurp.ws.services.teach.attendance.app.util.{ JsonBuilder, Params }
import org.openurp.ws.services.teach.attendance.app.util.Consts.{ DeviceId, Rule, SigninDate, SigninTime }
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.toCourseTime
import org.openurp.ws.services.teach.attendance.app.util.Render.render

import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet
/**
 * 该教室目前这次出勤的统计
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class RateServlet extends HttpServlet with Logging {

  var jdbcExecutor: JdbcExecutor = _

  var activityService: ActivityService = _

  var deviceRegistry: DeviceRegistry = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val watch = new Stopwatch(true)
    //返回码，总数，出席人数，出勤率
    var retcode, totlenum, attendnum, attendance = 0
    var retmsg, className = ""
    val params = Params.require(DeviceId).optional(SigninDate, SigninTime).get(req, Rule)
    var devid: Int = 0
    if (!params.ok) {
      retmsg = params.msg.values.mkString(";")
      retcode = -1
    } else {
      devid = params(DeviceId)
      val signinDate: Date = params.get(SigninDate).getOrElse(today)
      val signinTime: Time = params.get(SigninTime).getOrElse(new Time(now.getTime))
      var roomId: Int = 0
      deviceRegistry.get(devid) foreach { device =>
        roomId = device.room.id
        activityService.getActivity(device.room, join(signinDate, signinTime)) foreach { l =>
          className = l.className
        }
      }
      if (isEmpty(className)) {
        retcode = -1
        retmsg = "该时间没有课程"
      } else {
        // 根据教室id,考勤时间来获取该教室已打卡人数
        val datas = jdbcExecutor.query("select count(*),sum(case when attend_type_id<>" + AttendType.Absenteeism + " then 1 else 0 end) from " + detailTableName(signinDate) +
          " d,t_attend_activities a where a.id=d.activity_id and a.room_id = ? " +
          " and a.course_date = ? and ? between a.attend_begin_time and a.end_time", roomId, signinDate, toCourseTime(signinTime))
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