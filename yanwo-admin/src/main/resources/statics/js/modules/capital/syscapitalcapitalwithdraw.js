$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/syscapitalcapitalwithdraw/list',
        datatype: "json",
        postData:{'status':0},
        colModel: [
			{ label: '操作', name: 'id', index: 'id', width: 50, key: true,
                formatter: function (value, options, row) {
                    if(row['status']=='0'){
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.audit("+row['id']+")' >审核</a>&nbsp;&nbsp;&nbsp;";
                    }else{
                        return "";
                    }
                }
            },
			{ label: '微信昵称', name: 'userName', index: 'userName', width: 80 },
            { label: '微信手机号', name: 'phone', index: 'phone', width: 80 },
            { label: '银行卡号', name: 'bankCard', index: 'bank_card', width: 50, key: true },
            { label: '真实姓名', name: 'realName', index: 'real_name', width: 50, key: true },
			{ label: '提现金额', name: 'withdrawFee', index: 'withdraw_fee', width: 80 },
            { label: '手续费金额', name: 'poundageFee', index: 'poundage_fee', width: 80 },
            { label: '到账金额', name: 'actualFee', index: 'actual_fee', width: 80 },
			{ label: '状态', name: 'status', index: 'status', width: 80 ,formatter:function(cellvalue){
                    if(cellvalue == '0'){
                        return "待审核";
                    }else if(cellvalue == '1'){
                        return "审核通过";
                    }else{
                        return "驳回";
                    }
                }},
			{ label: '提现原因', name: 'withdrawReason', index: 'withdraw_reason', width: 80 },
			{ label: '驳回原因', name: 'refuseReason', index: 'refuse_reason', width: 80 },
			{ label: '申请时间', name: 'createdTime', index: 'created_time', width: 80 , formatter:function(cellvalue){
                    return formatDate(cellvalue);
                }},
			{ label: '修改时间', name: 'modifiedTime', index: 'modified_time', width: 80 , formatter:function(cellvalue){
                    return formatDate(cellvalue);
                }}
        ],
		viewrecords: true,
        height: 600,
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
		withdraw: {},
        status:'0',
        id:'',
        reason:''
	},
	methods: {
		query: function () {
			vm.reload();
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{'status':vm.status},
                page:page
            }).trigger("reloadGrid");
		},
        audit(id) {
            vm.withdraw.id = id;
            $('#projectModal2').modal();
        },
        pass:function(){
            var Data = {
                "id":vm.withdraw.id,
                "status":1,
                "reason":vm.withdraw.reason
            }
            $.ajax({
                type: "POST",
                url: baseURL + "sys/syscapitalcapitalwithdraw/audit",
                contentType: "application/json",
                data: JSON.stringify(Data),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            $('#projectModal2').modal('hide');
                            vm.user_id = '';
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        nopass:function(){
		    if(vm.withdraw.refuseReason==null){
               alert("请填写驳回原因");
               return;
            }
            var Data = {
                "id":vm.withdraw.id,
                "status":2,
                "reason":vm.withdraw.refuseReason
            }
            $.ajax({
                type: "POST",
                url: baseURL + "sys/syscapitalcapitalwithdraw/audit",
                contentType: "application/json",
                data: JSON.stringify(Data),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            $('#projectModal2').modal('hide');
                            vm.user_id = '';
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
	}
});