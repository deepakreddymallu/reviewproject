package com.review.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import org.slf4j.Logger;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
@Model(adaptables = { Resource.class },resourceType="review/components/content/quarternarynav",
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name="jackson",selector="nav",extensions="json")
public class QuarternaryNavModel {

	
  Logger log = LoggerFactory.getLogger(QuarternaryNavModel.class);
  @Inject
  private List<PrimaryLink> primaryLinks;
  
  @ScriptVariable
	Page currentPage;
  
  @OSGiService
  ResourceResolverFactory resourceResolverFactory;
  
  public List<PrimaryLink> getPrimaryLinks(){
	  return primaryLinks;
  }

  @PostConstruct
  public void init() {
    if (primaryLinks != null) {
      log.info("primarylinks size is {}",primaryLinks.size());
    }
  }
  @Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class PrimaryLink {
	  
	  Logger log = LoggerFactory.getLogger(PrimaryLink.class);
	  
      @Inject
      private String name;
    
      public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SecondaryLink> getSecondaryLinks() {
		return secondaryLinks;
	}

	@Inject
      private List<SecondaryLink> secondaryLinks;
      
     @PostConstruct
      public void init() {
        if (secondaryLinks != null) {
        	log.info("secondaryLinks size is {}",secondaryLinks.size());
        }
      }
  }

  @Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class SecondaryLink {
      @Inject
      private String name;
      Logger log = LoggerFactory.getLogger(SecondaryLink.class);
      public String getName() {
		return name;
	}

	public List<TertiaryLink> getTertiaryLinks() {
		return tertiaryLinks;
	}

	@Inject
      private List<TertiaryLink> tertiaryLinks;
      
     @PostConstruct
      public void init() {
        if (tertiaryLinks != null) {
        	log.info("tertiaryLinks size is {}",tertiaryLinks.size());
        }
      }
  }

  @Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class TertiaryLink {
	   Logger log = LoggerFactory.getLogger(TertiaryLink.class);
	   
      @Inject
      private String name;
      
      @Inject
      private List<QuarternaryLink> quarternaryLinks;
   
      public List<QuarternaryLink> getQuarternaryLinks() {
		return quarternaryLinks;
	}

	public String getName() {
		return name;
	}

	@PostConstruct
      public void init() {
        if (name != null) {
          log.info("tertiaryname is{} ",name);
        }
      }
  }
  
  @Model(adaptables = Resource.class,
		    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
		  public static class QuarternaryLink {
			   Logger log = LoggerFactory.getLogger(QuarternaryLink.class);
			   
		      @Inject
		      private String contentvalue;
		   
		      public String getContentValue() {
				return contentvalue;
			}

			@PostConstruct
		      public void init() {
		        if (contentvalue != null) {
		          log.info("quarternarynav value is{} ",contentvalue);
		        }
		      }
		  }
}