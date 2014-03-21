package org.openurp.ws.services.teach.attendance.app.impl

import org.beangle.commons.lang.Strings.{ replace, isNotEmpty, substring }
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.AttendTypePolicy
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy.{ detailTableName, logTableName }
import org.openurp.ws.services.teach.attendance.app.model.{ AttendType, SigninBean }
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.{ toCourseTime, toDateStr, toTimeStr }
import org.openurp.ws.services.teach.attendance.app.util.JsonBuilder
import com.google.gson.JsonObject
import java.util.Date

/**
 * 签到服务
 */
class SigninService extends Logging {
  var deviceRegistry: DeviceRegistry = _
  var executor: JdbcExecutor = _
  var attendTypePolicy: AttendTypePolicy = _
  var baseDataService: BaseDataService = _
  def signin(data: SigninBean): JsonObject = {
    var retcode, attendTypeId = 0
    // 返回消息，学生班级名称，学生姓名
    var retmsg, classname, custname = ""
    val signinAt = data.signinAt
    val signinTime = toCourseTime(signinAt)
    deviceRegistry.get(data.devId) match {
      case Some(device) =>
        try {
          //取出还没有结束的最近的一条记录
          val datas = executor.query("select d.id,xs.xm,xs.bjid,aa.begin_time,aa.end_time,d.attend_type_id from " + detailTableName(signinAt) + " d," +
            " xsxx_t xs,t_attend_activities aa where xs.id=d.std_id  and aa.id=d.activity_id " +
            " and to_char(aa.course_date,'yyyyMMdd')=? and (? <= aa.end_time) and aa.room_id=? and xs.xh=? order by aa.begin_time", toDateStr(signinAt.getTime()), signinTime, device.room.id, data.cardId)
          if (datas.isEmpty) {
            retmsg = "非本课程学生"
            custname = data.cardId
            log("Wrong place or time {}", data)
          } else {
            //取出时间最早开课的记录
            val first = datas(0)
            val signId = first(0).asInstanceOf[Number].longValue
            custname = first(1).asInstanceOf[String]
            classname = baseDataService.getAdminclassName(first(2).asInstanceOf[Number])
            val begin = first(3).asInstanceOf[Number].intValue
            val end = first(4).asInstanceOf[Number].intValue
            attendTypeId = attendTypePolicy.calcAttendType(signinTime, begin, end)
            if (attendTypeId == 0) {
              log("Time unsuitable {}", data)
            } else {
              val existTypeId = first(5).asInstanceOf[Number].intValue
              if (existTypeId != AttendType.Presence) {
                val operator = substring(data.cardId + "(" + custname + ")", 0, 30)
                val updatedAt = new Date()
                executor.update("update " + detailTableName(signinAt) +
                  " d set d.attend_type_id=?,d.signin_at=?,d.dev_id=?,d.updated_at=?,d.operator=? where id=?", attendTypeId, signinAt, device.id, updatedAt, operator, signId)
                retmsg = AttendType.names(attendTypeId)
              } else {
                attendTypeId = 0
                retmsg = "无效,已经出勤"
              }
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
    rs.add("custname", custname).add("signindate", toDateStr(signinAt.getTime))
    rs.add("signintime", toTimeStr(signinAt.getTime))
    rs.mkJson
  }

  private def logDB(data: SigninBean, msg: String) {
    executor.update("insert into " + logTableName(data.signinAt) + "(dev_id,card_id,signin_at,created_at,params,remark) values(?,?,?,?,?,?)", data.devId, data.cardId, data.signinAt, new Date(),data.params,msg)
  }

  private def log(msg: String, data: SigninBean) {
    logger.info(msg, data)
    logDB(data, replace(msg, "{}", ""))
  }
}