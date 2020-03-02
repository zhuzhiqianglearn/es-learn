package com.es.learn.core;

import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregator;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregatorFactory;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

public class D42_深入聚合数据分析_global_bucket单个品牌与所有品牌销量对比 {
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
        AvgAggregationBuilder sale_avg = AggregationBuilders.avg("sale_avg").field("price");

        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
                .setSize(0)
                .setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("price").gte(1200)))
                .addAggregation(sale_avg)
                .get();

        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
               Avg avg= (Avg) asMap.get(s);
            System.out.println(avg.getValue());
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

