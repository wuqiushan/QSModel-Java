package com.wuqiushan.testModel;

import java.io.*;

public class ReadJsonFile {

    public static String readJsonFile() {

        StringBuilder strBuffer = new StringBuilder();
        try {

            // 读路径下的文件
            InputStream ins = new FileInputStream(new File(Thread.currentThread().getContextClassLoader().getResource("Student.json").getFile()));
            InputStreamReader inputStreamReader = new InputStreamReader(ins, "UTF-8");
            BufferedReader in = new BufferedReader(inputStreamReader);

            String str;
            while ((str = in.readLine()) != null) {
                strBuffer.append(str);
            }
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strBuffer.toString();
    }
}
