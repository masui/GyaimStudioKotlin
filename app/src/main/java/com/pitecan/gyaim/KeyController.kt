//
//  Gyaimのキー操作に対するアクション (変換などの状態遷移)
//
package com.pitecan.gyaim

import java.util.ArrayList

import android.view.View
import android.view.KeyEvent
import android.text.TextUtils

class KeyController(gyaim: Gyaim, candView: CandView?) {

    init {
        KeyController.gyaim = gyaim
        KeyController.candView = candView
    }

    private fun resetInput() {
        inputPatArray = ArrayList<String>()
        nthCandSelected = 0
        exactMode = false
        shift = false
        alt = false
    }

    private fun searchAndDispCand() {
        Message.message("Gyaim", "searchAndDispCand()")
        //
        // バックグラウンドで検索実行 (AsyncTask機能)
        //
        searchTask = SearchTask(candView)
        searchTask!!.execute(inputPat())
    }

    private fun fix() {
        if (nthCandSelected > 0) { // 候補選択状態
            val word = Search.candidates[nthCandSelected - 1]!!.word
            gyaim!!.commitText(word!!) // 選択単語を貼り付け
            Search.sqlDict!!.add(word, inputPat())
            Search.sqlDict!!.limit(1000) // 1000個以上になれば古いエントリを消す
        } else {
            gyaim!!.commitText(inputPat()) // 入力パタンを貼り付け
        }
        resetInput()
        Search.reset()
        candView!!.invalidate()
        gyaim!!.setOldClipboardText()
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (searchTask != null) {
            searchTask!!.cancel(true) // キー入力があったらバックグラウンド検索を中止
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 画面上の左矢印キー
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_SYM) {
            // SYMキーのデフォルト動作(?)は変なダイアログが出るので
            // モード切り換えに利用してみる
            japaneseInputMode = !japaneseInputMode
            candView!!.visibility = if (japaneseInputMode) View.VISIBLE else View.GONE
            resetInput()
            fix()
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
            // 右シフトキーで日本語モード/素通しモード切替
            japaneseInputMode = !japaneseInputMode
            candView!!.visibility = if (japaneseInputMode) View.VISIBLE else View.GONE
            resetInput()
            fix()
            return true
        }
        if (!japaneseInputMode) {
            // 日本語モードでないときはfalseを返して素通しデフォルト動作(?)させる
            return false
        }
        //
        // これ以降は日本語入力モードの挙動
        // 状態遷移を特に記述するほどではないという理解で
        //
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            shift = true
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT) {
            alt = true
            return false
        }
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z && !shift && !alt) {
            val code = 0x61 + (keyCode - KeyEvent.KEYCODE_A)
            val charArray = Character.toChars(code)
            val s = String(charArray)

            if (nthCandSelected > 0) { // 候補選択状態だったら確定する
                fix()
            }

            inputPatArray.add(s)
            gyaim!!.showComposingText(inputPat())

            searchAndDispCand()
        }
        if (alt) {
            var c = ""
            when (keyCode) {
                KeyEvent.KEYCODE_A -> c = "*"
                KeyEvent.KEYCODE_B -> c = "!"
                KeyEvent.KEYCODE_C -> c = "9"
                KeyEvent.KEYCODE_D -> c = "5"
                KeyEvent.KEYCODE_E -> c = "2"
                KeyEvent.KEYCODE_F -> c = "6"
                KeyEvent.KEYCODE_G -> c = "/"
                KeyEvent.KEYCODE_H -> c = ":"
                KeyEvent.KEYCODE_I -> c = "-"
                KeyEvent.KEYCODE_J -> c = ";"
                KeyEvent.KEYCODE_K -> c = "'"
                KeyEvent.KEYCODE_L -> c = "\""
                KeyEvent.KEYCODE_M -> c = "."
                KeyEvent.KEYCODE_N -> c = ","
                KeyEvent.KEYCODE_O -> c = "+"
                KeyEvent.KEYCODE_P -> c = "@"
                KeyEvent.KEYCODE_Q -> c = "#"
                KeyEvent.KEYCODE_R -> c = "3"
                KeyEvent.KEYCODE_S -> c = "4"
                KeyEvent.KEYCODE_T -> c = "("
                KeyEvent.KEYCODE_U -> c = "_"
                KeyEvent.KEYCODE_V -> c = "?"
                KeyEvent.KEYCODE_W -> c = "1"
                KeyEvent.KEYCODE_X -> c = "8"
                KeyEvent.KEYCODE_Y -> c = ":"
                KeyEvent.KEYCODE_Z -> c = "7"
                KeyEvent.KEYCODE_0 -> c = "0"
            }
            if (c.length == 1) {
                if (nthCandSelected > 0) { // 候補選択状態だったら確定する
                    fix()
                }
                inputPatArray.add(c)
                gyaim!!.showComposingText(inputPat())
                searchAndDispCand()
            }
        }
        if (keyCode == KeyEvent.KEYCODE_SPACE) { // 候補選択
            if (inputPatArray.size == 0) {
                return false
            }
            nthCandSelected += 1
            gyaim!!.showComposingText(Search.candidates[nthCandSelected - 1]!!.word!!)
            candView!!.invalidate() // 候補表示更新
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) { // 確定
            if (inputPatArray.size == 0) {
                return false
            }
            if (nthCandSelected > 0 || exactMode) {
                fix()
                exactMode = false
                LocalDict.exactMode = false
            } else {
                exactMode = true
                LocalDict.exactMode = true
                searchAndDispCand()

                nthCandSelected += 1
                //gyaim.showComposingText(Search.candidates[nthCandSelected-1].word);
                gyaim!!.showComposingText(Romakana.roma2hiragana(inputPat())) // これは苦しい
                candView!!.invalidate() // 候補表示更新
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (nthCandSelected > 0) { // 候補選択状態
                nthCandSelected -= 1
                if (nthCandSelected > 0) {
                    gyaim!!.showComposingText(Search.candidates[nthCandSelected - 1]!!.word!!)
                } else {
                    gyaim!!.showComposingText(inputPat())
                }
                candView!!.invalidate() // 候補表示更新
            } else {
                val size = inputPatArray!!.size
                if (size > 0) {
                    inputPatArray.removeAt(size - 1)
                    gyaim!!.showComposingText(inputPat())
                    searchAndDispCand()
                } else {
                    return false // 変換中でないのでデフォルト動作
                }
            }
        }
        return true
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (japaneseInputMode) {
            if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
                shift = false
                return false
            }
            if (keyCode == KeyEvent.KEYCODE_ALT_LEFT) {
                alt = false
                return false
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) { // 画面上の左矢印キー
                // これがないとうまくBackしない
                return false
            }
            return true
        } else {
            return false
        }
    }

    companion object {
        private var gyaim: Gyaim? = null
        private var candView: CandView? = null

        private var searchTask: SearchTask? = null

        //private var inputPatArray: ArrayList<String>? = null // 入力文字の配列
        private var inputPatArray = ArrayList<String>()

        // 状態変数
        public var nthCandSelected = 0 // 0のときは候補選択前
        private var japaneseInputMode = true
        private var exactMode = false
        private var shift = false
        private var alt = false

        fun inputPat(): String {
            return TextUtils.join("", inputPatArray)
        }
    }
}
