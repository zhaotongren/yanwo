$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysratescore/list',
        datatype: "json",
        colModel: [			
			{ label: 'tid', name: 'tid', index: 'tid', width: 50, key: true },
			{ label: '用户ID', name: 'userId', index: 'user_id', width: 80 }, 			
			{ label: '店铺ID', name: 'shopId', index: 'shop_id', width: 80 }, 			
			{ label: '服务态度', name: 'attitudeScore', index: 'attitude_score', width: 80 }, 			
			{ label: '物流服务', name: 'logisticsServiceScore', index: 'logistics_service_score', width: 80 }, 			
			{ label: '', name: 'createdTime', index: 'created_time', width: 80 }, 			
			{ label: '', name: 'modifiedTime', index: 'modified_time', width: 80 }, 			
			{ label: '是否有效', name: 'disabled', index: 'disabled', width: 80 }			
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
		sysrateScore: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.sysrateScore = {};
		},
		update: function (event) {
			var tid = getSelectedRow();
			if(tid == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(tid)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.sysrateScore.tid == null ? "sys/sysratescore/save" : "sys/sysratescore/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.sysrateScore),
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
			var tids = getSelectedRows();
			if(tids == null){
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
                        url: baseURL + "sys/sysratescore/delete",
                        contentType: "application/json",
                        data: JSON.stringify(tids),
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
		getInfo: function(tid){
			$.get(baseURL + "sys/sysratescore/info/"+tid, function(r){
                vm.sysrateScore = r.sysrateScore;
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