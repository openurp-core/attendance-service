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
import java.util.{ Calendar, Timer, TimerTask }

import org.beangle.commons.bean.Initializing
import org.beangle.commons.io.IOs
import org.beangle.commons.lang.ClassLoaders
import org.beangle.commons.lang.Dates.toDate
import org.beangle.commons.lang.Numbers.toInt
import org.beangle.commons.lang.Strings
import org.beangle.commons.lang.Strings.{ isNotBlank, replace, split }
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy.{ detailPostfix, logPostfix }

/**
 * 数据分区和导入守护线程
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 1.0
 */
class ShardDaemon extends TimerTask with Logging with Initializing {
  var executor: JdbcExecutor = _
  var importer: DataImporter = _
  /**每次同步时间*/
  var firstTime = "15:00"

  /**检查表的时间(每天)*/
  var interval = 24 * (60 * 60 * 1000)

  def checkDetailTable(date: Date) {
    checkAndCreate("t_attend_details", detailPostfix(date))
  }

  def checkLogTable(date: Date) {
    checkAndCreate("t_attend_logs", logPostfix(date))
  }

  private def checkAndCreate(table: String, postfix: String) {
    val tableName = table + postfix
    // 1.check
    val count = executor.queryForInt("select count(*) from user_tables where table_name='" + tableName.toUpperCase() + "'")
    // 2.createf
    if (count == 0) {
      val url = ClassLoaders.getResource("ddl/create/" + table + ".sql", this.getClass)
      val sqls = split(IOs.readString(url.openStream()), ";")
      sqls foreach { sql =>
        if (isNotBlank(sql)) {
          val statment = replace(sql.trim, "${postfix}", postfix)
          executor.update(statment)
          logger.info("execute {}", statment)
        }
      }
    }
  }

  def run() {
    try {
      val tabCal = Calendar.getInstance()
      var date = toDate(tabCal)
      val year = tabCal.get(Calendar.YEAR)
      //检查当月的表
      checkDetailTable(date)
      checkLogTable(date)

      //检查下一个月/下一年的表
      tabCal.add(Calendar.MONTH, 1)
      checkDetailTable(date)
      if (tabCal.get(Calendar.YEAR) != year) checkLogTable(date)

      //导入当天的数据
      val dataCal = Calendar.getInstance()
      importer.importData(toDate(dataCal))
      //导入明天的数据
      dataCal.add(Calendar.DAY_OF_YEAR, 1)
      importer.importData(toDate(dataCal))
    } catch {
      case e: Exception => logger.error("Cannot check and create attend table", e)
    }
  }

  def init() {
    val cal = Calendar.getInstance
    if (Strings.isNotEmpty(firstTime)) {
      cal.set(Calendar.HOUR_OF_DAY, toInt(firstTime.substring(0, 2)))
      cal.set(Calendar.MINUTE, toInt(firstTime.substring(3)))
      cal.set(Calendar.SECOND, 0)
    }
    new Timer("Attenance Shard Deamon", true).schedule(this, cal.getTime(), interval)
  }
}