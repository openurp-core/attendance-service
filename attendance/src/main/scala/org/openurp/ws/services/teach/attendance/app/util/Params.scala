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
package org.openurp.ws.services.teach.attendance.app.util

import java.sql.{ Date, Time }
import java.{ util => ju }
import org.beangle.commons.conversion.Conversion
import org.beangle.commons.conversion.impl.DefaultConversion
import org.beangle.commons.lang.Numbers
import org.beangle.commons.lang.Strings.isEmpty

import javax.servlet.ServletRequest
/**
 * 参数工具类
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 1.0
 */
final object Params {
  class ParamOption() {
    val required = new collection.mutable.HashSet[String]
    val optional = new collection.mutable.HashSet[String]

    def optional(names: String*): this.type = {
      optional ++= names
      this
    }
    def require(names: String*): this.type = {
      optional ++= names
      this
    }
    def get(req: ServletRequest, formers: Map[String, Transformer]): Result = {
      validate(req.getParameterMap, this, formers)
    }
    def get(params: ju.Map[String, Array[String]], formers: Map[String, Transformer]): Result = {
      validate(params, this, formers)
    }
  }

  def require(names: String*): ParamOption = {
    val option = new ParamOption()
    option.required ++= names
    option
  }

  def optional(names: String*): ParamOption = {
    val option = new ParamOption()
    option.optional ++= names
    option
  }

  def validate(params: ju.Map[String, Array[String]], option: ParamOption, formers: Map[String, Transformer]): Result = {
    var result = 0
    val msg = new collection.mutable.HashMap[String, String]
    val datas = new collection.mutable.HashMap[String, Any]
    option.required.foreach { name =>
      val values = params.get(name)
      if (null == values || values.isEmpty || values.length == 1 && isEmpty(values(0))) {
        result -= 1
        msg.put(name, name + "参数不能为空")
      } else {
        datas.put(name, values)
      }
    }
    option.optional.foreach { name =>
      val values = params.get(name)
      if (null != values && !values.isEmpty) {
        datas.put(name, values)
      }
    }

    datas.foreach {
      case (name, values) =>
        val value = values.asInstanceOf[Array[String]]
        formers.get(name) match {
          case Some(v) =>
            if (value.length == 1) {
              val tuple = v.transform(value(0))
              if (tuple.ok) {
                datas.put(name, tuple.value)
              } else {
                result -= 1
                msg.put(name, tuple.msg)
              }
            } else {
              val newValue = java.lang.reflect.Array.newInstance(v.resultType, value.length).asInstanceOf[Array[Any]]
              var i = 0
              var ok = true
              while (ok && i < newValue.length) {
                val tuple = v.transform(value(i))
                if (tuple.ok) {
                  newValue(i) = tuple.value
                } else {
                  result -= 1
                  msg.put(name, tuple.msg)
                  ok = false
                }
                i += 1
              }
              if (i == newValue.length) datas.put(name, newValue)
            }
          case None => if (value.length == 1) datas.put(name, value(0))
        }
    }
    new Result(result, msg, datas)
  }
}

object Transformers {
  val PositiveInteger = new IntegerTransformer
  val Date = new ConverterTransformer(classOf[Date], DefaultConversion.Instance, "错误的日期格式")
  val DateTime = new ConverterTransformer(classOf[ju.Date], DefaultConversion.Instance, "错误的日期时间格式")
  val Time = new ConverterTransformer(classOf[Time], DefaultConversion.Instance, "错误的时间格式")
}

trait Transformer {

  def transform(value: String): TransformerResult

  def resultType: Class[_]
}

final class TransformerResult(val value: Any, val msg: String) {
  @inline def ok = isEmpty(msg)
}

class TransformerChain(val resultType: Class[_], formers: Transformer*) extends Transformer {
  def transform(value: String): TransformerResult = {
    var ok = true
    val formerIter = formers.iterator
    var curr: Any = value
    var result: TransformerResult = null
    while (ok && formerIter.hasNext) {
      result = formerIter.next.transform(curr.toString)
      if (result.ok) curr = result.value
      else ok = false
    }
    result
  }
}

class IntegerTransformer extends Transformer {
  def transform(value: String): TransformerResult = {
    val rs = Numbers.toInt(value)
    new TransformerResult(rs, if (value != "0" && 0 == rs) "无效的数字:" + value else null)
  }
  def resultType = classOf[Int]
}

class ConverterTransformer(val resultType: Class[_], conversion: Conversion, msg: String = "错误的数据格式") extends Transformer {
  def transform(value: String): TransformerResult = {
    var rs: Any = null
    try {
      rs = conversion.convert(value, resultType)
    } catch {
      case e: Exception =>
    }
    new TransformerResult(rs, if (null == rs) msg + ":" + value else null)
  }
}

final class Result(val failCount: Int, val msg: collection.Map[String, String], val datas: collection.Map[String, Any]) {

  @inline
  def apply[U](name: String): U = datas(name).asInstanceOf[U]

  @inline
  def get[U](name: String): Option[U] = datas.get(name).asInstanceOf[Option[U]]

  @inline
  def ok: Boolean = failCount == 0

}