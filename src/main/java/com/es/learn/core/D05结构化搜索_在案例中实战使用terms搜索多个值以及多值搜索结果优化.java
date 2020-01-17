package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D05结构化搜索_在案例中实战使用terms搜索多个值以及多值搜索结果优化 {
    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//         filter(client);
//         filter2(client);
         filter3(client);
         client.close();
    }

    /*搜索articleID为KDKE-B-9947-#kL5或QQPX-R-3956-#aD8的帖子
    GET /forum/article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "terms": {
          "articleID.keyword": [
            "KDKE-B-9947-#kL5",
            "QQPX-R-3956-#aD8"
          ]
        }
      }
    }
  }
}
     */
    public static void filter(TransportClient client){
        TermsQueryBuilder articleID = QueryBuilders.termsQuery("articleID.keyword", "KDKE-B-9947-#kL5", "QQPX-R-3956-#aD8");
        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                .setQuery(QueryBuilders.constantScoreQuery(articleID))
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }
  /*
   搜索tag中包含java的帖子
   GET /forum/article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "terms": {
          "tag": [
            "java"
          ]
        }
      }
    }
  }
}
    */
  public static void filter2(TransportClient client){
      TermsQueryBuilder tag = QueryBuilders.termsQuery("tag", "java");
      SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
              .setQuery(QueryBuilders.constantScoreQuery(tag))
              .get();
      SearchHit[] hits = searchResponse.getHits().getHits();
      for (SearchHit hit : hits) {
          System.out.println(hit.getSourceAsString());
      }
  }

  /*
  3、优化搜索结果，仅仅搜索tag只包含java的帖子
  GET /forum/article/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "tag_cnt": {
              "value":1
            }
          }
        },
        {
          "terms": {
            "tag": [
              "java"
            ]
          }
        }

      ]

    }
  }
}
   */

    public static void filter3(TransportClient client){
        TermsQueryBuilder tag = QueryBuilders.termsQuery("tag", "java");
        TermQueryBuilder tag_cnt = QueryBuilders.termQuery("tag_cnt", 1);
        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                .setQuery(QueryBuilders.constantScoreQuery(QueryBuilders.boolQuery().must(tag).must(tag_cnt)))
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }
}
