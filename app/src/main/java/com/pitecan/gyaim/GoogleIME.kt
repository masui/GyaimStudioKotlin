package com.pitecan.gyaim

import android.util.Log

import org.json.*

import java.io.*

import java.net.*;

import org.json.JSONArray

object GoogleIME {
    fun convert(q: String): Array<String> {
        val urlstr = "http://google.co.jp/transliterate?langpair=ja-Hira%7cja&text=" + q

        val maxSuggestions = 20
        val suggestions = Array<String>(maxSuggestions + 1, { "" })
        var nsuggest = 0
        var jsonText = "[[\"\",[]]]"

        try {
            val url = URL(urlstr)

            // HTTP 接続オブジェクトの取得

            val http = url.openConnection()
            //http.setRequestMethod("GET")
            // 接続
            http.connect()

            // コンテンツの取得と表示
            val bis = BufferedInputStream(http.getInputStream())
            val inReader = InputStreamReader(bis)
            val bufReader = BufferedReader(inReader)

            val result = StringBuilder()

            var line: String?
            while (true) {
                line = bufReader.readLine()
                if (line == null) break
                result.append(line);
            }
            bufReader.close();

            jsonText = result.toString()

            //Message.message("Gyaim","jsonText = $jsonText")
            // http://www.google.co.jp/ime/cgiapi.html
            // "ここではきものをぬぐ" のようなパタンを与えたとき、
            // Google日本語入力は以下のようなJSONテキストを返す
            // [
            //   ["ここでは",
            //     ["ここでは", "個々では", "此処では"],
            //   ],
            //   ["きものを",
            //     ["着物を", "きものを", "キモノを"],
            //   ],
            //   ["ぬぐ",
            //     ["脱ぐ", "ぬぐ", "ヌグ"],
            //   ],
            // ]
            // これを読んで適当に候補を生成する
            try {
                val ja1: JSONArray
                var ja2: JSONArray
                var ja3: JSONArray
                val len1: Int
                val len3: Int
                ja1 = JSONArray(jsonText)
                len1 = ja1.length()
                var i = 0
                ja2 = ja1.getJSONArray(i)
                ja3 = ja2.getJSONArray(1) // 第2要素 = 変換候補
                len3 = ja3.length()
                nsuggest = 0
                while (nsuggest < len3 && nsuggest < maxSuggestions) {
                    suggestions[nsuggest] = ja3.getString(nsuggest)
                    nsuggest++
                }
                suggestions[nsuggest] = ""
                i = 1
                while (i < len1) {
                    ja2 = ja1.getJSONArray(i)
                    // String s = ja2.getString(0); // 第1要素 = 元の文字列
                    ja3 = ja2.getJSONArray(1) // 第2要素 = 変換候補
                    for (j in 0..nsuggest - 1) {
                        suggestions[j] += ja3.getString(0) // ふたつめ以降は最初の候補を連結する
                    }
                    i++
                }
            } catch (e: JSONException) {
                Message.message("Gyaim", "JSON Exception $e")
            }
        } catch (e: Exception) {
            Message.message("Gyaim", "GoogleIME error $e")
        }

        return suggestions
    }
}



