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

import scala.collection.JavaConversions.propertiesAsScalaMap

import org.beangle.commons.bean.Initializing
import org.beangle.commons.lang.ClassLoaders
/**
 * 应用配置
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class AppConfig extends Initializing {

  val properties = new collection.mutable.HashMap[String, String]

  def init() {
    val url = ClassLoaders.getResource("config.properties", getClass)
    val props = new java.util.Properties
    val is = url.openStream()
    props.load(is)
    is.close()
    properties ++= props
  }
  def courseURL: String = properties("courseURL")
}