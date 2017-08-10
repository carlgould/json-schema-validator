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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneOfValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(RequiredValidator.class);

    private List<JsonSchema> schemas = new ArrayList<JsonSchema>();

    public OneOfValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.ONE_OF);
        for (JsonElement sub : schemaNode.getAsJsonArray()) {
            schemas.add(new JsonSchema(getValidatorType().getValue(), sub, parentSchema));
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        int numberOfValidSchema = 0;
        Set<ValidationMessage> errors = new HashSet<>();

        for (JsonSchema schema : schemas) {
            Set<ValidationMessage> schemaErrors = schema.validate(node, rootNode, at);
            if (schemaErrors.isEmpty()) {
                numberOfValidSchema++;
                errors = new HashSet<>();
            }
            if (numberOfValidSchema == 0) {
                errors.addAll(schemaErrors);
            }
            if (numberOfValidSchema > 1) {
                break;
            }
        }

        if (numberOfValidSchema == 0) {
            errors.removeIf(msg -> ValidatorTypeCode.ADDITIONAL_PROPERTIES.equals(
                ValidatorTypeCode.fromValue(msg.getType())));
        }
        if (numberOfValidSchema > 1) {
            errors = new HashSet<>();
            errors.add(buildValidationMessage(at, ""));
        }

        return errors;
    }

}
