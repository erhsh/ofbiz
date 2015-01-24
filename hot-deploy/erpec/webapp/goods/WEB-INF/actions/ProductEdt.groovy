import groovy.lang.Script;

import java.sql.Timestamp

import javax.servlet.http.HttpServletRequest

import org.codehaus.groovy.runtime.InvokerHelper;
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.GroovyUtil;
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.common.DataModelConstants
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.transaction.TransactionUtil

//import ProductEdtPrice;



String module = "ProductEdt.groovy";

HttpServletRequest request = request;
GenericDelegator delegator = delegator;
Timestamp now = UtilDateTime.nowTimestamp();

if (!UtilValidate.isEmpty(request.getParameter("isPrice"))) {
	
	Script script = InvokerHelper.createScript(GroovyUtil.getScriptClassFromLocation("component://erpec/webapp/goods/WEB-INF/actions/ProductEdtPrice.groovy", null), GroovyUtil.getBinding(context));
	result = script.run();
	println "-------------"+result;
	return;
}

boolean beganTransaction = false;
try {
	Debug.logInfo("begin edit product...", module);
	beganTransaction = TransactionUtil.begin();

	// 解析参数
	String prodId = request.getParameter("prodId");
	String prodName = request.getParameter("prodName");
	String prodModel = request.getParameter("prodModel");
	String prodCategoryId = request.getParameter("prodCategory")
	String prodTypeId = "FINISHED_GOOD"; // 产品类型：成品
	String prodPriceTypeId = "DEFAULT_PRICE"; // 价格类型:默认商品价格
	String prodPricePurposeId = "PURCHASE"; // 价格目的：购买价格
	String currencyUomId = "CNY"; // 价格币种：人民币
	String prodStoreGrpId = DataModelConstants.SEQ_ID_NA; // 产品店铺组ID：目前不可用

	// 商品
	GenericValue product = delegator.findOne("Product", ["productId" : prodId], false);

	if (null == product) {
		throw new Exception("unknown product, prodId:" + prodId);
	}

	// 临时表：saveOrUpdate
	GenericValue tmpProductEdit = product.getRelatedOne("TmpProductEdit", false);
	if (null == tmpProductEdit) {
		tmpProductEdit = delegator.makeValue("TmpProductEdit");
		tmpProductEdit.set("productId", prodId);
		tmpProductEdit.set("partyId", "admin");//TODO:
		tmpProductEdit.set("tmpProductNo", prodName);
		tmpProductEdit.set("tmpProductName", prodName);
		tmpProductEdit.set("tmpInternalName", prodModel);
		tmpProductEdit.set("tmpProductCategoryId", prodCategoryId);
		tmpProductEdit.set("checkState", "_NA_");
		tmpProductEdit.set("checkMsg", "_NA_");
		delegator.create(tmpProductEdit);
	}else {
		tmpProductEdit.set("partyId", "admin");//TODO:
		tmpProductEdit.set("tmpProductNo", prodName);
		tmpProductEdit.set("tmpProductName", prodName);
		tmpProductEdit.set("tmpInternalName", prodModel);
		tmpProductEdit.set("tmpProductCategoryId", prodCategoryId);
		tmpProductEdit.set("checkState", "_NA_");
		tmpProductEdit.set("checkMsg", "_NA_");
		tmpProductEdit.store();
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
