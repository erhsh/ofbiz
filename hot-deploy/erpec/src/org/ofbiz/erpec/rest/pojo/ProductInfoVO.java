package org.ofbiz.erpec.rest.pojo;

public class ProductInfoVO {
	//基本信息
	private String productId;
	private String productName;
	private String description;
	private String longDescription;
	private String productTypeId;
	private String productRating;
	private String ratingTypeEnum;

	// 长宽高重直径
	private String productHeight;
	private String productWidth;
	private String productDepth;
	private String productWeight;
	private String productDiameter;

	// 图片路径
	private String originalImageUrl;
	private String detailImageUrl;
	private String largeImageUrl;
	private String mediumImageUrl;
	private String smallImageUrl;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}

	public String getProductRating() {
		return productRating;
	}

	public void setProductRating(String productRating) {
		this.productRating = productRating;
	}

	public String getRatingTypeEnum() {
		return ratingTypeEnum;
	}

	public void setRatingTypeEnum(String ratingTypeEnum) {
		this.ratingTypeEnum = ratingTypeEnum;
	}

	public String getProductHeight() {
		return productHeight;
	}

	public void setProductHeight(String productHeight) {
		this.productHeight = productHeight;
	}

	public String getProductWidth() {
		return productWidth;
	}

	public void setProductWidth(String productWidth) {
		this.productWidth = productWidth;
	}

	public String getProductDepth() {
		return productDepth;
	}

	public void setProductDepth(String productDepth) {
		this.productDepth = productDepth;
	}

	public String getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(String productWeight) {
		this.productWeight = productWeight;
	}

	public String getProductDiameter() {
		return productDiameter;
	}

	public void setProductDiameter(String productDiameter) {
		this.productDiameter = productDiameter;
	}

	public String getOriginalImageUrl() {
		return originalImageUrl;
	}

	public void setOriginalImageUrl(String originalImageUrl) {
		this.originalImageUrl = originalImageUrl;
	}

	public String getDetailImageUrl() {
		return detailImageUrl;
	}

	public void setDetailImageUrl(String detailImageUrl) {
		this.detailImageUrl = detailImageUrl;
	}

	public String getLargeImageUrl() {
		return largeImageUrl;
	}

	public void setLargeImageUrl(String largeImageUrl) {
		this.largeImageUrl = largeImageUrl;
	}

	public String getMediumImageUrl() {
		return mediumImageUrl;
	}

	public void setMediumImageUrl(String mediumImageUrl) {
		this.mediumImageUrl = mediumImageUrl;
	}

	public String getSmallImageUrl() {
		return smallImageUrl;
	}

	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}

}
