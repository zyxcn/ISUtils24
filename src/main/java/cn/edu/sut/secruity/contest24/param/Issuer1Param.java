package cn.edu.sut.secruity.contest24.param;

import cn.edu.sut.secruity.contest24.SecurityBaseTest;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Issuer1Param {
    private static final int attrSize = 2;
    private static final int size = attrSize + 2;
    private static final String fileName = "issuer1.properties";

    public static Element[] isk;
    public static Element[] ivk;
    public static String[] attribute = {"发行方1属性1", "发行方1属性2"};

    static {
        isk = new Element[size];
        ivk = new Element[size];

        Properties properties = new Properties();
        InputStream input = null;

        try {
            // 加载properties文件
            input = SecurityBaseTest.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(input);

            //rsk读取属性值
            for (int i = 0; i < size; i++) {
                String iskStr = properties.getProperty("isk_" + i);
                isk[i] = ElementOperation.getElementFromString(iskStr).getImmutable();
            }

            //rvk读取属性值
            for (int i = 0; i < size; i++) {
                String ivkStr = properties.getProperty("ivk_" + i);
                ivk[i] = ElementOperation.getElementFromString(ivkStr).getImmutable();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
