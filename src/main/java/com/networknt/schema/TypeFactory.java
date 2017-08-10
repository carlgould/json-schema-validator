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

import java.math.BigInteger;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class TypeFactory {
    public static JsonType getSchemaNodeType(JsonElement node) {
        //Single Type Definition
        if (node.isJsonPrimitive()) {
            String type = node.getAsJsonPrimitive().getAsString();
            if ("object".equals(type)) {
                return JsonType.OBJECT;
            }
            if ("array".equals(type)) {
                return JsonType.ARRAY;
            }
            if ("string".equals(type)) {
                return JsonType.STRING;
            }
            if ("number".equals(type)) {
                return JsonType.NUMBER;
            }
            if ("integer".equals(type)) {
                return JsonType.INTEGER;
            }
            if ("boolean".equals(type)) {
                return JsonType.BOOLEAN;
            }
            if ("any".equals(type)) {
                return JsonType.ANY;
            }
            if ("null".equals(type)) {
                return JsonType.NULL;
            }
        }

        //Union Type Definition
        if (node.isJsonArray()) {
            return JsonType.UNION;
        }

        return JsonType.UNKNOWN;
    }

    public static JsonType getValueNodeType(JsonElement node) {
        if (node.isJsonObject()) return JsonType.OBJECT;
        if (node.isJsonArray()) return JsonType.ARRAY;
        if (node.isJsonNull()) return JsonType.NULL;
        if (node.isJsonPrimitive()) {
            JsonPrimitive primitive = node.getAsJsonPrimitive();
            if (primitive.isString()) return JsonType.STRING;
            if (primitive.isNumber()) {
                String numberAsString = primitive.getAsNumber().toString();
                try {
                    Long.parseLong(numberAsString);
                    return JsonType.INTEGER;
                } catch (NumberFormatException ignored) {
                    try {
                        new BigInteger(numberAsString);
                        return JsonType.INTEGER;
                    } catch (NumberFormatException moreIgnored) {
                        return JsonType.NUMBER;
                    }
                }
            }
            if (primitive.isBoolean()) return JsonType.BOOLEAN;
        }
        return JsonType.UNKNOWN;
    }

}
