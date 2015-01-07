import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericValue
import org.ofbiz.erpec.pojo.RoleVO



GenericDelegator delegator = delegator;

List<GenericValue> securityGroups = delegator.findList("SecurityGroup", null, null, null, null, false);

List<RoleVO> roleVOs = new ArrayList<RoleVO>();


for (securityGroup in securityGroups) {
	RoleVO roleVO = new RoleVO();

	roleVO.setRoleId(securityGroup.securityGroupId);
	roleVO.setRoleDesc(securityGroup.description);

	roleVOs.add(roleVO);
}

context.roleVOs = roleVOs;