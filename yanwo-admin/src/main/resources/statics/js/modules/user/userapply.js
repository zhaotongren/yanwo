$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'yanwo/userapply/list',
        datatype: "json",
        colModel: [			
            { label: 'id', name: 'id', index: 'id', width: 90, key: true,align: "center", hidden:true},
            {
                label: '操作', width: 50, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    if(row['status']=='1'){
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.audit("+row['id']+")' >审核</a>&nbsp;";
                    }else{
                        return "";
                    }
                }
            },
			{ label: 'userId', name: 'userId', index: 'user_id', width: 80 },
            { label: '头像', name: 'cerHandPic', index: 'cer_hand_pic', width: 60 ,formatter:function(cellvalue){
                    return "<img src="+cellvalue+" style='width:40px'>";
                }},
            { label: '昵称', name: 'nickName', index: 'nick_name', width: 80 },
            { label: '手机号', name: 'mobile', index: 'mobile', width: 80 },
			{ label: '姓名', name: 'realName', index: 'real_name', width: 80 },
			{ label: '身份证号', name: 'cardId', index: 'card_id', width: 80 }, 			
			{ label: '地址', name: 'addr', index: 'addr', width: 80 },
			{ label: '驳回原因', name: 'reson', index: 'reson', width: 80 },
            { label: '分销身份', name: 'status', index: 'status', width: 80 ,formatter:function(cellvalue){
                    if(cellvalue == '1'){
                        return "待审核";
                    }else if(cellvalue == '2'){
                        return "审核通过";
                    }else if(cellvalue == '3'){
                        return "审核驳回";
                    }else if(cellvalue == '4'){
                        return "已取消";
                    }
                }},
            { label: '申请时间', name: 'createTime', index: 'create_time', width: 80, formatter:function(cellvalue){
                    return formatDate(cellvalue);
                } }
        ],
		viewrecords: true,
        height: 600,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,
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
		userApply: {},
        q:{
            searchType:'',
            searchContent:''
        },
        id:'',
        reson:'',
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.userApply = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.userApply.id == null ? "yanwo/userapply/save" : "yanwo/userapply/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.userApply),
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
			var ids = getSelectedRows();
			if(ids == null){
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
                        url: baseURL + "yanwo/userapply/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
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
		getInfo: function(id){
			$.get(baseURL + "yanwo/userapply/info/"+id, function(r){
                vm.userApply = r.userApply;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{ 'searchType':vm.q.searchType,
                    'searchContent':vm.q.searchContent
                },
                page:page
            }).trigger("reloadGrid");
		},
        audit: function(id){
            vm.id = id;
            $('#auditModal').modal();
        },
        save_audit: function(type){
            var formdata = new FormData();
            formdata.append("id",vm.id);
            formdata.append("reson",vm.reson);
            formdata.append("type",type);
            $.ajax({
                url: baseURL + "yanwo/userapply/audit",
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            $("#auditModal").modal('hide');
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
            vm.id='';
            vm.reson='';
        },
	}
});