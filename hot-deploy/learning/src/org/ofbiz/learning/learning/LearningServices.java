package org.ofbiz.learning.learning;

import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class LearningServices {
	public static final String module = LearningServices.class.getName();

	@SuppressWarnings("rawtypes")
	public static Map learningFirstService(DispatchContext dctx, Map context) {

		Map resultMap = ServiceUtil
				.returnSuccess("You have called on service 'learningFirstService' successfully!");

		return resultMap;

	}

	@SuppressWarnings("rawtypes")
	public static Map handleParameters(DispatchContext dctx, Map context) {

		String firstName = (String) context.get("firstName");

		String lastName = (String) context.get("lastName");

		String planetId = (String) context.get("planetId");

		String message = "firstName: " + firstName + "<br/>";

		message = message + "lastName: " + lastName + "<br/>";

		message = message + "planetId: " + planetId;

		Map<String, Object> resultMap = ServiceUtil.returnSuccess(message);

		resultMap.put("fullName", firstName + " " + lastName);

		return resultMap;

	}

}
