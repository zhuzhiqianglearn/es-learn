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
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

public class D36_深入聚合数据分析_bucket嵌套实现颜色以及品牌的多层下钻分析 {
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
        TermsAggregationBuilder color = AggregationBuilders.terms("group_by_color").field("color");
        TermsAggregationBuilder brand = AggregationBuilders.terms("group_by_brand").field("brand");
        AvgAggregationBuilder price = AggregationBuilders.avg("avg_color_brand").field("price");
        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
                .setSize(0)
                .addAggregation(color.subAggregation(
                                                      brand.subAggregation(
                                                                            price)))
                .get();
        Map<String, Aggregation> stringAggregationMap =
                searchResponse.getAggregations().asMap();
        for (String s : stringAggregationMap.keySet()) {
            /*
            StringTerms:所取字段是 字符型
            LongTerms：所取字段是数字
            DoubleTerms：所取字段是小数
             */
            StringTerms colorgroup = (StringTerms) stringAggregationMap.get(s);
            Iterator<Terms.Bucket> colorgroupIterator = colorgroup.getBuckets().iterator();
            while (colorgroupIterator.hasNext()){
                Terms.Bucket colorBucket = colorgroupIterator.next();
//                System.out.println(next.getKey()+"---------"+next.getDocCount());
                Map<String, Aggregation> brandMap = colorBucket.getAggregations().getAsMap();
                for (String s1 : brandMap.keySet()) {
                    StringTerms brandTerms = (StringTerms) brandMap.get(s1);
                    Iterator<Terms.Bucket> brand_iterator = brandTerms.getBuckets().iterator();
                    while (brand_iterator.hasNext()){
                        Terms.Bucket brandBucket = brand_iterator.next();
                        Map<String, Aggregation> brandAvgMap = brandBucket.getAggregations().getAsMap();
                        for (String s2 : brandAvgMap.keySet()) {
                            Avg color_brand_avg = (Avg) brandAvgMap.get(s2);
                            System.out.println(colorBucket.getKey()+"--个数："+colorBucket.getDocCount()+"---品牌："
                                    +brandBucket.getKey()+"---个数："+brandBucket.getDocCount()
                                    +"--平均价格："+color_brand_avg.getValue());
                        }
                    }

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
       },
       "aggs": {
         "group_by_brand": {
           "terms": {
             "field": "brand"
           }
           , "aggs": {
             "avg_color_brand": {
               "avg": {
                 "field": "price"
               }
             }
           }
         }
       }
     }
   }
 }
  * */
}

