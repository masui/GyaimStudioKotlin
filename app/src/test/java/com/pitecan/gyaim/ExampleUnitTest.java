package com.pitecan.gyaim;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
/*
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}
*/

public class ExampleUnitTest {
    String testdata[][] = {
            {"masui", "ますい", "マスイ"},
            {"dhisukushisutemu", "でぃすくしすてむ", "ディスクシステム"},
            {"vaiorin", "う゛ぁいおりん", "ヴァイオリン"},
            {"hannnya", "はんにゃ", "ハンニャ"}
    };

    @Test
    public void ローマ字変換テスト() {
        for (String[] data : testdata) {
            assertTrue(Romakana.roma2hiragana(data[0]).equals(data[1]));
            assertTrue(Romakana.roma2katakana(data[0]).equals(data[2]));
        }
    }
}
