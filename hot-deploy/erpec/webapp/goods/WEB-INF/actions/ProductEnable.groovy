import java.sql.Timestamp

import javax.servlet.http.HttpServletRequest

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.transaction.TransactionUtil

String module = "ProductEnable.groovy";

HttpServletRequest request = request;
GenericDelegator delegator = delegator;
Timestamp now = UtilDateTime.nowTimestamp();


final String PROD_STAT_NEW = "NEW"; // 新建待提交
final String PROD_STAT_CHECKING = "CHECKING"; // 新建待审核
final String PROD_STAT_ENABLED = "ENABLED"; // 架下
final String PROD_STAT_DISABLED = "DISABLED"; // 架上

boolean beganTransaction = false;
try {
	Debug.logInfo("begin>>>", module);
	beganTransaction = TransactionUtil.begin();

	// 解析参数
	String[] prodIds = request.getParameterValues("prodIds[]");
	Debug.logInfo(">>>products:"+ prodIds, module);
	for (prodId in prodIds) {
		Debug.logInfo(">>>deal:" + prodId, module);

		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", prodId), false);
		if (null == product) {
			throw new Exception("unknow product");
		}
		
		GenericValue productState = product.getRelatedOne("ProductState", false);
		if (null == productState || !"DISABLED".equals(productState.get("state"))) {
			throw new Exception("product pre state should be disabled");
		}

		GenericValue tmpProductEnable = product.getRelatedOne("TmpProductEnable", false);
		if (null == tmpProductEnable) {
			tmpProductEnable = delegator.makeValue("TmpProductEnable");
			tmpProductEnable.set("productId", prodId);
			tmpProductEnable.set("partyId", "admin");//TODO:here use the real login user id
			tmpProductEnable.set("tmpEnable", "ENABLE");
			tmpProductEnable.set("checkState", "_NA_");//未经审核
			tmpProductEnable.set("checkMsg", "_NA_");
			delegator.create(tmpProductEnable);
		} else {
			tmpProductEnable.set("partyId", "admin");//TODO:here use the real login user id
			tmpProductEnable.set("tmpEnable", "ENABLE");
			tmpProductEnable.set("checkState", "_NA_");//未经审核
			tmpProductEnable.set("checkMsg", "_NA_");
			tmpProductEnable.store();
		}

	}

	Debug.logInfo("End>>>", module);
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
