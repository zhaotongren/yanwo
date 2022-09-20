$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysitemitem/list',
        datatype: "json",
        postData:{'type':2},
        colModel: [
            { label: '商品Id', name: 'itemId', index: 'item_id', width: 50, key: true },
			{ label: '标题', name: 'title', index: 'title', width: 80 },
            { label: '图片', name: 'imageDefaultId', index: 'image_default_id', width: 80,formatter:function(cellvalue){
                    return "<img src="+cellvalue+" style='width:60px'>";
                } },
			{ label: '价格', name: 'price', index: 'price', width: 80 },
            { label: '创建时间', name: 'createdTime', index: 'created_time', width: 80 , formatter:function(cellvalue){
                    return formatDate(cellvalue);
                }},
            { label: '最后更新时间', name: 'modifiedTime', index: 'modified_time', width: 80 , formatter:function(cellvalue){
                return formatDate(cellvalue);
            }}
        ],
		viewrecords: false,
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
		sysitemItem: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.sysitemItem = {};
            $('.summernote').summernote("code",'');
		},
		update: function (event) {
			var itemId = getSelectedRow();
			if(itemId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(itemId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.sysitemItem.itemId == null ? "sys/sysitemitem/save" : "sys/sysitemitem/update";
                vm.contentHtml=$('.summernote').summernote('code');

                var Data = {
                    "itemId":vm.sysitemItem.itemId,
                    "imageDefaultId":vm.sysitemItem.imageDefaultId,
                    "title":vm.sysitemItem.title,
                    "price":vm.sysitemItem.price,
                    "type":2,
                    "approveStatus":"1",
                    "itemSort":"1",
                    "description":encodeURI(vm.contentHtml)
                }

                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json;charset=UTF-8",
                    data: JSON.stringify(Data),
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
			var itemIds = getSelectedRows();
			if(itemIds == null){
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
                        url: baseURL + "sys/sysitemitem/delete",
                        contentType: "application/json",
                        data: JSON.stringify(itemIds),
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
		getInfo: function(itemId){
			$.get(baseURL + "sys/sysitemitem/info/"+itemId, function(r){
                $('.summernote').summernote("code",decodeURI(r.sysitemItem.description));
                vm.sysitemItem = r.sysitemItem;
            });
		},
		reload: function (event) {
            var obj = document.getElementById('fileInput') ;
            obj.outerHTML=obj.outerHTML;
            vm.sysitemItem={};
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{'type':2},
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
                        vm.sysitemItem.imageDefaultId=data.msg;
                        $("#itemImage").css("src",vm.sysitemItem.imageDefaultId);
                    }else{
                    }
                }
            });

        },
        upload2:function(file){
            var formData = new FormData();
            formData.append("file", file[0]);
            console.log(formData)
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
                    console.log('图片==='+data.code);
                    if(0==data.code){
                        $(".summernote").summernote('insertImage', data.msg, 'image');
                    }else{
                    }
                }
            });
        },
        putAway: function (event) {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }
            confirm('确定要上架架选中的商品？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/sysitemitem/putAway",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(index){
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        downAway: function (event) {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }
            confirm('确定要上架架选中的商品？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/sysitemitem/downAway",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(index){
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },

	}
});