$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/syscapitalcapitaldetail/list',
        datatype: "json",
        colModel: [
            { label: '订单号', name: 'tid', index: 'tid', width: 80 },
            { label: '用户名', name: 'userName', index: 'user_name', width: 80 },
            { label: '资产明细id', name: 'capitalDetailId', index: 'capital_detail_id', width: 50, key: true ,hidden:true},
            { label: '资产id', name: 'capitalId', index: 'capital_id', width: 80,hidden:true },
            { label: '返利金额', name: 'capitalFee', index: 'capital_fee', width: 80 },
            // { label: '来源', name: 'capitalType', index: 'capital_type', width: 80,formatter:function(cellvalue){
            //         if(cellvalue == '1'){
            //             return "直级返利";
            //         }else if(cellvalue == '2'){
            //             return "间接返利";
            //         }else if(cellvalue == '3'){
            //             return "团长返利";
            //         }else if(cellvalue == '4'){
            //             return "自己购买商品返利";
            //         }else if(cellvalue == '5'){
            //             return "消费";
            //         }else{
            //             return "提现";
            //         }
            //     } },
            { label: '产生描述', name: 'capitalDesc', index: 'capital_desc', width: 80 },
            /*{ label: '与该条记录关联的小订单号oid', name: 'oid', index: 'oid', width: 80,hidden:true },*/
            { label: '创建时间', name: 'createdTime', index: 'created_time', width: 80,formatter:function(cellvalue){
                    return formatDate(cellvalue);
                } }
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
        jsonReader: {
            root: "list",
            page: "currPage",
            total: "totalPage",
            records: "totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
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
		syscapitalCapitalDetail: {},
        q:{
            searchType:'',
            searchContent:''
        },
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.syscapitalCapitalDetail = {};
		},
		update: function (event) {
			var capitalDetailId = getSelectedRow();
			if(capitalDetailId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(capitalDetailId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.syscapitalCapitalDetail.capitalDetailId == null ? "sys/syscapitalcapitaldetail/save" : "sys/syscapitalcapitaldetail/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.syscapitalCapitalDetail),
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
			var capitalDetailIds = getSelectedRows();
			if(capitalDetailIds == null){
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
                        url: baseURL + "sys/syscapitalcapitaldetail/delete",
                        contentType: "application/json",
                        data: JSON.stringify(capitalDetailIds),
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
		getInfo: function(capitalDetailId){
			$.get(baseURL + "sys/syscapitalcapitaldetail/info/"+capitalDetailId, function(r){
                vm.syscapitalCapitalDetail = r.syscapitalCapitalDetail;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{'searchType':vm.q.searchType,'searchContent':vm.q.searchContent},
                page:page
            }).trigger("reloadGrid");
		}
	}
});