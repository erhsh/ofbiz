package org.ofbiz.erpec.goods.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class GoodsServices {
	public static final String module = GoodsServices.class.getName();
	public static final String resource = "PartyUiLabels";
	public static final String resourceError = "PartyErrorUiLabels";

	/**
	 * 使审核通过的价格生效
	 * 
	 * @param dctx
	 *            DispatchContext
	 * @param context
	 *            Map
	 * @return Map
	 */
	public static Map<String, Object> makePriceChangeValidate(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();

		try {
			// 查找审核通过的待生效商品
			EntityCondition entityCondition = EntityCondition.makeCondition(
					"checkState", "PASS");

			List<GenericValue> tmpProductPrices = delegator
					.findList("TmpProductPrice", entityCondition, null, null,
							null, false);

			Debug.logInfo(
					"Find the price changed products which passed, count="
							+ tmpProductPrices.size(), module);
			for (GenericValue tmpProductPrice : tmpProductPrices) {
				// 生效日期已到
				if (UtilDateTime.nowDate().after(
						tmpProductPrice.getTimestamp("fromDate"))) {

					GenericValue product = tmpProductPrice.getRelatedOne(
							"Product", false);
					if (null != product) {
						// 修改商品价格
						List<GenericValue> productPrices = product.getRelated(
								"ProductPrice", null, null, false);
						for (GenericValue productPrice : productPrices) {
							productPrice.set("price",
									tmpProductPrice.getBigDecimal("tmpPrice"));
							productPrice.store();
						}

						// 修改临时商品价格数据的状态
						tmpProductPrice.set("checkState", "_NA_");
						tmpProductPrice.store();
					}
				}

			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"PartyCannotCreateRoleTypeEntity",
					UtilMisc.toMap("errMessage", e.getMessage()), locale));
		}

		result = ServiceUtil.returnSuccess();
		return result;

	}

}
