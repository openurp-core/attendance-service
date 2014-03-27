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
package org.openurp.ws.services.teach.attendance.app.domain

import java.{ util => ju }
import org.beangle.commons.lang.Objects
import org.openurp.ws.services.teach.attendance.app.util.DateUtils
import java.{util => ju}
/**
 * 签到信息
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class SigninData(val devId: Int, val cardId: String, val signinAt: ju.Date, val params: String) {
  override def toString(): String = {
    Objects.toStringBuilder(this).add("devId", devId).add("cardId", cardId).add("signinAt", DateUtils.toDateTimeStr(signinAt)).toString
  }
}
