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

public class CredentialObjectSchemaTest extends AbstractSchemaTest {

    public static final String CREDENTIAL_OBJECT = """
            {
                "@context": ["https://w3id.org/dspace-dcp/v0.8"],
                "@type": "CredentialObject",
                "credentialType": ["VerifiableCredential"],
                "offerReason": "reissue",
                "bindingMethods": [
                  "did:web"
                ],
                "cryptography": [
                  "JsonWebSignature2020"
                ],
                "issuancePolicy": {
                  "id": "Scalable trust example",
                  "input_descriptors": [
                    {
                      "id": "pd-id",
                      "constraints": {
                        "fields": [
                          {
                            "path": [
                              "$.holderIdentifier"
                            ],
                            "filter": {
                              "type": "string",
                              "pattern": "^BPN[LS][a-zA-Z0-9]{12}$"
                            }
                          }
                        ]
                      }
                    }
                  ]
                }
            }
            """;

    private static final String INVALID_CREDENTIAL_OBJECT = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v0.8"],
              "@type": "CredentialObject"
            }""";

    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT = """
             {
                "credentialType": ["VerifiableCredential"],
                "offerReason": "reissue",
                "bindingMethods": [
                  "did:web"
                ],
                "cryptography": [
                  "JsonWebSignature2020"
                ],
                "issuancePolicy": {
                  "permission": [
                    {
                      "action": "use",
                      "constraint": {
                        "and": [
                          {
                            "leftOperand": "CredentialPrereq",
                            "operator": "eq",
                            "rightOperand": "active"
                          }
                        ]
                      }
                    }
                  ]
                }
            }""";

    @Test
    void verifySchema() {
        assertThat(schema.validate(CREDENTIAL_OBJECT, JSON)).isEmpty();
        assertThat(schema.validate(INVALID_CREDENTIAL_OBJECT, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("credentialType", REQUIRED),
                        error("offerReason", REQUIRED),
                        error("bindingMethods", REQUIRED),
                        error("cryptography", REQUIRED),
                        error("issuancePolicy", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT, JSON))
                .hasSize(6)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@type", REQUIRED));

    }

    @BeforeEach
    void setUp() {
        setUp("/issuance/credential-object-schema.json");
    }


}
