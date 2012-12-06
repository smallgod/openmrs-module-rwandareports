package org.openmrs.module.rwandareports.definition.result;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rowperpatientreports.patientdata.result.BasePatientDataResult;


public class CurrentDrugOrderResults extends BasePatientDataResult {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private List<DrugOrder> value;
	
	private String dateFormat = "yyyy-MM-dd";
	
	private ResultFilter drugFilter = null;

	public CurrentDrugOrderResults(RowPerPatientData patientData, EvaluationContext ec) {
	    super(patientData, ec);
	    setDateFormat(patientData.getDateFormat());
    }

	public Class<?> getColumnClass() {
		return String.class;
	}
	
	public List<DrugOrder> getValue() {
		return value;
	}
	
	public boolean isMultiple() {
		return true;
	}

    
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		if(dateFormat != null)
		{	
			this.dateFormat = dateFormat;
		}
	}
	
	public ResultFilter getDrugFilter() {
		return drugFilter;
	}

	public void setDrugFilter(ResultFilter drugFilter) {
		this.drugFilter = drugFilter;
	}

	/**
     * @param value the value to set
     */
    public void setValue(List<DrugOrder> value) {
    	this.value = value;
    }

    public String getValueAsString() {
	    StringBuilder result = new StringBuilder(" ");
	    
	    for(DrugOrder drO: getValue())
	    {
    		try{
    			if(drugFilter != null)
    			{
    				result.append(drugFilter.filter(drO));
    			}
    			else
    			{
    				result.append(drO.getDrug().getName());
    			}
    			
    			result.append(" ");
    		}
    		catch(Exception e)
    		{
    			log.info("Error retrieving drug info", e);
    		} 	
	    }
	    return result.toString().trim();
    }
}
