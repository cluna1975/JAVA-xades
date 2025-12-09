package com.xades.sri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.DataObjectReference;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesBesSigningProfile;
import xades4j.properties.DataObjectDesc;
import xades4j.providers.KeyingDataProvider;
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
public class XadesSigner {

    private static final Logger logger = LoggerFactory.getLogger(XadesSigner.class);
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
        logger.info("Starting XAdES signing process...");
        logger.debug("Input XML: {}", xmlPath);
        logger.debug("Keystore: {}", p12Path);
        
        // 1. Parse the XML document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlPath));
        
        Element rootElement = doc.getDocumentElement();
        logger.debug("Root element: {}", rootElement.getNodeName());

        // 2. Configure the keystore provider
        KeyingDataProvider keyingProvider = createKeyingDataProvider(p12Path, password);

        // 3. Create XAdES-BES signing profile
        XadesBesSigningProfile signingProfile = new XadesBesSigningProfile(keyingProvider);

        // 4. Create the signer
        xades4j.production.XadesSigner signer = signingProfile.newSigner();

        // 5. Define what to sign - the entire document (enveloped signature)
        DataObjectDesc dataObjRef = new DataObjectReference("")
                .withTransform(new EnvelopedSignatureTransform());
        
        SignedDataObjects dataObjs = new SignedDataObjects(dataObjRef);

        // 6. Sign the document
        logger.info("Signing document...");
        signer.sign(dataObjs, rootElement);
        logger.info("Signature created successfully");

        // 7. Save the signed document
        saveDocument(doc, outputPath);
        logger.info("Signed XML saved to: {}", outputPath);
    }

    /**
     * Creates a KeyingDataProvider for the PKCS12 keystore
     */
    private static KeyingDataProvider createKeyingDataProvider(String p12Path, String password) 
            throws Exception {
        
        // XAdES4j uses FileSystemKeyStoreKeyingDataProvider for file-based keystores
        return new FileSystemKeyStoreKeyingDataProvider(
                KEY_STORE_TYPE,
                p12Path,
                new FirstCertificateSelector(),
                new DirectPasswordProvider(password),
                new DirectPasswordProvider(password),
                true  // returnFullChain - returns the full certificate chain
        );
    }

    /**
     * Saves the signed XML document to a file
     */
    private static void saveDocument(Document doc, String outputPath) throws Exception {
        // Ensure output directory exists
        new File(outputPath).getParentFile().mkdirs();
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(outputPath)));
    }
}
