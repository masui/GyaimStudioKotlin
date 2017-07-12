//
// ローマ字かな変換のユニットテスト
//
package com.pitecan.gyaim

import org.junit.Test

import org.junit.Assert.*

class RomakanaTest {
    internal var testdata = arrayOf(
            arrayOf("masui", "ますい", "マスイ"),
            arrayOf("dhisukushisutemu", "でぃすくしすてむ", "ディスクシステム"),
            arrayOf("dexisukusisutemu", "でぃすくしすてむ", "ディスクシステム"),
            arrayOf("shachou", "しゃちょう", "シャチョウ"),
            arrayOf("syatyou", "しゃちょう", "シャチョウ"),
            arrayOf("vaiorin", "う゛ぁいおりん", "ヴァイオリン"),
            arrayOf("hannnya", "はんにゃ", "ハンニャ"))

    @Test
    fun ローマ字変換テスト() {
        for (data in testdata) {
            assertTrue(Romakana.roma2hiragana(data[0]) == data[1])
            assertTrue(Romakana.roma2katakana(data[0]) == data[2])
        }
    }
}
