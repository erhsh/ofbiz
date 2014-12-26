package org.ofbiz.learning.learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

public class LearningEvents {

	public static final String module = LearningEvents.class.getName();
	public static String postalAddressAdvisory(HttpServletRequest request,
			HttpServletResponse response) {

		String partyId = request.getParameter("partyId");

		Map mechMap = new HashMap();

		ContactMechWorker.getContactMechAndRelated(request, partyId, mechMap);

		Map postalAddress = (Map) mechMap.get("postalAddress");

		if (postalAddress == null)

			return "notMars";

		String planet = (String) postalAddress.get("planet");

		if (planet == null
				|| (!planet.equalsIgnoreCase("Mars") && !planet
						.equalsIgnoreCase("火星")))

			return "notMars";

		return "isMars";

	}

	public static String processFirstForm(HttpServletRequest request,
			HttpServletResponse response) {

		String firstName = request.getParameter("firstName");

		String lastName = request.getParameter("lastName");

		request.setAttribute("combined", firstName + " " + lastName);

		request.setAttribute("allParams", UtilHttp.getParameterMap(request));

		request.setAttribute("submit", "Submitted");

		return "success";

	}

	public static String processMultiForm(HttpServletRequest request,
			HttpServletResponse response) {

		Collection parsed = UtilHttp.parseMultiFormData(UtilHttp
				.getParameterMap(request));

		List combined = new ArrayList();

		Iterator parsedItr = parsed.iterator();

		while (parsedItr.hasNext()) {

			Map record = (Map) parsedItr.next();

			combined.add(record.get("firstName") + " " + record.get("lastName"));

		}

		request.setAttribute("combined", combined);

		request.setAttribute("allParams", UtilHttp.getParameterMap(request));

		request.setAttribute("submit", "Submitted");

		return "success";

	}

	public static String chooseResponse(HttpServletRequest request,
			HttpServletResponse response) {

		String responseName = (String) request.getParameter("responseName");

		if ("goodResponse".equals(responseName)) {

			return "success";

		} else {

			return "error";

		}

	}

	public static String exampleSendEmail(HttpServletRequest request,
			HttpServletResponse response) {

		String mailBody = "This is the body of my email";
		Map<String, String> sendMailContext = new HashMap<String, String>();

		sendMailContext.put("sendTo", "709956581@qq.com");
//		sendMailContext.put("sendCc", "erhsh_165@126.com");
//		sendMailContext.put("sendBcc", "caojian@blackcrystalinfo.com");
		sendMailContext.put("sendFrom", "erhsh_165@126.com");
		sendMailContext.put("subject",
				"Testing emails sent from an OFBiz Event");
		sendMailContext.put("body", mailBody);

		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		String errMsg = null;

		try {
			Map<String, Object> result = dispatcher.runSync("sendMail",
					sendMailContext, 360, true);
			if (ModelService.RESPOND_ERROR.equals((String) result
					.get(ModelService.RESPONSE_MESSAGE))) {
				Map<String, Object> messageMap = UtilMisc.toMap("errorMessage",
						result.get(ModelService.ERROR_MESSAGE));
				errMsg = "Problem sending this email";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (GenericServiceException e) {
			// For Events error messages are passed back
			Debug.logWarning(e, "", module);
			// as Request Attributes
			errMsg = "Problem with the sendMail Service call";
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}

}
