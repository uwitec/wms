package com.leqee.wms.schedule.job;

import java.io.Serializable;
import java.util.UUID;

public class Key<T> implements Serializable, Comparable<Key<T>>{
	
	private static final long serialVersionUID = -7141167957642391350L;
	public static final String DEFAULT_GROUP = "DEFAULT";
	private final String name;
	private final String group;
	
	public Key(String name, String group){
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null.");
		}
		
		this.name = name;
		if (group != null) {
			this.group = group;
		} else {
			this.group = "DEFAULT";
		}
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getGroup(){
		return this.group;
	}
	
	public String toString(){
		return getGroup() + '.' + getName();
	}
	
	@SuppressWarnings("unused")
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + (this.group == null ? 0 : this.group.hashCode());
		result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj){
		if (this == obj) {
		  return true;
		}
		  
		if (obj == null) {
		  return false;
		}
		  
		if (getClass() != obj.getClass()) {
		  return false;
		}
		
		@SuppressWarnings("rawtypes")
		Key<T> other = (Key)obj;
		if (this.group == null){
		    if (other.group != null) {
		      return false;
		    }
		}else if (!this.group.equals(other.group)) {
		    return false;
		}
		
		if (this.name == null){
		    if (other.name != null) {
		      return false;
		    }
		}else if (!this.name.equals(other.name)) {
		    return false;
		}
		
		return true;
	}
	
	public int compareTo(Key<T> o){
	    if ((this.group.equals("DEFAULT")) && (!o.group.equals("DEFAULT"))) {
	    	return -1;
	    }
	    if ((!this.group.equals("DEFAULT")) && (o.group.equals("DEFAULT"))) {
	    	return 1;
	    }
	    int r = this.group.compareTo(o.getGroup());
	    if (r != 0) {
	    	return r;
	    }
	   
	   return this.name.compareTo(o.getName());
	}
	
	public static String createUniqueName(String group){
		if (group == null) {
			group = "DEFAULT";
		}
		String n1 = UUID.randomUUID().toString();
		String n2 = UUID.nameUUIDFromBytes(group.getBytes()).toString();
	  
		return String.format("%s-%s", new Object[] { n2.substring(24), n1 });
	}
}
