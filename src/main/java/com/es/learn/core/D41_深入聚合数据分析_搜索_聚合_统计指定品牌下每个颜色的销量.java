package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

public class D41_深入聚合数据分析_搜索_聚合_统计指定品牌下每个颜色的销量 {
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
        TermsQueryBuilder trem_brand = QueryBuilders.termsQuery("brand", "小米");
        TermsAggregationBuilder sale_count = AggregationBuilders.terms("sale_count").field("color");
        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
                .setSize(0)
                .setQuery(trem_brand)
                .addAggregation(sale_count)
                .get();

        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            StringTerms stringTerm= (StringTerms) asMap.get(s);
            Iterator<Terms.Bucket> iterator = stringTerm.getBuckets().iterator();
            while (iterator.hasNext()){
                Terms.Bucket next = iterator.next();
                System.out.println(next.getKey()+"---"+next.getDocCount());
            }
        }

        }



  /*
GET /tvs/sales/_search
{
  "size": 10,
  "query": {
    "term": {
      "brand": "小米"
    }
  }
  , "aggs": {
    "sale_count": {
      "terms": {
        "field": "color"
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
    "total": 2,
    "max_score": 0.47000363,
    "hits": [
      {
        "_index": "tvs",
        "_type": "sales",
        "_id": "AXB2Wp7pUwCUKwWpa3lg",
        "_score": 0.47000363,
        "_source": {
          "price": 2500,
          "color": "蓝色",
          "brand": "小米",
          "sold_date": "2017-12-12"
        }
      },
      {
        "_index": "tvs",
        "_type": "sales",
        "_id": "AXB2Wp7pUwCUKwWpa3lb",
        "_score": 0.2876821,
        "_source": {
          "price": 3000,
          "color": "绿色",
          "brand": "小米",
          "sold_date": "2016-05-18"
        }
      }
    ]
  },
  "aggregations": {
    "sale_count": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [
        {
          "key": "绿色",
          "doc_count": 1
        },
        {
          "key": "蓝色",
          "doc_count": 1
        }
      ]
    }
  }
}
  * */
}

