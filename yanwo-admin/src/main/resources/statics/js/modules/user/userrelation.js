$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'tb/user/list',
        postData:{type:2},
        datatype: "json",
        colModel: [
            { label: '头像', name: 'cerHandPic', index: 'cer_hand_pic', width: 60 ,formatter:function(cellvalue){return "<img src="+cellvalue+" style='width:40px'>";}},
            { label: '昵称', name: 'nickName', index: 'nick_name', width: 80 },
            { label: '手机号', name: 'mobile', index: 'mobile', width: 80 },
            { label: '直接推荐人数', name: 'direct', index: 'direct', width: 80 },
            { label: '间接推荐人数', name: 'indirect', index: 'indirect', width: 80 },
            { label: '注册时间', name: 'createTime', index: 'create_time', width: 80, formatter:function(cellvalue){return formatDate(cellvalue);} },
            {label: '查看', width: 100, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    return "<a style='width:10px' class='label label-success' href='#' onclick='vm.getRelation("+row['userId']+")' >查看直接推荐人</a>&nbsp;&nbsp;&nbsp;"
                }
            },
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
		users: {},

        q:{
            searchType:'',
            searchContent:''
        },
        userdata:{},
        direct:'',
        indirect:''
	},
	methods: {
		query: function () {
			vm.reload();
		},
        getRelation(userId){
            $.get(baseURL + "tb/user/relation?userId="+userId, function(r){
                vm.users = r.users;
                if(vm.users!=null){
                    vm.direct=vm.users.length;
                }else{
                    vm.direct=0;
                }
                $("#directList").modal();
            });
        },
        getIndirect(userId){
            $.get(baseURL + "tb/user/relation?userId="+userId, function(r){
                vm.userdata = r.users;
                if(vm.userdata!=null){
                    vm.indirect=vm.userdata.length;
                }else{
                    vm.indirect=0;
                }
                $("#indirectList").modal();
            });
        },
        img(pic) {
            $("#evidencepicww").attr('src', pic);
            $("#photomax_modal").modal();
        },
        closepic() {
            $("#photomax_modal").modal('hide');
        },
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{ 'searchType':vm.q.searchType,
                    'searchContent':vm.q.searchContent,
                    type:2
                },
                page:1
            }).trigger("reloadGrid");
		},
	}
});