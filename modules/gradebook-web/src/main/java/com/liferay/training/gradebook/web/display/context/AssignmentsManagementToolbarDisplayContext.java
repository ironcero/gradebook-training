
package com.liferay.training.gradebook.web.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.training.gradebook.web.constants.GradebookPortletKeys;
import com.liferay.training.gradebook.web.constants.MVCCommandNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;

/**
 * Assigments management toolbar display context.
 * 
 * This class passes contextual information to the user interface for the Clay
 * management toolbar.
 * 
 * @author liferay
 */
public class AssignmentsManagementToolbarDisplayContext {

	private static final Log _log = LogFactoryUtil.getLog(AssignmentsManagementToolbarDisplayContext.class);

	public AssignmentsManagementToolbarDisplayContext(LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, HttpServletRequest request) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_request = request;
		_currentURLObj = PortletURLUtil.getCurrent(liferayPortletRequest, _liferayPortletResponse);

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(liferayPortletRequest);
	}

	/**
	 * Returns the creation menu for the toolbar (plus sign on the management
	 * toolbar).
	 * 
	 * @return creation menu
	 */
	public CreationMenu getCreationMenu() {

		ThemeDisplay themeDisplay = (ThemeDisplay) _request.getAttribute(WebKeys.THEME_DISPLAY);

		// Create the menu.

		CreationMenu creationMenu = new CreationMenu();

		creationMenu.addDropdownItem(dropdownItem -> {
			dropdownItem.setHref(_liferayPortletResponse.createRenderURL(), "mvcRenderCommandName",
					MVCCommandNames.EDIT_ASSIGNMENT, "redirect", _currentURLObj.toString());
			dropdownItem.setLabel(LanguageUtil.get(_request, "add-assignment"));
		});

		return creationMenu;
	}

	/**
	 * Returns the assignment list display style.
	 * 
	 * Current selection is stored in portal preferences.
	 * 
	 * @return current display style
	 */
	public String getDisplayStyle() {

		String displayStyle = ParamUtil.getString(_request, "displayStyle");

		if (Validator.isNull(displayStyle)) {
			displayStyle = _portalPreferences.getValue(GradebookPortletKeys.GRADEBOOK, "assignments-display-style",
					"icon");
		} else {
			_portalPreferences.setValue(GradebookPortletKeys.GRADEBOOK, "assignments-display-style", displayStyle);

			_request.setAttribute(WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
		}

		return displayStyle;
	}

	/**
	 * Returns the filter menu options.
	 * 
	 * @return menu options list
	 */
	@SuppressWarnings("serial")
	public List<DropdownItem> getFilterDropdownItems() {

		return new DropdownItemList() {

			{
				addGroup(dropdownGroupItem -> {
					dropdownGroupItem.setDropdownItems(_getOrderByDropdownItems());
					dropdownGroupItem.setLabel(LanguageUtil.get(_request, "order-by"));
				});
			}
		};
	}

	/**
	 * Returns the sort order column.
	 * 
	 * @return sort column
	 */
	public String getOrderByCol() {

		return ParamUtil.getString(_request, "orderByCol", "title");
	}

	/**
	 * Returns the sort type (ascending / descending).
	 * 
	 * @return sort type
	 */
	public String getOrderByType() {

		return ParamUtil.getString(_request, "orderByType", "desc");
	}

	/**
	 * Returns the action URL for the search.
	 * 
	 * @return search action URL
	 */
	public String getSearchActionURL() {

		PortletURL searchURL = _liferayPortletResponse.createRenderURL();

		searchURL.setProperty("mvcRenderCommandName", MVCCommandNames.VIEW_ASSIGNMENTS);

		searchURL.setParameter("orderByCol", getOrderByCol());
		searchURL.setParameter("orderByType", getOrderByType());

		return searchURL.toString();
	}

	/**
	 * Returns sorting URL.
	 * 
	 * @return sorting URL
	 * @throws PortletException
	 */
	public PortletURL getSortingURL() throws PortletException {

		PortletURL sortingURL = _getCurrentSortingURL();

		sortingURL.setParameter("orderByType", Objects.equals(getOrderByType(), "asc") ? "desc" : "asc");

		return sortingURL;
	}

	/**
	 * Returns the view type options (card, list, table).
	 * 
	 * @return list of view types
	 */
	public ViewTypeItemList getViewTypes() {

		// Create portlet URL and set parameters.

		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		portletURL.setParameter("mvcRenderCommandName", MVCCommandNames.VIEW_ASSIGNMENTS);

		int delta = ParamUtil.getInteger(_request, SearchContainer.DEFAULT_DELTA_PARAM);

		if (delta > 0) {
			portletURL.setParameter("delta", String.valueOf(delta));
		}

		String orderByCol = ParamUtil.getString(_request, "orderByCol", "title");
		String orderByType = ParamUtil.getString(_request, "orderByType", "asc");

		portletURL.setParameter("orderBycol", orderByCol);
		portletURL.setParameter("orderByType", orderByType);

		int cur = ParamUtil.getInteger(_request, SearchContainer.DEFAULT_CUR_PARAM);

		if (cur > 0) {
			portletURL.setParameter("cur", String.valueOf(cur));
		}

		return new ViewTypeItemList(portletURL, getDisplayStyle()) {

			{
				addCardViewTypeItem();

				addListViewTypeItem();

				addTableViewTypeItem();
			}

		};
	}

	/**
	 * Returns the current sorting URL.
	 * 
	 * @return current sorting portlet URL
	 * 
	 * @throws PortletException
	 */
	private PortletURL _getCurrentSortingURL() throws PortletException {

		Map<String, String[]> parsedParameterMap = new HashMap<String, String[]>();

		_currentURLObj.getParameterMap().entrySet().stream().forEach(entry -> {
			String key = entry.getKey();
			String[] value = entry.getValue();
			if (value != null && value.length > 0 && value[0] != null && !value[0].isEmpty()
					&& !value[0].equals("null")) {
				parsedParameterMap.put(key, value);
			}
		});

		_currentURLObj.setParameters(parsedParameterMap);

		PortletURL sortingURL = PortletURLUtil.clone(_currentURLObj, _liferayPortletResponse);

		sortingURL.setParameter("mvcRenderCommandName", MVCCommandNames.VIEW_ASSIGNMENTS);

		// Reset current page.

		sortingURL.setParameter(SearchContainer.DEFAULT_CUR_PARAM, "0");

		String keywords = ParamUtil.getString(_request, "keywords");

		if (Validator.isNotNull(keywords)) {
			sortingURL.setParameter("keywords", keywords);
		}

		return sortingURL;
	}

	/**
	 * Return the option items for the sort column menu.
	 * 
	 * @return options list for the sort column menu
	 */
	private List<DropdownItem> _getOrderByDropdownItems() {
		List<DropdownItem> result = new ArrayList<DropdownItem>();
		try {
			DropdownItem title = new DropdownItem();
			title.setActive("title".equals(getOrderByCol()));
			title.setHref(_getCurrentSortingURL(), "orderByCol", "title");
			title.setLabel(LanguageUtil.get(_request, "title"));
			result.add(title);

			DropdownItem createDate = new DropdownItem();
			createDate.setActive("createDate".equals(getOrderByCol()));
			createDate.setHref(_getCurrentSortingURL(), "orderByCol", "createDate");
			createDate.setLabel(LanguageUtil.get(_request, "create-date"));
			result.add(createDate);
		} catch (PortletException portletException) {
			_log.error(portletException);
		}
		return result;
	}

	private final PortletURL _currentURLObj;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PortalPreferences _portalPreferences;
	private final HttpServletRequest _request;
}
