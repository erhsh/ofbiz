package org.ofbiz.erpec.pojo;

import java.util.List;

/**
 * 角色值对象
 * 
 * 作为前后端角色数据交换载体
 * 
 * @author j
 *
 */
public class RoleVO {

	private String roleId;
	private String roleName;
	private String roleGrp;
	private String roleDesc;
	private String roleStat;
	private List<String> rolePerms;
	private List<SecurityPermissionVO> rolePermVOs;

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

	public List<String> getRolePerms() {
		return rolePerms;
	}

	public void setRolePerms(List<String> rolePerms) {
		this.rolePerms = rolePerms;
	}

	public List<SecurityPermissionVO> getRolePermVOs() {
		return rolePermVOs;
	}

	public void setRolePermVOs(List<SecurityPermissionVO> rolePermVOs) {
		this.rolePermVOs = rolePermVOs;
	}

	@Override
	public String toString() {
		return "RoleVO [roleId=" + roleId + ", roleName=" + roleName
				+ ", roleGrp=" + roleGrp + ", roleDesc=" + roleDesc
				+ ", roleStat=" + roleStat + ", rolePerms=" + rolePerms
				+ ", rolePermVOs=" + rolePermVOs + "]";
	}

}
