package com.es.learn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        System.out.println( "Hello World!" );
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
         createEmployeeList(client);
//		createEmployee(client);
//		createEmployee1(client);
//		createEmployee2(client);
//		getResults(client);
//        update(client);
//        delete(client);
//		getEmployee(client);
//		updateEmployee(client);
//		deleteEmployee(client);

        client.close();
    }

    /*
    *
    *    // 方式一：直接给JSON串
    *    String jsonString = "{" +
                    "\"user\":\"kimchy\"," +
                    "\"postDate\":\"2013-01-30\"," +
                    "\"message\":\"trying out Elasticsearch\"" +
                    "}";
            request.source(jsonString, XContentType.JSON);
            // 方式二：以map对象来表示文档
            /*
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("user", "kimchy");
            jsonMap.put("postDate", new Date());
            jsonMap.put("message", "trying out Elasticsearch");
            request.source(jsonMap);
            */

    // 方式三：用XContentBuilder来构建文档
            /*
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.field("user", "kimchy");
                builder.field("postDate", new Date());
                builder.field("message", "trying out Elasticsearch");
            }
            builder.endObject();
            request.source(builder);
            */

    // 方式四：直接用key-value对给出
            /*
            request.source("user", "kimchy",
                            "postDate", new Date(),
                            "message", "trying out Elasticsearch");
            */


    private static void createEmployee(TransportClient client) throws Exception {
        IndexResponse response = client.prepareIndex("company", "employee", "1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "jack")
                        .field("age", 27)
                        .field("position", "technique")
                        .field("country", "china")
                        .field("join_date", "2017-01-01")
                        .field("salary", 10000)
                        .endObject())
                .get();
        System.out.println(response.getResult());
    }
    private static void createEmployee1(TransportClient client) throws Exception {
        Employee employee=new Employee("jack2",18,"technique","china","2017-01-01",10000);
        String jsonObject=JSONObject.toJSONString(employee);
        Employee employee1 = JSONObject.toJavaObject(JSONObject.parseObject(jsonObject), Employee.class);
        System.out.println(employee1.toString());
        System.out.println(jsonObject);
        Map<String ,Object> mapJson1= (Map<String, Object>) JSONObject.parse(jsonObject);
        Map<String ,Object> mapJson=new HashMap<>();
        mapJson.put("name","jack1");
        mapJson.put("age",18);
        mapJson.put("position","technique");
        mapJson.put("country","china");
        mapJson.put("join_date","2017-01-02");
        mapJson.put("salary",10000);
        IndexResponse response = client.prepareIndex("company", "employee", "2")
                .setSource(mapJson1, XContentType.JSON)
                .get();
        System.out.println(response.getResult());
    }
    private static void createEmployee2(TransportClient client) throws Exception {
        IndexResponse response = client.prepareIndex("company", "employee", "3")
                .setSource(
                        "name","zzq","age",18
                )
                .get();
        System.out.println(response.getResult());
    }

    private static void createEmployeeList(TransportClient client) throws Exception {
        for (int i = 0; i < 10; i++) {
            IndexResponse response = client.prepareIndex("company", "employee", i+"")
                    .setSource(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("name", "jack"+i)
                            .field("age", 27+i)
                            .field("position", "technique")
                            .field("country", "china")
                            .field("join_date", "2017-01-01")
                            .field("salary", 10000+i)
                            .endObject())
                    .get();
            System.out.println(response.getResult());
        }

    }

    public static  void getResults(TransportClient client){
        GetResponse getFields = client.prepareGet("company", "employee", "2").get();
        String re= getFields.getSourceAsString();
        Employee employee = JSONObject.toJavaObject(JSONObject.parseObject(re), Employee.class);
        System.out.println(employee.toString());
    }

    public static  void getResults1(TransportClient client){
        GetResponse getFields = client.prepareGet("company", "employee", "2").get();
        String re= getFields.getSourceAsString();
        Employee employee = JSONObject.toJavaObject(JSONObject.parseObject(re), Employee.class);
        System.out.println(employee.toString());
    }

    public static  void update(TransportClient client){
        client.prepareUpdate("company", "employee", "2")
                .setDoc("name","zzq").get();
    }

    private static void delete(TransportClient client) {
        client.prepareDelete("company", "employee", "2").get();
    }


    @Test
    public void a() throws IOException {
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .field("name", "jack")
                .field("age", 27)
                .field("position", "technique")
                .field("country", "china")
                .field("join_date", "2017-01-01")
                .field("salary", 10000)
                .endObject();

    }
}
