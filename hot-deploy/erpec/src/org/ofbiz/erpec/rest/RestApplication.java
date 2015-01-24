package org.ofbiz.erpec.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RestApplication extends Application{
	private final HashSet<Object> singletons = new HashSet<Object>();
	public RestApplication(){
		singletons.add(new ProductRest());
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		return set;
	}
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
