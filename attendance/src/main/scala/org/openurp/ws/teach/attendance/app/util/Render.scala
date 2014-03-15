package org.openurp.ws.teach.attendance.app.util

import com.google.gson.JsonObject
import javax.servlet.ServletResponse

object Render {

  def render(res: ServletResponse, json: JsonObject) {
    //FIXME fix content type
    res.setContentType("application/json;charset=utf-8")
    res.getWriter().print(json.toString())
  }

  def render(res: ServletResponse, jsonBuilder: JsonBuilder) {
    render(res, jsonBuilder.mkJson)
  }
}