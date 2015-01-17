import java.sql.Timestamp

import javax.servlet.http.HttpServletRequest

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.common.DataModelConstants
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.transaction.TransactionUtil

String module = "ProductEdt.groovy";

HttpServletRequest request = request;
GenericDelegator delegator = delegator;
Timestamp now = UtilDateTime.nowTimestamp();

boolean beganTransaction = false;
try {
	Debug.logInfo("begin edit product...", module);
	beganTransaction = TransactionUtil.begin();

	// 解析参数
	String prodId = request.getParameter("prodId");
	String prodName = request.getParameter("prodName");
	String prodModel = request.getParameter("prodModel");
	String prodPrice = request.getParameter("prodPrice");
	String prodCategoryId = request.getParameter("prodCategoryId")
	String prodTypeId = "FINISHED_GOOD"; // 产品类型：成品
	String prodPriceTypeId = "DEFAULT_PRICE"; // 价格类型:默认商品价格
	String prodPricePurposeId = "PURCHASE"; // 价格目的：购买价格
	String currencyUomId = "CNY"; // 价格币种：人民币
	String prodStoreGrpId = DataModelConstants.SEQ_ID_NA; // 产品店铺组ID：目前不可用

	// 商品
	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", prodId), false);
	product.set("productName", prodName);
	product.set("internalName", prodModel);
	product.store();
	
	// 商品价格
	boolean isChanged = false;
	List<GenericValue> productPrices = product.getRelated("ProductPrice", null, null, false);
	for (GenericValue productPrice : productPrices) {
		Object price = productPrice.get("price");
		if (!prodPrice.equals(price)) {
			productPrice.set("price", new BigDecimal(prodPrice));
			productPrice.store();
		}
	}

	if (UtilValidate.isEmpty(productPrices)) {
		GenericValue productPrice = delegator.makeValue("ProductPrice");
		productPrice.set("productId", prodId);
		productPrice.set("productPriceTypeId", prodPriceTypeId);
		productPrice.set("productPricePurposeId", prodPricePurposeId);
		productPrice.set("currencyUomId", currencyUomId);
		productPrice.set("productStoreGroupId", prodStoreGrpId);
		productPrice.set("fromDate", now);
		productPrice.set("price", new BigDecimal(prodPrice));
		delegator.create(productPrice);
	}

	// 商品分类关系
	isChanged = false;
	List<GenericValue> oldCategorys = product.getRelated("ProductCategoryMember", null, null, false);
	for (GenericValue oldCategory in oldCategorys) {
		String oldCategoryId = oldCategory.getString("productCategoryId");
		if (!oldCategoryId.equals(prodCategoryId)){
			// 业务上：分类 商品 一对多， Ofbiz数据库设计：多对多
			isChanged = true;
			oldCategory.set("thruDate", now);
			oldCategory.store();
		}
	}

	if (isChanged || UtilValidate.isEmpty(oldCategorys)) {
		GenericValue newCategory = delegator.makeValue("ProductCategoryMember");
		newCategory.set("productCategoryId", prodCategoryId);
		newCategory.set("productId", prodId);
		newCategory.set("fromDate", now);
		delegator.create(newCategory);
	}

	Debug.logInfo("End edit product=========================", module);
} catch (GenericEntityException e) {
	String errMsg = "Failure in operation, rolling back transaction";
	Debug.logError(e, errMsg, module);
	try {
		TransactionUtil.rollback(beganTransaction, errMsg, e);
	}catch (GenericEntityException e2) {
		Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	}
} finally {
	TransactionUtil.commit(beganTransaction);
}
