package org.openurp.ws.services.teach.attendance.app.util

import com.google.gson.JsonObject
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServlet

object Render {

  def render(res: ServletResponse, json: JsonObject) {
    res.setContentType("application/json;charset=utf-8")
    res.asInstanceOf[HttpServletResponse].setStatus(HttpServletResponse.SC_OK)
    res.getWriter().print(json.toString())
  }

  def render(res: ServletResponse, jsonBuilder: JsonBuilder) {
    render(res, jsonBuilder.mkJson)
  }
}