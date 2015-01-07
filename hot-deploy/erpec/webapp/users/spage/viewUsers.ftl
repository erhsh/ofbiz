<div class="container">
    <div class="search-grid-container">
      <div id="grid"></div>
    </div>
</div>

<div id="content" class="hide">
      <form id="J_Form" class="form-horizontal" action="../data/edit.php?a=1">
      	<div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>账号：</label>
            <div class="controls">
              <input name="userLoginId" type="text" class="input-normal control-text">
            </div>
          </div>
          <div class="control-group span8">
            <label class="control-label"><s>*</s>姓名：</label>
            <div class="controls">
              <input name="userName" type="text" class="input-normal control-text">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>身份证：</label>
            <div class="controls">
              <input name="userCardId" type="text" class="input-normal control-text">
            </div>
          </div>
          <div class="control-group span8 ">
            <label class="control-label">联系方式：</label>
            <div class="controls">
              <input name="userTelNum" type="text" class="input-normal control-text">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span15 ">
            <label class="control-label">角色：</label>
            <div id="range" class="controls bui-form-group" style="width: 460px; height: 200px; overflow: scroll;">
              <#list roleVOs as roleVO>
              <label><input name="${roleVO.roleId}_role" type="checkbox" value="${roleVO.roleId}"/>${roleVO.roleDesc?if_exists}</label><br/>
              </#list>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span15">
            <label class="control-label">描述：</label>
            <div class="controls control-row4">
              <textarea name="userDesc" class="input-large" type="text"></textarea>
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
          {title:'账号',dataIndex:'userLoginId',width:150},
          {title:'姓名',dataIndex:'userName',width:150},
          {title:'身份证号',dataIndex:'userCardId',width:150},
          {title:'联系方式',dataIndex:'userTelNum',width:100},
          {title:'描述',dataIndex:'userDesc',width:300},
          {title:'状态',dataIndex:'userStat',width:100},
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
      store = Search.createStore('/erpec/users/control/ajaxUserLst',{
        proxy : {
          save : { 
            addUrl : '/erpec/users/control/ajaxUserAdd',
            updateUrl : '/erpec/users/control/ajaxUserEdt',
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