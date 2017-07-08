package com.pitecan.gyaim

import android.os.AsyncTask
import android.util.Log

//                                       <Params, Progress, Result> で型を指定する
class SearchTask(internal var candView: CandView) : AsyncTask<String, Int, Array<Candidate?>>() {

    init {
        Message.message("Gyaim", "SearchTask: candView = " + candView)
    }

    fun progress(i: Int) {
        publishProgress(0)
    }

    //
    // SearchTask.execute() でこれが呼ばれる
    //
    override fun doInBackground(vararg searchParams: String): Array<Candidate?> { // Result の型を返す 引数はParamsの型

            Message.message("Gyaim", "doInBackground start......")

        val res: Array<Candidate?>
        val pat = searchParams[0]
        //String word = searchParams[1];
        //if(pat != ""){
        if (pat != "") {
            res = Search.search(pat, this) // this.cancel()が呼ばれるとthis.isCancelled()がtrueになる
            if (isCancelled) {
            }
        } else {
            Search.ncands = 0
            /*
	    // 以下は何故か動かない?
	    Search.addCandidateWithLevel("が","ga",0);
	    Search.addCandidateWithLevel("は","ha",0);
	    Search.addCandidateWithLevel("の","no",0);
	    Search.addCandidateWithLevel("に","ni",0);
	    Search.addCandidateWithLevel("を","wo",0);
	    Search.addCandidateWithLevel("。",".",0);
	    Search.addCandidateWithLevel("、",",",0);
	    */

            res = Search.candidates
        }

        Message.message("Gyaim", "doInBackground end")
        return res
    }

    private fun updateView() {
        // Message.message("Gyaim","Search.ncands = " + Search.ncands);
        var i = 0
        if (Search.ncands > 0) {
            i = 0
            while (i < CandView.candText.size && i < Search.ncands) {
                CandView.candText[i] = Search.candidates[i]!!.word
                CandView.candPat[i] = Search.candidates[i]!!.pat
                i++
            }
        }
        while (i < CandView.candText.size) {
            CandView.candText[i] = ""
            CandView.candPat[i] = ""
            i++
        }
        candView.drawDefault()
    }

    protected fun onProgressUpdate(vararg progress: Int) { // Progressの型
        // setProgressPercent(progress[0]);
        //
        // 検索ルーチンで候補がみつかったとき適宜publishProgress()を呼ぶことにより候補表示を速くする。
        // publishProgress()が呼ばれるとこのスレッドのonProgressUpdate()が呼ばれる。
        //
        updateView()
    }

    override fun onPostExecute(candidates: Array<Candidate?>) { // Result の型の値が引数に入る doInBackgroundの返り値

            Log.v("Gyaim", "onPostExecute")
        // ここで候補を表示する
        updateView()
    }

    override fun onCancelled(candidates: Array<Candidate?>) {
        Log.v("Gyaim", "onCancelled")
    }
}
