package cn.edu.sut.secruity.contest24;

import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;

public class ServiceProvider {

    public Element pvk;
    private Element psk;

    //将TA生成的密钥对作为服务提供商的公私钥
    public void KeySet(RAAScheme param, Element[] keypair) {
        this.psk = keypair[0];
        this.pvk = keypair[1];

        System.out.println();
        System.out.println("the psk of the service provider = " + psk);
        System.out.println("the pvk of the service provider = " + pvk);
    }

    public Element[] PreCompute(RAAScheme param, RegistrationCenter ta, String t) {//计算D2，D3
        Element D2_p2 = param.pairing.pairing(param.g__1, param.g2).getImmutable();
        Element D2_p3 = param.pairing.pairing(param.g1, param.g2).getImmutable();
        Element D2_p4 = param.pairing.pairing(ta.rrk, ta.tvk[3]).getImmutable();
        Element D2_p5 = param.pairing.pairing(ta.rrk, param.g2).getImmutable();
        Element D2_p6 = param.pairing.pairing(param.g_1, param.g2).getImmutable();

        Element epoch_t = Util.StringToElement(param.pairing, t).getImmutable();
        Element D3_p1 = param.pairing.pairing(param.g1, ta.tvk[1]).getImmutable();
        Element D3_p2 = param.pairing.pairing(param.g1, ta.tvk[3]).getImmutable();
        Element D3_p3 = D2_p3.getImmutable();
        Element D3_p4 = param.pairing.pairing(param.g1, ta.tvk[0].duplicate().mul(ta.tvk[2].powZn(epoch_t))).getImmutable();

        Element[] precom = {D2_p2, D2_p3, D2_p4, D2_p5, D2_p6, D3_p1, D3_p2, D3_p3, D3_p4};
        return precom;

    }

    public void Verify(RAAScheme param, RegistrationCenter ta, Element[] proof, Element[][] ivk, String[][] attr, String t, String mes) {

        //计算D_1
        Element[][] attribute = new Element[attr.length][attr[1].length];

        for (int j = 1; j < attr.length; j++) {
            for (int i = 1; i < attr[j].length; i++) {
                byte[] att = attr[j][i].getBytes();
                attribute[j][i] = param.pairing.getZr().newElementFromBytes(att, 0);
            }
        }

        Element rig0 = ivk[1][1].duplicate().powZn(attribute[1][1]);

        for (int i = 2; i < ivk[1].length - 1; i++) {
            rig0 = rig0.duplicate().mul(ivk[1][i].duplicate().powZn(attribute[1][i]));//不知道ivk[1][]签名的属性个数，计算ivk1,i^a1,i
        }

        Element rigr = ivk[1][0].duplicate().mul(rig0).getImmutable();//计算ivk1,0*ivk1,i^a1,i
        Element rigg = rigr.duplicate().mul(ivk[1][ivk[1].length - 1]);//计算j=1下的ivk1,0*ivk1,i^a1,i*ivk1,ivk1.length-1

        for (int j = 2; j < ivk.length; j++) {
            Element r1 = ivk[j][1].duplicate().powZn(attribute[j][1]);
            for (int i = 2; i < ivk[j].length - 1; i++) {
                r1 = r1.duplicate().mul(ivk[j][i].duplicate().powZn(attribute[j][i]));//计算出j下的ivkj,i^aj,i;
            }
            Element r2 = ivk[j][0].duplicate().mul(r1).getImmutable();
            Element r3 = r2.duplicate().mul(ivk[j][ivk[j].length - 1]);
            rigg = rigg.duplicate().mul(r3).getImmutable();//将j个凭证值连乘
        }

        Element D1_r1 = param.pairing.pairing(proof[2].duplicate().powZn(proof[5]), rigg).getImmutable();
        Element D1_r2 = param.pairing.pairing(proof[3].duplicate().powZn(proof[4]).negate(), param.g2).getImmutable();
        Element D_1 = D1_r1.duplicate().mul(D1_r2).getImmutable();

        D_1 = param.pairing.getGT().newRandomElement();

        Element[] prevalue = PreCompute(param, ta, t);

        //计算D_2
        Element D2_r1 = param.pairing.pairing(proof[0].duplicate().powZn(proof[6]), param.g2).getImmutable();
        Element D2_r2 = prevalue[0].duplicate().powZn(proof[7]).getImmutable();
        Element D2_r3 = prevalue[1].duplicate().powZn(proof[5]).getImmutable();
        Element D2_r4 = prevalue[2].duplicate().powZn(proof[8]).getImmutable();
        Element D2_r5 = prevalue[3].duplicate().powZn(proof[9]).getImmutable();
        Element D2_r6_1 = param.pairing.pairing(proof[0], ta.tvk[3]).getImmutable();
        Element D2_r6_2 = prevalue[4].getImmutable();
        Element D2_r6 = D2_r6_1.duplicate().div(D2_r6_2).powZn(proof[4]).negate().getImmutable();
        Element D_2 = D2_r1.duplicate().mul(D2_r2).mul(D2_r3).mul(D2_r4).mul(D2_r5).mul(D2_r6).getImmutable();

        //计算D_3
        Element D3_r1 = prevalue[5].duplicate().powZn(proof[7]).getImmutable();
        Element D3_r2 = prevalue[6].duplicate().powZn(proof[10]).getImmutable();
        Element D3_r3 = prevalue[7].duplicate().powZn(proof[8]).getImmutable();
        Element D3_r4_1 = param.pairing.pairing(proof[1], param.g2).getImmutable();
        Element D3_r4_2 = prevalue[8].getImmutable();
        Element D3_r4 = D3_r4_1.duplicate().div(D3_r4_2).powZn(proof[4]).negate().getImmutable();
        Element D_3 = D3_r1.duplicate().mul(D3_r2).mul(D3_r3).mul(D3_r4).getImmutable();

        //计算D_4
        Element D4_r1 = param.g1.duplicate().powZn(proof[8]).getImmutable();
        Element D4_r2 = proof[2].duplicate().powZn(proof[4]).negate().getImmutable();
        Element D_4 = D4_r1.duplicate().mul(D4_r2).getImmutable();

        //将消息转成byte数组元素
        Element mess = Util.StringToElement(param.pairing, mes);

        //计算cc
        byte[] b_phi_1 = proof[0].toBytes();
        byte[] b_phi_2 = proof[1].toBytes();
        byte[] b_phi_3 = proof[2].toBytes();
        byte[] b_credagg_ran = proof[3].toBytes();
        byte[] b_D_1 = D_1.toBytes();
        byte[] b_D_2 = D_2.toBytes();
        byte[] b_D_3 = D_3.toBytes();
        byte[] b_D_4 = D_4.toBytes();
        byte[] b_mess = mess.toBytes();


        byte[] bytes = new byte[b_phi_1.length + b_phi_2.length + b_phi_3.length + b_credagg_ran.length + b_D_1.length + b_D_2.length + b_D_3.length + b_D_4.length + b_mess.length];
        System.arraycopy(b_phi_1, 0, bytes, 0, b_phi_1.length);
        System.arraycopy(b_phi_2, 0, bytes, b_phi_1.length, b_phi_2.length);
        System.arraycopy(b_phi_3, 0, bytes, b_phi_2.length, b_phi_3.length);
        System.arraycopy(b_credagg_ran, 0, bytes, b_phi_3.length, b_credagg_ran.length);
        System.arraycopy(b_D_1, 0, bytes, b_credagg_ran.length, b_D_1.length);
        System.arraycopy(b_D_2, 0, bytes, b_D_1.length, b_D_2.length);
        System.arraycopy(b_D_3, 0, bytes, b_D_2.length, b_D_3.length);
        System.arraycopy(b_D_4, 0, bytes, b_D_3.length, b_D_4.length);
        System.arraycopy(b_mess, 0, bytes, b_D_4.length, b_mess.length);

        Element cc = ElementOperation.Hash(proof[0], proof[1], proof[2], proof[3], D_1, D_2, D_3, D_4, mess);


        System.out.println("D_1 = " + D_1);
        System.out.println("D_2 = " + D_2);
        System.out.println("D_3 = " + D_3);
        System.out.println("D_4 = " + D_4);

        if (cc.isEqual(proof[4])) {
            System.out.println("cc = " + cc);
            System.out.println("c = " + proof[4]);
            System.out.println("车辆OBU验证成功");
        } else {
            System.out.println("The verification result is wrong in the Verify algorithm.");
        }


    }
}
