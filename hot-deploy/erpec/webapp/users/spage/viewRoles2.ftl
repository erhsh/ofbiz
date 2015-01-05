<div class="container">
	<div class="row">
      <form id="searchForm" class="form-horizontal span24">
        <div class="row">
          <div class="control-group span8">
            <label class="control-label">业务分组：</label>
            <div class="controls">
              <select name="roleGrpId" class="input-normal"> 
                <option value="">请选择</option>              
                <#list securityGroupTypes as securityGroupType>
					<#list securityGroupType.childTypes as childType>
						<option value="${childType.securityGroupTypeId}">${securityGroupType.description}>${childType.description}</option>
					</#list>
				</#list>
              </select>
            </div>
          </div>
          <div class="span3 offset2">
            <button type="button" id="btnSearch" class="button button-primary">搜索</button>
          </div>
        </div>
      </form>
    </div>
    <div class="search-grid-container">
      <div id="grid"></div>
    </div>
</div>

<div id="content" class="hide">
      <form id="J_Form" class="form-horizontal" action="../data/edit.php?a=1">
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
            <div id="range" class="controls bui-form-group" style="width: 460px; height: 200px; overflow: scroll;">
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
  BUI.use('common/search',function (Search) {
    
    var enumObj = {"1":"男","0":"女"},
      editing = new BUI.Grid.Plugins.DialogEditing({
        contentId : 'content', //设置隐藏的Dialog内容
        autoSave : true, //添加数据或者修改数据时，自动保存
        triggerCls : 'btn-edit'
      }),
      columns = [
          {title:'角色编号',dataIndex:'roleId',width:150},
          {title:'角色名称',dataIndex:'roleName',width:200},
          {title:'业务分组',dataIndex:'roleGrp',width:200},
          {title:'角色描述',dataIndex:'roleDesc',width:400},
          {title:'状态',dataIndex:'roleStat',width:50},
          {title:'操作',dataIndex:'',width:100,renderer : function(value,obj){
            var runStr = '<span class="grid-command btn-open" title="启用角色">启用</span>',
              stopStr = '<span class="grid-command btn-close" title="停用角色">停用</span>';
            return runStr + stopStr;
          }
          },
          {title:'编辑',dataIndex:'',width:100,renderer : function(value,obj){
            var editStr = '<span class="grid-command btn-edit" title="编辑角色信息">编辑</span>';
            return editStr;
          }
          }
        ],
      store = Search.createStore('/erpec/users/control/ajaxRoleLst',{
        proxy : {
          save : { //也可以是一个字符串，那么增删改，都会往那么路径提交数据，同时附加参数saveType
            addUrl : '/erpec/users/control/ajaxRoleAdd',
            updateUrl : '../data/edit.json',
            removeUrl : '../data/del.php'
          },
          method : 'POST'
        },
        autoSync : true //保存数据后，自动更新
      }),
      gridCfg = Search.createGridCfg(columns,{
        tbar : {
          items : [
            {text : '<i class="icon-plus"></i>新建',btnCls : 'button button-small',handler:addFunction}
          ]
        },
        plugins : [editing,BUI.Grid.Plugins.CheckSelection,BUI.Grid.Plugins.AutoFit] // 插件形式引入多选表格
      });

    var search = new Search({
        store : store,
        gridCfg : gridCfg
      }),
      grid = search.get('grid');

    function addFunction(){
      var newData = {isNew : true}; //标志是新增加的记录
      editing.add(newData,'name'); //添加记录后，直接编辑
    }

    //删除操作
    function delFunction(){
      var selections = grid.getSelection();
      delItems(selections);
    }

    function delItems(items){
      var ids = [];
      BUI.each(items,function(item){
        ids.push(item.id);
      });

      if(ids.length){
        BUI.Message.Confirm('确认要删除选中的记录么？',function(){
          store.save('remove',{ids : ids});
        },'question');
      }
    }

    //监听事件，删除一条记录
    grid.on('cellclick',function(ev){
      var sender = $(ev.domTarget); //点击的Dom
      if(sender.hasClass('btn-del')){
        var record = ev.record;
        delItems([record]);
      }
    });
  });
</script>