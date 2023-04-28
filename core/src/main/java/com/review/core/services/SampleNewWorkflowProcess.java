package com.review.core.services;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowTransition;
import com.adobe.granite.workflow.exec.Route;

@Component(service=WorkflowProcess.class,immediate=true, property = {"process.label=SampleNew Review Custom Workflow Process" })
public class SampleNewWorkflowProcess implements WorkflowProcess {

	Logger log = LoggerFactory.getLogger(SampleNewWorkflowProcess.class);
	public static final String ACTIVATE="activate";
	public static final String DEACTIVATE="deactivate";
	public static final String CREATE_VERSION="createversion";
	public static final String DEFAULT="default";
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap) throws WorkflowException {
		try{
		Map map =workItem.getWorkflow().getWorkflowData().getMetaDataMap();
		log.info("metadatamap is {}",map);
		String payloadpath = (String) workItem.getWorkflowData().getPayload();
		log.info("SampleNewWorkflowProcess payloadpath is {}",payloadpath);
		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		Resource jcrContentResource = resolver.resolve(payloadpath.concat("/jcr:content"));
		String status = jcrContentResource.getValueMap().get("status","");
		String nextstep = jcrContentResource.getValueMap().get("nextstep","");
		log.info("SampleNewWorkflowProcess status and nextstep is {}:{}",status,nextstep);
		int destination=0;
		switch(nextstep) {
		case ACTIVATE:{
			destination=0;
			break;
		}
		case DEACTIVATE:{
			destination=1;
			break;
		}
		case CREATE_VERSION:{
			destination=2;
			break;
		}
		}
		List<Route> routes = workflowSession.getRoutes(workItem, true);
		log.info("SampleNewworkflowProcess routes size with argument true is {}",routes.size());
		log.info("SampleNewworkflowProcess routes0name is {}", routes.get(0).getName());
		
		workflowSession.complete(workItem, routes.get(destination));
		}
		catch(Exception e){
			log.error("Exception occured in sampleNew workflow process {}",e);
		}
	}
}
