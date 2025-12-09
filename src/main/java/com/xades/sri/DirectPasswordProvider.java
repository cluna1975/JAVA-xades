package com.xades.sri;

import java.security.cert.X509Certificate;
import xades4j.providers.impl.KeyStoreKeyingDataProvider.KeyEntryPasswordProvider;
import xades4j.providers.impl.KeyStoreKeyingDataProvider.KeyStorePasswordProvider;

/**
 * Simple password provider that uses a direct string.
 * Implements both KeyStorePasswordProvider and KeyEntryPasswordProvider
 * for convenience when the same password is used for the keystore and the entry.
 */
public class DirectPasswordProvider implements KeyStorePasswordProvider, KeyEntryPasswordProvider {

    private final String password;

    public DirectPasswordProvider(String password) {
        this.password = password;
    }

    @Override
    public char[] getPassword() { // For KeyStorePasswordProvider
        return password != null ? password.toCharArray() : null;
    }

    @Override
    public char[] getPassword(String entryAlias, X509Certificate entryCert) { // For KeyEntryPasswordProvider
        return password != null ? password.toCharArray() : null;
    }
}
