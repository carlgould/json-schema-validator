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

import com.google.gson.JsonElement;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class UniqueItemsValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(UniqueItemsValidator.class);

    private boolean unique = false;

    public UniqueItemsValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.UNIQUE_ITEMS);
        if (isBoolean(schemaNode)) {
            unique = schemaNode.getAsJsonPrimitive().getAsBoolean();
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = new HashSet<>();

        if (unique && node.isJsonArray()) {
            Set<JsonElement> set = new HashSet<>();
            for (JsonElement n : node.getAsJsonArray()) {
                set.add(n);
            }

            if (set.size() < node.getAsJsonArray().size()) {
                errors.add(buildValidationMessage(at));
            }
        }

        return errors;
    }

}
