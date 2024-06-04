import cn.edu.sut.secruity.contest24.param.PublicParam;
import cn.edu.sut.secruity.contest24.param.RegisterParam;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class RegisterParamTest {
    private static final String fileName = "register.properties";
    private static final int size = 4;

    @Test
    public void createParam() {
        Properties properties = new Properties();
        OutputStream output = null;

        try {
            Field G1 = PublicParam.G1;
            Field G2 = PublicParam.G2;
            Field Zr = PublicParam.Zr;
            Element g2 = PublicParam.g2.duplicate();
            Element g1 = PublicParam.g1.duplicate();
            //rsk存储属性值
            for (int i = 0; i < 4; i++) {
                Element rsk = Zr.newRandomElement();
                String rskStr = ElementOperation.getElementString(rsk);
                properties.setProperty("rsk_" + i, rskStr);
                Element rvk = g2.duplicate().powZn(rsk);
                String rvkStr = ElementOperation.getElementString(rvk);
                properties.setProperty("rvk_" + i, rvkStr);
            }


            Element rtsk = Zr.newRandomElement();
            String rtskStr = ElementOperation.getElementString(rtsk);
            properties.setProperty("rtsk", rtskStr);
            Element rtvk = g1.duplicate().powZn(rtsk);
            String rvkStr = ElementOperation.getElementString(rtvk);
            properties.setProperty("rtvk", rvkStr);
            // 创建输出流
            output = Files.newOutputStream(Paths.get(fileName));

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
    public void readParam() {
        Element rtvk = RegisterParam.rtvk;
        Element rtsk = RegisterParam.rtsk;
        System.out.println(PublicParam.g1.powZn(rtsk).isEqual(rtvk));
    }
}
