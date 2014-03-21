package org.openurp.ws.services.teach.attendance.app.model
import java.util.Calendar
import org.beangle.commons.lang.Objects
import org.openurp.ws.services.teach.attendance.app.util.DateUtils

class SigninBean(val devId: Int, val cardId: String, val signinAt: Calendar,val params:String) {
  override def toString(): String = {
    Objects.toStringBuilder(this).add("devId", devId).add("cardId", cardId).add("signinAt", DateUtils.toDateTimeStr(signinAt.getTime())).toString
  }
}