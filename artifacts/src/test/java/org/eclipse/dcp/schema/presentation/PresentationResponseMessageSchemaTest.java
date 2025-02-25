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

public class PresentationResponseMessageSchemaTest extends AbstractSchemaTest {

    private static final String PRESENTATION_RESPONSE_MESSAGE = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "PresentationResponseMessage",
              "presentation": ["presentation1", "presentation2"]
            }""";

    private static final String PRESENTATION_RESPONSE_MESSAGE_WITH_OBJECT = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "PresentationResponseMessage",
              "presentation": ["presentation1", {"@id": "presentation2"}]
            }""";

    private static final String PRESENTATION_RESPONSE_MESSAGE_WITH_PRESENTATION_SUBMISSION = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "PresentationResponseMessage",
              "presentation": ["presentation1"],
              "presentationSubmission": {
                "id": "id",
                "definition_id": "definition_id",
                "descriptor_map": []
              }
            }""";

    private static final String INVALID_PRESENTATION_RESPONSE_MESSAGE_NO_PRESENTATION = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "PresentationResponseMessage"
            }""";

    private static final String INVALID_PRESENTATION_RESPONSE_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
              "presentation": ["presentation1", "presentation2"]
            }""";

    private static final String INVALID_PRESENTATION_RESPONSE_MESSAGE_EMPTY_PRESENTATION_SUBMISSION = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "PresentationResponseMessage",
              "presentation": ["presentation1"],
              "presentationSubmission": {
            
              }
            }""";

    @Test
    void verifySchema() {
        assertThat(schema.validate(PRESENTATION_RESPONSE_MESSAGE, JSON)).isEmpty();
        assertThat(schema.validate(PRESENTATION_RESPONSE_MESSAGE_WITH_OBJECT, JSON)).isEmpty();
        assertThat(schema.validate(PRESENTATION_RESPONSE_MESSAGE_WITH_PRESENTATION_SUBMISSION, JSON)).isEmpty();
        assertThat(schema.validate(INVALID_PRESENTATION_RESPONSE_MESSAGE_NO_PRESENTATION, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("presentation", REQUIRED));

        assertThat(schema.validate(INVALID_PRESENTATION_RESPONSE_MESSAGE_NO_TYPE_AND_CONTEXT, JSON))
                .hasSize(2)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@context", REQUIRED));

        assertThat(schema.validate(INVALID_PRESENTATION_RESPONSE_MESSAGE_EMPTY_PRESENTATION_SUBMISSION, JSON))
                .hasSize(3)
                .extracting(this::errorExtractor)
                .contains(error("id", REQUIRED), error("definition_id", REQUIRED), error("descriptor_map", REQUIRED));

    }

    @BeforeEach
    void setUp() {
        setUp("/presentation/presentation-response-message-schema.json");
    }

}
