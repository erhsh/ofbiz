import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator;
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

	dve.addMemberEntity("PE", "ProductExt");
	dve.addAlias("PE", "productId");
	dve.addAlias("PE", "productCode");
	dve.addAlias("PE", "state");

	dve.addMemberEntity("PRD", "Product")
	dve.addAlias("PRD", "productName");
	dve.addAlias("PRD", "internalName");
	dve.addAlias("PRD", "description");
	dve.addViewLink("PE", "PRD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

	dve.addMemberEntity("PCM", "ProductCategoryMember");
	dve.addAlias("PCM", "productCategoryId");
	dve.addAlias("PCM", "thruDate");
	dve.addViewLink("PE", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

	dve.addMemberEntity("PP", "ProductPrice");
	dve.addAlias("PP", "price");
	dve.addViewLink("PE", "PP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

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
			prodVO.setProdCode(product.getString("productCode"));
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
	
	dve.addMemberEntity("PE", "ProductExt");
	dve.addAlias("PE", "productId");
	dve.addAlias("PE", "productCode");
	dve.addAlias("PE", "stateEnable"); //标志是上架请求，还是下架请求

	dve.addMemberEntity("PRD", "Product")
	dve.addAlias("PRD", "productName");
	dve.addAlias("PRD", "internalName");
	dve.addAlias("PRD", "description");
	dve.addViewLink("PE", "PRD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

	dve.addMemberEntity("PCM", "ProductCategoryMember");
	dve.addAlias("PCM", "productCategoryId");
	dve.addAlias("PCM", "thruDate");
	dve.addViewLink("PE", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

	dve.addMemberEntity("PP", "ProductPrice");
	dve.addAlias("PP", "price");
	dve.addViewLink("PE", "PP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

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
		EntityCondition newProdCond = EntityCondition.makeCondition(EntityCondition.makeCondition("stateEnable", EntityOperator.EQUALS, "ENABLING"),
				EntityJoinOperator.OR, EntityCondition.makeCondition("stateEnable", EntityOperator.EQUALS, "DISABLING"))
		whereCond = EntityCondition.makeCondition(EntityOperator.AND, whereCond, newProdCond)

		eli = delegator.findListIteratorByCondition(dve, whereCond, null, null, null, null);

		for (GenericValue product : eli.getCompleteList()) {
			ProductVO prodVO = new ProductVO();
			prodVO.setProdId(product.getString("productId"));
			prodVO.setProdCode(product.getString("productCode"));
			prodVO.setProdName(product.getString("productName"));
			prodVO.setProdModel(product.getString("internalName"));
			prodVO.setProdCategory(product.getString("productCategoryId"));
			prodVO.setProdPrice(product.getString("price"));
			prodVO.setProdState(product.getString("stateEnable"));
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


public List<ProductVO> getEditProductCheckLst() {
	String module = "ProductCheckLst.groovy";
	List<ProductVO> prodVOs = new ArrayList<ProductVO>();

	DynamicViewEntity dve = new DynamicViewEntity();
	
	dve.addMemberEntity("PE", "ProductExt");
	dve.addAlias("PE", "productCode");
	dve.addAlias("PE", "productId");
	dve.addAlias("PE", "stateEdit"); //标志是修改请求

	dve.addMemberEntity("PRD", "Product")
	dve.addAlias("PRD", "productName");
	dve.addAlias("PRD", "internalName");
	dve.addAlias("PRD", "description");
	dve.addViewLink("PE", "PRD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

	dve.addMemberEntity("PCM", "ProductCategoryMember");
	dve.addAlias("PCM", "productCategoryId");
	dve.addAlias("PCM", "thruDate");
	dve.addViewLink("PE", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

	dve.addMemberEntity("PP", "ProductPrice");
	dve.addAlias("PP", "price");
	dve.addViewLink("PE", "PP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))
	
	dve.addMemberEntity("TPE", "TmpProductEdit");
	dve.addAlias("TPE", "tmpProductNo");
	dve.addAlias("TPE", "tmpProductName");
	dve.addAlias("TPE", "tmpInternalName");
	dve.addAlias("TPE", "tmpProductCategoryId");
	dve.addAlias("TPE", "checkState");//当前请求审核状态
	dve.addViewLink("PE", "TPE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))


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

		// 编辑待审核
		EntityCondition newProdCond = EntityCondition.makeCondition("stateEdit", EntityOperator.EQUALS, "EDITING");
		whereCond = EntityCondition.makeCondition(EntityOperator.AND, whereCond, newProdCond)

		eli = delegator.findListIteratorByCondition(dve, whereCond, null, null, null, null);

		for (GenericValue product : eli.getCompleteList()) {
			ProductVO prodVO = new ProductVO();
			prodVO.setProdId(product.getString("productId"));
			prodVO.setProdCode(product.getString("productCode"));
			prodVO.setProdName(product.getString("productName")+"->"+product.getString("tmpProductName"));
			prodVO.setProdModel(product.getString("internalName")+"->"+product.getString("tmpInternalName"));
			prodVO.setProdCategory(product.getString("productCategoryId")+"->"+product.getString("tmpProductCategoryId"));
			prodVO.setProdPrice(product.getString("price"));
			prodVO.setProdState(product.getString("stateEdit"));
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

public List<ProductVO> getPriceProductCheckLst() {
	String module = "ProductCheckLst.groovy";
	List<ProductVO> prodVOs = new ArrayList<ProductVO>();

	GenericDelegator delegator = delegator;
	DynamicViewEntity dve = new DynamicViewEntity();
	
	dve.addMemberEntity("PE", "ProductExt");
	dve.addAlias("PE", "productId");
	dve.addAlias("PE", "productCode");
	dve.addAlias("PE", "statePrice"); //标志是改价请求

	dve.addMemberEntity("PRD", "Product")
	dve.addAlias("PRD", "productName");
	dve.addAlias("PRD", "internalName");
	dve.addAlias("PRD", "description");
	dve.addViewLink("PE", "PRD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

	dve.addMemberEntity("PCM", "ProductCategoryMember");
	dve.addAlias("PCM", "productCategoryId");
	dve.addAlias("PCM", "thruDate");
	dve.addViewLink("PE", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))

	dve.addMemberEntity("PP", "ProductPrice");
	dve.addAlias("PP", "price");
	dve.addViewLink("PE", "PP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))
	
	dve.addMemberEntity("TPP", "TmpProductPrice");
	dve.addAlias("TPP", "partyId");
	dve.addAlias("TPP", "tmpPrice");
	dve.addAlias("TPP", "fromDate");
	dve.addAlias("TPP", "checkState");//当前请求审核状态
	dve.addViewLink("PE", "TPP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"))


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

		// 编辑待审核
		EntityCondition newProdCond = EntityCondition.makeCondition("statePrice", EntityOperator.EQUALS, "PRICING");
		whereCond = EntityCondition.makeCondition(EntityOperator.AND, whereCond, newProdCond)

		eli = delegator.findListIteratorByCondition(dve, whereCond, null, null, null, null);

		for (GenericValue product : eli.getCompleteList()) {
			ProductVO prodVO = new ProductVO();
			prodVO.setProdId(product.getString("productId"));
			prodVO.setProdCode(product.getString("productCode"));
			prodVO.setProdName(product.getString("productName"));
			prodVO.setProdModel(product.getString("internalName"));
			prodVO.setProdCategory(product.getString("productCategoryId"));
			prodVO.setProdPrice(product.getString("price")+"->" + product.getString("tmpPrice") + "(" + product.getString("fromDate")+")");
			prodVO.setProdState(product.getString("statePrice"));
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
prodVOs.addAll(getEditProductCheckLst());
prodVOs.addAll(getPriceProductCheckLst());

request.setAttribute("rows", prodVOs);
request.setAttribute("results", prodVOs.size());