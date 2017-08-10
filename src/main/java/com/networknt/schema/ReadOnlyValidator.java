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
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Carl says: I can't find where the readOnly check is defined in the json schema spec. Maybe delete?
 */
public class ReadOnlyValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(RequiredValidator.class);

    private List<String> fieldNames = new ArrayList<String>();

    public ReadOnlyValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.READ_ONLY);
        if (schemaNode.isJsonArray()) {
            for (JsonElement element : schemaNode.getAsJsonArray()) {
                fieldNames.add(asText(element));
            }
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = new HashSet<>();

        for (String fieldName : fieldNames) {
            JsonElement propertyNode = node.getAsJsonObject().get(fieldName);
            String datapath = "";
            if (at.equals("$")) {
                datapath = datapath + "#original." + fieldName;
            } else {
                datapath = datapath + "#original." + at.substring(2) + "." + fieldName;
            }
            JsonElement originalNode = getNode(datapath, rootNode.getAsJsonObject());

            boolean theSame = propertyNode != null && originalNode != null && propertyNode.equals(originalNode);
            if (!theSame) {
                errors.add(buildValidationMessage(at));
            }
        }

        return errors;
    }

    private JsonElement getNode(String datapath, JsonObject data) {
        String path = datapath;
        if (path.startsWith("$.")) {
            path = path.substring(2);
        }

        String[] parts = path.split("\\.");
        JsonObject result = null;
        for (String part : parts) {
            if (part.contains("[")) {
                int idx1 = part.indexOf("[");
                int idx2 = part.indexOf("]");
                String key = part.substring(0, idx1).trim();
                int idx = Integer.parseInt(part.substring(idx1 + 1, idx2).trim());
                result = data.get(key).getAsJsonArray().get(idx).getAsJsonObject();
            } else {
                result = data.get(part).getAsJsonObject();
            }
            if (result == null) {
                break;
            }
            data = result;
        }
        return result;
    }

}
