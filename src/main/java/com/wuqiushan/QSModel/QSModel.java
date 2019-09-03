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
     * Map 转 Model
     * @param map  Json字典
     * @param targetClass 目标类的类型
     * @param <T> 任意泛形，一定是个引用类型，而不是值类型
     * @return 返回转换后的对象
     */
    public static <T extends Object> T qs_modelWithMap(Map<String, Object> map, Class<T> targetClass) {

        if ( (map == null) || (targetClass == null) ) { return null; }
        T object = null;
        try {

            // 反射实例化对象
            object = targetClass.getConstructor().newInstance();

            // 通过反射循环获取 本类 -> 父类 -> ... -> Object 所有的字段(包括private成员变量)
            ArrayList<Field> fields = new ArrayList<>();
            Class currentClass = targetClass;

            while (currentClass != Object.class) {

                /**
                 * 1.获取当前类的成员变量
                 * 2.把成员变量存入ArrayList中
                 * 3.指向(相对本类的)父类
                 */
                Field[] fieldT = currentClass.getDeclaredFields();
                fields.addAll(Arrays.asList(fieldT));

                currentClass = currentClass.getSuperclass();
            }

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

                // 设置值到对象里，设置前，把允许打开
                field.setAccessible(true);
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
                                (mapValue.getClass() == TreeMap.class)) {

                            System.out.println(mapValue.getClass() + "转化不了");
                            mapValue = null;
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

                    // 非普通数组 获取元素类型字符串，转化为类型
                    if (!targetType.equals("")) {

                        fieldType = Class.forName(targetType);

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
                                        (elementTmp.getClass() == TreeMap.class)) {

                                    System.out.println(elementTmp.getClass() + "转化不了");
                                    elementTmp = null;
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
            /** 分隔前用把 {、[、]、} 的部分替换成 %(时间错)% 原身存放字典 */
            HashMap<String, String> subStrMap = splitMaxMatches(strBuilder, "[\\[\\]{}]{1}");

            // 分隔
            String[] strings = strBuilder.toString().split(",");

            for (String element : strings) { // "xx":xx

                /** 1.if(正则 "xx":"xx",) 为字符串String及中文 (频度大放最前面，减少判断开销) */
                if (element.matches("^\"\\w+\":\".*?\"")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index + 2, element.length() - 1);
                    hashMap.put(key, value);
                }
                /** 2.if(正则 "xx":[.0-9]],) 为浮点Number  优先匹配因为[0-9]容易把该类型匹配走 */
                else if(element.matches("^\"\\w+\":\\d+[.]{1}\\d+")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index + 1, element.length());
                    Double valueDouble = Double.parseDouble(value);
                    hashMap.put(key, valueDouble);
                }
                /** 3.if(正则 "xx":[0-9],) 为整形Number */
                else if(element.matches("^\"\\w+\":\\d+")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index + 1, element.length());
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
                /** if(正则 "xx":%xxxxx) 为[、{、}、] (即: 数组或者字典时) 递归 */
                else if (element.matches("^\"\\w+\":%\\w+%")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    String value = element.substring(index + 1, element.length());
                    /** 把原身值拿到 */
                    String subStr = subStrMap.get(value);
                    if (subStr != null) {
                        Object subObject = qs_objectWithString(subStr);
                        hashMap.put(key, subObject);
                    }
                }
                /** 8.if(正则 "xx":"null") 为null */
                else if(element.matches("^\"\\w+\":null")) {

                    int index    = element.indexOf(":");
                    String key   = element.substring(1, index - 1);
                    hashMap.put(key, null);
                }
                else {
                    System.out.println("解析失败：" + element + " 格式错误");
                    return null;
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

            /** {xx, xx}, {xx, xx}, {xx, xx} => {%UUID%: value, %UUID%: value} 字典 */
            HashMap<String, String> subArrayMap = splitMaxMatches(strBuilder, "[\\[\\]{}]{1}");

            for (String element : subArrayMap.values()) {
                Object objectElement = qs_objectWithString(element);
                arrayList.add(objectElement);
            }

            return arrayList;
        }

        System.out.println("解析失败：" +  "最外层的 {}、[]格式错误");
        return null;
    }

    /**
     * 查找并替换成 "%UUID%" 例： "courses":[{"key":"语文"}, {"key":"数字"}]  => "courses":%UUID%
     * 通过 %UUID% 从返回字典的里的查到替换的原始字符串 [{"key":"语文"}, {"key":"数字"}]
     *
     * @param text  需要匹配的字符串
     * @param regex 匹配的正则表达式
     * @return 返回 匹配后，替换的原始字符串，例：{"%UUID%": [{"key":"语文"}, {"key":"数字"}], ... };
     */
    public static HashMap<String, String> splitMaxMatches(StringBuilder text, String regex) {

        /** 存放匹配到的字符 字典，"%UUID%":[{[xxx]}] */
        HashMap<String, String> results = new HashMap<>();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        /** 采用堆栈是因为，这个场景堆栈更好用 */
        Stack<String> stack = new Stack<>();
        /** 保存位置信息，这里用堆栈是因为从字符后面开始换成%UUID%的话，不影响字符串前面
         *  <String, Integer> startIndex：代表起始位置
         *                    endIndex: 代表结束位置
         * */
        Stack<HashMap<String, Integer>> position = new Stack<>();
        // 记录起始位置
        Integer recordStartIndex = -1;

        while (matcher.find()) {

            // 含有[{ 就push, 含有}]且栈顶与此相等就弹出，否则格式错误
            String matcherValue = matcher.group();
            String peek = "";
            if (!stack.empty()) {
                peek = stack.peek();
            }
            if (matcherValue.equals("[") || matcherValue.equals("{")) {

                /** 当堆栈有首个值时，记录起始位置 */
                if (stack.empty()) {
                    recordStartIndex = matcher.start();
                }
                stack.push(matcherValue);
            }
            else if ( (peek.equals("{") && (matcherValue.equals("}"))) ||
                    (peek.equals("[") && (matcherValue.equals("]"))) ) {

                /** 当没有记录值 直接来这里会出错 */
                if (stack.empty() || (recordStartIndex == -1)) {
                    System.out.println("解析失败，格式错误");
                    return null;
                }
                stack.pop();

                /** 当堆栈无任何值时，记录结束位置，注意此时不能直接替换字符，会影响整体位置 */
                if (stack.empty()) {

                    // 把位置信息先存入 位置堆栈中
                    HashMap<String, Integer> map = new HashMap<>();
                    map.put("startIndex", recordStartIndex);
                    map.put("endIndex", matcher.end());
                    position.push(map);

                    // 复位记录值
                    recordStartIndex = -1;
                }
            }
            else {
                System.out.println("解析失败，格式错误");
                return null;
            }
        }

        /** 如果最后不为空，即括号没有一一对应，错误处理 */
        if (!stack.empty()) {
            return null;
        }

        /** 从堆栈的顶部取位置对象(即从后往前替换)，保证了前面替换时位置安全，替身为%UUID% */
        while (!position.empty()) {
            HashMap<String, Integer> indexMap = position.pop();
            int startIndex = indexMap.get("startIndex");
            int endIndex = indexMap.get("endIndex");
            if ((startIndex <= endIndex) && (endIndex <= text.length())) {

                /**
                 * 1.不使用，时间错(String.valueOf(new Date().getTime()))，因时间错精确到ms，
                 * 2.如果程序在1ms完成，此时生成的key都会相同，存入字典时会覆盖。现改成UUID，这样保证唯一
                 * 3.替换掉指定的位置
                 * 4.添加到字典中
                 * */
                String subText = text.substring(startIndex, endIndex);
                String timeText = "%" + UUID.randomUUID().toString().replace("-", "") + "%";
                text = text.replace(startIndex, endIndex, timeText);
                results.put(timeText, subText);
            }
            else {
                return null;
            }
        }
        return results;
    }
}
