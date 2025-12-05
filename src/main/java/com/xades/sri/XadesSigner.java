package com.xades.sri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.Transformer;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.*;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class XadesSigner {

    private static final String KEY_STORE_TYPE = "PKCS12";

    public static void signXml(String xmlPath, String outputPath, String p12Path, String password) throws Exception {
        // 1. Load the KeyStore and get PrivateKey and Certificate
        KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
        try (FileInputStream fis = new FileInputStream(p12Path)) {
            ks.load(fis, password.toCharArray());
        }

        String alias = ks.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

        // 2. Parse the XML document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlPath));

        // 3. Prepare the XML Signature Factory
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // 4. Create the Reference to the root element (URI="")
        Reference ref = fac.newReference(
                "",
                fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                null,
                null);

        // 5. Create the SignedInfo
        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref));

        // 6. Create the KeyInfo
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List<Object> x509Content = new ArrayList<>();
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        // 7. Create the Object for XAdES properties
        // We need to manually build the QualifyingProperties element
        String signatureId = "Signature-" + UUID.randomUUID().toString();
        XMLObject xadesObject = createXadesObject(fac, doc, cert, signatureId);

        // 8. Create the XMLSignature
        // We add the XAdES object to the signature
        XMLSignature signature = fac.newXMLSignature(si, ki, Collections.singletonList(xadesObject), signatureId, null);

        // 9. Create the DOMSignContext
        // SRI expects the signature to be enveloped, usually at the end of the document
        DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());

        // 10. Sign
        signature.sign(dsc);

        // 11. Save the result
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(outputPath)));

        System.out.println("XML signed successfully: " + outputPath);
    }

    private static XMLObject createXadesObject(XMLSignatureFactory fac, Document doc, X509Certificate cert,
            String signatureId) throws Exception {
        // Create the QualifyingProperties DOM structure
        String xadesNs = "http://uri.etsi.org/01903/v1.3.2#";
        String xadesPrefix = "etsi";

        Element qualifyingProperties = doc.createElementNS(xadesNs, xadesPrefix + ":QualifyingProperties");
        qualifyingProperties.setAttribute("Target", "#" + signatureId);

        Element signedProperties = doc.createElementNS(xadesNs, xadesPrefix + ":SignedProperties");
        signedProperties.setAttribute("Id", "SignedProperties-" + UUID.randomUUID().toString());
        qualifyingProperties.appendChild(signedProperties);

        Element signedSignatureProperties = doc.createElementNS(xadesNs, xadesPrefix + ":SignedSignatureProperties");
        signedProperties.appendChild(signedSignatureProperties);

        // SigningTime
        Element signingTime = doc.createElementNS(xadesNs, xadesPrefix + ":SigningTime");
        signingTime.setTextContent(
                java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(java.time.ZonedDateTime.now()));
        signedSignatureProperties.appendChild(signingTime);

        // SigningCertificate
        Element signingCertificate = doc.createElementNS(xadesNs, xadesPrefix + ":SigningCertificate");
        signedSignatureProperties.appendChild(signingCertificate);

        Element certElement = doc.createElementNS(xadesNs, xadesPrefix + ":Cert");
        signingCertificate.appendChild(certElement);

        Element certDigest = doc.createElementNS(xadesNs, xadesPrefix + ":CertDigest");
        certElement.appendChild(certDigest);

        Element digestMethod = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:DigestMethod");
        digestMethod.setAttribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#sha1");
        certDigest.appendChild(digestMethod);

        Element digestValue = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:DigestValue");
        // Calculate SHA-1 digest of the certificate
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(cert.getEncoded());
        digestValue.setTextContent(java.util.Base64.getEncoder().encodeToString(digest));
        certDigest.appendChild(digestValue);

        Element issuerSerial = doc.createElementNS(xadesNs, xadesPrefix + ":IssuerSerial");
        certElement.appendChild(issuerSerial);

        Element x509IssuerName = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:X509IssuerName");
        x509IssuerName.setTextContent(cert.getIssuerX500Principal().getName());
        issuerSerial.appendChild(x509IssuerName);

        Element x509SerialNumber = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:X509SerialNumber");
        x509SerialNumber.setTextContent(cert.getSerialNumber().toString());
        issuerSerial.appendChild(x509SerialNumber);

        // SignedDataObjectProperties (Optional for basic BES but good to have empty or
        // minimal if needed)
        Element signedDataObjectProperties = doc.createElementNS(xadesNs, xadesPrefix + ":SignedDataObjectProperties");
        signedProperties.appendChild(signedDataObjectProperties);

        Element dataObjectFormat = doc.createElementNS(xadesNs, xadesPrefix + ":DataObjectFormat");
        dataObjectFormat.setAttribute("ObjectReference", "#Reference-1"); // This needs to match the Reference Id if we
                                                                          // set one.
        // Since we didn't set an ID on the main reference, we might skip
        // DataObjectFormat or we need to ensure the Reference has an ID.
        // For simple SRI XAdES-BES, SignedDataObjectProperties is often omitted or kept
        // simple.
        // Let's remove DataObjectFormat to avoid ID mismatch issues unless strictly
        // required.
        // signedDataObjectProperties.appendChild(dataObjectFormat);
        // Element mimeType = doc.createElementNS(xadesNs, xadesPrefix + ":MimeType");
        // mimeType.setTextContent("text/xml");
        // dataObjectFormat.appendChild(mimeType);

        // Wrap in XMLObject
        return fac.newXMLObject(Collections.singletonList(new javax.xml.crypto.dom.DOMStructure(qualifyingProperties)),
                null, null, null);
    }
}
