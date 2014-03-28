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
package org.openurp.ws.services.teach.attendance.web.util

import com.google.gson.JsonObject

import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse
/**
 * 渲染JSON工具类
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
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