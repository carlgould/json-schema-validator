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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternPropertiesValidator extends BaseJsonValidator implements JsonValidator {
    public static final String PROPERTY = "patternProperties";
    private static final Logger logger = LoggerFactory.getLogger(PatternPropertiesValidator.class);
    private Map<Pattern, JsonSchema> schemas = new HashMap<Pattern, JsonSchema>();

    public PatternPropertiesValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.PATTERN_PROPERTIES);
        if (!schemaNode.isJsonObject()) {
            throw new JsonSchemaException("patternProperties must be an object node");
        }
        for (String name : schemaNode.getAsJsonObject().keySet()) {
            schemas.put(Pattern.compile(name),
                new JsonSchema(name, schemaNode.getAsJsonObject().get(name), parentSchema));
        }
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = new HashSet<>();

        if (!node.isJsonObject()) {
            return errors;
        }

        for (String name : node.getAsJsonObject().keySet()) {
            JsonElement n = node.getAsJsonObject().get(name);
            for (Pattern pattern : schemas.keySet()) {
                Matcher m = pattern.matcher(name);
                if (m.find()) {
                    errors.addAll(schemas.get(pattern).validate(n, rootNode, at + "." + name));
                }
            }
        }
        return errors;
    }

}
