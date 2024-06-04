import cn.edu.sut.secruity.contest24.operation.result.userproof.UserProof;
import cn.edu.sut.secruity.contest24.param.PublicParam;
import cn.edu.sut.secruity.contest24.trans.user.info.UserInfo;
import cn.edu.sut.secruity.contest24.trans.user.info.UserInfoVo;
import cn.edu.sut.secruity.contest24.trans.user.proof.UserProofVo;
import org.junit.Test;

public class TransTest {

    @Test
    public void testTransUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setRToken(PublicParam.g2);
        UserInfoVo userInfoVo = UserInfo.toVo(userInfo);
        System.out.println(userInfoVo);
    }

    @Test
    public void testTransUserProof() {
        UserProof proof = new UserProof();
        proof.setUvk(PublicParam.g1);
        UserProofVo proofVo = UserProof.toVo(proof);
        System.out.println(proofVo);
        System.out.println("fastjson".endsWith("json"));
    }
}
