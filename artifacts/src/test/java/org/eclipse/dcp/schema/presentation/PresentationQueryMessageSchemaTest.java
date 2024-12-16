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

public class PresentationQueryMessageSchemaTest extends AbstractSchemaTest {

    private static final String PRESENTATION_QUERY_MESSAGE = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "PresentationQueryMessage",
              "scope": ["scope1", "scope2"]
            }""";

    private static final String PRESENTATION_QUERY_MESSAGE_WITH_PRESENTATION_DEF = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "PresentationQueryMessage",
              "presentationDefinition": {
                "id": "presentation1",
                "input_descriptors": [
                {
                      "id": "organization credential",
                      "format": {
                        "ldp_vc": {
                          "proof_type": [
                            "Ed25519Signature2018"
                          ]
                        }
                      },
                      "constraints": {
                        "fields": [
                          {
                            "path": [
                              "$.type"
                            ],
                            "filter": {
                              "type": "string",
                              "pattern": "OrganizationCredential"
                            }
                          }
                        ]
                      }
                    }
                ]
              }
            }""";

    private static final String INVALID_PRESENTATION_QUERY_MESSAGE_NO_SCOPE = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "PresentationQueryMessage"
            }""";


    private static final String INVALID_PRESENTATION_QUERY_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
              "scope": ["scope1", "scope2"]
            }""";

    @Test
    void verifySchema() {
        assertThat(schema.validate(PRESENTATION_QUERY_MESSAGE, JSON)).isEmpty();
        assertThat(schema.validate(PRESENTATION_QUERY_MESSAGE_WITH_PRESENTATION_DEF, JSON)).isEmpty();

        assertThat(schema.validate(INVALID_PRESENTATION_QUERY_MESSAGE_NO_SCOPE, JSON))
                .extracting(this::errorExtractor)
                .contains(error("scope", REQUIRED), error("presentationDefinition", REQUIRED));


        assertThat(schema.validate(INVALID_PRESENTATION_QUERY_MESSAGE_NO_TYPE_AND_CONTEXT, JSON))
                .hasSize(2)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@context", REQUIRED));
    }

    @BeforeEach
    void setUp() {
        setUp("/presentation/presentation-query-message-schema.json");
    }


}
