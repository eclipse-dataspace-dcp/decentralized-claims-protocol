/*
 *  Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Contributors to the Eclipse Foundation - initial API and implementation
 *
 */

package org.eclipse.dcp.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dcp.schema.SchemaConstants.DCP_PREFIX;

/**
 * Verifies that each schema's {@code $id} matches the location it is published at, and that every absolute DCP
 * {@code $ref} points to the {@code $id} of an existing schema. Validators that resolve references by {@code $id}
 * (instead of by classpath prefix mapping, as the other tests do) rely on this.
 */
class SchemaIdTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void schemaIds_matchPublishedLocation() throws IOException, URISyntaxException {
        var root = resourceRoot();
        for (var schema : schemaFiles(root)) {
            var expectedId = DCP_PREFIX + root.relativize(schema).toString().replace('\\', '/');
            assertThat(MAPPER.readTree(schema.toFile()).path("$id").asText())
                    .as("$id of %s", schema)
                    .isEqualTo(expectedId);
        }
    }

    @Test
    void absoluteDcpRefs_resolveToKnownSchemaIds() throws IOException, URISyntaxException {
        var root = resourceRoot();
        var ids = new HashSet<String>();
        var refs = new ArrayList<String>();
        for (var schema : schemaFiles(root)) {
            var node = MAPPER.readTree(schema.toFile());
            ids.add(node.path("$id").asText());
            collectRefs(node, refs);
        }
        assertThat(refs)
                .filteredOn(ref -> ref.startsWith(DCP_PREFIX))
                .allSatisfy(ref -> assertThat(ids).as("schema $id for $ref %s", ref).contains(ref.split("#")[0]));
    }

    private static Path resourceRoot() throws URISyntaxException {
        var url = Objects.requireNonNull(SchemaIdTest.class.getResource("/common/context-schema.json"));
        return Path.of(url.toURI()).getParent().getParent();
    }

    private static List<Path> schemaFiles(Path root) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            return files.filter(p -> p.toString().endsWith("-schema.json")).sorted().toList();
        }
    }

    private static void collectRefs(JsonNode node, List<String> refs) {
        if (node.isObject()) {
            var ref = node.get("$ref");
            if (ref != null && ref.isTextual()) {
                refs.add(ref.asText());
            }
            node.elements().forEachRemaining(child -> collectRefs(child, refs));
        } else if (node.isArray()) {
            node.elements().forEachRemaining(child -> collectRefs(child, refs));
        }
    }
}
