package org.openmrs.module.rwandareports.web.widget.handler;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptSearchResult;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.CodedHandler;
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.module.rwandareports.widget.ConceptAnswers;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Handler(supports={ConceptAnswers.class},order = 1)
public class ConceptAnswersHandler extends CodedHandler {


    Locale locale = Context.getLocale();
    @Override
    public void populateOptions(WidgetConfig config, CodedWidget widget){

//        widget.addOption(new Option("All Concepts",config.getAttributeValue("label"),config.getAttributeValue("label"),config.getAttributeValue("label")),config);
        ConceptAnswers conceptAnswers = new ConceptAnswers();
        String ConceptQuestionName = config.getAttributeValue("conceptQuestion");


        Collection<ConceptAnswer> conceptAnss = Context.getConceptService().getConceptByName(ConceptQuestionName).getAnswers(false);

//                findConceptAnswers(null,locale,     conceptAnswers.conceptQuestion);
            for(ConceptAnswer csr: conceptAnss){

                widget.addOption(new Option(csr.getAnswerConcept().getPreferredName(locale).toString(),csr.getAnswerConcept().getPreferredName(locale).toString(),csr.getAnswerConcept().getPreferredName(locale).toString(),csr.getAnswerConcept().getPreferredName(locale).toString()),config);
            }

    }
    public Object parse(String input, Class<?> type) {
        if (StringUtils.isNotBlank(input)) {
            ConceptAnswers conceptAns = new ConceptAnswers();
            conceptAns.setValue(input);
            return conceptAns;
        }
        return null;
    }
}
