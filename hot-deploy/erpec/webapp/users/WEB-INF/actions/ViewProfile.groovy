import javax.servlet.http.HttpSession

import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericValue;
import org.ofbiz.erpec.pojo.ProfileVO
import org.ofbiz.erpec.pojo.RoleVO

GenericDelegator delegator = delegator;

GenericValue userLogin = userLogin;

if (parameters.userLoginId != null) {
	userLogin = delegator.findOne("UserLogin", ["userLoginId":parameters.userLoginId], false);
}


String userLoginId = userLogin.userLoginId;
String partyId = userLogin.partyId;

GenericValue party = delegator.findOne("Party", ["partyId":partyId], false);

// 用户名，身份证号
String nickname = "";
String cardId = "";
String desc = party.description == null ? "" : party.description;
GenericValue person = party.getRelatedOne("Person", false);
nickname = person.nickname == null ? "" : person.nickname;
cardId = person.cardId == null ? "" : person.cardId;

// 联系方式, 住址
String telNum = "";
String addr = "";

List<GenericValue> partyContactMechs = person.getRelated("PartyContactMech", null, null, false);
for (partyContactMech in partyContactMechs) {
	GenericValue contactMech = partyContactMech.getRelatedOne("ContactMech", false);

	// 电话号
	if ("TELECOM_NUMBER".equals(contactMech.contactMechTypeId)) {
		GenericValue telecomNumber = contactMech.getRelatedOne("TelecomNumber", false);
		telNum = telecomNumber.contactNumber;
	}

}

ProfileVO profileVO = new ProfileVO();

profileVO.setLoginId(userLoginId);
profileVO.setNickname(nickname==null?"":nickname);
profileVO.setTelNum(telNum==null?"":telNum)
profileVO.setTelNum2("");
profileVO.setCardId(cardId==null?"":cardId)
profileVO.setAddr(addr==null?"":desc);

// 角色
List<GenericValue> userLoginSecurityGroups = userLogin.getRelated("UserLoginSecurityGroup", null, null, false);
List<RoleVO> roleVOs = new ArrayList<RoleVO>();
for (userLoginSecurityGroup in userLoginSecurityGroups) {
	GenericValue securityGroup = userLoginSecurityGroup.getRelatedOne("SecurityGroup", false);

	RoleVO roleVO = new RoleVO();

	roleVO.setRoleId(securityGroup.groupId);
	roleVO.setRoleDesc(securityGroup.description);
	roleVOs.add(roleVO);
}
profileVO.setRoleVOs(roleVOs);


context.profileVO = profileVO;