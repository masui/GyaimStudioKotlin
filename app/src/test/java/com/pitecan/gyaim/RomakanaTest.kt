//
// ローマ字かな変換のユニットテスト
//
package com.pitecan.gyaim

import org.junit.Test

import org.junit.Assert.*

class RomakanaTest {
    val 変換例リスト = arrayOf(
            arrayOf("masui", "ますい", "マスイ"),
            arrayOf("dhisukushisutemu", "でぃすくしすてむ", "ディスクシステム"),
            arrayOf("dexisukusisutemu", "でぃすくしすてむ", "ディスクシステム"),
            arrayOf("shachou", "しゃちょう", "シャチョウ"),
            arrayOf("syatyou", "しゃちょう", "シャチョウ"),
            arrayOf("vaiorin", "う゛ぁいおりん", "ヴァイオリン"),
            arrayOf("hannnya", "はんにゃ", "ハンニャ"))

    @Test
    fun ローマ字変換テスト() {
        for (変換例 in 変換例リスト) {
            assertTrue(Romakana.roma2hiragana(変換例[0]) == 変換例[1])
            assertTrue(Romakana.roma2katakana(変換例[0]) == 変換例[2])
        }
    }
}
