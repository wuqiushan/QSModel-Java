package com.wuqiushan;

import static org.junit.Assert.assertTrue;

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
