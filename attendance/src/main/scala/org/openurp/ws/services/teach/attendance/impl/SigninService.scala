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
package org.openurp.ws.services.teach.attendance.impl

import java.sql.Date

import org.beangle.commons.lang.Dates.{ now, toDate }
import org.beangle.commons.lang.Strings.{ isNotEmpty, replace, substring }
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.domain.{ SigninData, SigninInfo }
import org.openurp.ws.services.teach.attendance.domain.AttendTypePolicy
import org.openurp.ws.services.teach.attendance.domain.ShardPolicy.{ activityTable, detailTable, logTable }
import org.openurp.ws.services.teach.attendance.model.AttendType
import org.openurp.ws.services.teach.attendance.domain.DateFormats.{ toCourseTime, toDateStr, toTimeStr }
import org.openurp.ws.services.teach.attendance.web.util.JsonBuilder

import com.google.gson.JsonObject

/**
 * 签到服务
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class SigninService extends Logging {
  var deviceRegistry: DeviceRegistry = _
  var executor: JdbcExecutor = _
  var attendTypePolicy: AttendTypePolicy = _
  var baseDataService: BaseDataService = _

  var daySigninCache: DaySigninCache = _

  def signin(data: SigninData): JsonObject = {
    var retcode, attendTypeId = 0
    // 返回消息，学生班级名称，学生姓名
    var retmsg, classname, custname = ""
    val signinAt = data.signinAt
    val signinOn = toDate(data.signinAt)
    val signinTime = toCourseTime(signinAt)
    deviceRegistry.get(data.devId) match {
      case Some(device) =>
        try {
          val signinInfoOpt = getSigninInfo(signinOn, device.room.id, signinTime, data.cardId)
          if (signinInfoOpt.isEmpty) {
            retmsg = "非本课程学生"
            custname = data.cardId
            log("Wrong place or time {}", data)
          } else {
            val signinInfo = signinInfoOpt.get
            val signId = signinInfo.signinId
            custname = signinInfo.stdName
            attendTypeId = attendTypePolicy.calcAttendType(signinTime, signinInfo)
            if (attendTypeId == 0) {
              retmsg = "考勤未开始"
              log("Time unsuitable {}", data)
            } else {
              val operator = substring(data.cardId + "(" + custname + ")", 0, 30)
              val updatedAt = now
              val rscnt = executor.update("update " + detailTable(signinOn) +
                " set  attend_type_id=?, signin_at=?, dev_id=?, updated_at=?, operator=? where id=? and signin_at is null", attendTypeId, signinAt, device.id, updatedAt, operator, signId)
              retmsg = if (0 == rscnt) "已经签到" else AttendType.names(attendTypeId)
              logDB(data, "ok")
            }
          }
        } catch {
          case e: Exception => {
            retmsg = "未知错误" + e.getMessage()
            logger.error("signin erorr:", e)
            log("Error " + e.getMessage() + " {}", data)
          }
        }
      case None => {
        retmsg = "无法连接，没有对应的教室信息"
        log("Invalid device {}", data)
      }
    }
    val rs = new JsonBuilder
    if (attendTypeId == 0 && isNotEmpty(retmsg)) retcode = -1
    rs.add("retcode", retcode).add("retmsg", retmsg)
    rs.add("classname", classname).add("stuempno", data.cardId)
    rs.add("custname", custname).add("signindate", toDateStr(signinAt))
    rs.add("signintime", toTimeStr(signinAt))
    rs.mkJson
  }

  private def getSigninInfo(signinOn: Date, roomId: Int, signinTime: Int, cardId: String): Option[SigninInfo] = {
    var rs = daySigninCache.get(signinOn, roomId, signinTime, cardId)
    rs match {
      case Some(d) => rs
      case None => {
        val datas = executor.query("select d.id,xs.xm,aa.attend_begin_time,aa.begin_time,aa.end_time from " + detailTable(signinOn) + " d,xsxx_t xs," + activityTable(signinOn) +
          " aa where xs.id=d.std_id and aa.id=d.activity_id and aa.course_date = ? and ? between aa.attend_begin_time and aa.end_time and aa.room_id=? and xs.xh=?", signinOn, signinTime, roomId, cardId)
        if (!datas.isEmpty) {
          val data = datas.head
          val attendBegin = data(2).asInstanceOf[Number].intValue
          val begin = data(3).asInstanceOf[Number].intValue
          val end = data(4).asInstanceOf[Number].intValue
          rs = Some(new SigninInfo(data(0).asInstanceOf[Number].longValue(), data(1).toString, attendBegin, begin, end))
        }
      }
    }
    rs
  }

  private def logDB(data: SigninData, msg: String) {
    executor.update("insert into " + logTable(toDate(data.signinAt)) +
      "(dev_id,card_id,signin_at,created_at,params,remark) values(?,?,?,?,?,?)", data.devId, data.cardId, data.signinAt, now, data.params, msg)
  }

  private def log(msg: String, data: SigninData) {
    logger.info(msg, data)
    logDB(data, replace(msg, "{}", ""))
  }
}