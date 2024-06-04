import cn.edu.sut.secruity.contest24.param.Issuer1Param;
import cn.edu.sut.secruity.contest24.param.PublicParam;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class IssuerParamTest {
    private static final int attrSize = 2;
    private static final int size = attrSize + 2;
    private static final String file1Name = "issuer1.properties";
    private static final String file2Name = "issuer2.properties";

    @Test
    public void createParam1() {
        Properties properties = new Properties();
        OutputStream output = null;

        try {
            Field Zr = PublicParam.Zr;
            Element g2 = PublicParam.g2;

            //isk存储属性值
            for (int i = 0; i < 4; i++) {
                Element isk = Zr.newRandomElement();
                properties.setProperty("isk_" + i, ElementOperation.getElementString(isk));
                properties.setProperty("ivk_" + i, ElementOperation.getElementString(g2.powZn(isk)));
            }
            // 创建输出流
            output = Files.newOutputStream(Paths.get(file1Name));

            // 存储数据到properties文件
            properties.store(output, "Store Register Params");

            System.out.println("Data stored successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void readParam1() {
        Element[] isk = Issuer1Param.isk;
        Element[] ivk = Issuer1Param.ivk;

        System.out.println(PublicParam.g2.duplicate().powZn(isk[0]).isEqual(ivk[0]));
    }

    @Test
    public void createParam2() {
        Properties properties = new Properties();
        OutputStream output = null;

        try {
            Field Zr = PublicParam.Zr;
            Element g2 = PublicParam.g2;

            //存储属性值
            for (int i = 0; i < 4; i++) {
                Element isk = Zr.newRandomElement();
                properties.setProperty("isk_" + i, ElementOperation.getElementString(isk));
                properties.setProperty("ivk_" + i, ElementOperation.getElementString(g2.powZn(isk)));
            }

            // 创建输出流
            output = Files.newOutputStream(Paths.get(file2Name));

            // 存储数据到properties文件
            properties.store(output, "Store Register Params");

            System.out.println("Data stored successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void readParam2() {
        Element[] isk = Issuer1Param.isk;
        Element[] ivk = Issuer1Param.ivk;
        System.out.println(PublicParam.g2.duplicate().powZn(isk[0]).isEqual(ivk[0]));


    }

}
