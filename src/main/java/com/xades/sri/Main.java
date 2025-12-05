package com.xades.sri;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            String xmlPath = "src/main/resources/test.xml";
            String outputPath = "src/main/resources/test_signed.xml";
            String p12Path = "src/main/key/mr.p12";
            // TODO: Cambie esto por la contraseña real de mr.p12
            String password = "ECUA2024";

            // Ensure output directory exists
            new File(outputPath).getParentFile().mkdirs();

            System.out.println("Starting signing process...");
            XadesSigner.signXml(xmlPath, outputPath, p12Path, password);
            System.out.println("Finished.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
