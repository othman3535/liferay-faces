/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.portal.el;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.model.DataModel;

import com.liferay.faces.bridge.logging.Logger;
import com.liferay.faces.bridge.logging.LoggerFactory;
import com.liferay.faces.portal.context.LiferayFacesContext;


/**
 * @author  Neil Griffin
 */
public class FaceletUtil {

	private static final Logger logger = LoggerFactory.getLogger(FaceletUtil.class);

	/**
	 * Returns the {@link javax.faces.model.DataModel} in the current ViewRoot associated with the specified
	 * forClientId.
	 */
	public static DataModel<?> findDataModel(String forClientId) {

		DataModel<?> dataModel = null;

		if (forClientId != null) {
			LiferayFacesContext liferayFacesContext = LiferayFacesContext.getInstance();
			UIComponent forUIComponent = liferayFacesContext.matchComponentInViewRoot(forClientId);

			if (forUIComponent != null) {

				if (forUIComponent instanceof UIData) {
					UIData uiData = (UIData) forUIComponent;
					Object uiDataValue = uiData.getValue();

					if (uiDataValue != null) {

						if (uiDataValue instanceof DataModel) {
							dataModel = (DataModel<?>) uiDataValue;
						}
						else {
							logger.error("The 'value' attribute for the component [{}] is not a subclass of [{}]",
								uiData.getClass().getName(), DataModel.class.getName());
						}
					}
					else {
						logger.error("The 'value' attribute is not set for the component [{}]",
							uiData.getClass().getName());
					}
				}
				else {
					logger.error(
						"Cannot determine javax.faces.DataModel for=[{0}] since corresponding component in the view=[{1}] is not a subclass of javax.faces.component.UIData",
						forClientId, forUIComponent.getClass().getName());
				}
			}
			else {
				logger.error("Unable to find component in the view with clientId for=[{0}]", forClientId);
			}
		}
		else {
			logger.error("Must specifiy a value for the for parameter");
		}

		return dataModel;
	}
}
