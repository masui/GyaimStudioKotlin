package com.pitecan.gyaim

import android.util.Log

import org.json.*

import java.io.*

import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import org.apache.http.params.HttpParams
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams

import org.apache.http.util.EntityUtils

class GoogleIME {
    companion object {

        fun ime(q: String): Array<String> {
            // Google CGI API for Japanese Input
            // http://www.google.co.jp/ime/cgiapi.html
            // Google日本語入力のURLは "http://google.co.jp/transliterate?langpair=ja-Hira|ja&text=かんじ" のような形式だが
            // "|" を "%7c" にしておかないと new HttpGet() が失敗する
            val urlstr = "http://google.co.jp/transliterate?langpair=ja-Hira%7cja&text=" + q
            //val suggestions: Array<String>
            val maxSuggestions = 20
            /*
            var suggestions = arrayOfNulls<String>(maxSuggestions + 1)
            for (i in suggestions.indices) {
                suggestions[i] = ""
            }
            */
            var suggestions = Array<String>(maxSuggestions + 1, { "" })

            var nsuggest = 0

            var jsonText = "[[\"\",[]]]"

            //Log.d("Gyaim", urlstr);

            try {
                // http://stackoverflow.com/questions/693997/how-to-set-httpresponse-timeout-for-android-in-java
                val httpParameters = BasicHttpParams()
                //Log.d("Gyaim", "parameters = " + httpParameters);
                // Set the timeout in milliseconds until a connection is established.
                val timeoutConnection = 1500
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection)
                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for data.
                val timeoutSocket = 1500
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket)
                val httpClient = DefaultHttpClient(httpParameters)
                //Log.d("Gyaim", "defaulthttpclient = " + httpClient);

                //DefaultHttpClient httpClient = new DefaultHttpClient();

                httpClient.params.setParameter("http.protocol.content-charset", "UTF-8")
                //Log.d("Gyaim", "setParameter");

                val request = HttpGet(urlstr)
                //Log.d("Gyaim", "request = " + request);
                var httpResponse: HttpResponse? = null
                try {
                    //Log.d("Gyaim", "Google Execute");
                    httpResponse = httpClient.execute(request)
                    //Log.d("Gyaim", "Response get");
                } catch (e: Exception) {
                    //Log.d("HttpSampleActivity", "Error Execute");
                }

                val status = httpResponse!!.statusLine.statusCode
                if (HttpStatus.SC_OK == status) {
                    try {
                        val outputStream = ByteArrayOutputStream()
                        jsonText = EntityUtils.toString(httpResponse.entity, "UTF-8") // これが大事らしいが...
                    } catch (e: Exception) {
                        //Log.d("Gyaim HttpSampleActivity", "Error");
                    }

                } else {
                    //Log.d("Gyaim HttpSampleActivity", "Status" + status);
                }

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
                    Log.e("Gyaim", "JSON Exception " + e)
                }

            } catch (e: Exception) {
                Log.v("Gyaim", "GoogleIME error")
            }

            return suggestions
        }
    }
}
