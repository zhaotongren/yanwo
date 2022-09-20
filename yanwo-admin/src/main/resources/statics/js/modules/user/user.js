$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'tb/user/list',
        datatype: "json",
        colModel: [
            {
                label: '操作', width: 100, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    if(row['memberShip']=='2'){
                        if(row['colonel']=='0'){
                            var str ="<a style='width:10px' class='label label-success' href='#' onclick='vm.colonel("+row['userId']+")' >加入团长</a>&nbsp;&nbsp;&nbsp;";
                        }else if(row['colonel']=='1'){
                            var str ="<a style='width:10px' class='label label-success' href='#' onclick='vm.colonel("+row['userId']+")' >取消团长</a>&nbsp;&nbsp;&nbsp;";
                        }else{
                            var str="";
                        }
                    }else{
                        if(row['colonel']=='1' ){
                            var str ="<a style='width:10px' class='label label-success' href='#' onclick='vm.colonel("+row['userId']+")' >取消团长</a>&nbsp;&nbsp;&nbsp;";
                        }else{
                            var str="";
                        }
                    }

                    return str;
                }
            },
            { label: '头像', name: 'cerHandPic', index: 'cer_hand_pic', width: 60 ,formatter:function(cellvalue){
                    return "<img src="+cellvalue+" style='width:40px'>";
                }},
            { label: '昵称', name: 'nickName', index: 'nick_name', width: 80 },
            /*{ label: '真实姓名', name: 'realName', index: 'real_name', width: 80 },*/
            { label: '手机号', name: 'mobile', index: 'mobile', width: 80 },
            { label: '推荐人', name: 'referrerName', width: 80 },
            { label: '分销身份', name: 'memberShip', index: 'member_ship', width: 80 ,formatter:function(value, options, row){
                    if(value == '1'){
                        return "待审核";
                    }else if(value == '2'){
                        return "审核通过&nbsp;&nbsp;<a style='width:10px' class='label label-success' href='#' onclick='vm.cancel("+row['userId']+")' >取消</a>&nbsp;";
                    }else if(value == '3'){
                        return "审核驳回&nbsp;&nbsp;原因:"+row['reson'];
                    }else if(value == '4'){
                        return "已取消&nbsp;&nbsp;原因:"+row['reson'];
                    }else {
                        return "未申请";
                    }
                }},
            { label: '注册时间', name: 'createTime', index: 'create_time', width: 80, formatter:function(cellvalue){
                    return formatDate(cellvalue);
                } },
            /* { label: '操作', name: 'cZ', index: 'userId', width: 60,sortable: false,formatter:cz}*/
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
		user: {},
        q:{
            searchType:'',
            searchContent:'',
            colonel:'',
            memberShip:''
        },
        years:'',
        user_id:'',
        receiverName:'',
        receiverMobile:'',
        receiverAddress:'',
        reson:''
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.user = {};
		},
		update: function (event) {
			var userId = getSelectedRow();
			if(userId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(userId)
		},
        upgradeMembership:function(){
            var userId = getSelectedRow();
            if(userId == null){
                return ;
            }
            vm.user_id = userId;
            $(".popup").show();
            $(".popupyer").show();
        },
        upgrade:function(){
            var Data = {
                "userId":vm.user_id,
                "years":vm.years,
                'receiverName':vm.receiverName,
                'receiverMobile':vm.receiverMobile,
                'receiverAddress':vm.receiverAddress
            }
            $.ajax({
                type: "POST",
                url: baseURL + "tb/user/upgrade",
                contentType: "application/json",
                data: JSON.stringify(Data),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            $(".popup").hide();
                            $(".popupyer").hide();
                            vm.user_id = '';
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        colseUpgrade:function(){
            $(".popup").hide();
            $(".popupyer").hide();
            vm.user_id = '';
        },
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.user.userId == null ? "tb/user/save" : "sys/user/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.user),
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
			var userIds = getSelectedRows();
			if(userIds == null){
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
                        url: baseURL + "tb/user/delete",
                        contentType: "application/json",
                        data: JSON.stringify(userIds),
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
		getInfo: function(userId){
			$.get(baseURL + "tb/user/info/"+userId, function(r){
                vm.user = r.user;
            });
		},
        colonel(userId){
            var formdata = new FormData();
            formdata.append("userId",userId);
            $.ajax({
                url: baseURL + "tb/user/colonel",
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                            vm.clear();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{ 'searchType':vm.q.searchType,
                    'searchContent':vm.q.searchContent,
                    'colonel':vm.q.colonel,
                    'memberShip':vm.q.memberShip
                },
                page:1
            }).trigger("reloadGrid");
		},
        cancel: function(userId){
            vm.user_id = userId;
            $("#cancelModal").modal();
        },
        save_cancel: function(){
		    if(vm.reson==null || vm.reson==''){
                alert("请填写原因");
                return;
            }

            var formdata = new FormData();
            formdata.append("userId",vm.user_id);
            formdata.append("reson",vm.reson);

            $.ajax({
                url: baseURL + "tb/user/cancel",
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            $("#cancelModal").modal('hide');
                            vm.reload();
                            vm.clear();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        clear(){
            vm.user_id='';
            vm.reson='';
        },
	}
});