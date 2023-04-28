package com.review.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.gson.JsonObject;

@Component(service=SampleScheduler.class,immediate=true)
@Designate(ocd=SampleSchedulerConfig.class)
public class SampleScheduler implements Runnable{

	Logger logger = LoggerFactory.getLogger(SampleScheduler.class);
	
	@Reference
	Scheduler scheduler;
	
	 @Reference
	 ResourceResolverFactory resourceResolverFactory;
	    
	 @Reference
	 QueryBuilder builder;
	    
	 @Reference
	 SimpleUsernameService config;
	
	public String cronexpression;
	
	public int schedulerId;
	
	@Activate
	public void Activate(SampleSchedulerConfig config) {
		this.cronexpression = config.getCronExpression();
		schedulerId =config.schedulerName().hashCode();
	}
	
	@Modified
	public void modified(SampleSchedulerConfig config) {
		removeScheduler();
		addScheduler(config);
		
	}
	@Deactivate
	protected void deactivate(SampleSchedulerConfig config) {
		removeScheduler();
	}
	
	public void addScheduler(SampleSchedulerConfig schedulerconfig) {
		//cronexpression is "0 8 * * * ?"  it executes at 8th minute of every hour
		//cronexpression is "0 15 10 ? * MON-FRI"  it executes 10:15 am MON-FRI
		ScheduleOptions sopts = scheduler.EXPR(cronexpression);
		//ScheduleOptions sopts = scheduler.NOW();
		sopts.name("sample scheduler");
		   sopts.canRunConcurrently(true);
		   scheduler.schedule(this,sopts);
		   logger.debug("SampleScheduler added succesfully");
	}
	
	public void removeScheduler() {
		scheduler.unschedule(String.valueOf(schedulerId));
	}
	
	@Override
	public void run() {
		logger.info("Scheduler scheduled for now at 2PM ");
		try {
		String serviceUserName = config.getUserName();
		logger.info("DisplayChildren ServiceUser Name read from config is {}",serviceUserName);
		Map<String, Object> usermap = new HashMap<>();
		usermap.put(ResourceResolverFactory.SUBSERVICE, serviceUserName);
		ResourceResolver serviceResolver = resourceResolverFactory.getServiceResourceResolver(usermap);
		logger.info("SampleScheduler serviceResolver is {}",serviceResolver);
		//ResourceResolver resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
		ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(usermap);
		Session session = resolver.adaptTo(Session.class);
		logger.info("ServiceResourceResolver is {}", resolver);
		JSONArray array = new JSONArray();
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","dam:AssetContent");
		map.put("path", "/content/dam/we-retail");
		map.put("1_property","cq:lastReplicationAction");
		map.put("1_property.value", "Deactivate");
		Query query = builder.createQuery(PredicateGroup.create(map), session);
		query.setStart(0);
		query.setHitsPerPage(20);
		SearchResult result = query.getResult();
		List<Hit> iterator = result.getHits();
		List<String> pagelist = new ArrayList<String>();
		for (Hit hit : iterator) {
			//pagelist.add(hit.getPath());
			//array.put(hit.getPath());
			String title = resolver.resolve(hit.getPath()).getValueMap().get("jcr:title","");
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(title,hit.getPath());
			array.put(jsonObject);
		}
		logger.info("List of deactivated assets is {}",array);
		}
		catch(Exception e) {
			logger.error("Exception occured in Sample Scheduler {}",e);
		}
	}
	}


