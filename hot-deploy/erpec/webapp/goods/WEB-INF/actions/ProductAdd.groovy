import java.sql.Timestamp

import javax.servlet.http.HttpServletRequest

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.common.DataModelConstants
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.transaction.TransactionUtil

String module = "ProductAdd.groovy";

HttpServletRequest request = request;
GenericDelegator delegator = delegator;
Timestamp now = UtilDateTime.nowTimestamp();

boolean beganTransaction = false;
try {
	Debug.logInfo("begin add product...", module);
	beganTransaction = TransactionUtil.begin();

	// 解析参数
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
	GenericValue product = delegator.makeValue("Product");
	String prodId = delegator.getNextSeqId("Product");
	product.set("productId", prodId)
	product.set("productName", prodName);
	product.set("internalName", prodModel);
	delegator.create(product);

	// 商品价格
	GenericValue productPrice = delegator.makeValue("ProductPrice");
	productPrice.set("productId", prodId);
	productPrice.set("productPriceTypeId", prodPriceTypeId);
	productPrice.set("productPricePurposeId", prodPricePurposeId);
	productPrice.set("currencyUomId", currencyUomId);
	productPrice.set("productStoreGroupId", prodStoreGrpId);
	productPrice.set("fromDate", now);
	productPrice.set("price", new BigDecimal(prodPrice));
	delegator.create(productPrice);

	// 商品分类关系
	GenericValue productCategoryMember = delegator.makeValue("ProductCategoryMember");
	productCategoryMember.set("productId", prodId)
	productCategoryMember.set("productCategoryId", prodCategoryId);
	productCategoryMember.set("fromDate", now);
	delegator.create(productCategoryMember);

	Debug.logInfo("End add product=========================", module);
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
