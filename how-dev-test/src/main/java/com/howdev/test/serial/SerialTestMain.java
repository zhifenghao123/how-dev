package com.howdev.test.serial;

import com.howdev.test.model.Person;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * SerialTestMain class
 *
 * @author haozhifeng
 * @date 2023/07/13
 */
public class SerialTestMain {
    public static void main(String[] args) {
        //serial();
        deSerial();
    }

    public static void serial() {
        Person person = new Person();
        person.setName("haozhifeng");
        person.setAge(25);

        OutputStream outputStream = null;
        System.out.println(System.getProperty("user.dir"));
        try {
            outputStream = new FileOutputStream(System.getProperty("user.dir") + "/how-dev-test/data_dir/person.txt");

            ObjectOutputStream objectOutputStream = null;
            objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(person);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deSerial() {

        InputStream inputStream = null;
        Person person = null;
        try {
            inputStream = new FileInputStream(System.getProperty("user.dir") + "/how-dev-test/data_dir/person.txt");

            ObjectInputStream objectInputStream = null;
            objectInputStream = new ObjectInputStream(inputStream);

            person = (Person)objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(person);
    }

}
