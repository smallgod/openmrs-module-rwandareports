/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandareports;

import ch.vorburger.mariadb4j.DB;
import org.junit.Ignore;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.rwandaprimarycare.validator.IdentifierValidatorRwanda;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Ignore
public class TestDataManager {

	private static DB db = null;
	private Map<String, Integer> patientsCreated = new HashMap<String, Integer>();

	public TestDataManager() {
		super();
		if (db == null) {
			db = DB.newEmbeddedDB(3307);
			db.start();
			db.source("org/openmrs/module/rwandareports/schemaAndMetadata.sql");
		}
	}

	public TestDataManager addPatient(String patientLookup, String firstName, String lastName, String gender, String birthDate, boolean estimated, String primaryCareId) {
		Patient p = new Patient();
		p.setGender(gender);
		p.setBirthdate(getDate(birthDate));
		p.setBirthdateEstimated(estimated);
		PersonName personName = new PersonName(firstName, "", lastName);
		personName.setPreferred(true);
		personName.setPerson(p);
		p.addName(personName);

		IdentifierValidatorRwanda ivr = new IdentifierValidatorRwanda();
		String pcId = ivr.getValidIdentifier(primaryCareId);

		PatientIdentifier pi = new PatientIdentifier();
		pi.setIdentifierType(Context.getPatientService().getPatientIdentifierTypeByName("IMB Primary Care Registration ID"));
		pi.setIdentifier(primaryCareId);
		pi.setLocation(Context.getLocationService().getLocation(1));
		p.addIdentifier(pi);
		p = savePatient(patientLookup, p);
		return this;
	}

	public TestDataManager markDead(String patientLookup, String deathDate, String causeOfDeath) {
		Patient p = getPatient(patientLookup);
		p.setDead(true);
		if (deathDate != null) {
			p.setDeathDate(getDate(deathDate));
		}
		if (causeOfDeath != null) {
			p.setCauseOfDeath(getConcept(causeOfDeath));
		}
		return this;
	}

	public TestDataManager addIdentifier(String patientLookup, String idType, String id) {
		Patient p = getPatient(patientLookup);
		PatientIdentifier pi = new PatientIdentifier();
		pi.setIdentifier(id);
		pi.setIdentifierType(Context.getPatientService().getPatientIdentifierTypeByName(idType));
		pi.setLocation(Context.getLocationService().getLocation(1));
		pi.setPatient(p);
		p.addIdentifier(pi);
		savePatient(patientLookup, p);
		return this;
	}

	public TestDataManager addObs(String patientLookup, String concept, String value, String date) {
		Obs o = new Obs();
		Concept c = getConcept(concept);
		o.setConcept(c);
		o.setObsDatetime(getDate(date));

		Concept codedValue = getConcept(value);
		if (codedValue != null) {
			o.setValueCoded(codedValue);
		}
		else {
			boolean found = false;
			try {
				o.setValueDatetime(getDate(value));
				found = true;
			}
			catch (Exception e) {}
			if (!found) {
				try {
					o.setValueNumeric(Double.parseDouble(value));
					found = true;
				}
				catch (Exception e) {}
			}
			if (!found) {
				o.setValueText(value);
			}
		}
		o.setPerson(getPatient(patientLookup));
		Context.getObsService().saveObs(o, "Adding obs");
		return this;
	}

	public Concept getConcept(String lookup) {
		return Context.getConceptService().getConceptByName(lookup);
	}

	public Date getDate(String lookup) {
		return DateUtil.parseDate(lookup, "yyyy-MM-dd");
	}

	public Integer getPatientId(String patientLookup) {
		return patientsCreated.get(patientLookup);
	}

	public DB getDb() {
		return db;
	}

	protected Patient getPatient(String patientLookup) {
		return Context.getPatientService().getPatient(getPatientId(patientLookup));
	}

	protected Patient savePatient(String patientLookup, Patient p) {
		p = Context.getPatientService().savePatient(p);
		patientsCreated.put(patientLookup, p.getPatientId());
		return p;
	}
}
