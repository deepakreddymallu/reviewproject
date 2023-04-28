package com.review.core.eventlistener;

import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.review.core.services.ResourceResolverUtilityService;
import com.review.core.services.SimpleUserConfig;
import com.review.core.services.SimpleUsernameConfig;
import com.review.core.services.SimpleUsernameService;

@Component(service = ReviewNodeModifiedListener.class, immediate = true)
public class ReviewNodeModifiedListener implements EventListener {

	Logger log = LoggerFactory.getLogger(ReviewNodeModifiedListener.class);

	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	 @Reference
	 private SlingRepository repository;
	
	@Reference
	SimpleUsernameService simpleUsernameService;
	    

	@Activate
	protected void activate(ComponentContext context) {
		log.info("ReviewNodeModifiedListener activate method");

		try {
			String serviceUser = simpleUsernameService.getUserName();
			Session session =repository.loginService(serviceUser, null);
			log.info("ReviewNodeModified session is {}",session);
			session.getWorkspace().getObservationManager().addEventListener(this,
					Event.NODE_ADDED | Event.NODE_REMOVED | Event.PROPERTY_CHANGED, "/content/we-retail/us/jcr:content",
					false, null, new String[] { "cq:PageContent", "nt:unstructured" }, true);
		}  catch (RepositoryException e) {
			log.error("ReviewNodeMofifiedListener Respository exception {}", e);
		} catch (Exception e) {
			log.error("ReviewNodeMofifiedListener exception occured {}", e);
		}

	}

	@Override
	public void onEvent(final EventIterator events) {
		try {
			log.info("ReviewNodeModifiedListener changelistener logged path{}", events.nextEvent().getPath());
			while (events.hasNext()) {
				Event event = events.nextEvent();
				log.info("ReviewNodeModifiedListener changelistener logged path{}", event.getPath());
				if (event.getType() == event.PROPERTY_CHANGED) {
					String eventpath = event.getPath();
					log.info("Property modified in ReviewNodeModifiedListener {}", event.getPath());
				}

				log.info("ReviewNodeModifiedListener Something has been added or modified {}", event.getPath());
			}
		} catch (RepositoryException e) {
			log.error("ReviewNodeModifiedListener exception occured {}", e);
		} catch (Exception e) {
			log.error("Exception in ReviewNodeModifiedListener {}", e);
		} finally {
			log.info("ReviewNodeModifiedListener finally block ");
		}

	}

}
