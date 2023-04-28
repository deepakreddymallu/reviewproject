package com.review.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.MapperFeature;

@Model(adaptables = Resource.class,resourceType="review/components/content/composite")
@Exporter(name="jackson",selector="sample",extensions="json",options= {@ExporterOption(name="MapperFeature.SORT_PROPERTIES_ALPHABETICALLY",value="false")})
public class CompositeDetail {

	@Inject
    private Resource subtechnologies;
	
	@Inject
	private ResourceResolver resolver;

	Logger log = LoggerFactory.getLogger(CompositeDetail.class);
    
	public Map<String, List<Map<String, String>>> getSubTechnologies() {
		Map<String, List<Map<String, String>>> finalmap = new HashMap<String, List<Map<String, String>>>();
		log.info("CompositeDetail inside getSubTechnologies... ");
		Iterator<Resource> resourcelist = subtechnologies.listChildren();
		try {
			while (resourcelist.hasNext()) {
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				Resource res = resourcelist.next();
				String maintech = res.getValueMap().get("namesubtech", "");
				Iterator<Resource> iterator = res.getChild("listsubtech").listChildren();
				log.info("Composite Valuemap is {}", res.getChild("listsubtech"));
				while (iterator.hasNext()) {
					ValueMap tech = iterator.next().adaptTo(ValueMap.class);
					String techone = tech.get("techone", "");
					String techtwo = tech.get("subtechtwo", "");
					Map<String, String> map = new HashMap<String, String>();
					map.put("techone", techone);
					map.put("techtwo", techtwo);
					list.add(map);
				}
				finalmap.put(maintech, list);
			}
		} catch (Exception e) {
			log.error("Exception occured in CompositeDetail {}", e);
		}
		return finalmap;
	}

}
