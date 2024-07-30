package org.openmrs.module.rwandareports.definition;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostRecentObservationOfSpecificEncountertypes extends BasePatientData implements RowPerPatientData {

    private Concept concept;
    @ConfigurationProperty(required=false)
    private List<Concept> answers = new ArrayList<Concept>();

    private ResultFilter filter = null;

    private boolean includeNull = false;
    @ConfigurationProperty(required=false)
    private Date startDate = null;
    @ConfigurationProperty(required=false)
    private Date endDate = null;
    @ConfigurationProperty(required=false)
    private List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
    @ConfigurationProperty(required=false)
    private List<Form> forms = new ArrayList<>();

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public List<Concept> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Concept> answers) {
        this.answers = answers;
    }

    public ResultFilter getFilter() {
        return filter;
    }

    public void setFilter(ResultFilter filter) {
        this.filter = filter;
    }

    public boolean isIncludeNull() {
        return includeNull;
    }

    public void setIncludeNull(boolean includeNull) {
        this.includeNull = includeNull;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<EncounterType> getEncounterTypes() {
        return encounterTypes;
    }

    public void setEncounterTypes(List<EncounterType> encounterTypes) {
        this.encounterTypes = encounterTypes;
    }

    public void addEncounterType(EncounterType encounterType) {
        encounterTypes.add(encounterType);
    }

    public List<Form> getForms() {
        return forms;
    }

    public void setForms(List<Form> forms) {
        this.forms = forms;
    }
}
