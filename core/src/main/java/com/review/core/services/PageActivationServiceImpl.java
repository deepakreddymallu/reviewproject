package com.review.core.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

@Component(service=PageActivationService.class,immediate=true)
public class PageActivationServiceImpl implements PageActivationService{
	/*{"text":[
	         "/content/we-retail/us/en/equipment",
	         "/content/we-retail/us/en/about-us"]
	     }*/
	private static final Logger log = LoggerFactory.getLogger(PageActivationServiceImpl.class);
	@Reference
	Replicator replicationService;
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	ResourceResolverUtilityService resourceResolverUtilityService;
	
	@Reference
	SimpleUsernameService simpleUsernameService;
	
	@Override
	public void activatePages(List<String> pagepaths) {
		try {
			log.info("Inside activatePages in PageActivationServiceImpl");
			String serviceUser = simpleUsernameService.getUserName();
			ResourceResolver resolver = resourceResolverUtilityService.getResourceResolver(serviceUser);
			Session session = resolver.adaptTo(Session.class);
			for(String pagepath:pagepaths) {
			replicationService.replicate(session, ReplicationActionType.ACTIVATE, pagepath);
			}
		} catch (ReplicationException e) {
			log.error("ReplicationException occured in PageActivateServiceImpl {}",e);
		}
		catch (Exception e) {
			log.error("Exception occured in PageActivateServiceImpl {}",e);
		}
	}

	@Override
	public void deactivatePages(List<String> pagepaths) {
		try {
			log.info("Inside deactivatePages in PageActivationServiceImpl");
			String serviceUser = simpleUsernameService.getUserName();
			ResourceResolver resolver = resourceResolverUtilityService.getResourceResolver(serviceUser);
			Session session = resolver.adaptTo(Session.class);
			
			for(String pagepath:pagepaths) {
			replicationService.replicate(session, ReplicationActionType.DEACTIVATE, pagepath);
			}
		} catch (ReplicationException e) {
			log.error("ReplicationException occured in PageActivateServiceImpl {}",e);
		}
		catch (Exception e) {
			log.error("Exception occured in PageActivateServiceImpl deactivation{}",e);
		}
		
	}

}
