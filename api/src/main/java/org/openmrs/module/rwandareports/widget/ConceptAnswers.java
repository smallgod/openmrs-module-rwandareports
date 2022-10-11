package org.openmrs.module.rwandareports.widget;

import org.openmrs.Concept;

import java.util.List;

public class ConceptAnswers {
    public Concept conceptQuestion;
    public List<Concept> conceptAnswers;

    public String value;

    public Concept getConceptQuestion() {
        return conceptQuestion;
    }

    public List<Concept> getConceptAnswers() {
        return conceptAnswers;
    }

    public String getValue() {
        return value;
    }

    public void setConceptQuestion(Concept conceptQuestion) {
        this.conceptQuestion = conceptQuestion;
    }

    public void setConceptAnswers(List<Concept> conceptAnswers) {
        this.conceptAnswers = conceptAnswers;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
