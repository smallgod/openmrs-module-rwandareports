package org.openmrs.module.rwandareports.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.MostRecentObservationInPeriod;
import org.openmrs.module.rwandareports.definition.MostRecentObservationOfSpecificEncountertypes;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Handler(supports={MostRecentObservationOfSpecificEncountertypes.class})
public class MostRecentObservationOfSpecificEncountertypesEvaluator implements RowPerPatientDataEvaluator {

    protected Log log = LogFactory.getLog(this.getClass());

    public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
        context = ObjectUtil.nvl(context, new EvaluationContext());
        if (context == null) {
            context = new EvaluationContext();
        }
        ObservationResult par = new ObservationResult(patientData, context);
        MostRecentObservationOfSpecificEncountertypes pd = (MostRecentObservationOfSpecificEncountertypes)patientData;

        Concept c = pd.getConcept();
        Date startDate = pd.getStartDate();
        Date endDate = pd.getEndDate();


        List<Form> forms = pd.getForms();


        List<Person> personList = new ArrayList<Person>();
        personList.add(pd.getPatient());

        List<Concept> conceptsList = new ArrayList<Concept>();
        conceptsList.add(pd.getConcept());


        EncounterSearchCriteriaBuilder builder = new EncounterSearchCriteriaBuilder();
        builder.setPatient(pd.getPatient()).setEncounterTypes(pd.getEncounterTypes()).setIncludeVoided(false).setToDate(endDate);
        List<Encounter> encounters = Context.getEncounterService().getEncounters(builder.createEncounterSearchCriteria());

//        List<Obs> obs =  Context.getObsService().getObservations( personList,null,conceptsList,null,null,null,null,null,null,startDate,endDate,false);

        List<Obs> obs = Context.getObsService().getObservations(personList,encounters,conceptsList,null,null,null,null,null,null,null,endDate,false);
        Obs ob = null;
        if(obs != null)
        {
            for(Obs o:obs)
            {
                if (ob == null || o.getObsDatetime().compareTo(ob.getObsDatetime()) > 0) {
                    if (pd.isIncludeNull()) {
                        ob = o;
                    } else {
                        String value = o.getValueAsString(Context.getLocale());
                        if (value != null && value.trim().length() > 0) {
                            ob = o;
                        }
                    }
                }


            }
        }

        if(ob != null)
        {

            if(pd.getFilter() != null)
            {
                par.setResultFilter(pd.getFilter());
            }

            par.setDateOfObservation(ob.getObsDatetime());
            par.setObs(ob);
        }
        return par;
    }
}
