   
  <div class="container">
    <div class="detail-page">
      <div class="detail-section">  
        <h3>个人信息</h3>
        <div class="row detail-row">
          <div class="span8">
            <label>用户账号：</label><span class="detail-text">${profileVO.loginId}</span>
          </div>
          <div class="span">
            <img src= "http://blog.erhsh.com/img/logo.png"/>
          </div>
        </div>
        <div class="row detail-row">
          <div class="span8">
            <label>用户姓名：</label><span class="detail-text">${profileVO.nickname}</span>
          </div>
        </div>
        <div class="row detail-row">
           <div class="span8">
            <label>联系手机：</label><span class="detail-text">${profileVO.telNum}</span>
          </div>
        </div>
        <div class="row detail-row">
          <div class="span8">
            <label>联系座机：</label><span class="detail-text">${profileVO.telNum2}</span>
          </div>
        </div>
        <div class="row detail-row">
          <div class="span8">
            <label>身份证号：</label><span class="detail-text">${profileVO.cardId}</span>
          </div>
        </div>
        <div class="row detail-row">
           <div class="span8">
            <label>家庭住址：</label><span class="detail-text">${profileVO.addr}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <a href="/erpec/users/control/editProfile" >编辑</a>