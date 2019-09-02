package com.wuqiushan.QSModel;

import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map;

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

                if (fieldName.equals("addressA")) {
//                    mapValue = (int)120;
                    System.out.println("test");
                }
                if (fieldName.equals("courses")) {
                    Type genericType = field.getGenericType();
                    System.out.println("test");
                }
                if (fieldName.equals("coursesA")) {
                    System.out.println("test");
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
                    if ((int)mapValue == -1) {

                        mapValue = map.get(fieldName);

                        if ((mapValue.getClass() == LinkedHashMap.class) ||
                                (mapValue.getClass() == HashMap.class) ||
                                (mapValue.getClass() == TreeMap.class) ||
                                (mapValue.getClass() == LinkedTreeMap.class)) {
                            System.out.println("来了");
                            mapValue = null;
                            System.out.println("转化了");
                        }
                        else {

                            // 为Map类型的话
                            if (map instanceof Map) {
                                mapValue = qs_modelWithMap((Map<String, Object>) mapValue, fieldType);
                            }
                        }
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
    public static <T, E> Object convertType(Class<T> targetT, Class<E> orgT, Object orgValue) throws Exception {

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
}
