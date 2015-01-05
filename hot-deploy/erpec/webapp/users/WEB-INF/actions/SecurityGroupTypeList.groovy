import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.erpec.pojo.SecurityGroupType

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
List<SecurityGroupType> securityGroupTypes = new ArrayList<SecurityGroupType>();
for (sgt in sgts) {

	// Copy entity
	SecurityGroupType securityGroupType = new SecurityGroupType();
	securityGroupType.setSecurityGroupTypeId(sgt.securityGroupTypeId);
	securityGroupType.setDescription(sgt.description);

	// deal child
	EntityCondition ec = EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, sgt.securityGroupTypeId);
	childSgts = delegator.findList("SecurityGroupType", ec, null, null, null, false);
	List<SecurityGroupType> childTypes = new ArrayList<SecurityGroupType>();
	for (childSgt in childSgts) {

		// Copy entity
		SecurityGroupType childType = new SecurityGroupType();
		childType.setSecurityGroupTypeId(childSgt.securityGroupTypeId);
		childType.setDescription(childSgt.description);

		// add list
		childTypes.add(childType);
	}
	securityGroupType.setChildTypes(childTypes);

	// add list
	securityGroupTypes.add(securityGroupType);
}

context.securityGroupTypes = securityGroupTypes;





