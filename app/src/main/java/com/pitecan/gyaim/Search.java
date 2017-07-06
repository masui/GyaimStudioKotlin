//
// いろんな辞書を検索するものをまとめる
// その後でまとめてAsyncTaskにする。
// 
package com.pitecan.gyaim;

import java.util.Arrays;

import android.util.Log;

import android.content.res.AssetManager;

import java.io.InputStream;
import java.io.IOException;
import java.util.Objects;

class Candidate {
    String pat, word;
    int weight;

    public Candidate(String pat, String word, int weight) {
        this.pat = pat;
        this.word = word;
        this.weight = weight;
    }
}

class Search {
    //private static LocalDict localDict;
    static SQLDict sqlDict;
    private static Gyaim gyaim;

    public static Candidate[] candidates = new Candidate[Gyaim.Companion.getMAXCANDS()];  // 候補単語リスト
    public static int ncands = 0;
    public static boolean useGoogle = true; // GoogleIMEで検索するかどうか

    Search(Gyaim gyaim) {
        Search.gyaim = gyaim;
        //
        // 内蔵固定辞書
        //
        //localDict = null;
        try {
            AssetManager as = gyaim.getResources().getAssets();
            InputStream is = as.open("dict.txt");
        /* localDict = */
            new LocalDict(is);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //
        // 学習辞書(SQL)
        //
        sqlDict = new SQLDict(gyaim);

        for (int i = 0; i < Gyaim.Companion.getMAXCANDS(); i++) {
            candidates[i] = new Candidate("", "", 0);
        }
    }

    public static void reset() {
        ncands = 0;
    }

    //
    // いろんな辞書を使った検索!
    //
    public static Candidate[] search(String pat, SearchTask searchTask) {
        Message.message("Gyaim", "Search - pat=" + pat);
        ncands = 0;

        //
        // 完全一致モードではひらがな/カタカナを候補に出す
        //
        if (LocalDict.exactMode) {
            String hiragana = Romakana.roma2hiragana(pat);
            String katakana = Romakana.roma2katakana(pat);
            addCandidateWithLevel(hiragana, pat, -100);
            addCandidateWithLevel(katakana, pat, -99);
        }

        //
        // クリップボードの単語を候補に出す (新規登録用)
        //
        if (!LocalDict.exactMode) {
            String s = gyaim.getNewClipboardText();
            if (!Objects.equals(s, "") && s.length() < 10) { // コピー文字列が短い場合だけ候補にする
                addCandidate(s, pat);
            }
        }

        //
        // SQLの学習辞書検索
        //
        String[][] s = sqlDict.match(pat, LocalDict.exactMode);
        for (int k = 0; k < s.length; k++) {
            addCandidateWithLevel(s[k][0], s[k][1], -50 + k);
        }

        //
        // 通常のローカル辞書を検索
        // 時間がかかることがあるのでSearchTaskでバックグラウンド動作させている。
        // 何かキー入力があれば isCancelled() がtrueになる。
        //
        LocalDict.search(pat, searchTask);

        //
        // Google検索
        //
        Message.message("Gyaim", "UseGoogle = " + useGoogle);
        if (!searchTask.isCancelled()) {
            // Google Suggest または Google日本語入力を利用
            if (useGoogle) {
                Message.message("Gyaim", "isConnected() = " + gyaim.isConnected());
                if (gyaim.isConnected()) {
                    // 昔はGoogleSuggestを使っていたが制限があるようなのでGoogleIME APIを利用する
                    // String[] suggestions = GoogleSuggest.suggest(word);
                    String[] suggestions = GoogleIME.ime(Romakana.roma2hiragana(pat));
                    Log.v("Gyaim", "length=" + suggestions.length);
                    for (int i = 0; suggestions[i] != null && suggestions[i] != ""; i++) {
                        Message.message("Gyaim", "Use Google ... suggestions = " + suggestions[i]);
                        addCandidateWithLevel(suggestions[i], KeyController.inputPat(), 50);
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

        return candidates;
    }

    public static void addCandidate(String word, String pat) {
        addCandidateWithLevel(word, pat, 0);
    }

    public static void addCandidateWithLevel(String word, String pat, int level) {
        int i;
        //Message.message("Gyaim","addCandidate: word="+word+" pat="+pat+" ncands="+ncands+" level="+level);
        if (ncands >= Gyaim.Companion.getMAXCANDS()) return;
        for (i = 0; i < ncands; i++) {
            if (candidates[i].word == null) break;
            if (candidates[i].word.equals(word)) break;
        }
        if (i >= ncands) {
            candidates[ncands].pat = pat;
            candidates[ncands].word = word;
            candidates[ncands].weight = level;
            //Message.message("Gyaim", "Add "+word+" to candidates");
            ncands++;
        }
    }
}
