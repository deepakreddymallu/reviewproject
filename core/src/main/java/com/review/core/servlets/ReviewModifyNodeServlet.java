/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.review.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import org.apache.sling.api.resource.ResourceResolverFactory;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*{
    "text": {
        "path": "/content/review/newreview/jcr:content",
        "properties": {
            "jcr:title": "value4",
            "property2": "value5",
            "property3":"value6"
        }
    }
}*/
@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.methods=" + HttpConstants.METHOD_DELETE,
		"sling.servlet.paths=" + ReviewModifyNodeServlet.UPDATE_NODE_SERVLET_PATH })
public class ReviewModifyNodeServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ReviewModifyNodeServlet.class);
	public static final String UPDATE_NODE_SERVLET_PATH = "/bin/review/updatenode";
	@Reference
	ResourceResolverFactory resourceResolverFactory;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws IOException {
		LOG.info("Inside dopost of ReviewServlet");
		Map<String, Object> usermap = new HashMap<String, Object>();
		usermap.put(ResourceResolverFactory.SUBSERVICE, "reviewserviceuser");
		try {
			ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(usermap);
			LOG.info("ServiceResourceResolver is {}", resolver);
			JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
			JsonObject text = data.getAsJsonObject("text");
			JsonObject propertiesJson = text.getAsJsonObject("properties");
			String path = text.get("path").getAsString();

			Map map = new Gson().fromJson(propertiesJson, Map.class);
			Resource reviewresource = resolver.getResource(path);
			Node node = reviewresource.adaptTo(Node.class);

			Set<String> keyset = map.keySet();
			for (String key : keyset) {
				LOG.info("ReviewModifyNodeServlet Key is {}", key);
				LOG.info("ReviewModifyNodeServlet Value is {}", map.get(key));
				node.setProperty(key, map.get(key).toString());
			}
			LOG.info("Node in newnode path is {}", node.getPath());
			Session session = node.getSession();
			session.save();

		} catch (LoginException e) {
			LOG.error("LoginException occured in dopost of ReviewServlet {}", e);
		} catch (RepositoryException e) {
			LOG.error("RepositoryException occured in dopost of ReviewServlet {}", e);
		} catch (Exception e) {
			LOG.error("Exception occured in dopost of ReviewServlet {}", e);
		}
	}

	@Override
	protected void doDelete(SlingHttpServletRequest request,SlingHttpServletResponse response)
			throws IOException {
		//String path = "/content/review/newreview/jcr:content/newnode";
		try {
			Map<String, Object> usermap = new HashMap<String, Object>();
			usermap.put(ResourceResolverFactory.SUBSERVICE, "reviewserviceuser");
			ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(usermap);
			
			JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
			JsonObject text = data.getAsJsonObject("text");
			JsonObject propertiesJson = text.getAsJsonObject("properties");
			String path = text.get("path").getAsString();
			
			Resource deleteresource = resolver.getResource(path);
			Node deletenode = deleteresource.adaptTo(Node.class);
			deletenode.remove();
			Session session = deletenode.getSession();
			session.save();
		} catch (LoginException e) {
			LOG.error("LoginException occured in doDelete of ReviewModifyServlet {}", e);
		} catch (RepositoryException e) {
			LOG.error("RepositoryException occured in doDelete of ReviewModifyServlet {}", e);
		}
		catch(Exception e) {
			LOG.error("Exception occured in doDelete of ReviewModifyServlet {}",e);
		}

	}
}
