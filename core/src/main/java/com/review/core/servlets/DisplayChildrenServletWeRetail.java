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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.review.core.services.SimpleUsernameService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + DisplayChildrenServletWeRetail.DISPLAY_CHILDREN_SERVLET_WERETAILPATH,
		DisplayChildrenServletWeRetail.SLING_SERVLET_SELECTORS + "=display",
		DisplayChildrenServletWeRetail.SLING_SERVLET_SELECTORS + "=displayEditablePages",
		"sling.servlet.extensions=json" })
public class DisplayChildrenServletWeRetail extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DisplayChildrenServletWeRetail.class);
    public static final String DISPLAY_CHILDREN_SERVLET_WERETAILPATH = "/bin/show/displayweretailchild";
    public static final String PAGE_PATH="pagepath";
    public static final String display = "display";
    public static final String displayEditablePages = "displayEditablePages";
    public static final String SLING_SERVLET_SELECTORS = "sling.servlet.selectors";
    
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    @Reference
    QueryBuilder builder;
    
    @Reference
    SimpleUsernameService config;
    
    @Override
    protected void doGet(SlingHttpServletRequest request,SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("Inside doget of DisplayChildrenServlet");
		LOG.info("ResourceResolverFactory is {}", resourceResolverFactory);
		try {
			String path = request.getParameter(PAGE_PATH);
			String selector = request.getRequestPathInfo().getSelectorString();
			//LOG.info("DisplayChildrenServlet Config is {}", config);
			//LOG.error("DisplayChildrenServlet Config is {}", config);
			String serviceUserName = config.getUserName();
			LOG.info("DisplayChildren ServiceUser Name read from config is {}",serviceUserName);
			Map<String, Object> usermap = new HashMap<>();
			usermap.put(ResourceResolverFactory.SUBSERVICE, serviceUserName);
			ResourceResolver serviceResolver = resourceResolverFactory.getServiceResourceResolver(usermap);
			LOG.info("DisplayChildrenServletWeRetail serviceResolver is {}",serviceResolver);
			//ResourceResolver resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(usermap);
			Session session = resolver.adaptTo(Session.class);
			LOG.info("ServiceResourceResolver is {}", resolver);
			Resource pageresource = resolver.getResource(path);
			JSONArray array = new JSONArray();
			//if (path.contains("we-retail")) {
			switch(selector)
			{
				case displayEditablePages:
			{
				Map<String, String> map = new HashMap<String, String>();
				map.put("type","cq:Page");
				map.put("path", "/content/we-retail");
				map.put("1_param","cq:template");
				map.put("1_param.value", "/conf/we-retail/settings/wcm/templates/hero-page");
				map.put("group.p.and", "true");
				Query query = builder.createQuery(PredicateGroup.create(map), session);
				query.setStart(0);
				query.setHitsPerPage(20);

				SearchResult result = query.getResult();
				List<Hit> iterator = result.getHits();
				List<String> pagelist = new ArrayList<String>();
				for (Hit hit : iterator) {
					pagelist.add(hit.getPath());
					array.put(hit.getPath());
				}
				response.getWriter().write(array.toString());
				break;
			}
				case display:
				{
				Iterator<Resource> iterator = pageresource.listChildren();
				List<String> pagepaths = new ArrayList<String>();
				while (iterator.hasNext()) {
					Resource childresource = iterator.next();
					pagepaths.add(childresource.getPath());
					array.put(childresource.getPath());
				}
				response.getWriter().write(array.toString());
				break;
				}
				
				default:
				{
					response.getWriter().write("Please provide selector display or displayEditablePages");
				}
			}
    }
		catch (Exception e) {
			LOG.error("Exception occured in doget of DisplayChildrenServlet {}", e);
		}
	
		}
}