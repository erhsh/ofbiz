import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.erpec.pojo.SecurityGroupTypeVO

/**
 * @tile 查找安全组分类
 * @description 查找所有安全组分类，形成树形结构。
 * @author j
 * @version V1.0
 * @date 2014-12-31
 */

GenericDelegator delegator = delegator;

EntityCondition cond = EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, null);
sgts = delegator.findList("SecurityGroupType", cond, null, null, null, false);
List<SecurityGroupTypeVO> securityGroupTypeVOs = new ArrayList<SecurityGroupTypeVO>();
for (sgt in sgts) {

	// Copy entity
	SecurityGroupTypeVO securityGroupTypeVO = new SecurityGroupTypeVO();
	securityGroupTypeVO.setSecurityGroupTypeId(sgt.securityGroupTypeId);
	securityGroupTypeVO.setDescription(sgt.description);

	// deal child
	EntityCondition ec = EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, sgt.securityGroupTypeId);
	childSgts = delegator.findList("SecurityGroupType", ec, null, null, null, false);
	List<SecurityGroupTypeVO> childTypes = new ArrayList<SecurityGroupTypeVO>();
	for (childSgt in childSgts) {

		// Copy entity
		SecurityGroupTypeVO childType = new SecurityGroupTypeVO();
		childType.setSecurityGroupTypeId(childSgt.securityGroupTypeId);
		childType.setDescription(childSgt.description);

		// add list
		childTypes.add(childType);
	}
	securityGroupTypeVO.setChildTypes(childTypes);

	// add list
	securityGroupTypeVOs.add(securityGroupTypeVO);
}

context.securityGroupTypeVOs = securityGroupTypeVOs;





