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

import java.sql.{ Date, Time }
import org.beangle.commons.lang.Dates.{ join, now, today }
import org.beangle.commons.lang.Strings.isEmpty
import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.impl.{ ActivityService, DeviceRegistry }
import org.openurp.ws.services.teach.attendance.web.util.{ JsonBuilder, Params }
import org.openurp.ws.services.teach.attendance.web.util.Consts.{ DeviceId, Rule, SigninDate, SigninTime }
import org.openurp.ws.services.teach.attendance.domain.DateFormats.toTimeStr
import org.openurp.ws.services.teach.attendance.web.util.Render.render
import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet

/**
 * 发送当前考勤活动
 * 
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class ActivityServlet extends HttpServlet with Logging {

  var activityService: ActivityService = _

  var deviceRegistry: DeviceRegistry = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val params = Params.require(DeviceId).optional(SigninDate, SigninTime).get(req, Rule)
    // 返回消息，课程代码，课程名称，教师名称，班级名称
    var retmsg, courseCode, courseName, teacherName, className = ""
    var retcode, beginTime, endTime = 0
    if (!params.ok) {
      retmsg = params.msg.values.mkString(";")
      retcode = -1
    } else {
      val devid: Int = params(DeviceId)
      val signinDate: Date = params.get(SigninDate).getOrElse(today)
      val signinTime: Time = params.get(SigninTime).getOrElse(new Time(now.getTime))
      deviceRegistry.get(devid) foreach { device =>
        activityService.getActivity(device.room, join(signinDate, signinTime)) match {
          case Some(activity) =>
            courseCode = activity.course.code
            courseName = activity.course.name
            className = activity.className
            beginTime = activity.beginTime
            endTime = activity.endTime
            teacherName = activity.teacherName
          case None =>
        }
      }
      if (isEmpty(courseCode)) {
        retcode = -1
        retmsg = "当前时间没有课程"
      }
    }
    val jb = new JsonBuilder()
    jb.add("retcode", retcode).add("retmsg", retmsg);
    jb.add("classname", className).add("courseid", courseCode)
    jb.add("currcourse", courseName).add("teacher", teacherName)
    jb.add("coursetime", toTimeStr(beginTime) + "-" + toTimeStr(endTime))
    render(res, jb.mkJson)
  }
}