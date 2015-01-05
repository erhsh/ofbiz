package org.ofbiz.erpec.user.service;

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
import org.ofbiz.erpec.pojo.RoleVO;
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
			// 创建权限组
			securityGroup = delegator.makeValue("SecurityGroup");
			securityGroup.setPKFields(context);
			securityGroup.setNonPKFields(context);
			securityGroup = delegator.create(securityGroup);

			// 关联权限
			String permissionIdStr = (String) context.get("permissionIdStr");
			String[] permissionIds = new String[0];
			if (null != permissionIdStr) {
				permissionIds = permissionIdStr.split(",");
			}
			for (String permissionId : permissionIds) {
				GenericValue securityGroupPermission = delegator
						.makeValue("SecurityGroupPermission");
				securityGroupPermission.setPKFields(context);
				securityGroupPermission.set("permissionId", permissionId);
				delegator.create(securityGroupPermission);
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
	 * 更新权限组（角色）
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
			// 更新权限组
			String groupId = (String) context.get("groupId");
			securityGroup = delegator.findOne("SecurityGroup",
					UtilMisc.toMap("groupId", groupId), false);
			securityGroup.setNonPKFields(context);
			securityGroup.store();

			// 更新关联权限
			String permissionIdStr = (String) context.get("permissionIdStr");
			String[] permissionIds = new String[0];
			if (null != permissionIdStr) {
				permissionIds = permissionIdStr.split(",");
			}
			EntityCondition cond = EntityCondition.makeCondition("groupId",
					EntityOperator.EQUALS, groupId);
			List<GenericValue> permissions = delegator.findList(
					"SecurityGroupPermission", cond, null, null, null, false);

			// 找出添加的权限
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

			// 找出删除的权限
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
						"SecurityGroupPermission", UtilMisc.toMap("groupId",
								groupId, "permissionId", delPermissionId),
						false);

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

	public static Map<String, Object> roleLst(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		List<RoleVO> roleVOs = new ArrayList<RoleVO>();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> sgtgvs;
		try {
			String roleGroupId = (String) context.get("roleGrpId");

			EntityCondition cond = null;

			if (null != roleGroupId && !roleGroupId.isEmpty()) {
				cond = EntityCondition.makeCondition("securityGroupTypeId",
						EntityOperator.EQUALS, roleGroupId);
			}

			sgtgvs = delegator.findList("SecurityGroupTypeGroupView", cond,
					null, null, null, false);

			for (GenericValue sgtgv : sgtgvs) {
				RoleVO roleVO = new RoleVO();
				roleVO.setRoleId(sgtgv.getString("groupId"));
				roleVO.setRoleName(sgtgv.getString("groupId"));
				roleVO.setRoleDesc(sgtgv.getString("grpDesc"));
				roleVO.setRoleGrp(sgtgv.getString("typeDesc"));
				roleVO.setRoleStat("--");

				roleVOs.add(roleVO);
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			e.printStackTrace();

			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"PartyCannotCreateRoleTypeEntity",
					UtilMisc.toMap("errMessage", e.getMessage()), locale));
		}
		result.put("rows", roleVOs);
		result.put("results", 40);
		return result;

	}

	public static Map<String, Object> roleAdd(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();

		try {

			String roleId = (String) context.get("roleId");
			// String roleName = (String) context.get("roleName");
			String roleDesc = (String) context.get("roleDesc");
			String roleGrp = (String) context.get("roleGrp");

			Object permissionIdsObj = context.get("permissionIds");

			List<?> permissionIds = null;
			if (permissionIdsObj instanceof List<?>) {
				permissionIds = (List<?>) permissionIdsObj;
			}

			// 创建权限组
			GenericValue securityGroup = delegator.makeValue("SecurityGroup");
			securityGroup.set("groupId", roleId);
			securityGroup.set("description", roleDesc);
			securityGroup = delegator.create(securityGroup);

			// 关联权限组分类
			GenericValue securityGroupRelationship = delegator
					.makeValue("SecurityGroupRelationship");
			securityGroupRelationship.set("securityGroupTypeId", roleGrp);
			securityGroupRelationship.set("groupId", roleId);
			delegator.create(securityGroupRelationship);

			// 关联权限
			if (null != permissionIds) {
				for (Object permissionId : permissionIds) {
					GenericValue securityGroupPermission = delegator
							.makeValue("SecurityGroupPermission");
					securityGroupPermission.set("groupId", roleId);
					securityGroupPermission.set("permissionId", permissionId);
					delegator.create(securityGroupPermission);
				}
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			e.printStackTrace();

			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"PartyCannotCreateRoleTypeEntity",
					UtilMisc.toMap("errMessage", e.getMessage()), locale));
		}

		result = ServiceUtil.returnSuccess();
		return result;

	}

	public static Map<String, Object> roleEdt(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();
		EntityCondition cond = null;
		try {

			// = 入参提取
			String roleId = (String) context.get("roleId"); // 角色Id
			// String roleName = (String) context.get("roleName"); // 角色名称
			String roleDesc = (String) context.get("roleDesc"); // 角色描述
			String roleGrp = (String) context.get("roleGrp"); // 角色分类（分类ID）
			List<?> permissionIds = (List<?>) context.get("permissionIds"); // 关联权限列表

			// = 更新角色
			GenericValue securityGroup = delegator.findOne("SecurityGroup",
					UtilMisc.toMap("groupId", roleId), false);
			securityGroup.set("groupId", roleId);
			securityGroup.set("description", roleDesc);
			securityGroup.store();

			// = 更新角色分组关联关系
			cond = EntityCondition.makeCondition("groupId",
					EntityOperator.EQUALS, roleId);
			List<GenericValue> securityGroupRelationships = delegator.findList(
					"SecurityGroupRelationship", cond, null, null, null, false);
			GenericValue securityGroupRelationship = null;
			for (GenericValue sgrs : securityGroupRelationships) {
				securityGroupRelationship = sgrs;
			}

			if (null == securityGroupRelationship) {
				// 未分组
				securityGroupRelationship = delegator
						.makeValue("SecurityGroupRelationship");
				securityGroupRelationship.set("securityGroupTypeId", roleGrp);
				securityGroupRelationship.set("groupId", roleId);
				delegator.create(securityGroupRelationship);
			} else if (!roleGrp.equals(securityGroupRelationship
					.getString("securityGroupTypeId"))) {
				// 已分组，但组不同
				securityGroupRelationship.remove();

				securityGroupRelationship = delegator
						.makeValue("SecurityGroupRelationship");
				securityGroupRelationship.set("securityGroupTypeId", roleGrp);
				securityGroupRelationship.set("groupId", roleId);
				delegator.create(securityGroupRelationship);
			}

			String securityGroupTypeId = securityGroupRelationship
					.getString("securityGroupTypeId");

			// 一个角色只能属于一个角色分类，一个角色分类可以有多个角色
			if (!securityGroupTypeId.equals(roleGrp)) {
				securityGroupRelationship.remove();
				securityGroupRelationship = delegator
						.makeValue("SecurityGroupRelationship");
				securityGroupRelationship.set("securityGroupTypeId", roleGrp);
				securityGroupRelationship.set("groupId", roleId);
				delegator.create(securityGroupRelationship);
			}

			// = 更新关联权限
			cond = EntityCondition.makeCondition("groupId",
					EntityOperator.EQUALS, roleId);
			List<GenericValue> permissions = delegator.findList(
					"SecurityGroupPermission", cond, null, null, null, false);

			// == 找出添加的权限
			List<String> addPermissionIds = new ArrayList<String>();
			boolean isAdd = true;
			for (Object permissionId : permissionIds) {
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
					addPermissionIds.add((String) permissionId);
				}
			}

			// == 找出删除的权限
			List<String> delPermissionIds = new ArrayList<String>();
			boolean isDel;
			for (GenericValue permission : permissions) {
				isDel = true;
				String oldPermissionId = (String) permission
						.get("permissionId");
				for (Object permissionId : permissionIds) {
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

			// == 添加角色权限关联关系
			for (String addPermissionId : addPermissionIds) {
				GenericValue securityGroupPermission = delegator
						.makeValue("SecurityGroupPermission");
				securityGroupPermission.set("groupId", roleId);
				securityGroupPermission.set("permissionId", addPermissionId);
				securityGroupPermission.create();
			}

			// == 删除角色权限关联关系
			for (String delPermissionId : delPermissionIds) {
				GenericValue securityGroupPermission = delegator
						.findOne("SecurityGroupPermission", UtilMisc.toMap(
								"groupId", roleId, "permissionId",
								delPermissionId), false);
				securityGroupPermission.remove();
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			e.printStackTrace();

			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"PartyCannotCreateRoleTypeEntity",
					UtilMisc.toMap("errMessage", e.getMessage()), locale));
		}

		result = ServiceUtil.returnSuccess();
		return result;

	}
}
