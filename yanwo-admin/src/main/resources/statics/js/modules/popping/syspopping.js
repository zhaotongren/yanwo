$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'yanwo/syspopping/list',
        datatype: "json",
        colModel: [			
			{ label: 'poppingId', name: 'poppingId', index: 'popping_id', width: 20, key: true,hidden:true },
			{ label: '名称', name: 'poppingName', index: 'popping_name', width: 50 },
			{ label: '说明', name: 'poppingExplain', index: 'popping_explain', width: 80 }, 			
			{ label: '图片', name: 'poppingImg', index: 'popping_img', width: 50,formatter:function (cellvalue) {
                    return "<img src="+cellvalue+" style='width:60px'>"
                } },
			{ label: '跳转链接', name: 'skipUrl', index: 'skip_url', width: 80 }, 			
			{ label: '每天弹出次数', name: 'poppingDayCount', index: 'popping_day_count', width: 40 },
			{ label: '每人弹出总次数', name: 'poppingSum', index: 'popping_sum', width: 50 },
			{ label: '状态', name: 'status', index: 'status', width: 30, formatter:function (cellvalue) {
                    if (cellvalue==0){
                        return '开启中';
                    }else if (cellvalue==1) {
                        return '禁用';
                    }else {
                        return '已过期';
                    }
                }},
			{ label: '弹出间隔(分钟)', name: 'intervalMin', index: 'interval_min', width: 50 },
			{ label: '过期时间', name: 'expiresTime', index: 'expires_time', width: 80, formatter:function (cellvalue) {
                    return formatDate(cellvalue);
                }},
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80, formatter:function (cellvalue) {
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
    if (value == "" || value == null) {
        return "";
    }
    var date = new Date(value * 1000);
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
    var t = Y + '-' + m + '-' + d + ' ' + H + ':' + i + ':' + s;
    <!-- 获取时间格式 2017-01-03 -->
    //var t = Y + '-' + m + '-' + d;
    return t;
}
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		sysPopping: {
            expiresTime:'',
            intervalMin:0
        },
        imgUrl:''
	},
	methods: {
        dateToTimestamp:function(timeStr){
            var date = new Date(timeStr+' 23:59:59');
            var time3 = Date.parse(date);
            console.log(time3);
            return time3/1000;
        },
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.sysPopping = {
                expiresTime:'',
                intervalMin:0
            };
		},
		update: function (event) {
			var poppingId = getSelectedRow();
			if(poppingId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(poppingId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.sysPopping.poppingId == null ? "yanwo/syspopping/save" : "yanwo/syspopping/update";
                vm.sysPopping.poppingImg=vm.imgUrl;
                vm.sysPopping.expiresTime=vm.dateToTimestamp(vm.sysPopping.expiresTime);
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.sysPopping),
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
			var poppingIds = getSelectedRows();
			if(poppingIds == null){
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
                        url: baseURL + "yanwo/syspopping/delete",
                        contentType: "application/json",
                        data: JSON.stringify(poppingIds),
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
		getInfo: function(poppingId){
			$.get(baseURL + "yanwo/syspopping/info/"+poppingId, function(r){
                vm.sysPopping = r.sysPopping;
                vm.imgUrl = r.sysPopping.poppingImg;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		},
        upload:function(){
            var formData = new FormData();
            formData.append("file", $("#fileInput")[0].files[0]);
            $.ajax({
                type:'POST',
                url:baseURL + "upload/upload",
                data:formData,
                contentType:false,
                processData:false,//这个很有必要，不然不行
                dataType:"json",
                mimeType:"multipart/form-data",
                success:function(data){
                    console.log('图片==='+JSON.stringify(data));
                    console.log('图片==='+data.code==0);
                    if(0==data.code || '0'==data.code){
                        if(data.msg!=''){
                            vm.imgUrl=data.msg
                        }
                        // vm.sysitemItem.imageDefaultId=data.msg;
                        // $("#itemImage").attr("src",vm.sysitemItem.imageDefaultId);
                        // $("#itemImage").append('<img :src='+vm.sysitemItem.imageDefaultId+' width="60px"/>');
                    }else{
                    }
                }
            });

        },
        del_pic:function(){
            vm.imgUrl='';
        }
	}
});