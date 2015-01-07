package org.ofbiz.erpec.pojo;

import java.util.List;

/**
 * 用户信息值对象
 * 
 * 作为前后端角色数据交换载体
 * 
 * @author j
 *
 */
public class UserInfoVO {

	private String userId;
	private String userLoginId;
	private String userName;
	private String userCardId;
	private String userTelNum;
	private String userDesc;
	private String userStat;
	private List<String> userRoles;
	private List<RoleVO> userRoleVOs;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserCardId() {
		return userCardId;
	}

	public void setUserCardId(String userCardId) {
		this.userCardId = userCardId;
	}

	public String getUserTelNum() {
		return userTelNum;
	}

	public void setUserTelNum(String userTelNum) {
		this.userTelNum = userTelNum;
	}

	public String getUserDesc() {
		return userDesc;
	}

	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}

	public String getUserStat() {
		return userStat;
	}

	public void setUserStat(String userStat) {
		this.userStat = userStat;
	}

	public List<String> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<String> userRoles) {
		this.userRoles = userRoles;
	}

	public List<RoleVO> getUserRoleVOs() {
		return userRoleVOs;
	}

	public void setUserRoleVOs(List<RoleVO> userRoleVOs) {
		this.userRoleVOs = userRoleVOs;
	}

	@Override
	public String toString() {
		return "UserInfoVO [userId=" + userId + ", userLoginId=" + userLoginId
				+ ", userName=" + userName + ", userCardId=" + userCardId
				+ ", userTelNum=" + userTelNum + ", userDesc=" + userDesc
				+ ", userStat=" + userStat + ", userRoles=" + userRoles
				+ ", userRoleVOs=" + userRoleVOs + "]";
	}

}
