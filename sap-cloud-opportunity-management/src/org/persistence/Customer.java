package org.persistence;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@NamedQuery(name = "AllCustomers", query = "SELECT c FROM Customer c")
public class Customer implements Serializable {

	private static final long serialVersionUID = 1L;

	public Customer() {
	}

	@Id
	private long id;
	private String custID;
	private String name;
	private String address;
	private String city;

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

	public String getName() {
		return name;
	}

	public void setName(String param) {
		this.name = param;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String param) {
		this.address = param;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String param) {
		this.city = param;
	}

}