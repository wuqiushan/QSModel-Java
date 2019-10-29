package com.wuqiushan;

import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.wuqiushan.QSModel.QSModel;
import com.wuqiushan.testModel.ReadJsonFile;
import com.wuqiushan.testModel.Student;
import com.wuqiushan.testModel.SupStudent;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testModelWithMap()
    {
        assertTrue( true );
        // 读取JSON文件
        String jsonStr = ReadJsonFile.readJsonFile();
        System.out.println(jsonStr);

        // String => Map
        Map<String, Object> map = (Map<String, Object>)QSModel.qs_objectWithString(jsonStr);
        // Map => Model
        Student student = QSModel.qs_modelWithMap(map, Student.class);

        System.out.println(map);
    }


    @Test
    public void testNestingStringWithMap() {

        // 第三层
        HashMap<String, Object> suenMap = new HashMap<>();
        suenMap.put("summaryId", "sum123");
        suenMap.put("mapId", "map123");

        // 第二层
        HashMap<String, Object> subMap = new HashMap<>();
        subMap.put("type", "type123");
        subMap.put("jsonData", QSModel.qs_stringWithObject(suenMap));

        // 第一层
        HashMap<String, Object> tmpMap = new HashMap<>();
        tmpMap.put("id", "id123");
        tmpMap.put("command", QSModel.qs_stringWithObject(subMap));

        String gsonStr = QSModel.qs_stringWithObject(tmpMap);
        System.out.println(gsonStr);

//        // 第三层
//        HashMap<String, Object> suenMap = new HashMap<>();
//        suenMap.put("summaryId", "sum123");
//        suenMap.put("mapId", "map123");
//
//        // 第二层
//        HashMap<String, Object> subMap = new HashMap<>();
//        subMap.put("type", "type123");
//        subMap.put("jsonData", new Gson().toJson(suenMap));
//
//        // 第一层
//        HashMap<String, Object> tmpMap = new HashMap<>();
//        tmpMap.put("id", "id123");
//        tmpMap.put("command", new Gson().toJson(subMap));
//
//        String gsonStr = new Gson().toJson(tmpMap);
//        System.out.println(gsonStr);
    }

    @Test
    public void testStringArray() {

        List<Object> testArray = new ArrayList<>();
        testArray.add("123");
        testArray.add("test");

//        List<Object> addArray = new ArrayList<>();
//        addArray.add("test1");
//        addArray.add("test2");
//        testArray.add(addArray);
        String testStr = QSModel.qs_stringWithObject(testArray);
        List<String> result = (List<String>) QSModel.qs_objectWithString(testStr);
        System.out.println(result);
    }

    @Test
    public void testStringWithMap()
    {
        assertTrue( true );
        // 读取JSON文件
        String jsonStr = ReadJsonFile.readJsonFile();
        System.out.println(jsonStr);

        // String => Map
        Map<String, Object> map = (Map<String, Object>)QSModel.qs_objectWithString(jsonStr);
        // Map => Model
        String str = QSModel.qs_stringWithObject(map);

        Map<String, Object> map1 = (Map<String, Object>)QSModel.qs_objectWithString(jsonStr);

        Student student = QSModel.qs_modelWithMap(map1, Student.class);

        System.out.println(map);
    }

    @Test
    public void testMapWithModel()
    {
        assertTrue( true );
        // 读取JSON文件
        String jsonStr = ReadJsonFile.readJsonFile();
        System.out.println(jsonStr);

        // String => Map
        Map<String, Object> map = (Map<String, Object>)QSModel.qs_objectWithString(jsonStr);
        Student student = QSModel.qs_modelWithMap(map, Student.class);

        HashMap map1 = QSModel.qs_mapWithModel(student);

        System.out.println(map);
    }


    @Test
    public void testMapWithString() {
//        String str = "{\"id\":\"MOONA21543202291701\",\"command\":{\"jsonData\":\"{\\\"summaryId\\\":\\\"5d8d78c15cc18d03ca0b09e7\\\",\\\"mapId\\\":\\\"{\\\\\\\"test\\\\\\\":\\\\\\\"5d8d78c15cc18d03ca0b09e7\\\\\\\"}\\\"}\",\"cmd\":\"close_team\",\"type\":17}}";
        String str = "{    \"id\":\"2462079046\",    \"name\": \"张三\",    \"age\":\"22\",    \"weight\":120.0,   \"six\":false,    \"address\":{        \"country\": \"中国\",        \"province\": \"湖南省\"    },    \"addressA\":{        \"country\": \"中国\",        \"province\": \"台湾省\"    },    \"courses\":[        {            \"name\": \"物理\",            \"duration\": 30        },        {            \"name\": \"化学\",            \"duration\": 45        }    ],    \"coursesA\":[        {            \"name\": \"物理\",            \"duration\": 30        },        {            \"name\": \"化学\",            \"duration\": 45        }    ],    \"birthday\": \"1996-03-28 05:27:31.050\"}";
        Object hashMap = QSModel.qs_objectWithString(str);
        System.out.println(hashMap);
    }

    @Test
    public void testDo() {
        System.out.println(double.class);
        System.out.println(Double.class);
        if (double.class == Double.class) {
            System.out.println("相等了");
        }
    }

    @Test
    public void testRegex() {

        StringBuilder strB = new StringBuilder("\"id\":\"2462079046\",    \"name\": \"张三\",    \"age\":\"22\",    \"weight\":120.0,   \"six\":false,    \"address\":{        \"country\": \"中国\",        \"province\": \"湖南省\"    },    \"addressA\":{        \"country\": \"中国\",        \"province\": \"台湾省\"    },    \"courses\":[        {            \"name\": \"物理\",            \"duration\": 30        },        {            \"name\": \"化学\",            \"duration\": 45        }    ],    \"coursesA\":[        {            \"name\": \"物理\",            \"duration\": 30        },        {            \"name\": \"化学\",            \"duration\": 45        }    ],    \"birthday\": \"1996-03-28 05:27:31.050\"");
        HashMap map = QSModel.splitMaxMatches(strB, "[\\[\\]{}]{1}");
        System.out.println(map);
        System.out.println(strB);
    }
}
