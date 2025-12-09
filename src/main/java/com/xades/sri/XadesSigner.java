package com.xades.sri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.DataObjectReference;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesBesSigningProfile;
import xades4j.production.XadesSigner;
import xades4j.properties.DataObjectDesc;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.impl.DirectPasswordProvider;
import xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider;

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
 * XAdES Signer using XAdES4j library
 * This implementation creates XAdES-BES signatures compliant with SRI requirements
 */
public class XadesSignerWithXades4j {

    private static final String KEY_STORE_TYPE = "PKCS12";
    
    static {
        // Register Bouncy Castle as security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * Signs an XML document with XAdES-BES signature
     * 
     * @param xmlPath Path to the XML file to sign
     * @param outputPath Path where the signed XML will be saved
     * @param p12Path Path to the PKCS12 keystore (.p12 file)
     * @param password Password for the keystore
     * @throws Exception if signing fails
     */
    public static void signXml(String xmlPath, String outputPath, String p12Path, String password) throws Exception {
        System.out.println("=== XAdES4j Signing Process ===");
        System.out.println("Input XML: " + xmlPath);
        System.out.println("Output XML: " + outputPath);
        System.out.println("Keystore: " + p12Path);
        
        // 1. Parse the XML document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlPath));
        
        Element rootElement = doc.getDocumentElement();
        System.out.println("Root element: " + rootElement.getNodeName());

        // 2. Configure the keystore provider
        KeyingDataProvider keyingProvider = createKeyingDataProvider(p12Path, password);

        // 3. Create XAdES-BES signing profile
        XadesBesSigningProfile signingProfile = new XadesBesSigningProfile(keyingProvider);
        
        // Optional: Configure additional properties
        // signingProfile.withSignatureAlgorithms(new SignatureAlgorithms()
        //     .withSignatureAlgorithm("RSA", SignatureAlgorithm.RSA_SHA256)
        //     .withCanonicalizationAlgorithmForSignature(CanonicalizationMethod.INCLUSIVE)
        //     .withCanonicalizationAlgorithmForTimeStampProperties(CanonicalizationMethod.INCLUSIVE));

        // 4. Create the signer
        XadesSigner signer = signingProfile.newSigner();

        // 5. Define what to sign - the entire document (enveloped signature)
        DataObjectDesc dataObjRef = new DataObjectReference("")
                .withTransform(new EnvelopedSignatureTransform());
        
        SignedDataObjects dataObjs = new SignedDataObjects(dataObjRef);

        // 6. Sign the document
        System.out.println("Signing document...");
        signer.sign(dataObjs, rootElement);
        System.out.println("Signature created successfully");

        // 7. Save the signed document
        saveDocument(doc, outputPath);
        System.out.println("XML signed successfully: " + outputPath);
        System.out.println("=== Signing Complete ===");
    }

    /**
     * Creates a KeyingDataProvider for the PKCS12 keystore
     */
    private static KeyingDataProvider createKeyingDataProvider(String p12Path, String password) 
            throws Exception {
        
        // XAdES4j uses FileSystemKeyStoreKeyingDataProvider for file-based keystores
        KeyingDataProvider keyingProvider = new FileSystemKeyStoreKeyingDataProvider(
                KEY_STORE_TYPE,
                p12Path,
                new DirectPasswordProvider(password),
                new DirectPasswordProvider(password),
                true  // returnFullChain - returns the full certificate chain
        );
        
        return keyingProvider;
    }

    /**
     * Saves the signed XML document to a file
     */
    private static void saveDocument(Document doc, String outputPath) throws Exception {
        // Ensure output directory exists
        new File(outputPath).getParentFile().mkdirs();
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        
        // Optional: Format the output
        // trans.setOutputProperty(OutputKeys.INDENT, "yes");
        // trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        trans.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(outputPath)));
    }
    
    /**
     * Validates a signed XML document (optional utility method)
     * This is a placeholder - full validation would require additional configuration
     */
    public static boolean validateSignature(String signedXmlPath) throws Exception {
        System.out.println("=== XAdES Signature Validation ===");
        System.out.println("Validating: " + signedXmlPath);
        
        // Parse the signed document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(signedXmlPath));
        
        // TODO: Implement validation using XAdES4j
        // This would require:
        // 1. XadesVerificationProfile
        // 2. XadesVerifier
        // 3. Proper certificate validation chain
        
        System.out.println("Note: Full validation not yet implemented");
        System.out.println("=== Validation Complete ===");
        
        return true;
    }
}
