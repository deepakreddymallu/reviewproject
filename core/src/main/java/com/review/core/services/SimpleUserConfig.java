package com.review.core.services;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Simple User Config",
description = "A Simple Service Username provider")
public @interface SimpleUserConfig {
	
	 @AttributeDefinition(
             name = "Username",
             description = "To return service user"
     )
     String getUserName() default "reviewserviceuser";
	 
	 @AttributeDefinition(
             name = "Password",
             description = "To return service user password"
     )
     String getUserPassword() default "reviewuserpassword";
	 
	 @AttributeDefinition(name= "Addresses",type= AttributeType.STRING)
	 String[] getAddresses() default {""};

}
