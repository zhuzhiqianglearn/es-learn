package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class D40_深入聚合数据分析_下钻分析之统计每季度每个品牌的销售额 {
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

        //季度分组
        DateHistogramAggregationBuilder datehistogram=new DateHistogramAggregationBuilder("jidu");
        datehistogram.field("sold_date");
        datehistogram.format("yyyy-MM");
        datehistogram.minDocCount(0);
        datehistogram.dateHistogramInterval(DateHistogramInterval.QUARTER);
        //季度卖出总金额
        SumAggregationBuilder price_sale = AggregationBuilders.sum("sum_sales_money").field("price");

        //品牌卖出总金额
        SumAggregationBuilder brand_price_sale = AggregationBuilders.sum("sum_brand_sales_money").field("price");

        //按照品牌分组

        TermsAggregationBuilder brand_sales = AggregationBuilders.terms("brand_sales").field("brand");

        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
                .setSize(0)
                .addAggregation(datehistogram.subAggregation(price_sale)
                                             .subAggregation(brand_sales.subAggregation(brand_price_sale)))
                .get();

        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            InternalDateHistogram dateHistogram = (InternalDateHistogram) asMap.get(s);
            Iterator<Histogram.Bucket> jidu_iterator = dateHistogram.getBuckets().iterator();
            while (jidu_iterator.hasNext()){
                Histogram.Bucket jidu_next = jidu_iterator.next();
                Map<String, Aggregation> jidu_map = jidu_next.getAggregations().getAsMap();
                for (String jindu_key : jidu_map.keySet()) {
                     if(jindu_key.equals("sum_sales_money")){
                         Sum sales_jidu= (Sum) jidu_map.get(jindu_key);
                         System.out.println(jidu_next.getKeyAsString()+"-------"+"----卖出总数："+jidu_next.getDocCount()+"-----卖出总价："+sales_jidu.getValue());
                     }else  if(jindu_key.equals("brand_sales")){
                         StringTerms brand_term = (StringTerms) jidu_map.get(jindu_key);
                         Iterator<Terms.Bucket> brand_iterator = brand_term.getBuckets().iterator();
                          while (brand_iterator.hasNext()){
                              Terms.Bucket next = brand_iterator.next();
                              Map<String, Aggregation> brand_sum_price = next.getAggregations().getAsMap();
                              for (String s1 : brand_sum_price.keySet()) {
                                  Sum sum = (Sum) brand_sum_price.get(s1);
                                  System.out.println(next.getKey()+"----卖出"+next.getDocCount()+"个-----金额："+sum.getValue());
                              }

                          }
                     }
                }
            }
        }
        }



  /*
GET /tvs/sales/_search
{
  "size": 0,
   "aggs": {
     "jidu": {
       "date_histogram": {
         "field": "sold_date",
         "interval": "quarter",
         "format": "yyyy-MM"
         , "min_doc_count": 0
       }
       , "aggs": {
         "sum_sales_money": {
           "sum": {
             "field": "price"
           }
         },
         "brand_sales":{
           "terms": {
             "field": "brand"
           }
           , "aggs": {
             "brand_sum": {
               "sum": {
                 "field": "price"
               }
             }
           }
         }
       }
     }
   }
}

结果：

{
  "took": 14,
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
    "jidu": {
      "buckets": [
        {
          "key_as_string": "2016-04",
          "key": 1459468800000,
          "doc_count": 1,
          "brand_sales": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [
              {
                "key": "小米",
                "doc_count": 1,
                "brand_sum": {
                  "value": 3000
                }
              }
            ]
          },
          "sum_sales_money": {
            "value": 3000
          }
        },
        {
          "key_as_string": "2016-07",
          "key": 1467331200000,
          "doc_count": 3,
          "brand_sales": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [
              {
                "key": "TCL",
                "doc_count": 2,
                "brand_sum": {
                  "value": 2700
                }
              },
              {
                "key": "长虹",
                "doc_count": 1,
                "brand_sum": {
                  "value": 2000
                }
              }
            ]
          },
          "sum_sales_money": {
            "value": 4700
          }
        },
        {
          "key_as_string": "2016-10",
          "key": 1475280000000,
          "doc_count": 2,
          "brand_sales": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [
              {
                "key": "长虹",
                "doc_count": 2,
                "brand_sum": {
                  "value": 3000
                }
              }
            ]
          },
          "sum_sales_money": {
            "value": 3000
          }
        },
        {
          "key_as_string": "2017-01",
          "key": 1483228800000,
          "doc_count": 1,
          "brand_sales": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [
              {
                "key": "三星",
                "doc_count": 1,
                "brand_sum": {
                  "value": 8000
                }
              }
            ]
          },
          "sum_sales_money": {
            "value": 8000
          }
        },
        {
          "key_as_string": "2017-04",
          "key": 1491004800000,
          "doc_count": 0,
          "brand_sales": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": []
          },
          "sum_sales_money": {
            "value": 0
          }
        },
        {
          "key_as_string": "2017-07",
          "key": 1498867200000,
          "doc_count": 0,
          "brand_sales": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": []
          },
          "sum_sales_money": {
            "value": 0
          }
        },
        {
          "key_as_string": "2017-10",
          "key": 1506816000000,
          "doc_count": 1,
          "brand_sales": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [
              {
                "key": "小米",
                "doc_count": 1,
                "brand_sum": {
                  "value": 2500
                }
              }
            ]
          },
          "sum_sales_money": {
            "value": 2500
          }
        }
      ]
    }
  }
}
  * */
}

