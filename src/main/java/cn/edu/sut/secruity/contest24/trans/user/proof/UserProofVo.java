package cn.edu.sut.secruity.contest24.trans.user.proof;

import lombok.Data;

@Data
public class UserProofVo {
    private String phone;
    private String uvk;
    private String HashedR;
    private String Sb;
}
