package cn.edu.sut.secruity.contest24.param;

import cn.edu.sut.secruity.contest24.SecurityBaseTest;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RegisterParam {
    private static final String fileName = "register.properties";
    private static final int size = 4;

    public static Element[] rvk;
    public static Element rtvk;

    public static Element[] rsk;
    public static Element rtsk;


    static {

        Properties properties = new Properties();
        InputStream input = null;

        rvk = new Element[size];
        rsk = new Element[size];

        try {
            // 加载properties文件
            input = SecurityBaseTest.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(input);

            //rsk读取属性值
            for (int i = 0; i < size; i++) {
                String rskStr = properties.getProperty("rsk_" + i);
                rsk[i] = ElementOperation.getElementFromString(rskStr).getImmutable();
            }

            //rvk读取属性值
            for (int i = 0; i < size; i++) {
                String rvkStr = properties.getProperty("rvk_" + i);
                rvk[i] = ElementOperation.getElementFromString(rvkStr).getImmutable();
            }

            //rtsk获取属性值
            String rtskStr = properties.getProperty("rtsk");
            rtsk = ElementOperation.getElementFromString(rtskStr).getImmutable();

            String rtvkStr = properties.getProperty("rtvk");
            rtvk = ElementOperation.getElementFromString(rtvkStr).getImmutable();

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
