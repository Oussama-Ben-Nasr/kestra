package io.kestra.core.secret;

import io.kestra.core.exceptions.KestraRuntimeException;

/**
 * Exception when a secret is not found.
 */
public class SecretNotFoundException extends KestraRuntimeException {

    public SecretNotFoundException(String message) {
        super(message);
    }
}
