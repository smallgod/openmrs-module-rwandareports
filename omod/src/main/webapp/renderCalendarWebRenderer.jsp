<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/headerFull.jsp" %>

<openmrs:htmlInclude file="/moduleResources/rwandareports/fullcalendar/fullcalendar.js" />
<openmrs:htmlInclude file="/moduleResources/rwandareports/fullcalendar/fullcalendar.css" />
<openmrs:htmlInclude file="/moduleResources/rwandareports/jquery.jqprint-0.3.js" />
<openmrs:htmlInclude file="/moduleResources/rwandareports/calendar.css" />

<script type="text/javascript">

jQuery(document).ready(function() {
	
  jQuery("#printCalendar").hide();	
  
  jQuery('#calendar').html('');
  jQuery('#calendar').fullCalendar({
	  height: 100,
	  events: [
				<c:forEach var="dataSetDefinitionEntry" items="${__openmrs_report_data.definition.dataSetDefinitions}" varStatus="itemStatus">
				<c:set var="dataSetKey" value="${dataSetDefinitionEntry.key}"/>
				<c:set var="dataSet" value="${__openmrs_report_data.dataSets[dataSetKey]}"/>
				<c:forEach items="${dataSet.rowMap}" var="rowEntry" varStatus="rowEntryIndex">
					<c:if test="${rowEntryIndex.index > 0 || itemStatus.index > 0}">,</c:if>	
						{
					    title  : '<c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[1]]}"/> <c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[2]]}"/> <c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[3]]}"/>',				
					    start  : '<c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[4]]}"/>',
					    end :'<c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[4]]}"/>',
					    textColor: '#000000',
					    color: <c:if test="${itemStatus.index == 0}">'#99CCFF'</c:if><c:if test="${itemStatus.index == 1}">'#D076FF'</c:if><c:if test="${itemStatus.index == 2}">'#FFC79A'</c:if><c:if test="${itemStatus.index == 3}">'#BBFFA2'</c:if><c:if test="${itemStatus.index == 4}">'#FFBFEF'</c:if><c:if test="${itemStatus.index == 5}">'#2F72FF'</c:if>,
					    url : '/openmrs/patientDashboard.form?patientId=<c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[0]]}"/>' 
					 }
				</c:forEach>
				</c:forEach>
	       ]
    });
  
  	jQuery('#printCalendarContents').html('');
  	jQuery('#printCalendarContents').fullCalendar({ 
		height: 10,
		header: {
			    left:   'title',
			    center: '',
			    right:  ''
		},
		events: [
			<c:forEach var="dataSetDefinitionEntry" items="${__openmrs_report_data.definition.dataSetDefinitions}" varStatus="itemStatus">
			<c:set var="dataSetKey" value="${dataSetDefinitionEntry.key}"/>
			<c:set var="dataSet" value="${__openmrs_report_data.dataSets[dataSetKey]}"/>

			<c:forEach items="${dataSet.rowMap}" var="rowEntry" varStatus="rowEntryIndex">
				<c:if test="${rowEntryIndex.index > 0 || itemStatus.index > 0}">,</c:if>	
					{
				    title  : '<c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[5]]}"/> <c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[1]]}"/> <c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[2]]}"/> <c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[3]]}"/>',				
				    start  : '<c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[4]]}"/>',
				    end :'<c:out value="${rowEntry.value.columnValues[dataSet.metaData.columns[4]]}"/>',
				    textColor: '#000000',
				    color: <c:if test="${itemStatus.index == 0}">'#99CCFF'</c:if><c:if test="${itemStatus.index == 1}">'#D076FF'</c:if><c:if test="${itemStatus.index == 2}">'#FFC79A'</c:if><c:if test="${itemStatus.index == 3}">'#BBFFA2'</c:if><c:if test="${itemStatus.index == 4}">'#FFBFEF'</c:if><c:if test="${itemStatus.index == 5}">'#2F72FF'</c:if>
				 }
			</c:forEach>
			</c:forEach>
		       ]
	    });
  	jQuery('#printCalendarContents').hide();
  	
  	jQuery('#printButton').click(function(){ 
		jQuery('.openmrs_error').hide();
		jQuery('#printDialog').dialog('open');
	});
	
	jQuery('#printDialog').dialog({
		position: 'middle',
		autoOpen: false,
		modal: true,
		title: 'Print Report',
		width: '80%',
		zIndex: 100,
		buttons: { 'Print': function() { printReportCalendar(); jQuery(this).dialog("close"); },
				   '<spring:message code="general.cancel"/>': function() { jQuery(this).dialog("close"); }
		}
	});
	
	jQuery(".monthPicker").datepicker({
        dateFormat: 'MM yy',
        changeMonth: true,
        changeYear: true,
        showButtonPanel: true,

        onClose: function(dateText, inst) {
            var month = jQuery("#ui-datepicker-div .ui-datepicker-month :selected").val();
            var year = jQuery("#ui-datepicker-div .ui-datepicker-year :selected").val();
            jQuery(this).val(jQuery.datepicker.formatDate('MM yy', new Date(year, month, 1)));
        }
    });

	jQuery(".monthPicker").focus(function () {
		jQuery(".ui-datepicker-calendar").hide();
		jQuery("#ui-datepicker-div").position({
            my: "center top",
            at: "center bottom",
            of: jQuery(this)
        });
    });
}); 

function printReportCalendar() {
	
	jQuery('#printCalendarContents').show();
	
	var startMonth = jQuery("#startMonth").val();
	var endMonth = jQuery("#endMonth").val();
	
	var error = "";
	
	if(startMonth == ""){
		error = error + " pick a start month ";	
	}
	if(endMonth == "") {
		error = error + " pick and end month ";	
	}
	
	if(error != ""){
		jQuery('.openmrs_error').show();
		jQuery('.openmrs_error').html(error);
	}
	else {
		
		var monthsToIncrement = monthDiff(startMonth, endMonth);
		
		var d1Parts = startMonth.split(" ");
		var d1String = d1Parts[0] + " 1, " + d1Parts[1];
	    var date = new Date(d1String);
	
		var index;
		
		for (index = 0; index < monthsToIncrement; index++) {
	
			jQuery(jQuery("#printCalendarContents").fullCalendar('gotoDate',  date)).clone().appendTo("#printCalendar");
		
			date.setMonth(date.getMonth() + 1);
		}
		jQuery("#printCalendar").show();
		jQuery('#printCalendarContents').fullCalendar('render');
		jQuery('#printCalendar').jqprint();
		jQuery("#printCalendar").hide();
		jQuery('#printCalendarContents').hide();
	}
	
	function monthDiff(date1, date2) {
	    
		var d1Parts = date1.split(" ");
		var d1String = d1Parts[0] + " 1, " + d1Parts[1];
	    var d1 = new Date(d1String);
	    
	    var d2Parts = date2.split(" ");
	    var d2String = d2Parts[0] + " 1, " + d2Parts[1];
	    var d2 = new Date(d2String);
	   
	    return ((d2.getFullYear() * 12 + d2.getMonth()) - (d1.getFullYear() * 12 + d1.getMonth())) + 1;
	}
}
</script>


<%--
  This page assumes a ReportData object in the session as the attribute '__openmrs_report_data'
--%>

<div id="header"><h2>${__openmrs_report_data.definition.name}</h2></div><div id="printButton"><input type="button" id="printButton" value='Print Calendar'>
<c:forEach var="dataSetDefinitionEntry" items="${__openmrs_report_data.definition.dataSetDefinitions}" varStatus="itemStatus">
				<c:set var="dataSetKey" value="${dataSetDefinitionEntry.key}"/>
				<span class="legend${itemStatus.index}">&nbsp;&nbsp;&nbsp;</span> <c:out value="${dataSetKey}"/> 
			</c:forEach>
</div>

<div id="page">
	<div id="container">
		<div id="tabs">
				
			<div id="calendarContents">
				<div id="calendar">
				</div>
			</div>
			
			
			<div id="printCalendarContents"></div>
			<div id="printCalendar">
			</div>
			
			<div id="printDialog">
				<div class="box">
					<div id="openmrs_error" class="openmrs_error"></div>
					<label for="month">Start Month: </label><input type="text" id="startMonth" name="startMonth" class="monthPicker" />
					<label for="month">End Month: </label><input type="text" id="endMonth" name="endMonth" class="monthPicker" />
				</div>
			</div>
		</div>
	</div>
</div>



	
									
<%@ include file="/WEB-INF/template/footer.jsp"%>