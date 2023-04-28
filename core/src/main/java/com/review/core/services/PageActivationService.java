package com.review.core.services;

import java.util.List;

public interface PageActivationService {

	void activatePages(List<String> pagepaths);
	
	void deactivatePages(List<String> pagepaths);
}
