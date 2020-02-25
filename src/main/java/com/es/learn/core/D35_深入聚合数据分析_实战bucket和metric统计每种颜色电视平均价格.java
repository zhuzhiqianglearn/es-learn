package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

public class D35_深入聚合数据分析_实战bucket和metric统计每种颜色电视平均价格 {
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
        TermsAggregationBuilder color = AggregationBuilders.terms("group_by_color").field("color");
        AvgAggregationBuilder price = AggregationBuilders.avg("avg_price").field("price");
        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
                .setSize(0)
                .addAggregation(color.subAggregation(price))
                .get();
        Map<String, Aggregation> stringAggregationMap =
                searchResponse.getAggregations().asMap();
        for (String s : stringAggregationMap.keySet()) {
            /*
            StringTerms:所取字段是 字符型
            LongTerms：所取字段是数字
            DoubleTerms：所取字段是小数
             */
            StringTerms aggregation = (StringTerms) stringAggregationMap.get(s);
            Iterator<Terms.Bucket> iterator = aggregation.getBuckets().iterator();
            while (iterator.hasNext()){
                Terms.Bucket next = iterator.next();
//                System.out.println(next.getKey()+"---------"+next.getDocCount());
                Map<String, Aggregation> asMap = next.getAggregations().getAsMap();
                for (String s1 : asMap.keySet()) {
                    Avg avg = (Avg) asMap.get(s1);
                    System.out.println(s1);
                    System.out.println(next.getKey()+"--个数："+next.getDocCount()+"   平均价格："+avg.getValue());
                }

            }
        }

    }
  /*
 GET  /tvs/sales/_search
 {
   "size": 0,
   "aggs": {
     "group_by_color": {
       "terms": {
         "field": "color"
       }
       ,
       "aggs": {
         "avg_price": {
           "avg": {
             "field": "price"
           }
         }
       }
     }
   }
 }
  * */
}

