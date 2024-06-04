package cn.edu.sut.secruity.contest24.operation.user;

import cn.edu.sut.secruity.contest24.credential.AggregationCredential;
import cn.edu.sut.secruity.contest24.credential.Credential;
import cn.edu.sut.secruity.contest24.operation.result.proof.Proof;
import cn.edu.sut.secruity.contest24.operation.result.userproof.UserProof;
import cn.edu.sut.secruity.contest24.param.PublicParam;
import cn.edu.sut.secruity.contest24.param.RegisterParam;
import cn.edu.sut.secruity.contest24.param.UserParam;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

/**
 * user operations
 * such create userProof
 * verify cred
 * cred aggregation
 */
public class UserOperation {
    /**
     * 生成用户凭证,确保你拥有私钥
     *
     * @param ivk
     * @return
     */
    public static UserProof createUserProof(Element ivk) {
        Element g1 = PublicParam.g1;
        Element g2 = PublicParam.g2;

        Element uvk = UserParam.uvk;
        Element usk = UserParam.usk;

        Element f = PublicParam.Zr.newRandomElement().getImmutable();
        Element R = g1.duplicate().powZn(f).getImmutable();
        Element hashedR = ElementOperation.Hash(ivk, uvk, R);
        Element Sb = f.add(hashedR.duplicate().mul(usk)).getImmutable();

        UserProof userProof = new UserProof();
        userProof.setUvk(uvk);
        userProof.setSb(Sb);
        userProof.setHashedR(hashedR);
        return userProof;
    }

    /**
     * 验证发行方的发布的凭证的有效性
     *
     * @param credential
     * @param ivkList
     * @return
     */
    public static Boolean SingleCredVerify(Credential credential, Element[] ivkList) {
        Pairing pairing = PublicParam.pairing;
        Element g2 = PublicParam.g2;

        Element uvk = UserParam.uvk;

        Element left = pairing.pairing(credential.getCredential(), g2);
        String[] attributes = credential.getAttributes();
        Element[] attribute = new Element[attributes.length];
        Element[] exp = new Element[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            byte[] attributeByte = attributes[i].getBytes();
            attribute[i] = PublicParam.Zr.newElementFromBytes(attributeByte);
            exp[i] = ivkList[i + 1].duplicate().powZn(attribute[i]).getImmutable();
        }

        Element signaturePrefix = exp[0];
        for (int i = 1; i < attributes.length; i++) {
            signaturePrefix = signaturePrefix.duplicate().mul(exp[i]);
        }

        Element signature = ivkList[0].duplicate().mul(signaturePrefix).mul(ivkList[ivkList.length - 1]);

        Element right = pairing.pairing(uvk, signature);

        return left.isEqual(right);
    }

    /**
     * 将所有的凭证聚合为一个凭证
     *
     * @param credentials
     * @return
     */
    public static AggregationCredential CredAgg(Credential... credentials) {
        AggregationCredential aggregationCredential = new AggregationCredential();
        //聚合凭证
        Element aggCred = credentials[0].getCredential();
        for (int i = 1; i < credentials.length; i++) {
            aggCred = aggCred.duplicate().mul(credentials[i].getCredential());
        }
        aggregationCredential.setAggCredential(aggCred);

        //属性和issuerId
        String[][] attributes = new String[credentials.length][];
        Element[][] ivkList = new Element[credentials.length][];
        for (int i = 0; i < credentials.length; i++) {
            attributes[i] = credentials[i].getAttributes();
            ivkList[i] = credentials[i].getIvkList();
        }

        aggregationCredential.setAttributes(attributes);
        aggregationCredential.setIvkList(ivkList);
        return aggregationCredential;

    }

    public static Proof Show(AggregationCredential credential, String message) {
        Element credagg = credential.getAggCredential();
        Element[][] ivk = credential.getIvkList();
        String[][] attr = credential.getAttributes();

        //引入参数
        Pairing pairing = PublicParam.pairing;
        Field Zr = PublicParam.Zr;
        Element g1 = PublicParam.g1;
        Element g__1 = PublicParam.g__1;
        Element g2 = PublicParam.g2;

        Element rToken = UserParam.rToken;
        Element tToken = UserParam.tToken;
        Element usk = UserParam.usk;
        Element fr = UserParam.fr;
        Element nodeValue = UserParam.nodeValue;
        Element Rv = UserParam.Rv;


        Element rtvk = RegisterParam.rtvk;
        Element[] rvk = RegisterParam.rvk;

        //生成预计算值
        Element g__1_E_g2 = pairing.pairing(g__1, g2).getImmutable();
        Element g1_E_g2 = pairing.pairing(g1, g2).getImmutable();
        Element rtvk_E_rvk4 = pairing.pairing(rtvk, rvk[3]).getImmutable();
        Element rtvk_E_g2 = pairing.pairing(rtvk, g2).getImmutable();
        Element g1_E_rvk2 = pairing.pairing(g1, rvk[1]).getImmutable();
        Element g1_E_rvk4 = pairing.pairing(g1, rvk[3]).getImmutable();


        //计算参数
        Element o = Zr.newRandomElement().getImmutable();

        Element phi_1 = tToken.duplicate().mul(rtvk.duplicate().powZn(o)).getImmutable();
        Element phi_2 = rToken.duplicate().mul(g1.duplicate().powZn(fr)).getImmutable();
        Element phi_3 = g1.duplicate().powZn(o).getImmutable();
        Element beta = o.duplicate().mul(fr).getImmutable();
        Element credagg_ran = credagg.duplicate().powZn(o).getImmutable();

        Element S_u = Zr.newRandomElement().getImmutable();
        Element S_f = Zr.newRandomElement().getImmutable();
        Element S_k = Zr.newRandomElement().getImmutable();
        Element S_ou = Zr.newRandomElement().getImmutable();
        Element S_o = Zr.newRandomElement().getImmutable();
        Element S_beta = Zr.newRandomElement().getImmutable();

        //计算D1
        Element[][] attribute = new Element[attr.length][];

        for (int i = 0; i < attr.length; i++) {
            attribute[i] = new Element[attr[i].length];
            for (int j = 0; j < attr[i].length; j++) {
                byte[] att = attr[i][j].getBytes();
                attribute[i][j] = Zr.newElementFromBytes(att, 0);
            }
        }

        Element rig0 = ivk[0][1].duplicate().powZn(attribute[0][0]);

        for (int i = 1; i < attr[0].length; i++) {
            rig0 = rig0.duplicate().mul(ivk[0][i + 1].duplicate().powZn(attribute[0][i]));//不知道ivk[1][]签名的属性个数，计算ivk1,i^a1,i
        }

        Element rigr = ivk[0][0].duplicate().mul(rig0).getImmutable();//计算ivk1,0*ivk1,i^a1,i
        Element rigg = rigr.duplicate().mul(ivk[0][ivk[0].length - 1]);//计算j=1下的ivk1,0*ivk1,i^a1,i*ivk1,ivk1.length-1

        for (int i = 1; i < attr.length; i++) {
            Element r1 = ivk[i][1].duplicate().powZn(attribute[i][0]);
            for (int j = 1; j < attr[i].length; j++) {
                r1 = r1.duplicate().mul(ivk[i][j + 1].duplicate().powZn(attribute[i][j]));//计算出j下的ivkj,j^aj,j;
            }
            Element r2 = ivk[i][0].duplicate().mul(r1).getImmutable();
            Element r3 = r2.duplicate().mul(ivk[i][ivk[i].length - 1]);
            rigg = rigg.duplicate().mul(r3).getImmutable();//将j个凭证值连乘
        }

        Element leftElement = phi_3.duplicate().powZn(S_u).getImmutable();

        Element D1 = pairing.pairing(leftElement, rigg);

        //计算D2
        Element D2_r1 = pairing.pairing(phi_1.duplicate().powZn(S_f.duplicate().negate()), g2).getImmutable();
        Element D2_r2 = g__1_E_g2.duplicate().powZn(S_k).getImmutable();
        Element D2_r3 = g1_E_g2.duplicate().powZn(S_u).getImmutable();
        Element D2_r4 = rtvk_E_rvk4.duplicate().powZn(S_o).getImmutable();
        Element D2_r5 = rtvk_E_g2.duplicate().powZn(S_beta).getImmutable();
        Element D2 = D2_r1.duplicate().mul(D2_r2).mul(D2_r3).mul(D2_r4).mul(D2_r5).getImmutable();

        //计算D3
        Element D3_r1 = g1_E_rvk2.duplicate().powZn(S_k).getImmutable();
        Element D3_r2 = g1_E_rvk4.duplicate().powZn(S_ou).getImmutable();
        Element D3_r3 = g1_E_g2.duplicate().powZn(S_f).getImmutable();
        Element D3 = D3_r1.duplicate().mul(D3_r2).mul(D3_r3).getImmutable();

        //计算D4
        Element D4 = g1.duplicate().powZn(S_o).getImmutable();

        Element mess = ElementOperation.StringConvertZrElement(message);

        Element c = ElementOperation.Hash(phi_1, phi_2, phi_3, credagg_ran, D1, D2, D3, D4, mess);

        Element W_u = S_u.duplicate().add(c.duplicate().mul(usk)).getImmutable();
        Element W_f = S_f.duplicate().add(c.duplicate().mul(fr)).getImmutable();
        Element W_k = S_k.duplicate().add(c.duplicate().mul(nodeValue)).getImmutable();
        Element W_o = S_o.duplicate().add(c.duplicate().mul(o)).getImmutable();
        Element W_beta = S_beta.duplicate().add(c.duplicate().mul(beta)).getImmutable();
        Element W_ou = S_ou.duplicate().add(c.duplicate().mul(Rv)).getImmutable();
        return new Proof(phi_1, phi_2, phi_3, credagg_ran, c, W_u, W_f, W_k, W_o, W_beta, W_ou, attr, credential.getIvkList(), message);
    }


}
