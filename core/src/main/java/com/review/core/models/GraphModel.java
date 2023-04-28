package com.review.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables=Resource.class)
public class GraphModel {
	
	Logger log = LoggerFactory.getLogger(GraphModel.class);
	
	@Inject
	List<Year> years;
	
	
	public List<Year> getYears() {
		return years;
	}

	@PostConstruct
	public void init() {
		log.info("Inside GraphModel init method ");
	}
	
	@Model(adaptables=Resource.class)
	public static class Year{
		
		@Inject
		String year;

		@Inject
		List<Company> graphitems;
		
		public String getYear() {
			return year;
		}
		
		public List<Company> getGraphitems() {
			return graphitems;
		}
		
	}
	
	@Model(adaptables=Resource.class)
	public static class GraphItems{
		
		
		@Inject
		List<Company> graphitems;

		public List<Company> getGraphitems() {
			return graphitems;
		}
		
		
	}
	
	@Model(adaptables=Resource.class)
	public static class Company{
		
		@Inject
		String companyname;
		
		@Inject
		String percentagePoints;

		public String getCompanyname() {
			return companyname;
		}

		public String getPercentagePoints() {
			return percentagePoints;
		}
		
	}
}
