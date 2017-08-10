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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemsValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(ItemsValidator.class);
    private static final String PROPERTY_ADDITIONAL_ITEMS = "additionalItems";

    private JsonSchema schema;
    private List<JsonSchema> tupleSchema;
    private boolean additionalItems = true;
    private JsonSchema additionalSchema;

    public ItemsValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.ITEMS);
        if (schemaNode.isJsonObject()) {
            schema = new JsonSchema(getValidatorType().getValue(), schemaNode, parentSchema);
        } else if (schemaNode.isJsonArray()) {
            tupleSchema = new ArrayList<>();
            for (JsonElement s : schemaNode.getAsJsonArray()) {
                tupleSchema.add(new JsonSchema(getValidatorType().getValue(), s, parentSchema));
            }

            JsonObject parentSchemaObject = getParentSchema().getSchemaNode().getAsJsonObject();
            JsonElement addItemNode = parentSchemaObject.get(PROPERTY_ADDITIONAL_ITEMS);
            if (addItemNode != null) {
                if (isBoolean(addItemNode)) {
                    additionalItems = addItemNode.getAsJsonPrimitive().getAsBoolean();
                } else if (addItemNode.isJsonObject()) {
                    additionalSchema = new JsonSchema(addItemNode);
                }
            }
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        if (!node.isJsonArray()) {
            // ignores non-arrays
            return Collections.emptySet();
        }

        Set<ValidationMessage> errors = new HashSet<>();

        int i = 0;
        for (JsonElement n : node.getAsJsonArray()) {
            if (schema != null) {
                // validate with item schema (the whole array has the same item schema)
                errors.addAll(schema.validate(n, rootNode, at + "[" + i + "]"));
            }

            if (tupleSchema != null) {
                if (i < tupleSchema.size()) {
                    // validate against tuple schema
                    errors.addAll(tupleSchema.get(i).validate(n, rootNode, at + "[" + i + "]"));
                } else {
                    if (additionalSchema != null) {
                        // validate against additional item schema
                        errors.addAll(additionalSchema.validate(n, rootNode, at + "[" + i + "]"));
                    } else if (!additionalItems) {
                        // no additional item allowed, return error
                        errors.add(buildValidationMessage(at, "" + i));
                    }
                }
            }

            i++;
        }
        return errors;
    }

}
