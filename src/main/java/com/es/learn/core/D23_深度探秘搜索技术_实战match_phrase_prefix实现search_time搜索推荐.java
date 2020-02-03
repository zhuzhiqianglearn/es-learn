package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D23_深度探秘搜索技术_实战match_phrase_prefix实现search_time搜索推荐 {
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
                .setQuery(QueryBuilders.matchPhrasePrefixQuery("title","is s").slop(4).maxExpansions(50))
                .get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getId()+"----"+hit.getSourceAsString());
        }
    }
  /*
GET /myindex/mytype/_search
{
  "query": {
    "prefix": {
      "title": {
        "value": "C3"
      }
    }
  }
}

GET /myindex/mytype/_search
{
  "query": {
    "wildcard": {
      "title": {
        "value": "C??5*5"
      }
    }
  }
}

GET /myindex/mytype/_search
{
  "query": {
    "regexp": {
      "title": {
        "value": "[A-Z].+"
      }
    }
  }
}

  *
  * */
}

