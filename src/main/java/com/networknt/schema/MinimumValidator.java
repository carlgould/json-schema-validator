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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinimumValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(MinimumValidator.class);
    private static final String PROPERTY_EXCLUSIVE_MINIMUM = "exclusiveMinimum";

    private double minimum;
    private boolean excluded = false;

    public MinimumValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.MINIMUM);
        if (schemaNode.isJsonPrimitive() && schemaNode.getAsJsonPrimitive().isNumber()) {
            minimum = schemaNode.getAsJsonPrimitive().getAsNumber().doubleValue();
        } else {
            throw new JsonSchemaException("minimum value is not a number");
        }

        JsonElement exclusiveMinimumNode = getParentSchema().getSchemaNode().getAsJsonObject()
            .get(PROPERTY_EXCLUSIVE_MINIMUM);
        if (exclusiveMinimumNode != null) {
            excluded = exclusiveMinimumNode.getAsJsonPrimitive().getAsBoolean();
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        if (!(node.isJsonPrimitive() && node.getAsJsonPrimitive().isNumber())) {
            // minimum only applies to numbers
            return Collections.emptySet();
        }

        double value = node.getAsJsonPrimitive().getAsNumber().doubleValue();
        if (lessThan(value, minimum) || (excluded && equals(value, minimum))) {
            return Collections.singleton(buildValidationMessage(at, "" + minimum));
        } else {
            return Collections.emptySet();
        }
    }

}
