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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesValidator extends BaseJsonValidator implements JsonValidator {
    public static final String PROPERTY = "properties";
    private static final Logger logger = LoggerFactory.getLogger(PropertiesValidator.class);
    private Map<String, JsonSchema> schemas;

    public PropertiesValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.PROPERTIES);
        schemas = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : schemaNode.getAsJsonObject().entrySet()) {
            String name = entry.getKey();
            JsonElement val = entry.getValue();
            schemas.put(name, new JsonSchema(schemaPath + "/" + name, val, parentSchema));
        }
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = null;

        if (node.isJsonObject()) {
            for (String key : schemas.keySet()) {
                JsonSchema propertySchema = schemas.get(key);
                JsonElement propertyNode = node.getAsJsonObject().get(key);

                if (propertyNode != null) {
                    Set<ValidationMessage> subErrors = propertySchema.validate(propertyNode, rootNode, at + "." + key);
                    if (!subErrors.isEmpty()) {
                        if (errors == null) errors = new HashSet<>();
                        errors.addAll(subErrors);
                    }
                }
            }
        }

        return errors == null ? Collections.emptySet() : errors;
    }

}
