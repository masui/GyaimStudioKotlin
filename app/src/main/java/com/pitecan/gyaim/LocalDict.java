//
// ローカル辞書ファイルを使った変換
//
// このファイルにはAndroid依存のものを入れないようにしたいのだが
// 計算中にユーザ入力チェックみたいことをしてるので難しいかもしれない
//
package com.pitecan.gyaim;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class LocalDict {
    private static int[] keyLink = new int[10];
    private static int[] connectionLink = new int[2000];
    private static Pattern[] regexp = new Pattern[50];       // パタンの部分文字列にマッチするRegExp
    private static int[] cslength = new int[50];             // regexp[n]に完全マッチするパタンの長さ

    private static String[] wordStack = new String[20];
    private static String[] patStack = new String[20];

    static boolean exactMode = false;

    private static int fib1, fib2; // フィボナッチ数... なんでこんなの使ってたんだろ?

    private class DictEntry {
        String pat, word;
        int inConnection, outConnection;
        int keyLink;
        int connectionLink;

        DictEntry(String p, String w, int i, int o) {
            pat = p;
            word = w;
            inConnection = i;
            outConnection = o;
        }

        public String word() {
            return word;
        }
    }

    private static ArrayList<DictEntry> dict = new ArrayList<DictEntry>();

    public ArrayList<DictEntry> dict() {
        return dict;
    }

    LocalDict(InputStream is) {
        try {
            InputStreamReader in;
            BufferedReader br;

            String line;

            in = new InputStreamReader(is);
            br = new BufferedReader(in);
            while ((line = br.readLine()) != null) {
                // Message.message("Gyaim",line);
                int c = line.charAt(0);
                if (c == '#' || c == ' ' || c == '\t') continue; // コメント行
                String[] a = line.split("\t", 4);
                if (a[3] == null || Objects.equals(a[3], "") || a[3].length() == 0 || ("" + a[3] == ""))
                    a[3] = "0";
                // Message.message("Gyaim",a[1]);
                dict.add(new DictEntry(a[0], a[1], Integer.valueOf(a[2]), Integer.valueOf(a[3])));
            }
            br.close();
            in.close();
            // Message.message("Gyaim","" + dict.size());

        } catch (IOException e) {
            e.printStackTrace();
        }

        initLink(); // 辞書エントリ間のリンク設定
    }

    private static void initLink() {
        // Message.message("Gyaim","initLink");
        //
        // 先頭読みが同じ単語のリスト
        //
        int[] cur = new int[10];
        for (int i = 0; i < 10; i++) {
            keyLink[i] = -1;
        }
        for (int i = 0; i < dict.size(); i++) {
            if (dict.get(i).word.startsWith("*")) continue;
            // if(dict[i].inConnection < 1000) continue; // 活用の接続の場合
            int ind = patInd(dict.get(i).pat);
            if (keyLink[ind] < 0) {
                cur[ind] = i;
                keyLink[ind] = i;
            } else {
                dict.get(cur[ind]).keyLink = i;
                cur[ind] = i;
            }
            dict.get(i).keyLink = -1; // リンクの末尾
        }
        //
        // コネクションつながりのリスト
        //
        cur = new int[2000];
        for (int i = 0; i < 2000; i++) {
            connectionLink[i] = -1;
        }
        for (int i = 0; i < dict.size(); i++) {
            int ind = dict.get(i).inConnection;
            if (connectionLink[ind] < 0) {
                cur[ind] = i;
                connectionLink[ind] = i;
            } else {
                dict.get(cur[ind]).connectionLink = i;
                cur[ind] = i;
            }
            dict.get(i).connectionLink = -1; // リンクの末尾
        }
    }

    private static String patInit(String pat, int level) {
        String p = "";
        String top = "";
        Pattern re;
        Matcher matcher;

        cslength[level] = 0;
        if (pat.length() > 0) {
            re = Pattern.compile("^(\\[[^\\]]+\\])(.*)$");
            matcher = re.matcher(pat);
            if (matcher.find()) {
                top = matcher.group(1);
                p = patInit(matcher.group(2), level + 1);
            } else {
                re = Pattern.compile("^(.)(.*)$");
                matcher = re.matcher(pat);
                matcher.find();
                top = matcher.group(1);
                p = patInit(matcher.group(2), level + 1);
            }
            cslength[level] = cslength[level + 1] + 1;
        }

        top += (p.length() > 0 ? "(" + p + ")?" : "");
        regexp[level] = Pattern.compile("^(" + top + ")");
        return top;
    }

    // ローカル辞書の接続検索
    static void search(String pat, SearchTask searchTask) {
        patInit(pat, 0);
        fib1 = fib2 = 1;
        generateCand(0, patInd(pat), 0, "", "", 0, searchTask); // 接続辞書を使って候補を生成
    }

    // パタンのlen文字目からのマッチを調べる
    // 接続リンクを深さ優先検索してマッチするものを候補に加えていく
    private static void generateCand(int connection, int keylink, int len, String word, String pat, int level, SearchTask searchTask) {
        Message.message("Gyaim","GenerateCand("+word+","+pat+","+level+")");
        wordStack[level] = word;
        patStack[level] = pat;

        int patlen = cslength[len];
        int d = (connection != 0 ? connectionLink[connection] : keyLink[keylink]);
        for (; d >= 0 && Search.ncands < Gyaim.MAXCANDS; d = (connection != 0 ? dict.get(d).connectionLink : dict.get(d).keyLink)) {
            if (searchTask.isCancelled()) break;
            Matcher m = regexp[len].matcher(dict.get(d).pat);
            if (m.find()) {
                int matchlen = m.group(1).length();
                if (matchlen == patlen && (!exactMode || exactMode && dict.get(d).pat.length() == matchlen)) { // 最後までマッチ
                    addConnectedCandidate(dict.get(d).word, dict.get(d).pat, dict.get(d).outConnection, level, matchlen);
                    // Message.message("Gyaim","ncands = " + Search.ncands + ", fib1 = " + fib1);
                    if (Search.ncands >= fib1) {
                        searchTask.progress(0); //いくつかみつかったら画面更新
                        int tmp = fib1;
                        fib1 += fib2;
                        fib2 = tmp;
                    }
                } else if (matchlen == dict.get(d).pat.length() && dict.get(d).outConnection != 0) { // とりあえずその単語まではマッチ
                    generateCand(dict.get(d).outConnection, 0, len + matchlen, dict.get(d).word, dict.get(d).pat, level + 1, searchTask);
                }
            }
        }
    }

    static void addConnectedCandidate(String word, String pat, int connection, int level, int matchlen) { // 候補追加
        int i;
        if (Objects.equals(word, "")) return; // 2011/11/3
        //if(word.charAt(0) == '*') return; // 単語活用の途中
        if (word.charAt(word.length() - 1) == '*') return;

        String p = "";
        for (i = 0; i < level + 1; i++) p += patStack[i];
        p += pat;
        String w = "";
        for (i = 0; i < level + 1; i++) w += wordStack[i];
        w += word;

        w = w.replaceAll("\\*", "");
        Search.addCandidateWithLevel(w, p, level);
        // Message.message("Gyaim","addCandidateWithLevel: word = " + w + "  pattern = " + p + "  level = " + level);
    }

    private static Pattern[] patIndPattern = new Pattern[10];
    private static boolean patIndInitialized = false;

    static int patInd(String str) {
        if (!patIndInitialized) {
            patIndPattern[0] = Pattern.compile("\\[?[aiueoAIUEO].*");
            patIndPattern[1] = Pattern.compile("\\[?[kg].*");
            patIndPattern[2] = Pattern.compile("\\[?[sz].*");
            patIndPattern[3] = Pattern.compile("\\[?[tdT].*");
            patIndPattern[4] = Pattern.compile("\\[?[n].*");
            patIndPattern[5] = Pattern.compile("\\[?[hbp].*");
            patIndPattern[6] = Pattern.compile("\\[?[m].*");
            patIndPattern[7] = Pattern.compile("\\[?[yY].*");
            patIndPattern[8] = Pattern.compile("\\[?[r].*");
            patIndInitialized = true;
        }
        for (int i = 0; i < 9; i++) {
            Matcher matcher = patIndPattern[i].matcher(str);
            if (matcher.find()) return i;
        }
        return 9;
    }
}
