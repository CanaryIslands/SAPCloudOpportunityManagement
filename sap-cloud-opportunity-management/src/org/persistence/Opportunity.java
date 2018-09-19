package org.persistence;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@NamedQuery(name = "AllOpportunitues", query = "SELECT o FROM Opportunity o")
public class Opportunity implements Serializable {

	private static final long serialVersionUID = 1L;

	public Opportunity() {
	}

	@Id
	private long id;
	private String oppText;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOppText() {
		return oppText;
	}

	public void setOppText(String param) {
		this.oppText = param;
	}

}