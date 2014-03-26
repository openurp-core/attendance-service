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
package org.openurp.ws.services.teach.attendance.app.model

import java.{ util => ju }
import org.beangle.commons.lang.Objects

/**
 * 设备信息
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class Device(val id: Int, val room: Classroom, var syncAt: ju.Date) {
  override def toString: String = {
    Objects.toStringBuilder(this).add("id", id).add("room", room).add("syncAt", syncAt).toString
  }
}

/**
 * 教室信息
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class Classroom(val id: Int, val name: String) {
  override def toString(): String = Objects.toStringBuilder(this).add("id", id).add("name", name).toString
}
