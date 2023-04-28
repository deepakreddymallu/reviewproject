package com.review.core.services;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service=JobConsumer.class,name="SampleJobConsumer",property= {JobConsumer.PROPERTY_TOPICS+"=sample/first/job"})
public class SampleConsumer implements JobConsumer{

	Logger log = LoggerFactory.getLogger(SampleConsumer.class);
	@Override
	public JobResult process(Job arg0) {
		log.info("progress step count is {}",arg0.getProgressStepCount());
		String resultMessage = arg0.getResultMessage();
		String id = arg0.getId();
		int progressStepCount = arg0.getProgressStepCount();
		log.info("consumer progresstepcount is {}",progressStepCount);
		log.info("consumer Id is {}",id);
		log.info("consumer resultMessage is {}",resultMessage);
		log.info("consumer arg0 is job {}",arg0);
		log.info("consumer jobstate is {}",arg0.getJobState());
		return JobResult.OK;
	}

}
