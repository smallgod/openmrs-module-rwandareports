package org.openmrs.module.rwandareports.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.rwandareports.api.FlattenDatabaseService;
import org.openmrs.module.rwandareports.api.dao.FlattenDatabaseDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arthur D. Mugume date: 01/03/2023
 */
@Transactional
public class FlattenDatabaseServiceImpl extends BaseOpenmrsService implements FlattenDatabaseService {
	
	private FlattenDatabaseDao dao;
	
	public void setDao(FlattenDatabaseDao dao) {
		this.dao = dao;
	}
	
	@Override
	public void flattenDatabase() {
		dao.executeFlatteningScript();
	}
}
