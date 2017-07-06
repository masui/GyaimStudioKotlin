//
//	Gyaim for Android
//
//	Slimeを改造し、キーボード専用にする 2017/06/27 12:17:19 (masui)
//

package com.pitecan.gyaim;

import android.inputmethodservice.InputMethodService;
////import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.CursorAnchorInfo;

import android.os.Bundle;
import android.os.Build; // for Build.VERSION.SDK_INT
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.util.Log;

import android.content.Context;

import android.text.ClipboardManager;
// Android3.0以上の場合こちらを使うらしい
//import android.content.ClipData;
//import android.content.ClipboardManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
//import android.view.ViewGroup.AbsoluteLayoutParams;

import android.graphics.Matrix;
import android.graphics.Rect;

public class Gyaim extends InputMethodService {
    //private Keys keys;
    private CandView candView;

    private KeyController keyController;
    private Search search;

    private ClipboardManager clipboardManager;
    private String oldClipboardText = "";

    static final int MAXCANDS = 50;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        search = new Search(this);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    /*
      This is the point where you can do all of your UI initialization.  It
      is called after creation and any configuration change.
     */
    /*
    @Override public void onInitializeInterface() {
	super.onInitializeInterface(); // 必要??
    }
    */

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        InputConnection ic = getCurrentInputConnection();
        ic.requestCursorUpdates(InputConnection.CURSOR_UPDATE_MONITOR);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    /*
      InputViewはキーボードを表示するViewなのだが、これが出ているとき
      アプリの大きさはそのぶんだけ減る。なので入力枠が最下部にある
      場合などは候補を出すためにこれが定義されていると良いのかもしれないのだが...
     */
    @Override
    public View onCreateInputView() {
        // return super.onCreateInputView(); // 必要??

        CandView dummyCandView = (CandView) getLayoutInflater().inflate(R.layout.candidate, null);

        Log.v("Gyaim", "CANDVIEW = " + candView);

        // candView.setY(100);
        // candView.setTop(100);

        // setCandidatesViewShown(true); // これが無いと表示されない

        keyController = new KeyController(this, candView); // この場所?

        return candView;
    }

    /*
      候補を表示するView。
     */
    @Override
    public View onCreateCandidatesView() {
        super.onCreateCandidatesView(); // 必要??

        candView = (CandView) getLayoutInflater().inflate(R.layout.candidate, null);

        Log.v("Gyaim", "CANDVIEW = " + candView);

        // candView.setY(100);
        // candView.setTop(100);

        setCandidatesViewShown(true); // これが無いと表示されない

        keyController = new KeyController(this, candView); // この場所?

        return candView;
    }

    public void commitText(String s) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) ic.commitText(s, 1); // 入力貼り付け
    }

    public void showComposingText(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.setComposingText(text, 1);
        }
    }

    //
    // 新規登録用にクリップボードの単語を返す
    //
    public void setOldClipboardText() {
        CharSequence seq = clipboardManager.getText();
        oldClipboardText = (seq == null ? "" : seq.toString());
    }

    public String getNewClipboardText() {
        CharSequence seq = clipboardManager.getText();
        String cilpboardText = (seq == null ? "" : seq.toString());
        if (cilpboardText.equals(oldClipboardText)) {
            return "";
        } else {
            return cilpboardText;
        }
    }

    //
    // KeyイベントをすべてKeyControllerに投げる
    //
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("Gyaim", "onKeyDown - keyCode = " + keyCode);
        return keyController.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.v("Gyaim", "onKeyUp - keyCode = " + keyCode);
        return keyController.onKeyUp(keyCode, event);
    }

    // こういうものはないようだが
    //public boolean onTouchEvent(MotionEvent event) {
    //	Message.message("Gyaim","onTouchEvent - ");
    //	return false;
    //}

    //
    // ConnectivityManagerというのを使って、ネットが使えるかどうかを判断し、
    // ネットがあるときは常にGoogleIMEを使うようにしてみる
    // http://yife.hateblo.jp/entry/2012/10/29/203330
    // http://wada811.blog.fc2.com/?tag=ConnectivityManager
    // AndroidManifest.xmlに以下の追加が必要である
    // <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    //
    // メソッドがActivityの中でしか使えないのでここで定義する。
    //
    public Boolean isConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        Message.message("Gyaim", "networkInfo = " + networkInfo);
        return networkInfo != null && networkInfo.isConnected();
    }

    public void logWord(String word) {
    }


    void updateCursor(Rect newCursor) {
        Message.message("Gyaim", "updateCursor: rect = " + newCursor);
    }

    void updateCursorAnchorInfo(View view, CursorAnchorInfo cursorAnchorInfo) {
        Message.message("Gyaim", "updateCursorAnchorInfo: view = " + view);
    }

    //
    // キャレットの位置を取得する方法?
    //
    // https://stackoverflow.com/questions/4930416/android-edittexts-cursor-coordinates-absolute-position
    @Override
    public void onUpdateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) {
    /*
	InputConnection ic = getCurrentInputConnection();
	ic.requestCursorUpdates(int cursorUpdateMode)
	cursorAnchorInfo.getInsertionMarkerHorizontal() (x)
	    and cursorAnchorInfo.getInsertionMarkerTop() (y).
	*/
        Message.message("Gyaim", "insertionMarkerTop = " + cursorAnchorInfo.getInsertionMarkerTop()); // NaNになることあり
        Message.message("Gyaim", "insertionMarkerHorizontal = " + cursorAnchorInfo.getInsertionMarkerHorizontal()); // NaNになることあり

        Matrix matrix = cursorAnchorInfo.getMatrix();
        float f[] = new float[9];
        matrix.getValues(f);
        Log.v("Gyaim", "matrix = " + f[0]);
        Log.v("Gyaim", "matrix = " + f[1]);
        Log.v("Gyaim", "matrix = " + f[2]);
        Log.v("Gyaim", "matrix = " + f[3]);
        Log.v("Gyaim", "matrix = " + f[4]);
        Log.v("Gyaim", "matrix = " + f[5]);
        Log.v("Gyaim", "matrix = " + f[6]);
        Log.v("Gyaim", "matrix = " + f[7]);
        Log.v("Gyaim", "matrix = " + f[8]);

        int loc[] = new int[2];
        candView.getLocationInWindow(loc);
        Log.v("Gyaim", "location x= " + loc[0]);
        Log.v("Gyaim", "location y= " + loc[1]);

        float floc[] = new float[2];
        //floc[0] = (float)loc[0];
        //floc[1] = (float)loc[1];
        floc[0] = (float) 0.0;
        floc[1] = (float) cursorAnchorInfo.getInsertionMarkerTop();
        matrix.mapPoints(floc);
        Log.v("Gyaim", "flocation x= " + floc[0]);
        Log.v("Gyaim", "flocation y= " + floc[1]);

        LayoutParams lp = candView.getLayoutParams();
        MarginLayoutParams mlp = (MarginLayoutParams) lp;
        //AbsoluteLayoutParams alp = (AbsoluteLayoutParams)lp; こんなのないか
        Log.v("Gyaim", "topmargin = " + mlp.topMargin);
        Log.v("Gyaim", "bottommargin = " + mlp.bottomMargin);
        Log.v("Gyaim", "leftmargin = " + mlp.leftMargin);
        Log.v("Gyaim", "rightmargin = " + mlp.rightMargin);

        int markerTop = (int) cursorAnchorInfo.getInsertionMarkerTop();
        int margin;
        if (Float.isNaN(floc[1])) {
            margin = 0;
        } else {
            //margin = 1400 - (int)(f[2] + f[5] + markerTop);
            margin = 1400 - (int) (floc[1]); //  + markerTop);
        }
        if (margin < 0) margin = 0;

        mlp.setMargins(mlp.leftMargin, mlp.topMargin, mlp.rightMargin, margin); // これで表示場所を変えられる
        //マージンを設定
        candView.setLayoutParams(mlp);
    }
}
