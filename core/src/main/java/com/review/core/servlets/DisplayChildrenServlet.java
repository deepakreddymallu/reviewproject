package com.review.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
//import org.apache.sling.commons.json.JSONArray;
//import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ DisplayChildrenServlet.DISPLAY_CHILDREN_SERVLET_PATH
               }
)
public class DisplayChildrenServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DisplayChildrenServlet.class);
    public static final String DISPLAY_CHILDREN_SERVLET_PATH = "/bin/show/displaychildren";
    public static final String PAGE_PATH="pagepath";
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    @Override
    protected void doGet(SlingHttpServletRequest request,SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("Inside doget of DisplayChildrenServlet");
		LOG.info("ResourceResolverFactory is {}", resourceResolverFactory);
		try {
			String path = request.getParameter(PAGE_PATH);
			String selector = request.getRequestPathInfo().getSelectorString();
			Map<String, Object> usermap = new HashMap<String, Object>();
			usermap.put(ResourceResolverFactory.SUBSERVICE, "reviewserviceuser");
			ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(usermap);
			LOG.info("ServiceResourceResolver is {}", resolver);
			Resource pageresource = resolver.getResource(path);
			Node pagenode = pageresource.adaptTo(Node.class);
			NodeIterator nodeIterator = pagenode.getNodes();
			List<String> pagepaths = new ArrayList<String>();
			JSONArray array = new JSONArray();
			while (nodeIterator.hasNext()) {
				Node childnode = nodeIterator.nextNode();
				pagepaths.add(childnode.getPath());
				array.put(childnode.getPath());
			}
			response.getWriter().write(array.toString());

		} catch (Exception e) {
			LOG.error("Exception occured in doget of DisplayChildrenServlet {}", e);
		}
	}

}
