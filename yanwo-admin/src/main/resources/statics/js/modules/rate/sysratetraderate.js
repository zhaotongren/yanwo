$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysratetraderate/list',
        datatype: "json",
        colModel: [			
			{ label: 'rateId', name: 'rateId', index: 'rate_id', width: 50, key: true },
			{ label: '订单号', name: 'tid', index: 'tid', width: 80 }, 			
			{ label: '子订单号', name: 'oid', index: 'oid', width: 80 }, 			
			{ label: '', name: 'userId', index: 'user_id', width: 80 }, 			
			{ label: '描述相符', name: 'describeScore', index: 'describe_score', width: 80 }, 			
			{ label: '', name: 'content', index: 'content', width: 80 }, 			
			{ label: '晒单图片', name: 'ratePic', index: 'rate_pic', width: 80 }, 			
			{ label: '是否匿名', name: 'anony', index: 'anony', width: 80 }, 			
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
		sysrateTraderate: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.sysrateTraderate = {};
		},
		update: function (event) {
			var rateId = getSelectedRow();
			if(rateId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(rateId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.sysrateTraderate.rateId == null ? "sys/sysratetraderate/save" : "sys/sysratetraderate/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.sysrateTraderate),
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
			var rateIds = getSelectedRows();
			if(rateIds == null){
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
                        url: baseURL + "sys/sysratetraderate/delete",
                        contentType: "application/json",
                        data: JSON.stringify(rateIds),
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
		getInfo: function(rateId){
			$.get(baseURL + "sys/sysratetraderate/info/"+rateId, function(r){
                vm.sysrateTraderate = r.sysrateTraderate;
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