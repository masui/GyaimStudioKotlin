//
// FontMetricsは以下を参照
// http://wikiwiki.jp/android/?%A5%C6%A5%AD%A5%B9%A5%C8%A4%CE%C9%C1%B2%E8%28FontMetrics%29
//
package com.pitecan.gyaim

import android.view.View
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetrics
import android.content.res.Resources
import android.content.Context
import android.util.AttributeSet
import android.util.Log

import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup.LayoutParams

import android.graphics.Typeface

class CandView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val buttonTextSize = 50
    private var buttonTextPaint: Paint? = null

    companion object {
        var candText = Array<String?>(Gyaim.MAXCANDS, { "" })
        var candPat = Array<String?>(Gyaim.MAXCANDS, { "" })
    }

    private fun initGraphics() {
        // 候補ボタンのテキスト色
        buttonTextPaint = Paint()
        buttonTextPaint!!.isAntiAlias = true
        buttonTextPaint!!.textSize = buttonTextSize.toFloat() //  * expand);
        buttonTextPaint!!.color = 0xff101010.toInt() // 黒
        buttonTextPaint!!.typeface = Typeface.DEFAULT_BOLD
    }

    fun drawDefault() {
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        // Message.message("Gyaim","onDraw----------------------------");

        canvas.drawColor(0xb0d0d0d0.toInt())

        var text: String? = ""
        var textPos = 20f
        var textWidth: Float

        var i = 0
        while (i < 5 && i < Search.ncands) {
            text = candText[i + KeyController.nthCandSelected]
            if (text != null) {
                textWidth = buttonTextPaint!!.measureText(text)
                canvas.drawText(text, textPos, 60f, buttonTextPaint!!)
                textPos += (textWidth + 20.0).toFloat()
            }
            i++
            // Message.message("Gyaim","text = " + button.text);
        }
    }

    // よくわからないがこれを設定するとViewの大きさを決められるようだ...
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)

        Log.v("Gyaim", "onMeasure: width = $width height = $height")

        setMeasuredDimension(1200, 100) // KEYOne用にいい加減に設定
        /*
    LayoutParams lp = getLayoutParams();
	MarginLayoutParams mlp = (MarginLayoutParams)lp;
	mlp.setMargins(mlp.leftMargin, 120, mlp.rightMargin, 120);
	//マージンを設定
	setLayoutParams(mlp);
	*/

        // self.layout()

        //Log.v("Gyaim","onMeasure = width = "+widthMeasureSpec+" height="+heightMeasureSpec);
        //int specMode = MeasureSpec.getMode(widthMeasureSpec);
        //int specSize = MeasureSpec.getSize(widthMeasureSpec);
        //Log.v("Gyaim","modeandsize = "+specMode+", "+specSize);
        //specMode = MeasureSpec.getMode(heightMeasureSpec);
        //specSize = MeasureSpec.getSize(heightMeasureSpec);
        //Log.v("Gyaim","modeandsize = "+specMode+", "+specSize);

        // Android.manifestで以下のような記述をしておけば勝手にスケールしてくれる
        // http://y-anz-m.blogspot.com/2010/02/andro
        // SDK version 4以降でこれが必要らしい
        //    <supports-screens
        //       android:smallScreens="true"
        //       android:normalScreens="true"
        //       android:largeScreens="true"
        //       android:anyDensity="false" />
        // ... と思ったがうまくスケールしてくれないのできちんと倍率を計算して処理する

        // Nexus5のとき、何故かheightに変な値が返ってきてしまう。
        // height=585, width=1080 になってしまったりすることがあるので
        // とりあえず width > height で orientation を判定するのをやめる。
        // これにより landscape だとSlimeが画面一杯になってしまうのだが...
        //
        /*
    Log.v("Gyaim","onMeasure: width = " +width + " height = " + height);
	int imeWidth = width;
	//	if(width > height) imeWidth = height;
	expand = (float)imeWidth / (float)320.0;
        setMeasuredDimension((int)(320 * expand),(int)(216 * expand));
	*/

        initGraphics()

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

