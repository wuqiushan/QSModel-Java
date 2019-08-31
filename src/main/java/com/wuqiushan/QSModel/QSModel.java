package com.wuqiushan.QSModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map;
import java.util.Stack;

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

                // 设置值到对象里，设置前，把允许打开
                field.setAccessible(true);

                if (fieldName.equals("weight")) {

                    if ((mapValue.getClass() == Integer.class) || (mapValue.getClass() == int.class)) {

                        field.set(object, mapValue);
                    }
                    else if ((mapValue.getClass() == Double.class) || (mapValue.getClass() == double.class)) {

                        double fl = (double) ((int) 120);
                        field.set(object, fl);
                    }
                }

//                if (mapValue.getClass() != fieldType) {
//
                    if (fieldType == Integer.class) {
                        field.set(object, (Integer) mapValue);
                    }
                    else if (fieldType == double.class) {
                        float fl = (float) 120.0;
                        field.set(object, fl);
                    }
//                }
//                else {
//                    field.set(object, mapValue);
//                }
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
}
