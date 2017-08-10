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

public class UnionTypeValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(UnionTypeValidator.class);

    private List<JsonValidator> schemas;
    private String error;

    public UnionTypeValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema, Gson mapper) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.UNION_TYPE);
        schemas = new ArrayList<JsonValidator>();
        String sep = "";
        StringBuilder error = new StringBuilder();
        error.append("[");

        if (!schemaNode.isJsonArray()) {
            throw new JsonSchemaException("Expected array for type property on Union Type Definition.");
        }

        int i = 0;
        for (JsonElement n : schemaNode.getAsJsonArray()) {
            JsonType t = TypeFactory.getSchemaNodeType(n);
            error.append(sep).append(t);
            sep = ", ";

            if (n.isJsonObject()) {
                schemas.add(new JsonSchema(mapper, ValidatorTypeCode.TYPE.getValue(), n, parentSchema));
            } else {
                schemas.add(new TypeValidator(schemaPath + "/" + i, n, parentSchema, mapper));
            }

            i++;
        }

        error.append("]");
        this.error = error.toString();
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        JsonType nodeType = TypeFactory.getValueNodeType(node);

        Set<ValidationMessage> _return = new HashSet<ValidationMessage>();
        boolean valid = false;

        for (JsonValidator schema : schemas) {
            Set<ValidationMessage> errors = schema.validate(node, rootNode, at);
            if (errors == null || errors.size() == 0) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            _return.add(buildValidationMessage(at, nodeType.toString(), error));
        }

        return _return;
    }

}
