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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotAllowedValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(NotAllowedValidator.class);

    private List<String> fieldNames = new ArrayList<String>();

    public NotAllowedValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema, Gson mapper) {

        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.NOT_ALLOWED);
        if (schemaNode.isJsonArray()) {
            for (JsonElement element : schemaNode.getAsJsonArray()) {
                fieldNames.add(asText(element));
            }
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = new HashSet<ValidationMessage>();

        for (String fieldName : fieldNames) {
            if (node.getAsJsonObject().has(fieldName)) {
                errors.add(buildValidationMessage(at, fieldName));
            }
        }

        return errors;
    }

}
