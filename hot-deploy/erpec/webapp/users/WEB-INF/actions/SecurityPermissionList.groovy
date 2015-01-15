import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.erpec.pojo.SecurityPermissionVO

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
List<SecurityPermissionVO> securityPermissionVOs = new ArrayList<SecurityPermissionVO>();
for (sp in sps) {

	// Copy entity
	SecurityPermissionVO securityPermissionVO = new SecurityPermissionVO();
	securityPermissionVO.setPermissionId(sp.permissionId);
	securityPermissionVO.setDescription(sp.description);

	// add list
	securityPermissionVOs.add(securityPermissionVO);
}

context.securityPermissionVOs = securityPermissionVOs;