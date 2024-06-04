package cn.edu.sut.secruity.contest24;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Arrays;
import java.util.List;

public class Issuer {
    public Element[] ivk;
    public String ID;
    private Element[] isk;

    //发行方密钥生成算法
    public void IKeyGen(RAAScheme param, String identity, int n) {

        Element[] sk = new Element[n];
        Element[] vk = new Element[n];

        for (int i = 0; i < n; i++) {

            sk[i] = param.pairing.getZr().newRandomElement().getImmutable();
            vk[i] = param.g2.duplicate().powZn(sk[i]).getImmutable();

        }

        this.isk = sk;
        this.ivk = vk;
        this.ID = identity;

        System.out.println("the key pair of " + identity);
        for (int i = 0; i < ivk.length; i++) {
            System.out.println("isk[" + i + "] = " + isk[i]);
            System.out.println("ivk[" + i + "] = " + ivk[i]);
        }
        System.out.println();

    }


    /*凭证签发算法
      1.发行方首先验证用户关于usk的知识证明是否正确；
      2.关于usk的知识证明验证通过后，为此用户颁发一个凭证。
     */
    public Element CredIssue(RAAScheme param, Element[] proofs, Vehicle vehicle, Issuer issuer, String[] attr, Element[] list) {
        Element upk = vehicle.uvk;
        List<Element> elelist = Arrays.asList(list);
        boolean result = elelist.contains(upk);
        if (!result) {
            System.out.println("当前车辆未被撤销");
            System.out.println("开始验证车辆OBU发送的证明......");

            //将Element[]数组元素转成数组元素不可变的list数组形式
            //for example: Element[0] = 0, Element[1] = 1; list = [0,1]
            //List prr = Arrays.stream(proof).toList();
            //List list = Collections.unmodifiableList(prr);
            //System.out.println(list.get(1));//相当于输出Element[1]

            Element Rl = param.g1.duplicate().powZn(proofs[0]).getImmutable();
            Element Rr = vehicle.uvk.duplicate().powZn(proofs[1]).negate();

            Element RR = Rl.duplicate().mul(Rr).getImmutable();
            System.out.print("RR = ");
            System.out.println(RR);

            //Element conn = RR.duplicate().add(user.uvk);

            byte[] conn1 = issuer.ivk[0].toBytes();
            byte[] conn2 = vehicle.uvk.toBytes();
            byte[] conn3 = RR.toBytes();
            System.out.println(Arrays.toString(conn3));
            byte[] connt = new byte[conn1.length + conn2.length + conn3.length];
            System.arraycopy(conn1, 0, connt, 0, conn1.length);
            System.arraycopy(conn2, 0, connt, conn1.length, conn2.length);
            System.arraycopy(conn3, 0, connt, conn1.length + conn2.length, conn3.length);

            //Element cc = pairing.getZr().newElementFromHash(conn.toBytes(), 0, conn.getLengthInBytes());
            Element cc = param.pairing.getZr().newElementFromHash(connt, 0, connt.length);

            System.out.print("cc = ");
            System.out.println(cc);

            Element cred = null;
            if (cc.isEqual(proofs[1])) {
                System.out.println("验证成功");
                System.out.println("正在签发访问凭证..........");

                Element[] attribute = new Element[attr.length];
                Element[] ya = new Element[attr.length];
                for (int i = 1; i < attr.length; i++) {
                    byte[] att = attr[i].getBytes();
                    attribute[i] = param.pairing.getZr().newElementFromBytes(att, 0);//将属性转为Zr群上的元素
                    ya[i] = issuer.isk[i].duplicate().mul(attribute[i]).getImmutable();
                }

                Element expp = ya[1];
                for (int i = 2; i < attr.length; i++) {//
                    expp = expp.duplicate().add(ya[i]);

                }

                Element exp = issuer.isk[0].duplicate().add(expp).add(issuer.isk[issuer.isk.length - 1]);

                cred = vehicle.uvk.duplicate().powZn(exp).getImmutable();

                System.out.print("发行方" + issuer.ID + "签发的访问凭证cred " + "=" + " ");
                System.out.println(cred);
                System.out.println("Credential's length = " + cred.getLengthInBytes() + " " + "bytes.");


            } else {
                System.out.println("The proof of usk is wrong.");
            }

            return cred;

        } else {
            String res = "error!";
            return Util.StringToElement(param.pairing, res);
        }


        /* 未添加集合Lt的凭证签发算法
        Element[] proof = proofs;

        //将Element[]数组元素转成数组元素不可变的list数组形式
        //for example: Element[0] = 0, Element[1] = 1; list = [0,1]
        //List prr = Arrays.stream(proof).toList();
        //List list = Collections.unmodifiableList(prr);
        //System.out.println(list.get(1));//相当于输出Element[1]

        Element Rl = param.g1.duplicate().powZn(proof[0]).getImmutable();
        Element Rr = user.uvk.duplicate().powZn(proof[1]).negate();

        Element RR = Rl.duplicate().mul(Rr).getImmutable();
        System.out.print("RR = ");
        System.out.println(RR);

        //Element conn = RR.duplicate().add(user.uvk);

        byte[] conn1 = issuer.ivk[0].toBytes();
        byte[] conn2 = user.uvk.toBytes();
        byte[] conn3 = RR.toBytes();
        byte[] connt = new byte[conn1.length + conn2.length + conn3.length];
        System.arraycopy(conn1, 0, connt, 0, conn1.length);
        System.arraycopy(conn2, 0, connt, conn1.length, conn2.length);
        System.arraycopy(conn3, 0, connt, conn2.length, conn3.length);

        //Element cc = pairing.getZr().newElementFromHash(conn.toBytes(), 0, conn.getLengthInBytes());
        Element cc = param.pairing.getZr().newElementFromHash(connt, 0, connt.length);

        System.out.print("cc = ");
        System.out.println(cc);

        Element credential = null;

        if (cc.isEqual(proof[1])) {
            System.out.println("The proof of usk is right.");
            System.out.println("Issuing credential.....");

            Element[] attribute = new Element[attr.length];
            Element[] ya = new Element[attr.length];
            for (int i = 1; i < attr.length; i++) {
                byte[] att = attr[i].getBytes();
                attribute[i] = param.pairing.getZr().newElementFromBytes(att, 0);
                ya[i] = issuer.isk[i].duplicate().mul(attribute[i]).getImmutable();

            }

            Element expp = ya[1];
            for (int i = 2; i < attr.length; i++) {
                expp = expp.duplicate().add(ya[i]);

            }

            Element exp = issuer.isk[0].duplicate().add(expp).add(issuer.isk[issuer.isk.length - 1]);

            credential = user.uvk.duplicate().powZn(exp).getImmutable();

            System.out.print("Credential issued by" + " " + issuer.ID + " " + "=" + " ");
            System.out.println(credential);
            System.out.println("Credential's length = " + credential.getLengthInBytes() + " " + "bytes.");


        } else {
            System.out.println("The proof of usk is wrong.");
        }

        return credential;

    }
         */

    }

}
