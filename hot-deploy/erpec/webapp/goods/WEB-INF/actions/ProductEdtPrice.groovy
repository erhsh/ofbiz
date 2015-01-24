import java.sql.Timestamp
import java.text.DateFormat

import javax.servlet.http.HttpServletRequest

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.transaction.TransactionUtil

String module = "ProductEdt.groovy";

HttpServletRequest request = request;
GenericDelegator delegator = delegator;
Timestamp now = UtilDateTime.nowTimestamp();
DateFormat df = UtilDateTime.toDateFormat(UtilDateTime.DATE_FORMAT,TimeZone.getDefault(), Locale.getDefault());
boolean beganTransaction = false;
try {
	Debug.logInfo("begin edit product...", module);
	beganTransaction = TransactionUtil.begin();

	// 解析参数
	String prodId = request.getParameter("prodId");
	String prodPrice = request.getParameter("prodPrice");
	String prodPriceFromDate = request.getParameter("prodPriceFromDate");

	println "prodId:" + prodPrice;
	println "prodPrice:" + prodPrice;
	println "prodPriceFromDate:" + prodPriceFromDate;

	// 商品
	GenericValue product = delegator.findOne("Product", ["productId" : prodId], false);
	if (null == product) {
		throw new Exception("unknown product, prodId:" + prodId);
	}

	// 商品状态
	GenericValue productExt = product.getRelatedOne("ProductExt", false);
	if (null == productExt) {
		throw new Exception("product state cannot find");
	}
	if ("_NA_".equals(productExt.get("statePrice"))) {
		productExt.set("statePrice", "PRICING");//价格调整中
		productExt.store();
	}

	// 临时表：saveOrUpdate
	GenericValue tmpProductPrice = product.getRelatedOne("TmpProductPrice", false);
	if (null == tmpProductPrice) {
		tmpProductPrice = delegator.makeValue("TmpProductPrice");
		tmpProductPrice.set("productId", prodId);
		tmpProductPrice.set("partyId", "admin");//TODO:
		tmpProductPrice.set("tmpPrice", new BigDecimal(prodPrice));
		tmpProductPrice.set("fromDate", UtilDateTime.toTimestamp(df.parse(prodPriceFromDate)));
		tmpProductPrice.set("checkState", "_NA_");
		tmpProductPrice.set("checkMsg", "_NA_");
		delegator.create(tmpProductPrice);
	}else {
		tmpProductPrice.set("partyId", "admin");//TODO:
		tmpProductPrice.set("tmpPrice", new BigDecimal(prodPrice));
		tmpProductPrice.set("fromDate", UtilDateTime.toTimestamp(df.parse(prodPriceFromDate)));
		tmpProductPrice.set("checkState", "_NA_");
		tmpProductPrice.set("checkMsg", "_NA_");
		tmpProductPrice.store();
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
