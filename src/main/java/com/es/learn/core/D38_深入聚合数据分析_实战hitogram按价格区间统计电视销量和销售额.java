package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

public class D38_深入聚合数据分析_实战hitogram按价格区间统计电视销量和销售额 {
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
        HistogramAggregationBuilder histogram_price_agg = AggregationBuilders.histogram("histogram_price").field("price").interval(2000);
        AvgAggregationBuilder avg_price = AggregationBuilders.avg("avg_price").field("price");
        MaxAggregationBuilder max_price = AggregationBuilders.max("max_price").field("price");
        MinAggregationBuilder min_price = AggregationBuilders.min("min_price").field("price");
        SumAggregationBuilder sum_price = AggregationBuilders.sum("sum_price").field("price");
        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
                .setSize(0)
                .addAggregation(histogram_price_agg.subAggregation(avg_price)
                                                  .subAggregation(max_price)
                                                  .subAggregation(min_price)
                                                  .subAggregation(sum_price))
                .get();

        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            Histogram histogram = (Histogram) asMap.get(s);
            Iterator<Histogram.Bucket> iterator = histogram.getBuckets().iterator();
            while (iterator.hasNext()){
                Histogram.Bucket next = iterator.next();
                Map<String, Aggregation> asMap1 = next.getAggregations().getAsMap();
                for (String s1 : asMap1.keySet()) {
                    if(s1.equals("max_price")){
                        Max max = (Max) asMap1.get(s1);
                        System.out.println(next.getKey()+"----------"+next.getDocCount()+"--------最大-----"+max.getValue());
                    }
                    else if(s1.equals("min_price")){
                        Min min = (Min) asMap1.get(s1);
                        System.out.println(next.getKey()+"----------"+next.getDocCount()+"--------最小-----"+min.getValue());
                    }
                    else if(s1.equals("avg_price")){
                        Avg avg = (Avg) asMap1.get(s1);
                        System.out.println(next.getKey()+"----------"+next.getDocCount()+"-------平均------"+avg.getValue());
                    }
                    else if(s1.equals("sum_price")){
                        Sum sum = (Sum) asMap1.get(s1);
                        System.out.println(next.getKey()+"----------"+next.getDocCount()+"-------总计------"+sum.getValue());
                    }
                }
                System.out.println("------------------------------");
                System.out.println("------------------------------");
            }
        }

    }


  /*
GET /tvs/sales/_search
{
  "size": 0
  , "aggs": {
    "group_color": {
      "terms": {
        "field": "color"
      },
      "aggs": {
        "avg_price": {
          "avg": {
            "field": "price"
          }
        },
        "max_price":{
          "max": {
            "field": "price"
          }
        },
        "min_price":{
          "min": {
            "field": "price"
          }
        },
        "sum_price":{
          "sum": {
            "field": "price"
          }
        }
      }

    }
  }
}



结果：


{
  "took": 24,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 8,
    "max_score": 0,
    "hits": []
  },
  "aggregations": {
    "group_color": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [
        {
          "key": "红色",
          "doc_count": 4,
          "max_price": {
            "value": 8000
          },
          "min_price": {
            "value": 1000
          },
          "avg_price": {
            "value": 3250
          },
          "sum_price": {
            "value": 13000
          }
        },
        {
          "key": "绿色",
          "doc_count": 2,
          "max_price": {
            "value": 3000
          },
          "min_price": {
            "value": 1200
          },
          "avg_price": {
            "value": 2100
          },
          "sum_price": {
            "value": 4200
          }
        },
        {
          "key": "蓝色",
          "doc_count": 2,
          "max_price": {
            "value": 2500
          },
          "min_price": {
            "value": 1500
          },
          "avg_price": {
            "value": 2000
          },
          "sum_price": {
            "value": 4000
          }
        }
      ]
    }
  }
}
  * */
}

