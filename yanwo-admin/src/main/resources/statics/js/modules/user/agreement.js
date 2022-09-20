
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
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        sysindexArt: {
            enableFlag: '0'
        }
    },
    created:function(){
        this.getInfo();
    },
    methods: {
        query: function () {
            vm.reload();
        },
        articleLink(id) {
            alert("文章信息："+"/pages/home/toutiao_detail/toutiao_detail?id="+id);
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.sysindexArt = {enableFlag: '0'};
            $('.summernote').summernote("code", '');
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function () {
                var url = vm.sysindexArt.id == null ? "sys/sysindexart/save" : "sys/sysindexart/update";
                vm.contentHtml = $('.summernote').summernote('code');

                var Data = {
                    "id": vm.sysindexArt.id,
                    "type": '3',
                    "enableFlag": 0,
                    "description": encodeURI(vm.contentHtml)
                }
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(Data),
                    success: function (r) {
                        if (r.code === 0) {
                            layer.msg("操作成功", {icon: 1});
                            vm.reload();
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        } else {
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
            if (ids == null) {
                return;
            }
            var lock = false;
            layer.confirm('确定要删除选中的记录？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                if (!lock) {
                    lock = true;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "sys/sysindexart/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
                        success: function (r) {
                            if (r.code == 0) {
                                layer.msg("操作成功", {icon: 1});
                                $("#jqGrid").trigger("reloadGrid");
                            } else {
                                layer.alert(r.msg);
                            }
                        }
                    });
                }
            }, function () {
            });
        },
        getInfo: function () {
            $.get(baseURL + "sys/sysindexart/agreement", function (r) {
                if(r.sysindexArt != null && r.sysindexArt.description != null){
                    $('.summernote').summernote("code", decodeURI(r.sysindexArt.description));
                }
                vm.sysindexArt = r.sysindexArt;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        },
        upload: function () {
            var formData = new FormData();
            formData.append("file", $("#fileInput")[0].files[0]);
            $.ajax({
                type: 'POST',
                url: baseURL + "upload/upload",
                data: formData,
                contentType: false,
                processData: false,//这个很有必要，不然不行
                dataType: "json",
                mimeType: "multipart/form-data",
                success: function (data) {
                    if (0 == data.code) {
                        vm.sysindexArt.imgPath = data.msg;
                        $("#imgPathUrl").attr("src", vm.sysindexArt.imgPath);
                    } else {
                    }
                }
            });

        },
        upload2: function (file) {
            var formData = new FormData();
            formData.append("file", file[0]);
            console.log(formData)
            $.ajax({
                type: 'POST',
                url: baseURL + "upload/upload",
                data: formData,
                contentType: false,
                processData: false,//这个很有必要，不然不行
                dataType: "json",
                mimeType: "multipart/form-data",
                success: function (data) {
                    if (0 == data.code) {
                        $(".summernote").summernote('insertImage', data.msg, 'image');
                    } else {
                    }
                }
            });
        }
    }
});