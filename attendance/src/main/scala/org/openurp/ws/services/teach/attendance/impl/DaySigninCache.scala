package org.openurp.ws.services.teach.attendance.impl

import java.sql.Date
import java.util.{Calendar, Timer, TimerTask}

import scala.collection.mutable.{HashMap => MutableMap}

import org.beangle.commons.bean.Initializing
import org.beangle.commons.lang.Dates
import org.beangle.commons.lang.time.Stopwatch
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.domain.DateFormats
import org.openurp.ws.services.teach.attendance.domain.ShardPolicy.{activityTable, detailTable}
import org.openurp.ws.services.teach.attendance.domain.SigninInfo

object DaySigninCache {
  def find(times: Iterable[Int], begin: Int): Option[Int] = {
    var max = 0
    times.foreach { time =>
      if (time <= begin && time > max) max = time
    }
    if (max != 0) Some(max) else None
  }
}
/**
 * 一天的考勤名单缓存
 */
class DaySigninCache extends TimerTask with Initializing with Logging {

  import DaySigninCache._
  var today: Date = _
  var executor: JdbcExecutor = _
  // room -> (attend_begin_time -> (code -> signininfo))
  var cache: collection.Map[Int, collection.Map[Int, collection.Map[String, SigninInfo]]] = _

  def get(signinDate: Date, roomId: Int, time: Int, stdCode: String): Option[SigninInfo] = {
    var rs: Option[SigninInfo] = None
    if (signinDate == today) {
      cache.get(roomId) foreach { timeCache =>
        find(timeCache.keys, time) foreach { beginTime =>
          rs = timeCache(beginTime).get(stdCode)
        }
      }
    }
    rs
  }

  def init() {
    //每天运行数据缓存,凌晨6点执行
    val cal = Calendar.getInstance
    cal.add(Calendar.DAY_OF_YEAR,1)
    cal.set(Calendar.HOUR_OF_DAY, 6)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    new Timer("Attenance Day Signin Cache Deamon", true).schedule(this, cal.getTime(), 24 * (60 * 60 * 1000))
  }

  def run() {
    loadData(Dates.today)
  }

  def loadData(date: Date) {
    import collection.mutable.{ HashMap => MutableMap }
    val watch = new Stopwatch(true)
    cache = new MutableMap[Int, MutableMap[Int, MutableMap[String, SigninInfo]]]
    today = date
    val newCache = new MutableMap[Int, MutableMap[Int, MutableMap[String, SigninInfo]]]
    val datas = executor.query("select aa.room_id,aa.attend_begin_time,xs.xh,d.id,xs.xm,aa.begin_time,aa.end_time from " + detailTable(date) + " d,xsxx_t xs,"
      + activityTable(date) + " aa where xs.id=d.std_id and aa.id=d.activity_id and aa.course_date = ?", date)
    datas foreach { data =>
      val roomId = data(0).asInstanceOf[Number].intValue()
      val timeMap = newCache.get(roomId) match {
        case Some(a) => a
        case None => {
          val newMap = new MutableMap[Int, MutableMap[String, SigninInfo]]
          newCache.put(roomId, newMap)
          newMap
        }
      }
      val attendBegin = data(1).asInstanceOf[Number].intValue()
      val signMap = timeMap.get(attendBegin) match {
        case Some(s) => s
        case None => {
          val newMap = new MutableMap[String, SigninInfo]
          timeMap.put(attendBegin, newMap)
          newMap
        }
      }
      val begin = data(5).asInstanceOf[Number].intValue
      val end = data(6).asInstanceOf[Number].intValue
      signMap.put(data(2).toString, new SigninInfo(data(3).asInstanceOf[Number].longValue(), data(4).toString, attendBegin, begin, end))
    }
    cache = newCache
    logger.info("load " + DateFormats.toDateStr(date) + "  sign data {}  in {}", datas.size, watch)
  }
}


