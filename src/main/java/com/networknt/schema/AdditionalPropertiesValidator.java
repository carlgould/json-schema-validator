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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdditionalPropertiesValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(AdditionalPropertiesValidator.class);

    private boolean allowAdditionalProperties;
    private JsonSchema additionalPropertiesSchema;
    private Set<String> allowedProperties = new HashSet<String>();
    private List<Pattern> patternProperties = new ArrayList<Pattern>();

    public AdditionalPropertiesValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.ADDITIONAL_PROPERTIES);
        allowAdditionalProperties = false;
        if (isBoolean(schemaNode)) {
            allowAdditionalProperties = schemaNode.getAsJsonPrimitive().getAsBoolean();
        }
        if (schemaNode.isJsonObject()) {
            allowAdditionalProperties = true;
            additionalPropertiesSchema =
                new JsonSchema(getValidatorType().getValue(), schemaNode, parentSchema);
        }

        JsonObject parentSchemaObj = parentSchema.getSchemaNode().getAsJsonObject();

        JsonElement propertiesNode = parentSchemaObj.get(PropertiesValidator.PROPERTY);
        if (propertiesNode != null) {
            allowedProperties.addAll(propertiesNode.getAsJsonObject().keySet());
        }

        JsonElement patternPropertiesNode = parentSchemaObj.get(PatternPropertiesValidator.PROPERTY);
        if (patternPropertiesNode != null) {
            for (String pattern : patternPropertiesNode.getAsJsonObject().keySet()) {
                patternProperties.add(Pattern.compile(pattern));
            }
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        if (logger.isDebugEnabled()) debug(logger, node, rootNode, at);

        Set<ValidationMessage> errors = new HashSet<>();
        if (!node.isJsonObject()) {
            // ignore no object
            return errors;
        }

        for (String pname : node.getAsJsonObject().keySet()) {
            // skip the context items
            if (pname.startsWith("#")) {
                continue;
            }
            boolean handledByPatternProperties = false;
            for (Pattern pattern : patternProperties) {
                Matcher m = pattern.matcher(pname);
                if (m.find()) {
                    handledByPatternProperties = true;
                    break;
                }
            }

            if (!allowedProperties.contains(pname) && !handledByPatternProperties) {
                if (!allowAdditionalProperties) {
                    errors.add(buildValidationMessage(at, pname));
                } else {
                    if (additionalPropertiesSchema != null) {
                        JsonElement value = node.getAsJsonObject().get(pname);
                        errors.addAll(additionalPropertiesSchema.validate(value, rootNode, at + "." + pname));
                    }
                }
            }
        }
        return errors;
    }

}
