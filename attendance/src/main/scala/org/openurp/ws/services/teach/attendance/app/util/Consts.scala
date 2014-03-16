package org.openurp.ws.services.teach.attendance.app.util

import java.util.Date
import java.sql.Time

object Consts {
  /**设备ID*/
  val Devid = "devid"
  /**获取工牌号*/
  val CardId = "cardphyid"
  /**签到日期*/
  val SigninDate = "signindate"
  /**签到时间*/
  val SigninTime = "signintime"

  private val Decryptor = new DesDecryptorFactory().getObject

  private val SigninDateTransformer = new TransformerChain(classOf[Date], new DecryptTransformer(Decryptor), Transformers.Date)

  private val SigninTimeTransformer = new TransformerChain(classOf[Time], new DecryptTransformer(Decryptor), Transformers.Time)

  val Rule = Map((Devid, Transformers.PositiveInteger), (SigninDate, SigninDateTransformer), (SigninTime, SigninTimeTransformer))

}