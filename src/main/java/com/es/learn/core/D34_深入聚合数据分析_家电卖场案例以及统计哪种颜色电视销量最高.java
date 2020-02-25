package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class D34_深入聚合数据分析_家电卖场案例以及统计哪种颜色电视销量最高 {
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

        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
               .addAggregation(AggregationBuilders.terms("populer_color").field("price"))
                .get();
        Map<String, Aggregation> stringAggregationMap =
                searchResponse.getAggregations().asMap();
        for (String s : stringAggregationMap.keySet()) {
            /*
            StringTerms:所取字段是 字符型
            LongTerms：所取字段是数字
            DoubleTerms：所取字段是小数
             */
            LongTerms aggregation = (LongTerms) stringAggregationMap.get(s);
            Iterator<Terms.Bucket> iterator = aggregation.getBuckets().iterator();
            while (iterator.hasNext()){
                Terms.Bucket next = iterator.next();
                System.out.println(next.getKey()+"---------"+next.getDocCount());

            }
        }

    }
  /*
 GET /tvs/sales/_search
 {
   "size": 0,
   "aggs": {
     "populer_color": {
       "terms": {
         "field": "color"
       }
     }
   }
 }
  * */
}

