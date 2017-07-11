//
// ローマ字かな変換のユニットテスト
//
// ここはJavaで書く必要があるのだろうか 2017/07/11 08:56:02
//
package com.pitecan.gyaim

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
/*
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}
*/

class RomakanaTest {
    internal var testdata = arrayOf(arrayOf("masui", "ますい", "マスイ"), arrayOf("dhisukushisutemu", "でぃすくしすてむ", "ディスクシステム"), arrayOf("dexisukusisutemu", "でぃすくしすてむ", "ディスクシステム"), arrayOf("shachou", "しゃちょう", "シャチョウ"), arrayOf("syatyou", "しゃちょう", "シャチョウ"), arrayOf("vaiorin", "う゛ぁいおりん", "ヴァイオリン"), arrayOf("hannnya", "はんにゃ", "ハンニャ"))

    @Test
    @Throws(Exception::class)
    fun ローマ字変換テスト() {
        for (data in testdata) {
            assertTrue(Romakana.roma2hiragana(data[0]) == data[1])
            assertTrue(Romakana.roma2katakana(data[0]) == data[2])
        }
    }
}
