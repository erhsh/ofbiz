package org.ofbiz.erpec.pojo;

import java.util.List;

public class ProfileVO {
	private String loginId;

	private String nickname;

	private String telNum;

	private String telNum2;

	private String cardId;

	private String addr;

	private List<RoleVO> roleVOs;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public String getTelNum2() {
		return telNum2;
	}

	public void setTelNum2(String telNum2) {
		this.telNum2 = telNum2;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public List<RoleVO> getRoleVOs() {
		return roleVOs;
	}

	public void setRoleVOs(List<RoleVO> roleVOs) {
		this.roleVOs = roleVOs;
	}

	@Override
	public String toString() {
		return "ProfileVO [loginId=" + loginId + ", nickname=" + nickname
				+ ", telNum=" + telNum + ", telNum2=" + telNum2 + ", cardId="
				+ cardId + ", addr=" + addr + ", roleVOs=" + roleVOs + "]";
	}

}
