package com.review.core.services;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@Component(service=SimpleUsernameService.class,immediate=true)
@Designate(ocd=SimpleUserConfig.class)
public class SimpleUsernameConfig implements SimpleUsernameService {
	String username;
	String password;
	   
	   @Activate
	   protected void activate(SimpleUserConfig config) {
		   this.username= config.getUserName();
		   this.password = config.getUserPassword();
	   }
	
	@Override
	public String getUserName() {
		return username;
	}
	@Override
	public String getPassword() {
		return password;
	}

}
