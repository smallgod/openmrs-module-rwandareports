<%@ include file="/WEB-INF/template/include.jsp"%>

<c:set var="useMinimalHeader" value="true"/>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/moduleResources/rwandareports/jquery.js" />

<h3>Patient Summary!!!!!!  ${patientSummaryManager.name}</h3>

Patient ID:  ${patientId}<br/>
Given Name: ${givenName}</br>
Family Name: ${familyName}</br>
Last Weight: ${lastWeight} (${lastWeightDate})</br>

<%@ include file="/WEB-INF/template/footer.jsp"%>


