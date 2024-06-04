package cn.edu.sut.secruity.contest24.operation.result.proof;

import cn.edu.sut.secruity.contest24.operation.result.ElementStringConverter;
import cn.edu.sut.secruity.contest24.trans.user.proof.ProofVo;
import cn.edu.sut.secruity.contest24.util.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proof {
    private Element phi_1;
    private Element phi_2;
    private Element phi_3;
    private Element credAgg;
    private Element c;
    private Element W_u;
    private Element W_f;
    private Element W_k;
    private Element W_o;
    private Element W_beta;
    private Element W_ou;
    private String[][] attributes;
    private Element[][] ivk;
    private String msg;

    public static Proof fromVo(ProofVo proofVo) {
        Proof proof = new Proof();
        proof.setAttributes(proofVo.getAttributes());
        proof.setMsg(proofVo.getMsg());
        String[][] ivkStr = proofVo.getIvk();
        Element[][] ivkList = new Element[ivkStr.length][];
        for (int i = 0; i < ivkList.length; i++) {
            ivkList[i] = new Element[ivkStr[i].length];
            for (int j = 0; j < ivkList[i].length; j++)
                ivkList[i][j] = ElementOperation.getElementFromString(ivkStr[i][j]);
        }
        proof.setIvk(ivkList);
        ElementStringConverter.stringToElement(proofVo, proof);
        return proof;
    }

    public static ProofVo toVo(Proof proof) {
        ProofVo proofVo = new ProofVo();
        proofVo.setMsg(proof.msg);
        proofVo.setAttributes(proof.getAttributes());
        Element[][] ivkList = proof.getIvk();
        String[][] ivkStr = new String[ivkList.length][];
        for (int i = 0; i < ivkStr.length; i++) {
            ivkStr[i] = new String[ivkList[i].length];
            for (int j = 0; j < ivkStr[i].length; j++)
                ivkStr[i][j] = ElementOperation.getElementString(ivkList[i][j]);
        }
        proofVo.setIvk(ivkStr);
        ElementStringConverter.elementToString(proof, proofVo);
        return proofVo;
    }
}
