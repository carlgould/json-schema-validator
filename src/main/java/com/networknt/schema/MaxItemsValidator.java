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

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxItemsValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(MaxItemsValidator.class);

    private int max = 0;

    public MaxItemsValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.MAX_ITEMS);
        if (isInteger(schemaNode)) {
            max = asInt(schemaNode);
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = new HashSet<>();

        if (node.isJsonArray()) {
            if (node.getAsJsonArray().size() > max) {
                errors.add(buildValidationMessage(at, "" + max));
            }
        }

        return errors;
    }

}
