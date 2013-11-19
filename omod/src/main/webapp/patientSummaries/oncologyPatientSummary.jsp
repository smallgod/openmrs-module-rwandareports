<%@ include file="/WEB-INF/template/include.jsp"%>

 <c:set var="useMinimalHeader" value="true" />
<%--<%@ include file="/WEB-INF/template/header.jsp"%>
 --%>
<openmrs:htmlInclude file="/moduleResources/rwandareports/jquery.js" />
<openmrs:htmlInclude
	file="/moduleResources/rwandareports/oncologyPatientSummarytabs.css" />

<script>
	$(document).ready(function() {
		$("#divtab1").show();
		$("#divtab2").hide();
		$("#divtab3").hide();
		$("#divtab4").hide();
		$("#divtab5").hide();
		$("#divtab6").hide();

		$("#htab1").click(function() {
			$("#divtab1").show();
			$("#divtab2").hide();
			$("#divtab3").hide();
			$("#divtab4").hide();
			$("#divtab5").hide();
			$("#divtab6").hide();
		});

		$("#htab2").click(function() {
			$("#divtab2").show();
			$("#divtab1").hide();
			$("#divtab3").hide();
			$("#divtab4").hide();
			$("#divtab5").hide();
			$("#divtab6").hide();
		});

		$("#htab3").click(function() {
			$("#divtab3").show();
			$("#divtab1").hide();
			$("#divtab2").hide();
			$("#divtab4").hide();
			$("#divtab5").hide();
			$("#divtab6").hide();
		});
		$("#htab4").click(function() {
			$("#divtab4").show();
			$("#divtab1").hide();
			$("#divtab2").hide();
			$("#divtab3").hide();
			$("#divtab5").hide();
			$("#divtab6").hide();
		});

		$("#htab5").click(function() {
			$("#divtab5").show();
			$("#divtab1").hide();
			$("#divtab2").hide();
			$("#divtab3").hide();
			$("#divtab4").hide();
			$("#divtab6").hide();
		});

		$("#htab6").click(function() {
			$("#divtab6").show();
			$("#divtab1").hide();
			$("#divtab2").hide();
			$("#divtab3").hide();
			$("#divtab4").hide();
			$("#divtab5").hide();
		});

	});
</script>

<style>
table#summaryHearder{
width: 100%;
border-collapse: collapse;
border: 1px solid;
margin-left: 5px;
margin-right: 5px;
background-color: #fff;
}
table#summaryHearder td{
width: 50%;
}
</style>



<table id="summaryHearder">
<tr>
<td>
${givenName} ${familyName}</br>
</br>
</br>
Allergies:</br>
Last Height: ${lastHeight}cm ${lastHeightDate} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;last Weight: ${lastWeight}kg ${lastWeightDate}
</td>

<td>
<div align="right">
ID: ${imbId}</br></br>
Health Center:</br>
Active Programs: ${activePrograms} </br></br>
Last visit: ${lastEncounterDate} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Type: ${lastEncounterType}
</div>
</td>
</tr>

</table>

<article class="tabs"> <section id="tab1">
<h2 id="htab1">
	<a href="#tab1">Program Summary</a>
</h2>
<div id="divtab1">
	<p>Tab 1.</p>	
	
</div>
</section> <section id="tab2">
<h2 id="htab2">
	<a href="#tab2">Overview</a>
</h2>
<div id="divtab2">
	<p>Tab 2.</p>

</div>
</section> <section id="tab3">
<h2 id="htab3">
	<a href="#tab3">Medications</a>
</h2>
<div id="divtab3">
	<p>Tab 3.</p>

</div>
</section> <section id="tab4">
<h2 id="htab4">
	<a href="#tab4">Labs/Imagery</a>
</h2>
<div id="divtab4">
	<p>Tab 4.</p>

</div>
</section> </section> <section id="tab5">
<h2 id="htab5">
	<a href="#tab5">Visits</a>
</h2>
<div id="divtab5">
	<p>Tab 5.</p>

</div>
</section> <section id="tab6">
<h2 id="htab6">
	<a href="#tab6">H/W Tracker</a>
</h2>
<div id="divtab6">
	<p>Tab 6.</p>

</div>
</section> </article>
















<%-- <h3>Patient Summary!!!!!!  ${patientSummaryManager.name}</h3>

Patient ID:  ${patientId}<br/>
Given Name: ${givenName}</br>
Family Name: ${familyName}</br>
Last Weight: ${lastWeight} (${lastWeightDate})</br> --%>

<%@ include file="/WEB-INF/template/footer.jsp"%>