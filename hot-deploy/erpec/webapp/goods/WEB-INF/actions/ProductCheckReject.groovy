import java.sql.Timestamp

import javax.servlet.http.HttpServletRequest

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.transaction.TransactionUtil

String module = "ProductCheckReject.groovy";

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
	String[] prodStates = request.getParameterValues("prodStates[]");
	Debug.logInfo(">>>Product ids:" + prodIds, module);
	Debug.logInfo(">>>Product states:" + prodStates, module);

	int count = (prodIds.length == prodStates.length) ? prodIds.length : 0;

	for (int i=0; i<count; i++) {
		String prodId = prodIds[i];
		String prodState = prodStates[i];

		// 商品
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", prodId), false);

		if (null == product) {
			throw new Exception("Unknow prodId:" + prodId);
		}

		GenericValue productExt = product.getRelatedOne("ProductExt", false);
		if (null == productExt) {
			throw new Exception("ProductExt is null, prodId:" + prodId);
		}


		if ("CHECKING".equals(prodState)) {
			// 新建待审核
			String oldState = productExt.getString("state");
			if (prodState.equals(oldState)) {
				// 状态恢复
				productExt.set("state", PROD_STAT_NEW);
				productExt.store();
			}
		} else if ("ENABLING".equals(prodState) || "DISABLING".equals(prodState)) {
			// 上下架审核
			String stateEnable = productExt.getString("stateEnable");
			if (prodState.equals(stateEnable)) {
				// 状态恢复
				productExt.set("stateEnable", "_NA_")
				productExt.store();

				GenericValue tmpProductEnable = product.getRelatedOne("TmpProductEnable", false);
				if (null != tmpProductEnable){
					// 保存审核结果
					tmpProductEnable.setString("checkState", "REJECT");
					tmpProductEnable.setString("checkMsg", "拒绝还需要理由吗?");
					tmpProductEnable.store();

					Debug.logInfo(">>>Check ENABLING or DISABLING over.", module);
				} else {
					Debug.logWarning("TmpProductEnable lost data.", module);
				}
			}

		} else if ("EDITING".equals(prodState)) {
			Debug.logInfo(">>>Deal EDITING...", module);
			// 编辑审核
			String stateEdit = productExt.getString("stateEdit");
			if (prodState.equals(stateEdit)) {// 编辑请求
				// 状态恢复
				productExt.set("stateEdit", "_NA_")
				productExt.store();

				// 保存审核结果
				GenericValue tmpProductEdit = product.getRelatedOne("TmpProductEdit", false);
				tmpProductEdit.setString("checkState", "REJECT");
				tmpProductEdit.setString("checkMsg", "拒绝还需要理由吗?");
				tmpProductEdit.store();

				Debug.logInfo(">>>Deal EDITING over.", module);
			} else {
				Debug.logWarning("Old state is not EDITING, discard this operation.", module);
			}
		} else if ("PRICING".equals(prodState)) {
			Debug.logInfo(">>>Deal Pricing...", module);
			String statePrice = productExt.getString("statePrice");
			if (prodState.equals(statePrice)) {
				// 更新产品状态
				productExt.set("statePrice", "_NA_");
				productExt.store();

				// 保存审核结果
				GenericValue tmpProductPrice = product.getRelatedOne("TmpProductPrice", false);
				tmpProductPrice.setString("checkState", "REJECT");
				tmpProductPrice.setString("checkMsg", "拒绝还需要理由吗?");
				tmpProductPrice.store();

				Debug.logInfo(">>>Deal Pricing over.", module);
			} else {
				Debug.logWarning("Old price state is not pricing, discard this operation.", module);
			}
		} else {
			Debug.logWarning("Unkown request state: " + prodState, module);
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
