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

package org.eclipse.dcp.context.fixtures;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonp.JSONPModule;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.apicatalog.jsonld.JsonLd.compact;
import static com.apicatalog.jsonld.JsonLd.expand;
import static com.apicatalog.jsonld.lang.Keywords.CONTEXT;
import static com.networknt.schema.SpecVersion.VersionFlag.V202012;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dcp.schema.SchemaConstants.DCP_CONTEXT;
import static org.eclipse.dcp.schema.SchemaConstants.DCP_PREFIX;
import static org.eclipse.dcp.schema.fixtures.AbstractSchemaTest.PRESENTATION_EXCHANGE_PREFIX;

/**
 * Base class for Json-Ld expansion and compaction tests.
 */
public abstract class AbstractJsonLdTest {
    private static final String CLASSPATH_SCHEMA = "classpath:/";
    private static final String CONTEXT_REFERENCE = format("{\"@context\": [\"%s\"]}", DCP_CONTEXT);
    private final Map<String, String> contextMap = Map.of(
            DCP_CONTEXT, "/context/dcp.jsonld",
            "https://www.w3.org/ns/odrl.jsonld", "/context/odrl.jsonld"
    );
    protected ObjectMapper mapper;
    protected JsonStructure compactionContext;
    protected JsonLdOptions options;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JSONPModule());

        Map<String, Document> cache = new HashMap<>();

        contextMap.forEach((key, value) -> {
            try (var stream = getClass().getResourceAsStream(value)) {
                var context = mapper.readValue(stream, JsonObject.class);
                cache.put(key, JsonDocument.of(context));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            var documentLoader = new LocalDocumentLoader(cache);
            compactionContext = mapper.readValue(CONTEXT_REFERENCE, JsonStructure.class);
            options = new JsonLdOptions();
            options.setDocumentLoader(documentLoader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected void verifyRoundTrip(String jsonFile, String schemaFile) {
        try {
            var stream = getClass().getResourceAsStream(jsonFile);
            var message = mapper.readValue(stream, JsonObject.class);
            verifyRoundTrip(message, schemaFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void verifyRoundTrip(JsonObject message, String schemaFile) {
        try {

            var context = Json.createObjectBuilder().add(CONTEXT, message.get(CONTEXT)).build();
            var expanded = expand(JsonDocument.of(message)).options(options).get();
            var compacted = compact(JsonDocument.of(expanded), JsonDocument.of(context)).options(options).get();

            var schemaFactory = JsonSchemaFactory.getInstance(V202012, builder ->
                    builder.schemaMappers(schemaMappers -> schemaMappers.mapPrefix(DCP_PREFIX, CLASSPATH_SCHEMA)
                            .mapPrefix(PRESENTATION_EXCHANGE_PREFIX, CLASSPATH_SCHEMA))
            );

            var schema = schemaFactory.getSchema(SchemaLocation.of(DCP_PREFIX + schemaFile));
            var result = schema.validate(mapper.convertValue(compacted, JsonNode.class));
            assertThat(result.isEmpty()).describedAs(String.join(", ", result.stream().map(Object::toString).toList())).isTrue();
            assertThat(compacted).isEqualTo(message);
        } catch (JsonLdError e) {
            throw new RuntimeException(e);
        }
    }

    private static class LocalDocumentLoader implements DocumentLoader {
        private final Map<String, Document> contexts = new HashMap<>();

        LocalDocumentLoader(Map<String, Document> contexts) {
            this.contexts.putAll(contexts);
        }

        @Override
        public Document loadDocument(URI url, DocumentLoaderOptions options) {
            return contexts.get(url.toString());
        }
    }
}