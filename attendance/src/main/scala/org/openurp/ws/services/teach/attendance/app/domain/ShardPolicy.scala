package org.openurp.ws.services.teach.attendance.app.domain

import java.util.Calendar
import org.beangle.commons.lang.Strings.{concat, leftPad}

object ShardPolicy {
  /**
   * 日志表按照年进行归档
   */
  def logTableName(date: Calendar): String = "t_attend_logs" + logPostfix(date)
  /**
   * 考勤表按照月进行归档
   */
  def detailTableName(date: Calendar): String = "t_attend_details" + detailPostfix(date)

  def detailPostfix(date: Calendar): String = {
    val year = date.get(Calendar.YEAR)
    val month = String.valueOf(date.get(Calendar.MONTH) + 1)
    concat(year, leftPad(month, 2, '0'))
  }

  def logPostfix(date: Calendar): String = String.valueOf(date.get(Calendar.YEAR))

}
