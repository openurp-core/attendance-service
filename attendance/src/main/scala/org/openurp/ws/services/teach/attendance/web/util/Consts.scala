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

import java.sql.{ Date, Time }
/**
 * 参数常量
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
object Consts {
  /**设备ID*/
  val DeviceId = "devid"
  /**获取工牌号*/
  val CardId = "cardphyid"
  /**签到日期*/
  val SigninDate = "signindate"
  /**签到时间*/
  val SigninTime = "signintime"

  private val DecryptTransformer = new DecryptTransformer(new DesDecryptorFactory().getObject)

  private val SigninDateTransformer = new TransformerChain(classOf[Date], DecryptTransformer, Transformers.Date)

  private val SigninTimeTransformer = new TransformerChain(classOf[Time], DecryptTransformer, Transformers.Time)

  val Rule = Map((DeviceId, Transformers.PositiveInteger), (SigninDate, SigninDateTransformer), (SigninTime, SigninTimeTransformer),
    (CardId, DecryptTransformer))

}