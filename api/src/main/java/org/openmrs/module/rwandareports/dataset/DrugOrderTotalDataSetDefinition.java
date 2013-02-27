package org.openmrs.module.rwandareports.dataset;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


/**
 *
 */
public class DrugOrderTotalDataSetDefinition extends BaseDataSetDefinition {
	
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	Date asOfDate = null;
	
	@ConfigurationProperty
	List<Concept> indication = null;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public DrugOrderTotalDataSetDefinition() {
		
	}

    public Date getAsOfDate() {
    	return asOfDate;
    }
	
    public void setAsOfDate(Date asOfDate) {
    	this.asOfDate = asOfDate;
    }

    public List<Concept> getIndication() {
    	return indication;
    }
    
    public void setIndication(List<Concept> indication) {
    	this.indication = indication;
    }
}
