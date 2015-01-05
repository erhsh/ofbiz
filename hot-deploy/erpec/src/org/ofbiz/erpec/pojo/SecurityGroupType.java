package org.ofbiz.erpec.pojo;

import java.util.List;

public class SecurityGroupType {
	private String securityGroupTypeId;

	private String parentTypeId;

	private String description;

	private List<SecurityGroupType> childTypes;

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

	public List<SecurityGroupType> getChildTypes() {
		return childTypes;
	}

	public void setChildTypes(List<SecurityGroupType> childTypes) {
		this.childTypes = childTypes;
	}

}
