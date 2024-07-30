package org.openmrs.module.rwandareports.filter;

import org.openmrs.Obs;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rwandareports.dataset.comparator.ObsComparator;
import org.openmrs.module.rwandareports.dataset.comparator.ObsComparatorDesc;
import org.openmrs.module.rwandareports.dataset.comparator.ObsValueDatetimeComparatorDesc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HighestValueDateTimeUptoDate implements ResultFilter {
//    private Date endDate;
//    public HighestValueDateTimeUptoDate(Date endDate){
//        super();
//        this.endDate = endDate;
//    }

    public Object filter(Object value) {

        List<Obs> allObs = (List<Obs>)value;

        if(allObs != null)
        {
            Collections.sort(allObs, new ObsValueDatetimeComparatorDesc());
        }

        if(allObs.size() >= 1) {
//            List<Obs> lastObs = new ArrayList<Obs>();
            List<Obs> beforeEndDateObs = new ArrayList<Obs>();
            List<Obs> finalValue = new ArrayList<Obs>();

            for (int i = 0; i < allObs.size(); i++)
            {
                if((new Date()).after(allObs.get(i).getValueDatetime())){
                    beforeEndDateObs.add(allObs.get(i));
                    Collections.sort(beforeEndDateObs, new ObsValueDatetimeComparatorDesc());
                }
            }

//            for(int i = 0; i < 1; i++)
//            {
//                beforeEndDateObs.add(allObs.get(i));
//                Collections.sort(lastObs, new ObsValueDatetimeComparatorDesc());
//            }
            if(beforeEndDateObs.size()>= 1){
                finalValue.add(beforeEndDateObs.get(0));
                return finalValue;
            }
            return allObs;
        }

        return allObs;
    }

    public Object filterWhenNull() {
        // TODO Auto-generated method stub
        return null;
    }
}
