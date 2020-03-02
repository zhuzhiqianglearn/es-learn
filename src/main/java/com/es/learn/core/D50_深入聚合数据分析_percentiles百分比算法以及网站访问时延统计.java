package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

public class D50_深入聚合数据分析_percentiles百分比算法以及网站访问时延统计 {
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


        AvgAggregationBuilder avg_baifenbi = AggregationBuilders.avg("avg_baifenbi").field("latency");
        PercentilesAggregationBuilder percentiles = AggregationBuilders.percentiles("baifenbi").field("latency").percentiles(1, 5, 25, 50, 75, 95, 99);

        SearchResponse searchResponse = client.prepareSearch("website1")
                .setTypes("logs")
                .setSize(0)
                .addAggregation(avg_baifenbi)
                .addAggregation(percentiles)
                .get();

        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            if(s.equals("avg_baifenbi")){
                Avg avg= (Avg) asMap.get(s);
                System.out.println("avg-------------"+avg.getValue());
            }
            else{
                Percentiles percentiles1 = (Percentiles) asMap.get(s);
                Iterator<Percentile> iterator = percentiles1.iterator();
                while (iterator.hasNext()){
                    Percentile next = iterator.next();
                    System.out.println(next.getPercent()+"--------"+next.getValue());
                }
            }
        }

        }



  /*


GET website1/logs/_search
{

  "size": 0,
   "aggs": {
    "baifenbi": {
      "percentiles": {
        "field": "latency",
        "percents": [
          1,
          5,
          25,
          50,
          75,
          95,
          99
        ]
      }
    },
    "avg_baifenbi":{
      "avg": {
        "field": "latency"
      }
    }
   }
}


 结果：

{
  "took": 5,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 12,
    "max_score": 0,
    "hits": []
  },
  "aggregations": {
    "baifenbi": {
      "values": {
        "1.0": 68.88,
        "5.0": 72.4,
        "25.0": 89.75,
        "50.0": 108.5,
        "75.0": 281.75,
        "95.0": 508.24999999999983,
        "99.0": 624.8500000000001
      }
    },
    "avg_baifenbi": {
      "value": 201.91666666666666
    }
  }
}

  * */
}

