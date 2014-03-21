package org.openurp.ws.services.teach.attendance.app.impl

import java.util.Calendar

import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy.detailTableName
import org.openurp.ws.services.teach.attendance.app.domain.WeekStates
import org.openurp.ws.services.teach.attendance.app.model.AttendType
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.toDateStr

class DataImporter extends Logging {

  var executor: JdbcExecutor = _
  private val lockName = "tmp_attend_importer_lock"

  def importData(cal: Calendar): (Int, Int) = {
    var rs = WeekStates.build(cal)
    val dateStr = toDateStr(cal.getTime())
    val count = executor.queryForInt("select count(*) from user_tables where table_name='" + lockName.toUpperCase + "'")
    var activityCountSum, detailCount = 0
    if (0 == count) {
      logger.info("Attend Importer {}  executing...", dateStr)
      executor.update("create table " + lockName + "(id number(10))")
      try {
        //具体考勤活动数据
        val activityCntList = executor.query("select count(*) from t_attend_activities where to_char(course_date,'yyyyMMdd')=?", dateStr)
        if (activityCntList.isEmpty || activityCntList.head.head.asInstanceOf[Number].intValue == 0) {
          val sql = "insert into t_attend_activities(id,semester_id,lesson_id,course_id,course_date,begin_time,end_time,room_id,course_hours,attend_begin_time)" +
            " select seq_t_attend_activities.nextval,rw.jxrlid,rw.id,rw.kcid,to_date(" + dateStr + ",'yyyyMMdd')," +
            " hd.qssj,hd.jssj,hd.jsid,(hd.jsxj-hd.ksxj+1) ks,to_coursetime(to_minutes(hd.qssj)-15) begin_attend_time from jxrw_t rw ,jxhd_t hd" +
            " where rw.id=hd.jxrwid and hd.zj=? and hd.nf=? and bitand(hd.yxzsz,?)>0 and hd.jsid is not null and hd.zyyy=1"
          rs._2.foreach { data =>
            val activityCount = executor.update(sql, rs._1, data._1, data._2)
            activityCountSum += activityCount
            logger.info("Importe {} attend activities", activityCount)
          }
        }
        //上课名单数据
        val detailCntList = executor.query("select count(*) from " + detailTableName(cal) + " d,t_attend_activities aa where d.activity_id=aa.id " +
          " and to_char(aa.course_date,'yyyyMMdd')=?", dateStr)
        if (detailCntList.isEmpty || detailCntList.head.head.asInstanceOf[Number].intValue == 0) {
          val detailSql = "insert into " + detailTableName(cal) + "(id,std_id,activity_id,attend_type_id)" +
            " select seq_t_attend_details.nextval,jxb.xsid,aa.id," + AttendType.Absenteeism + " from t_attend_activities aa,jxbxs_t jxb" +
            " where to_char(aa.course_date,'yyyyMMdd')=? and aa.lesson_id=jxb.jxrwid"
          detailCount = executor.update(detailSql, dateStr)
          logger.info("Importe {} attend details.", detailCount)
        }
      } finally {
        executor.update("drop table " + lockName)
      }
    }
    (activityCountSum, detailCount)
  }
}