package org.openmrs.module.rwandareports.filter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class ObservationValueNumericAfterSixmonthsFilter implements ResultFilter {
	protected Log log = LogFactory.getLog(this.getClass());
	private ArrayList<Form> forms=new ArrayList<Form>();
	private String obsValueNumeric=null;	
	
public ObservationValueNumericAfterSixmonthsFilter(ArrayList<Form> forms) {
		super();
		this.forms = forms;
	}
public Object filter(Object value) {
	int obsId = Integer.parseInt((String)value);
	
	Obs ob=Context.getObsService().getObs(obsId);
	
	List<Obs> obs = new ArrayList<Obs>();
		
	List<Obs> observations=Context.getObsService().getObservationsByPersonAndConcept(ob.getPerson(), ob.getConcept());
	
	for (Obs obs2 : observations) {
		if(!forms.isEmpty()){
			for (Form form : forms) {
				if(obs2.getEncounter().getForm().getFormId()==form.getFormId()){
					obs.add(obs2);
				}
			}
		}
	}
	
	
	
	
	Obs obNeeded=null;
	
	Calendar cal1 = Calendar.getInstance();
	Calendar cal2 = Calendar.getInstance();
	Obs tempOb=null;
	for (int j = 0; j < obs.size(); j++) {	
	for (int i = 0; i < obs.size(); i++) {
		if(i+1 < obs.size() && obs.get(i).getObsDatetime().compareTo(obs.get(i+1).getObsDatetime())>0){
			tempOb=obs.get(i);
			obs.set(i, obs.get(i+1));
			obs.set(i+1, tempOb);			
		}
	}
	}
	if(!obs.isEmpty()){
	cal1.setTime(obs.get(0).getObsDatetime());
	}
	if(obs != null && obs.size()>=2)
    {
		
		for(Obs o:obs)
		{
			cal2.setTime(o.getObsDatetime());
			
			if(cal2.getTimeInMillis()-cal1.getTimeInMillis() >= 15552000000L ) {   //15552000000L milliseconds is equivalent to 6 months
				obNeeded=o;
				break;
			}				
		}
    }
	
	if(obNeeded!=null && obNeeded.getValueNumeric()!= null){
	String[] splitedValue=obNeeded.getValueNumeric().toString().split(".0");
	obsValueNumeric=splitedValue[0];	
	return obsValueNumeric;	
	}
		return null;
	}
public Object filterWhenNull() {
	// TODO Auto-generated method stub
	return null;
}

}
