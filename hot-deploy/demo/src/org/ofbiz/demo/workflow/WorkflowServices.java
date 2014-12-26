package org.ofbiz.demo.workflow;

import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class WorkflowServices {
	public static Map<String, Object> setOrderStatus(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		successResult.put("oldStatusId", "oldStatusId");
		
		return successResult;
	}
}
