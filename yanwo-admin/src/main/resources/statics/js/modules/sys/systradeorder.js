$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/systradeorder/list',
        datatype: "json",
        colModel: [			
			{ label: 'oid', name: 'oid', index: 'oid', width: 50, key: true },
			{ label: '订单编号', name: 'tid', index: 'tid', width: 80 }, 			
			{ label: '商品id', name: 'itemId', index: 'item_id', width: 80 }, 			
			{ label: '商品标题', name: 'title', index: 'title', width: 80 }, 			
			{ label: '商品价格', name: 'price', index: 'price', width: 80 }, 			
			{ label: '购买数量', name: 'num', index: 'num', width: 80 }, 			
			{ label: '商品图片', name: 'picPath', index: 'pic_path', width: 80 ,formatter:function(cellvalue){
                return "<img src="+cellvalue+" style='width:60px'>";
            }},
			{ label: '买家id', name: 'userId', index: 'user_id', width: 80 }, 			
			{ label: '应付金额', name: 'totalFee', index: 'total_fee', width: 80 }, 			
			{ label: '实付金额', name: 'payment', index: 'payment', width: 80 }, 			
			{ label: '抵扣金额', name: 'welfareFee', index: 'welfare_fee', width: 80 }, 			
			{ label: '', name: 'postFee', index: 'post_fee', width: 80 }, 			
			{ label: '成本价', name: 'costPrice', index: 'cost_price', width: 80 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		systradeOrder: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.systradeOrder = {};
		},
		update: function (event) {
			var oid = getSelectedRow();
			if(oid == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(oid)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.systradeOrder.oid == null ? "sys/systradeorder/save" : "sys/systradeorder/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.systradeOrder),
                    success: function(r){
                        if(r.code === 0){
                             layer.msg("操作成功", {icon: 1});
                             vm.reload();
                             $('#btnSaveOrUpdate').button('reset');
                             $('#btnSaveOrUpdate').dequeue();
                        }else{
                            layer.alert(r.msg);
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        }
                    }
                });
			});
		},
		del: function (event) {
			var oids = getSelectedRows();
			if(oids == null){
				return ;
			}
			var lock = false;
            layer.confirm('确定要删除选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
		            $.ajax({
                        type: "POST",
                        url: baseURL + "sys/systradeorder/delete",
                        contentType: "application/json",
                        data: JSON.stringify(oids),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
                                $("#jqGrid").trigger("reloadGrid");
                            }else{
                                layer.alert(r.msg);
                            }
                        }
				    });
			    }
             }, function(){
             });
		},
		getInfo: function(oid){
			$.get(baseURL + "sys/systradeorder/info/"+oid, function(r){
                vm.systradeOrder = r.systradeOrder;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});