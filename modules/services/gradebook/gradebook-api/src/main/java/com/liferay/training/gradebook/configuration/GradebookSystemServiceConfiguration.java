package com.liferay.training.gradebook.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
		category = "Gradebook",
		scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id= "com.liferay.training.gradebook.configuration.GradebookSystemServiceConfiguration",
	localization = "content/Language",
	name = "gradebook-service-configuration-name"
)
public interface GradebookSystemServiceConfiguration {
	
	@Meta.AD(
		deflt = "10",
		description = "element-per-page-description",
		name = "element-per-page-name",
		required = false
	)
	public int elementPerPage();
}
