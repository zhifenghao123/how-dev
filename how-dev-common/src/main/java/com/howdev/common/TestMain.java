package com.howdev.common;

import com.howdev.common.testmodel.RequestData;
import com.howdev.common.util.EmojiUtil;
import com.howdev.common.util.JacksonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * TestMain class
 *
 * @author haozhifeng
 * @date 2023/03/21
 */
public class TestMain {
    public static void main(String[] args) {
        String str =
        "{\n" +
                "    \"type\":\"async\",\n" +
                "    \"reqId\":\"1111111111\",\n" +
                "    \"params\":{\n" +
                "        \"kuaishou_loan_user_id\":\"465340806850291026\",\n" +
                "        \"seller_type\":\"\\u4e2a\\u4eba\\u5356\\u5bb6\",\n" +
                "        \"seller_name\":\"\\u5c1a\\u5251\\u51ef\",\n" +
                "        \"shop_name\":\"KILLER\\\\\\ud83e\\udd66\\u7684\\u5c0f\\u5e97\",\n" +
                "        \"shop_score\":\"4.50\",\n" +
                "        \"shop_rank\":\"\\u4e2d\",\n" +
                "        \"shop_status\":\"\\u6b63\\u5e38\"\n" +
                "    }\n" +
                "}";


        //String str2 = readFileContent("../../resources/testfile.txt");



        RequestData requestData = JacksonUtil.toObject(str, RequestData.class);

        System.out.println(requestData);

        Map emojiReplaceMap = EmojiUtil.emojiReplaceMap(JacksonUtil.toJson(requestData.getParams()));

        System.out.println(emojiReplaceMap);


    }

    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }
}
