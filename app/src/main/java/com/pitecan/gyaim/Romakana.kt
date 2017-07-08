package com.pitecan.gyaim

// class Romakana
// companion object {
object Romakana {
    val RKTABLE = arrayOf(
            arrayOf("a", "あ", "ア"),
            arrayOf("ba", "ば", "バ"),
            arrayOf("be", "べ", "ベ"),
            arrayOf("bi", "び", "ビ"),
            arrayOf("bo", "ぼ", "ボ"),
            arrayOf("bu", "ぶ", "ブ"),
            arrayOf("bya", "びゃ", "ビャ"),
            arrayOf("bye", "びぇ", "ビェ"),
            arrayOf("byi", "びぃ", "ビィ"),
            arrayOf("byo", "びょ", "ビョ"),
            arrayOf("byu", "びゅ", "ビュ"),
            arrayOf("cha", "ちゃ", "チャ"),
            arrayOf("che", "ちぇ", "チェ"),
            arrayOf("chi", "ち", "チ"),
            arrayOf("cho", "ちょ", "チョ"),
            arrayOf("chu", "ちゅ", "チュ"),
            arrayOf("da", "だ", "ダ"),
            arrayOf("de", "で", "デ"),
            arrayOf("dha", "でゃ", "デャ"),
            arrayOf("dhe", "でぇ", "デェ"),
            arrayOf("dhi", "でぃ", "ディ"),
            arrayOf("dho", "でょ", "デョ"),
            arrayOf("dhu", "でゅ", "デュ"),
            arrayOf("di", "ぢ", "ヂ"),
            arrayOf("do", "ど", "ド"),
            arrayOf("du", "づ", "ヅ"),
            arrayOf("dya", "ぢゃ", "ヂャ"),
            arrayOf("dye", "ぢぇ", "ヂェ"),
            arrayOf("dyi", "ぢぃ", "ヂィ"),
            arrayOf("dyo", "ぢょ", "ヂョ"),
            arrayOf("dyu", "でゅ", "デュ"),
            arrayOf("e", "え", "エ"),
            arrayOf("fa", "ふぁ", "ファ"),
            arrayOf("fe", "ふぇ", "フェ"),
            arrayOf("fi", "ふぃ", "フィ"),
            arrayOf("fo", "ふぉ", "フォ"),
            arrayOf("fuxyu", "ふゅ", "フュ"),
            arrayOf("fu", "ふ", "フ"),
            arrayOf("ga", "が", "ガ"),
            arrayOf("ge", "げ", "ゲ"),
            arrayOf("gi", "ぎ", "ギ"),
            arrayOf("go", "ご", "ゴ"),
            arrayOf("gu", "ぐ", "グ"),
            arrayOf("gya", "ぎゃ", "ギャ"),
            arrayOf("gye", "ぎぇ", "ギェ"),
            arrayOf("gyi", "ぎぃ", "ギィ"),
            arrayOf("gyo", "ぎょ", "ギョ"),
            arrayOf("gyu", "ぎゅ", "ギュ"),
            arrayOf("ha", "は", "ハ"),
            arrayOf("he", "へ", "ヘ"),
            arrayOf("hi", "ひ", "ヒ"),
            arrayOf("ho", "ほ", "ホ"),
            arrayOf("hu", "ふ", "フ"),
            arrayOf("hya", "ひゃ", "ヒャ"),
            arrayOf("hye", "ひぇ", "ヒェ"),
            arrayOf("hyi", "ひぃ", "ヒィ"),
            arrayOf("hyo", "ひょ", "ヒョ"),
            arrayOf("hyu", "ひゅ", "ヒュ"),
            arrayOf("i", "い", "イ"),
            arrayOf("ja", "じゃ", "ジャ"),
            arrayOf("je", "じぇ", "ジェ"),
            arrayOf("ji", "じ", "ジ"),
            arrayOf("jo", "じょ", "ジョ"),
            arrayOf("ju", "じゅ", "ジュ"),
            arrayOf("ka", "か", "カ"),
            arrayOf("ke", "け", "ケ"),
            arrayOf("ki", "き", "キ"),
            arrayOf("ko", "こ", "コ"),
            arrayOf("ku", "く", "ク"),
            arrayOf("kya", "きゃ", "キャ"),
            arrayOf("kye", "きぇ", "キェ"),
            arrayOf("kyi", "きぃ", "キィ"),
            arrayOf("kyo", "きょ", "キョ"),
            arrayOf("kyu", "きゅ", "キュ"),
            arrayOf("ma", "ま", "マ"),
            arrayOf("me", "め", "メ"),
            arrayOf("mi", "み", "ミ"),
            arrayOf("mo", "も", "モ"),
            arrayOf("mu", "む", "ム"),
            arrayOf("mya", "みゃ", "ミャ"),
            arrayOf("mye", "みぇ", "ミェ"),
            arrayOf("myi", "みぃ", "ミィ"),
            arrayOf("myo", "みょ", "ミョ"),
            arrayOf("myu", "みゅ", "ミュ"),

            // P"n'",	"ん",	"ン"},
            arrayOf("nn", "ん", "ン"),
            arrayOf("na", "な", "ナ"),
            arrayOf("ne", "ね", "ネ"),
            arrayOf("ni", "に", "ニ"),
            arrayOf("no", "の", "ノ"),
            arrayOf("nu", "ぬ", "ヌ"),
            arrayOf("nya", "にゃ", "ニャ"),
            arrayOf("nye", "にぇ", "ニェ"),
            arrayOf("nyi", "にぃ", "ニィ"),
            arrayOf("nyo", "にょ", "ニョ"),
            arrayOf("nyu", "にゅ", "ニュ"),
            arrayOf("o", "お", "オ"),
            arrayOf("pa", "ぱ", "パ"),
            arrayOf("pe", "ぺ", "ペ"),
            arrayOf("pi", "ぴ", "ピ"),
            arrayOf("po", "ぽ", "ポ"),
            arrayOf("pu", "ぷ", "プ"),
            arrayOf("pya", "ぴゃ", "ピャ"),
            arrayOf("pye", "ぴぇ", "ピェ"),
            arrayOf("pyi", "ぴぃ", "ピィ"),
            arrayOf("pyo", "ぴょ", "ピョ"),
            arrayOf("pyu", "ぴゅ", "ピュ"),
            arrayOf("ra", "ら", "ラ"),
            arrayOf("re", "れ", "レ"),
            arrayOf("ri", "り", "リ"),
            arrayOf("ro", "ろ", "ロ"),
            arrayOf("ru", "る", "ル"),
            arrayOf("rya", "りゃ", "リャ"),
            arrayOf("rye", "りぇ", "リェ"),
            arrayOf("ryi", "りぃ", "リィ"),
            arrayOf("ryo", "りょ", "リョ"),
            arrayOf("ryu", "りゅ", "リュ"),
            arrayOf("sa", "さ", "サ"),
            arrayOf("se", "せ", "セ"),
            arrayOf("sha", "しゃ", "シャ"),
            arrayOf("she", "しぇ", "シェ"),
            arrayOf("shi", "し", "シ"),
            arrayOf("sho", "しょ", "ショ"),
            arrayOf("shu", "しゅ", "シュ"),
            arrayOf("si", "し", "シ"),
            arrayOf("so", "そ", "ソ"),
            arrayOf("su", "す", "ス"),
            arrayOf("sya", "しゃ", "シャ"),
            arrayOf("sye", "しぇ", "シェ"),
            arrayOf("syi", "しぃ", "シィ"),
            arrayOf("syo", "しょ", "ショ"),
            arrayOf("syu", "しゅ", "シュ"),
            arrayOf("ta", "た", "タ"),
            arrayOf("te", "て", "テ"),
            arrayOf("tha", "てゃ", "テャ"),
            arrayOf("the", "てぇ", "テェ"),
            arrayOf("thi", "てぃ", "ティ"),
            arrayOf("tho", "てょ", "テョ"),
            arrayOf("thu", "てゅ", "テュ"),
            arrayOf("ti", "ち", "チ"),
            arrayOf("to", "と", "ト"),
            arrayOf("tsu", "つ", "ツ"),
            arrayOf("tu", "つ", "ツ"),
            arrayOf("tya", "ちゃ", "チャ"),
            arrayOf("tye", "ちぇ", "チェ"),
            arrayOf("tyi", "ちぃ", "チィ"),
            arrayOf("tyo", "ちょ", "チョ"),
            arrayOf("tyu", "ちゅ", "チュ"),
            arrayOf("u", "う", "ウ"),
            arrayOf("va", "う゛ぁ", "ヴァ"),
            arrayOf("ve", "う゛ぃ", "ヴェ"),
            arrayOf("vi", "う゛ぅ", "ヴィ"),
            arrayOf("vo", "う゛ぉ", "ヴォ"),
            arrayOf("vu", "う゛", "ヴ"),
            arrayOf("wa", "わ", "ワ"),
            arrayOf("we", "うぇ", "ウェ"),
            arrayOf("wi", "うぃ", "ウィ"),
            arrayOf("wo", "を", "ヲ"),
            arrayOf("xa", "ぁ", "ァ"),
            arrayOf("xe", "ぇ", "ェ"),
            arrayOf("xi", "ぃ", "ィ"),
            arrayOf("xo", "ぉ", "ォ"),
            arrayOf("xtu", "っ", "ッ"),
            arrayOf("xtsu", "っ", "ッ"),
            arrayOf("xu", "ぅ", "ゥ"),
            arrayOf("xwa", "ゎ", "ヮ"),
            arrayOf("ya", "や", "ヤ"),
            arrayOf("yo", "よ", "ヨ"),
            arrayOf("yu", "ゆ", "ユ"),
            arrayOf("za", "ざ", "ザ"),
            arrayOf("ze", "ぜ", "ゼ"),
            arrayOf("zi", "じ", "ジ"),
            arrayOf("zo", "ぞ", "ゾ"),
            arrayOf("zu", "ず", "ズ"),
            arrayOf("zya", "じゃ", "ジャ"),
            arrayOf("zye", "じぇ", "ジェ"),
            arrayOf("zyi", "じぃ", "ジィ"),
            arrayOf("zyo", "じょ", "ジョ"),
            arrayOf("zyu", "じゅ", "ジュ"),
            arrayOf("xya", "ゃ", "ャ"),
            arrayOf("xyu", "ゅ", "ュ"),
            arrayOf("xyo", "ょ", "ョ"),
            arrayOf("-", "ー", "ー")
    )

    fun roma2hiragana(roma: String): String {
        return roma2kana(roma, true)
    }

    fun roma2katakana(roma: String): String {
        return roma2kana(roma, false)
    }

    fun substring(s: String, start: Int, end: Int): String {
        val len = s.length
        if (start < 0 || start >= len) return ""
        if (end < start || end > len) return ""
        return s.substring(start, end)
    }


    fun roma2kana(roma: String, hiragana: Boolean): String {
        var okay = true
        var kana = ""
        var ind = 0
        var found: Boolean

        while (ind < roma.length) {
            found = false
            for (aRKTABLE in RKTABLE) {
                val r = aRKTABLE[0]
                val h = if (hiragana) aRKTABLE[1] else aRKTABLE[2]
                val len = r.length
                if (substring(roma, ind, ind + len) == r) {
                    kana += h
                    ind += len
                    found = true
                    break
                }
            }
            if (!found) {
                val r0 = substring(roma, ind, ind + 1)
                val r1 = substring(roma, ind + 1, ind + 2)
                if ((r0 == "n" || r0 == "N") && "bcdfghjklmnpqrstvwxz".contains(r1)) {
                    kana += if (hiragana) "ん" else "ン"
                    ind += 1
                } else {
                    if ("bcdfghjklmpqrstvwxyz".contains(r0) && r0 == r1) {
                        kana += if (hiragana) "っ" else "ッ"
                        ind += 1
                    } else {
                        if ((r0 == "n" || r0 == "N") && r1 != "") {
                            kana += if (hiragana) "ん" else "ン"
                            ind += 1
                        } else {
                            ind += 1
                            okay = false
                        }
                    }
                }
            }
        }
        return kana
    }
}
