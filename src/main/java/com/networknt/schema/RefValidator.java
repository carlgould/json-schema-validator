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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class RefValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(RefValidator.class);

    protected JsonSchema schema;
    
    private final String REF_DOMAIN = "/";
    private final String REF_CURRENT = "#";
    private final String REF_RELATIVE = "../";

    public RefValidator(String schemaPath, JsonElement schemaNode, JsonSchema parentSchema, Gson mapper) {

        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.REF);
        String refValue = asText(schemaNode);
        if (!refValue.startsWith(REF_CURRENT)) {
            // handle remote ref
        	String schemaUrl = refValue;
        	int index = refValue.indexOf(REF_CURRENT);
            if (index > 0) {
                schemaUrl = schemaUrl.substring(0, index);
            }
        	if(isRelativePath(schemaUrl)){
        		schemaUrl = obtainAbsolutePath(parentSchema, schemaUrl);
        	}
            
            JsonSchemaFactory factory = new JsonSchemaFactory(mapper);
            try {
                URL url = new URL(schemaUrl);
                parentSchema = factory.getSchema(url);
            } catch (MalformedURLException e) {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaUrl);
                parentSchema = factory.getSchema(is);
            }
            if (index < 0) {
                schema = parentSchema.findAncestor();
            } else {
                refValue = refValue.substring(index);
            }
        }
        if (refValue.equals(REF_CURRENT)) {
            schema = parentSchema.findAncestor();
        } else {
            JsonElement node = parentSchema.getRefSchemaNode(refValue);
            if (node != null) {
                schema = new JsonSchema(mapper, refValue, node, parentSchema);
            }
        }
    }
    
    private boolean isRelativePath(String schemaUrl) {
    	return !schemaUrl.startsWith("http");
    }
    
    private String obtainAbsolutePath(JsonSchema parentSchema, String schemaUrl) {
    	String baseSchemaUrl = asText(parentSchema.findAncestor().getSchemaNode().getAsJsonObject().get("id"));
		int index = baseSchemaUrl.lastIndexOf("/");
		baseSchemaUrl = baseSchemaUrl.substring(0, index);
		
		String schemaRef = schemaUrl;
		
		if(schemaRef.startsWith(REF_DOMAIN)){
			// from domain add ref
			try {
				URL url = new URL(baseSchemaUrl);
				baseSchemaUrl = url.getProtocol()+"//"+url.getHost();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}else if(schemaRef.startsWith(REF_RELATIVE)){
			// relative from schema
			while(schemaRef.startsWith(REF_RELATIVE)){
				index = baseSchemaUrl.lastIndexOf("/");
				baseSchemaUrl = baseSchemaUrl.substring(0, index);
				schemaRef = schemaRef.replaceFirst(REF_RELATIVE, "");
			}
		}
		schemaRef = baseSchemaUrl +"/"+ schemaRef;
		return schemaRef;
    }

    public Set<ValidationMessage> validate(JsonElement node, JsonElement rootNode, String at) {
        debug(logger, node, rootNode, at);

        if (schema != null) {
            return schema.validate(node, rootNode, at);
        } else {
            return new HashSet<ValidationMessage>();
        }
    }

}
