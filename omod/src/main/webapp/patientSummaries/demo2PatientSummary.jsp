<%@ include file="/WEB-INF/template/include.jsp"%>

<c:set var="useMinimalHeader" value="true"/>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/moduleResources/rwandareports/jquery.js" />

<h3>Patient Summary!!!!!!  ${patientSummaryManager.name}</h3>

Patient ID:  ${patientId}  (${age} years old<br/>
Name: ${familyName}, ${givenName}</br>

<%@ include file="/WEB-INF/template/footer.jsp"%>


