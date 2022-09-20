$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysrechargecard/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '卡号', name: 'cardNo', index: 'card_no', width: 80 },
            { label: '金额', name: 'rechargeMoney', index: 'recharge_money', width: 80 },
            { label: '用户ID', name: 'userId', index: 'user_id', width: 80 },
            { label: '用户昵称', name: 'userName', index: 'user_name', width: 80 },
            { label: '手机号', name: 'phone', index: 'phone', width: 80 },
            { label: '创建时间', name: 'createdTime', index: 'created_time', width: 80,formatter:function(cellvalue){
                    return formatDate(cellvalue);
                } },
            { label: '激活时间', name: 'modifiedTime', index: 'modified_time', width: 80,formatter:function(cellvalue){
                    return formatDate(cellvalue);
                } },
            { label: '状态', name: 'status', index: 'status', width: 80,formatter:function(cellvalue){
                    if(cellvalue == '0'){
                        return "未激活";
                    }else if(cellvalue == '1'){
                        return "已激活";
                    }else{
                        return "";
                    }
                } },
            { label: '是否有效', name: 'disabled', index: 'disabled', width: 80,formatter:function(cellvalue){
                    if(cellvalue == '0'){
                        return "是";
                    }else if(cellvalue == '1'){
                        return "否";
                    }else{
                        return "";
                    }
                } },
            {
                label: '操作', width: 80, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    if (row['disabled'] == "0" ) {
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.setDisabled("+row['id']+","+row['disabled']+")' >禁用</a>&nbsp;&nbsp;&nbsp;";
                    } else if (row['disabled'] == '1') {
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.setDisabled("+row['id']+","+row['disabled']+")' >启用</a>&nbsp;&nbsp;&nbsp;";
                    } else {
                        return "";
                    }
                }
            }
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
		sysrechargeCard: {},
        q:{
            startCard:'',
            endCard:'',
            nickname:'',
            mobile:''
        },
	},
	methods: {
        handleImport() {
            this.$notify({
                title: '温馨提醒',
                message: '请按照导入模版导入哦',
                type: 'warning'
            })
        },
		query: function () {
			vm.reload();
		},
		getInfo: function(id){
			$.get(baseURL + "yanwo/sysrechargecard/info/"+id, function(r){
                vm.sysrechargeCard = r.sysrechargeCard;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{'startCard':vm.q.startCard,'endCard':vm.q.endCard,'nickname':vm.q.nickname,'mobile':vm.q.mobile},
                page:page
            }).trigger("reloadGrid");
		},
        importSuccess(res) {
            console.log(JSON.stringify(res));
            if(res.code==0){
                this.$notify.success({
                    title: '成功',
                    message: res.msg
                })
            }else{
                this.$notify.error({
                    title: '失败',
                    message: res.msg
                })
            }

            this.reload();
        },
        setDisabled(id,disabled) {
            var str = "确定要禁用吗？";
            if (disabled == 1) {
                str = "确定要启用吗？";
            }
            confirm(str, function(){
                var formdata = new FormData();
                formdata.append("id", id);
                $.ajax({
                    url: baseURL + "sys/sysrechargecard/setDisabled",
                    type: "POST",
                    data: formdata,
                    contentType: false,
                    processData: false,
                    success: function (r) {
                        if (r.code === 0) {
                            alert('操作成功', function (index) {
                                vm.reload();
                            });
                        } else {
                            alert(r.message);
                        }
                    }
                });
            });

        }
	}
});