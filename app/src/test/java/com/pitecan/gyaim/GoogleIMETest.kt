//
// Google検索のテスト
//
package com.pitecan.gyaim

import org.junit.Test

import org.junit.Assert.*

import com.pitecan.gyaim.GoogleIME

class GoogleIMETest {
    @Test
    fun 普通単語検索チェック() {
        var words = arrayOf("")
        words = GoogleIME.convert("べんきょう")
        assertTrue(words.contains("勉強"))
    }

    @Test
    fun 特殊地名検索チェック() {
        var words = GoogleIME.convert("いちのもと")
        assertTrue(words.contains("櫟本"))
    }

    @Test
    fun 連文節変換チェック() {
        var words = GoogleIME.convert("かまくらのかいがん")
        assertTrue(words.contains("鎌倉の海岸"))
    }
}

