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

            System.out.println("===========================================");
            System.out.println("  XAdES-BES Signer for SRI (using XAdES4j)");
            System.out.println("===========================================");
            System.out.println();
            
            // Sign using XAdES4j
            XadesSigner.signXml(xmlPath, outputPath, p12Path, password);
            
            System.out.println();
            System.out.println("Process completed successfully!");
            System.out.println("Signed file: " + outputPath);

        } catch (Exception e) {
            System.err.println("ERROR: Signing process failed!");
            System.err.println("Reason: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
