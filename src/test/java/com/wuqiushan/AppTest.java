package com.wuqiushan;

import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.wuqiushan.QSModel.QSModel;
import com.wuqiushan.testModel.ReadJsonFile;
import com.wuqiushan.testModel.Student;
import com.wuqiushan.testModel.SupStudent;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
        // 读取JSON文件
        String jsonStr = ReadJsonFile.readJsonFile();
        System.out.println(jsonStr);
        // 用Gson把 String => Map  (这个后面要换掉)

        Map<String, Object> map = new Gson().fromJson(jsonStr, HashMap.class);
//        Student student1 = new Gson().fromJson(jsonStr, Student.class);
        Student student = QSModel.qs_modelWithMap(map, Student.class);
        System.out.println(map);
    }

    @Test
    public void testDo() {
        System.out.println(double.class);
        System.out.println(Double.class);
        if (double.class == Double.class) {
            System.out.println("相等了");
        }
    }


//    public static Object testM() {
//        Object result = null;
//        try {
//            result = convertType(int.class, Byte.class, (byte)12);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    @Test
    public void testSwitch() {

//        Object result = testM();
//        System.out.println(result);
    }
}
