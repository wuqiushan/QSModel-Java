package com.wuqiushan.QSModel;

import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QSModel {

    /**
     *
     * @param map  Json字典
     * @param targetClass 目标类的类型
     * @param <T> 任意泛形，一定是个引用类型，而不是值类型
     * @return 返回转换后的对象
     */
    public static <T extends Object> T qs_modelWithMap(Map<String, Object> map, Class<T> targetClass) {

        if (map == null) { return null; }
        T object = null;
        try {
            object = targetClass.getConstructor().newInstance();
            // 通过反射获取(除父类外)所有属性包含private
            Field[] fields = targetClass.getDeclaredFields();
            // 循环去父类的字段

            // 遍历所有成员变量
            for (Field field : fields) {

                // 获取成员变量名称
                String fieldName = field.getName();

                // 获取成员变量类型
                Class<?> fieldType = field.getType();

                // 通过成员变量获取字典的值
                Object mapValue = map.get(fieldName);
                if (mapValue == null) {  // 如果字典里没有值就返回
                    continue;
                }

                if (fieldName.equals("address")) {
//                    mapValue = (int)120;
                    System.out.println("test");
                }
                if (fieldName.equals("addressA")) {
//                    mapValue = (int)120;
                    System.out.println("test");
                }
                if (fieldName.equals("courses")) {
                    System.out.println("test");
                }
                if (fieldName.equals("coursesA")) {
                    System.out.println("test");
                }

                // 设置值到对象里，设置前，把允许打开
                field.setAccessible(true);
//                System.out.println(">>>1" + mapValue.getClass());
//                System.out.println(">>>2" + fieldType);
                if (mapValue.getClass() != fieldType) {

                    try {
                        mapValue = convertType(fieldType, mapValue.getClass(), mapValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 如果类型没有相等的，在基本类型又找不到，初步判断为对象
                    if ((mapValue.getClass() == Integer.class) && ((int)mapValue == -1)) {

                        mapValue = map.get(fieldName);

                        if ((mapValue.getClass() == LinkedHashMap.class) ||
                                (mapValue.getClass() == HashMap.class) ||
                                (mapValue.getClass() == TreeMap.class)) {
                            System.out.println("来了");
                            mapValue = null;
                            System.out.println("转化了");
                        }
                        else {

                            // 为Map类型的话
                            if (mapValue instanceof Map) {
                                mapValue = qs_modelWithMap((Map<String, Object>) mapValue, fieldType);
                            }
                        }
                    }
                }
                // 如果是数组
                else if ((fieldType == ArrayList.class) && (mapValue.getClass() == ArrayList.class)) {

                    /**
                     * 1.获取其元素类型字符串
                     * 2.例：genericType = java.util.ArrayList<java.util.Map>
                     * 3.获取类型
                     */
                    Type genericType = field.getGenericType();
                    Pattern pattern = Pattern.compile("<[.a-zA-Z]*>");
                    Matcher matcher = pattern.matcher(genericType.getTypeName());

                    // 匹配到结果如：<java.util.Map>
                    String targetType = "";
                    while (matcher.find()) {

                        String matcherStr = matcher.group();
                        if ((matcherStr.length() > 2) &&
                                (matcherStr.charAt(0) == '<') &&
                                (matcherStr.charAt(matcherStr.length() - 1) == '>')) {

                            targetType = matcherStr.substring(1, matcherStr.length() - 1);
                            break;
                        }
                    }

                    // 得到数组元素的类型
                    System.out.println("得到数组元素的类型" + targetType);

                    // 获取到类型字符串时，转化为类型
                    if (!targetType.equals("")) {
                        fieldType = Class.forName(targetType);
                    }

                    // 遍历字典的元素
                    ArrayList<Object> arrayList = new ArrayList<>();
                    for (Object element : (ArrayList)mapValue) {

                        Object elementTmp = null;
                        try {
                            elementTmp = convertType(fieldType, element.getClass(), element);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // 如果类型没有相等的，在基本类型又找不到，初步判断为对象
                        if ((elementTmp.getClass() == Integer.class) && ((int)elementTmp == -1)) {

//                            elementTmp = map.get(fieldName);
                            elementTmp = element;

                            if ((elementTmp.getClass() == LinkedHashMap.class) ||
                                    (elementTmp.getClass() == HashMap.class) ||
                                    (elementTmp.getClass() == TreeMap.class)) {
                                System.out.println("来了");
                                elementTmp = null;
                                System.out.println("转化了");
                            }
                            else {

                                // 为Map类型的话
                                if (elementTmp instanceof Map) {
                                    elementTmp = qs_modelWithMap((Map<String, Object>) elementTmp, fieldType);
                                }
                            }
                        }
                        arrayList.add(elementTmp);
                    }
                    mapValue = arrayList;
                }

                // 如果有值才设置
                if (mapValue != null) {
                    field.set(object, mapValue);
                }
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }


    /**
     * 类型转化为对应的类型，如果出错，返回null
     * 本类型中支持7种内置数据类型，除char以外，因为在json中没有char型
     * @param targetT  目标类型
     * @param orgT     原类型
     * @param orgValue 原值
     * @param <T>      目标泛型
     * @param <E>      原泛型
     * @return 返回转化后的结果
     * @throws Exception
     */
    private static <T, E> Object convertType(Class<T> targetT, Class<E> orgT, Object orgValue) throws Exception {

        Object object = null;
        try {
            String targetType = targetT.getName();
            String tmpStr = String.valueOf(orgValue);

            switch (targetType) {
                case "java.lang.Double":
                case "double":
                    object = Double.parseDouble(tmpStr);
                    break;
                case "java.lang.Integer":
                case "int":
                    object = Integer.parseInt(tmpStr);
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    object = Boolean.parseBoolean(tmpStr);
                    break;

                case "java.lang.Byte":
                case "byte":
                    object = Byte.parseByte(tmpStr);
                    break;

                case "java.lang.Character":
                case "char":
//                    object = Character.;
                    break;

                case "java.lang.Long":
                case "long":
                    object = Long.parseLong(tmpStr);
                    break;

                case "java.lang.Short":
                case "short":
                    object = Short.parseShort(tmpStr);
                    break;

                default:
                    object = -1; // 代表未找到类型
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(orgT.getName() + " convert to " + targetT.getName() + " error");
        }
        return object;
    }

    // jsonString to Map/Array
    public static Object qs_objectWithString(String orgStr) {

        if (orgStr == null) { return null; }

        // 解析前：去除所有空格 \r \n 等字符
        orgStr = orgStr.replace("\r", "");
        orgStr = orgStr.replace("\n", "");
        orgStr = orgStr.replace(" ", "");

        if (orgStr.length() == 0) { return null; }
        StringBuilder strBuilder = new StringBuilder(orgStr);

        // 判断是否为字典
        if ( (strBuilder.charAt(0) == '{') && (strBuilder.charAt(orgStr.length() - 1) == '}') ) {

            /** 删除前后的 {} ==> "xx":xx,"xx":"xx" */
            strBuilder.deleteCharAt(orgStr.length() - 1);
            strBuilder.deleteCharAt(0);

            /** 存放转换后的元素 */
            HashMap<String, Object> hashMap = new HashMap<>();

            /** "key1":xx,"key2":"xx","key3":{"sub1":"xx", "sub2":"xx"},"key4":[] */
            String[] strings = strBuilder.toString().split(",");
            for (String element : strings) { // "xx":xx

                /** 1.if(正则 "xx":"xx",) 为字符串String (频度大放最前面，减少判断开销) */
                if (element.matches("^\"\\w+\":\"\\d+\"")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index + 1, element.length() - 1);
                    hashMap.put(key, value);
                }
                /** 2.if(正则 "xx":[.0-9]],) 为浮点Number  优先匹配因为[0-9]容易把该类型匹配走 */
                else if(element.matches("^\"\\w+\":\\d+[.]{1}\\d+")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index, element.length());
                    Double valueDouble = Double.parseDouble(value);
                    hashMap.put(key, valueDouble);
                }
                /** 3.if(正则 "xx":[0-9],) 为整形Number */
                else if(element.matches("^\"\\w+\":\\d+")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index, element.length());
                    Integer valueInt = Integer.parseInt(value);
                    hashMap.put(key, valueInt);
                }
                /** 4.if(正则 "xx":true,) 为Boolean true */
                else if(element.matches("^\"\\w+\":true")) {
                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    Boolean value = true;
                    hashMap.put(key, value);
                }
                /** 5.if(正则 "xx":false,) 为Boolean false */
                else if(element.matches("^\"\\w+\":false")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    Boolean value = false;
                    hashMap.put(key, value);
                }
                /** 6.if(正则 "xx":[]) 为Array 递归 第一步 */
                else if(element.matches("^\"\\w+\":\\[")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index, element.length());
                    Object subObject = qs_objectWithString(value);
                    hashMap.put(key, subObject);
                }
                /** 7.if(正则 "xx":{}) 为Map(Object) 递归 第一步 */
                else if(element.matches("^\"\\w+\":\\{")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index, element.length());
                    Object subObject = qs_objectWithString(value);
                    hashMap.put(key, subObject);
                }
                /** 8.if(正则 "xx":"null") 为null */
                else if(element.matches("^\"\\w+\":null")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    hashMap.put(key, null);
                }
                else {
                    System.out.println("解析失败：" + element + " 格式错误");
                }
            }

            return hashMap;
        }
        // 判断是否为数组 (要测试这种空的情况：[] )
        else if ( (strBuilder.charAt(0) == '[') && (strBuilder.charAt(orgStr.length() - 1) == ']') ) {

            /** 删除前后的 [] ==> {},{},{} */
            strBuilder.deleteCharAt(orgStr.length() - 1);
            strBuilder.deleteCharAt(0);

            /** 存放转换后的元素 */
            ArrayList<Object> arrayList = new ArrayList<>();

            /** {},{},{} 遍历后， 递归调用获取结束 */
            String[] strings = strBuilder.toString().split(",");
            for (String element : strings) {
                Object objectElement = qs_objectWithString(element);
                arrayList.add(objectElement);
            }

            return arrayList;
        }

        System.out.println("解析失败：" +  "最外层的 {}、[]格式错误");
        return null;
    }
}
