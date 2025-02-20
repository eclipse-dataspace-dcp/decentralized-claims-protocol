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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.networknt.schema.InputFormat.JSON;
import static org.assertj.core.api.Assertions.assertThat;

public class CredentialStatusSchemaTest extends AbstractSchemaTest {

    private static final String CREDENTIAL_STATUS = """
            {
                "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
                "type": "CredentialStatus",
                "issuerPid": "issuerPid",
                "holderPid": "holderPid",
                "status": "%s"
            }""";

    private static final String INVALID_CREDENTIAL_STATUS = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
              "type": "CredentialStatus"
            }""";

    private static final String INVALID_CREDENTIAL_STATUS_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
                "issuerPid": "issuerPid",
                "holderPid": "holderPid",
                "status": "RECEIVED"
            }""";

    @Test
    void verifySchema() {
        assertThat(schema.validate(CREDENTIAL_STATUS.formatted("INVALID_STATUS"), JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error(null, ENUM));
        assertThat(schema.validate(INVALID_CREDENTIAL_STATUS, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("issuerPid", REQUIRED), error("holderPid", REQUIRED), error("status", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_STATUS_MESSAGE_NO_TYPE_AND_CONTEXT, JSON))
                .hasSize(2)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@context", REQUIRED));
    }

    @ParameterizedTest
    @ValueSource(strings = { "RECEIVED", "REJECTED", "ISSUED" })
    void verifySchemaStatus(String status) {
        assertThat(schema.validate(CREDENTIAL_STATUS.formatted(status), JSON)).isEmpty();

    }

    @BeforeEach
    void setUp() {
        setUp("/issuance/credential-status-schema.json");
    }


}
