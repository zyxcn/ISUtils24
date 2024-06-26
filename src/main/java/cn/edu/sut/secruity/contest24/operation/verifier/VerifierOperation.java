package cn.edu.sut.secruity.contest24.operation.verifier;

import cn.edu.sut.secruity.contest24.credential.AggregationCredential;
import cn.edu.sut.secruity.contest24.operation.result.proof.Proof;
import cn.edu.sut.secruity.contest24.param.PublicParam;
import cn.edu.sut.secruity.contest24.param.RegisterParam;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class VerifierOperation {
    /**
     * 对聚合凭证进行验证
     *
     * @param uvk
     * @param aggregationCredential
     * @param ivkList
     * @return
     */
    public static Boolean AggregationCredVerify(Element uvk, AggregationCredential aggregationCredential, Element[]... ivkList) {
        Element g2 = PublicParam.g2;
        Element credential = aggregationCredential.getAggCredential();
        String[][] attributes = aggregationCredential.getAttributes();

        Pairing pairing = PublicParam.pairing;

        Element left = pairing.pairing(credential, g2);

        Element[][] attributesElement = new Element[attributes.length][];
        for (int i = 0; i < attributes.length; i++) {
            attributesElement[i] = new Element[attributes[i].length];
            for (int j = 0; j < attributes[i].length; j++) {
                byte[] attribute = attributes[i][j].getBytes();
                attributesElement[i][j] = pairing.getZr().newElementFromBytes(attribute, 0);
            }
        }

        Element[] rigs = new Element[attributes.length];

        for (int i = 0; i < attributes.length; i++) {
            Element rig = ivkList[i][1].duplicate().powZn(attributesElement[i][0]);
            for (int j = 1; j < attributes[i].length; j++)
                rig = rig.duplicate().mul(ivkList[i][j + 1].duplicate().powZn(attributesElement[i][j]));
            rig = ivkList[i][0].duplicate().mul(rig).getImmutable();
            rig = rig.duplicate().mul(ivkList[i][ivkList[i].length - 1]);
            rigs[i] = rig;
        }

        Element aggregationRig = rigs[0];
        for (int i = 1; i < rigs.length; i++)
            aggregationRig = aggregationRig.duplicate().mul(rigs[i]).getImmutable();

        Element right = pairing.pairing(uvk, aggregationRig);

        return right.isEqual(left);
    }

    public static Boolean Verify(Proof proof) {

        //引入参数
        Pairing pairing = PublicParam.pairing;
        Element g2 = PublicParam.g2;
        Element g__1 = PublicParam.g__1;
        Element g_1 = PublicParam.g_1;
        Element g1 = PublicParam.g1;

        Element rtvk = RegisterParam.rtvk;
        Element[] rvk = RegisterParam.rvk;
        Element[][] ivk = proof.getIvk();
        String message = proof.getMsg();

        String epoch = PublicParam.epoch;
        Element epoch_t = ElementOperation.StringConvertZrElement(epoch).getImmutable();

        Element phi_1 = proof.getPhi_1().getImmutable();
        Element phi_2 = proof.getPhi_2().getImmutable();
        Element phi_3 = proof.getPhi_3().getImmutable();
        Element credAgg = proof.getCredAgg().getImmutable();
        Element c = proof.getC().getImmutable();
        Element c_negate = c.duplicate().negate();
        Element W_u = proof.getW_u().getImmutable();
        Element W_f = proof.getW_f().getImmutable();
        Element W_k = proof.getW_k().getImmutable();
        Element W_o = proof.getW_o().getImmutable();
        Element W_beta = proof.getW_beta().getImmutable();
        Element W_ou = proof.getW_ou().getImmutable();

        String[][] attr = proof.getAttributes();

        //计算D_1
        Element[][] attribute = new Element[attr.length][];

        for (int i = 0; i < attr.length; i++) {
            attribute[i] = new Element[attr[i].length];
            for (int j = 0; j < attr[i].length; j++) {
                byte[] att = attr[i][j].getBytes();
                attribute[i][j] = pairing.getZr().newElementFromBytes(att, 0);
            }
        }

        Element rig0 = ivk[0][1].duplicate().powZn(attribute[0][0]);

        for (int i = 1; i < attr[0].length; i++) {
            rig0 = rig0.duplicate().mul(ivk[0][i + 1].duplicate().powZn(attribute[0][i]));//不知道ivk[1][]签名的属性个数，计算ivk1,i^a1,i
        }

        Element rigr = ivk[0][0].duplicate().mul(rig0).getImmutable();//计算ivk1,0*ivk1,i^a1,i
        Element rigg = rigr.duplicate().mul(ivk[0][ivk[0].length - 1]);//计算j=1下的ivk1,0*ivk1,i^a1,i*ivk1,ivk1.length-1

        for (int i = 1; i < ivk.length; i++) {
            Element r1 = ivk[i][1].duplicate().powZn(attribute[i][0]);
            for (int j = 1; j < attr[i].length; j++) {
                r1 = r1.duplicate().mul(ivk[i][j + 1].duplicate().powZn(attribute[i][j]));//计算出j下的ivkj,j^aj,j;
            }
            Element r2 = ivk[i][0].duplicate().mul(r1).getImmutable();
            Element r3 = r2.duplicate().mul(ivk[i][ivk[i].length - 1]);
            rigg = rigg.duplicate().mul(r3).getImmutable();//将j个凭证值连乘
        }


        //计算D_1
        Element D1_r1 = pairing.pairing(phi_3.duplicate().powZn(W_u), rigg).getImmutable();
        Element D1_r2 = pairing.pairing(credAgg.duplicate().powZn(c_negate), g2).getImmutable();
        Element D_1 = D1_r1.duplicate().mul(D1_r2).getImmutable();

        Element g__1_E_g2 = pairing.pairing(g__1, g2).getImmutable();
        Element g1_E_g2 = pairing.pairing(g1, g2).getImmutable();
        Element rtvk_E_rvk4 = pairing.pairing(rtvk, rvk[3]).getImmutable();
        Element rtvk_E_g2 = pairing.pairing(rtvk, g2).getImmutable();
        Element g1_E_rvk2 = pairing.pairing(g1, rvk[1]).getImmutable();
        Element g1_E_rvk4 = pairing.pairing(g1, rvk[3]).getImmutable();
        Element g_1_E_g2 = pairing.pairing(g_1, g2).getImmutable();
        Element g1_E_rvk1_rvk3_epoch = pairing.pairing(g1, rvk[0].duplicate().mul(rvk[2].duplicate().powZn(epoch_t))).getImmutable();

        //计算D_2
        Element D2_r1 = pairing.pairing(phi_1.duplicate().powZn(W_f.duplicate().negate()), g2).getImmutable();
        Element D2_r2 = g__1_E_g2.duplicate().powZn(W_k).getImmutable();
        Element D2_r3 = g1_E_g2.duplicate().powZn(W_u).getImmutable();
        Element D2_r4 = rtvk_E_rvk4.duplicate().powZn(W_o).getImmutable();
        Element D2_r5 = rtvk_E_g2.duplicate().powZn(W_beta).getImmutable();
        Element D2_r6_1 = pairing.pairing(phi_1, rvk[3]).getImmutable();

        //Element D2_r6 = (D2_r6_1.duplicate().powZn(c_negate).mul(g_1_E_g2.duplicate().powZn(c))).getImmutable();

        Element D2_r6 = (D2_r6_1.duplicate().div(g_1_E_g2)).powZn(c_negate).getImmutable();
        Element D_2 = D2_r1.duplicate().mul(D2_r2).mul(D2_r3).mul(D2_r4).mul(D2_r5).mul(D2_r6).getImmutable();

        //计算D_3
        Element D3_r1 = g1_E_rvk2.duplicate().powZn(W_k).getImmutable();
        Element D3_r2 = g1_E_rvk4.duplicate().powZn(W_ou).getImmutable();
        Element D3_r3 = g1_E_g2.duplicate().powZn(W_f).getImmutable();
        Element D3_r4_1 = pairing.pairing(phi_2, g2).getImmutable();
        Element D3_r4 = (D3_r4_1.duplicate().div(g1_E_rvk1_rvk3_epoch)).powZn(c_negate).getImmutable();
        Element D_3 = D3_r1.duplicate().mul(D3_r2).mul(D3_r3).mul(D3_r4).getImmutable();

        //计算D_4
        Element D4_r1 = g1.duplicate().powZn(W_o).getImmutable();
        Element D4_r2 = phi_3.duplicate().powZn(c_negate).getImmutable();
        Element D_4 = D4_r1.duplicate().mul(D4_r2).getImmutable();

        //将消息转成byte数组元素
        Element mes = ElementOperation.StringConvertZrElement(message);

        //Element cc = ElementOperation.Hash(phi_1,D_1);
        Element cc = ElementOperation.Hash(phi_1, phi_2, phi_3, credAgg, D_1, D_2, D_3, D_4, mes);
        return cc.isEqual(c);
    }
}
