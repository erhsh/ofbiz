package org.ofbiz.erpec.models;

import java.util.ArrayList;
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

	public static List<SecurityGroupType> mock() {
		List<SecurityGroupType> result = new ArrayList<SecurityGroupType>();

		SecurityGroupType sgt = new SecurityGroupType();

		sgt.setSecurityGroupTypeId("aaa");
		sgt.setDescription("aaaaaaaaa");

		SecurityGroupType sgt1 = new SecurityGroupType();

		sgt1.setSecurityGroupTypeId("111");
		sgt1.setDescription("11111111111111");
		sgt1.setParentTypeId("aaa");

		SecurityGroupType sgt2 = new SecurityGroupType();

		sgt2.setSecurityGroupTypeId("222");
		sgt2.setDescription("22222222222222");
		sgt2.setParentTypeId("aaa");

		List<SecurityGroupType> childTypes = new ArrayList<SecurityGroupType>();

		childTypes.add(sgt1);
		childTypes.add(sgt2);

		sgt.setChildTypes(childTypes);

		result.add(sgt);

		return result;
	}

	public static void main(String[] args) {
		System.out.println(SecurityGroupType.mock());
	}
}
