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
package org.apache.lucene.search;


import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;

import org.apache.lucene.search.SampleRescorer;

/** A {@link SampleRescorer}
 */
public class SampleRescorer extends Rescorer {
  private int modulo;
    
  public SampleRescorer(int modulo) {
      this.modulo = modulo;
  }

  @Override
  public TopDocs rescore(IndexSearcher searcher, TopDocs firstPassTopDocs, int topN) throws IOException {
    
    //   ReRankCollector のL103で呼び出される関数
    final String fieldWeight = "weight";
    final String fieldId = "id";
    final Set<String> fieldAsSet  = new HashSet<String>(Arrays.asList(fieldId, fieldWeight));

    final float maxFirstScore = firstPassTopDocs.getMaxScore();

    // 最初のクエリでヒットしたtopN件だけを再計算対象に設定
    ScoreDoc[] subset = new ScoreDoc[topN];
    System.arraycopy(firstPassTopDocs.scoreDocs, 0, subset, 0, topN);

    // docID 順に並べ替え　　      
    Arrays.sort(subset,
                new Comparator<ScoreDoc>() {
                  @Override
                  public int compare(ScoreDoc a, ScoreDoc b) {
                    return a.doc - b.doc;
                  }
                });

    List<LeafReaderContext> leaves = searcher.getIndexReader().leaves();

    int subsetUpto = 0;
    int readerUpto = -1;
    int endDoc = 0;
    int docBase = 0;

    LeafReaderContext readerContext = null;

    while (subsetUpto < subset.length) {
      ScoreDoc scoreDoc = subset[subsetUpto];
      int docID = scoreDoc.doc;

      while (docID >= endDoc) {
        readerUpto++;
        readerContext = leaves.get(readerUpto);
        endDoc = readerContext.docBase + readerContext.reader().maxDoc();
      }

      int targetDoc = docID - readerContext.docBase;

      // フィルド値からscore となる値を計算
      final Document document = readerContext.reader().document(targetDoc, fieldAsSet);
      IndexableField indexableField = document.getField(fieldId);
      int id = Integer.parseInt(indexableField.stringValue());

      // 条件に合致するときだけスコアを更新
      if ((id % modulo) == 0) {
        indexableField = document.getField(fieldWeight);
        scoreDoc.score = maxFirstScore + indexableField.numericValue().floatValue();
      }
      subsetUpto++;
    }

    Arrays.sort(subset,
                new Comparator<ScoreDoc>() {
                  @Override
                  public int compare(ScoreDoc a, ScoreDoc b) {
                    // Sort by score descending, then docID ascending:
                    if (a.score > b.score) {
                      return -1;
                    } else if (a.score < b.score) {
                      return 1;
                    } else {
                      // This subtraction can't overflow int
                      // because docIDs are >= 0:
                      return a.doc - b.doc;
                    }
                  }
                });

    return new TopDocs(subset.length, subset, subset[0].score);
  }
    
  @Override
  public Explanation explain(IndexSearcher searcher, Explanation firstPassExplanation, int docID) throws IOException {
    TopDocs oneHit = new TopDocs(1, new ScoreDoc[] {new ScoreDoc(docID, firstPassExplanation.getValue())});
    TopDocs hits = rescore(searcher, oneHit, 1);

    Explanation first = Explanation.match(firstPassExplanation.getValue(), "first pass score", firstPassExplanation);
    Explanation second = Explanation.match(hits.scoreDocs[0].score, "second pass score");

    return Explanation.match(hits.scoreDocs[0].score, "combined first and second pass score using " + getClass(), first, second);
  }
}
