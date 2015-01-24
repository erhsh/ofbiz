<div class="container">
	<div class="row">
      <form id="searchForm" class="form-horizontal span24">
        <div class="row">
          <div class="control-group span8">
            <label class="control-label">商品分类：</label>
            <div class="controls">
              <select name="prodCategoryId" class="input-normal"> 
                <option value="">请选择</option>
                <#list productCategoryVOs as productCategoryVO>
					<option value="${productCategoryVO.productCategoryId}">${productCategoryVO.categoryName}</option>
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

<div id="contentNew" class="hide">
      <form id="J_Form" class="form-horizontal" action="../data/edit.php?a=1">
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>商品编码：</label>
            <div class="controls">
              <input name="prodCode" type="text" data-rules="{required:true}" class="input-normal control-text">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>品名规格：</label>
            <div class="controls">
              <input name="prodName" type="text" data-rules="{required:true}" class="input-normal control-text">
            </div>
          </div>
          <div class="control-group span8">
            <label class="control-label"><s>*</s>商品型号：</label>
            <div class="controls">
              <input name="prodModel" type="text" data-rules="{required:true}" class="input-normal control-text">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>分销价格：</label>
            <div class="controls">
              <input name="prodPrice" type="text" data-rules="{required:true}" class="input-normal control-text">
            </div>
          </div>
          <div class="control-group span8">
            <label class="control-label"><s>*</s>商品分类：</label>
            <div class="controls">
              <select data-rules="{required:true}" name="prodCategory" class="input-normal"> 
                <option value="">请选择</option>
                <#list productCategoryVOs as productCategoryVO>
					<option value="${productCategoryVO.productCategoryId}">${productCategoryVO.categoryName}</option>
				</#list>
              </select>
            </div>
          </div>
        </div>
      </form>
    </div>
</div>

<div id="contentEdit" class="hide">
      <form id="J_Form" class="form-horizontal" action="../data/edit.php?a=1">
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>商品编码：</label>
            <div class="controls">
              <input name="prodCode" type="text" class="input-normal control-text" readonly="true">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>品名规格：</label>
            <div class="controls">
              <input name="prodName" type="text" data-rules="{required:true}" class="input-normal control-text">
            </div>
          </div>
          <div class="control-group span8">
            <label class="control-label"><s>*</s>商品型号：</label>
            <div class="controls">
              <input name="prodModel" type="text" data-rules="{required:true}" class="input-normal control-text">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>分销价格：</label>
            <div class="controls">
              <input name="prodPrice" type="text" class="input-normal control-text" readonly="true">
            </div>
          </div>
          <div class="control-group span8">
            <label class="control-label"><s>*</s>商品分类：</label>
            <div class="controls">
              <select data-rules="{required:true}" name="prodCategory" class="input-normal"> 
                <option value="">请选择</option>
                <#list productCategoryVOs as productCategoryVO>
					<option value="${productCategoryVO.productCategoryId}">${productCategoryVO.categoryName}</option>
				</#list>
              </select>
            </div>
          </div>
        </div>
      </form>
    </div>
</div>

<div id="contentPrice" class="hide">
      <form id="J_Form" class="form-horizontal" action="../data/edit.php?a=1">
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>商品价格：</label>
            <div class="controls">
              <input name="prodPrice" type="text" data-rules="{required:true}" class="input-normal control-text">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>生效日期：</label>
            <div class="controls">
              <input type="text" class="calendar" data-rules="{required:true}" name="prodPriceFromDate">
            </div>
          </div>
        </div>
        <div class="row">
          <div class="control-group span8">
            <label class="control-label"><s>*</s>勾选一下：</label>
            <div class="controls">
              <input type="radio" name="isPrice" data-rules="{required:true}" />
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
  var enumObj = {
				<#list productCategoryVOs as productCategoryVO>
					"${productCategoryVO.productCategoryId}":"${productCategoryVO.categoryName}",
				</#list>
  	  },
  	  enumState = {
				"NEW":"新建未提交",
				"CHECKING":"新建审核中",
				"ENABLED":"架上",
				"DISABLED":"架下",
  	  },
  	  editingNew = new BUI.Grid.Plugins.DialogEditing({
        contentId : 'contentNew', //设置隐藏的Dialog内容
        autoSave : true, //添加数据或者修改数据时，自动保存
      }),
      editingEdit = new BUI.Grid.Plugins.DialogEditing({
        contentId : 'contentEdit', //设置隐藏的Dialog内容
        autoSave : true, //添加数据或者修改数据时，自动保存
        triggerCls : 'btn-edit'
      }),
      editingPrice = new BUI.Grid.Plugins.DialogEditing({
        contentId : 'contentPrice', //设置隐藏的Dialog内容
        autoSave : true, //添加数据或者修改数据时，自动保存
        triggerCls : 'btn-edit-price'
      }),

      columns = [
          {title:'商品ID',dataIndex:'prodId',width:80},
          {title:'商品编码',dataIndex:'prodCode',width:150},
          {title:'品名规格',dataIndex:'prodName',width:150},
          {title:'商品型号',dataIndex:'prodModel',width:150},
          {title:'分销售价',dataIndex:'prodPrice',width:100},
          {title:'商品分类',dataIndex:'prodCategory',width:100,renderer:BUI.Grid.Format.enumRenderer(enumObj)},
          {title:'状态',dataIndex:'prodState',width:100,renderer:BUI.Grid.Format.enumRenderer(enumState)},
          {title:'操作',dataIndex:'',width:100,renderer : function(value,obj){
            var enableStr = '<span class="grid-command btn-enable" title="商品上架">上架</span>',
              disableStr = '<span class="grid-command btn-disable" title="商品下架">下架</span>';
            return enableStr + disableStr;
          }},
          {title:'编辑',dataIndex:'',width:100,renderer : function(value,obj){
            var editParam = '<span class="grid-command btn-edit" title="编辑商品参数">编辑参数</span>',
            editPrice = '<span class="grid-command btn-edit-price" title="调整商品价格">调价</span>';
            return editParam + editPrice;
          }},
          {title:'Other',dataIndex:'',width:200,renderer : function(value,obj){
            var submitParam = '<span class="grid-command btn-submit" title="提交新建数据">提交新建</span>',
            submitEditParam = '<span class="grid-command btn-submit-edit" title="提交编辑数据">提交编辑</span>',
            delParam = '<span class="grid-command btn-del" title="删除商品">删除</span>';
            return submitParam + submitEditParam + delParam;
          }}
        ],
      store = Search.createStore('/erpec/goods/control/ajaxGoodsLst',{
        proxy : {
          save : { //也可以是一个字符串，那么增删改，都会往那么路径提交数据，同时附加参数saveType
            addUrl : '/erpec/goods/control/ajaxGoodsAdd',
            updateUrl : '/erpec/goods/control/ajaxGoodsEdt',
            removeUrl : '/erpec/goods/control/ajaxGoodsDel',
            submitUrl : '/erpec/goods/control/ajaxGoodsSubmit',
            submitEditUrl : '/erpec/goods/control/ajaxGoodsSubmitEdit',
            enableUrl : '/erpec/goods/control/ajaxGoodsEnable',
            disableUrl : '/erpec/goods/control/ajaxGoodsDisable',
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
        plugins : [editingNew, editingEdit, editingPrice, BUI.Grid.Plugins.AutoFit] // 插件形式引入多选表格
      });

    var search = new Search({
        				store : store,
        				gridCfg : gridCfg
      			}),
      	grid = search.get('grid');

    function addFunction(){
      var newData = {isNew : true}; //标志是新增加的记录
      editingNew.add(newData,'name'); //添加记录后，直接编辑
    }

    //删除操作
    function delItems(items){
      var prodIds = [];
      BUI.each(items,function(item){
        prodIds.push(item.prodId);
      });

      if(prodIds.length){
        BUI.Message.Confirm('确认要删除选中的记录么？',function(){
          store.save('remove',{prodIds : prodIds});
        },'question');
      }
    }
    
    //提交操作
    function submitItems(items){
      var prodIds = [];
      BUI.each(items,function(item){
        prodIds.push(item.prodId);
      });

      if(prodIds.length){
        BUI.Message.Confirm('确认要提交选中的记录么？',function(){
          store.save('submit',{prodIds : prodIds});
        },'question');
      }
    }
    
    //提交编辑操作
    function submitEditItems(items){
      var prodIds = [];
      BUI.each(items,function(item){
        prodIds.push(item.prodId);
      });

      if(prodIds.length){
        BUI.Message.Confirm('确认要提交选中的记录的编辑信息么？',function(){
          store.save('submitEdit',{prodIds : prodIds});
        },'question');
      }
    }
    
    //上架操作
    function enableItems(items){
      var prodIds = [];
      BUI.each(items,function(item){
        prodIds.push(item.prodId);
      });

      if(prodIds.length){
        BUI.Message.Confirm('确认要提交选中的记录么？',function(){
          store.save('enable',{prodIds : prodIds});
        },'question');
      }
    }
    
    //下架操作
    function disableItems(items){
      var prodIds = [];
      BUI.each(items,function(item){
        prodIds.push(item.prodId);
      });

      if(prodIds.length){
        BUI.Message.Confirm('确认要提交选中的记录么？',function(){
          store.save('disable',{prodIds : prodIds});
        },'question');
      }
    }

    //监听事件，删除一条记录
    grid.on('cellclick',function(ev){
      var sender = $(ev.domTarget); //点击的Dom
      if(sender.hasClass('btn-del')){
        var record = ev.record;
        delItems([record]);
      }else if(sender.hasClass('btn-submit')){
        var record = ev.record;
        submitItems([record]);
      }else if(sender.hasClass('btn-enable')){
      	var record = ev.record;
        enableItems([record]);
      }else if(sender.hasClass('btn-disable')){
      	var record = ev.record;
        disableItems([record]);
      }else if(sender.hasClass('btn-submit-edit')){
      	var record = ev.record;
        submitEditItems([record]);
      }
      
    });
  });
</script>