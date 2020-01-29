package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D06__结构化搜索_在案例中实战基于range_filter来进行范围过滤 {
    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
         filter(client);
         client.close();
    }

  /*  搜索发帖日期在最近1个月的帖子

    POST /forum/article/_bulk
    { "index": { "_id": 5 }}
    { "articleID" : "DHJK-B-1395-#Ky5", "userID" : 3, "hidden": false, "postDate": "2017-03-01", "tag": ["elasticsearch"], "tag_cnt": 1, "view_cnt": 10 }

    GET /forum/article/_search
    {
        "query": {
        "constant_score": {
            "filter": {
                "range": {
                    "postDate": {
                        "gt": "2017-03-10||-30d"
                    }
                }
            }
        }
    }
    }*/

    public static void filter(TransportClient client){

        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
//                .setQuery(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("articleID.keyword", "QQPX-R-3956-#aD8")))
                .setQuery(QueryBuilders.constantScoreQuery(QueryBuilders.rangeQuery("postDate").gt("2017-03-10||-30d")))
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }


    }

}
