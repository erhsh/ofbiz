<div class="header">
    <div class="dl-title">
	  <a href="#" title="文档库地址" target="_blank">
	    <span class="lp-title-port">ERP-EC</span> <span class="dl-title-text">订 单 融 合 系 统</span>
	  </a>
    </div>
	<div class="dl-log">欢迎您，<span class="dl-log-user">**.**@alibaba-inc.com</span><a href="###" title="退出系统" class="dl-log-quit">[退出]</a><a href="http://http://sc.chinaz.com/" title="文档库" class="dl-log-quit">文档库</a>
	</div>
</div>
<div class="content">
    <div class="dl-main-nav">
      <ul id="J_Nav"  class="nav-list ks-clear">
        <li class="nav-item"><div class="nav-item-inner nav-home">首页</div></li>
        <li class="nav-item dl-selected"><div class="nav-item-inner nav-task">任务</div></li>
        <li class="nav-item"><div class="nav-item-inner nav-distributor">分销商</div></li>
        <li class="nav-item"><div class="nav-item-inner nav-storehouse">仓库</div></li>
        <li class="nav-item"><div class="nav-item-inner nav-goods">商品</div></li>
        <li class="nav-item"><div class="nav-item-inner nav-users">用户</div></li>
      </ul>
    </div>
    <ul id="J_NavContent" class="dl-tab-conten">

    </ul>
</div>
   
<script>
BUI.use('common/main',function(){
  var config = [
        {
        id:'main', 
        homePage:'homepage',
        menu:[{
            text:'首页',
            items:[
              {id:'homepage',text:'首页',href:'https://localhost:8443/assets/main/code.html',closeable:false},
            ]
          }]
      },{
        id:'task', 
        homePage:'myTaskUndo',
        menu:[{
            text:'我的任务',
            items:[
              {id:'myTaskUndo',text:'未处理任务',href:'main/menu.html',closeable:false},
              {id:'myTaskOndo',text:'进行中任务',href:'main/dyna-menu.html'},
              {id:'myTaskDone',text:'已完成任务',href:'main/second-menu.html'}
            ]
          }]
      },{
        id:'distributor',
        menu:[{
            text:'分销管理',
            items:[
              {id:'index',text:'分销管理',href:'distributor/index.html'}
              ,{id:'fzxx',text:'发展下线',href:'distributor/fzxx.html'}
            ]
          }]
      },{
        id:'storehouse',
        menu:[{
            text:'仓库管理',
            items:[
              {id:'index',text:'仓库管理',href:'storehouse/index.html'},
            ]
          }]
      },{
        id:'goods',
        menu:[{
            text:'商品管理',
            items:[
              {id:'code',text:'商品管理',href:'goods/index.html'}
            ]
          }]
      },{
        id : 'users',
        homePage:'role-manage',
        menu : [{
            text:'用户管理',
            items:[
              {id:'role-manage',text:'角色管理',href:'/erpec/users/control/viewRoles',closeable:false}
              ,{id:'user-manage',text:'用户管理',href:'/erpec/users/control/viewUsers'}
              ,{id:'profile',text:'个人信息',href:'/erpec/users/control/viewProfile'}
            ]
        }]
      }];
  new PageUtil.MainPage({
    modulesConfig : config
  });
});
</script>