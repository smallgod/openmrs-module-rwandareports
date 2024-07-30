package org.openmrs.module.rwandareports.dataset.comparator;

import org.openmrs.Obs;

import java.util.Comparator;

public class ObsValueDatetimeComparatorDesc implements Comparator<Obs> {
    public int compare(Obs ob1, Obs ob2) {

        if(ob1 != null && ob2 != null)
        {
            if(ob1.getValueDatetime().after(ob2.getValueDatetime()))
            {
                return -2;
            }
            else if(ob2.getValueDatetime().after(ob1.getValueDatetime()))
            {
                return 2;
            }
            else
            {
                return 0;
            }
        }

        if(ob1 != null && ob2 == null)
        {
            return 1;
        }
        else if(ob1 == null && ob2 != null)
        {
            return -1;
        }
        return 0;
    }
}
