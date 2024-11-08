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

package org.eclipse.dcp.schema.presentation;

import org.eclipse.dcp.schema.fixtures.AbstractSchemaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.networknt.schema.InputFormat.JSON;
import static org.assertj.core.api.Assertions.assertThat;

public class CredentialMessageSchemaTest extends AbstractSchemaTest {

    private static final String CREDENTIAL_MESSAGE_MESSAGE = """
            {
               "@context": [
                 "https://w3id.org/dspace-dcp/v0.8"
               ],
               "@type": "CredentialMessage",
               "credentials": [
                 {
                   "@type": "CredentialContainer",
                   "payload": "jwt"
                 }
               ],
               "requestId": "requestId"
             }""";

    private static final String INVALID_CREDENTIAL_MESSAGE = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v0.8"],
              "@type": "CredentialMessage"
            }""";

    private static final String INVALID_CREDENTIAL_MESSAGE_INVALID_CREDENTIAL_CONTAINER = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v0.8"],
              "@type": "CredentialMessage",
              "credentials": [
                 {
                   "@type": "CredentialContainer"
                 }
               ],
              "requestId": "requestId"
            }""";


    private static final String INVALID_CREDENTIAL_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
              "credentials": [
                 {
                   "@type": "CredentialContainer",
                   "payload": "jwt"
                 }
              ],
              "requestId": "requestId"
            }""";

    @Test
    void verifySchema() {
        assertThat(schema.validate(CREDENTIAL_MESSAGE_MESSAGE, JSON)).isEmpty();
        assertThat(schema.validate(INVALID_CREDENTIAL_MESSAGE, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("credentials", REQUIRED), error("requestId", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_MESSAGE_INVALID_CREDENTIAL_CONTAINER, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("payload", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_MESSAGE_NO_TYPE_AND_CONTEXT, JSON))
                .hasSize(4)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@type", REQUIRED), error("@context", REQUIRED));
    }

    @BeforeEach
    void setUp() {
        setUp("/presentation/credential-message-schema.json");
    }


}
