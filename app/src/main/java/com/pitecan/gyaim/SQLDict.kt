//
// 学習辞書をSQLiteで実装する
//
// そもそも学習辞書をSQLiteで実装すべきなのかよくわからないし、
// 仕様が色々面倒なのだがとりあえず...
// (2011/12/10)
//
// 以下のようなページを参考にした。
// AndroidのSQLiteの使い方
// http://android.roof-balcony.com/shori/strage/select/
// サンプル
// http://codezine.jp/article/detail/4814?p=2
// サンプル
// http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=74
// Helper
// http://android.roof-balcony.com/shori/strage/sqlite/
// http://www.ipentec.com/document/document.aspx?page=android-use-sqlite-simple-app
// Android SQLiteマニュアル
// http://developer.android.com/intl/ja/reference/android/database/sqlite/SQLiteDatabase.html
// http://developer.android.com/intl/ja/reference/android/database/Cursor.html

package com.pitecan.gyaim

import java.util.regex.Pattern
import java.util.regex.Matcher
import java.util.ArrayList

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement

import android.content.Context

import android.util.Log

internal class DBHelper(context: Context) : SQLiteOpenHelper(context, "learndict", null, 1) {

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onCreate(db: SQLiteDatabase) {
        // DBが存在する場合は呼ばれないらしい
        db.execSQL(
                "create table history(" +
                        "   word text not null," +
                        "   pat text not null," +
                        "   patind text not null," +
                        "   date text not null" +
                        ");"
        )
    }
}

class SQLDict(context: Context) {
    init {
        var helper = DBHelper(context)
        db = helper.writableDatabase
    }

    companion object {
        internal var db: SQLiteDatabase? = null

        fun add(word: String, pat: String) { // エントリ追加
            // 最初に全部消す
            db!!.delete("history", "word = '$word' AND pat = '$pat'", null)
            val patind = LocalDict.patInd(pat)
            // SQLite3の日付処理
            // http://www.tamandua-webtools.net/sqlite3-date.html
            db!!.execSQL("insert into history(word,pat,patind,date) values ('$word', '$pat', $patind, datetime('now', 'localtime'));")
        }

        fun limit(max: Int) { // max個までにDBを制限する
            var max = max
            val cursor: Cursor
            var word: String
            var pat: String
            cursor = db!!.query("history", arrayOf("word", "pat", "patind", "date"), null, null, null, null, "date desc")
            val count = cursor.count
            while (max < count) {
                cursor.moveToPosition(max)
                word = cursor.getString(0)
                pat = cursor.getString(1)
                //Log.v("SQLite","delete -> " + word);
                db!!.delete("history", "word = '$word' AND pat = '$pat'", null)
                max++
            }
            cursor.close()
        }

        fun search(pat: String, exactMode: Boolean): Array<Array<String?>> { // 新しいものから検索
            val words = ArrayList<String>()
            val wordpats = ArrayList<String>()
            val pattern = if (exactMode) Pattern.compile("^" + pat) else Pattern.compile("^$pat.*")
            //Log.v("Gyaim","pattern="+pattern);

            val cursor = db!!.query("history", arrayOf("word", "pat", "date"),
                    "patind = " + LocalDict.patInd(pat), null, null, null, "date desc")
            var isEof = cursor.moveToFirst()
            while (isEof) {
                val word = cursor.getString(0)
                val wordpat = cursor.getString(1)
                //Log.v("Gyaim",String.format("word:%s wordpat:%s\r\n", word, wordpat));
                if (pattern.matcher(wordpat).matches()) {
                    //Log.v("Gyaim/SQLite - match",String.format("word:%s pat:%s\r\n", word, wordpat));
                    words.add(word)
                    wordpats.add(wordpat)
                }
                isEof = cursor.moveToNext()
            }
            cursor.close()
            //Log.v("Gyaim","length = "+words.size());
            val res = Array<Array<String?>>(words.size) { arrayOfNulls<String>(2) }
            for (i in words.indices) {
                res[i][0] = words[i]
                res[i][1] = wordpats[i]
            }
            return res
        }
    }
}
