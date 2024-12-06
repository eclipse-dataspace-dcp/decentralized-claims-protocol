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

package org.eclipse.dcp.context.presentation;

import org.eclipse.dcp.context.fixtures.AbstractJsonLdTest;
import org.junit.jupiter.api.Test;

public class PresentationContextTest extends AbstractJsonLdTest {

    @Test
    void verifyPresentationQueryMessage() {
        verifyRoundTrip("/presentation/example/presentation-query-message.json", "/presentation/presentation-query-message-schema.json");
    }

    @Test
    void verifyPresentationQueryMessage_withPresentationDefinition() {
        verifyRoundTrip("/presentation/example/presentation-query-message-w-presentation-definition.json", "/presentation/presentation-query-message-schema.json");
    }

    @Test
    void verifyPresentationResponseMessage() {
        verifyRoundTrip("/presentation/example/presentation-response-message.json", "/presentation/presentation-response-message-schema.json");
    }

}