package com.es.learn;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.pipeline.bucketselector.BucketSelectorPipelineAggregationBuilder;
import org.elasticsearch.search.sort.*;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SearchTest {
    public static void main(String[] args) throws Exception{
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//            search(client);
            searchAggs(client);
//        searchAggsHard(client);
//        searchAggsHards(client);
           client.close();
    }

    public static void search(TransportClient client){
        SearchResponse searchResponse = client.prepareSearch("company")
                .setTypes("employee")
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("age","111"))
                        .mustNot(QueryBuilders.multiMatchQuery("11","field1","field2"))
                        .should(QueryBuilders.rangeQuery("age").from(28).to(35))
                        .filter(null)
                )
                .addSort("age", SortOrder.ASC)
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (int i = 0; i < hits.length; i++) {
            System.out.println(hits[i].getSourceAsString());
        }
    }

    public static void searchAggs(TransportClient client){
        SearchResponse searchResponse = client.prepareSearch("company")
                .setTypes("employee")
                .addAggregation(AggregationBuilders.avg("people")
                        .field("age")
                ).get();
        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            Avg stringTerms = (Avg) asMap.get(s);
            System.out.println(s);
            System.out.println(stringTerms.getValue());

        }
    }

   /* GET company/employee/_search
    {
        "size": 0,
            "aggs": {
        "people": {
            "avg": {
                "field": "age"
            }
        }
    }
    }*/

    public static  void  searchAggsHard(TransportClient client){

        SearchResponse searchResponse = client.prepareSearch("company")
                .setTypes("employee")
                .addAggregation(AggregationBuilders.terms("country-group").field("country").
                                     subAggregation(AggregationBuilders.terms("age-group").field("age").order((List<Terms.Order>) SortOrder.DESC)
                                                     .subAggregation(AggregationBuilders.avg("salary").field("salary")
                                                     )))
                .setSize(0)
                .get();
        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            StringTerms groupByCountry = (StringTerms) asMap.get("country-group");
            for (Bucket bucket : groupByCountry.getBuckets()) {
                System.out.println(bucket.getKey().toString()+"----"+bucket.getDocCount());
                Map<String, Aggregation> stringAggregationMap = bucket.getAggregations().asMap();
                LongTerms agegroup = (LongTerms) stringAggregationMap.get("age-group");
                for (Bucket bucket1 : agegroup.getBuckets()) {
                    Avg avg = (Avg)bucket1.getAggregations().getAsMap().get("salary");
                            System.out.println(bucket1.getKey()+"------"+bucket1.getDocCount()+"-------"+avg.getValue());
                }
            }

        }
    }

   /* GET /company/employee/_search
    {
        "size": 0,
            "aggs": {
        "country-group": {
            "terms": {
                "field": "country"
            },
            "aggs": {
                "age-group": {
                    "terms": {
                        "field": "age",
                                "order": {
                            "_count": "asc"
                        }
                    },
                    "aggs": {
                        "salary": {
                            "avg": {
                                "field": "salary"
                            }
                        }
                    }
                }
            }
        }
    }
    }*/


    public static  void  searchAggsHards(TransportClient client){
        Map<String,String> result_map = new ConcurrentHashMap<>();
        StringBuffer scripts=new StringBuffer();
        result_map.put("userCount","salary-avg");
//        result_map.put("salary-avg","userCount");
        List<Terms.Order> sortOrders=new ArrayList<>();
//        sortOrders.add();
        List<FieldSortBuilder> fieldSorts=new ArrayList<>();
        fieldSorts.add(new FieldSortBuilder("age").order(SortOrder.DESC));
        SearchResponse searchResponse = client.prepareSearch("company")
                .setTypes("employee")
                .addAggregation(AggregationBuilders.terms("country-group").field("country").
                        subAggregation(
                                AggregationBuilders.terms("age-group").field("age").order(Terms.Order.count(false))
                                .subAggregation(AggregationBuilders.avg("salary-avg").field("salary"))
//                                .subAggregation(PipelineAggregatorBuilders.bucketSelector("dd",result_map,new Script("params.userCount >= 10009")))
                        )
                )
                .setSize(0)
                .execute().actionGet();
        System.out.println(searchResponse.getAggregations().toString());
        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            StringTerms groupByCountry = (StringTerms) asMap.get("country-group");
            for (Bucket bucket : groupByCountry.getBuckets()) {
                System.out.println(bucket.getKey().toString()+"----"+bucket.getDocCount());
                Map<String, Aggregation> stringAggregationMap = bucket.getAggregations().asMap();
                LongTerms agegroup = (LongTerms) stringAggregationMap.get("age-group");
                for (Bucket bucket1 : agegroup.getBuckets()) {
                    Avg avg = (Avg)bucket1.getAggregations().getAsMap().get("salary-avg");
                    System.out.println(bucket1.getKey()+"------"+bucket1.getDocCount()+"-------"+avg.getValue());
                }
            }

        }
    }

    /*GET /company/employee/_search
    {
        "size": 0,
            "aggs": {
        "country-group": {
            "terms": {
                "field": "country"
            },
            "aggs": {
                "age-group": {
                    "terms": {
                        "field": "age",
                                "order": {
                            "_count": "asc"
                        }
                    },
                    "aggs": {
                        "pipe_selector": {
                            "avg": {
                                "field": "salary"
                            }
                        },
                        "dd": {
                            "bucket_selector": {
                                "buckets_path": {
                                    "userCount": "pipe_selector"
                                },
                                "script": "params.userCount > 10007"
                            }
                        }
                    }
                }
            }
        }
    }
    }*/

    public static  void  searchAggsHardss(TransportClient client){
        Map<String,String> result_map = new ConcurrentHashMap<>();
        StringBuffer scripts=new StringBuffer();
        result_map.put("userCount","salary-avg");
//        result_map.put("salary-avg","userCount");
        List<Terms.Order> sortOrders=new ArrayList<>();
//        sortOrders.add();
        List<FieldSortBuilder> fieldSorts=new ArrayList<>();
        fieldSorts.add(new FieldSortBuilder("age").order(SortOrder.DESC));
        SearchResponse searchResponse = client.prepareSearch("company")
                .setTypes("employee")
                .addAggregation(AggregationBuilders.terms("country-group").field("country").
                                subAggregation(
                                        AggregationBuilders.terms("age-group").field("age").order(Terms.Order.count(false))
                                                .subAggregation(AggregationBuilders.avg("salary-avg").field("salary"))
                                .subAggregation(PipelineAggregatorBuilders.bucketSelector("dd",result_map,new Script("params.userCount >= 10009")))
                                )
                )
                .setSize(0)
                .execute().actionGet();
        System.out.println(searchResponse.getAggregations().toString());
        Map<String, Aggregation> asMap = searchResponse.getAggregations().getAsMap();
        for (String s : asMap.keySet()) {
            StringTerms groupByCountry = (StringTerms) asMap.get("country-group");
            for (Bucket bucket : groupByCountry.getBuckets()) {
                System.out.println(bucket.getKey().toString()+"----"+bucket.getDocCount());
                Map<String, Aggregation> stringAggregationMap = bucket.getAggregations().asMap();
                LongTerms agegroup = (LongTerms) stringAggregationMap.get("age-group");
                for (Bucket bucket1 : agegroup.getBuckets()) {
                    Avg avg = (Avg)bucket1.getAggregations().getAsMap().get("salary-avg");
                    System.out.println(bucket1.getKey()+"------"+bucket1.getDocCount()+"-------"+avg.getValue());
                }
            }

        }
    }
}
