package com.howdev.common.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * FileWriteUtil class
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
public class FileWriteUtil {

    /**
     * 使用 FileWriter 写文件
     *
     * FileWriter 属于「字符流」体系中的一员，也是文件写入的基础类，它包含 5 个构造函数，可以传递一个具体的文件位置，或者 File 对象，
     * 第二参数表示是否要追加文件，默认值为 false 表示重写文件内容，而非追加文件内容
     *
     * @param filepath 文件目录
     * @param content  待写入内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithFileWriter(String filepath, String content) throws IOException {
        // 关于资源释放的问题：在 JDK 7 以上的版本，我们只需要使用 try-with-resource 的方式就可以实现资源的释放，
        // 就比如使用 try (FileWriter fileWriter = new FileWriter(filepath)) {...} 就可以实现 FileWriter 资源的自动释放。
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            fileWriter.append(content);
        }
    }


    /**
     * 使用 BufferedWriter 写文件
     *
     * BufferedWriter 也属于字符流体系的一员，与 FileWriter 不同的是 BufferedWriter自带缓冲区，因此它写入文件的性能更高。
     * 缓冲区又称为缓存，它是内存空间的一部分。也就是说，在内存空间中预留了一定的存储空间，这些存储空间用来缓冲输入或输出的数据，这部分预留的空间就叫做缓冲区。
     * 缓冲区的优势:
     * 以文件流的写入为例，如果我们不使用缓冲区，那么每次写操作 CPU 都会和低速存储设备也就是磁盘进行交互，
     * 那么整个写入文件的速度就会受制于低速的存储设备(磁盘)。但如果使用缓冲区的话，每次写操作会先将数据保存在高速缓冲区内存上，
     * 当缓冲区的数据到达某个阈值之后，再将文件一次性写入到磁盘上。因为内存的写入速度远远大于磁盘的写入速度，所以当有了缓冲区之后，
     * 文件的写入速度就被大大提升了。
     *
     * 论是 PrintWriter 还是 BufferedWriter 都必须基于 FileWriter 类来完成调用
     *
     * @param filepath 文件目录
     * @param content  待写入内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithBufferedWriter(String filepath, String content) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath))) {
            bufferedWriter.write(content);
        }
    }


    /**
     * 使用 PrintWriter 写文件
     *
     * PrintWriter 也属于字符流体系中的一员，它虽然叫“字符打印流”，但使用它也可以实现文件的写入
     *
     * 论是 PrintWriter 还是 BufferedWriter 都必须基于 FileWriter 类来完成调用
     *
     * @param filepath 文件目录
     * @param content  待写入内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithPrintWriter(String filepath, String content) throws IOException {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(filepath))) {
            printWriter.print(content);
        }
    }

    /**
     * 使用 FileOutputStream 写文件
     *
     * @param filepath 文件目录
     * @param content  待写入内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithFileOutputStream(String filepath, String content) throws IOException {
        byte[] bytes = content.getBytes();
        writeToFileWithFileOutputStream(filepath, bytes);
    }

    /**
     * 使用 FileOutputStream 写文件
     *
     * @param filepath 文件目录
     * @param bytesData  待写入字节数据内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithFileOutputStream(String filepath, byte[] bytesData) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filepath)) {
            fileOutputStream.write(bytesData);
        }
    }


    /**
     * 使用 BufferedOutputStream 写文件
     *
     * @param filepath 文件目录
     * @param content  待写入内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithBufferedOutputStream(String filepath, String content) throws IOException {
        writeToFileWithBufferedOutputStream(filepath, content.getBytes());
    }

    /**
     * 使用 BufferedOutputStream 写文件
     *
     * @param filepath 文件目录
     * @param bytesData  待写入内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithBufferedOutputStream(String filepath, byte[] bytesData) throws IOException {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                new FileOutputStream(filepath))) {
            bufferedOutputStream.write(bytesData);
        }
    }


    /**
     * 使用 Files 写文件
     * <p>
     * Files 类是 JDK 7 添加的新的操作文件的类，它提供了提供了大量处理文件的方法，例如文件复制、读取、写入，获取文件属性、快捷遍历文件目录等，
     * 这些方法极大的方便了文件的操作
     *
     * @param filepath 文件目录
     * @param content  待写入内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithFiles(String filepath, String content) throws IOException {
        Files.write(Paths.get(filepath), content.getBytes());
    }

    /**
     * 使用 Files 写文件
     * <p>
     * Files 类是 JDK 7 添加的新的操作文件的类，它提供了提供了大量处理文件的方法，例如文件复制、读取、写入，获取文件属性、快捷遍历文件目录等，
     * 这些方法极大的方便了文件的操作
     *
     * @param filepath 文件目录
     * @param bytesData  待写入内容
     * @throws IOException
     * @return:
     * @author: haozhifeng
     */
    public static void writeToFileWithFiles(String filepath, byte[] bytesData) throws IOException {
        Files.write(Paths.get(filepath), bytesData);
    }


    public static void main(String[] args) {
        try {
            writeToFileWithFileWriter("./how-dev-common/data_dir/writeToFileWithFileWriter.txt", "我的天");
            writeToFileWithBufferedWriter("./how-dev-common/data_dir/writeToFileWithBufferedWriter.txt", "我的天");
            writeToFileWithPrintWriter("./how-dev-common/data_dir/writeToFileWithPrintWriter.txt", "我的天");
            writeToFileWithFileOutputStream("./how-dev-common/data_dir/writeToFileWithFileOutputStream.txt", "我的天");
            writeToFileWithBufferedOutputStream("./how-dev-common/data_dir/test.txt", "我的天");
            writeToFileWithFiles("./how-dev-common/data_dir/writeToFileWithFiles.txt", "我的天");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
