package com.review.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
//import org.apache.sling.models.annotations.Exporter;
import javax.inject.Inject;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import java.util.Optional;

@Model(adaptables = Resource.class,resourceType="review/components/content/reviewdetail")
@Exporter(name="jackson",selector="reviewsel",extensions="json")
public class ReviewDetail {

	@Inject
    private Resource listsubtech;

	
	Logger log = LoggerFactory.getLogger(ReviewDetail.class);
    
    public ArrayList<ValueMap> getListTech(){
    	ArrayList<ValueMap> listTechnology = new ArrayList<ValueMap>();
    	try {
    	Iterator<Resource> iterator =listsubtech.listChildren(); 
    	while(iterator.hasNext() ) {
    		Resource technology = iterator.next();
    		ValueMap valuemap = technology.adaptTo(ValueMap.class);
    		listTechnology.add(valuemap);
    	}
    	}
    	catch(Exception e) {
    		log.error("Exception occured in ReviewDetail Model {}",e);
    	}
    	return listTechnology;
    }

}
