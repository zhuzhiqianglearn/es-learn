package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.rescore.RescoreBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D22_深度探秘搜索技术_实战前缀搜索_通配符搜索_正则搜索等技术 {
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

        SearchResponse searchResponse = client.prepareSearch("myindex")
                .setTypes("mytype")
//                .setQuery(QueryBuilders.prefixQuery("title","C3"))
//                .setQuery(QueryBuilders.wildcardQuery("title","C*5*5"))//?：任意字符 *：0个或任意多个字符
                .setQuery(QueryBuilders.regexpQuery("title","[A-Z].+"))
                /*
                [0-9]：指定范围内的数字
                [a-z]：指定范围内的字母
                .：一个字符
                +：前面的正则表达式可以出现一次或多次
                 */
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

