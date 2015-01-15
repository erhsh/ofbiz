import org.ofbiz.base.util.Debug
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.transaction.TransactionUtil
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.erpec.pojo.goods.ProductCategoryVO;


// 所有照明产品的父分类, 数据库初始化默认分类
String parentProductCategoryId = "LED_LIGHTING";

// 数据库代理对象
GenericDelegator delegator = delegator;

// 动态视图实体， 对应sql的left join/inner join
DynamicViewEntity dynViewEntity = new DynamicViewEntity();

dynViewEntity.addMemberEntity("PCR", "ProductCategoryRollup");
dynViewEntity.addAlias("PCR", "productCategoryId");
dynViewEntity.addAlias("PCR", "parentProductCategoryId");

dynViewEntity.addMemberEntity("PC", "ProductCategory")
dynViewEntity.addAlias("PC", "categoryName");

dynViewEntity.addViewLink("PCR", "PC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productCategoryId"))

// 查询
EntityListIterator eli;
List<ProductCategoryVO> productCategoryVOs = new ArrayList<ProductCategoryVO>();
boolean beganTransaction = false;
try {
	beganTransaction = TransactionUtil.begin();
	EntityCondition cond = EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, parentProductCategoryId);
	eli = delegator.findListIteratorByCondition(dynViewEntity, cond, null, null, null, null);
	List<GenericValue> lsts = eli.getCompleteList();
	
	for (lst in lsts) {
		ProductCategoryVO productCategoryVO = new ProductCategoryVO();
		productCategoryVO.setProductCategoryId(lst.getString("productCategoryId"));
		productCategoryVO.setCategoryName(lst.getString("categoryName"));
		productCategoryVOs.add(productCategoryVO);
	}
	
	eli.close();
}catch(GenericEntityException e){
	errMsg = "Failure in operation, rolling back transaction";
	Debug.logError(e, errMsg, "ProductCategoryLst");
	try {
		TransactionUtil.rollback(beganTransaction, errMsg, e);
	} catch (GenericEntityException e2) {
		Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), "ProductCategoryLst");
	}
	throw e;
} finally {
	TransactionUtil.commit(beganTransaction);
}

context.productCategoryVOs = productCategoryVOs;