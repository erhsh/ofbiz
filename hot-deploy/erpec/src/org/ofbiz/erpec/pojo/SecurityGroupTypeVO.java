package org.ofbiz.erpec.pojo;

import java.util.List;

public class SecurityGroupTypeVO {

	private String securityGroupTypeId;
	private String parentTypeId;
	private String description;
	private List<SecurityGroupTypeVO> childTypes;

	public String getSecurityGroupTypeId() {
		return securityGroupTypeId;
	}

	public void setSecurityGroupTypeId(String securityGroupTypeId) {
		this.securityGroupTypeId = securityGroupTypeId;
	}

	public String getParentTypeId() {
		return parentTypeId;
	}

	public void setParentTypeId(String parentTypeId) {
		this.parentTypeId = parentTypeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SecurityGroupTypeVO> getChildTypes() {
		return childTypes;
	}

	public void setChildTypes(List<SecurityGroupTypeVO> childTypes) {
		this.childTypes = childTypes;
	}

}
