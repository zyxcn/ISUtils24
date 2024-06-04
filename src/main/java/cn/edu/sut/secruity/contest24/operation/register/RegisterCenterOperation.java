package cn.edu.sut.secruity.contest24.operation.register;

import cn.edu.sut.secruity.contest24.operation.common.CommonOperation;
import cn.edu.sut.secruity.contest24.operation.result.proof.Proof;
import cn.edu.sut.secruity.contest24.operation.result.userproof.UserProof;
import cn.edu.sut.secruity.contest24.param.PublicParam;
import cn.edu.sut.secruity.contest24.param.RegisterParam;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;

public class RegisterCenterOperation {
    /**
     * 生成追踪令牌
     * 先验证用户是否真的持有私钥
     * 在根据fr参数和specialValue生成令牌
     *
     * @param userProof 用户提交的证明
     * @param nodeValue 节点对应的特殊值,系统分配
     * @param fr        负责溯源的元素
     * @return 追踪令牌
     */
    public static Element createUserTraceToken(UserProof userProof, Element nodeValue, Element fr) {
        Element[] rvk = RegisterParam.rvk;
        Element[] rsk = RegisterParam.rsk;
        Element rvk1 = rvk[0];
        Element rsk3 = rsk[3];

        Boolean isUserValid = CommonOperation.isUserHaveSecretKey(rvk1, userProof);
        if (isUserValid) {
            Element g_1 = PublicParam.g_1;
            Element g__1 = PublicParam.g__1;
            Element uvk = userProof.getUvk();

            Element expPrefix = rsk3.add(fr).getImmutable();
            Element exp = expPrefix.duplicate().invert().getImmutable();
            Element basePrefix = g__1.duplicate().powZn(nodeValue).duplicate().mul(uvk).getImmutable();
            Element base = g_1.duplicate().mul(basePrefix).getImmutable();
            Element tToken = base.duplicate().powZn(exp).getImmutable();
            return tToken;
        } else {
            return null;
        }
    }

    /**
     * 生成撤销令牌
     *
     * @param nodeValue 节点对应的特殊值,系统分配
     * @param Rv        负责溯源的元素
     * @return 撤销令牌
     */
    public static Element createUserRevokeToken(Element nodeValue, Element Rv) {
        String epoch = PublicParam.epoch;
        Element g1 = PublicParam.g1;

        Element[] rsk = RegisterParam.rsk;

        Element epochElement = ElementOperation.StringConvertZrElement(epoch);
        Element exp1 = rsk[1].duplicate().mul(nodeValue).getImmutable();
        Element exp2 = rsk[2].duplicate().mul(epochElement).getImmutable();
        Element exp3 = rsk[3].duplicate().mul(Rv).getImmutable();
        Element exp4 = rsk[0].duplicate().add(exp1).getImmutable();
        Element exp5 = exp4.duplicate().add(exp2).getImmutable();
        Element exp = exp5.duplicate().add(exp3).getImmutable();
        return g1.duplicate().powZn(exp).getImmutable();
    }

    public static Element trace(Proof proof) {
        Element phi_1 = proof.getPhi_1();
        Element phi_3 = proof.getPhi_3();
        Element rtsk = RegisterParam.rtsk;
        Element trace_token = phi_1.duplicate().div(phi_3.duplicate().powZn(rtsk)).getImmutable();
        return trace_token;
    }

}
