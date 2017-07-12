//
// Google検索のテスト
//
package com.pitecan.gyaim

import org.junit.Test

import org.junit.Assert.*

import com.pitecan.gyaim.GoogleIME

class GoogleIMETest {
    val 変換例リスト = arrayOf(
            arrayOf("べんきょう", "勉強"),
            arrayOf("ますい", "増井"),
            arrayOf("いちのもと", "櫟本"),
            arrayOf("むさしぼうべんけい","武蔵坊弁慶"),
            arrayOf("かまくらのかいがん", "鎌倉の海岸")
            )

    @Test
    fun Google変換チェック() {
        for (変換例 in 変換例リスト) {
            assertTrue(GoogleIME.convert(変換例[0]).contains(変換例[1]))
        }
    }
}

