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


import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.FileResourceManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.undertow.Handlers.resource;

public class JsonSchemaTest {
    protected static Undertow server = null;

    public JsonSchemaTest() {
    }

    @BeforeClass
    public static void setUp() {
        if (server == null) {
            server = Undertow.builder()
                .addHttpListener(1234, "localhost")
                .setHandler(resource(new FileResourceManager(
                    new File("./src/test/resources/tests"), 100)))
                .build();
            server.start();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (server != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
            server.stop();
        }
    }

    private void runTestFile(String testCaseFile) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(testCaseFile);

        JsonArray testCases = BaseJsonSchemaValidatorTest.getJsonElementFromStream(in).getAsJsonArray();

        for (int j = 0; j < testCases.size(); j++) {
            try {
                JsonObject testCase = testCases.get(j).getAsJsonObject();
                JsonSchema schema = new JsonSchema(testCase.get("schema"));
                JsonArray testNodes = testCase.get("tests").getAsJsonArray();
                for (int i = 0; i < testNodes.size(); i++) {
                    JsonObject test = testNodes.get(i).getAsJsonObject();
                    JsonElement node = test.get("data");
                    List<ValidationMessage> errors = new ArrayList<ValidationMessage>();

                    errors.addAll(schema.validate(node));

                    if (test.get("valid").getAsJsonPrimitive().getAsBoolean()) {
                        if (!errors.isEmpty()) {
                            System.out.println("---- test case filed ----");
                            System.out.println("schema: " + schema.toString());
                            System.out.println("data: " + test.get("data"));
                        }
                        Assert.assertEquals(StringUtils.join(errors, ", "), 0, errors.size());
                    } else {
                        if (errors.isEmpty()) {
                            System.out.println("---- test case filed ----");
                            System.out.println("schema: " + schema);
                            System.out.println("data: " + test.get("data"));
                        }
                        Assert.assertEquals(false, errors.isEmpty());
                    }
                }
            } catch (JsonSchemaException e) {
                System.out.println("Bypass validation due to invalid schema: " + e.getMessage());
            }
        }
    }

    @Test(/*expected = java.lang.StackOverflowError.class*/)
    public void testLoadingWithId() throws Exception {
        URL url = new URL("http://localhost:1234/self_ref/selfRef.json");
        JsonElement schemaJson = BaseJsonSchemaValidatorTest.getJsonElementFromStream(url.openStream());
        JsonSchemaFactory factory = new JsonSchemaFactory();
        JsonSchema schema = factory.getSchema(schemaJson);
    }

    @Test
    public void testBignumValidator() throws Exception {
        runTestFile("tests/optional/bignum.json");
    }

    @Test
    public void testFormatValidator() throws Exception {
        runTestFile("tests/optional/format.json");
    }

    @Test
    public void testZeroTerminatedFloatsValidator() throws Exception {
        runTestFile("tests/optional/zeroTerminatedFloats.json");
    }

    @Test
    public void testAdditionalItemsValidator() throws Exception {
        runTestFile("tests/additionalItems.json");
    }

    @Test
    public void testAdditionalPropertiesValidator() throws Exception {
        runTestFile("tests/additionalProperties.json");
    }

    @Test
    public void testAllOfValidator() throws Exception {
        runTestFile("tests/allOf.json");
    }

    @Test
    public void testAnyOFValidator() throws Exception {
        runTestFile("tests/anyOf.json");
    }

    @Test
    public void testDefaultValidator() throws Exception {
        runTestFile("tests/default.json");
    }

    @Test
    public void testDefinitionsValidator() throws Exception {
        runTestFile("tests/definitions.json");
    }

    @Test
    public void testDependenciesValidator() throws Exception {
        runTestFile("tests/dependencies.json");
    }

    @Test
    public void testEnumValidator() throws Exception {
        runTestFile("tests/enum.json");
    }

    @Test
    public void testItemsValidator() throws Exception {
        runTestFile("tests/items.json");
    }

    @Test
    public void testMaximumValidator() throws Exception {
        runTestFile("tests/maximum.json");
    }

    @Test
    public void testMaxItemsValidator() throws Exception {
        runTestFile("tests/maxItems.json");
    }

    @Test
    public void testMaxLengthValidator() throws Exception {
        runTestFile("tests/maxLength.json");
    }

    @Test
    public void testMaxPropertiesValidator() throws Exception {
        runTestFile("tests/maxProperties.json");
    }

    @Test
    public void testMinimumValidator() throws Exception {
        runTestFile("tests/minimum.json");
    }

    @Test
    public void testMinItemsValidator() throws Exception {
        runTestFile("tests/minItems.json");
    }

    @Test
    public void testMinLengthValidator() throws Exception {
        runTestFile("tests/minLength.json");
    }

    @Test
    public void testMinPropertiesValidator() throws Exception {
        runTestFile("tests/minProperties.json");
    }

    @Test
    public void testMultipleOfValidator() throws Exception {
        runTestFile("tests/multipleOf.json");
    }

    @Test
    public void testNotValidator() throws Exception {
        runTestFile("tests/not.json");
    }

    @Test
    public void testOneOfValidator() throws Exception {
        runTestFile("tests/oneOf.json");
    }

    @Test
    public void testPatternValidator() throws Exception {
        runTestFile("tests/pattern.json");
    }

    @Test
    public void testPatternPropertiesValidator() throws Exception {
        runTestFile("tests/patternProperties.json");
    }

    @Test
    public void testPropertiesValidator() throws Exception {
        runTestFile("tests/properties.json");
    }

    @Test
    public void testRefValidator() throws Exception {
        runTestFile("tests/ref.json");
    }

    @Test
    public void testRefRemoteValidator() throws Exception {
        runTestFile("tests/refRemote.json");
    }

    @Test
    public void testRequiredValidator() throws Exception {
        runTestFile("tests/required.json");
    }

    @Test
    public void testTypeValidator() throws Exception {
        runTestFile("tests/type.json");
    }

    @Test
    public void testUniqueItemsValidator() throws Exception {
        runTestFile("tests/uniqueItems.json");
    }

    // @Test
    // public void testIdSchemaWithUrl() throws Exception {
    //     runTestFile("tests/id_schema/property.json");
    // }

}
