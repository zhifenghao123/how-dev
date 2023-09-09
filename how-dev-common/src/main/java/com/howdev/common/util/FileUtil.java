package com.howdev.common.util;

import java.io.File;
import java.io.IOException;

/**
 * FileUtil class
 *
 * @author haozhifeng
 * @date 2023/09/09
 */
public class FileUtil {
    /**
     * 创建目录
     * mkdirs 和 mkdir 都是 Java 中的文件操作方法，它们都可以用来创建目录。但是，它们之间有一个重要的区别：
     * mkdir 只能在当前目录中创建单个目录。如果给定的路径中存在父目录，而该父目录并不存在，则 mkdir 方法将抛出 IOException 异常。
     * mkdirs 可以在当前目录中创建单个目录或多个目录。如果给定的路径中存在父目录，而该父目录并不存在，则 mkdirs 方法会自动创建所有父目录。
     * 因此，如果需要在当前目录中创建多级目录，则应使用 mkdirs 方法。
     *
     * @param destDirName destDirName
     * @return:
     * @author: haozhifeng
     */
    public boolean createDirectory(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
            System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }

    /**
     * 创建文件
     * 
     * @param destFileName destFileName
     * @return: 
     * @author: haozhifeng     
     */
    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                System.out.println("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                System.out.println("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }

}
