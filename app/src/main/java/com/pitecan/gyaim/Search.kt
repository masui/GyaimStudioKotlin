//
// いろんな辞書を検索するものをまとめる
// その後でまとめてAsyncTaskにする。
// 
package com.pitecan.gyaim

import java.util.Arrays

import android.util.Log

import android.content.res.AssetManager

import java.io.InputStream
import java.io.IOException
import java.util.Objects

class Search(gyaim: Gyaim) {

    init {
        Search.gyaim = gyaim
        //
        // 内蔵固定辞書
        //
        try {
            val assets = gyaim.resources.assets
            val inputStream = assets.open("dict.txt")
            LocalDict(inputStream)
        } catch (e: IOException) {
            //e.printStackTrace();
        }

        //
        // 学習辞書(SQL)
        //
        sqlDict = SQLDict(gyaim)

        for (i in 0..Gyaim.MAXCANDS - 1) {
            candidates[i] = Candidate("", "", 0)
        }
    }

    companion object {
        //private static LocalDict localDict;
        var sqlDict: SQLDict? = null
        private var gyaim: Gyaim? = null

        var candidates: Array<Candidate?> = arrayOfNulls<Candidate>(Gyaim.MAXCANDS)  // 候補単語リスト
        var ncands = 0
        var useGoogle = true // GoogleIMEで検索するかどうか

        fun reset() {
            ncands = 0
        }

        //
        // いろんな辞書を使った検索!
        //
        fun search(pat: String, searchTask: SearchTask): Array<Candidate?> {
            Message.message("Gyaim", "Search - pat=" + pat)
            ncands = 0

            //
            // 完全一致モードではひらがな/カタカナを候補に出す
            //
            if (LocalDict.exactMode) {
                val hiragana = Romakana.roma2hiragana(pat)
                val katakana = Romakana.roma2katakana(pat)
                addCandidateWithLevel(hiragana, pat, -100)
                addCandidateWithLevel(katakana, pat, -99)
            }

            //
            // クリップボードの単語を候補に出す (新規登録用)
            //
            if (!LocalDict.exactMode) {
                val s = gyaim!!.newClipboardText
                if (s != "" && s.length < 10) { // コピー文字列が短い場合だけ候補にする
                    addCandidate(s, pat)
                }
            }

            //
            // SQLの学習辞書検索
            //
            val s = sqlDict!!.match(pat, LocalDict.exactMode)
            for (k in s.indices) {
                addCandidateWithLevel(s[k][0], s[k][1], -50 + k)
            }

            //
            // 通常のローカル辞書を検索
            // 時間がかかることがあるのでSearchTaskでバックグラウンド動作させている。
            // 何かキー入力があれば isCancelled() がtrueになる。
            //
            LocalDict.search(pat, searchTask)

            //
            // Google検索
            //
            Message.message("Gyaim", "UseGoogle = " + useGoogle)
            if (!searchTask.isCancelled) {
                // Google Suggest または Google日本語入力を利用
                if (useGoogle) {
                    Message.message("Gyaim", "isConnected() = " + gyaim!!.isConnected!!)
                    if (gyaim!!.isConnected!!) {
                        // 昔はGoogleSuggestを使っていたが制限があるようなのでGoogleIME APIを利用する
                        // String[] suggestions = GoogleSuggest.suggest(word);
                        var suggestions = GoogleIME.ime(Romakana.roma2hiragana(pat))
                        Log.v("Gyaim", "length=" + suggestions.size)
                        var i = 0
                        while (suggestions[i] != null && suggestions[i] !== "") {
                            Message.message("Gyaim", "Use Google ... suggestions = " + suggestions[i])
                            addCandidateWithLevel(suggestions[i], KeyController.inputPat(), 50)
                            i++
                        }
                    }
                }

                // 優先度に従って候補を並べなおし
                //for(int j=ncands;j<Gyaim.MAXCANDS;j++){
                //	candidates[j].weight = 100;
                //}
                // ソートをやめてみたが全く違いがわからない... 要るのだろうか?? (2012/12/11 08:58:42)
                //Arrays.sort(candidates, new CandidateComparator());
            }

            return candidates
        }

        fun addCandidate(word: String, pat: String) {
            addCandidateWithLevel(word, pat, 0)
        }

        fun addCandidateWithLevel(word: String?, pat: String?, level: Int) {
            //Message.message("Gyaim","addCandidate: word="+word+" pat="+pat+" ncands="+ncands+" level="+level);
            if (ncands >= Gyaim.MAXCANDS) return
            var i = 0
            while (i < ncands) {
                if (candidates[i]!!.word == null) break
                if (candidates[i]!!.word == word) break
                i++
            }
            if (i >= ncands) {
                candidates[ncands]!!.pat = pat
                candidates[ncands]!!.word = word
                candidates[ncands]!!.weight = level
                //Message.message("Gyaim", "Add "+word+" to candidates");
                ncands++
            }
        }
    }
}
