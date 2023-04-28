package com.review.core.services;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "SampleSchedulerConfig",
description = "Sample Scheduler Config")
public @interface SampleSchedulerConfig {
	
	 @AttributeDefinition(
             name = "CronExpression",
             description = "To return cron expression"
     )
     String getCronExpression() default "0 0 0/1 1/1 * ? *";
	 
	 @AttributeDefinition(
				name = "Scheduler name", 
				description = "Name of the scheduler", 
				type = AttributeType.STRING)
		public String schedulerName() default "Sample Sling Scheduler Configuration";
	 
}
