//
// LocalDict.ktのテスト用ダミー
//

package com.pitecan.gyaim

import java.util.ArrayList

class Search {
    companion object {
        var ncands = 0
        var words = ArrayList<String>()
        var pats = ArrayList<String>()

        fun addCandidateWithLevel(word: String, pat: String, level: Int) {
            words.add(word)
            pats.add(pat)
            ncands++
        }
    }
}
