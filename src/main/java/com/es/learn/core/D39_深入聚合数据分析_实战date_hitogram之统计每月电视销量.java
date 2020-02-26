package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
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
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class D39_深入聚合数据分析_实战date_hitogram之统计每月电视销量 {
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
        DateHistogramAggregationBuilder date_histogram=new DateHistogramAggregationBuilder("date_histogram");
        date_histogram.dateHistogramInterval(DateHistogramInterval.DAY);
        date_histogram.format("yyyy-MM-dd");
        date_histogram.field("sold_date");
        date_histogram.minDocCount(1);
//        date_histogram.extendedBounds(new ExtendedBounds("2017-01","2017-12"));
        long a=3L;
        long b=5L;
        date_histogram.extendedBounds(new ExtendedBounds(a,b));
        SearchResponse searchResponse = client.prepareSearch("tvs")
                .setTypes("sales")
                .setSize(0)
                .addAggregation(date_histogram)
                .get();

        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            InternalDateHistogram dateHistogram = (InternalDateHistogram) asMap.get(s);
            List<Histogram.Bucket> buckets = dateHistogram.getBuckets();
            for (Histogram.Bucket bucket : buckets) {
                System.out.println(bucket.getKeyAsString()+"----"+bucket.getDocCount());
            }
        }
        }



  /*
GET /tvs/sales/_search
{
  "size": 0,
  "aggs": {
    "aaaa": {
      "date_histogram": {
        "field": "sold_date",
        "interval": "month",
        "format": "yyyy-MM-dd",
        "min_doc_count": 0
      }
    }
  }
}


结果：

{
  "took": 6,
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
    "aaaa": {
      "buckets": [
        {
          "key_as_string": "2016-05-01",
          "key": 1462060800000,
          "doc_count": 1
        },
        {
          "key_as_string": "2016-06-01",
          "key": 1464739200000,
          "doc_count": 0
        },
        {
          "key_as_string": "2016-07-01",
          "key": 1467331200000,
          "doc_count": 1
        },
        {
          "key_as_string": "2016-08-01",
          "key": 1470009600000,
          "doc_count": 1
        },
        {
          "key_as_string": "2016-09-01",
          "key": 1472688000000,
          "doc_count": 0
        },
        {
          "key_as_string": "2016-10-01",
          "key": 1475280000000,
          "doc_count": 1
        },
        {
          "key_as_string": "2016-11-01",
          "key": 1477958400000,
          "doc_count": 2
        },
        {
          "key_as_string": "2016-12-01",
          "key": 1480550400000,
          "doc_count": 0
        },
        {
          "key_as_string": "2017-01-01",
          "key": 1483228800000,
          "doc_count": 1
        },
        {
          "key_as_string": "2017-02-01",
          "key": 1485907200000,
          "doc_count": 1
        }
      ]
    }
  }
}
  * */
}

