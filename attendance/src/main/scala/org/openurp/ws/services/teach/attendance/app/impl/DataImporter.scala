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
package org.openurp.ws.services.teach.attendance.app.impl

import java.sql.Date

import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy.detailTableName
import org.openurp.ws.services.teach.attendance.app.domain.WeekStates
import org.openurp.ws.services.teach.attendance.app.model.AttendType
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.toDateStr
/**
 * 考勤数据导入服务
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 1.0
 */
class DataImporter extends Logging {

  var executor: JdbcExecutor = _
  private val lockName = "tmp_attend_importer_lock"

  def importData(date: Date): (Int, Int) = {
    var rs = WeekStates.build(date)
    val count = executor.queryForInt("select count(*) from user_tables where table_name='" + lockName.toUpperCase + "'")
    var activityCountSum, detailCount = 0
    if (0 == count) {
      logger.info("Attend Importer {}  executing...", toDateStr(date))
      executor.update("create table " + lockName + "(id number(10))")
      try {
        //具体考勤活动数据
        val activityCntList = executor.query("select count(*) from t_attend_activities where course_date=?", date)
        if (activityCntList.isEmpty || activityCntList.head.head.asInstanceOf[Number].intValue == 0) {
          val sql = "insert into t_attend_activities(id,semester_id,lesson_id,course_id,course_date,begin_time,end_time,room_id,course_hours,attend_begin_time)" +
            " select seq_t_attend_activities.nextval,rw.jxrlid,rw.id,rw.kcid,to_date(" + toDateStr(date) + ",'yyyyMMdd')," +
            " hd.qssj,hd.jssj,hd.jsid,(hd.jsxj-hd.ksxj+1) ks,to_coursetime(to_minutes(hd.qssj)-15) begin_attend_time from jxrw_t rw ,jxhd_t hd" +
            " where rw.id=hd.jxrwid and hd.zj=? and hd.nf=? and bitand(hd.yxzsz,?)>0 and hd.jsid is not null and hd.zyyy=1"
          rs._2.foreach { data =>
            val activityCount = executor.update(sql, rs._1, data._1, data._2)
            activityCountSum += activityCount
            logger.info("Importe {} attend activities", activityCount)
          }
        }
        //上课名单数据
        val detailCntList = executor.query("select count(*) from " + detailTableName(date) + " d,t_attend_activities aa where d.activity_id=aa.id " +
          " and aa.course_date =?", date)
        if (detailCntList.isEmpty || detailCntList.head.head.asInstanceOf[Number].intValue == 0) {
          val detailSql = "insert into " + detailTableName(date) + "(id,std_id,activity_id,attend_type_id)" +
            " select seq_t_attend_details.nextval,jxb.xsid,aa.id," + AttendType.Absenteeism + " from t_attend_activities aa,jxbxs_t jxb" +
            " where aa.course_date = ? and aa.lesson_id=jxb.jxrwid"
          detailCount = executor.update(detailSql, date)
          logger.info("Importe {} attend details.", detailCount)
        }
      } finally {
        executor.update("drop table " + lockName)
      }
    }
    (activityCountSum, detailCount)
  }
}