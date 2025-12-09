package com.xades.sri;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Application: XAdES-BES Signer");
        
        try {
            String xmlPath = "src/main/resources/test.xml";
            String outputPath = "src/main/resources/test_signed.xml";
            String p12Path = "src/main/key/mr.p12";
            // TODO: Cambie esto por la contraseña real de mr.p12
            String password = "ECUA2024";

            if (!checkFileExists(xmlPath)) {
                logger.error("XML file not found: {}", xmlPath);
                System.exit(1);
            }

            if (!checkFileExists(p12Path)) {
                logger.error("P12 Keystore not found: {}", p12Path);
                System.exit(1);
            }

            // Ensure output directory exists
            new File(outputPath).getParentFile().mkdirs();

            // Sign using XAdES4j
            XadesSigner.signXml(xmlPath, outputPath, p12Path, password);
            
            logger.info("Process completed successfully!");
            logger.info("File signed at: {}", outputPath);

        } catch (Exception e) {
            logger.error("Signing process failed!", e);
            System.exit(1);
        }
    }

    private static boolean checkFileExists(String path) {
        return new File(path).exists();
    }
}
