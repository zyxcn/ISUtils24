package cn.edu.sut.secruity.contest24;

import it.unisa.dia.gas.jpbc.Element;

public class Bechmark {
    public void FBechmark(RAAScheme param) {
        Element G1 = param.pairing.getG1().newRandomElement().getImmutable();
        Element G2 = param.pairing.getG2().newRandomElement().getImmutable();
        Element GT = param.pairing.getGT().newRandomElement().getImmutable();
        Element Zr = param.pairing.getZr().newRandomElement().getImmutable();
        Element pa = param.pairing.pairing(G1, G2);

        System.out.println("Computation benchmark of the type F curve");
        long start_g1 = System.currentTimeMillis();
        Element g1_exp = G1.powZn(Zr).duplicate();
        long end_g1 = System.currentTimeMillis();
        System.out.println("g1_exp = " + (start_g1 - end_g1) + " ms");

        long start_g2 = System.currentTimeMillis();
        Element g2_exp = G2.powZn(Zr).duplicate();
        long end_g2 = System.currentTimeMillis();
        System.out.println("g2_exp = " + (start_g2 - end_g2) + " ms");

        long start_gt = System.currentTimeMillis();
        Element gt_exp = GT.powZn(Zr).duplicate();
        long end_gt = System.currentTimeMillis();
        System.out.println("gt_exp = " + (start_gt - end_gt) + " ms");

        long start_pa = System.currentTimeMillis();
        Element pai = param.pairing.pairing(g1_exp, g2_exp).duplicate();
        long end_pa = System.currentTimeMillis();
        System.out.println("p_exp = " + (start_pa - end_pa) + " ms");

        System.out.println();


        System.out.println("Communication bechmark of the type F curve");
        System.out.println("G1 = " + G1.getLengthInBytes() + " bytes");
        System.out.println("G2 = " + G2.getLengthInBytes() + " bytes");
        System.out.println("GT = " + GT.getLengthInBytes() + " bytes");
        System.out.println("Zr = " + Zr.getLengthInBytes() + " bytes");
        System.out.println("pa = " + pa.getLengthInBytes() + " bytes");


    }
}
