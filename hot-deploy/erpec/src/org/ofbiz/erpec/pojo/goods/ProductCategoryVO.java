package org.ofbiz.erpec.pojo.goods;

public class ProductCategoryVO {
	private String productCategoryId;

	private String categoryName;

	public String getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(String productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString() {
		return "ProductCategoryVO [productCategoryId=" + productCategoryId
				+ ", categoryName=" + categoryName + "]";
	}

}
