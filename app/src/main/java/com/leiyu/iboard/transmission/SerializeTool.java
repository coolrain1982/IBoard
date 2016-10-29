package com.leiyu.iboard.transmission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by leiyu on 2016/10/25.
 */

public class SerializeTool {

    private static BASE64Encoder encode = new BASE64Encoder();
    private static BASE64Decoder decode = new BASE64Decoder();

    public static String object2String(Object o) {
        String objBody = null;
        ByteArrayOutputStream baops = null;
        ObjectOutputStream oos = null;

        try {
            baops = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baops);
            oos.writeObject(o);
            byte[] bytes = baops.toByteArray();
            objBody = encode.encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (Exception e1) {}

            try {
                baops.close();
            } catch (Exception e1) {}
        }

        return objBody;
    }

    public static <T extends Serializable> T getObjectFromString(String objBody, Class<T> clazz) {
        ObjectInputStream ois = null;
        T obj = null;

        try {
            byte[] bytes = decode.decodeBuffer(objBody);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                ois.close();
            } catch (Exception e1) {}
        }

        return obj;
    }
}
