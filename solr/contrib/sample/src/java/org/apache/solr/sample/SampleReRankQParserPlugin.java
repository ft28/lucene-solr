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
package org.apache.solr.search;

import java.io.IOException;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import org.apache.lucene.search.SampleRescorer;

import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;

/*
*
*  Syntax: q=*:*&rq={!sample reRankDocs=300 reRankModulo=3}
* 
*/

public class SampleReRankQParserPlugin extends QParserPlugin {

  public static final String NAME = "sample";
  private static Query defaultQuery = new MatchAllDocsQuery();

  public static final String RERANK_DOCS = "reRankDocs";
  public static final int RERANK_DOCS_DEFAULT = 200;
  public static final String RERANK_MODULO = "reRankModulo";
  public static final int RERANK_MODULO_DEFAULT = 3;    
    
  public QParser createParser(String query, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    return new SampleReRankQParser(query, localParams, params, req);
  }

  private class SampleReRankQParser extends QParser  {

    public SampleReRankQParser(String query, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
      super(query, localParams, params, req);
    }

    public Query parse() throws SyntaxError {
      int reRankDocs  = localParams.getInt(RERANK_DOCS, RERANK_DOCS_DEFAULT);
      reRankDocs = Math.max(1, reRankDocs);

      int reRankModulo = localParams.getInt(RERANK_MODULO, RERANK_MODULO_DEFAULT);
      reRankModulo = Math.max(1, reRankModulo);
      return new SampleReRankQuery(defaultQuery, reRankDocs, reRankModulo);
    }
  }

  private final class SampleReRankQuery extends AbstractReRankQuery {
    final private int reRankModulo;

    public int hashCode() {
      return 31 * classHash() + mainQuery.hashCode() + reRankDocs + reRankModulo;
    }

    public boolean equals(Object other) {
      return sameClassAs(other) &&
             equalsTo(getClass().cast(other));
    }

    private boolean equalsTo(SampleReRankQuery rrq) {
      return mainQuery.equals(rrq.mainQuery) &&
        reRankDocs == rrq.reRankDocs &&
        reRankModulo == rrq.reRankModulo;     
    }

    public SampleReRankQuery(Query query , int reRankDocs, int reRankModulo) {
      super(query, reRankDocs, new SampleRescorer(reRankModulo));
      this.reRankModulo = reRankModulo;
    }

    @Override
    public String toString(String s) {
      final StringBuilder sb = new StringBuilder(100); // default initialCapacity of 16 won't be enough
      sb.append("{!").append(NAME);
      sb.append(" mainQuery='").append(mainQuery.toString()).append("' ");
      sb.append(RERANK_DOCS).append('=').append(reRankDocs).append(' ');
      sb.append(RERANK_MODULO).append('=').append(reRankModulo).append(' ');      
      return sb.toString();
    }

    protected Query rewrite(Query rewrittenMainQuery) throws IOException {
      return new SampleReRankQuery(defaultQuery, reRankDocs, reRankModulo).wrap(rewrittenMainQuery);
    }
  }
}
