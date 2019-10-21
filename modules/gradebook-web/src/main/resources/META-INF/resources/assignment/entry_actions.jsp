<%-- 
	Actions menu for a single assignment.
--%>

<%@ include file="/init.jsp"%>

<c:set var="assignment" value="${SEARCH_CONTAINER_RESULT_ROW.object}" />

<liferay-ui:icon-menu markupView="lexicon">

	<%-- Edit action. --%>


	<portlet:renderURL var="editAssignmentURL">
		<portlet:param name="mvcRenderCommandName"
			value="<%=MVCCommandNames.EDIT_ASSIGNMENT%>" />
		<portlet:param name="redirect" value="${currentURL}" />
		<portlet:param name="assignmentId" value="${assignment.assignmentId}" />
	</portlet:renderURL>

	<liferay-ui:icon message="edit" url="${editAssignmentURL}" />


	<%-- Permissions action. --%>

	<%-- Delete action. --%>

	<portlet:actionURL name="<%=MVCCommandNames.DELETE_ASSIGNMENT%>"
		var="deleteAssignmentURL">
		<portlet:param name="redirect" value="${currentURL}" />
		<portlet:param name="assignmentId" value="${assignment.assignmentId}" />
	</portlet:actionURL>

	<liferay-ui:icon-delete url="${deleteAssignmentURL}" />

</liferay-ui:icon-menu>
