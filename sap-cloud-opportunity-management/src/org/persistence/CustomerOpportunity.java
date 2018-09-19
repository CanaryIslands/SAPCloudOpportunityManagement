package org.persistence;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@NamedQuery(name = "AllCustomerOpportunities", query = "SELECT co FROM CustomerOpportunity co")
public class CustomerOpportunity implements Serializable {

	private static final long serialVersionUID = 1L;

	public CustomerOpportunity() {
	}

	@Id
	private long id;
	private String custID;
	private String oppID;
	private String date;
	private String status;
	private String estimatedValue;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String param) {
		this.custID = param;
	}

	public String getOppID() {
		return oppID;
	}

	public void setOppID(String param) {
		this.oppID = param;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String param) {
		this.date = param;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String param) {
		this.status = param;
	}

	public String getEstimatedValue() {
		return estimatedValue;
	}

	public void setEstimatedValue(String param) {
		this.estimatedValue = param;
	}

}