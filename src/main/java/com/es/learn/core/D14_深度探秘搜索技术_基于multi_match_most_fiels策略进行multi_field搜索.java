package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D14_深度探秘搜索技术_基于multi_match_most_fiels策略进行multi_field搜索 {
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
        SearchResponse searchResponse = client.prepareSearch("forum")
                .setTypes("article")
                .setQuery(QueryBuilders.multiMatchQuery("learning courses","sub_title","sub_title.std")
                                              .type(MultiMatchQueryBuilder.Type.MOST_FIELDS)
                )
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
    "multi_match": {
      "query": "java solution",
      "fields": ["title","content^2"],
       "type": "best_fields",
       "tie_breaker": 0.3,
       "minimum_should_match": "50%"
    }
  }
}
  *
  * */
}

