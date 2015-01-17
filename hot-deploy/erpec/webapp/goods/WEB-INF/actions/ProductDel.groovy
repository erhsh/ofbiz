import java.sql.Timestamp

import javax.servlet.http.HttpServletRequest

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.transaction.TransactionUtil

String module = "ProductDel.groovy";

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
	Debug.logInfo(">>>Submit products:"+ prodIds, module);
	for (prodId in prodIds) {
		// 商品
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", prodId), false);
		if (null == product) {
			throw new Exception("product is null, prodId:" + prodId);
		}

		// 商品状态
		GenericValue productState = product.getRelatedOne("ProductState", false);
		String oldState = productState.getString("state");
		if (!PROD_STAT_NEW.equals(oldState)) {
			throw new Exception("Unexpected product state");
		}

		// 删除状态
		productState.remove();

		// 删除分类关系
		product.removeRelated("ProductCategoryMember");

		// 删除价格关系
		product.removeRelated("ProductPrice");

		// 删除商品搜索关键字
		product.removeRelated("ProductKeyword");

		// 删除商品
		product.remove();

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
