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
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public abstract class BaseJsonValidator implements JsonValidator {
    private String schemaPath;
    private JsonElement schemaNode;
    private JsonSchema parentSchema;
    private JsonSchema subSchema;
    private ValidatorTypeCode validatorType;
    private String errorCode;

    public BaseJsonValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema,
                             ValidatorTypeCode validatorType) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.parentSchema = parentSchema;
        this.validatorType = validatorType;
        this.subSchema = obtainSubSchemaNode(schemaNode);
    }

    public BaseJsonValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema,
                             ValidatorTypeCode validatorType, JsonSchema subSchema) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.parentSchema = parentSchema;
        this.validatorType = validatorType;
        this.subSchema = subSchema;
    }

    protected String getSchemaPath() {
        return schemaPath;
    }

    protected JsonElement getSchemaNode() {
        return schemaNode;
    }

    protected JsonSchema getParentSchema() {
        return parentSchema;
    }

    protected JsonSchema getSubSchema() {
        return subSchema;
    }

    protected boolean hasSubSchema() {
        return subSchema != null;
    }

    protected JsonSchema obtainSubSchemaNode(JsonElement schemaNode) {
        if (schemaNode.isJsonObject()) {
            JsonObject schemaObject = schemaNode.getAsJsonObject();
            JsonElement node = schemaObject.get("id");
            if (node == null) return null;
            if (node.equals(schemaObject.get("$schema"))) return null;

            try {
                JsonSchemaFactory factory = new JsonSchemaFactory();
                URL url = new URL(node.toString());
                return factory.getSchema(url);
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public Set<ValidationMessage> validate(JsonElement node) {
        return validate(node, node, AT_ROOT);
    }

    protected boolean equals(double n1, double n2) {
        return Math.abs(n1 - n2) < 1e-12;
    }

    protected boolean greaterThan(double n1, double n2) {
        return n1 - n2 > 1e-12;
    }

    protected boolean lessThan(double n1, double n2) {
        return n1 - n2 < -1e-12;
    }

    protected void parseErrorCode(String errorCodeKey) {
        if (getParentSchema().getSchemaNode().isJsonObject()) {
            JsonObject parentSchemaObject = getParentSchema().getSchemaNode().getAsJsonObject();
            JsonElement errorCodeNode = parentSchemaObject.get(errorCodeKey);
            if (errorCodeNode != null &&
                errorCodeNode.isJsonPrimitive() &&
                errorCodeNode.getAsJsonPrimitive().isString()) {
                errorCode = errorCodeNode.toString();
            }
        }
    }

    private String getErrorCode() {
        return errorCode;
    }

    private boolean isUsingCustomErrorCode() {
        return StringUtils.isNotBlank(errorCode);
    }

    protected ValidationMessage buildValidationMessage(String at, String... arguments) {
        ValidationMessage.Builder builder = new ValidationMessage.Builder();
        if (isUsingCustomErrorCode()) {
            builder.code(getErrorCode()).path(at).arguments(arguments).type(validatorType.getValue());
        } else {
            builder.code(validatorType.getErrorCode()).path(at).arguments(arguments)
                .format(validatorType.getMessageFormat()).type(validatorType.getValue());
        }
        return builder.build();
    }

    protected void debug(Logger logger, JsonElement node, JsonElement rootNode, String at) {
        if (logger.isDebugEnabled()) {
            logger.debug("validate( " + node + ", " + rootNode + ", " + at + ")");
        }
    }

    protected ValidatorTypeCode getValidatorType() {
        return validatorType;
    }

    static boolean isInteger(JsonElement element) {
        return TypeFactory.getValueNodeType(element) == JsonType.INTEGER;
    }

    public int asInt(JsonElement element) {
        return element.getAsJsonPrimitive().getAsNumber().intValue();
    }

    static boolean isBoolean(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
    }

    static boolean isNumber(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }

    static boolean isString(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    static String asText(JsonElement element) {
        return element.isJsonPrimitive() ? element.getAsJsonPrimitive().getAsString() : element.toString();
    }

    static double doubleValue(JsonElement element) {
        return element.getAsJsonPrimitive().getAsNumber().doubleValue();
    }
}
