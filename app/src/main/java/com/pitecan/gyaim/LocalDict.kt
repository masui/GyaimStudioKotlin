//
// ローカル辞書ファイルを使った変換
//
// このファイルにはAndroid依存のものを入れないようにしたいのだが
// 計算中にユーザ入力チェックみたいことをしてるので難しいかもしれない
//
package com.pitecan.gyaim

import java.io.*

import java.util.ArrayList

import java.util.Objects
import java.util.regex.Pattern
import java.util.regex.Matcher


public class DictEntry internal constructor(pat: String, word: String, internal var inConnection: Int, internal var outConnection: Int) {
    var pat: String = pat
    var word: String = word

    internal var keyLink: Int = 0
    internal var connectionLink: Int = 0
}

object LocalDict {

    private var inputStream: InputStream? = null

    @JvmStatic
    fun initWithInputStream(inputStream: InputStream) {
        LocalDict.inputStream = inputStream

        try {
            val inputStreamReader: InputStreamReader = InputStreamReader(inputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            var line: String?
            while (true) {
                line = bufferedReader.readLine()
                if (line == null) break
                //Message.message ("Gyaim",line);
                val c = line[0].toChar()
                if (c == '#' || c == ' ' || c == '\t') continue // コメント行
                val a = line.split("\t".toRegex(), 4).toTypedArray()
                if (a[3] == null || a[3] == "") a[3] = "0"
                //Message.message("Gyaim",a[1]);
                dict.add(DictEntry(a[0], a[1], Integer.valueOf(a[2])!!, Integer.valueOf(a[3])!!))
            }
            bufferedReader.close()
            inputStreamReader.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        initLink() // 辞書エントリ間のリンク設定
    }

    private val keyLink = IntArray(10)
    private val connectionLink = IntArray(2000)
    private val regexp = arrayOfNulls<Pattern>(50)       // パタンの部分文字列にマッチするRegExp
    private val cslength = IntArray(50)             // regexp[n]に完全マッチするパタンの長さ

    private val wordStack = Array<String>(20, { "" })
    private val patStack = Array<String>(20, { "" })

    var exactMode = false

    private var fib1 = 0
    private var fib2 = 0 // フィボナッチ数...

    public val dict = ArrayList<DictEntry>()

    private fun initLink() {
        // Message.message("Gyaim","initLink");
        //
        // 先頭読みが同じ単語のリスト
        //
        var cur = IntArray(10)
        cur.indices.forEach { i -> keyLink[i] = -1 }
        dict.indices.forEach { i ->
            if (dict[i].word.startsWith("*")) return@forEach
            // if(dict[i].inConnection < 1000) continue; // 活用の接続の場合
            val ind = patInd(dict[i].pat)
            if (keyLink[ind] < 0) {
                cur[ind] = i
                keyLink[ind] = i
            } else {
                dict[cur[ind]].keyLink = i
                cur[ind] = i
            }
            dict[i].keyLink = -1 // リンクの末尾
        }
        //
        // コネクションつながりのリスト
        //
        cur = IntArray(2000)
        cur.indices.forEach { i -> connectionLink[i] = -1 }

        dict.indices.forEach { i ->
            val ind = dict[i].inConnection
            if (connectionLink[ind] < 0) {
                cur[ind] = i
                connectionLink[ind] = i
            } else {
                dict[cur[ind]].connectionLink = i
                cur[ind] = i
            }
            dict[i].connectionLink = -1 // リンクの末尾
        }
    }

    private fun patInit(pat: String, level: Int): String {
        var p = ""
        var top = ""
        var re: Pattern
        var matcher: Matcher

        cslength[level] = 0
        if (pat.isNotEmpty()) {
            re = Pattern.compile("^(\\[[^\\]]+\\])(.*)$")
            matcher = re.matcher(pat)
            if (matcher.find()) {
                top = matcher.group(1)
                p = patInit(matcher.group(2), level + 1)
            } else {
                re = Pattern.compile("^(.)(.*)$")
                matcher = re.matcher(pat)
                matcher.find()
                top = matcher.group(1)
                p = patInit(matcher.group(2), level + 1)
            }
            cslength[level] = cslength[level + 1] + 1
        }

        top += if (p.isNotEmpty()) "($p)?" else ""
        regexp[level] = Pattern.compile("^($top)")
        return top
    }

    // ローカル辞書の接続検索
    @JvmStatic
    fun search(pat: String, searchTask: SearchTask) {
        patInit(pat, 0)
        fib2 = 1
        fib1 = fib2
        generateCand(0, patInd(pat), 0, "", "", 0, searchTask) // 接続辞書を使って候補を生成
    }

    // パタンのlen文字目からのマッチを調べる
    // 接続リンクを深さ優先検索してマッチするものを候補に加えていく
    private fun generateCand(connection: Int, keylink: Int, len: Int, word: String, pat: String, level: Int, searchTask: SearchTask) {
        //Message.message("Gyaim", "GenerateCand($word,$pat,$level)")
        wordStack[level] = word
        patStack[level] = pat

        val patlen = cslength[len]
        var d = if (connection != 0) connectionLink[connection] else keyLink[keylink]
        while (d >= 0 && Search.ncands < Gyaim.MAXCANDS) {
            if (searchTask.isCancelled) break
            val m = regexp[len]!!.matcher(dict[d].pat)
            if (m.find()) {
                val matchlen = m.group(1).length
                if (matchlen == patlen && (!exactMode || exactMode && dict[d].pat.length == matchlen)) { // 最後までマッチ
                    addConnectedCandidate(dict[d].word, dict[d].pat, dict[d].outConnection, level, matchlen)
                    // Message.message("Gyaim","ncands = " + Search.ncands + ", fib1 = " + fib1);
                    if (Search.ncands >= fib1) {
                        searchTask.progress(0) //いくつかみつかったら画面更新
                        val tmp = fib1
                        fib1 += fib2
                        fib2 = tmp
                    }
                } else if (matchlen == dict[d].pat.length && dict[d].outConnection != 0) { // とりあえずその単語まではマッチ
                    generateCand(dict[d].outConnection, 0, len + matchlen, dict[d].word, dict[d].pat, level + 1, searchTask)
                }
            }
            d = if (connection != 0) dict[d].connectionLink else dict[d].keyLink
        }
    }

    fun addConnectedCandidate(word: String, pat: String, connection: Int, level: Int, matchlen: Int) { // 候補追加
        if (word == "") return  // 2011/11/3
        //if(word.charAt(0) == '*') return; // 単語活用の途中
        if (word[word.length - 1] == '*') return
        var p = patStack.take(level+1).joinToString("") + pat
        var w = wordStack.take(level+1).joinToString("") + word

        w = w.replace("\\*".toRegex(), "")
        Search.addCandidateWithLevel(w, p, level)
        // Message.message("Gyaim","addCandidateWithLevel: word = " + w + "  pattern = " + p + "  level = " + level);
    }

    private val patIndPattern = arrayOf(
            Pattern.compile("\\[?[aiueoAIUEO].*"),
            Pattern.compile("\\[?[kg].*"),
            Pattern.compile("\\[?[sz].*"),
            Pattern.compile("\\[?[tdT].*"),
            Pattern.compile("\\[?[n].*"),
            Pattern.compile("\\[?[hbp].*"),
            Pattern.compile("\\[?[m].*"),
            Pattern.compile("\\[?[yY].*"),
            Pattern.compile("\\[?[r].*")
    )

    fun patInd(str: String): Int {
        for (i in patIndPattern.indices) {
            if (patIndPattern[i].matcher(str).find()) return i
        }
        return 9
    }
}
