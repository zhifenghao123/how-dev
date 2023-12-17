package com.howdev.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FileReadUtil class
 *
 * @author haozhifeng
 * @date 2023/09/09
 */
public class FileReadUtil {
    public static InputStream getStreamByFileName(String fileName) throws IOException {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName should not be null!");
        }

        if (fileName.startsWith("http")) {
            // 网络地址
            URL url = new URL(fileName);
            return url.openStream();
        } else if (fileName.startsWith("/")) {
            // 绝对路径
            Path path = Paths.get(fileName);
            return Files.newInputStream(path);
        } else { // 相对路径
            return FileUtil.class.getClassLoader().getResourceAsStream(fileName);
        }
    }


    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件
     *
     * @param fileName 文件的名
     * @return:
     * @author: haozhifeng
     */
    public static InputStream getFileInputStream(String fileName) throws IOException {
        File file = new File(fileName);
        return new FileInputStream(file);
    }


    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     *
     * @param fileName fileName
     * @return:
     * @author: haozhifeng
     */
    public static Reader getInputStreamReader(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        return new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
    }


    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     *
     * @param fileName fileName
     * @return:
     * @author: haozhifeng
     */
    public static BufferedReader getBufferedReader(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        //        return new BufferedReader(new FileReader(file));
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
    }


    /**
     * readByByte
     * 一个字节一个字节读
     *
     * @param fileName fileName
     * @return:
     * @author: haozhifeng
     */
    public static String readByByte(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);

        String content;
        int ch = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while((ch = fis.read()) != -1){
            byteArrayOutputStream.write(ch);
        }
        // 将 ByteArrayOutputStream 转换为 byte[]
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return new String(byteArray);
    }


    public static String readByByteArray(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len = 0;
        while((len = fis.read(buffer)) != -1){
            byteArrayOutputStream.write(buffer,0,len);
        }
        // 将 ByteArrayOutputStream 转换为 byte[]
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return new String(byteArray);
    }

    public static String readByReadLine(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String string = null;
        StringBuffer stringBuffer = new StringBuffer();
        while((string = br.readLine()) != null){
            stringBuffer.append(string);
        }
        br.close();
        return  stringBuffer.toString();
    }

    public static void main(String[] args) {
        try {
            String str = readByByte("./how-dev-common/data_dir/writeToFileWithFiles.txt");
            System.out.println(str);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


}
