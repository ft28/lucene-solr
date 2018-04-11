/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.analysis.ja.util;


import java.io.IOException;
import java.util.HashMap;

/**
 * Utility class for english translations of morphological data,
 * used only for debugging.
 */
public class ToStringUtil2 extends ToStringUtil {
  private static final char KATA_a = 'ァ';
  private static final char KATA_N = 'ン';
  private static final char HIRA_a = 'ぁ';
  private static final char HIRA_N = 'ん';

  public static String toKatakana(CharSequence s) {
    StringBuilder sb = new StringBuilder(s);
    final int len = s.length();
    for (int i=0; i<len; i++) {
        char word = sb.charAt(i);
        if (word >= HIRA_a && word <= HIRA_N) {
            sb.setCharAt(i, (char) (word - HIRA_a + KATA_a));
        }
    }
    return sb.toString();
  }

  public static void getRomanization(Appendable builder, CharSequence s, boolean flagSub) throws IOException {
    final int len = s.length();
     
    for (int i = 0; i < len; i++) {
      // maximum lookahead: 3
      char ch = s.charAt(i);
      char ch2 = (i < len - 1) ? s.charAt(i + 1) : 0;
      char ch3 = (i < len - 2) ? s.charAt(i + 2) : 0;
      
      main: switch (ch) {
        case 'ッ':
          switch (ch2) {
            case 'カ':
            case 'キ':
            case 'ク':
            case 'ケ':
            case 'コ':
              builder.append('k');
              break main;
            case 'サ':
            case 'シ':
            case 'ス':
            case 'セ':
            case 'ソ':
              builder.append('s');
              break main;
            case 'タ':
            case 'チ':
            case 'ツ':
            case 'テ':
            case 'ト':
              builder.append('t');
              break main;
            case 'パ':
            case 'ピ':
            case 'プ':
            case 'ペ':
            case 'ポ':
              builder.append('p');
              break main;
          }
          break;
        case 'ア':
          builder.append('a');
          break;
        case 'イ':
          if (ch2 == 'ィ') {
            builder.append("yi");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("ye");
            i++;
          } else {
            builder.append('i');
          }
          break;
        case 'ウ':
          switch(ch2) {
            case 'ァ':
              builder.append("wa");
              i++;
              break;
            case 'ィ':
              builder.append("wi");
              i++;
              break;
            case 'ゥ':
              builder.append("wu");
              i++;
              break;
            case 'ェ':
              builder.append("we");
              i++;
              break;
            case 'ォ':
              builder.append("wo");
              i++;
              break;
            case 'ュ':
              builder.append("wyu");
              i++;
              break;
            default:
              builder.append('u');
              break;
          }
          break;
        case 'エ':
          builder.append('e');
          break;
        case 'オ':
          if (ch2 == 'ウ') {
            builder.append("ou");
            i++;
          } else {
            builder.append('o');
          }
          break;
        case 'カ':
          builder.append("ka");
          break;
        case 'キ':
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("kyou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("kyuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("kya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("kyo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("kyu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("kye");
            i++;
          } else {
            builder.append("ki");
          }
          break;
        case 'ク':
          switch(ch2) {
            case 'ァ':
              builder.append("kwa");
              i++;
              break;
            case 'ィ':
              builder.append("kwi");
              i++;
              break;
            case 'ェ':
              builder.append("kwe");
              i++;
              break;
            case 'ォ':
              builder.append("kwo");
              i++;
              break;
            case 'ヮ':
              builder.append("kwa");
              i++;
              break;
            default:
              builder.append("ku");
              break;
          }
          break;
        case 'ケ':
          builder.append("ke");
          break;
        case 'コ':
          if (ch2 == 'ウ') {
            builder.append("kou");
            i++;
          } else {
            builder.append("ko");
          }
          break;
        case 'サ':
          builder.append("sa");
          break;
        case 'シ':
          if (flagSub) {
            if (ch2 == 'ョ' && ch3 == 'ウ') {
              builder.append("syou");
              i += 2;
            } else if (ch2 == 'ュ' && ch3 == 'ウ') {
              builder.append("syuu");
              i += 2;
            } else if (ch2 == 'ャ') {
              builder.append("sya");
              i++;
            } else if (ch2 == 'ョ') {
              builder.append("syo");
              i++;
            } else if (ch2 == 'ュ') {
              builder.append("syu");
              i++;
            } else if (ch2 == 'ェ') {
              builder.append("sye");
              i++;
            } else {
              builder.append("si");
            }
          } else {
            if (ch2 == 'ョ' && ch3 == 'ウ') {
              builder.append("shou");
              i += 2;
            } else if (ch2 == 'ュ' && ch3 == 'ウ') {
              builder.append("shuu");
              i += 2;
            } else if (ch2 == 'ャ') {
              builder.append("sha");
              i++;
            } else if (ch2 == 'ョ') {
              builder.append("sho");
              i++;
            } else if (ch2 == 'ュ') {
              builder.append("shu");
              i++;
            } else if (ch2 == 'ェ') {
              builder.append("she");
              i++;
            } else {
              builder.append("shi");
            }
          }
          break;
        case 'ス':
          if (ch2 == 'ィ') {
            builder.append("si");
            i++;
          } else {
            builder.append("su");
          }
          break;
        case 'セ':
          builder.append("se");
          break;
        case 'ソ':
          if (ch2 == 'ウ') {
            builder.append("sou");
            i++;
          } else {
            builder.append("so");
          }
          break;
        case 'タ':
          builder.append("ta");
          break;
        case 'チ':
          if (flagSub) {
            if (ch2 == 'ョ' && ch3 == 'ウ') {
              builder.append("tyou");
              i += 2;
            } else if (ch2 == 'ュ' && ch3 == 'ウ') {
              builder.append("tyuu");
              i += 2;
            } else if (ch2 == 'ャ') {
              builder.append("tya");
              i++;
            } else if (ch2 == 'ョ') {
              builder.append("tyo");
              i++;
            } else if (ch2 == 'ュ') {
              builder.append("tyu");
              i++;
            } else if (ch2 == 'ェ') {
              builder.append("tye");
              i++;
            } else {
              builder.append("ti");
            }
          } else {
            if (ch2 == 'ョ' && ch3 == 'ウ') {
              builder.append("chou");
              i += 2;
            } else if (ch2 == 'ュ' && ch3 == 'ウ') {
              builder.append("chuu");
              i += 2;
            } else if (ch2 == 'ャ') {
              builder.append("cha");
              i++;
            } else if (ch2 == 'ョ') {
              builder.append("cho");
              i++;
            } else if (ch2 == 'ュ') {
              builder.append("chu");
              i++;
            } else if (ch2 == 'ェ') {
              builder.append("che");
              i++;
            } else {
              builder.append("chi");
            }
          }
          break;
        case 'ツ':
          if (ch2 == 'ァ') {
            builder.append("tsa");
            i++;
          } else if (ch2 == 'ィ') {
            builder.append("tsi");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("tse");
            i++;
          } else if (ch2 == 'ォ') {
            builder.append("tso");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("tsyu");
            i++;
          } else {
              if (flagSub) {
                  builder.append("tu");
              } else {
                  builder.append("tsu");
              }
          }
          break;
        case 'テ':
          if (ch2 == 'ィ') {
            builder.append("ti");
            i++;
          } else if (ch2 == 'ゥ') {
            builder.append("tu");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("tyu");
            i++;
          } else {
            builder.append("te");
          }
          break;
        case 'ト':
          if (ch2 == 'ウ') {
            builder.append("tou");
            i++;
          } else if (ch2 == 'ゥ') {
            builder.append("tu");
            i++;
          } else {
            builder.append("to");
          }
          break;
        case 'ナ':
          builder.append("na");
          break;
        case 'ニ':
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("nyou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("nyuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("nya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("nyo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("nyu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("nye");
            i++;
          } else {
            builder.append("ni");
          }
          break;
        case 'ヌ':
          builder.append("nu");
          break;
        case 'ネ':
          builder.append("ne");
          break;
        case 'ノ':
          if (ch2 == 'ウ') {
            builder.append("nou");
            i++;
          } else {
            builder.append("no");
          }
          break;
        case 'ハ':
          builder.append("ha");
          break;
        case 'ヒ':
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("hyou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("hyuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("hya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("hyo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("hyu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("hye");
            i++;
          } else {
            builder.append("hi");
          }
          break;
        case 'フ':
          if (ch2 == 'ャ') {
            builder.append("fya");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("fyu");
            i++;
          } else if (ch2 == 'ィ' && ch3 == 'ェ') {
            builder.append("fye");
            i+=2;
          } else if (ch2 == 'ョ') {
            builder.append("fyo");
            i++;
          } else if (ch2 == 'ァ') {
            builder.append("fa");
            i++;
          } else if (ch2 == 'ィ') {
            builder.append("fi");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("fe");
            i++;
          } else if (ch2 == 'ォ') {
            builder.append("fo");
            i++;
          } else {
              if (flagSub) {
                  builder.append("hu");
              } else {
                  builder.append("fu");
              }
          }
          break;
        case 'ヘ':
          builder.append("he");
          break;
        case 'ホ':
          if (ch2 == 'ウ') {
            builder.append("hou");
            i++;
          } else if (ch2 == 'ゥ') {
            builder.append("hu");
            i++;
          } else {
            builder.append("ho");
          }
          break;
        case 'マ':
          builder.append("ma");
          break;
        case 'ミ':
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("myou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("myuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("mya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("myo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("myu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("mye");
            i++;
          } else {
            builder.append("mi");
          }
          break;
        case 'ム':
          builder.append("mu");
          break;
        case 'メ':
          builder.append("me");
          break;
        case 'モ':
          if (ch2 == 'ウ') {
            builder.append("mou");
            i++;
          } else {
            builder.append("mo");
          }
          break;
        case 'ヤ':
          builder.append("ya");
          break;
        case 'ユ':
          builder.append("yu");
          break;
        case 'ヨ':
          if (ch2 == 'ウ') {
            builder.append("you");
            i++;
          } else {
            builder.append("yo");
          }
          break;
        case 'ラ':
          if (ch2 == '゜') {
            builder.append("la");
            i++;
          } else {
            builder.append("ra");
          }
          break;
        case 'リ':
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("ryou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("ryuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("rya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("ryo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("ryu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("rye");
            i++;
          } else if (ch2 == '゜') {
            builder.append("li");
            i++;
          } else {
            builder.append("ri");
          }
          break;
        case 'ル':
          if (ch2 == '゜') {
            builder.append("lu");
            i++;
          } else {
            builder.append("ru");
          }
          break;
        case 'レ':
          if (ch2 == '゜') {
            builder.append("le");
            i++;
          } else {
            builder.append("re");
          }
          break;
        case 'ロ':
          if (ch2 == 'ウ') {
            builder.append("rou");
            i++;
          } else if (ch2 == '゜') {
            builder.append("lo");
            i++;
          } else {
            builder.append("ro");
          }
          break;
        case 'ワ':
          builder.append("wa");
          break;
        case 'ヰ':
          builder.append("i");
          break;
        case 'ヱ':
          builder.append("e");
          break;
        case 'ヲ':
          builder.append("o");
          break;
        case 'ン':
          builder.append("n");
          break main;
        case 'ガ':
          builder.append("ga");
          break;
        case 'ギ':
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("gyou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("gyuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("gya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("gyo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("gyu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("gye");
            i++;
          } else {
            builder.append("gi");
          }
          break;
        case 'グ':
          switch(ch2) {
            case 'ァ':
              builder.append("gwa");
              i++;
              break;
            case 'ィ':
              builder.append("gwi");
              i++;
              break;
            case 'ェ':
              builder.append("gwe");
              i++;
              break;
            case 'ォ':
              builder.append("gwo");
              i++;
              break;
            case 'ヮ':
              builder.append("gwa");
              i++;
              break;
            default:
              builder.append("gu");
              break;
          }
          break;
        case 'ゲ':
          builder.append("ge");
          break;
        case 'ゴ':
          if (ch2 == 'ウ') {
            builder.append("gou");
            i++;
          } else {
            builder.append("go");
          }
          break;
        case 'ザ':
          builder.append("za");
          break;
        case 'ジ':
          if (flagSub) {
            if (ch2 == 'ョ' && ch3 == 'ウ') {
              builder.append("zyou");
              i += 2;
            } else if (ch2 == 'ュ' && ch3 == 'ウ') {
              builder.append("zyou");
              i += 2;
            } else if (ch2 == 'ャ') {
              builder.append("zya");
              i++;
            } else if (ch2 == 'ョ') {
              builder.append("zyo");
              i++;
            } else if (ch2 == 'ュ') {
              builder.append("zyu");
              i++;
            } else if (ch2 == 'ェ') {
              builder.append("zye");
              i++;
            } else {
              builder.append("zi");
            }
          } else {
            if (ch2 == 'ョ' && ch3 == 'ウ') {
              builder.append("jou");
              i += 2;
            } else if (ch2 == 'ュ' && ch3 == 'ウ') {
              builder.append("jou");
              i += 2;
            } else if (ch2 == 'ャ') {
              builder.append("ja");
              i++;
            } else if (ch2 == 'ョ') {
              builder.append("jo");
              i++;
            } else if (ch2 == 'ュ') {
              builder.append("ju");
              i++;
            } else if (ch2 == 'ェ') {
              builder.append("je");
              i++;
            } else {
              builder.append("ji");
            }
          }
          break;
        case 'ズ':
          if (ch2 == 'ィ') {
            builder.append("zi");
            i++;
          } else {
            builder.append("zu");
          }
          break;
        case 'ゼ':
          builder.append("ze");
          break;
        case 'ゾ':
          if (ch2 == 'ウ') {
            builder.append("zou");
            i++;
          } else {
            builder.append("zo");
          }
          break;
        case 'ダ':
          builder.append("da");
          break;
        case 'ヂ':
          // TODO: investigate all this
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("dyou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("dyuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("dya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("dyo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("dyu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("dye");
            i++;
          } else {
            builder.append("di");
          }
          break;
        case 'ヅ':
          builder.append("du");
          break;
        case 'デ':
          if (ch2 == 'ィ') {
            builder.append("di");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("dyu");
            i++;
          } else {
            builder.append("de");
          }
          break;
        case 'ド':
          if (ch2 == 'ウ') {
            builder.append("dou");
            i++;
          } else if (ch2 == 'ゥ') {
            builder.append("du");
            i++;
          } else {
            builder.append("do");
          }
          break;
        case 'バ':
          builder.append("ba");
          break;
        case 'ビ':
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("byou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("byuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("bya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("byo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("byu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("bye");
            i++;
          } else {
            builder.append("bi");
          }
          break;
        case 'ブ':
          builder.append("bu");
          break;
        case 'ベ':
          builder.append("be");
          break;
        case 'ボ':
          if (ch2 == 'ウ') {
            builder.append("bou");
            i++;
          } else {
            builder.append("bo");
          }
          break;
        case 'パ':
          builder.append("pa");
          break;
        case 'ピ':
          if (ch2 == 'ョ' && ch3 == 'ウ') {
            builder.append("pyou");
            i += 2;
          } else if (ch2 == 'ュ' && ch3 == 'ウ') {
            builder.append("pyuu");
            i += 2;
          } else if (ch2 == 'ャ') {
            builder.append("pya");
            i++;
          } else if (ch2 == 'ョ') {
            builder.append("pyo");
            i++;
          } else if (ch2 == 'ュ') {
            builder.append("pyu");
            i++;
          } else if (ch2 == 'ェ') {
            builder.append("pye");
            i++;
          } else {
            builder.append("pi");
          }
          break;
        case 'プ':
          builder.append("pu");
          break;
        case 'ペ':
          builder.append("pe");
          break;
        case 'ポ':
          if (ch2 == 'ウ') {
            builder.append("pou");
            i++;
          } else {
            builder.append("po");
          }
          break;
        case 'ヷ':
          builder.append("va");
          break;
        case 'ヸ':
          builder.append("vi");
          break;
        case 'ヹ':
          builder.append("ve");
          break;
        case 'ヺ':
          builder.append("wo");
          break;
        case 'ヴ':
          builder.append("vu");
          break;
        case 'ァ':
          builder.append("xa");
          break;
        case 'ィ':
          builder.append("xi");
          break;
        case 'ゥ':
          builder.append("xu");
          break;
        case 'ェ':
          builder.append("xe");
          break;
        case 'ォ':
          builder.append("xo");
          break;
        case 'ヮ':
          builder.append("xwa");
          break;
        case 'ャ':
          builder.append("xya");
          break;
        case 'ュ':
          builder.append("xyu");
          break;
        case 'ョ':
          builder.append("xyo");
          break;
        case 'ー':
          break;
        default:
          builder.append(ch);
      }
    }
  }
}
