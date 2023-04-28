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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.review.core.services.ResourceResolverUtilityService;
import com.review.core.services.SimpleUsernameService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=/bin/page/createpage" })
public class ReviewPageCreateServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ReviewPageCreateServlet.class);
	public static final String CREATE_PAGE_SERVLET_PATH = "/bin/page/createpage";
	@Reference
	ResourceResolverFactory resourceResolverFactory;

	@Reference
	SimpleUsernameService simpleUsernameService;

	@Reference
	ResourceResolverUtilityService resourceResolverUtilityService;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		LOG.info("Inside dopost of ReviewPageCreateServlet");
		JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
		String parentpath = (null != data) ? data.get("pagepath").getAsString() : "/content/review";
		String template = data.get("template") != null ? data.get("template").getAsString() : "";
		String pagetitle = data.get("pagetitle") != null ? data.get("pagetitle").getAsString() : "";
		String pagename = data.get("pagename") != null ? data.get("pagename").getAsString() : "";
		String pagenewname = data.get("pagenewname") != null ? data.get("pagenewname").getAsString() : "";

		String serviceUser = simpleUsernameService.getUserName();
		LOG.info("ReviewPageCreateServlet serviceUser is {}", serviceUser);
		Map<String, Object> usermap = new HashMap<String, Object>();
		usermap.put(ResourceResolverFactory.SUBSERVICE, serviceUser);
		try {
			// ResourceResolver resolver =
			// resourceResolverFactory.getServiceResourceResolver(usermap);
			ResourceResolver resolver = resourceResolverUtilityService.getResourceResolver(serviceUser);
			PageManager pageManager = resolver.adaptTo(PageManager.class);
			Resource newpageresource = resolver.resolve(parentpath.concat("/").concat(pagename));
			Page page;
			if (ResourceUtil.isNonExistingResource(newpageresource)) {
				page = pageManager.create(parentpath, pagename, template, pagetitle);
				LOG.info("ReviewPageCreateServlet page created is {}", page);
				Session session = resolver.adaptTo(Session.class);
				session.save();
			} else {
				page = pageManager.getPage(parentpath.concat("/").concat(pagename));
				if (!pagename.equals(pagenewname)) {
					String newpagepath = parentpath.concat("/").concat(pagenewname);
					pageManager.move(page, newpagepath, pagename, false, false, null);
				}
				String newtitle = pagetitle;
				ModifiableValueMap modifiablemap = resolver.resolve(page.getPath().concat("/jcr:content"))
						.adaptTo(ModifiableValueMap.class);
				modifiablemap.put("jcr:title", newtitle);
				LOG.info("PageCreationServlet page path provided already exists {}", page.getPath());
				Session session = resolver.adaptTo(Session.class);
				session.save();
			}
		} catch (WCMException e) {
			LOG.error("RepositoryException occured in dopost of ReviewPageCreateServlet {}", e);
		} catch (Exception e) {
			LOG.error("Exception occured in dopost of ReviewCreatPageServlet {}", e);
		}
	}

}
