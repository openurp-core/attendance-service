package org.openurp.ws.services.teach.attendance.app.impl

import scala.collection.JavaConversions.propertiesAsScalaMap

import org.beangle.commons.bean.Initializing
import org.beangle.commons.lang.ClassLoaders

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