package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D09_深度探秘搜索技术_基于boost的细粒度搜索条件权重控制 {
    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        query(client);
        client.close();
    }
    public static void query(TransportClient client){
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "blog");
        MatchQueryBuilder boostHadoop = QueryBuilders.matchQuery("title", "java").boost(2.0f);
        MatchQueryBuilder boostElasticsearch = QueryBuilders.matchQuery("title", "elasticsearch").boost(1.0f);
        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                .setQuery(QueryBuilders.boolQuery()
                                                  .must(matchQueryBuilder)
                                                  .should(boostHadoop)
                                                  .should(boostElasticsearch)
                         )
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString()+"-----------"+hit.getScore());
        }


    }
  /*  GET /forum/article/_search
    {
        "query": {
        "constant_score": {
            "filter": {
                "term": {
                    "articleID.keyword":"QQPX-R-3956-#aD8"
                }
            }
        }
    }
    }*/
}

