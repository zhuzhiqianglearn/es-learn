package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FieldValueFactorFunctionBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D28_深度探秘搜索技术_实战用function_score自定义相关度分数算法 {
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
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("java spark", "title", "content");

        FieldValueFactorFunctionBuilder fieldQuery = new FieldValueFactorFunctionBuilder(
                "follower_num");
        fieldQuery.modifier(FieldValueFactorFunction.Modifier.LOG1P).factor(0.5f);

        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(multiMatchQueryBuilder,fieldQuery);
        SearchResponse searchResponse = client.prepareSearch("forum")
                .setTypes("article")
                .setQuery(
                        functionScoreQueryBuilder.maxBoost(1.5f).boostMode(CombineFunction.SUM)
                )
                .get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getScore()+"--------"+hit.getId()+"----"+hit.getSourceAsString());
        }
    }
  /*

 GET /forum/article/_search
 {
   "query": {
     "function_score": {
       "query": {
         "multi_match": {
           "query": "java spark",
           "fields": ["title","content"]
         }
       },
       "field_value_factor": {
         "field": "follower_num",
          "modifier": "log1p",
          "factor": 0.5
       },
       "boost_mode": "sum",
       "max_boost": 0.2
     }
   }
 }
  * */
}

