package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D07__深度探秘搜索技术_在案例中体验如何手动控制全文检索结果的精准度 {
    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        matchAnd(client);
        System.out.println("----------------------");
        matchZhiShao(client);
         client.close();
    }

  /*
3、搜索标题中包含java和elasticsearch的blog
    GET /forum/article/_search
{
  "query": {
    "match": {
      "title": {
        "query": "java elasticsearch",
        "operator": "and"
      }
    }
  }
}
    */

    public static void matchAnd(TransportClient client){

        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                .setQuery(QueryBuilders.matchQuery("title","java elasticsearch").operator(Operator.AND)
                )
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /*
    GET /forum/article/_search
{
  "query": {
    "match": {
      "title": {
        "query": "java elasticsearch spark hadoop",
         "minimum_should_match": "50%"
      }
    }
  }
}
     */
    public static void matchZhiShao(TransportClient client){

        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                .setQuery(QueryBuilders.matchQuery("title","java elasticsearch spark hadoop").minimumShouldMatch("75%")
                )
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

}
