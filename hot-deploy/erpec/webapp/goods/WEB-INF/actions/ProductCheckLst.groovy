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

public List<ProductVO> getNewProductCheckLst() {
	String module = "ProductCheckLst.groovy";
	List<ProductVO> prodVOs = new ArrayList<ProductVO>();

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

	dve.addMemberEntity("PS", "ProductState");
	dve.addAlias("PS", "state");
	dve.addViewLink("PRD", "PS", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))


	// 所有照明产品的父分类
	String productCategoryId = parameters.prodCategoryId;

	EntityListIterator eli;
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

		// 新建待审核
		EntityCondition newProdCond = EntityCondition.makeCondition("state", EntityOperator.EQUALS, "CHECKING");
		whereCond = EntityCondition.makeCondition(EntityOperator.AND, whereCond, newProdCond)

		eli = delegator.findListIteratorByCondition(dve, whereCond, null, null, null, null);

		for (GenericValue product : eli.getCompleteList()) {
			ProductVO prodVO = new ProductVO();
			prodVO.setProdId(product.getString("productId"));
			prodVO.setProdName(product.getString("productName"));
			prodVO.setProdModel(product.getString("internalName"));
			prodVO.setProdCategory(product.getString("productCategoryId"));
			prodVO.setProdPrice(product.getString("price"));
			prodVO.setProdState(product.getString("state"));
			prodVOs.add(prodVO);
		}

		eli.close();
	}catch(GenericEntityException e){
		errMsg = "Failure in operation, rolling back transaction";
		Debug.logError(e, errMsg, module);
		try {
			TransactionUtil.rollback(beganTransaction, errMsg, e);
		} catch (GenericEntityException e2) {
			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		}
		throw e;
	} finally {
		TransactionUtil.commit(beganTransaction);
	}

	println prodVOs;
	return prodVOs;
}

public List<ProductVO> getEnableProductCheckLst() {
	String module = "ProductCheckLst.groovy";
	List<ProductVO> prodVOs = new ArrayList<ProductVO>();

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

	dve.addMemberEntity("TPE", "TmpProductEnable");
	dve.addAlias("TPE", "tmpEnable");//标志是上架请求，还是下架请求
	dve.addAlias("TPE", "checkState");//当前请求审核状态
	dve.addViewLink("PRD", "TPE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))


	// 所有照明产品的父分类
	String productCategoryId = parameters.prodCategoryId;

	EntityListIterator eli;
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

		// 上下架待审核
		EntityCondition newProdCond = EntityCondition.makeCondition("checkState", EntityOperator.EQUALS, "_NA_");
		whereCond = EntityCondition.makeCondition(EntityOperator.AND, whereCond, newProdCond)

		eli = delegator.findListIteratorByCondition(dve, whereCond, null, null, null, null);

		for (GenericValue product : eli.getCompleteList()) {
			ProductVO prodVO = new ProductVO();
			prodVO.setProdId(product.getString("productId"));
			prodVO.setProdName(product.getString("productName"));
			prodVO.setProdModel(product.getString("internalName"));
			prodVO.setProdCategory(product.getString("productCategoryId"));
			prodVO.setProdPrice(product.getString("price"));
			prodVO.setProdState(product.getString("tmpEnable"));
			prodVOs.add(prodVO);
		}

		eli.close();
	}catch(GenericEntityException e){
		errMsg = "Failure in operation, rolling back transaction";
		Debug.logError(e, errMsg, module);
		try {
			TransactionUtil.rollback(beganTransaction, errMsg, e);
		} catch (GenericEntityException e2) {
			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		}
		throw e;
	} finally {
		TransactionUtil.commit(beganTransaction);
	}
	return prodVOs;
}

List<ProductVO> prodVOs = new ArrayList<ProductVO>();
prodVOs.addAll(getNewProductCheckLst());
prodVOs.addAll(getEnableProductCheckLst());

request.setAttribute("rows", prodVOs);
request.setAttribute("results", prodVOs.size());