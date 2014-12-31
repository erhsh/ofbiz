import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator



GenericDelegator delegator = delegator;


securityGroupTypeId = null;
securityGroupTypeId = parameters.securityGroupTypeId;
EntityCondition cond = null;
if(null != securityGroupTypeId && !securityGroupTypeId.isEmpty()) {
	cond = EntityCondition.makeCondition("securityGroupTypeId", EntityOperator.EQUALS, securityGroupTypeId);
}

sgtgv = delegator.findList("SecurityGroupTypeGroupView", cond, null, null, null, false)

println sgtgv;
context.securityGroups = sgtgv;







