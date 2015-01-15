import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.transaction.TransactionUtil
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.erpec.pojo.goods.ProductVO



GenericDelegator delegator = delegator;

DynamicViewEntity dve = new DynamicViewEntity();

dve.addMemberEntity("PCM", "ProductCategoryMember");
dve.addAlias("PCM", "productId");
dve.addAlias("PCM", "productCategoryId");
dve.addAlias("PCM", "thruDate");

dve.addMemberEntity("PRD", "Product")
dve.addAlias("PRD", "productName");
dve.addAlias("PRD", "internalName");
dve.addAlias("PRD", "description");
dve.addViewLink("PCM", "PRD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

dve.addMemberEntity("PP", "ProductPrice");
dve.addAlias("PP", "price");
dve.addViewLink("PRD", "PP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

// 所有照明产品的父分类
String productCategoryId = parameters.prodCategoryId;

EntityListIterator eli;
List<ProductVO> prodVOs = new ArrayList<ProductVO>();
boolean beganTransaction = false;
try {
	beganTransaction = TransactionUtil.begin();

	// 日期过滤
	EntityCondition whereCond = EntityCondition.makeCondition(
			EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, UtilDateTime.nowTimestamp()),
			EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)
			);

	if (UtilValidate.isNotEmpty(productCategoryId)) {
		// 目录过滤
		EntityCondition categoryCond = EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId);
		whereCond = EntityCondition.makeCondition(EntityOperator.AND, whereCond, categoryCond)
	}

	eli = delegator.findListIteratorByCondition(dve, whereCond, null, null, null, null);

	for (GenericValue product : eli.getCompleteList()) {
		ProductVO prodVO = new ProductVO();
		prodVO.setProdId(product.getString("productId"));
		prodVO.setProdName(product.getString("productName"));
		prodVO.setProdModel(product.getString("internalName"));
		prodVO.setProdCategory(product.getString("productCategoryId"));
		prodVO.setProdPrice(product.getString("price"));

		prodVOs.add(prodVO);
	}

	eli.close();
}catch(GenericEntityException e){
	errMsg = "Failure in operation, rolling back transaction";
	Debug.logError(e, errMsg, "ProductLst");
	try {
		TransactionUtil.rollback(beganTransaction, errMsg, e);
	} catch (GenericEntityException e2) {
		Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), "ProductLst");
	}
	throw e;
} finally {
	TransactionUtil.commit(beganTransaction);
}

println prodVOs;
request.setAttribute("rows", prodVOs);
request.setAttribute("results", prodVOs.size());