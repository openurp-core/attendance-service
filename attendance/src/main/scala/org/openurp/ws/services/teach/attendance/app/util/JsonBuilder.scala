package org.openurp.ws.services.teach.attendance.app.util

import com.google.gson.JsonObject

class JsonBuilder {

  private val json = new JsonObject()

  def add(property: String, value: String): this.type = {
    json.addProperty(property, value)
    this
  }

  def add(property: String, value: Number): this.type = {
    json.addProperty(property, value)
    this
  }

  def mkJson: JsonObject = json
}