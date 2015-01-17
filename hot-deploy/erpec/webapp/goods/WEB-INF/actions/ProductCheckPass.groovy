import java.sql.Timestamp

import javax.servlet.http.HttpServletRequest

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.transaction.TransactionUtil

String module = "ProductCheckPass.groovy";

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
	Debug.logInfo(">>>Products:"+ prodIds, module);
	for (prodId in prodIds) {
		// 商品
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", prodId), false);

		if (null == product) {
			throw new Exception("Unknow prodId:" + prodId);
		}

		GenericValue productState = product.getRelatedOne("ProductState", false);
		if (null == productState) {
			throw new Exception("ProductState is null, prodId:" + prodId);
		}

		String oldState = productState.getString("state");

		// 新建待审核
		if (PROD_STAT_CHECKING.equals(oldState)) {

			productState.set("state", PROD_STAT_DISABLED);
			productState.store();
		}

		// 架上 or 架下
		if (PROD_STAT_ENABLED.equals(oldState) || PROD_STAT_DISABLED.equals(oldState)) {

			// 上下架请求
			GenericValue tmpProductEnable = product.getRelatedOne("TmpProductEnable", false);
			String newState = tmpProductEnable.getString("tmpEnable");
			if ("ENABLE".equals(newState)) {
				// 上架请求
				productState.set("state", PROD_STAT_ENABLED);
				productState.store();
			}else if ("DISABLE".equals(newState)){
				// 下架请求
				productState.set("state", PROD_STAT_DISABLED);//TODO:下架需考虑订单的问题
				productState.store();
			}

			// 保存审核结果
			tmpProductEnable.setString("checkState", "PASS");
			tmpProductEnable.setString("checkMsg", "ok, no problem~");
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
