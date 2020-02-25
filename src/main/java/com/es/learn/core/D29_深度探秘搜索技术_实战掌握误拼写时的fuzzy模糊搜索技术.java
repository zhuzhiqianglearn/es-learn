package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FieldValueFactorFunctionBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class D29_深度探秘搜索技术_实战掌握误拼写时的fuzzy模糊搜索技术 {
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
        FuzzyQueryBuilder fuzziness = QueryBuilders.fuzzyQuery("text", "surprize").fuzziness(Fuzziness.TWO);

        SearchResponse searchResponse = client.prepareSearch("my_index")
                .setTypes("my_type")
                .setQuery(
                     fuzziness
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

