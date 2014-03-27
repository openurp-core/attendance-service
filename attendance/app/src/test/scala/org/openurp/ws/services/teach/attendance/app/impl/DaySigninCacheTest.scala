package org.openurp.ws.services.teach.attendance.app.impl

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DaySigninCacheTest extends FunSpec {
  describe("SigninCache") {
    it("find") {
      assert(None == DaySigninCache.find(List(1000, 900, 805, 1455, 1300, 1550), 700))
      assert(Some(805) == DaySigninCache.find(List(1000, 900, 805, 1455, 1300, 1550), 807))
      assert(Some(900) == DaySigninCache.find(List(1000, 900, 805, 1455, 1300, 1550), 950))
      assert(Some(900) == DaySigninCache.find(List(1000, 900, 805, 1455, 1300, 1550), 900))
    }
  }
}