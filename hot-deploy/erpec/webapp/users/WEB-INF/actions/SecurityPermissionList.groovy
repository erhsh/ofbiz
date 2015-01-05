import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.erpec.pojo.SecurityPermission

/**
 * @tile 权限列表
 * @description 根据查询条件，列出权限列表，无条件，列出所有。
 * @author j
 * @version V1.0
 * @date 2014-12-31
 */

GenericDelegator delegator = delegator;

EntityCondition cond = null;
sps = delegator.findList("SecurityPermission", cond, null, null, null, false);
List<SecurityPermission> securityPermissions = new ArrayList<SecurityPermission>();
for (sp in sps) {

	// Copy entity
	SecurityPermission securityPermission = new SecurityPermission();
	securityPermission.setPermissionId(sp.permissionId);
	securityPermission.setDescription(sp.description);

	// add list
	securityPermissions.add(securityPermission);
}

context.securityPermissions = securityPermissions;