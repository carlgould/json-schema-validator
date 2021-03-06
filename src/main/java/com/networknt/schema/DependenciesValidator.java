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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependenciesValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(DependenciesValidator.class);
    /**
     * Simple case of "dependencies" If the object has properties in the key set, then the properties listed in
     * the value must also be present.
     */
    private final Map<String, List<String>> propertyDeps = new HashMap<String, List<String>>();
    /**
     * Dependencies can also list additional schemas that must be passed if certain properties are present
     */
    private Map<String, JsonSchema> schemaDeps = new HashMap<String, JsonSchema>();

    public DependenciesValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.DEPENDENCIES);

        for (String name : schemaNode.getAsJsonObject().keySet()) {
            JsonElement value = schemaNode.getAsJsonObject().get(name);
            if (value.isJsonArray() && value.getAsJsonArray().size() > 0) {
                List<String> depsProps = new ArrayList<String>();
                propertyDeps.put(name, depsProps);

                for (JsonElement element : value.getAsJsonArray()) {
                    depsProps.add(asText(element));
                }
            } else if (value.isJsonObject()) {
                schemaDeps.put(name, new JsonSchema(name, value, parentSchema));
            }
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = new HashSet<>();

        if (node.isJsonObject()) {
            JsonObject object = node.getAsJsonObject();

            for (String name : object.keySet()) {
                if (propertyDeps.containsKey(name)) {
                    for (String requiredField : propertyDeps.get(name)) {
                        if (!object.has(requiredField)) {
                            errors.add(buildValidationMessage(at, propertyDeps.toString()));
                        }
                    }
                }

                if (schemaDeps.containsKey(name)) {
                    errors.addAll(schemaDeps.get(name).validate(node, rootNode, at));
                }
            }
        }

        return errors;
    }

}
