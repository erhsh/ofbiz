package org.ofbiz.erpec.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class UserServices {

	public static final String module = UserServices.class.getName();
	public static final String resource = "PartyUiLabels";
	public static final String resourceError = "PartyErrorUiLabels";

	/**
	 * 创建权限组（角色）
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createSecurityGroup(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue securityGroup = null;
		try {
			securityGroup = delegator.makeValue("SecurityGroup");
			securityGroup.setPKFields(context);
			securityGroup.setNonPKFields(context);
			securityGroup = delegator.create(securityGroup);

			// 关联权限
			String permissionIdStr = (String) context.get("permissionIdStr");
			String[] permissionIds = permissionIdStr.split(",");

			for (String permissionId : permissionIds) {
				GenericValue securityGroupPermission = delegator
						.makeValue("SecurityGroupPermission");
				securityGroupPermission.set("permissionId", permissionId);
				securityGroupPermission.create();
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"PartyCannotCreateRoleTypeEntity",
					UtilMisc.toMap("errMessage", e.getMessage()), locale));
		}
		if (securityGroup != null) {
			result.put("securityGroup", securityGroup);
		}
		return result;
	}

	/**
	 * 创建权限组（角色）
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateSecurityGroup(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue securityGroup = null;
		try {
			String groupId = (String) context.get("groupId");
			securityGroup = delegator.findOne("SecurityGroup",
					UtilMisc.toMap("groupId", groupId), false);
			securityGroup.setNonPKFields(context);
			securityGroup.store();

			EntityCondition cond = EntityCondition.makeCondition("groupId",
					EntityOperator.EQUALS, groupId);
			List<GenericValue> permissions = delegator.findList(
					"SecurityGroupPermission", cond, null, null, null, false);

			// 关联权限
			String permissionIdStr = (String) context.get("permissionIdStr");
			String[] permissionIds = permissionIdStr.split(",");

			// 分析出添加的权限
			List<String> addPermissionIds = new ArrayList<String>();
			boolean isAdd = true;
			for (String permissionId : permissionIds) {
				isAdd = true;
				for (GenericValue permission : permissions) {
					String oldPermissionId = (String) permission
							.get("permissionId");
					// 不用添加的权限
					if (oldPermissionId.equals(permissionId)) {
						isAdd = false;
						break;
					}
				}

				if (isAdd) {
					addPermissionIds.add(permissionId);
				}
			}

			// 分析出删除的权限
			List<String> delPermissionIds = new ArrayList<String>();
			boolean isDel;
			for (GenericValue permission : permissions) {
				isDel = true;
				String oldPermissionId = (String) permission
						.get("permissionId");
				for (String permissionId : permissionIds) {
					// 不用刪除的权限
					if (permissionId.equals(oldPermissionId)) {
						isDel = false;
						break;
					}
				}
				if (isDel) {
					delPermissionIds.add(oldPermissionId);
				}

			}

			for (String addPermissionId : addPermissionIds) {
				GenericValue securityGroupPermission = delegator
						.makeValue("SecurityGroupPermission");
				securityGroupPermission.set("groupId", groupId);
				securityGroupPermission.set("permissionId", addPermissionId);
				securityGroupPermission.create();
			}

			for (String delPermissionId : delPermissionIds) {

				GenericValue securityGroupPermission = delegator.findOne(
						"SecurityGroupPermission",
						UtilMisc.toMap("permissionId", delPermissionId), false);

				securityGroupPermission.remove();
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"PartyCannotCreateRoleTypeEntity",
					UtilMisc.toMap("errMessage", e.getMessage()), locale));
		}
		if (securityGroup != null) {
			result.put("securityGroup", securityGroup);
		}
		return result;
	}
}
