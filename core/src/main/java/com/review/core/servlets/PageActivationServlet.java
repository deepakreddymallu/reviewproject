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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
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

import com.review.core.services.PageActivationService;
import com.review.core.services.PageActivationServiceImpl;
import com.review.core.services.SimpleUsernameService;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;



@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths="+PageActivationServlet.REPLICATE_PAGES_SERVLET_PATH,
                "sling.servlet.selectors=activate",
                "sling.servlet.selectors=deactivate"
        }
)
public class PageActivationServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PageActivationServlet.class);
    public static final String REPLICATE_PAGES_SERVLET_PATH = "/bin/paths/replicate";
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    @Reference
    SimpleUsernameService simpleUsernameService;
    
    @Reference
    PageActivationService pageActivationService;
    
    private static final String SELECTOR = "selector";
    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";
    
    @Override
    protected void doPost(SlingHttpServletRequest request,SlingHttpServletResponse response)
			throws IOException {
		LOG.info("Inside doget of PageActivationServlet");
		
		try {
			String[] selectors = request.getRequestPathInfo().getSelectors();
			String selector = selectors[0];
			LOG.info("PageActivationServlet selector is {}",selector);
		JsonObject json = new Gson().fromJson(request.getReader(),JsonObject.class);
		JsonArray pagesJson = json.get("text").getAsJsonArray();
		Iterator<JsonElement> iterator = pagesJson.iterator();
		List<String> pagesList = new ArrayList<String>();
		while(iterator.hasNext()) {
			JsonElement jsonElement = iterator.next();
			String pagepath = jsonElement.getAsString();
			pagesList.add(pagepath);
		}
		LOG.info("pageactivation paths list size is {} : {}",pagesList.get(0),pagesList.get(1));
		switch(selector) {
		case ACTIVATE: {
			pageActivationService.activatePages(pagesList);
			break;
		}
		case DEACTIVATE:{
			pageActivationService.deactivatePages(pagesList);
			break;
		}
		}

		} catch (Exception e) {
			LOG.error("Exception occured in dopost of PageActivationServlet {}", e);
		}
	}

}
