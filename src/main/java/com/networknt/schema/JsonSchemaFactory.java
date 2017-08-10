/*
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSchemaFactory {

    // Draft 6 uses "$id"
    private static final String DRAFT_4_ID = "id";

    private static final Logger logger = LoggerFactory
        .getLogger(JsonSchemaFactory.class);

    public JsonSchemaFactory() {
    }

    public JsonSchema getSchema(String schema) {
        try {
            JsonElement schemaNode = new JsonParser().parse(schema);
            return new JsonSchema(schemaNode);
        } catch (Exception ex) {
            logger.error("Failed to load json schema!", ex);
            throw new JsonSchemaException(ex);
        }
    }

    public JsonSchema getSchema(InputStream schemaStream) {
        try {
            JsonElement schemaNode = new JsonParser().parse(
                new InputStreamReader(schemaStream, StandardCharsets.UTF_8));
            return new JsonSchema(schemaNode);
        } catch (Exception ex) {
            logger.error("Failed to load json schema!", ex);
            throw new JsonSchemaException(ex);
        }
    }

    public JsonSchema getSchema(URL schemaURL) {
        try {

            Reader reader = new InputStreamReader(schemaURL.openStream(), StandardCharsets.UTF_8);
            JsonElement schemaNode = new JsonParser().parse(reader);

            if (this.idMatchesSourceUrl(schemaNode, schemaURL)) {
                return new JsonSchema(schemaNode, null);
            }

            return new JsonSchema(schemaNode);

        } catch (IOException ioe) {
            logger.error("Failed to load json schema!", ioe);
            throw new JsonSchemaException(ioe);
        }
    }

    public JsonSchema getSchema(JsonElement jsonNode) {
        return new JsonSchema(jsonNode);
    }

    private boolean idMatchesSourceUrl(JsonElement schema, URL schemaUrl) {

        JsonElement idNode = schema.getAsJsonObject().get(DRAFT_4_ID);

        if (idNode == null) {
            return false;
        }

        String id = idNode.getAsJsonPrimitive().getAsString();
        logger.info("Matching " + id + " to " + schemaUrl.toString());
        return id.equals(schemaUrl.toString());

    }

}
