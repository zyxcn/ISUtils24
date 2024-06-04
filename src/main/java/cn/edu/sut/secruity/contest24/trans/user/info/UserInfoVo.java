package cn.edu.sut.secruity.contest24.trans.user.info;

import lombok.Data;

@Data
public class UserInfoVo {
    private Long id;
    private String rToken;
    private String nodeValue;
    private String rv;
    private String fr;
    private String tToken;
}
