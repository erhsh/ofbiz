package org.ofbiz.erpec.pojo;

public class RoleVO {
	private String roleId;
	private String roleName;
	private String roleGrp;
	private String roleDesc;
	private String roleStat;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleGrp() {
		return roleGrp;
	}

	public void setRoleGrp(String roleGrp) {
		this.roleGrp = roleGrp;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleStat() {
		return roleStat;
	}

	public void setRoleStat(String roleStat) {
		this.roleStat = roleStat;
	}

	@Override
	public String toString() {
		return "RoleVO [roleId=" + roleId + ", roleName=" + roleName
				+ ", roleGrp=" + roleGrp + ", roleDesc=" + roleDesc
				+ ", roleStat=" + roleStat + "]";
	}

}
