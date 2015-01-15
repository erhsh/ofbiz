import org.ofbiz.base.util.Debug
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.transaction.TransactionUtil
import org.ofbiz.entity.util.EntityListIterator



GenericDelegator delegator = delegator;

DynamicViewEntity dynViewEntity = new DynamicViewEntity();

dynViewEntity.addMemberEntity("PCR", "ProductCategoryRollup");
dynViewEntity.addAlias("PCR", "productCategoryId");
dynViewEntity.addAlias("PCR", "parentProductCategoryId");

dynViewEntity.addMemberEntity("PC", "ProductCategory")
dynViewEntity.addAlias("PC", "categoryName");

dynViewEntity.addViewLink("PCR", "PC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productCategoryId"))

// 所有照明产品的父分类
parentProductCategoryId = "LED_LIGHTING";

EntityListIterator eli;
List lst;
boolean beganTransaction = false;
try {
	beganTransaction = TransactionUtil.begin();
	EntityCondition cond = EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, parentProductCategoryId);
	eli = delegator.findListIteratorByCondition(dynViewEntity, cond, null, null, null, null);
	lst = eli.getCompleteList();
	eli.close();
}catch(GenericEntityException e){
	errMsg = "Failure in operation, rolling back transaction";
	Debug.logError(e, errMsg, "ViewFacilityInventoryByProduct");
	try {
		// only rollback the transaction if we started one...
		TransactionUtil.rollback(beganTransaction, errMsg, e);
	} catch (GenericEntityException e2) {
		Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), "ViewFacilityInventoryByProduct");
	}
	// after rolling back, rethrow the exception
	throw e;
} finally {
	TransactionUtil.commit(beganTransaction);
}

println lst;
request.setAttribute("result", lst);