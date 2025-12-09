package com.xades.sri;

import xades4j.providers.impl.KeyStoreKeyingDataProvider.SigningCertSelector;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Selects the first available certificate in the keystore.
 */
public class FirstCertificateSelector implements SigningCertSelector {
    @Override
    public X509Certificate selectCertificate(List<X509Certificate> availableCertificates) {
        return availableCertificates != null && !availableCertificates.isEmpty() 
            ? availableCertificates.get(0) 
            : null;
    }
}
