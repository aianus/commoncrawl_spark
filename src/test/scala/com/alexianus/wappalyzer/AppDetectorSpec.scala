package com.alexianus.wappalyzer

import org.apache.commons.io.IOUtils
import org.scalatest.Matchers._

class AppDetectorSpec extends UnitSpec {
  val coinbase_html = IOUtils.toString(getClass.getResourceAsStream("/com/alexianus/wappalyzer/coinbase.html"))

  describe("AppDetector") {
    it("should correctly tag a script app") {
      val apps = AppDetector.detect(coinbase_html)
      apps should contain ("jQuery")
    }
    it("should correctly tag an html app") {
      val apps = AppDetector.detect(coinbase_html)
      apps should contain ("Question2Answer")
    }
  }
}
