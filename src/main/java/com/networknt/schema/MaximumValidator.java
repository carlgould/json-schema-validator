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
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaximumValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(MaximumValidator.class);
    private static final String PROPERTY_EXCLUSIVE_MAXIMUM = "exclusiveMaximum";

    private double maximum;
    private boolean excludeEqual = false;

    public MaximumValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {

        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.MAXIMUM);
        if (isNumber(schemaNode)) {
            maximum = doubleValue(schemaNode);
        } else {
            throw new JsonSchemaException("maximum value is not a number");
        }

        JsonObject parentSchemaObject = getParentSchema().getSchemaNode().getAsJsonObject();
        JsonElement exclusiveMaximumNode = parentSchemaObject.get(PROPERTY_EXCLUSIVE_MAXIMUM);
        if (exclusiveMaximumNode != null && isBoolean(exclusiveMaximumNode)) {
            excludeEqual = exclusiveMaximumNode.getAsJsonPrimitive().getAsBoolean();
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        if (!isNumber(node)) {
            // maximum only applies to numbers
            return Collections.emptySet();
        }

        double value = doubleValue(node);
        if (greaterThan(value, maximum) || (excludeEqual && equals(value, maximum))) {
            return Collections.singleton(buildValidationMessage(at, "" + maximum));
        } else {
            return Collections.emptySet();
        }
    }

}
