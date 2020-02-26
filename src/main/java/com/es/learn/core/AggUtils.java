package com.es.learn.core;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AggUtils {
    public static String getResults(Map<String, Aggregation> aggregationMap,String... aggTerms) {
        String key="";
        String value="";
        List<String> stringList=new ArrayList<String>();
        getBucketAsList(stringList,key,value,aggregationMap,0,aggTerms);
        System.out.println(stringList);
        return null;
    }
    private static List<AggObject> getBucketAsList(List<String> stringList,String key,String value,Map<String, Aggregation> aggregationMap,int index,String... aggTerms) {
        if(index==aggTerms.length-1){
            for (String s : aggregationMap.keySet()) {
                getResultvalue(aggregationMap.get(s),aggTerms[index]);
                System.out.println(key+","+getResultvalue(aggregationMap.get(s),aggTerms[index]));
            }
            index=index-1;
        }
        else{
            for (String s: aggregationMap.keySet()) {
                Aggregation aggregation = aggregationMap.get(s);
                Iterator<Terms.Bucket> bucketListIterator = getBucketList(aggregation, aggTerms[index]);
                while (bucketListIterator.hasNext()){
                    Terms.Bucket next = bucketListIterator.next();

                    key=key+next.getKeyAsString()+",";
                    value=value+next.getDocCount()+",";
                    index++;
                    getBucketAsList(stringList,key,value,next.getAggregations().getAsMap(),index,aggTerms);
                }
                index--;
            }
        }
        return null;
    }

    private static  Iterator<Terms.Bucket> getBucketList(Aggregation aggregation,String terms){
        Iterator<Terms.Bucket> buckets=null;

        if(terms.equals("StringTerms")){
             buckets = ((StringTerms) aggregation).getBuckets().iterator();
        }else if(terms.equals("LongTerms")){
            buckets = ((LongTerms) aggregation).getBuckets().iterator();
        }else if(terms.equals("DoubleTerms")){
            buckets = ((DoubleTerms) aggregation).getBuckets().iterator();
        }else{
            buckets = ((Terms) aggregation).getBuckets().iterator();
        }
        return buckets;
    }
    private static  String getResultvalue(Aggregation  aggregation, String terms){
        String value="";
        if(terms.equals("avg")){
            value = ((Avg) aggregation).getValueAsString()+((Avg) aggregation).getName();
        }else if(terms.equals("sum")){
            value = ((Sum) aggregation).getValueAsString();
        }else if(terms.equals("avg11")){
        }
        return value;
    }
}
