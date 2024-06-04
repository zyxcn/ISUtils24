package cn.edu.sut.secruity.contest24.operation.issuer;

import cn.edu.sut.secruity.contest24.credential.Credential;
import cn.edu.sut.secruity.contest24.param.PublicParam;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

public class IssuerOperation {
    /**
     * 根据用户的公钥颁发基于属性的凭证
     *
     * @param uvk
     * @param attributeSize
     * @param iskList
     * @param attributes
     * @return
     */
    public static Credential getCredential(Element uvk, int attributeSize, Element[] ivkList, Element[] iskList, String[] attributes) {
        Element cred = null;
        Field Zr = PublicParam.Zr;
        if (attributes.length != attributeSize || iskList.length != attributeSize + 2)
            throw new IllegalArgumentException("参数错误!");

        Element[] attribute = new Element[attributeSize];
        Element[] attrGenerate = new Element[attributeSize];
        for (int i = 0; i < attributeSize; i++) {
            byte[] attributeByte = attributes[i].getBytes();
            attribute[i] = Zr.newElementFromBytes(attributeByte, 0);
            attrGenerate[i] = iskList[i + 1].duplicate().mul(attribute[i]).getImmutable();
        }

        Element expPrefix = attrGenerate[0];
        for (int i = 1; i < attributeSize; i++) {
            expPrefix = expPrefix.duplicate().add(attrGenerate[i]);
        }

        Element exp = iskList[0].duplicate().add(expPrefix).add(iskList[iskList.length - 1]);

        cred = uvk.duplicate().powZn(exp).getImmutable();

        Credential credential = new Credential();
        credential.setCredential(cred);
        credential.setAttributes(attributes);
        credential.setIvkList(ivkList);
        return credential;
    }
}
