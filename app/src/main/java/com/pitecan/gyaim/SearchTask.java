package com.pitecan.gyaim;

import android.os.AsyncTask;
import android.util.Log;

//                                       <Params, Progress, Result> で型を指定する
public class SearchTask extends AsyncTask<String, Integer, Candidate[]> {

    private CandView candView;

    public SearchTask(CandView candView) {
        this.candView = candView;
        Message.message("Gyaim", "SearchTask: candView = " + candView);
    }

    public void progress(int i) {
        publishProgress(0);
    }

    //
    // SearchTask.execute() でこれが呼ばれる
    //
    protected Candidate[] doInBackground(String... searchParams) { // Result の型を返す 引数はParamsの型
        Message.message("Gyaim", "doInBackground start......");

        Candidate[] res;
        String pat = searchParams[0];
        //String word = searchParams[1];
        //if(pat != ""){
        if (!pat.equals("")) {
            res = Search.search(pat, this); // this.cancel()が呼ばれるとthis.isCancelled()がtrueになる
            if (isCancelled()) {
            }
        } else {
            Search.ncands = 0;
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

            res = Search.candidates;
        }

        Message.message("Gyaim", "doInBackground end");
        return res;
    }

    private void updateView() {
        // Message.message("Gyaim","Search.ncands = " + Search.ncands);
        int i = 0;
        if (Search.ncands > 0) {
            for (i = 0; i < CandView.candButtons.length && i < Search.ncands; i++) {
                CandView.candButtons[i].text = Search.candidates[i].word;
                CandView.candButtons[i].pat = Search.candidates[i].pat;
            }
        }
        for (; i < CandView.candButtons.length; i++) {
            CandView.candButtons[i].text = "";
            CandView.candButtons[i].pat = "";
        }
        candView.drawDefault();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) { // Progressの型
        // setProgressPercent(progress[0]);
        //
        // 検索ルーチンで候補がみつかったとき適宜publishProgress()を呼ぶことにより候補表示を速くする。
        // publishProgress()が呼ばれるとこのスレッドのonProgressUpdate()が呼ばれる。
        //
        updateView();
    }

    @Override
    protected void onPostExecute(Candidate[] candidates) { // Result の型の値が引数に入る doInBackgroundの返り値
        Log.v("Gyaim", "onPostExecute");
        // ここで候補を表示する
        updateView();
    }

    @Override
    protected void onCancelled(Candidate[] candidates) {
        Log.v("Gyaim", "onCancelled");
    }
}
