package com.review.core.eventlistener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.JcrTagManagerFactory;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

/**
 * This is simple event listener which tags a page as soon as it is created
 */

@Component(service=ResourceChangeListener.class,property= {ResourceChangeListener.PATHS+"="+"/content/we-retail/de/jcr:content",
		ResourceChangeListener.CHANGES+"="+"ADDED",
		ResourceChangeListener.CHANGES +"="+"REMOVED",
		ResourceChangeListener.CHANGES +"="+"CHANGED"})
public class SampleListener implements ResourceChangeListener {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(SampleListener.class);
	
	private static final String ADDED = "ADDED";
	private static final String REMOVED = "REMOVED";
	private static final String CHANGED = "CHANGED";

	/**
	 * Injecting Sling Resource Resolver Factory
	 */
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	private JobManager jobManager;

	/**
	 * Session instance
	 */
	private Session session;

	/**
	 * Observation Manager
	 */
	private ObservationManager observationManager;

	/**
	 * Placing the application's logic here to define custom event handler
	 */
	@Activate
	protected void activate(ComponentContext context) {
		context.getBundleContext();

		try {
			/**
			 * Creating a session
			 */
			ResourceResolver resolver = resolverFactory.getAdministrativeResourceResolver(null);
			session = resolver.adaptTo(Session.class);

			/**
			 * Setup the event handler
			 */
		//	observationManager = session.getWorkspace().getObservationManager();

			/**
			 * Types of nodes where the event listening needs to take place
			 */
			final String[] types = { "cq:Page", "nt:unstructured" };
			
			/**
			 * Path under which event listening should take place
			 */
			final String path = "/";

			/**
			 * Adding the event listener in the Observation Manager
			 */
			
			/**
			 * Logging the information
			 */
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Clean up activities
	 */
	@Deactivate
	protected void deactivate(ComponentContext componentContext) throws RepositoryException {

		if (observationManager != null) {
			observationManager.removeEventListener((EventListener) this);
		}
		if (session != null) {
			session.logout();
			session = null;
		}
	}


	@Override
	public void onChange(List<ResourceChange> changes) {
		try {
			for(ResourceChange change:changes) {
				log.info("SampleListener Resource changed path is {}",change.getPath());
				log.info("SampleListener Resource changed is {}",change);
				switch(change.getType()) {
				case CHANGED:
				{
					log.info("SampleResourceChangeListener Changed event");
					 final Map<String, Object> props = new HashMap<String, Object>();
                     props.put("path", change.getPath());
                     props.put("userId", change.getUserId());
                     jobManager.addJob("com/adobe/weretail/samples/titlePropChanged", props);
                break;
				}

				case ADDED:
				{
					log.info("SampleResourceChangeListener ADDED event");
					 final Map<String, Object> props = new HashMap<String, Object>();
                     props.put("path", change.getPath());
                     props.put("userId", change.getUserId());
                     jobManager.addJob("com/adobe/weretail/samples/propNodeAdded", props);
                    break;
				}
				default :
				{
					log.info("SampleResourceChangeListener do nothing");
				}
				}
			}
		}
		catch(Exception e) {
			log.error("Exception occured in SampleListenerResourceChangeListener{}",e.getMessage());
		}
	}

}