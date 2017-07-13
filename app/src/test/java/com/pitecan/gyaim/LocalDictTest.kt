//
// ローカル辞書検索のテスト
//
// - @Test というアノテーションをつけるとテスト関数と解釈される
// - assertThat() みたいなのが使える
//
package com.pitecan.gyaim

import org.junit.Test

import org.junit.Assert.*

import com.pitecan.gyaim.LocalDict
import com.pitecan.gyaim.SearchTask
import com.pitecan.gyaim.Search

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.IOException

class LocalDictTest {
    init {
        try {
            val file = File("app/src/main/assets/dict.txt")
            val inputStream = FileInputStream(file)
            LocalDict(inputStream)
        } catch (e: IOException) {
            println("辞書読出し失敗")
            e.printStackTrace()
        }
    }

    @Test
    fun 辞書サイズチェック() {
        assertTrue(LocalDict.dict.size > 10000)
    }

    @Test
    fun 重要単語登録チェック() {
        val 重要単語リスト = arrayOf(
                "漢字",
                "東京",
                "増井",
                "料理",
                "勉強"
        )
        for (重要単語 in 重要単語リスト) {
            var registered = false
            for (entry in LocalDict.dict) {
                if (entry.word == 重要単語) registered = true
            }
            assertTrue(registered)
        }
    }

    @Test
    fun 変換チェック() {
        val 変換例リスト = arrayOf(
                arrayOf("kangae", "考える"),
                arrayOf("atarashii", "新しい"),
                arrayOf("masui", "増井"),
                arrayOf("toukyoue", "東京駅"),
                arrayOf("sanjuusan", "三十三"),
                arrayOf("taberarenai", "食べられない")
        )
        for (変換例 in 変換例リスト) {
            LocalDict.search(変換例[0], SearchTask())
            assertTrue(Search.words.contains(変換例[1]))
        }
    }
}
