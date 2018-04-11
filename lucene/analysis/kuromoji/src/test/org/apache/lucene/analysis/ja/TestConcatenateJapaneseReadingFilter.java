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

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.analysis.Tokenizer;

/**
 * Simple tests to ensure the ConcatenateJapaneseReading filter factory works.
 */
public class TestConcatenateJapaneseReadingFilter extends BaseTokenStreamTestCase {

  public void testRoman1() throws Exception {    
    JapaneseTokenizerFactory tokenizerFactory = new JapaneseTokenizerFactory(new HashMap<String,String>());
    tokenizerFactory.inform(new StringMockResourceLoader(""));
    Tokenizer ts = tokenizerFactory.create(newAttributeFactory());
    ts.setReader(new StringReader("今日は シーチキンです。"));

    TokenStream stream = new ConcatenateJapaneseReadingFilter(ts, 30, 1);
    assertTokenStreamContents(stream,  new String[] {"kyouha shichikindesu"});
  }

  public void testRoman2() throws Exception {    
    JapaneseTokenizerFactory tokenizerFactory = new JapaneseTokenizerFactory(new HashMap<String,String>());
    tokenizerFactory.inform(new StringMockResourceLoader(""));
    Tokenizer ts = tokenizerFactory.create(newAttributeFactory());
    ts.setReader(new StringReader("今日は シーチキンです。"));

    TokenStream stream = new ConcatenateJapaneseReadingFilter(ts, 30, 2);
    assertTokenStreamContents(stream,  new String[] {"kyouha shichikindesu", "kyouha sitikindesu"});
  }
}
