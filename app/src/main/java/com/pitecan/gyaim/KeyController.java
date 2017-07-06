//
//  Gyaimのキー操作に対するアクション (変換などの状態遷移)
//
package com.pitecan.gyaim;

import java.util.ArrayList;

import android.view.View;
import android.view.KeyEvent;
import android.text.TextUtils;

class KeyController {

    private static Gyaim gyaim;
    private static CandView candView;

    public static ArrayList<String> inputPatArray; // 入力文字の配列

    // 状態変数
    public static int nthCandSelected = 0; // 0のときは候補選択前
    private static boolean japaneseInputMode = true;
    private static boolean exactMode = false;
    private static boolean shift = false;
    private static boolean alt = false;

    private SearchTask searchTask = null;

    public KeyController(Gyaim gyaim, CandView candView) {
        this.gyaim = gyaim;
        this.candView = candView;
        resetInput();
    }

    private void resetInput() {
        inputPatArray = new ArrayList<String>();
        nthCandSelected = 0;
        exactMode = false;
        shift = false;
        alt = false;
    }

    private void searchAndDispCand() {
        Message.message("Gyaim", "searchAndDispCand()");
        //
        // バックグラウンドで検索実行 (AsyncTask機能)
        //
        searchTask = new SearchTask(candView);
        searchTask.execute(inputPat());
    }

    public static String inputPat() {
        return TextUtils.join("", inputPatArray);
    }

    private void fix() {
        if (nthCandSelected > 0) { // 候補選択状態
            String word = Search.candidates[nthCandSelected - 1].word;
            gyaim.commitText(word); // 選択単語を貼り付け
            Search.sqlDict.add(word, inputPat());
            Search.sqlDict.limit(1000); // 1000個以上になれば古いエントリを消す
        } else {
            gyaim.commitText(inputPat()); // 入力パタンを貼り付け
        }
        resetInput();
        Search.reset();
        candView.invalidate();
        gyaim.setOldClipboardText();
    }

    boolean onKeyDown(int keyCode, KeyEvent event) {
        if (searchTask != null) {
            searchTask.cancel(true); // キー入力があったらバックグラウンド検索を中止
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 画面上の左矢印キー
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_SYM) {
            // SYMキーのデフォルト動作(?)は変なダイアログが出るので
            // モード切り換えに利用してみる
            japaneseInputMode = !japaneseInputMode;
            ;
            candView.setVisibility(japaneseInputMode ? View.VISIBLE : View.GONE);
            resetInput();
            fix();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
            // 右シフトキーで日本語モード/素通しモード切替
            japaneseInputMode = !japaneseInputMode;
            candView.setVisibility(japaneseInputMode ? View.VISIBLE : View.GONE);
            resetInput();
            fix();
            return true;
        }
        if (!japaneseInputMode) {
            // 日本語モードでないときはfalseを返して素通しデフォルト動作(?)させる
            return false;
        }
        //
        // これ以降は日本語入力モードの挙動
        // 状態遷移を特に記述するほどではないという理解で
        //
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            shift = true;
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT) {
            alt = true;
            return false;
        }
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z && !shift && !alt) {
            int code = 0x61 + (keyCode - KeyEvent.KEYCODE_A);
            char[] charArray = Character.toChars(code);
            String s = new String(charArray);

            if (nthCandSelected > 0) { // 候補選択状態だったら確定する
                fix();
            }

            inputPatArray.add(s);
            gyaim.showComposingText(inputPat());

            searchAndDispCand();
        }
        if (alt) {
            String c = "";
            switch (keyCode) {
                case KeyEvent.KEYCODE_A:
                    c = "*";
                    break;
                case KeyEvent.KEYCODE_B:
                    c = "!";
                    break;
                case KeyEvent.KEYCODE_C:
                    c = "9";
                    break;
                case KeyEvent.KEYCODE_D:
                    c = "5";
                    break;
                case KeyEvent.KEYCODE_E:
                    c = "2";
                    break;
                case KeyEvent.KEYCODE_F:
                    c = "6";
                    break;
                case KeyEvent.KEYCODE_G:
                    c = "/";
                    break;
                case KeyEvent.KEYCODE_H:
                    c = ":";
                    break;
                case KeyEvent.KEYCODE_I:
                    c = "-";
                    break;
                case KeyEvent.KEYCODE_J:
                    c = ";";
                    break;
                case KeyEvent.KEYCODE_K:
                    c = "'";
                    break;
                case KeyEvent.KEYCODE_L:
                    c = "\"";
                    break;
                case KeyEvent.KEYCODE_M:
                    c = ".";
                    break;
                case KeyEvent.KEYCODE_N:
                    c = ",";
                    break;
                case KeyEvent.KEYCODE_O:
                    c = "+";
                    break;
                case KeyEvent.KEYCODE_P:
                    c = "@";
                    break;
                case KeyEvent.KEYCODE_Q:
                    c = "#";
                    break;
                case KeyEvent.KEYCODE_R:
                    c = "3";
                    break;
                case KeyEvent.KEYCODE_S:
                    c = "4";
                    break;
                case KeyEvent.KEYCODE_T:
                    c = "(";
                    break;
                case KeyEvent.KEYCODE_U:
                    c = "_";
                    break;
                case KeyEvent.KEYCODE_V:
                    c = "?";
                    break;
                case KeyEvent.KEYCODE_W:
                    c = "1";
                    break;
                case KeyEvent.KEYCODE_X:
                    c = "8";
                    break;
                case KeyEvent.KEYCODE_Y:
                    c = ":";
                    break;
                case KeyEvent.KEYCODE_Z:
                    c = "7";
                    break;
                case KeyEvent.KEYCODE_0:
                    c = "0";
                    break;
            }
            if (c.length() == 1) {
                if (nthCandSelected > 0) { // 候補選択状態だったら確定する
                    fix();
                }
                inputPatArray.add(c);
                gyaim.showComposingText(inputPat());
                searchAndDispCand();
            }
        }
        if (keyCode == KeyEvent.KEYCODE_SPACE) { // 候補選択
            if (inputPatArray.size() == 0) {
                return false;
            }
            nthCandSelected += 1;
            gyaim.showComposingText(Search.candidates[nthCandSelected - 1].word);
            candView.invalidate(); // 候補表示更新
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) { // 確定
            if (inputPatArray.size() == 0) {
                return false;
            }
            if (nthCandSelected > 0 || exactMode) {
                fix();
                exactMode = false;
                LocalDict.exactMode = false;
            } else {
                exactMode = true;
                LocalDict.exactMode = true;
                searchAndDispCand();

                nthCandSelected += 1;
                //gyaim.showComposingText(Search.candidates[nthCandSelected-1].word);
                gyaim.showComposingText(Romakana.roma2hiragana(inputPat())); // これは苦しい
                candView.invalidate(); // 候補表示更新
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (nthCandSelected > 0) { // 候補選択状態
                nthCandSelected -= 1;
                if (nthCandSelected > 0) {
                    gyaim.showComposingText(Search.candidates[nthCandSelected - 1].word);
                } else {
                    gyaim.showComposingText(inputPat());
                }
                candView.invalidate(); // 候補表示更新
            } else {
                int size = inputPatArray.size();
                if (size > 0) {
                    inputPatArray.remove(size - 1);
                    gyaim.showComposingText(inputPat());
                    searchAndDispCand();
                } else {
                    return false; // 変換中でないのでデフォルト動作
                }
            }
        }
        return true;
    }

    boolean onKeyUp(int keyCode, KeyEvent event) {
        if (japaneseInputMode) {
            if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
                shift = false;
                return false;
            }
            if (keyCode == KeyEvent.KEYCODE_ALT_LEFT) {
                alt = false;
                return false;
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) { // 画面上の左矢印キー
                // これがないとうまくBackしない
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
