package com.es.learn.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class D04结构化搜索_在案例中实战基于bool组合多个filter条件来搜索数据 {
    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//        filter(client);
//        filter2(client);
//        filter3(client);
        filter4(client);
        client.close();
    }
    public static void filter(TransportClient client) {

        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                                              .setQuery(QueryBuilders.constantScoreQuery(
                                                 QueryBuilders.boolQuery()
                                                .should(QueryBuilders.termQuery("postDate","2017-01-01"))
                                                .should(QueryBuilders.termQuery("articleID","XHDK-A-1293-#fJ3"))
                                                .mustNot(QueryBuilders.termQuery("postDate","2017-01-02"))
                                              ))
                                              .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }
   /* GET /forum/article/_search
    {
        "query": {
        "constant_score": {
            "filter": {
                "bool": {
                    "should": [
                    {"term": {
                        "postDate": {
                            "value": "2017-01-01"
                        }
                    }},{
                        "term": {
                            "articleID": {
                                "value": "XHDK-A-1293-#fJ3"
                            }
                        }
                    }
       ],
                    "must_not": [
                    {"term": {
                        "postDate": {
                            "value": "2017-01-02"
                        }
                    }}
       ]
                }
            }
        }
    }
    }*/


    public static void filter2(TransportClient client) {
        Collection<QueryBuilder> collection=new ArrayList<>();
        collection.add(QueryBuilders.termQuery("articleID.keyword","JODL-X-1937-#pV7"));
        collection.add(QueryBuilders.termQuery("postDate","2017-01-01"));
        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                .setQuery(QueryBuilders.constantScoreQuery(
                             QueryBuilders.boolQuery()
                            .should(QueryBuilders.termQuery("articleID.keyword","XHDK-A-1293-#fJ3"))
                            .should(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("articleID.keyword","JODL-X-1937-#pV7"))
                                                             .must(QueryBuilders.termQuery("postDate","2017-01-01"))
                             )
                           )
                         )
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

   /*
    GET /forum/article/_search
    {
        "query": {
        "constant_score": {
            "filter": {
                "bool": {
                    "should": [
                    {
                        "term": {
                        "articleID.keyword": {
                            "value": "XHDK-A-1293-#fJ3"
                        }
                    }
                    },
                    {
                        "bool": {
                        "must": [
                        {
                            "term": {
                            "articleID.keyword": {
                                "value": "JODL-X-1937-#pV7"
                            }
                        }
                        },
                        {
                            "term": {
                            "postDate": {
                                "value": "2017-01-01"
                            }
                        }
                        }
            ]
                    }
                    }
      ]
                }
            }
        }
    }
    }
*/


    public static void filter3(TransportClient client) {
        //articleID.keyword 进行筛选
        TermQueryBuilder termQueryBuilderARIID = QueryBuilders.termQuery("articleID.keyword","JODL-X-1937-#pV7");
        //postDate 进行筛选
        TermQueryBuilder termQueryBuilderDate = QueryBuilders.termQuery("postDate","2017-01-01");
        //boolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(termQueryBuilderARIID).must(termQueryBuilderDate);
        //
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("articleID.keyword", "XHDK-A-1293-#fJ3");
        BoolQueryBuilder should = QueryBuilders.boolQuery().should(termQueryBuilder).should(boolQueryBuilder);
        ConstantScoreQueryBuilder constantScoreQueryBuilder = QueryBuilders.constantScoreQuery(should);
        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                .setQuery(constantScoreQueryBuilder)
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    public static void filter4(TransportClient client) {
       //   "articleID.keyword", "XHDK-A-1293-#fJ3"
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("articleID.keyword", "XHDK-A-1293-#fJ3");

        //articleID.keyword 进行筛选
        TermQueryBuilder termQueryBuilderARIID = QueryBuilders.termQuery("articleID.keyword","JODL-X-1937-#pV7");
        //postDate 进行筛选
        TermQueryBuilder termQueryBuilderDate = QueryBuilders.termQuery("postDate","2017-01-01");

        SearchResponse searchResponse = client.prepareSearch("forum").setTypes("article")
                .setQuery(QueryBuilders.constantScoreQuery(
                        QueryBuilders.boolQuery()
                                                .should(termQueryBuilder)
                                                .should(QueryBuilders.boolQuery()
                                                                                 .must(termQueryBuilderARIID)
                                                                                 .must(termQueryBuilderDate)
                                                       )
                        )
                )
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

}
