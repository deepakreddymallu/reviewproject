package com.review.core.services;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutionResult;
import org.apache.sling.event.jobs.consumer.JobExecutor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service=JobExecutor.class,property= {JobExecutor.PROPERTY_TOPICS+"=sample/first/job"})
public class SampleExecutor implements JobExecutor{

	Logger log = LoggerFactory.getLogger(SampleExecutor.class);
	@Override
	public JobExecutionResult process(Job arg0, JobExecutionContext arg1) {
		JobExecutionResult result = arg1.result().message("job executed successfully").succeeded();
		String[] progresslog = arg0.getProgressLog();
		String jobid = arg0.getId();
		log.info("jobid in executor is {}",jobid);
		log.info("jobexecutor progresslog is {}",progresslog.toString());
		return result;
	}

}
