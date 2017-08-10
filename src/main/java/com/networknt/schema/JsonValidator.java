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

import java.util.Set;

import com.google.gson.JsonElement;

/**
 * Standard json validator interface, implemented by all validators and JsonSchema.
 */
public interface JsonValidator {
    String AT_ROOT = "$";

    /**
     * Validate the given root JsonElement, starting at the root of the data path.
     *
     * @param rootNode JsonElement
     * @return A list of ValidationMessage if there is any validation error, or an empty
     * list if there is no error.
     */
    Set<ValidationMessage> validate(JsonElement rootNode);

    /**
     * Validate the given JsonElement, the given node is the child node of the root node at given
     * data path.
     *
     * @param node     JsonElement
     * @param rootNode JsonElement
     * @param at       String
     * @return A list of ValidationMessage if there is any validation error, or an empty
     * list if there is no error.
     */
    Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at);

}
