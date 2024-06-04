package cn.edu.sut.secruity.contest24;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Arrays;


public class RAASTest {
    static byte[][] bytes = new byte[4][];

    private static void raasTest() throws Exception {
        RAAScheme raa = new RAAScheme();
        raa.Setup("f.properties");

        RegistrationCenter ta = new RegistrationCenter();
        ta.TAKeyGen(raa, 4);
        Element[] sp_key = ta.SPKeyGen(raa);

        ServiceProvider sp = new ServiceProvider();
        sp.KeySet(raa, sp_key);
        System.out.println();

        //完全子树的根节点集合
        String[] stnodes = new String[1];
        stnodes[0] = "node 1";

        System.out.println("RC = [" + stnodes[0] + "]");

        Vehicle vehicle2 = new Vehicle();
        vehicle2.UKeyGen(raa, "id 9");
        Vehicle vehicle3 = new Vehicle();
        vehicle3.UKeyGen(raa, "id 10");

        Vehicle vehicle = new Vehicle();
        vehicle.UKeyGen(raa, "id 8");

        Element[] list = new Element[10];
        list[0] = vehicle2.uvk;
        list[1] = vehicle3.uvk;

        //用户的路径节点集合
        String[] userpath = new String[4];
        userpath[0] = "node 1";
        userpath[1] = "node 2";
        userpath[2] = "node 4";
        userpath[3] = "node 8";

        System.out.println("车辆OBU正在向注册中心请求令牌.......................");
        Element[] rs = vehicle.TokenObtain(raa, vehicle, ta);

        System.out.println("注册中心正在验证车辆OBU发送的证明.......................");
        TToken traceToken = ta.TokenIssue(raa, rs, vehicle, ta, userpath);

        //System.out.println("注册中心正在进行撤销操作.......................");
        //System.out.println("完全子树根节点集合RC = " + Arrays.toString(stnodes));
        String epoch = "revocation epoch1";
        //RToken revocationToken = ta.RTokenIssue(raa, stnodes, epoch);

        //long start_revoke = System.currentTimeMillis();
        //撤销令牌
        RToken revocationToken = ta.Revoke(raa, stnodes, epoch);
        //long end_revoke = System.currentTimeMillis();

        Tranv tv = new Tranv();
        tv.uid = vehicle.id;
        tv.upk = vehicle.uvk;
        tv.tToken = traceToken.tToken;
        tv.f = traceToken.f;

        System.out.println("注册中心正在生成撤销令牌.......................");
        Element interrt = ta.Update(raa, stnodes, userpath, tv, revocationToken, list);
        Element ran = revocationToken.ran[0];

        System.out.println();
        System.out.println("车辆OBU正在验证注册中心签发的追踪令牌.......................");
        vehicle.TTokenVerify(raa, ta, traceToken, vehicle, userpath[0], 0);

        System.out.println("车辆OBU正在验证注册中心签发的撤销令牌.......................");
        //user.RTokenVerify(raa, ta, revocationToken, stnodes[0], epoch, 0);
        vehicle.RTokenVerify(raa, ta, interrt, ran, stnodes[0], epoch, 0);

        System.out.println();
        Issuer issuer1 = new Issuer();
        issuer1.IKeyGen(raa, "Issuer1", 7);

        Issuer issuer2 = new Issuer();
        issuer2.IKeyGen(raa, "Issuer2", 7);

        Issuer issuer3 = new Issuer();
        issuer3.IKeyGen(raa, "Issuer3", 7);

        Issuer issuer4 = new Issuer();
        issuer4.IKeyGen(raa, "Issuer4", 5);

        Issuer issuer5 = new Issuer();
        issuer5.IKeyGen(raa, "Issuer5", 4);

        /*测试发行方的密钥
        System.out.println(issuer1.ID);
        for (int i = 0; i < 4; i++) {
            System.out.println("issuer1.isk[" + i + "]" + ":" + issuer1.isk[i]);
            System.out.println("issuer1.ivk[" + i + "]" + ":" + issuer1.ivk[i]);
        }
         */

        String[] attr1 = new String[6];
        //attr[0] = "zero";
        attr1[1] = "attribute 1.1";
        attr1[2] = "attribute 1.2";
        attr1[3] = "attribute 1.3";
        attr1[4] = "attribute 1.4";
        attr1[5] = "attribute 1.5";

        String[] attr2 = new String[6];
        //attr[0] = "zero";
        attr2[1] = "attribute 2.1";
        attr2[2] = "attribute 2.2";
        attr2[3] = "attribute 2.3";
        attr2[4] = "attribute 2.4";
        attr2[5] = "attribute 2.5";

        String[] attr3 = new String[6];
        //attr[0] = "zero";
        attr3[1] = "attribute 3.1";
        attr3[2] = "attribute 3.2";
        attr3[3] = "attribute 3.3";
        attr3[4] = "attribute 3.4";
        attr3[5] = "attribute 3.5";

        String[] attr4 = new String[4];
        //attr[0] = "zero";
        attr4[1] = "attribute 4.1";
        attr4[2] = "attribute 4.2";
        attr4[3] = "attribute 4.3";

        String[] attr5 = new String[3];
        //attr[0] = "zero";
        attr5[1] = "attribute 5.1";
        attr5[2] = "attribute 5.2";

        System.out.println();
        System.out.println("车辆OBU向发行方" + issuer1.ID + "申请访问凭证...................");
        Element[] pr1 = vehicle.CredObtain(raa, vehicle, issuer1, attr1);
        System.out.println("发行方" + issuer1.ID + "正在验证车辆OBU的合法性...................");
        Element sig1 = issuer1.CredIssue(raa, pr1, vehicle, issuer1, attr1, list);
        System.out.println("车辆OBU正在验证发行方" + issuer1.ID + "签发的访问凭证的有效性...................");
        vehicle.SingleCredVerify(raa, sig1, vehicle, issuer1, attr1);
        System.out.println();

        System.out.println("车辆OBU向发行方" + issuer2.ID + "申请访问凭证...................");
        Element[] pr2 = vehicle.CredObtain(raa, vehicle, issuer2, attr2);
        System.out.println("发行方" + issuer2.ID + "正在验证车辆OBU的合法性...................");
        Element sig2 = issuer2.CredIssue(raa, pr2, vehicle, issuer2, attr2, list);
        System.out.println("车辆OBU正在验证发行方" + issuer2.ID + "签发的访问凭证的有效性...................");
        vehicle.SingleCredVerify(raa, sig2, vehicle, issuer2, attr2);
        System.out.println();

        System.out.println("Interacting with " + issuer3.ID + "...................");
        System.out.println("vehicle is requesting.....");
        Element[] pr3 = vehicle.CredObtain(raa, vehicle, issuer3, attr3);
        System.out.println("Issuer3 is verifying the proof sent by vehicle.....");
        Element sig3 = issuer3.CredIssue(raa, pr3, vehicle, issuer3, attr3, list);
        System.out.println("vehicle is verifying the correctness of credential.....");
        vehicle.SingleCredVerify(raa, sig3, vehicle, issuer3, attr3);
        System.out.println();

        System.out.println("车辆OBU正在聚合访问凭证..........................");
        Element[] cred = new Element[3];
        cred[0] = sig1;
        cred[1] = sig2;
        cred[2] = sig3;

        Element credagg = vehicle.CredAgg(cred, 3);

        String[][] a = new String[4][];
        //a[0] = new String[]{"0"};
        a[1] = attr1;
        a[2] = attr2;
        a[3] = attr3;

        Element[][] ivkarry = new Element[4][];
        ivkarry[1] = issuer1.ivk;
        ivkarry[2] = issuer2.ivk;
        ivkarry[3] = issuer3.ivk;

        System.out.println("车辆OBU开始验证聚合凭证的有效性.......................");
        vehicle.CredAggVerify(raa, credagg, ivkarry, a, vehicle);
        System.out.println();

        System.out.println("车辆向服务提供商请求C-ITS服务.....................");
        Element tto = traceToken.tToken[0];
        Element tto_f = traceToken.f[0];
        Element rto = revocationToken.rToken[0];
        Element rto_ou = revocationToken.ran[0];
        String message = "requesting A service and B service.";
        Element[] show = vehicle.Show(raa, ta, credagg, ivkarry, a, vehicle, tto, tto_f, rto, rto_ou, userpath[0], message);


        System.out.println();
        System.out.println("服务提供商对当前车辆OBU进行验证.....................");
        ServiceProvider verifier = new ServiceProvider();
        verifier.Verify(raa, ta, show, ivkarry, a, epoch, message);

        System.out.println();
        System.out.println("注册中心正在执行追踪操作.............");
        ta.Trace(show, tv);

        System.out.println("注册中心正在执行撤销操作.............");
        String[] stnodes_t = {"node 3", "node 4"};
        System.out.println("RC = " + Arrays.toString(stnodes_t));
        RToken rtoken = ta.Revoke(raa, stnodes_t, epoch);
        System.out.println("revocation epoch = " + epoch);
        System.out.println("sr[0] from RC = " + stnodes_t[0]);
        System.out.println("rtoken[0] = " + rtoken.rToken[0]);
        System.out.println("sr[1] from RC = " + stnodes_t[1]);
        System.out.println("rtoken[1] = " + rtoken.rToken[1]);

        System.out.println("注册中心正在执行更新操作.............");
        ta.Update(raa, stnodes_t, userpath, tv, rtoken, list);


        System.out.println();
        System.out.println("Bechmark test:");
        Bechmark bech = new Bechmark();
        bech.FBechmark(raa);


    }

    public static boolean isOk(byte[] a, byte[] aa) {
        for (int i = 0; i < a.length; i++)
            if (a[i] != aa[i])
                return false;
        return true;
    }

    public static void main(String[] args) throws Exception {
        raasTest();
    }
}
