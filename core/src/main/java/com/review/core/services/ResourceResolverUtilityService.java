package com.review.core.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface ResourceResolverUtilityService {
	
	ResourceResolver getResourceResolver(String serviceUser);

}
