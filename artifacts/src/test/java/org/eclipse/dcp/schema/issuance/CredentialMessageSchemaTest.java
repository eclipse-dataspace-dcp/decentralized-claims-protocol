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

public class CredentialMessageSchemaTest extends AbstractSchemaTest {

    private static final String CREDENTIAL_MESSAGE_MESSAGE = """
            {
              "@context": [
                "https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"
              ],
              "type": "CredentialMessage",
              "credentials": [
                {
                  "credentialType": "MembershipCredential",
                  "payload": "<JWT-STRING>",
                  "format": "jwt"
                },
                {
                  "credentialType": "OrganizationCredential",
                  "payload": "<LD-Object>",
                  "format": "json-ld"
                }
              ],
              "status": "ISSUED",
              "issuerPid": "issuerPid",
              "holderPid": "holderPid"
            }""";

    private static final String CREDENTIAL_MESSAGE_MESSAGE_WRONG_STATUS = """
            {
              "@context": [
                "https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"
              ],
              "type": "CredentialMessage",
              "credentials": [
                {
                  "credentialType": "MembershipCredential",
                  "payload": "<JWT-STRING>",
                  "format": "jwt"
                },
                {
                  "credentialType": "OrganizationCredential",
                  "payload": "<LD-Object>",
                  "format": "json-ld"
                }
              ],
              "status": "INVALID",
              "issuerPid": "issuerPid",
              "holderPid": "holderPid"
            }""";

    private static final String CREDENTIAL_MESSAGE_MESSAGE_REJECTION = """
            {
              "@context": [
                "https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"
              ],
              "type": "CredentialMessage",
              "status": "REJECTED",
              "rejectionReason": "some rejection reason",
              "issuerPid": "issuerPid",
              "holderPid": "holderPid"
            }""";

    private static final String INVALID_CREDENTIAL_MESSAGE = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialMessage",
              "status": "ISSUED"
            }""";

    private static final String INVALID_CREDENTIAL_MESSAGE_INVALID_CREDENTIAL_CONTAINER = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialMessage",
              "credentials": [
                  {
                    "credentialType": "MembershipCredential",
                    "format": "jwt"
                  }
              ],
              "status": "ISSUED",
              "issuerPid": "issuerPid",
              "holderPid": "holderPid"
            }""";

    private static final String INVALID_CREDENTIAL_MESSAGE_NO_STATUS = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialMessage",
              "credentials": [
                 {
                  "credentialType": "OrganizationCredential",
                  "payload": "<LD-Object>",
                  "format": "json-ld"
                }
              ],
              "issuerPid": "issuerPid",
              "holderPid": "holderPid"
            }""";


    private static final String INVALID_CREDENTIAL_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
              "credentials": [
                   {
                     "credentialType": "MembershipCredential",
                     "payload": "<JWT-STRING>",
                     "format": "jwt"
                   }
              ],
              "status": "ISSUED",
              "issuerPid": "issuerPid",
              "holderPid": "holderPid"
            }""";

    @Test
    void verifySchema() {
        assertThat(schema.validate(CREDENTIAL_MESSAGE_MESSAGE, JSON)).isEmpty();
        assertThat(schema.validate(CREDENTIAL_MESSAGE_MESSAGE_REJECTION, JSON)).isEmpty();
        assertThat(schema.validate(INVALID_CREDENTIAL_MESSAGE, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("issuerPid", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_MESSAGE_INVALID_CREDENTIAL_CONTAINER, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("payload", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_MESSAGE_NO_TYPE_AND_CONTEXT, JSON))
                .hasSize(2)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@context", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_MESSAGE_NO_STATUS, JSON))
                .hasSize(1)
                .extracting(this::errorExtractor)
                .contains(error("status", REQUIRED));

        assertThat(schema.validate(CREDENTIAL_MESSAGE_MESSAGE_WRONG_STATUS, JSON))
                .hasSize(1)
                .extracting(this::errorExtractor)
                .contains(error(null, ENUM)); // for some reason the property is `null` on enum validation errors
    }

    @BeforeEach
    void setUp() {
        setUp("/issuance/credential-message-schema.json");
    }


}
