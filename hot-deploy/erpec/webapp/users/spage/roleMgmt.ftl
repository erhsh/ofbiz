<#list securityGroupTypes as securityGroupType>
	<div>
	${securityGroupType.description}(${securityGroupType.securityGroupTypeId})
	</div>
	<#list securityGroupType.childTypes as childType>
		<a href="<@ofbizUrl>roleMgmt</@ofbizUrl>?securityGroupTypeId=${childType.securityGroupTypeId}">------${childType.description}</a><br/>
	</#list>
</#list>

<br/>


<div class="container">
	<div id="grid"></div>
	<p>
		<button id="btnSave" class="button button-primary">提交</button>
	</p>
</div>

<div id="content" class="hide">
      <form id="J_Form" class="form-horizontal">
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>角色名称：</label>
            <div class="controls">
              <input name="roleName" type="text" data-rules="{required:true}" class="input-normal control-text">
            </div>
          </div>
          <div class="control-group span8">
            <label class="control-label"><s>*</s>业务分组：</label>
            <div class="controls">
              <select data-rules="{required:true}" name="roleGrp" class="input-normal"> 
                <option value="">请选择</option>              
                <#list securityGroupTypes as securityGroupType>
					<#list securityGroupType.childTypes as childType>
						<option value="${childType.securityGroupTypeId}">${securityGroupType.description}>${childType.description}</option>
					</#list>
				</#list>
              </select>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span15 ">
            <label class="control-label">角色权限：</label>
            <div id="range" class="controls bui-form-group" data-rules="{dateRange : true}" style="width: 460px; height: 200px; overflow: scroll;">
              <#list securityPermissions as securityPermission>
              <label><input name="rolePermNum" type="checkbox" value="${securityPermission.permissionId}"/>${securityPermission.description}</label><br/>
              </#list>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span15">
            <label class="control-label">描述：</label>
            <div class="controls control-row4">
              <textarea name="roleDesc" class="input-large" type="text"></textarea>
            </div>
          </div>
        </div>
      </form>
    </div>
</div>



  <script type="text/javascript">
    BUI.use('common/page');
  </script>
  <!-- 仅仅为了显示代码使用，不要在项目中引入使用-->
  <script type="text/javascript" src="/assets/js/prettify.js"></script>
  <script type="text/javascript">
    $(function () {
      prettyPrint();
    });
  </script> 
<script type="text/javascript">
  BUI.use(['bui/grid','bui/data'],function (Grid,Data) {

    var columns = [
    		{title : 'ID',dataIndex :'roleId', width:100},
            {title : '角色名称',dataIndex :'roleName', width:300},
            {title : '业务分组',dataIndex :'roleGrp', width:500},
            {title : '角色权限数量',dataIndex :'rolePermNum',width:200},
            {title : '角色描述',dataIndex :'roleDesc',width:500},
            {title : '操作',renderer : function(){
              return '<span class="grid-command btn-edit">编辑</span>';
            }}
          ],
      //默认的数据
      data = [
      	<#list securityGroups as securityGroup>
		  {roleId:'1',roleName:'${securityGroup.groupId}',roleGrp:'用户权限管理平台>用户管理系统',rolePermNum: '3',roleDesc:'${securityGroup.grpDesc}'},
		</#list>
        {roleId:'1',roleName:'分销商权限管理员',roleGrp:'用户权限管理平台>用户管理系统',rolePermNum: '3',roleDesc:'管理分销商机构用户的角色。'},
        {roleId:'2',roleName:'分销商权限管理员',roleGrp:'用户权限管理平台>用户管理系统',rolePermNum: '3',roleDesc:'管理分销商机构用户的角色。'},
        {roleId:'3',roleName:'分销商权限管理员',roleGrp:'用户权限管理平台>用户管理系统',rolePermNum: '3',roleDesc:'管理分销商机构用户的角色。'}
      ],
      

      store = new Data.Store({
        data:data
      }),
      editing = new Grid.Plugins.DialogEditing({
        contentId : 'content',
        triggerCls : 'btn-edit'
      }),
      grid = new Grid.Grid({
        render : '#grid',
        columns : columns,
        width : 700,
        forceFit : true,
        store : store,
        plugins : [Grid.Plugins.CheckSelection,editing],
        tbar:{
          items : [{
            btnCls : 'button button-small',
            text : '<i class="icon-plus"></i>添加',
            listeners : {
              'click' : addFunction
            }
          },
          {
            btnCls : 'button button-small',
            text : '<i class="icon-remove"></i>删除',
            listeners : {
              'click' : delFunction
            }
          }]
        }

      });
    grid.render();

    function addFunction(){
      var newData = {school :'请输入学校名称'};
      editing.add(newData); //添加记录后，直接编辑
    }

    function delFunction(){
      var selections = grid.getSelection();
      store.remove(selections);
    }
    var logEl = $('#log');
    $('#btnSave').on('click',function(){

      var records = store.getResult();
      logEl.text(BUI.JSON.stringify(records));
    });
  });
</script>