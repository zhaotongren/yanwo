$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysnotifymessage/list',
        datatype: "json",
        colModel: [			
			{ label: '消息编号', name: 'messageId', index: 'message_id', width: 50, key: true },
			{ label: '消息标题', name: 'title', index: 'title', width: 80 }, 			
			{ label: '消息内容', name: 'content', index: 'content', width: 80},
			{ label: '消息类型', name: 'type', index: 'type', width: 80 ,formatter:function(cellvalue){
                if(cellvalue == '1'){
                    return "系统消息";
                }else if(cellvalue == '2'){
                    return "物流消息";
                }else{
                    return "活动消息";
                }
            } },
			{ label: '创建时间', name: 'createdTime', index: 'created_time', width: 80 , formatter:function(cellvalue){
                return formatDate(cellvalue);
            }}
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
function formatDate(value) {
    if(value==""||value==null){
        return "";
    }
    var date = new Date(value*1000);
    Y = date.getFullYear(),
        m = date.getMonth() + 1,
        d = date.getDate(),
        H = date.getHours(),
        i = date.getMinutes(),
        s = date.getSeconds();
    if (m < 10) {
        m = '0' + m;
    }
    if (d < 10) {
        d = '0' + d;
    }
    if (H < 10) {
        H = '0' + H;
    }
    if (i < 10) {
        i = '0' + i;
    }
    if (s < 10) {
        s = '0' + s;
    }
    <!-- 获取时间格式 2017-01-03 10:13:48 -->
    var t = Y+'-'+m+'-'+d+' '+H+':'+i+':'+s;
    <!-- 获取时间格式 2017-01-03 -->
    //var t = Y + '-' + m + '-' + d;
    return t;
}
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		sysnotifyMessage: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.sysnotifyMessage = {};
		},
		update: function (event) {
			var messageId = getSelectedRow();
			if(messageId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(messageId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.sysnotifyMessage.messageId == null ? "sys/sysnotifymessage/save" : "sys/sysnotifymessage/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.sysnotifyMessage),
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
			var messageIds = getSelectedRows();
			if(messageIds == null){
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
                        url: baseURL + "sys/sysnotifymessage/delete",
                        contentType: "application/json",
                        data: JSON.stringify(messageIds),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
                                history.go(0);
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
		getInfo: function(messageId){
			$.get(baseURL + "sys/sysnotifymessage/info/"+messageId, function(r){
                vm.sysnotifyMessage = r.sysnotifyMessage;
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