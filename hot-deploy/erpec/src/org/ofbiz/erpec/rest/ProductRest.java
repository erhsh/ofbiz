package org.ofbiz.erpec.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GroovyUtil;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.erpec.rest.pojo.ProductInfoVO;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceDispatcher;

/**
 * 商品信息获取API<br/>
 * 
 * 根据商品Id，获取商品详细/基本信息/价格信息/评论信息
 * 
 * 1. 商品详细信息>通过电商前端页面展示的所有数据集，但信息太杂，只适合参考，用到那些数据需要具体适配，参照本类的基本信息实现方法即可。<br>
 * 2. 基本信息>展示商品名称、描述、大小、重量、图片url等基本信息，属于详细信息的一个子信息集。<br>
 * 3. 价格信息>商品的价格，如果只有一种价格可以合并到基本信息里，抽出来是为了应对多价格.<br>
 * 4. 评论信息>商品的所有
 * 
 * @author j
 * 
 */
@Path("product")
public class ProductRest {

	public static final String module = ProductRest.class.getName();

	private GenericDelegator delegator = (GenericDelegator) DelegatorFactory
			.getDelegator("default");

	private LocalDispatcher dispatcher = ServiceDispatcher.getLocalDispatcher(
			"default", delegator);

	/**
	 * 构建运行Groovy脚本运行上下文环境
	 * 
	 * @param request
	 * @return
	 */
	private FastMap<String, Object> buildContext(HttpServletRequest request) {
		FastMap<String, Object> context = FastMap.newInstance();
		context.put("request", request);
		context.put("delegator", delegator);
		context.put("dispatcher", dispatcher);
		context.put("security", request.getAttribute("security"));
		context.put("session", request.getSession());
		context.put("userLogin", request.getAttribute("userLogin"));
		Map<String, Object> requestParameters = UtilHttp
				.getParameterMap(request);
		context.put("requestParameters", requestParameters);

		context.put("parameters", requestParameters);
		return context;
	}

	/**
	 * 数据转Json串通用方法
	 * 
	 * @param data
	 * @return
	 */
	private String getJsonData(Object data) {

		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(StringUtil.StringWrapper.class,
				new JsonValueProcessor() {

					@Override
					public Object processObjectValue(String arg0, Object arg1,
							JsonConfig arg2) {
						return arg1.toString();
					}

					@Override
					public Object processArrayValue(Object arg0, JsonConfig arg1) {
						return null;
					}
				});
		config.setIgnoreDefaultExcludes(true);
		config.setIgnoreTransientFields(true);
		config.setExcludes(new String[] { "request", "session", "delegator",
				"dispatcher", "userLogin", "security", "class" });

		JSONArray JsonCataLog = JSONArray.fromObject(data, config);

		return JsonCataLog.toString();
	}

	/**
	 * 获取商品详细信息
	 * 
	 * @param request
	 * @param productId
	 * @return
	 */
	@GET
	@Path("info/detail")
	@Produces("application/json")
	public String getProductInfoDetail(@Context HttpServletRequest request,
			@QueryParam("productId") String productId) {

		FastMap<String, Object> context = buildContext(request);
		boolean beganTransaction = false;
		try {

			beganTransaction = TransactionUtil.begin();
			GenericValue product = delegator.findOne("Product",
					UtilMisc.toMap("productId", productId), false);
			context.put("product", product);
			GroovyUtil
					.runScriptAtLocation(
							"component://order/webapp/ordermgr/WEB-INF/actions/entry/catalog/ProductDetail.groovy",
							null, context);

		} catch (GeneralException e) {
			Debug.logError(e, "error happen for productId" + productId, module);
			try {
				TransactionUtil.rollback(beganTransaction,
						"Failure in operation rolling back transaction", e);
			} catch (GenericTransactionException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				TransactionUtil.commit(beganTransaction);
			} catch (GenericTransactionException e) {
				e.printStackTrace();
			}
		}

		return getJsonData(context);

	}

	/**
	 * 获取商品基本信息
	 * 
	 * @param request
	 * @param productId
	 * @return
	 */
	@GET
	@Path("info/base")
	@Produces("application/json")
	public String getProductInfoBase(@Context HttpServletRequest request,
			@QueryParam("productId") String productId) {

		FastMap<String, Object> context = buildContext(request);
		boolean beganTransaction = false;
		try {
			beganTransaction = TransactionUtil.begin();
			GenericValue product = delegator.findOne("Product",
					UtilMisc.toMap("productId", productId), false);
			context.put("product", product);
			GroovyUtil
					.runScriptAtLocation(
							"component://order/webapp/ordermgr/WEB-INF/actions/entry/catalog/ProductDetail.groovy",
							null, context);

		} catch (GeneralException e) {
			Debug.logError(e, "error happen for productId" + productId, module);
			try {
				TransactionUtil.rollback(beganTransaction,
						"Failure in operation rolling back transaction", e);
			} catch (GenericTransactionException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				TransactionUtil.commit(beganTransaction);
			} catch (GenericTransactionException e) {
				e.printStackTrace();
			}
		}

		Object obj = context.get("product");

		if (obj instanceof GenericValue) {
			GenericValue prod = (GenericValue) obj;
			ProductInfoVO prodInfoVO = new ProductInfoVO();

			prodInfoVO.setProductId(prod.getString("productId"));
			prodInfoVO.setProductName(prod.getString("productName"));
			prodInfoVO.setDescription(prod.getString("description"));
			prodInfoVO.setLongDescription(prod.getString("longDescription"));
			prodInfoVO.setProductTypeId(prod.getString("productTypeId"));
			prodInfoVO.setDescription(prod.getString("description"));

			prodInfoVO.setProductRating(prod.getString("productRating"));
			prodInfoVO.setRatingTypeEnum(prod.getString("ratingTypeEnum"));

			prodInfoVO.setProductHeight(prod.getString("productHeight"));
			prodInfoVO.setProductWidth(prod.getString("productWidth"));
			prodInfoVO.setProductDepth(prod.getString("productDepth"));
			prodInfoVO.setProductWeight(prod.getString("weight"));
			prodInfoVO.setProductDiameter(prod.getString("productDiameter"));

			prodInfoVO.setOriginalImageUrl(prod.getString("originalImageUrl"));
			prodInfoVO.setDetailImageUrl(prod.getString("detailImageUrl"));
			prodInfoVO.setLargeImageUrl(prod.getString("largeImageUrl"));
			prodInfoVO.setMediumImageUrl(prod.getString("mediumImageUrl"));
			prodInfoVO.setSmallImageUrl(prod.getString("smallImageUrl"));

			System.out.println(prodInfoVO);
			return getJsonData(prodInfoVO);
		}

		return getJsonData(context.get("product"));

	}

	/**
	 * 获取商品价格信息
	 * 
	 * @param request
	 * @param productId
	 * @return
	 */
	@GET
	@Path("info/prices")
	@Produces("application/json")
	public String getProductInfoPrice(@Context HttpServletRequest request,
			@QueryParam("productId") String productId) {

		FastMap<String, Object> context = buildContext(request);
		boolean beganTransaction = false;
		try {
			beganTransaction = TransactionUtil.begin();
			GenericValue product = delegator.findOne("Product",
					UtilMisc.toMap("productId", productId), false);
			context.put("product", product);
			GroovyUtil
					.runScriptAtLocation(
							"component://order/webapp/ordermgr/WEB-INF/actions/entry/catalog/ProductDetail.groovy",
							null, context);

		} catch (GeneralException e) {
			Debug.logError(e, "error happen for productId" + productId, module);
			try {
				TransactionUtil.rollback(beganTransaction,
						"Failure in operation rolling back transaction", e);
			} catch (GenericTransactionException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				TransactionUtil.commit(beganTransaction);
			} catch (GenericTransactionException e) {
				e.printStackTrace();
			}
		}

		return getJsonData(context.get("priceMap"));

	}

	/**
	 * 获取商品评价信息
	 * 
	 * @param request
	 * @param productId
	 * @return
	 */
	@GET
	@Path("info/reviews")
	@Produces("application/json")
	public String getProductInfoReviews(@Context HttpServletRequest request,
			@QueryParam("productId") String productId) {

		FastMap<String, Object> context = buildContext(request);
		boolean beganTransaction = false;
		try {
			beganTransaction = TransactionUtil.begin();
			GenericValue product = delegator.findOne("Product",
					UtilMisc.toMap("productId", productId), false);
			context.put("product", product);
			GroovyUtil
					.runScriptAtLocation(
							"component://order/webapp/ordermgr/WEB-INF/actions/entry/catalog/ProductDetail.groovy",
							null, context);

		} catch (GeneralException e) {
			Debug.logError(e, "error happen for productId" + productId, module);
			try {
				TransactionUtil.rollback(beganTransaction,
						"Failure in operation rolling back transaction", e);
			} catch (GenericTransactionException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				TransactionUtil.commit(beganTransaction);
			} catch (GenericTransactionException e) {
				e.printStackTrace();
			}
		}

		return getJsonData(context.get("productReviews"));

	}
}
