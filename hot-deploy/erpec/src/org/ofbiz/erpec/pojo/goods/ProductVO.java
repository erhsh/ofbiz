package org.ofbiz.erpec.pojo.goods;

public class ProductVO {
	private String prodId;// 编号
	private String prodName;// 品名规格
	private String prodModel;// 型号
	private String prodPrice;// 价格
	private String prodState;// 状态
	private String prodUnit;// 计量单位
	private String prodCostPrice;// 原价
	private String prodCategory;// 分类
	private String prodLightColor;// 发光颜色
	private String prodColor;// 颜色
	private String prodLength;// 长
	private String prodWidth;// 宽
	private String prodHeight;// 高
	private String prodWeight;// 重

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getProdModel() {
		return prodModel;
	}

	public void setProdModel(String prodModel) {
		this.prodModel = prodModel;
	}

	public String getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(String prodPrice) {
		this.prodPrice = prodPrice;
	}

	public String getProdState() {
		return prodState;
	}

	public void setProdState(String prodState) {
		this.prodState = prodState;
	}

	public String getProdUnit() {
		return prodUnit;
	}

	public void setProdUnit(String prodUnit) {
		this.prodUnit = prodUnit;
	}

	public String getProdCostPrice() {
		return prodCostPrice;
	}

	public void setProdCostPrice(String prodCostPrice) {
		this.prodCostPrice = prodCostPrice;
	}

	public String getProdCategory() {
		return prodCategory;
	}

	public void setProdCategory(String prodCategory) {
		this.prodCategory = prodCategory;
	}

	public String getProdLightColor() {
		return prodLightColor;
	}

	public void setProdLightColor(String prodLightColor) {
		this.prodLightColor = prodLightColor;
	}

	public String getProdColor() {
		return prodColor;
	}

	public void setProdColor(String prodColor) {
		this.prodColor = prodColor;
	}

	public String getProdLength() {
		return prodLength;
	}

	public void setProdLength(String prodLength) {
		this.prodLength = prodLength;
	}

	public String getProdWidth() {
		return prodWidth;
	}

	public void setProdWidth(String prodWidth) {
		this.prodWidth = prodWidth;
	}

	public String getProdHeight() {
		return prodHeight;
	}

	public void setProdHeight(String prodHeight) {
		this.prodHeight = prodHeight;
	}

	public String getProdWeight() {
		return prodWeight;
	}

	public void setProdWeight(String prodWeight) {
		this.prodWeight = prodWeight;
	}

	@Override
	public String toString() {
		return "ProductVO [prodId=" + prodId + ", prodName=" + prodName
				+ ", prodModel=" + prodModel + ", prodPrice=" + prodPrice
				+ ", prodState=" + prodState + ", prodUnit=" + prodUnit
				+ ", prodCostPrice=" + prodCostPrice + ", prodCategory="
				+ prodCategory + ", prodLightColor=" + prodLightColor
				+ ", prodColor=" + prodColor + ", prodLength=" + prodLength
				+ ", prodWidth=" + prodWidth + ", prodHeight=" + prodHeight
				+ ", prodWeight=" + prodWeight + "]";
	}

}
