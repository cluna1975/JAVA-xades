package com.xades.sri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.*;
import xades4j.properties.*;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.impl.DirectPasswordProvider;
import xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider;
import xades4j.algorithms.GenericAlgorithm;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Security;

/**
 * Advanced XAdES Signer with additional features and customization options
 * 
 * This class demonstrates advanced usage of XAdES4j including:
 * - Custom signature properties
 * - Different signature algorithms
 * - Additional signed properties
 * - Production place and signer role
 */
public class XadesSignerAdvanced {

    private static final String KEY_STORE_TYPE = "PKCS12";
    
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * Signs an XML document with XAdES-BES signature with advanced options
     * 
     * @param xmlPath Path to the XML file to sign
     * @param outputPath Path where the signed XML will be saved
     * @param p12Path Path to the PKCS12 keystore (.p12 file)
     * @param password Password for the keystore
     * @param signerRole Role of the signer (e.g., "Emisor", "Contador")
     * @param productionPlace City where the signature was produced
     * @throws Exception if signing fails
     */
    public static void signXmlAdvanced(
            String xmlPath, 
            String outputPath, 
            String p12Path, 
            String password,
            String signerRole,
            String productionPlace) throws Exception {
        
        System.out.println("=== Advanced XAdES4j Signing Process ===");
        System.out.println("Input XML: " + xmlPath);
        System.out.println("Output XML: " + outputPath);
        System.out.println("Signer Role: " + signerRole);
        System.out.println("Production Place: " + productionPlace);
        
        // 1. Parse the XML document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlPath));
        Element rootElement = doc.getDocumentElement();

        // 2. Configure the keystore provider
        KeyingDataProvider keyingProvider = createKeyingDataProvider(p12Path, password);

        // 3. Create XAdES-BES signing profile with custom properties
        XadesBesSigningProfile signingProfile = new XadesBesSigningProfile(keyingProvider);
        
        // Add custom signed signature properties
        signingProfile.withSignaturePropertiesProvider(new SignaturePropertiesProvider() {
            @Override
            public void provideProperties(SignaturePropertiesCollector signaturePropsCol) {
                // Add signer role
                if (signerRole != null && !signerRole.isEmpty()) {
                    signaturePropsCol.addSignerRole(new ClaimedSignerRole(signerRole));
                }
                
                // Add production place
                if (productionPlace != null && !productionPlace.isEmpty()) {
                    signaturePropsCol.setSignatureProductionPlace(
                        new SignatureProductionPlace()
                            .withCity(productionPlace)
                            .withCountryName("Ecuador")
                    );
                }
            }
        });
        
        // Optional: Configure data object properties
        signingProfile.withDataObjectPropertiesProvider(new DataObjectPropertiesProvider() {
            @Override
            public void provideProperties(DataObjectDesc dataObj) {
                // Set MIME type for the signed data
                dataObj.withDataObjectFormat(new DataObjectFormatProperty("text/xml"));
            }
        });

        // 4. Create the signer
        XadesSigner signer = signingProfile.newSigner();

        // 5. Define what to sign
        DataObjectDesc dataObjRef = new DataObjectReference("")
                .withTransform(new EnvelopedSignatureTransform());
        
        SignedDataObjects dataObjs = new SignedDataObjects(dataObjRef);

        // 6. Sign the document
        System.out.println("Signing document with advanced properties...");
        signer.sign(dataObjs, rootElement);
        System.out.println("Signature created successfully");

        // 7. Save the signed document
        saveDocument(doc, outputPath);
        System.out.println("XML signed successfully: " + outputPath);
        System.out.println("=== Advanced Signing Complete ===");
    }

    /**
     * Signs with SHA-256 instead of SHA-1 (more secure)
     */
    public static void signXmlWithSHA256(
            String xmlPath, 
            String outputPath, 
            String p12Path, 
            String password) throws Exception {
        
        System.out.println("=== Signing with SHA-256 ===");
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlPath));
        Element rootElement = doc.getDocumentElement();

        KeyingDataProvider keyingProvider = createKeyingDataProvider(p12Path, password);
        XadesBesSigningProfile signingProfile = new XadesBesSigningProfile(keyingProvider);
        
        // Configure to use SHA-256
        signingProfile.withSignatureAlgorithms(new SignatureAlgorithms()
            .withSignatureAlgorithm("RSA", 
                new GenericAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"))
            .withDigestAlgorithmForDataObjsReferences(
                new GenericAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256"))
            .withDigestAlgorithmForReferenceProperties(
                new GenericAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256"))
        );

        XadesSigner signer = signingProfile.newSigner();
        DataObjectDesc dataObjRef = new DataObjectReference("")
                .withTransform(new EnvelopedSignatureTransform());
        SignedDataObjects dataObjs = new SignedDataObjects(dataObjRef);

        System.out.println("Signing with SHA-256 algorithm...");
        signer.sign(dataObjs, rootElement);
        
        saveDocument(doc, outputPath);
        System.out.println("XML signed with SHA-256: " + outputPath);
    }

    /**
     * Signs multiple data objects in a single signature
     */
    public static void signMultipleReferences(
            String xmlPath, 
            String outputPath, 
            String p12Path, 
            String password,
            String[] elementIds) throws Exception {
        
        System.out.println("=== Signing Multiple References ===");
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlPath));
        Element rootElement = doc.getDocumentElement();

        KeyingDataProvider keyingProvider = createKeyingDataProvider(p12Path, password);
        XadesBesSigningProfile signingProfile = new XadesBesSigningProfile(keyingProvider);
        XadesSigner signer = signingProfile.newSigner();

        // Create multiple references
        SignedDataObjects dataObjs = new SignedDataObjects();
        
        // Add reference to whole document
        dataObjs.addDataObject(new DataObjectReference("")
                .withTransform(new EnvelopedSignatureTransform()));
        
        // Add references to specific elements
        if (elementIds != null) {
            for (String elementId : elementIds) {
                dataObjs.addDataObject(new DataObjectReference("#" + elementId));
            }
        }

        System.out.println("Signing " + (elementIds != null ? elementIds.length + 1 : 1) + " references...");
        signer.sign(dataObjs, rootElement);
        
        saveDocument(doc, outputPath);
        System.out.println("XML signed with multiple references: " + outputPath);
    }

    private static KeyingDataProvider createKeyingDataProvider(String p12Path, String password) 
            throws Exception {
        return new FileSystemKeyStoreKeyingDataProvider(
                KEY_STORE_TYPE,
                p12Path,
                new DirectPasswordProvider(password),
                new DirectPasswordProvider(password),
                true
        );
    }

    private static void saveDocument(Document doc, String outputPath) throws Exception {
        new File(outputPath).getParentFile().mkdirs();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(outputPath)));
    }

    /**
     * Example usage
     */
    public static void main(String[] args) {
        try {
            String xmlPath = "src/main/resources/test.xml";
            String outputPath = "src/main/resources/test_signed_advanced.xml";
            String p12Path = "src/main/key/mr.p12";
            String password = "ECUA2024";

            // Example 1: Sign with advanced properties
            signXmlAdvanced(
                xmlPath, 
                outputPath, 
                p12Path, 
                password,
                "Emisor de Factura Electrónica",  // Signer role
                "Quito"                            // Production place
            );

            // Example 2: Sign with SHA-256
            // signXmlWithSHA256(xmlPath, "test_signed_sha256.xml", p12Path, password);

            // Example 3: Sign multiple references
            // String[] elementIds = {"element1", "element2"};
            // signMultipleReferences(xmlPath, "test_signed_multi.xml", p12Path, password, elementIds);

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
