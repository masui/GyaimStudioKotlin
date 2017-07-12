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

    companion object {
        // この絶対パスをなんとかしたいのだが...
        internal val 単語辞書ファイル = "/Users/masui/Gyaim/app/src/main/assets/dict.txt"
    }

    init {
        val file = File(単語辞書ファイル)
        try {
            val inputStream = FileInputStream(file)
            LocalDict.initWithInputStream(inputStream)
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
                arrayOf("toukyoue", "東京駅")
        )
        for (変換例 in 変換例リスト) {
            var found = false
            LocalDict.search(変換例[0], SearchTask())
            for (変換結果 in Search.words) {
                if (変換結果 == 変換例[1]) found = true
            }
            assertTrue(found)
        }
    }
}
