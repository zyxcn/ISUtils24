package cn.edu.sut.secruity.contest24;

import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;

public class Vehicle {

    public Element uvk;
    public String id;
    private Element usk;

    //用户密钥生成算法
    public void UKeyGen(RAAScheme param, String identity) {

        String uid = identity;
        Element sk = param.pairing.getZr().newRandomElement().getImmutable();
        Element pk = param.g1.duplicate().powZn(sk).getImmutable();

        this.usk = sk;
        this.uvk = pk;
        this.id = uid;

        System.out.println("车辆OBU " + id + " 的私钥usk = " + usk);
        System.out.println("车辆OBU " + id + " 的公钥uvk = " + uvk);
    }


    /*令牌获取算法
      1.用户在申请令牌时向可信方证明自己知道公钥对应的私钥。
      2.f<-Zp,R=g1^f,c=H(ivk0||uvk||R),k=f+c*usk
     */
    public Element[] TokenObtain(RAAScheme param, Vehicle vehicle, RegistrationCenter ta) {

        Element f = param.pairing.getZr().newRandomElement().getImmutable();
        Element R = param.g1.duplicate().powZn(f).getImmutable();
        //Element con = R.duplicate().add(user.uvk.add(issuer.ivk[0]));
        //System.out.print("R = ");
        //System.out.println(R);

        //Element con = R.duplicate().add(user.uvk);//add方法只能将同一个群的元素相加，不同群的元素不能相加

        byte[] con1 = ta.tvk[0].toBytes();//将ivk0,uvk,R转换成byte数组
        byte[] con2 = vehicle.uvk.toBytes();
        byte[] con3 = R.toBytes();
        byte[] cont = new byte[con1.length + con2.length + con3.length];//将三个byte数组拼接合并成一个byte数组
        System.arraycopy(con1, 0, cont, 0, con1.length);
        System.arraycopy(con2, 0, cont, con1.length, con2.length);
        System.arraycopy(con3, 0, cont, con2.length, con3.length);

        //Element c = pairing.getZr().newElementFromHash(con.toBytes(), 0, con.getLengthInBytes());
        Element c = param.pairing.getZr().newElementFromHash(cont, 0, cont.length);//把H(ivk0||uvk||R)映射成Zr上的一个点

        System.out.print("c = ");
        System.out.println(c);

        Element ff = c.duplicate().mul(vehicle.usk);
        Element k = f.add(ff).getImmutable();
        System.out.println("k = " + k);


        Element[] result = new Element[2];
        result[0] = k;
        result[1] = c;

        return result;

    }

    public void TTokenVerify(RAAScheme param, RegistrationCenter ta, TToken ttoken, Vehicle vehicle, String path, int i) {

        Element node = Util.StringToElement(param.pairing, path);


        long start_token = System.currentTimeMillis();
        Element left1 = ta.tvk[3].duplicate().mul(param.g2.powZn(ttoken.f[i])).getImmutable();
        Element left = param.pairing.pairing(ttoken.tToken[i], left1).getImmutable();
        Element base1 = param.g__1.duplicate().powZn(node).duplicate().mul(vehicle.uvk).getImmutable();
        Element base = param.g_1.duplicate().mul(base1).getImmutable();
        Element right = param.pairing.pairing(base, param.g2).getImmutable();

        if (left.isEqual(right)) {
            System.out.println("left[" + i + "] = " + right);
            System.out.println("right[" + i + "] = " + right);
            System.out.println("令牌tToken[" + i + "]" + "验证成功");
        } else {
            System.out.println("tToken[" + i + "] " + "is wrong.");
        }
        long end_token = System.currentTimeMillis();
        //System.out.println("time in the ttoken algorithm " + (end_token - start_token) + "ms");


    }

    /*原没有加update算法的撤销令牌验证算法
    public void RTokenVerify(RAAScheme param, TrustedAuthority ta, RToken rToken, String subnode, String t, int i) {

        Element node = Util.StringToElement(param.pairing, subnode);
        Element epoch = Util.StringToElement(param.pairing, t);

        Element left = param.pairing.pairing(rToken.rToken[i], param.g2).getImmutable();

        Element r1 = ta.tvk[1].duplicate().powZn(node).getImmutable();
        Element r2 = ta.tvk[2].duplicate().powZn(epoch).getImmutable();
        Element r3 = ta.tvk[3].duplicate().powZn(rToken.ran[i]).getImmutable();
        Element r4 = ta.tvk[0].duplicate().mul(r1).getImmutable();
        Element r5 = r4.duplicate().mul(r2).getImmutable();
        Element right1 = r5.duplicate().mul(r3).getImmutable();

        Element right = param.pairing.pairing(param.g1, right1).getImmutable();

        if (left.isEqual(right)) {
            System.out.println("left = " + left);
            System.out.println("right = " + right);
            System.out.println("rToken is right.");
        } else {
            System.out.println("rToken is wrong.");
        }

    }

     */

    public void RTokenVerify(RAAScheme param, RegistrationCenter ta, Element rToken, Element ran, String subnode, String t, int i) {

        Element node = Util.StringToElement(param.pairing, subnode);
        Element epoch = Util.StringToElement(param.pairing, t);

        long start_revoke = System.currentTimeMillis();

        Element left = param.pairing.pairing(rToken, param.g2).getImmutable();

        Element r1 = ta.tvk[1].duplicate().powZn(node).getImmutable();
        Element r2 = ta.tvk[2].duplicate().powZn(epoch).getImmutable();
        Element r3 = ta.tvk[3].duplicate().powZn(ran).getImmutable();
        Element r4 = ta.tvk[0].duplicate().mul(r1).getImmutable();
        Element r5 = r4.duplicate().mul(r2).getImmutable();
        Element right1 = r5.duplicate().mul(r3).getImmutable();

        Element right = param.pairing.pairing(param.g1, right1).getImmutable();

        if (left.isEqual(right)) {
            System.out.println("left = " + left);
            System.out.println("right = " + right);
            System.out.println("令牌rToken验证成功");
        } else {
            System.out.println("rToken is wrong.");
        }
        long end_revoke = System.currentTimeMillis();
        System.out.println("time of verifying the revocation token in user class = " + (end_revoke - start_revoke) + "ms");


    }


    /*凭证获取算法
      1.用户在申请凭证时向发行方证明自己知道公钥对应的私钥。
      2.f<-Zp,R=g1^f,c=H(ivk0||uvk||R),k=f+c*usk
     */
    public Element[] CredObtain(RAAScheme param, Vehicle vehicle, Issuer issuer, String[] attr) {

        Element f = param.pairing.getZr().newRandomElement().getImmutable();
        Element R = param.g1.duplicate().powZn(f).getImmutable();
        //Element con = R.duplicate().add(user.uvk.add(issuer.ivk[0]));
        //System.out.print("R = ");
        //System.out.println(R);

        //Element con = R.duplicate().add(user.uvk);//add方法只能将同一个群的元素相加，不同群的元素不能相加

        byte[] con1 = issuer.ivk[0].toBytes();//将ivk0,uvk,R转换成byte数组
        byte[] con2 = vehicle.uvk.toBytes();
        byte[] con3 = R.toBytes();
        byte[] cont = new byte[con1.length + con2.length + con3.length];//将三个byte数组拼接合并成一个byte数组
        System.arraycopy(con1, 0, cont, 0, con1.length);
        System.arraycopy(con2, 0, cont, con1.length, con2.length);
        System.arraycopy(con3, 0, cont, con1.length + con2.length, con3.length);

        //Element c = pairing.getZr().newElementFromHash(con.toBytes(), 0, con.getLengthInBytes());
        Element c = param.pairing.getZr().newElementFromHash(cont, 0, cont.length);//把H(ivk0||uvk||R)映射成Zr上的一个点

        System.out.print("c = ");
        System.out.println(c);

        Element ff = c.duplicate().mul(vehicle.usk);
        Element k = f.add(ff).getImmutable();
        System.out.println("k = " + k);


        Element[] result = new Element[2];
        result[0] = k;
        result[1] = c;

        return result;


    }


    //单个凭证验证算法
    public int SingleCredVerify(RAAScheme param, Element sig, Vehicle vehicle, Issuer issuer, String[] attr) {
        //sig=param.pairing.getG1().newRandomElement();
        Element left = param.pairing.pairing(sig, param.g2);

        Element[] attribute = new Element[attr.length];
        Element[] exp = new Element[attr.length];
        for (int i = 1; i < attr.length; i++) {
            byte[] att = attr[i].getBytes();
            attribute[i] = param.pairing.getZr().newElementFromBytes(att);
            exp[i] = issuer.ivk[i].duplicate().powZn(attribute[i]);
        }

        Element sig1 = exp[1];
        for (int i = 2; i < attr.length; i++) {
            sig1 = sig1.duplicate().mul(exp[i]);

        }

        Element rsig = issuer.ivk[0].duplicate().mul(sig1).mul(issuer.ivk[issuer.ivk.length - 1]);

        Element right = param.pairing.pairing(vehicle.uvk, rsig);

        if (left.isEqual(right)) {
            System.out.println("left = " + left);
            System.out.println("right = " + right);
            System.out.println("访问凭证cred验证成功");
        } else {
            System.out.println("The verification of single credential is wrong.");
        }

        return 0;
    }


    //凭证聚合算法：将m个凭证聚合成一个凭证
    public Element CredAgg(Element[] Cred, int m) {

        Element credagg = Cred[0];
        for (int i = 1; i < m; i++) {
            credagg = credagg.duplicate().mul(Cred[i]);
        }

        System.out.print("credagg = ");
        System.out.println(credagg);
        System.out.println("credagglenth = " + credagg.getLengthInBytes() + " " + "bytes.");

        return credagg;

    }


    //聚合凭证验证算法：验证聚合后的凭证是否正确
    public int CredAggVerify(RAAScheme param, Element credagg, Element[][] ivk, String[][] attr, Vehicle vehicle) {

        Element left = param.pairing.pairing(credagg, param.g2);

        Element[][] attribute = new Element[attr.length][attr[1].length];

        for (int j = 1; j < attr.length; j++) {
            for (int i = 1; i < attr[j].length; i++) {
                byte[] att = attr[j][i].getBytes();
                attribute[j][i] = param.pairing.getZr().newElementFromBytes(att, 0);
            }
        }


        /*从第2行开始，输出每行的元素值
        for (int j = 1; j < attribute.length; j++) {
            for (int i = 1; i < attribute[j].length; i++) {
                System.out.println("attribute[" + j + "]" + "[" + i + "]" + "=" + attribute[j][i]);
            }
        }

         */

        Element rig0 = ivk[1][1].duplicate().powZn(attribute[1][1]);

        //Element rig1 = ivk[1][2].duplicate().powZn(attribute[1][2]);//已知ivk[1][]签名的属性个数，计算ivk1,2^a1,2
        //Element rig = rig0.duplicate().mul(rig1);
        //Element rigr = ivk[1][0].duplicate().mul(rig).getImmutable();

        for (int i = 2; i < ivk[1].length - 1; i++) {
            rig0 = rig0.duplicate().mul(ivk[1][i].duplicate().powZn(attribute[1][i]));//不知道ivk[1][]签名的属性个数，计算ivk1,i^a1,i
        }

        Element rigr = ivk[1][0].duplicate().mul(rig0).getImmutable();//计算ivk1,0*ivk1,i^a1,i
        Element rigg = rigr.duplicate().mul(ivk[1][ivk[1].length - 1]);//计算j=1下的ivk1,0*ivk1,i^a1,i*ivk1,ivk1.length-1

        for (int j = 2; j < ivk.length; j++) {
            Element r1 = ivk[j][1].duplicate().powZn(attribute[j][1]);
            for (int i = 2; i < ivk[j].length - 1; i++) {
                //rigg = rigg.duplicate().mul(ivk[j][0].duplicate().mul(ivk[j][i].duplicate().powZn(attribute[j][i])).duplicate().mul(ivk[j][ivk[j].length-1]));
                r1 = r1.duplicate().mul(ivk[j][i].duplicate().powZn(attribute[j][i]));//计算出j下的ivkj,i^aj,i;
            }
            Element r2 = ivk[j][0].duplicate().mul(r1).getImmutable();
            Element r3 = r2.duplicate().mul(ivk[j][ivk[j].length - 1]);
            rigg = rigg.duplicate().mul(r3).getImmutable();//将j个凭证值连乘
        }


        Element right = param.pairing.pairing(vehicle.uvk, rigg);

        System.out.print("left = ");
        System.out.println(left);
        System.out.print("right = ");
        System.out.println(right);


        //Verifier.AggregationCredVerify(param.g2,uvk,)

        if (left.isEqual(right)) {
            System.out.println("聚合凭证credagg验证成功");
        } else {
            System.out.println("The verification of aggregation credential is wrong.");
        }

        /*全都罗列出来
        Element rigfb = ivk[1][0].duplicate().mul(ivk[1][ivk[1].length - 1]);
        Element sig1 = rig.duplicate().mul(rigfb).getImmutable();

        Element rig2 = ivk[2][1].duplicate().powZn(attribute[2][1]).getImmutable();

        Element rigfb1 = ivk[2][0].duplicate().mul(ivk[2][ivk[2].length-1]);
        Element sig2 = rig2.duplicate().mul(rigfb1).getImmutable();

        Element total = sig1.duplicate().mul(sig2).getImmutable();
        Element totalp = pairing.pairing(user.uvk,total);

        System.out.print("rigg=");
        System.out.println(rigg);
        System.out.print("total=");
        System.out.println(total);
        System.out.print("totalp=");
        System.out.println(totalp);

         */


        return 0;

    }


    //预先计算的配对值
    public Element[] PreCompute(RAAScheme param, RegistrationCenter ta) {
        Element D2_p2 = param.pairing.pairing(param.g__1, param.g2).getImmutable();
        Element D2_p3 = param.pairing.pairing(param.g1, param.g2).getImmutable();
        Element D2_p4 = param.pairing.pairing(ta.rrk, ta.tvk[3]).getImmutable();
        Element D2_p5 = param.pairing.pairing(ta.rrk, param.g2).getImmutable();

        Element D3_p1 = param.pairing.pairing(param.g1, ta.tvk[1]).getImmutable();
        Element D3_p2 = param.pairing.pairing(param.g1, ta.tvk[3]).getImmutable();
        Element D3_p3 = D2_p3.getImmutable();

        Element[] preCom = new Element[7];
        preCom[0] = D2_p2.getImmutable();
        preCom[1] = D2_p3.getImmutable();
        preCom[2] = D2_p4.getImmutable();
        preCom[3] = D2_p5.getImmutable();
        preCom[4] = D3_p1.getImmutable();
        preCom[5] = D3_p2.getImmutable();
        preCom[6] = D3_p3.getImmutable();

        return preCom;
    }

    public Element[] Show(RAAScheme param, RegistrationCenter ta, Element credagg, Element[][] ivk, String[][] attr, Vehicle vehicle, Element ttoken, Element f, Element rtoken, Element ou, String node, String mes) {

        long start1 = System.currentTimeMillis();
        Element o = param.pairing.getZr().newRandomElement().getImmutable();
        Element phi_1 = ttoken.duplicate().mul(ta.rrk.duplicate().powZn(o)).getImmutable();
        Element phi_2 = rtoken.duplicate().mul(param.g1.duplicate().powZn(o)).getImmutable();
        Element phi_3 = param.g1.duplicate().powZn(o).getImmutable();
        Element beta = o.duplicate().mul(f).getImmutable();
        Element credagg_ran = credagg.duplicate().powZn(o).getImmutable();

        Element S_u = param.pairing.getZr().newRandomElement().getImmutable();
        Element S_f = param.pairing.getZr().newRandomElement().getImmutable();
        Element S_k = param.pairing.getZr().newRandomElement().getImmutable();
        Element S_ou = param.pairing.getZr().newRandomElement().getImmutable();
        Element S_o = param.pairing.getZr().newRandomElement().getImmutable();
        Element S_beta = param.pairing.getZr().newRandomElement().getImmutable();

        //计算D1
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

        Element leftele = phi_3.duplicate().powZn(S_u).getImmutable();
        Element D1 = param.pairing.pairing(leftele, rigg);
        long end1 = System.currentTimeMillis();

        //给出预先计算的配对值
        Element[] preValue = vehicle.PreCompute(param, ta);

        long start2 = System.currentTimeMillis();
        //计算D2
        Element D2_r1 = param.pairing.pairing(phi_1.duplicate().powZn(S_f).getImmutable(), param.g2).getImmutable();
        Element D2_r2 = preValue[0].duplicate().powZn(S_k).getImmutable();
        Element D2_r3 = preValue[1].duplicate().powZn(S_u).getImmutable();
        Element D2_r4 = preValue[2].duplicate().powZn(S_o).getImmutable();
        Element D2_r5 = preValue[3].duplicate().powZn(S_beta).getImmutable();
        Element D2 = D2_r1.duplicate().mul(D2_r2).mul(D2_r3).mul(D2_r4).mul(D2_r5).getImmutable();

        //计算D3
        Element D3_r1 = preValue[4].duplicate().powZn(S_k).getImmutable();
        Element D3_r2 = preValue[5].duplicate().powZn(S_ou).getImmutable();
        Element D3_r3 = preValue[6].duplicate().powZn(S_o).getImmutable();
        Element D3 = D3_r1.duplicate().mul(D3_r2).mul(D3_r3).getImmutable();

        //计算D4
        Element D4 = param.g1.duplicate().powZn(S_o).getImmutable();
        long end2 = System.currentTimeMillis();

        Element mess = Util.StringToElement(param.pairing, mes);

        //计算c
        byte[] b_phi_1 = phi_1.toBytes();
        byte[] b_phi_2 = phi_2.toBytes();
        byte[] b_phi_3 = phi_3.toBytes();
        byte[] b_credagg_ran = credagg_ran.toBytes();
        byte[] b_D1 = D1.toBytes();
        byte[] b_D2 = D2.toBytes();
        byte[] b_D3 = D3.toBytes();
        byte[] b_D4 = D4.toBytes();
        byte[] b_mess = mess.toBytes();

        byte[] bytes = new byte[b_phi_1.length + b_phi_2.length + b_phi_3.length + b_credagg_ran.length + b_D1.length + b_D2.length + b_D3.length + b_D4.length + b_mess.length];

        Element c = ElementOperation.Hash(phi_1, phi_2, phi_3, credagg_ran, D1, D2, D3, D4, mess);

        Element k = Util.StringToElement(param.pairing, node);

        Element W_u = S_u.duplicate().add(c.duplicate().mul(vehicle.usk)).getImmutable();
        Element W_f = S_f.duplicate().add(c.duplicate().mul(f)).getImmutable();
        Element W_k = S_k.duplicate().add(c.duplicate().mul(k)).getImmutable();
        Element W_o = S_o.duplicate().add(c.duplicate().mul(o)).getImmutable();
        Element W_beta = S_beta.duplicate().add(c.duplicate().mul(beta)).getImmutable();
        Element W_ou = S_ou.duplicate().add(c.duplicate().mul(ou)).getImmutable();
        long end3 = System.currentTimeMillis();

        //System.out.println("Show algorithm needs " + ((end1 - start1) + (end2 - start2) + (end3 - start3)) + "ms");

        Element[] showproof = {phi_1, phi_2, phi_3, credagg_ran, c, W_u, W_f, W_k, W_o, W_beta, W_ou};

        System.out.println("phi_1 = " + showproof[0]);
        System.out.println("phi_2 = " + showproof[1]);
        System.out.println("phi_3 = " + showproof[2]);
        System.out.println("beta = " + beta);
        System.out.println("credagg_ran = " + showproof[3]);
        System.out.println("D1 = " + D1);
        System.out.println("D2 = " + D2);
        System.out.println("D3 = " + D3);
        System.out.println("D4 = " + D4);
        System.out.println("c = " + showproof[4]);
        System.out.println("W_u = " + showproof[5]);
        System.out.println("W_f = " + showproof[6]);
        System.out.println("W_k = " + showproof[7]);
        System.out.println("W_o = " + showproof[8]);
        System.out.println("W_beta = " + showproof[9]);
        System.out.println("W_ou = " + showproof[10]);

        return showproof;

    }

}
