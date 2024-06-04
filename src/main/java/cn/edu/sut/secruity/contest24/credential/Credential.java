package cn.edu.sut.secruity.contest24.credential;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

@Data
public class Credential {
    private Element credential;
    private String[] attributes;
    private Element[] ivkList;
}