package org.openmrs.module.rwandareports.dataset;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


/**
 *
 */
public class DrugOrderDataSetDefinition extends BaseDataSetDefinition {
	
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	Date asOfDate = null;
	
	@ConfigurationProperty
	List<Concept> indication = null;
	
	@ConfigurationProperty
	List<Concept> drugExclusions = null;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public DrugOrderDataSetDefinition() {
		
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
	
    public List<Concept> getDrugExclusions() {
    	return drugExclusions;
    }

    public void setDrugExclusions(List<Concept> drugExclusions) {
    	this.drugExclusions = drugExclusions;
    }   
}
