  <div class="container">
    <h3>个人信息</h3>
    <form id="J_Form" class="form-horizontal" action="/erpec/users/control/profileEdt" method="post">
      	<div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>用户账号：</label>
            <div class="controls">
              <input name="loginId" type="text" class="input-normal control-text" value="${profileVO.loginId}">
            </div>
          </div>
          <div class="control-group span8">
            <img src= "http://blog.erhsh.com/img/logo.png"/>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>用户姓名：</label>
            <div class="controls">
              <input name="nickname" type="text" class="input-normal control-text" value="${profileVO.nickname}">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>联系手机：</label>
            <div class="controls">
              <input name="telNum" type="text" class="input-normal control-text" value="${profileVO.telNum}">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>联系座机：</label>
            <div class="controls">
              <input name="telNum2" type="text" class="input-normal control-text" value="${profileVO.telNum2}">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>身份证号：</label>
            <div class="controls">
              <input name="cardId" type="text" class="input-normal control-text" value="${profileVO.cardId}">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>家庭住址：</label>
            <div class="controls">
              <input name="addr" type="text" class="input-normal control-text" value="${profileVO.addr}">
            </div>
          </div>
        </div>
    </form>
  </div>
  
  <a href="/erpec/users/control/viewProfile" >返回</a>
  <a href="#" onclick="javascript:document.getElementById('J_Form').submit()" >保存</a>