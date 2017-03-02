package com.leqee.wms.api;


import java.io.Serializable;
import java.util.List;

public abstract class LeqeeResponse
  implements Serializable
{
  private static final long serialVersionUID = 5014379068811962022L;
  private String result;
  private String note;
 
  private List<LeqeeError> errors ;

	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public List<LeqeeError> getErrors() {
		return errors;
	}
	
	public void setErrors(List<LeqeeError> errors) {
		this.errors = errors;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
  
  
  
}

