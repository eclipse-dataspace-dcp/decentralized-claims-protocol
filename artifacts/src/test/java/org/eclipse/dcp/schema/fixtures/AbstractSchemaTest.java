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

package org.eclipse.dcp.schema.fixtures;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.ValidationMessage;

import static com.networknt.schema.SpecVersion.VersionFlag.V202012;
import static org.eclipse.dcp.schema.SchemaConstants.DCP_PREFIX;

/**
 * Base test class.
 */
public abstract class AbstractSchemaTest {
    public static final String PRESENTATION_EXCHANGE_PREFIX = "https://identity.foundation/";
    protected static final String MIN_CONTAINS = "minContains";
    protected static final String REQUIRED = "required";
    protected static final String ONE_OF = "oneOf";
    protected static final String TYPE = "type";
    protected static final String ENUM = "enum";
    private static final String CLASSPATH_SCHEMA = "classpath:/";
    protected JsonSchema schema;

    protected void setUp(String schemaFile) {
        var schemaFactory = JsonSchemaFactory.getInstance(V202012, builder ->
                builder.schemaMappers(schemaMappers ->
                        schemaMappers.mapPrefix(DCP_PREFIX, CLASSPATH_SCHEMA)
                                .mapPrefix(PRESENTATION_EXCHANGE_PREFIX, CLASSPATH_SCHEMA))
        );

        schema = schemaFactory.getSchema(SchemaLocation.of(DCP_PREFIX + schemaFile));
    }



    protected SchemaError errorExtractor(ValidationMessage validationMessage) {
        return new SchemaError(validationMessage.getProperty(), validationMessage.getType());
    }

    protected SchemaError error(String property, String type) {
        return new SchemaError(property, type);
    }

    public record SchemaError(String property, String type) {

    }
}
