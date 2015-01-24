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

<div id="content" class="hide">
      <form id="J_Form" class="form-horizontal" action="../data/edit.php?a=1">
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
              <select data-rules="{required:true}" name="prodCategoryId" class="input-normal"> 
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
      editing = new BUI.Grid.Plugins.DialogEditing({
        contentId : 'content', //设置隐藏的Dialog内容
        autoSave : true, //添加数据或者修改数据时，自动保存
        triggerCls : 'btn-edit'
      }),
      columns = [
          {title:'商品ID',dataIndex:'prodId',width:80},
          {title:'商品编号',dataIndex:'prodCode',width:150},
          {title:'品名规格',dataIndex:'prodName',width:150},
          {title:'商品型号',dataIndex:'prodModel',width:150},
          {title:'分销售价',dataIndex:'prodPrice',width:100},
          {title:'商品分类',dataIndex:'prodCategory',width:100,renderer:BUI.Grid.Format.enumRenderer(enumObj)},
          {title:'状态',dataIndex:'prodState',width:100,renderer:BUI.Grid.Format.enumRenderer(enumState)},
          {title:'操作',dataIndex:'',width:200,renderer : function(value,obj){
            var passStr = '<span class="grid-command btn-pass" title="审核通过">审核通过</span>',
              rejectStr = '<span class="grid-command btn-reject" title="打回申请">打回申请</span>';
            return passStr + rejectStr;
          }}
        ],
      store = Search.createStore('/erpec/goods/control/ajaxGoodsCheckLst',{
        proxy : {
          save : {
            passUrl : '/erpec/goods/control/ajaxGoodsCheckPass',
            rejectUrl : '/erpec/goods/control/ajaxGoodsCheckReject'
          },
          method : 'POST'
        },
        autoSync : true //保存数据后，自动更新
      }),
      gridCfg = Search.createGridCfg(columns,{
        tbar : {
          items : []
        },
        plugins : [editing, BUI.Grid.Plugins.AutoFit] // 插件形式引入多选表格
      });

    var search = new Search({
        store : store,
        gridCfg : gridCfg
      }),
      grid = search.get('grid');

	// 通过    
    function passItems(items){
      var prodIds = [];
      var prodStates = [];
      BUI.each(items,function(item){
        prodIds.push(item.prodId);
        prodStates.push(item.prodState);
      });

      if(prodIds.length){
        BUI.Message.Confirm('确认要通过选中的记录么？',function(){
          store.save('pass',{prodIds : prodIds, prodStates : prodStates});
        },'question');
      }
    }
    
    // 打回
    function rejectItems(items){
      var prodIds = [];
      var prodStates = [];
      BUI.each(items,function(item){
        prodIds.push(item.prodId);
        prodStates.push(item.prodState);
      });

      if(prodIds.length){
        BUI.Message.Confirm('确认要打回选中的记录么？',function(){
          store.save('reject',{prodIds : prodIds, prodStates : prodStates});
        },'question');
      }
    }

    //审核通过
    grid.on('cellclick',function(ev){
      var sender = $(ev.domTarget); //点击的Dom
      if(sender.hasClass('btn-pass')){
        var record = ev.record;
        passItems([record]);
      }
    });

    //审核打回
    grid.on('cellclick',function(ev){
      var sender = $(ev.domTarget); //点击的Dom
      if(sender.hasClass('btn-reject')){
        var record = ev.record;
        rejectItems([record]);
      }
    });
  });
</script>