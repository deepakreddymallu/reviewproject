package com.review.core.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service=ResourceResolverUtilityService.class,immediate=true)
public class ResourceResolverUtilityServiceImpl implements ResourceResolverUtilityService{
	Logger LOG = LoggerFactory.getLogger(ResourceResolverUtilityServiceImpl.class);
	
	@Reference 
	ResourceResolverFactory resourceResolverFactory;
	
	@Override
	public ResourceResolver getResourceResolver(String serviceUser) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(ResourceResolverFactory.SUBSERVICE, serviceUser);
		ResourceResolver resolver =null;
		try {
			 resolver = resourceResolverFactory.getServiceResourceResolver(map);
		} catch (LoginException e) {
			LOG.error("LoginException occured in ResourceResolverUtilityServiceImpl {}",e);
		}
		catch(Exception e) {
			LOG.error("Exception occured in UtilityServiceImpl {}",e);
		}
		
		return resolver;
	}

}
