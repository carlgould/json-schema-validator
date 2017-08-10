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
import java.util.List;
import java.util.Set;

import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnionTypeValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(UnionTypeValidator.class);

    private List<JsonValidator> schemas;
    private String error;

    public UnionTypeValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.UNION_TYPE);
        schemas = new ArrayList<>();
        List<String> errorList = new ArrayList<>();

        if (!schemaNode.isJsonArray()) {
            throw new JsonSchemaException("Expected array for type property on Union Type Definition.");
        }

        int i = 0;
        for (JsonElement n : schemaNode.getAsJsonArray()) {
            JsonType t = TypeFactory.getSchemaNodeType(n);
            errorList.add(t.toString());

            if (n.isJsonObject()) {
                schemas.add(new JsonSchema(ValidatorTypeCode.TYPE.getValue(), n, parentSchema));
            } else {
                schemas.add(new TypeValidator(schemaPath + "/" + i, n, parentSchema));
            }

            i++;
        }

        this.error = "[" + StringUtils.join(errorList, ", ") + "]";
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        JsonType nodeType = TypeFactory.getValueNodeType(node);

        boolean valid = false;

        for (JsonValidator schema : schemas) {
            Set<ValidationMessage> errors = schema.validate(node, rootNode, at);
            if (errors == null || errors.size() == 0) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            return Collections.singleton(buildValidationMessage(at, nodeType.toString(), error));
        } else {
            return Collections.emptySet();
        }
    }

}
