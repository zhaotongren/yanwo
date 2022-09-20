$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysindexad/list',
        datatype: "json",
        postData:{'type':2},
        colModel: [
            { label: 'id', name: 'id', index: 'id', width: 50, key: true },

            { label: '标题', name: 'title', index: 'title', width: 80 },

            { label: '图片', name: 'pic', index: 'pic', width: 80,formatter:function(cellvalue){
                    return "<img src="+cellvalue+" style='width:60px'>";
                } },
            { label: '跳转链接', name: 'url', index: 'url', width: 80 },

            { label: '是否启用', name: 'enableFlag', index: 'enable_flag', width: 80 ,formatter:function(cellvalue){
                    if(cellvalue == 0){
                        return "是";
                    }else{
                        return "否";
                    }
                }},
            { label: '排序', name: 'sort', index: 'sort', width: 80 }
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
        sysindexAd: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.sysindexAd = {enableFlag:'0'};
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
                var url = vm.sysindexAd.id == null ? "sys/sysindexad/save" : "sys/sysindexad/update";
                vm.sysindexAd.type = '2';
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.sysindexAd),
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
                        url: baseURL + "sys/sysindexad/delete",
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
            $.get(baseURL + "sys/sysindexad/info/"+id, function(r){
                vm.sysindexAd = r.sysindexAd;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'type':2},
                page:page
            }).trigger("reloadGrid");
        },
        upload:function(event){
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
                    if(0==data.code){
                        console.log(data.msg)
                        vm.sysindexAd.pic=data.msg;
                    }else{
                    }
                }
            });

        }
    }
});