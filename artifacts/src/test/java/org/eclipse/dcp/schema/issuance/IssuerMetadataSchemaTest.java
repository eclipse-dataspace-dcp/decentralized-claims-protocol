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
import static org.eclipse.dcp.schema.issuance.CredentialObjectSchemaTest.CREDENTIAL_OBJECT;

public class IssuerMetadataSchemaTest extends AbstractSchemaTest {

    public static final String ISSUER_METADATA = """
            {
                "@context": ["https://w3id.org/dspace-dcp/v0.8"],
                "@type": "IssuerMetadata",
                "credentialIssuer": "did:web:issuer-url",
                "credentialsSupported": [%s]
            }""";

    private static final String INVALID_ISSUER_METADATA = """
            {
              "@context": ["https://w3id.org/dspace-dcp/v0.8"],
              "@type": "IssuerMetadata"
            }""";

    private static final String INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT = """
            {
              "credentialIssuer": "did:web:issuer-url",
              "credentialsSupported": [%s]
            }""";

    @Test
    void verifySchema() {
        assertThat(schema.validate(ISSUER_METADATA.formatted(CREDENTIAL_OBJECT), JSON)).isEmpty();
        assertThat(schema.validate(INVALID_ISSUER_METADATA, JSON))
                .extracting(this::errorExtractor)
                .containsExactly(error("credentialIssuer", REQUIRED), error("credentialsSupported", REQUIRED));

        assertThat(schema.validate(INVALID_CREDENTIAL_REQUEST_MESSAGE_NO_TYPE_AND_CONTEXT.formatted(CREDENTIAL_OBJECT), JSON))
                .hasSize(4)
                .extracting(this::errorExtractor)
                .contains(error("type", REQUIRED), error("@type", REQUIRED), error("@context", REQUIRED));
    }

    @BeforeEach
    void setUp() {
        setUp("/issuance/issuer-metadata-schema.json");
    }


}
