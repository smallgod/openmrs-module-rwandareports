package org.openmrs.module.rwandareports.indicator.evaluator;

import java.util.ArrayList;
import java.util.Collection;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.calculation.ReportingCalculationUtil;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.PersonData;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.SimpleIndicatorResult;
import org.openmrs.module.reporting.indicator.aggregation.MedianAggregator;
import org.openmrs.module.reporting.indicator.evaluator.IndicatorEvaluator;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.module.rwandareports.indicator.EncounterIndicator;
import org.openmrs.module.rwandareports.indicator.EncounterIndicatorResult;
import org.openmrs.module.rwandareports.indicator.MedianAgeIndicator;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports={ MedianAgeIndicator.class})
public class MedianAgeIndicatorEvaluator implements IndicatorEvaluator {

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Autowired
	PersonDataService personDataService;
	
	public IndicatorResult evaluate(Indicator indicator, EvaluationContext context) throws EvaluationException {
		
		MedianAgeIndicator mai = (MedianAgeIndicator) indicator;

    	Cohort patients = cohortDefinitionService.evaluate(mai.getCohortDefinition(), context);
		AgeDataDefinition add = new AgeDataDefinition();

		EvaluationContext ageContext = new EvaluationContext();
		ageContext.setBaseCohort(patients);
		PersonData ageData = personDataService.evaluate(add, ageContext);
		Collection<Number> ages = new ArrayList<Number>();
		for (Integer pId : ageData.getData().keySet()) {
			Age patientAge = (Age) ageData.getData().get(pId);
			ages.add(patientAge.getFullYears());
		}
		MedianAggregator medianAggregator = new MedianAggregator();
		Number medianAge = medianAggregator.compute(ages);

		SimpleIndicatorResult result = new SimpleIndicatorResult();
		result.setContext(context);
		result.setIndicator(mai);
		result.setNumeratorResult(medianAge);
		
		return result;
	}
}
