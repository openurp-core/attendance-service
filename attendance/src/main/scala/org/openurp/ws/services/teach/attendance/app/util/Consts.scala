package org.openurp.ws.services.teach.attendance.app.util

import java.sql.Date
import java.sql.Time

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