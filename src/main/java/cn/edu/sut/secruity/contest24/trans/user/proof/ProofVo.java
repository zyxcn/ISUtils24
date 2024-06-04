package cn.edu.sut.secruity.contest24.trans.user.proof;

import lombok.Data;

@Data
public class ProofVo {
    private String phi_1;
    private String phi_2;
    private String phi_3;
    private String credAgg;
    private String c;
    private String W_u;
    private String W_f;
    private String W_k;
    private String W_o;
    private String W_beta;
    private String W_ou;
    private String[][] attributes;
    private String[][] ivk;
    private String msg;
}
