package cn.edu.sut.secruity.contest24;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Arrays;
import java.util.List;

public class RegistrationCenter {

    public Element[] tvk;
    public Element rrk;//the trace key
    private Element[] tsk;
    private Element rtk;//the private key of trace key

    //可信方密钥生成算法
    public void TAKeyGen(RAAScheme param, int num) {

        Element[] tsk = new Element[num];
        Element[] tvk = new Element[num];

        //生成注册中心的签名私钥和验证公钥
        for (int i = 0; i < num; i++) {
            tsk[i] = param.pairing.getZr().newRandomElement().getImmutable();
            tvk[i] = param.g2.duplicate().powZn(tsk[i]).getImmutable();
        }

        Element rtk = param.pairing.getZr().newRandomElement().getImmutable();//随机选择追踪密钥
        Element rrk = param.g1.duplicate().powZn(rtk).getImmutable();//生成追踪公钥

        this.tvk = tvk;
        this.tsk = tsk;
        this.rtk = rtk;
        this.rrk = rrk;

        System.out.println();
        System.out.println("正在生成注册中心的公私钥.............");
        for (int i = 0; i < tsk.length; i++) {
            System.out.println("签名私钥tsk[" + i + "] = " + tsk[i]);
            System.out.println("验证公钥tvk[" + i + "] = " + tvk[i]);
        }
        System.out.println("注册中心的追踪私钥rtk = " + rtk);
        System.out.println("注册中心的追踪公钥rrk = " + rrk);

    }

    //可信方为服务提供商生成密钥
    public Element[] SPKeyGen(RAAScheme param) {
        Element psk = param.pairing.getZr().newRandomElement().getImmutable();
        Element pvk = param.g2.duplicate().powZn(psk).getImmutable();

        Element[] sp = new Element[2];
        sp[0] = psk;
        sp[1] = pvk;

        return sp;

    }

    /*长期的追踪令牌颁发算法
      1.可信方首先验证用户关于usk的知识证明是否正确；
      2.关于usk的知识证明验证通过后，为此用户颁发追踪令牌。
     */
    public TToken TokenIssue(RAAScheme param, Element[] proofs, Vehicle vehicle, RegistrationCenter ta, String[] path) {

        Element Rl = param.g1.duplicate().powZn(proofs[0]).getImmutable();
        Element Rr = vehicle.uvk.duplicate().powZn(proofs[1]).negate();

        Element RR = Rl.duplicate().mul(Rr).getImmutable();
        System.out.print("RR = ");
        System.out.println(RR);

        //Element conn = RR.duplicate().add(user.uvk);

        byte[] conn1 = ta.tvk[0].toBytes();
        byte[] conn2 = vehicle.uvk.toBytes();
        byte[] conn3 = RR.toBytes();
        System.out.println(conn3);
        byte[] connt = new byte[conn1.length + conn2.length + conn3.length];
        System.arraycopy(conn1, 0, connt, 0, conn1.length);
        System.arraycopy(conn2, 0, connt, conn1.length, conn2.length);
        System.arraycopy(conn3, 0, connt, conn2.length, conn3.length);

        //Element cc = pairing.getZr().newElementFromHash(conn.toBytes(), 0, conn.getLengthInBytes());
        Element cc = param.pairing.getZr().newElementFromHash(connt, 0, connt.length);

        System.out.print("cc = ");
        System.out.println(cc);

        Element[] tToken = new Element[path.length];
        Element[] f = new Element[path.length];

        if (cc.isEqual(proofs[1])) {
            System.out.println("验证成功.........");
            System.out.println("注册中心正在生成追踪令牌.........");

            /*生成长期的追踪令牌
              1.获取用户路径上的节点，并将节点放入一个数组中
              2.对数组中的每个节点进行签名
              3.将追踪令牌作为一个类返回给用户
              4.可信方管理一个tranv类作为用户的数据库
             */

            Element[] upath = Util.StringToElementOne(param.pairing, path);
            Element[] exp = new Element[upath.length];
            Element[] iexp = new Element[upath.length];
            Element[] base1 = new Element[upath.length];
            Element[] base = new Element[upath.length];


            for (int i = 0; i < path.length; i++) {

                f[i] = param.pairing.getZr().newRandomElement().getImmutable();
                exp[i] = tsk[3].add(f[i]).getImmutable();
                iexp[i] = exp[i].duplicate().invert().getImmutable();
                base1[i] = param.g__1.duplicate().powZn(upath[i]).duplicate().mul(vehicle.uvk).getImmutable();
                base[i] = param.g_1.duplicate().mul(base1[i]).getImmutable();
                tToken[i] = base[i].duplicate().powZn(iexp[i]).getImmutable();

                System.out.println("f[" + i + "] = " + f[i]);
                System.out.println("tToken[" + i + "] = " + tToken[i]);

            }
        } else {
            System.out.println("The proof of usk is wrong.");
        }

        TToken ttoken = new TToken();
        ttoken.tToken = tToken;
        ttoken.f = f;

        return ttoken;

    }


    //返回用户路径
    public String[] Userpath(CBTree cbt) {
        String[] st = new String[3];
        return st;

    }

    //返回二叉树完全子树根节点的集合
    public String[] RCnode(CBTree cbt) {
        String[] RC = new String[3];
        return RC;
    }

    /*单个撤销令牌生成算法
    public RToken RTokenIssue(RAAScheme param, String strnode, String t) {

        Element stn = Util.StringToElement(param.pairing, strnode);
        Element tt = Util.StringToElement(param.pairing, t);

        Element rv = param.pairing.getZr().newRandomElement().getImmutable();
        Element exp1 = tsk[1].duplicate().mul(stn).getImmutable();
        Element exp2 = tsk[2].duplicate().mul(tt).getImmutable();
        Element exp3 = tsk[3].duplicate().mul(rv).getImmutable();
        Element exp4 = tsk[0].duplicate().add(exp1).getImmutable();
        Element exp5 = exp4.duplicate().add(exp2).getImmutable();
        Element exp = exp5.duplicate().add(exp3).getImmutable();
        Element rtoken = param.g1.duplicate().powZn(exp).getImmutable();

        RToken rt = new RToken();
        rt.rToken = rtoken;
        rt.ran = rv;

        System.out.println("strnode from RC = " + strnode);
        System.out.println("revocation epoch = " + t);
        System.out.println("rtoken = "+rtoken);

        return rt;

    }
     */

    //为完全子树根节点集合的每个节点生成撤销令牌
    public RToken Revoke(RAAScheme param, String[] sr, String t) {
        Element[] stn = Util.StringToElementOne(param.pairing, sr);
        Element tt = Util.StringToElement(param.pairing, t);

        Element[] rtoken = new Element[sr.length];
        Element[] rv = new Element[sr.length];
        Element[] exp1 = new Element[sr.length];
        Element[] exp2 = new Element[sr.length];
        Element[] exp3 = new Element[sr.length];
        Element[] exp4 = new Element[sr.length];
        Element[] exp5 = new Element[sr.length];
        Element[] exp = new Element[sr.length];

        for (int i = 0; i < sr.length; i++) {
            rv[i] = param.pairing.getZr().newRandomElement().getImmutable();
            exp1[i] = tsk[1].duplicate().mul(stn[i]).getImmutable();
            exp2[i] = tsk[2].duplicate().mul(tt).getImmutable();
            exp3[i] = tsk[3].duplicate().mul(rv[i]).getImmutable();
            exp4[i] = tsk[0].duplicate().add(exp1[i]).getImmutable();
            exp5[i] = exp4[i].duplicate().add(exp2[i]).getImmutable();
            exp[i] = exp5[i].duplicate().add(exp3[i]).getImmutable();
            rtoken[i] = param.g1.duplicate().powZn(exp[i]).getImmutable();

            //System.out.println("sr[" + i + "] from RC = " + sr[i]);
            //System.out.println("revocation epoch = " + t);
            //System.out.println("rtoken[" + i + "] = " + rtoken[i]);
        }

        RToken rt = new RToken();
        rt.rToken = rtoken;
        rt.ran = rv;

        return rt;

    }

    //更新用户的撤销令牌
    public Element Update(RAAScheme param, String[] sr, String[] upath, Tranv tranv, RToken rToken, Element[] list) {
        Element upk = tranv.upk;
        List<Element> elelist = Arrays.asList(list);
        boolean result = elelist.contains(upk);
        String node = null;
        if (!result) {

            for (int i = 0; i < upath.length; i++) {
                for (int j = 0; j < sr.length; j++) {
                    if (upath[i] == sr[j]) {
                        node = upath[i];
                        break;
                    }
                }
            }
            System.out.println("交集节点node = " + node);

            if (rToken.rToken.length == 1) {
                System.out.println("交集节点[" + node + "]对应的撤销令牌rToken = " + rToken.rToken[0]);
                return rToken.rToken[0];
            } else {
                System.out.println("交集节点[" + node + "]对应的撤销令牌rToken = " + rToken.rToken[1]);
                return rToken.rToken[1];
            }


        } else {
            String res = "error";
            Element results = Util.StringToElement(param.pairing, res);
            return results;
        }

    }

    //根据用户在出示阶段出示的证明，解密出用户的追踪令牌
    public Element Trace(Element[] showproof, Tranv tranv) {

        Element trace_token = showproof[0].duplicate().div(showproof[2].duplicate().powZn(rtk)).getImmutable();
        System.out.println("phi_1 = " + showproof[0]);
        System.out.println("tToken = " + trace_token);

        if (trace_token.isEqual(tranv.tToken[0])) {
            System.out.println("追踪到车辆OBU " + tranv.uid + ".");
        }

        return trace_token;

    }

}
