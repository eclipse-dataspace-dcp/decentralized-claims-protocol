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

package org.eclipse.dcp.context.issuance;

import jakarta.json.JsonObject;
import org.eclipse.dcp.context.fixtures.AbstractJsonLdTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

public class IssuanceContextTest extends AbstractJsonLdTest {

    @Test
    void verifyCredentialRequestMessage() {
        verifyRoundTrip("/issuance/example/credential-request-message.json", "/issuance/credential-request-message-schema.json");
    }

    @Test
    void verifyCredentialMessage() {
        verifyRoundTrip("/issuance/example/credential-message.json", "/issuance/credential-message-schema.json");
    }

    @Test
    void verifyCredentialMessageRejected() {
        verifyRoundTrip("/issuance/example/credential-message-rejected.json", "/issuance/credential-message-schema.json");
    }

    @Test
    void verifyCredentialOfferMessage() {
        verifyRoundTrip("/issuance/example/credential-offer-message.json", "/issuance/credential-offer-message-schema.json");
    }

    @Test
    void verifyCredentialObject() {
        verifyRoundTrip("/issuance/example/credential-object.json", "/issuance/credential-object-schema.json");
    }

    @Test
    void verifyIssuerMetadata() {
        verifyRoundTrip("/issuance/example/issuer-metadata.json", "/issuance/issuer-metadata-schema.json");
    }

    @Test
    void verifyCredentialStatus() {
        verifyRoundTrip("/issuance/example/credential-status.json", "/issuance/credential-status-schema.json");
    }

    @ParameterizedTest
    @ValueSource(strings = { "RECEIVED", "REJECTED", "ISSUED" })
    void verifyCredentialStatus_withStatus(String status) throws IOException {
        var msg = """
                {
                    "@context": ["https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"],
                    "type": "CredentialStatus",
                    "issuerPid": "issuerPid",
                    "holderPid": "holderPid",
                    "status": "%s"
                }""".formatted(status);

        var message = mapper.readValue(msg, JsonObject.class);
        verifyRoundTrip(message, "/issuance/credential-status-schema.json");
    }

}