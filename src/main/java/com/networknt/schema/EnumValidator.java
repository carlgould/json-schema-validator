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

import static org.apache.commons.lang3.StringUtils.join;

public class EnumValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(EnumValidator.class);

    private List<JsonElement> nodes;
    private String error;

    public EnumValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.ENUM);
        nodes = new ArrayList<>();
        error = "[none]";

        if (schemaNode != null && schemaNode.isJsonArray()) {
            List<String> errorStrings = new ArrayList<String>(schemaNode.getAsJsonArray().size());

            for (JsonElement n : schemaNode.getAsJsonArray()) {
                nodes.add(n);
                errorStrings.add(asText(n));
            }
            this.error = "[" + join(errorStrings, ", ") + "]";
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = new HashSet<>();

        if (!nodes.contains(node)) {
            errors.add(buildValidationMessage(at, error));
        }

        return errors;
    }

}
