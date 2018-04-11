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


import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

/** 
 * Factory for {@link ConcatenateJapaneseReadingFilter}.
 * <pre class="prettyprint">
 * &lt;fieldType name="text_concatenate" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;
 *     &lt;filter class="solr.ConcatenateJapaneseReadingFilterFactory" maxNumTokens="10" mode="1" /&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 */
public class ConcatenateJapaneseReadingFilterFactory extends TokenFilterFactory {
  private static final String PARAM_MAX_NUM_TOKENS = "maxNumTokens";
  private static final String PARAM_MODE = "mode";

  private int maxNumTokens;
  private int mode;

  public ConcatenateJapaneseReadingFilterFactory(Map<String, String> args) {
    super(args);
    maxNumTokens = getInt(args, PARAM_MAX_NUM_TOKENS, ConcatenateJapaneseReadingFilter.DEFAULT_MAX_OUTPUT_TOKEN_SIZE);
    mode = getInt(args, PARAM_MODE, ConcatenateJapaneseReadingFilter.DEFAULT_MODE);

    if (maxNumTokens < 1) {
        throw new IllegalArgumentException("Invalid maxNumTokens (" + maxNumTokens + ") - must be at least 1");
    }

    if (   (mode != ConcatenateJapaneseReadingFilter.MODE_OUTPUT_ROMA)
        && (mode != ConcatenateJapaneseReadingFilter.MODE_OUTPUT_ALL)) {
        throw new IllegalArgumentException("Invalid mode (" + mode + ")");
    }
  }

  @Override
  public ConcatenateJapaneseReadingFilter create(TokenStream input) {
    return new ConcatenateJapaneseReadingFilter(input, maxNumTokens, mode);
  }
}
