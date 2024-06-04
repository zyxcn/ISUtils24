package cn.edu.sut.secruity.contest24.operation.common;

import cn.edu.sut.secruity.contest24.operation.result.userproof.UserProof;
import cn.edu.sut.secruity.contest24.param.PublicParam;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

/**
 * some common verify operation
 */
public class CommonOperation {
    /**
     * 根据用户提交的证据验证用户是否持有对应的私钥
     *
     * @param ivk
     * @param userProof
     * @return
     */
    public static Boolean isUserHaveSecretKey(Element ivk, UserProof userProof) {
        Field Zr = PublicParam.Zr;
        Field G2 = PublicParam.G2;
        Element g1 = PublicParam.g1;
        Element g2 = PublicParam.g2;

        Element Sb = userProof.getSb();
        Element uvk = userProof.getUvk();
        Element hashedR = userProof.getHashedR();

        Element Rl = g1.duplicate().powZn(Sb).getImmutable();
        Element Rr = uvk.duplicate().powZn(hashedR).negate();
        Element R1 = Rl.duplicate().mul(Rr).getImmutable();
        Element hashedElement1 = ElementOperation.Hash(ivk, uvk, R1);
        return hashedElement1.isEqual(hashedR);
    }

}
