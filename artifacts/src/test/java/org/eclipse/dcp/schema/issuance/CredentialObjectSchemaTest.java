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
                "id": "d5c77b0e-7f4e-4fd5-8c5f-28b5fc3f96d1",
                "type": "CredentialObject",
                "credentialType": "VerifiableCredential",
                "credentialSchema": "https://example.com/credentials/credentialSchema",
                "offerReason": "reissue",
                "bindingMethods": [
                  "did:web"
                ],
                "profile": "vc20-bssl/jwt",
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
              "type": "CredentialObject"
            }""";

    public static final String SPARSE_CREDENTIAL_OBJECT = """
            {
              "id": "d5c77b0e-7f4e-4fd5-8c5f-28b5fc3f96d1"
            }""";

    private static final String CREDENTIAL_OBJECT_WRONG_TYPE = """
            {
              "id": "d5c77b0e-7f4e-4fd5-8c5f-28b5fc3f96d1",
              "type": "CredentialOfferMessage"
            }""";

    private static final String CREDENTIAL_OBJECT_NO_TYPE_AND_CONTEXT = """
             {
                "id": "d5c77b0e-7f4e-4fd5-8c5f-28b5fc3f96d1",
                "credentialType": "VerifiableCredential",
                "offerReason": "reissue",
                "bindingMethods": [
                  "did:web"
                ],
                "profile":"vc20-bssl/jwt",
                "issuancePolicy": {
                   "id": "Scalable trust example",
                   "input_descriptors": [
                     {
                       "id": "pd-id",
                       "constraints": {
                         "fields": [
                           {
                             "path": [
                               "$.vc.type"
                             ],
                             "filter": {
                               "type": "string",
                               "pattern": "^AttestationCredential$"
                             }
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
    }

    @Test
    void verifySchema_missingRequiredFields() {
        assertThat(schema.validate(INVALID_CREDENTIAL_OBJECT, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(
                        error("id", REQUIRED));
    }

    @Test
    void verifySchema_missingTypeAndContext_isValid() {
        // type is optional, so that sparse entries in a CredentialOfferMessage (only an id) are valid
        assertThat(schema.validate(CREDENTIAL_OBJECT_NO_TYPE_AND_CONTEXT, JSON)).isEmpty();
    }

    @Test
    void verifySchema_sparse_isValid() {
        assertThat(schema.validate(SPARSE_CREDENTIAL_OBJECT, JSON)).isEmpty();
    }

    @Test
    void verifySchema_wrongType() {
        assertThat(schema.validate(CREDENTIAL_OBJECT_WRONG_TYPE, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error(null, "const")); // the property is `null` on const validation errors
    }

    @BeforeEach
    void setUp() {
        setUp("/issuance/credential-object-schema.json");
    }


}
