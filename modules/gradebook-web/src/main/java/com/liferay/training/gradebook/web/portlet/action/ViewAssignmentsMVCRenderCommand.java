
package com.liferay.training.gradebook.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.training.gradebook.configuration.GradebookSystemServiceConfiguration;
import com.liferay.training.gradebook.model.Assignment;
import com.liferay.training.gradebook.service.AssignmentService;
import com.liferay.training.gradebook.web.constants.GradebookPortletKeys;
import com.liferay.training.gradebook.web.constants.MVCCommandNames;
import com.liferay.training.gradebook.web.display.context.AssignmentsManagementToolbarDisplayContext;

import java.util.List;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * MVC command for showing the assignments list.
 * 
 * @author liferay
 */
@Component(
		immediate = true, 
		configurationPid = "com.liferay.training.gradebook.configuration.GradebookSystemServiceConfiguration", 
		property = {
				"javax.portlet.name=" + GradebookPortletKeys.GRADEBOOK, 
				"mvc.command.name=/",
				"mvc.command.name=" + MVCCommandNames.VIEW_ASSIGNMENTS 
		}, 
		service = MVCRenderCommand.class)
public class ViewAssignmentsMVCRenderCommand implements MVCRenderCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gradebookSystemServiceConfiguration = ConfigurableUtil
				.createConfigurable(GradebookSystemServiceConfiguration.class, properties);
	}

	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

		// Add assignment list related attributes.

		addAssignmentListAttributes(renderRequest);

		// Add Clay management toolbar related attributes.

		addManagementToolbarAttributes(renderRequest, renderResponse);

		return "/view.jsp";
	}

	/**
	 * Adds assigment list related attributes to the request.
	 * 
	 * @param renderRequest
	 */
	private void addAssignmentListAttributes(RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		// Resolve start and end for the search.

		int currentPage = ParamUtil.getInteger(renderRequest, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_CUR);

		int delta = _gradebookSystemServiceConfiguration.elementPerPage();

		int start = ((currentPage > 0) ? (currentPage - 1) : 0) * delta;
		int end = start + delta;

		// Get sorting options.
		// Notice that this doesn't really sort on title because the field is
		// stored in XML
		// In real world this search would be integrated to the search engine to
		// get localized
		// sort options.

		String orderByCol = ParamUtil.getString(renderRequest, "orderByCol", "title");
		String orderByType = ParamUtil.getString(renderRequest, "orderByType", "asc");

		// Create comparator

		OrderByComparator<Assignment> comp = OrderByComparatorFactoryUtil.create("Assignment", orderByCol,
				!("asc").equals(orderByType));

		// Get keywords.
		// Notice that cleaning keywords is not implemented.

		String keywords = ParamUtil.getString(renderRequest, "keywords");

		// Get the workflow status for the list.

		int status = getAllowedWorkflowStatus(renderRequest);

		// Call the service to get the list of assignments.

		List<Assignment> assigments = _assignmentService.getAssignmentsByKeywords(themeDisplay.getScopeGroupId(),
				keywords, start, end, status, comp);

		// Set request attributes.

		renderRequest.setAttribute("assignments", assigments);
		renderRequest.setAttribute("assignmentCount",
				_assignmentService.getAssignmentsCountByKeywords(themeDisplay.getScopeGroupId(), keywords, status));

	}

	/**
	 * Adds Clay management toolbar context object to the request.
	 * 
	 * @param renderRequest
	 * @param renderResponse
	 */
	private void addManagementToolbarAttributes(RenderRequest renderRequest, RenderResponse renderResponse) {

		LiferayPortletRequest liferayPortletRequest = _portal.getLiferayPortletRequest(renderRequest);

		LiferayPortletResponse liferayPortletResponse = _portal.getLiferayPortletResponse(renderResponse);

		AssignmentsManagementToolbarDisplayContext assignmentsManagementToolbarDisplayContext = new AssignmentsManagementToolbarDisplayContext(
				liferayPortletRequest, liferayPortletResponse, _portal.getHttpServletRequest(renderRequest));

		renderRequest.setAttribute("assignmentsManagementToolbarDisplayContext",
				assignmentsManagementToolbarDisplayContext);

	}

	/**
	 * Returns workflow status current user is allowed to see.
	 * 
	 * This simple example returns ANY status for company admin and APPROVED for
	 * other users.
	 * 
	 * @param renderRequest
	 * @return
	 */
	private int getAllowedWorkflowStatus(RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();

		int status;

		if (permissionChecker.isCompanyAdmin()) {
			status = WorkflowConstants.STATUS_ANY;
		} else {
			status = WorkflowConstants.STATUS_APPROVED;
		}

		return status;
	}

	@Reference
	protected AssignmentService _assignmentService;

	@Reference
	private Portal _portal;

	protected volatile GradebookSystemServiceConfiguration _gradebookSystemServiceConfiguration;
}
