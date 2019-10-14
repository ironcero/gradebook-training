<%--
	The main view.
--%>
 
<%@ include file="/init.jsp"%>


<%-- Success messages. --%>

<%-- [PLACEHOLDER FOR ASSIGNMENT ADDED SUCCESS MESSAGE --%>

<%-- [PLACEHOLDER FOR ASSIGNMENT ADDED UPDATED MESSAGE --%>

<liferay-ui:success key="assignment-deleted" message="assignment-deleted-successfully" />

<%-- Application display template renderer. 
     Contents of this tag can be overridden by your custom application display template. --%>




	<div class="container-fluid-1280">
	
		<h1><liferay-ui:message key="assignments" /></h1>
		
		<%-- Clay management toolbar. --%>
		
		<clay:management-toolbar
			clearResultsURL="${assignmentsManagementToolbarDisplayContext.getSearchActionURL()}"
			componentId="assignmentsManagementToolbar"
			creationMenu="${assignmentsManagementToolbarDisplayContext.getCreationMenu()}"
			disabled="${assignmentCount eq 0}"
			filterDropdownItems="${assignmentsManagementToolbarDisplayContext.getFilterDropdownItems()}"
			itemsTotal="${assignmentCount}"
			searchActionURL="${assignmentsManagementToolbarDisplayContext.getSearchActionURL()}"
			searchContainerId="assignmentEntries"
			searchFormName="searchFm"
			selectable="false"			
			showInfoButton="false"
			sortingOrder="${assignmentsManagementToolbarDisplayContext.getOrderByType()}"
			sortingURL="${assignmentsManagementToolbarDisplayContext.getSortingURL()}"
			viewTypeItems="${assignmentsManagementToolbarDisplayContext.getViewTypes()}"
		/>
	
		<%-- Search container. --%>
	
		<liferay-ui:search-container 
			emptyResultsMessage="no-assignments"
			id="assignmentEntries"
			iteratorURL="${portletURL}" 
			total="${assignmentCount}">

			<liferay-ui:search-container-results results="${assignments}" />

			<liferay-ui:search-container-row
				className="com.liferay.training.gradebook.model.Assignment"
				modelVar="entry">

				<%@ include file="/assignment/entry_search_columns.jspf" %>

			</liferay-ui:search-container-row>
			
			<%-- Iterator / Paging --%>

			<liferay-ui:search-iterator 
				displayStyle="${assignmentsManagementToolbarDisplayContext.getDisplayStyle()}"
				markupView="lexicon" 
			/>
		</liferay-ui:search-container>
	</div>
