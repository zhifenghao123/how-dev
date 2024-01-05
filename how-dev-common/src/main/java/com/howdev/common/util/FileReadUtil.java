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
 * Java 中的流（Stream）分为两种类型：字节流和字符流。
 * 字节流以字节为单位，字符流以字符为单位。
 * 字节流以 InputStream 和 OutputStream 为基类，字符流以 Reader 和 Writer 为基类。
 *
 * （1）字节流（Byte Stream）
 * 字节流可以处理任何类型的数据，但是它们是以字节为单位进行操作的。Java 中提供了两种字节流：InputStream 和 OutputStream。
 *
 * InputStream 是字节输入流，用于从数据源读取数据。InputStream 的常用子类包括：
 *  1）FileInputStream：用于从文件中读取数据。
 *  2）ByteArrayInputStream：用于从字节数组中读取数据。
 *  3）PipedInputStream：用于从管道中读取数据。
 *
 *  OutputStream 是字节输出流，用于将数据写入到目标位置。OutputStream 的常用子类包括：
 *   1）FileOutputStream：用于将数据写入文件。
 *   2）ByteArrayOutputStream：用于将数据写入字节数组。
 *   3）PipedOutputStream：用于将数据写入管道。
 *
 * （2）字符流（Character Stream）
 * 字符流用于处理字符数据，它们是以字符为单位进行操作的。Java 中提供了两种字符流：Reader 和 Writer。
 *
 * Reader 是字符输入流，用于从数据源读取字符，Reader 的常用子类包括：
 *  1）FileReader：用于从文件中读取字符。
 *  2）CharArrayReader：用于从字符数组中读取字符。
 *  3）StringReader：用于从字符串中读取字符。
 *
 * Writer 是字符输出流，用于将字符写入到目标位置，Writer 的常用子类包括：
 *  1）FileWriter：用于将字符写入到文件。
 *  2）CharArrayWriter：用于将字符写入到字符数组中。
 *  3）StringWriter：用于将字符写入到字符串中。
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
     * 一个字节一个字节读取，返回文件内容字符串
     *
     * @param fileName fileName
     * @return:
     * @author: haozhifeng
     */
    public static String readStringByByte(String fileName) throws IOException {
        byte[] bytes = readByByte(fileName);
        return new String(bytes);
    }

    /**
     * 一个字节一个字节读取，返回文件内容字节数组
     *
     * @param fileName fileName
     * @return:
     * @author: haozhifeng
     */
    public static byte[] readByByte(String fileName) throws IOException {
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
        return byteArray;
    }


    /**
     * 一个字节数组一个字节数组的读取文件内容，返回文件内容字符串
     *
     * @param fileName fileName
     * @return:
     * @author: haozhifeng
     */
    public static String readStringByByteArray(String fileName) throws IOException {
        byte[] bytes = readByByteArray(fileName);
        return new String(bytes);
    }
    /**
     * 一个字节数组一个字节数组的读取文件内容，返回文件内容字节数组
     *
     * @param fileName fileName
     * @return:
     * @author: haozhifeng
     */
    public static byte[] readByByteArray(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len = 0;
        while((len = fis.read(buffer)) != -1){
            byteArrayOutputStream.write(buffer,0, len);
        }
        // 将 ByteArrayOutputStream 转换为 byte[]
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return byteArray;
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
            String str = readStringByByte("./how-dev-common/data_dir/writeToFileWithFiles.txt");
            System.out.println(str);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


}
