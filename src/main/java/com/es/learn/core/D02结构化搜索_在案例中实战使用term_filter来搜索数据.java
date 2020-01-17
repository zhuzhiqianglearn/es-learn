package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class D02结构化搜索_在案例中实战使用term_filter来搜索数据 {
    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
         filter(client);
         client.close();
    }
    public static void filter(TransportClient client){

        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
//                .setQuery(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("articleID.keyword", "QQPX-R-3956-#aD8")))
                .setQuery(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("articleID", "QQPX-R-3956-#aD8")))
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
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
