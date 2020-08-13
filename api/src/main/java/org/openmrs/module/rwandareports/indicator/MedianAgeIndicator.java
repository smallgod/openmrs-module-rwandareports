package org.openmrs.module.rwandareports.indicator;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.BaseIndicator;

public class MedianAgeIndicator extends BaseIndicator {

	private Mapped<? extends CohortDefinition> cohortDefinition;

	public Mapped<? extends CohortDefinition> getCohortDefinition() {
		return cohortDefinition;
	}

	public void setCohortDefinition(
			Mapped<? extends CohortDefinition> cohortDefinition) {
		this.cohortDefinition = cohortDefinition;
	}
}
