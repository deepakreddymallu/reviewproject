package com.review.core.services;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.models.export.spi.ModelExporter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;


@Component(service=WorkflowProcess.class,property= {"process.label=Page Json Response Process"})
public class PageJsonResponseProcess implements WorkflowProcess {

	Logger log = LoggerFactory.getLogger(PageJsonResponseProcess.class);
	
	 @Reference
	  private ModelExporter modelExporter;
	 
	 @Reference
	 private RequestResponseFactory requestResponseFactory;
	 

	   @Reference
	   private SlingRequestProcessor requestProcessor;
	  

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap) throws WorkflowException {
		String payloadpath = (String) workItem.getWorkflow().getWorkflowData().getPayload();

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		Resource container = resolver.resolve(payloadpath.concat("/jcr:content/root/responsivegrid"));
		Node containerNode = container.adaptTo(Node.class);
		log.info("container path in PageJsonResponseProcess is {}", container.getPath());
		NodeIterator nodeIterator;
		PrintWriter printWriter = null;
		
		Set<String> propertyNamesToIgnore = new HashSet<String>();
		propertyNamesToIgnore.add("jcr:createdBy");
		propertyNamesToIgnore.add("jcr:primaryType");
		propertyNamesToIgnore.add("jcr:lastModifiedBy");
		propertyNamesToIgnore.add("jcr:lastModified");
		propertyNamesToIgnore.add("jcr:created");
		propertyNamesToIgnore.add("cq:responsive");
		
		try {
			nodeIterator = containerNode.getNodes();
			String response = modelExporter.export(container, String.class, new HashMap<>());
			log.info("json response from PageJsonResponseProcess using modelExporter is {}", response);
			JsonItemWriter jsonWriter = new JsonItemWriter(propertyNamesToIgnore);
			File f = new File("C:/Deepak/sample.json");
			FileWriter writer = new FileWriter(f);
			BufferedWriter bufferedwriter = new BufferedWriter(writer);
			printWriter = new PrintWriter(bufferedwriter);
			StringWriter stringWriter = new StringWriter();
			log.info("Inside samplenav condition");
			jsonWriter.dump(nodeIterator, stringWriter);
			String json = stringWriter.toString();
			printWriter.write(response);
			printWriter.close();
		} catch (JSONException jsonException) {
			log.error("JSONException occured in PageJsonResponseProcess {}", jsonException);
		} catch (IOException e) {
			log.error("IOException occured in PageJsonResponseProcess {}", e);
		} catch (Exception e) {
			log.error("Exception occured in PageJsonResponseProcess {}", e);
		} finally {
			if (null != printWriter) {
				printWriter.close();
			}
		}

	}

}
