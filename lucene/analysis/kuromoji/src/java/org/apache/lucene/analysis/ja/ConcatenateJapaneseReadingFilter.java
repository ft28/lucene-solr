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
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute;
import org.apache.lucene.analysis.ja.util.ToStringUtil2;

import java.io.IOException;

public class ConcatenateJapaneseReadingFilter extends TokenFilter {

  public static final int DEFAULT_MAX_OUTPUT_TOKEN_SIZE = 20;
  public static final int DEFAULT_MODE = 1;

  public static final int MODE_OUTPUT_ROMA = 1;
  public static final int MODE_OUTPUT_ALL  = 2;
    
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
  private final PositionLengthAttribute posLenAtt = addAttribute(PositionLengthAttribute.class);
  private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
  private final ReadingAttribute readingAtt = addAttribute(ReadingAttribute.class);
    
  private LinkedList<String> terms = null;
  private LinkedList<String> romanTerms = null;
  private LinkedList<String> subRomanTerms = null;
    
  private final int maxOutputTokenSize;
  private AttributeSource.State finalState;

  private int mode = 0;

  private boolean flagSubRomanTerms = false;
  private boolean inputEnded = false;
  private int outputOffset = 0;
  Pattern pattern = Pattern.compile("^[a-zA-Z0-9 ]+$");

  /**
   * Create a new ConcatenateJapaneseReadingFilter with default settings
   */
  public ConcatenateJapaneseReadingFilter(TokenStream input) {
      this(input, DEFAULT_MAX_OUTPUT_TOKEN_SIZE, DEFAULT_MODE);
  }

  /**
   * Create a new ConcatenatepaneseFilter with control over all settings
   * 
   * @param input
   *          the source of tokens to be summarized into a single token
   * @param maxOutputTokenSize
   *          the maximum length of the summarized output token. If exceeded, no
   *          output token is emitted
   * @param mode
   *          output token mode
   *
   */
  public ConcatenateJapaneseReadingFilter(TokenStream input, int maxOutputTokenSize, int mode) {
      super(input);
      this.maxOutputTokenSize = maxOutputTokenSize;
      this.mode = mode;
  }

  @Override
  public final boolean incrementToken() throws IOException {
    if (terms != null) {
      restoreState(finalState);
      if((mode >= MODE_OUTPUT_ALL) && (flagSubRomanTerms)) {
          update_terms(subRomanTerms);
          flagSubRomanTerms = false;
          return true;
      } else {
          return false;
      }
    }
    boolean result = buildSingleOutputToken();
    finalState = captureState();
    return result;
  }
  
  private void update_terms(List<String> x) {
     Object[] items = x.toArray();
     StringBuilder sb = new StringBuilder();
     for (Object item : items) {
       sb.append((String) item);
     }
     termAtt.setEmpty().append(sb.toString());
     offsetAtt.setOffset(0, outputOffset);
     posLenAtt.setPositionLength(1);
     posIncrAtt.setPositionIncrement(1);
     typeAtt.setType("concate");
     x.clear();
  }
 
  /**
   * Gathers all tokens from input, de-duplicates, sorts then concatenates.
   * 
   * @return false for end of stream; true otherwise
   */
  private final boolean buildSingleOutputToken() throws IOException {
    inputEnded = false;
    StringBuilder buffer1 = new StringBuilder();
    StringBuilder buffer2 = new StringBuilder();

    terms = new LinkedList<String>();
    romanTerms = new LinkedList<String>();
    subRomanTerms = new LinkedList<String>();

    final String separator = " ";

    int outputTokenSize = 0;
    int lastEndOffset = 0;

    String term = "";
    String romanTerm = "";
    String subRomanTerm = "";

    char clonedLastTerm[] = null;

    while (input.incrementToken()) {
      if (outputTokenSize >= maxOutputTokenSize) {
        break;
      }

      final char char_term[] = termAtt.buffer();
      final int length = termAtt.length();
      final int currentStartOffset = offsetAtt.startOffset();

      clonedLastTerm = new char[length];
      System.arraycopy(char_term, 0, clonedLastTerm, 0, length);
      term = String.valueOf(clonedLastTerm);
      romanTerm = "";
      subRomanTerm = "";

      buffer1.setLength(0);
      buffer2.setLength(0);

      String reading = readingAtt.getReading();
      if (pattern.matcher(term).find()) {
          // termがアルファベットからだけなる時は読みがあっても使わない
          reading = term;
      } else if ((reading == null) || (length > reading.length())) {
          reading = ToStringUtil2.toKatakana(term);
      }

      ToStringUtil2.getRomanization(buffer1, reading, false);
      romanTerm = buffer1.toString();

      if (mode > 1) {
          ToStringUtil2.getRomanization(buffer2, reading, true);
          subRomanTerm = buffer2.toString();

          if (!romanTerm.equals(subRomanTerm)) {
             flagSubRomanTerms = true;
          }
      }

      if (currentStartOffset > lastEndOffset) {
          terms.add(separator);
          romanTerms.add(separator);
          subRomanTerms.add(separator);
          outputTokenSize += 1;
      }

      terms.add(term);
      romanTerms.add(romanTerm);
      subRomanTerms.add(subRomanTerm);
      outputTokenSize++;
      
      lastEndOffset = offsetAtt.endOffset();
    }
    //Force end-of-stream operations to get the final state.
    input.end();
    inputEnded = true;

    outputOffset = lastEndOffset;

    //No tokens gathered - no output
    if (terms.size() < 1) {
      termAtt.setEmpty();
      return false;
    }

    //Tokens gathered are too large - no output
    if (outputTokenSize > maxOutputTokenSize) {
      termAtt.setEmpty();
      terms.clear();
      return false;
    }

    update_terms(romanTerms);
    romanTerms.clear();
    terms.clear();
    
    return true;
  }

  @Override
  public final void end() throws IOException {
    if (!inputEnded) {
      input.end();
      inputEnded = true;
    }

    if (finalState != null) {
      restoreState(finalState);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void reset() throws IOException {
    super.reset();
    inputEnded = false;
    terms = null;
    romanTerms = null;
    subRomanTerms = null;
    flagSubRomanTerms = false;
  }

}
