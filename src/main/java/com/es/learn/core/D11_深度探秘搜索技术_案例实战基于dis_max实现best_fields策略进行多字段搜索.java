package com.es.learn.core;

import org.apache.lucene.spatial3d.geom.LinearSquaredDistance;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class D11_深度探秘搜索技术_案例实战基于dis_max实现best_fields策略进行多字段搜索 {
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
        QueryBuilder title = QueryBuilders.matchQuery("title", "java solution");
        QueryBuilder content = QueryBuilders.matchQuery("content", "java solution");
        List<QueryBuilder> list=new ArrayList<QueryBuilder>();
        list.add(title);
        list.add(content);
        SearchResponse searchResponse = client.prepareSearch("forum")
                .setTypes("article")
                .setQuery(QueryBuilders.disMaxQuery().add(title).add(content))
                .get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getId()+"----"+hit.getSourceAsString());
        }
    }
  /*
  GET /forum/article/_search
{
  "query": {
    "dis_max": {
      "queries": [
        {
          "match": {
            "title": "java solution"
          }
        },
        {"match": {
            "content": "java solution"
        }}
      ]

    }
  }
}
  *
  * */
}

