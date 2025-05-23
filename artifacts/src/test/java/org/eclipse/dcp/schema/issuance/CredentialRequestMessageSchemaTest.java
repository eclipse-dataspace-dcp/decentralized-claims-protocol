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
              "holderPid": "holderPid",
              "credentials": [
                 {"credentialObjectId": "d5c77b0e-7f4e-4fd5-8c5f-28b5fc3f96d1"},
                 {"credentialObjectId": "c0f81e68-6d35-4f9d-bc04-51e511b2e46c"}
              ]
            }""";

    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_HOLDER_REQUEST_ID = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialRequestMessage",
              "credentials": [
                 {"credentialObjectId": "d5c77b0e-7f4e-4fd5-8c5f-28b5fc3f96d1"},
                 {"credentialObjectId": "c0f81e68-6d35-4f9d-bc04-51e511b2e46c"}
              ]
            }""";

    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_ID_NOT_STRING = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialRequestMessage",
              "holderPid": "holderPid",
              "credentials": [
                  { "credentialObjectId": 42069 },
                  { "credentialObjectId": 4711 }
              ]
            }""";


    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_EMPTY_ARRAY = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialRequestMessage",
              "holderPid": "holderPid",
              "credentials": [
              ]
            }""";

    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
              "holderPid": "holderPid",
              "credentials": [
                 {"credentialObjectId": "d5c77b0e-7f4e-4fd5-8c5f-28b5fc3f96d1"},
                 {"credentialObjectId": "c0f81e68-6d35-4f9d-bc04-51e511b2e46c"}
              ]
            }""";


    @Test
    void verifySchema() {
        assertThat(schema.validate(CREDENTIAL_REQUEST_MESSAGE, JSON)).isEmpty();
        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_EMPTY_ARRAY, JSON))
                .isEmpty();

        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_HOLDER_REQUEST_ID, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("holderPid", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_ID_NOT_STRING, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error(null, TYPE), error(null, TYPE));

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
