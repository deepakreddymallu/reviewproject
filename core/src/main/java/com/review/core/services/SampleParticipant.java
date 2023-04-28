package com.review.core.services;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component( service = ParticipantStepChooser.class, property = { ParticipantStepChooser.SERVICE_PROPERTY_LABEL + "=Sample review participant step chooser" })
public class SampleParticipant implements ParticipantStepChooser {
	
	static final Logger log = LoggerFactory.getLogger(SampleParticipant.class);
	
	@Override
	public String getParticipant(WorkItem arg0, WorkflowSession arg1, MetaDataMap arg2) throws WorkflowException {
		// TODO Auto-generated method stub
		ResourceResolver resolver = arg1.adaptTo(ResourceResolver.class);
		Session session = arg1.adaptTo(Session.class);
		UserManager usermanager = resolver.adaptTo(UserManager.class);
		String userid = session.getUserID();
		Authorizable auth = resolver.adaptTo(Authorizable.class);
		MetaDataMap map = arg0.getWorkflow().getMetaDataMap();
		log.info("Sample workflow metadatamap is {}",map);
		map.put("status", new String("activate"));
		Set<String> keyset = map.keySet();
		Iterator<String> i = keyset.iterator();
		while (i.hasNext()) {
			Object key = i.next();
			log.info("\r\rThe Sample workflow medata includes key {} and value {}\r\r", key.toString(),
					map.get(key).toString());
		}
		try {
			session.save();
			List<Route> routes = arg1.getRoutes(arg0, true);
			log.info("SampleworkflowParticipant routes size with argument true is {}",routes.size());
		} catch (RepositoryException e) {
	log.error("Exception while saving session in dynamicparticipantstep Sample Workflow{}",e);
		}
		return "admin";
	}

}
