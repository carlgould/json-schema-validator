package com.networknt.schema;

import com.google.gson.JsonElement;
import org.junit.Test;

/**
 * Created by stevehu on 2016-12-20.
 */
public class SelfRefTest extends BaseJsonSchemaValidatorTest {
    @Test
    public void testSelfRef() throws Exception {
        JsonElement node = getJsonElementFromClasspath("selfref.json");
        System.out.println("node = " + node);
    }
}
