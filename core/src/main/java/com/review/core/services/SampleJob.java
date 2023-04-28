package com.review.core.services;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;

@Component
@Designate(ocd=SampleSchedulerConfig.class)
public class SampleJob {
	
	String cronexpression;
	
	@Reference
	JobManager jobManager;
	
	
	@Activate
	public void Activate(SampleSchedulerConfig config) {
		cronexpression = config.getCronExpression();
		addJob();
	}
	
	public void addJob() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "deepak");
		map.put("employeeid","46109746");
		
		jobManager.addJob("sample/first/job", new HashMap<String,Object>());
	}

}
