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

@Component(service=WorkflowProcess.class,immediate=true, property = {"process.label=Sample Review Custom Workflow Process" })
public class SampleWorkflowProcess implements WorkflowProcess {

	Logger log = LoggerFactory.getLogger(SampleWorkflowProcess.class);
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap) throws WorkflowException {
		
		try{
		Map map =workItem.getWorkflow().getWorkflowData().getMetaDataMap();
		log.info("metadatamap is {}",map);
		String payloadpath = (String) workItem.getWorkflowData().getPayload();
		log.info("SampleWorkflowProcess payloadpath is {}",payloadpath);
		
		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		Resource jcrContentResource = resolver.resolve(payloadpath.concat("/jcr:content"));
		
		String status = jcrContentResource.getValueMap().get("status","");
		String nextstep = jcrContentResource.getValueMap().get("nextstep","");
		log.info("SampleWorkflowProcess status is {}",status);
		log.info("SampleWorkflowProcess nextstep is {}",nextstep);
		
		List<Route> routes = workflowSession.getRoutes(workItem, true);
		log.info("SampleworkflowProcess routes size with argument true is {}",routes.size());
		List<Route> backRoutes = workflowSession.getBackRoutes(workItem, true); 
		List<Route> falsebackRoutes = workflowSession.getBackRoutes(workItem, false);
		log.info("SampleworkflowProcess backroutes size with argument true is {}",backRoutes.size());
		log.info("SampleworkflowProcess backroutes size with argument false is {}",falsebackRoutes.size());
		}
		catch(Exception e){
			log.error("Exception occured in sample workflow process {}",e);
		}
	}
}
