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
package org.openurp.ws.services.teach.attendance.web.app

import org.beangle.commons.logging.Logging
import org.openurp.ws.services.teach.attendance.impl.SigninService
import org.openurp.ws.services.teach.attendance.web.util.{ JsonBuilder, Params }
import org.openurp.ws.services.teach.attendance.web.util.Consts.{ DeviceId, Rule }
import org.openurp.ws.services.teach.attendance.web.util.Render.render
import javax.servlet.{ ServletRequest, ServletResponse }
import javax.servlet.http.HttpServlet
/**
 * 滚动的通知公告
 * FIXME 未实现
 * 
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class NoticeServlet extends HttpServlet with Logging {
  var signinService: SigninService = _

  override def service(req: ServletRequest, res: ServletResponse) {
    val params = Params.require(DeviceId).get(req, Rule)
    var devid: Int = 0
    var retmsg: String = ""
    if (!params.ok) params.msg.values.mkString(";")

    devid = params(DeviceId)
    val rs = new JsonBuilder
    rs.add("retcode", -1).add("retmsg", params.msg.values.mkString(";"))
    rs.add("devid", devid)
    rs.add("classname", "").add("msgtype", 1)
    rs.add("msg", "").add("msgdata", "")

    render(res, rs.mkJson)
  }

}