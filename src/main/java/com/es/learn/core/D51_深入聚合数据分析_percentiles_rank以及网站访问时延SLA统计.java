package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.*;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

public class D51_深入聚合数据分析_percentiles_rank以及网站访问时延SLA统计 {
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


        PercentileRanksAggregationBuilder percentileRanks = AggregationBuilders.percentileRanks("baifenbi_zhanbi").field("latency").values(200, 500, 1000);
        SearchResponse searchResponse = client.prepareSearch("website1")
                .setTypes("logs")
                .setSize(0)
                .addAggregation(percentileRanks)
                .get();

        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {

            PercentileRanks percentiles = (PercentileRanks) asMap.get(s);
            Iterator<Percentile> iterator = percentiles.iterator();
            while (iterator.hasNext())
            {
                Percentile next = iterator.next();
                System.out.println(next.getValue()+"------------"+next.getPercent());
            }

        }


        }



  /*

GET /website1/logs/_search
{
  "size": 0,
  "aggs": {
    "qiu baifenbi": {
      "percentile_ranks": {
        "field": "latency",
        "values": [
          200,
          500,
          1000
        ]
      }
    }
  }
}

 结果：
{
  "took": 13,
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
    "qiu baifenbi": {
      "values": {
        "200.0": 64.57055214723927,
        "500.0": 82.65723270440252,
        "1000.0": 100
      }
    }
  }
}
  * */
}

