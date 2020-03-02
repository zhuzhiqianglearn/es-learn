package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

public class D45_深入聚合数据分析_排序_按每种颜色的平均销售额降序排序 {
    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        query(client);
        client.close();
    }
    public static void query(TransportClient client) throws ParseException {

        TermsAggregationBuilder orderTerms = AggregationBuilders.terms("group_by_color").field("brand")
                .order(Terms.Order.aggregation("sale_avg",false));//true 升序 false 降序

        //平均价钱
        AvgAggregationBuilder sale_avg = AggregationBuilders.avg("sale_avg").field("price");

        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
                .setSize(0)
                .addAggregation(orderTerms.subAggregation(sale_avg))
                .get();

        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            StringTerms stringTerms= (StringTerms) asMap.get(s);
            Iterator<Terms.Bucket> iterator = stringTerms.getBuckets().iterator();
            while (iterator.hasNext()){
                Terms.Bucket next = iterator.next();
                Map<String, Aggregation> asMap1 = next.getAggregations().getAsMap();
                for (String s1 : asMap1.keySet()) {
                    Avg avg= (Avg) asMap1.get(s1);

                    System.out.println(next.getKey()+"-------"+next.getDocCount()+"---"+avg.getValue());
                }
            }
        }

        }



  /*
GET /tvs/sales/_search
{
  "size": 0,
 "query": {
   "bool": {
     "filter": {
       "range": {
         "price": {
           "gt": 1200
         }
       }
     }
   }
 }, "aggs": {
   "avg_price": {
     "avg": {
       "field": "price"
     }
   },
   "sum_price":{
     "sum": {
       "field": "price"
     }
   },
   "count_price":{
     "value_count": {
       "field": "price"
     }
   }
 }
}
结果：
{
  "took": 2,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 6,
    "max_score": 0,
    "hits": []
  },
  "aggregations": {
    "count_price": {
      "value": 6
    },
    "avg_price": {
      "value": 3166.6666666666665
    },
    "sum_price": {
      "value": 19000
    }
  }
}
  * */
}

