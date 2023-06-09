package com.review.core.eventlistener;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.apache.jackrabbit.api.observation.JackrabbitEvent;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.review.core.services.ResourceResolverUtilityService;
import com.review.core.services.SimpleUsernameService;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import java.beans.EventSetDescriptor;
import java.util.HashMap;
import java.util.Map;


@Component(
        name = "Sample - JCR Event Listener",
        immediate = true)
public class SampleJcrEventListener implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(SampleJcrEventListener.class);

    /*
     * A combination of one or more event type constants encoded as a bitmask
     *
     * Available JCR Events:
     *
     * Event.NODE_ADDED
     * Event.NODE_MOVED
     * Event.NODE_REMOVED
     * Event.PERSIST
     * Event.PROPERTY_ADDED
     * Event.PROPERTY_REMOVED
     * Event.PROPERTY_CHANGED
    */
    //private final int events = Event.PROPERTY_ADDED | Event.PROPERTY_REMOVED | Event.PROPERTY_CHANGED | Event.NODE_ADDED | Event.NODE_REMOVED | Event.NODE_MOVED;
    private final int events = Event.NODE_ADDED; 
    // Only events whose associated node is at absPath (or within its subtree, if isDeep is true) will be received.
    // It is permissible to register a listener for a path where no node currently exists.
    private final String absPath = "/content/we-retail";
    private final boolean isDeep = false;

    // If noLocal is true, events generated by the session through which the listener was registered are ignored.
    // Otherwise, they are not ignored.
    private final boolean noLocal = false;

    private final String[] uuids = null;

    // Only events whose associated node has one of the node types (or a subtype of one of the node types) in this list will be received. If his parameter is null then no node type-related restriction is placed on events received.
    private final String[] nodeTypes = new String[]{"nt:unstructured", "nt:folder","cq:Page"};

    // This is one of the VERY FEW times that a JCR Session is appropriate in an OSGi Services instance var space
    // This must be left open for the life of the Event Listener as this is the security context that scopes what
    // events this listener can see.
    private Session observationSession = null;


    @Reference
    private SlingRepository repository;

    @Reference
    private EventAdmin eventAdmin;
    
    @Reference
    SimpleUsernameService simpleUsernameService;
    
    @Reference
    private ResourceResolverUtilityService resourceResolverUtilityService;

    @Override
    public void onEvent(final EventIterator events) {
        // Handle events
        while (events.hasNext()) {
            try {
                Event event = events.nextEvent();

                final String path = event.getPath();

                if (Event.NODE_ADDED == event.getType()) {
                    log.info("SampleJcrEventListner Node added :{}",event.getPath());
                } 
                else if (Event.NODE_REMOVED == event.getType()) {
                	log.info("SampleJcrEventListner Node removed :{}",event.getPath());
                }
                else if (Event.NODE_MOVED == event.getType()) {
                	log.info("SampleJcrEventListner Node moved :{}",event.getPath());
                }
                else if (Event.PROPERTY_ADDED == event.getType()) {
                    // Property added!
                	log.info("SampleJcrEventListner Property added :{}",event.getPath());
                }
                else if (Event.PROPERTY_CHANGED== event.getType()) {
                	log.info("SampleJcrEventListner Property changed :{}",event.getPath());
                }
                else if (Event.PROPERTY_REMOVED== event.getType()) {
                	log.info("SampleJcrEventListner Property removed :{}",event.getPath());
                }
                
            } catch (RepositoryException e) {
                log.error("Repository Exception occured SampleJcrEventListener {}", e);
            }
            catch(Exception e) {
            	log.error("Exception occured SampleJcrEventListener {}", e);
            }
        }
    }

    @Activate
    public void activate(final Map<String, String> config) throws RepositoryException {
    	String serviceUser = simpleUsernameService.getUserName();
    	 // Getting the JCR Session to bind this Event Listener 
		observationSession = repository.loginService(serviceUser, null);
        // Get JCR ObservationManager from Workspace
        final ObservationManager observationManager  = observationSession.getWorkspace().getObservationManager();

        // Register the JCR Listener

        /** !! This is the KEY element where this listener is registered !! **/
        observationManager.addEventListener(this, events, absPath, isDeep,
                uuids, nodeTypes, noLocal);
    }

    @Deactivate
    public void deactivate(final Map<String, String> config) throws RepositoryException {
        try {
            // Get JCR ObservationManager from Workspace
            final ObservationManager observationManager = observationSession.getWorkspace().getObservationManager();

            if (observationManager != null) {
                // Un-register event handler
                observationManager.removeEventListener(this);
            }
        } finally {
            if (observationSession != null) {
                observationSession.logout();
            }
        }
    }
}
