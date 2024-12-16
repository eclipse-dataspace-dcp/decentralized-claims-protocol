/*
 *  Copyright (c) 2024 Metaform Systems, Inc.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Metaform Systems, Inc. - initial API and implementation
 *
 */

package org.eclipse.dcp.schema.issuance;

import org.eclipse.dcp.schema.fixtures.AbstractSchemaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.networknt.schema.InputFormat.JSON;
import static org.assertj.core.api.Assertions.assertThat;

public class CredentialRequestMessageSchemaTest extends AbstractSchemaTest {

    private static final String CREDENTIAL_REQUEST_MESSAGE = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialRequestMessage",
              "format": "jwt",
              "credentialType": ["VerifiableCredential"]
            }""";

    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_FORMAT = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialRequestMessage",
              "credentialType": ["VerifiableCredential"]
            }""";

    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_CREDENTIAL_TYPE = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialRequestMessage",
              "format": "jwt"
            }""";

    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
              "format": "jwt",
              "credentialType": ["VerifiableCredential"]
            }""";


    @Test
    void verifySchema() {
        assertThat(schema.validate(CREDENTIAL_REQUEST_MESSAGE, JSON)).isEmpty();
        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_FORMAT, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("format", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_CREDENTIAL_TYPE, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("credentialType", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT, JSON))
                .hasSize(2)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@context", REQUIRED));


    }

    @BeforeEach
    void setUp() {
        setUp("/issuance/credential-request-message-schema.json");
    }


}
