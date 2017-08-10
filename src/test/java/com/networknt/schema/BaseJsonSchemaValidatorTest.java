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


import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Created by steve on 22/10/16.
 */
public class BaseJsonSchemaValidatorTest {
    static JsonElement getJsonElementFromClasspath(String name) throws Exception {
        return getJsonElementFromStream(
            Thread.currentThread().getContextClassLoader().getResourceAsStream(name));
    }

    static JsonElement getJsonElementFromStringContent(String content) throws Exception {
        return new JsonParser().parse(content);
    }

    static JsonElement getJsonElementFromUrl(String url) throws Exception {
        return getJsonElementFromStream(new URL(url).openStream());
    }

    static JsonElement getJsonElementFromStream(InputStream stream) throws Exception {
        return new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    protected JsonSchema getJsonSchemaFromClasspath(String name) throws Exception {
        JsonSchemaFactory factory = new JsonSchemaFactory();
        InputStream is = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(name);
        return factory.getSchema(is);
    }

    protected JsonSchema getJsonSchemaFromStringContent(String schemaContent) throws Exception {
        JsonSchemaFactory factory = new JsonSchemaFactory();
        return factory.getSchema(schemaContent);
    }

    protected JsonSchema getJsonSchemaFromUrl(String url) throws Exception {
        JsonSchemaFactory factory = new JsonSchemaFactory();
        return factory.getSchema(new URL(url));
    }

    protected JsonSchema getJsonSchemaFromJsonElement(JsonElement JsonElement) throws Exception {
        JsonSchemaFactory factory = new JsonSchemaFactory();
        return factory.getSchema(JsonElement);
    }
}
