package org.ofbiz.erpec.user.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.erpec.pojo.RoleVO;
import org.ofbiz.erpec.pojo.UserInfoVO;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class UserServices {
	public static final String module = UserServices.class.getName();
	public static final String resource = "PartyUiLabels";
	public static final String resourceError = "PartyErrorUiLabels";

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
				roleVO.setRoleId(sgtgv.getString("groupId")); // 角色编号
				roleVO.setRoleName(sgtgv.getString("groupName")); // 角色名称
				roleVO.setRoleDesc(sgtgv.getString("grpDesc")); // 角色描述
				roleVO.setRoleGrp(sgtgv.getString("typeDesc")); // 角色类型
				roleVO.setRoleStat(sgtgv.getString("staDesc")); // 角色状态

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
		result.put("results", roleVOs.size());
		return result;

	}

	public static Map<String, Object> roleAdd(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();

		try {

			String roleId = (String) context.get("roleId");
			String roleName = (String) context.get("roleName");
			String roleDesc = (String) context.get("roleDesc");
			String roleGrp = (String) context.get("roleGrp");

			Object permissionIdsObj = context.get("permissionIds");

			List<?> permissionIds = null;
			if (permissionIdsObj instanceof List<?>) {
				permissionIds = (List<?>) permissionIdsObj;
			}

			if (UtilValidate.isEmpty(roleId)) {
				roleId = delegator.getNextSeqId("SecurityGroup");
			}
			
			// 创建权限组
			GenericValue securityGroup = delegator.makeValue("SecurityGroup");
			securityGroup.set("groupId", roleId);
			securityGroup.set("groupName", roleName);
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
			String roleName = (String) context.get("roleName"); // 角色名称
			String roleDesc = (String) context.get("roleDesc"); // 角色描述
			String roleGrp = (String) context.get("roleGrp"); // 角色分类（分类ID）
			List<?> permissionIds = (List<?>) context.get("permissionIds"); // 关联权限列表

			// = 更新角色
			GenericValue securityGroup = delegator.findOne("SecurityGroup",
					UtilMisc.toMap("groupId", roleId), false);
			securityGroup.set("groupId", roleId);
			securityGroup.set("groupName", roleName);
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
			boolean isAdd;
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

	public static Map<String, Object> userLst(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		List<UserInfoVO> userInfoVOs = new ArrayList<UserInfoVO>();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> userLogins;
		try {

			userLogins = delegator
					.findList("UserLogin", null,
							UtilMisc.toSet("partyId", "userLoginId"), null,
							null, false);

			for (GenericValue userLogin : userLogins) {
				String partyId = userLogin.getString("partyId");
				if (null == partyId || "".equals(partyId)) {
					continue;
				}

				// 登录账号
				UserInfoVO userInfoVO = new UserInfoVO();
				userInfoVO.setUserId(partyId);
				userInfoVO.setUserLoginId(userLogin.getString("userLoginId"));

				// 用户ID， 描述
				GenericValue party = userLogin.getRelatedOne("Party", false);
				if (null != party) {
					userInfoVO.setUserId(party.getString("partyId"));
					userInfoVO.setUserDesc(party.getString("description"));
				}

				// 用户状态
				GenericValue statusItem = party.getRelatedOne("StatusItem",
						false);
				if (null != statusItem) {
					userInfoVO.setUserStat(statusItem.getString("description"));
				}

				// 用户名，身份证号
				GenericValue person = party.getRelatedOne("Person", false);
				if (null != person) {
					userInfoVO.setUserName(person.getString("nickname"));
					userInfoVO.setUserCardId(person.getString("cardId"));
				}

				// 联系方式
				userInfoVO.setUserTelNum(getTeleNum(delegator, partyId));

				// 角色
				List<GenericValue> ulsgs = userLogin.getRelated(
						"UserLoginSecurityGroup", null, null, false);

				List<RoleVO> userRoleVOs = new ArrayList<RoleVO>();
				List<String> userRoles = new ArrayList<String>();
				ulsgs = EntityUtil.filterByDate(ulsgs);
				for (GenericValue ulsg : ulsgs) {
					RoleVO roleVO = new RoleVO();
					roleVO.setRoleId(ulsg.getString("groupId"));
					userRoleVOs.add(roleVO);
					userRoles.add(ulsg.getString("groupId"));
				}

				userInfoVO.setUserRoleVOs(userRoleVOs);
				userInfoVO.setUserRoles(userRoles);
				userInfoVOs.add(userInfoVO);
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			e.printStackTrace();

			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"PartyCannotCreateRoleTypeEntity",
					UtilMisc.toMap("errMessage", e.getMessage()), locale));
		}
		result.put("rows", userInfoVOs);
		result.put("results", userInfoVOs.size());
		return result;

	}

	private static String getTeleNum(Delegator delegator, String partyId) {
		String result = "";
		List<Map<String, Object>> contacts = ContactMechWorker
				.getPartyContactMechValueMaps(delegator, partyId, false,
						"TELECOM_NUMBER");

		for (Map<String, Object> contact : contacts) {
			GenericValue telecomNumber = (GenericValue) contact
					.get("telecomNumber");
			if (null != telecomNumber) {
				result = telecomNumber.getString("contactNumber");
			}
		}

		return result;
	}

	public static Map<String, Object> userAdd(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();

		try {
			Timestamp now = UtilDateTime.nowTimestamp();
			String userLoginId = (String) context.get("userLoginId");
			String userName = (String) context.get("userName");
			String userCardId = (String) context.get("userCardId");
			String userTelNum = (String) context.get("userTelNum");
			String userDesc = (String) context.get("userDesc");
			Object userRoles = context.get("userRoles");

			String userPass = "123456";

			List<?> userRoleIds = null;
			if (userRoles instanceof List<?>) {
				userRoleIds = (List<?>) userRoles;
			}

			// 创建团体
			String partyId = delegator.getNextSeqId("Party"); // 用户Id，很多表的关联外键
			GenericValue party = delegator.makeValue("Party");
			party.set("partyId", partyId);
			party.set("description", userDesc);
			party.set("partyTypeId", "PERSON");
			delegator.create(party);

			// 创建人员团体
			GenericValue person = delegator.makeValue("Person");
			person.set("partyId", partyId);
			person.set("nickname", userName);
			person.set("cardId", userCardId);
			delegator.create(person);

			boolean useEncryption = "true"
					.equals(UtilProperties.getPropertyValue(
							"security.properties", "password.encrypt"));
			// 创建登录
			GenericValue userLogin = delegator.makeValue("UserLogin");
			userLogin.set("userLoginId", userLoginId);
			userLogin.set("partyId", partyId);
			userLogin.set(
					"currentPassword",
					useEncryption ? HashCrypt.cryptUTF8(getHashType(), null,
							userPass) : userPass);
			delegator.create(userLogin);

			// 创建联系方式
			String contactMechId = delegator.getNextSeqId("ContactMech");
			GenericValue contactMech = delegator.makeValue("ContactMech");
			contactMech.set("contactMechId", contactMechId);
			contactMech.set("contactMechTypeId", "TELECOM_NUMBER");
			delegator.create(contactMech);

			// 创建电话联系
			GenericValue telecomNumber = delegator.makeValue("TelecomNumber");
			telecomNumber.set("contactMechId", contactMechId);
			telecomNumber.set("contactNumber", userTelNum);
			delegator.create(telecomNumber);

			// 创建团体与联系方式的关联关系
			GenericValue partyContactMech = delegator
					.makeValue("PartyContactMech");
			partyContactMech.set("partyId", partyId);
			partyContactMech.set("contactMechId", contactMechId);
			partyContactMech.set("fromDate", now);
			delegator.create(partyContactMech);

			// 关联角色
			if (null != userRoleIds) {
				for (Object userRoleId : userRoleIds) {
					GenericValue userLoginSecurityGroup = delegator
							.makeValue("UserLoginSecurityGroup");
					userLoginSecurityGroup.set("userLoginId", userLoginId);
					userLoginSecurityGroup.set("groupId", userRoleId);
					userLoginSecurityGroup.set("fromDate", now);
					delegator.create(userLoginSecurityGroup);
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

	public static Map<String, Object> userEdt(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();

		try {
			Timestamp now = UtilDateTime.nowTimestamp();
			String userLoginId = (String) context.get("userLoginId");
			String userName = (String) context.get("userName");
			String userCardId = (String) context.get("userCardId");
			String userTelNum = (String) context.get("userTelNum");
			String userDesc = (String) context.get("userDesc");
			Object userRoles = context.get("userRoles");

			List<?> userRoleIds = null;
			if (userRoles instanceof List<?>) {
				userRoleIds = (List<?>) userRoles;
			}

			// 登录
			GenericValue userLogin = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", userLoginId), false);

			String partyId = userLogin.getString("partyId");

			// 团体
			GenericValue party = userLogin.getRelatedOne("Party", false);
			party.set("description", userDesc);
			party.store();

			// 人员团体
			GenericValue person = party.getRelatedOne("Person", false);
			person.set("nickname", userName);
			person.set("cardId", userCardId);
			person.store();

			// 团体与联系方式的关联关系
			List<GenericValue> partyContactMechs = delegator.findByAnd(
					"PartyContactMech", UtilMisc.toMap("partyId", partyId),
					null, false);

			partyContactMechs = EntityUtil
					.filterByDate(partyContactMechs, true);
			for (GenericValue partyContactMech : partyContactMechs) {
				GenericValue contactMech = partyContactMech.getRelatedOne(
						"ContactMech", false);
				if ("TELECOM_NUMBER".equals(contactMech
						.getString("contactMechTypeId"))) {
					GenericValue telecomNumber = contactMech.getRelatedOne(
							"TelecomNumber", false);
					telecomNumber.set("contactNumber", userTelNum);
					telecomNumber.store();
				}
			}

			// 关联角色
			List<GenericValue> userLoginSecurityGroups = delegator.findByAnd(
					"UserLoginSecurityGroup",
					UtilMisc.toMap("userLoginId", userLoginId), null, false);
			userLoginSecurityGroups = EntityUtil.filterByDate(
					userLoginSecurityGroups, true);

			boolean isAdd;
			List<Object> addRoles = new ArrayList<Object>();
			for (Object userRoleId : userRoleIds) {
				isAdd = true;
				for (GenericValue userLoginSecurityGroup : userLoginSecurityGroups) {
					if (userRoleId.equals(userLoginSecurityGroup
							.getString("groupId"))) {
						isAdd = false;
						continue;
					}
				}
				if (isAdd) {
					addRoles.add(userRoleId);
				}
			}

			boolean isDel;
			List<GenericValue> delRoles = new ArrayList<GenericValue>();
			for (GenericValue userLoginSecurityGroup : userLoginSecurityGroups) {
				isDel = true;

				for (Object userRoleId : userRoleIds) {
					if (userRoleId.equals(userLoginSecurityGroup
							.getString("groupId"))) {
						isDel = false;
						continue;
					}
				}
				if (isDel) {
					delRoles.add(userLoginSecurityGroup);
				}
			}

			for (Object addRole : addRoles) {
				GenericValue userLoginSecurityGroup = delegator
						.makeValue("UserLoginSecurityGroup");
				userLoginSecurityGroup.set("userLoginId", userLoginId);
				userLoginSecurityGroup.set("groupId", addRole);
				userLoginSecurityGroup.set("fromDate", now);
				delegator.create(userLoginSecurityGroup);
			}

			for (GenericValue delRole : delRoles) {
				// 更新过期时间，不删
				delRole.set("thruDate", now);
				delRole.store();
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

	public static String getHashType() {
		String hashType = UtilProperties.getPropertyValue(
				"security.properties", "password.encrypt.hash.type");

		if (UtilValidate.isEmpty(hashType)) {
			Debug.logWarning(
					"Password encrypt hash type is not specified in security.properties, use SHA",
					module);
			hashType = "SHA";
		}

		return hashType;
	}

	public static Map<String, Object> profileEdt(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();

		try {
			Timestamp now = UtilDateTime.nowTimestamp();
			String loginId = (String) context.get("loginId");
			String nickname = (String) context.get("nickname");
			String cardId = (String) context.get("cardId");
			String telNum = (String) context.get("telNum");
			String addr = (String) context.get("addr");

			// 登录
			GenericValue userLogin = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", loginId), false);

			String partyId = userLogin.getString("partyId");

			// 团体
			GenericValue party = userLogin.getRelatedOne("Party", false);
			party.set("description", addr);
			party.store();

			// 人员团体
			GenericValue person = party.getRelatedOne("Person", false);
			person.set("nickname", nickname);
			person.set("cardId", cardId);
			person.store();

			// 团体与联系方式的关联关系
			List<GenericValue> partyContactMechs = delegator.findByAnd(
					"PartyContactMech", UtilMisc.toMap("partyId", partyId),
					null, false);

			partyContactMechs = EntityUtil
					.filterByDate(partyContactMechs, true);
			int count = 0; // 联系方式数量
			for (GenericValue partyContactMech : partyContactMechs) {
				GenericValue contactMech = partyContactMech.getRelatedOne(
						"ContactMech", false);
				if ("TELECOM_NUMBER".equals(contactMech
						.getString("contactMechTypeId"))) {
					GenericValue telecomNumber = contactMech.getRelatedOne(
							"TelecomNumber", false);
					telecomNumber.set("contactNumber", telNum);
					telecomNumber.store();
					count++; // 已经有的update
				}
			}

			if (0 == count) {
				// 创建联系方式
				String contactMechId = delegator.getNextSeqId("ContactMech");
				GenericValue contactMech = delegator.makeValue("ContactMech");
				contactMech.set("contactMechId", contactMechId);
				contactMech.set("contactMechTypeId", "TELECOM_NUMBER");
				delegator.create(contactMech);

				// 创建电话联系
				GenericValue telecomNumber = delegator
						.makeValue("TelecomNumber");
				telecomNumber.set("contactMechId", contactMechId);
				telecomNumber.set("contactNumber", telNum);
				delegator.create(telecomNumber);

				// 创建团体与联系方式的关联关系
				GenericValue partyContactMech = delegator
						.makeValue("PartyContactMech");
				partyContactMech.set("partyId", partyId);
				partyContactMech.set("contactMechId", contactMechId);
				partyContactMech.set("fromDate", now);
				delegator.create(partyContactMech);
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

	public static Map<String, Object> testService(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
//		try {
//			Object obj = GroovyUtil
//					.runScriptAtLocation(
//							"component://ecommerce/webapp/ecommerce/WEB-INF/actions/catalog/BestSellingCategory.groovy",
//							null, null);
//			System.out.println(obj);
//
//		} catch (GeneralException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Debug.logError("---------------UserServices.testService()", module);
		return result;

	}
}
