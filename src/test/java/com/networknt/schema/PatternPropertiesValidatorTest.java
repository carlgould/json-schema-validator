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
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by steve on 22/10/16.
 */
public class PatternPropertiesValidatorTest extends BaseJsonSchemaValidatorTest {

    @Test(expected=JsonSchemaException.class)
    public void testInvalidPatternPropertiesValidator() throws Exception {
        JsonSchemaFactory factory = new JsonSchemaFactory();
        JsonSchema schema = factory.getSchema("{\"patternProperties\":6}");

        JsonElement node = getJsonElementFromStringContent("");
        Set<ValidationMessage> errors = schema.validate(node);
        Assert.assertEquals(errors.size(), 0);
    }
}
