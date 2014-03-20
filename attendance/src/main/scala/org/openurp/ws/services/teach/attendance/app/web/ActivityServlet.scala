package org.openurp.ws.services.teach.attendance.app.web

import java.util.{ Calendar, Date }

import org.beangle.commons.lang.Strings.isEmpty
import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.app.impl.{ ActivityService, DeviceRegistry }
import org.openurp.ws.services.teach.attendance.app.util.{ JsonBuilder, Params }
import org.openurp.ws.services.teach.attendance.app.util.Consts.{ DeviceId, Rule }
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.toTimeStr
import org.openurp.ws.services.teach.attendance.app.util.Render.render

import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet

/**
 * 发送当前考勤活动
 */
class ActivityServlet extends HttpServlet with Logging {

  var activityService: ActivityService = _

  var deviceRegistry: DeviceRegistry = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val params = Params.require(DeviceId).get(req, Rule)
    // 返回消息，课程代码，课程名称，教师名称，班级名称
    var retmsg, courseCode, courseName, teacherName, className = ""
    var retcode, beginTime, endTime = 0
    if (!params.ok) {
      retmsg = params.msg.values.mkString(";")
      retcode = -1
    } else {
      val devid: Int = params(DeviceId)
      val now = Calendar.getInstance
      deviceRegistry.get(devid) foreach { device =>
        activityService.getActivity(device.room, now.getTime()) match {
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