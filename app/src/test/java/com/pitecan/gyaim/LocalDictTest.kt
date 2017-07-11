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
        val file = File(単語辞書ファイル)
        try {
            val inputStream = FileInputStream(file)
            LocalDict.setInputStream(inputStream)
        } catch (e: IOException) {
            println("辞書読出し失敗")
            e.printStackTrace()
        }
    }

    @Test
    fun 単語辞書のサイズが充分大きい() {
        assertTrue(LocalDict.dict().size > 10000)
    }

    @Test
    fun 単語登録チェック() {
        val 必須単語 = arrayOf("漢字", "東京", "増井", "料理")
        for (単語 in 必須単語) {
            var 登録されてる = false
            for (entry in LocalDict.dict()) {
                if (entry.word() == 単語) 登録されてる = true
            }
            assertTrue(登録されてる)
        }
    }

    @Test
    fun 検索テスト() {
        LocalDict.search("kangae", SearchTask())
        assertTrue(Search.words.size > 0)
        var 考えるが検索された = false
        for (word in Search.words) {
            if (word == "考える") 考えるが検索された = true
        }
        assertTrue(考えるが検索された)

        LocalDict.search("atarashii", SearchTask())
        assertTrue(Search.words.size > 0)
        var 新しいが検索された = false
        for (word in Search.words) {
            if (word == "新しい") 新しいが検索された = true
        }
        assertTrue(新しいが検索された)
    }

    companion object {
        internal val 単語辞書ファイル = "/Users/masui/Gyaim/app/src/main/assets/dict.txt"
    }
}
