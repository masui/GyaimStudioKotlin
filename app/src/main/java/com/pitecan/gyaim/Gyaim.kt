//
//	Gyaim for Android
//
//	Slimeを改造し、キーボード専用にする 2017/06/27 12:17:19 (masui)
//

package com.pitecan.gyaim

import android.inputmethodservice.InputMethodService
////import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.CursorAnchorInfo

import android.os.Bundle
import android.os.Build // for Build.VERSION.SDK_INT
import android.view.View
import android.view.ViewGroup
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Button
import android.util.Log

import android.content.Context

import android.text.ClipboardManager
// Android3.0以上の場合こちらを使うらしい
//import android.content.ClipData;
//import android.content.ClipboardManager;

import android.net.ConnectivityManager
import android.net.NetworkInfo

import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
//import android.view.ViewGroup.AbsoluteLayoutParams;

import android.graphics.Matrix
import android.graphics.Rect

class Gyaim : InputMethodService() {
    //private Keys keys;
    private var candView: CandView? = null

    private var keyController: KeyController? = null
    private var search: Search? = null

    private var clipboardManager: ClipboardManager? = null
    private var oldClipboardText = ""

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    override fun onCreate() {
        super.onCreate()
        search = Search(this)
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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
    override fun onStartInput(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInput(attribute, restarting)

        val ic = currentInputConnection
        ic.requestCursorUpdates(InputConnection.CURSOR_UPDATE_MONITOR)
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
    override fun onCreateInputView(): View? {
        // return super.onCreateInputView(); // 必要??

        val dummyCandView = layoutInflater.inflate(R.layout.candidate, null) as CandView

        Log.v("Gyaim", "CANDVIEW = " + candView)

        // candView.setY(100);
        // candView.setTop(100);

        // setCandidatesViewShown(true); // これが無いと表示されない

        keyController = KeyController(this, candView) // この場所?

        return candView
    }

    /*
      候補を表示するView。
     */
    override fun onCreateCandidatesView(): View? {
        super.onCreateCandidatesView() // 必要??

        candView = layoutInflater.inflate(R.layout.candidate, null) as CandView

        Log.v("Gyaim", "CANDVIEW = " + candView)

        // candView.setY(100);
        // candView.setTop(100);

        setCandidatesViewShown(true) // これが無いと表示されない

        keyController = KeyController(this, candView) // この場所?

        return candView as View
    }

    fun commitText(s: String) {
        currentInputConnection.commitText(s, 1)
    }

    fun showComposingText(text: String) {
        currentInputConnection.setComposingText(text, 1)
    }

    //
    // 新規登録用にクリップボードの単語を返す
    //
    fun setOldClipboardText() {
        val seq = clipboardManager!!.text
        oldClipboardText = seq?.toString() ?: ""
    }

    val newClipboardText: String
        get() {
            val seq = clipboardManager!!.text
            val cilpboardText = seq?.toString() ?: ""
            if (cilpboardText == oldClipboardText) {
                return ""
            } else {
                return cilpboardText
            }
        }

    //
    // KeyイベントをすべてKeyControllerに投げる
    //
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.v("Gyaim", "onKeyDown - keyCode = " + keyCode)
        return keyController!!.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        Log.v("Gyaim", "onKeyUp - keyCode = " + keyCode)
        return keyController!!.onKeyUp(keyCode, event)
    }

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
    val isConnected: Boolean?
        get() {
            val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.activeNetworkInfo
            Message.message("Gyaim", "networkInfo = " + networkInfo!!)
            return networkInfo.isConnected
        }

    fun logWord(word: String) {}


    internal fun updateCursor(newCursor: Rect) {
        Message.message("Gyaim", "updateCursor: rect = " + newCursor)
    }

    internal fun updateCursorAnchorInfo(view: View, cursorAnchorInfo: CursorAnchorInfo) {
        Message.message("Gyaim", "updateCursorAnchorInfo: view = " + view)
    }

    //
    // キャレットの位置を取得する方法?
    //
    // https://stackoverflow.com/questions/4930416/android-edittexts-cursor-coordinates-absolute-position
    override fun onUpdateCursorAnchorInfo(cursorAnchorInfo: CursorAnchorInfo) {
        /*
      	InputConnection ic = getCurrentInputConnection();
	ic.requestCursorUpdates(int cursorUpdateMode)
	cursorAnchorInfo.getInsertionMarkerHorizontal() (x)
	    and cursorAnchorInfo.getInsertionMarkerTop() (y).
	*/
        Message.message("Gyaim", "insertionMarkerTop = " + cursorAnchorInfo.insertionMarkerTop) // NaNになることあり
        Message.message("Gyaim", "insertionMarkerHorizontal = " + cursorAnchorInfo.insertionMarkerHorizontal) // NaNになることあり

        val matrix = cursorAnchorInfo.matrix
        val f = FloatArray(9)
        matrix.getValues(f)
        Log.v("Gyaim", "matrix = " + f[0])
        Log.v("Gyaim", "matrix = " + f[1])
        Log.v("Gyaim", "matrix = " + f[2])
        Log.v("Gyaim", "matrix = " + f[3])
        Log.v("Gyaim", "matrix = " + f[4])
        Log.v("Gyaim", "matrix = " + f[5])
        Log.v("Gyaim", "matrix = " + f[6])
        Log.v("Gyaim", "matrix = " + f[7])
        Log.v("Gyaim", "matrix = " + f[8])

        val loc = IntArray(2)
        candView!!.getLocationInWindow(loc)
        Log.v("Gyaim", "location x= " + loc[0])
        Log.v("Gyaim", "location y= " + loc[1])

        val floc = FloatArray(2)
        //floc[0] = (float)loc[0];
        //floc[1] = (float)loc[1];
        floc[0] = 0.0.toFloat()
        floc[1] = cursorAnchorInfo.insertionMarkerTop.toFloat()
        matrix.mapPoints(floc)
        Log.v("Gyaim", "flocation x= " + floc[0])
        Log.v("Gyaim", "flocation y= " + floc[1])

        val lp = candView!!.layoutParams
        val mlp = lp as MarginLayoutParams
        //AbsoluteLayoutParams alp = (AbsoluteLayoutParams)lp; こんなのないか
        Log.v("Gyaim", "topmargin = " + mlp.topMargin)
        Log.v("Gyaim", "bottommargin = " + mlp.bottomMargin)
        Log.v("Gyaim", "leftmargin = " + mlp.leftMargin)
        Log.v("Gyaim", "rightmargin = " + mlp.rightMargin)

        val markerTop = cursorAnchorInfo.insertionMarkerTop.toInt()
        var margin: Int
        if (java.lang.Float.isNaN(floc[1])) {
            margin = 0
        } else {
            //margin = 1400 - (int)(f[2] + f[5] + markerTop);
            margin = 1400 - floc[1].toInt() //  + markerTop);
        }
        if (margin < 0) margin = 0

        mlp.setMargins(mlp.leftMargin, mlp.topMargin, mlp.rightMargin, margin) // これで表示場所を変えられる
        //マージンを設定
        candView!!.layoutParams = mlp
    }

    companion object {

        val MAXCANDS = 50
    }
}
