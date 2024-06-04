package cn.edu.sut.secruity.contest24.operation.result.userproof;

import cn.edu.sut.secruity.contest24.operation.result.ElementStringConverter;
import cn.edu.sut.secruity.contest24.trans.user.proof.UserProofVo;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

@Data
public class UserProof {
    private Element uvk;
    private Element HashedR;
    private Element Sb;

    public static UserProof fromVo(UserProofVo userProofVo) {
        UserProof userProof = new UserProof();
        ElementStringConverter.stringToElement(userProofVo, userProof);
        return userProof;
    }

    public static UserProofVo toVo(UserProof UserProof) {
        UserProofVo UserProofVo = new UserProofVo();
        ElementStringConverter.elementToString(UserProof, UserProofVo);
        return UserProofVo;
    }
}
