var Cate = {
    id: "cateTable",
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Cate.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: '分类ID', field: 'id', visible: false, align: 'center', valign: 'middle', width: '80px'},
        {title: '分类名称', field: 'name', align: 'center', valign: 'middle', sortable: true, width: '100px'},
        {title: '排序号', field: 'sort', align: 'center', valign: 'middle', sortable: true, width: '100px'},
        {title: '操作', field: 'level_', align: 'center', valign: 'middle', sortable: true, width: '100px',
            formatter: function(row){
                if(row.homeShow == 1){
                    var r='<a class="open" href="#" onclick="vm.catShow('+row.id+')">取消首页展示</a>';
                }else{
                    var r='<a class="open" href="#" onclick="vm.catShow('+row.id+')">加入首页展示</a>';
                }

                if(row.level_==1){
                    return '<a class="open" href="#" onclick="vm.create('+row.id+','+row.level_+')">添加二级分类</a>&nbsp;&nbsp'+
                        '<a class="open" href="#" onclick="vm.update('+row.id+','+row.level_+')">编辑</a>&nbsp;&nbsp'+
                        '<a class="open" href="#" onclick="vm.delete('+row.id+')">删除</a>&nbsp;&nbsp';
                }else{
                    return '<a class="open" href="#" onclick="vm.update('+row.id+','+row.level_+')">编辑</a>&nbsp;&nbsp'+
                        '<a class="open" href="#" onclick="vm.delete('+row.id+')">删除</a>&nbsp;&nbsp'+
                        '<a class="open" href="#"  onclick="vm.catLink('+row.id+')">分类链接</a>&nbsp;&nbsp;&nbsp;';
                }
            }},]
    return columns;
};


var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "pid",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};
var ztree;

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        two_parId:0,
        three_parId:0,
        catetree: {
            parentName: null,
            parId: '0',
            orderNum: 0
        },
        pageData: {},
        brandContent: {
            content: ''
        },
        searchType: '',
        searchContent: '',
        cateId: '',
        cmd: 'create',
        cateIds: [],
        title: '',
        parentCate: [],
        cate: {
            catId: '',
            catName: '',
            parentId: '',
            platformFee: '',
            guaranteeMoney: '',
            catServiceRates: '',
            catTemplate: '',
            catLogoFile: '',
            catLogo: '',
            orderSort: '',
        },
    },
    methods: {
        catLink(catId){
            alert("分类链接："+"/pages/home/list/list?catId="+catId);
        },
        getCate: function () {
            //加载部门树
            $.get(baseURL + "sys/cate/select", function (r) {
                ztree = $.fn.zTree.init($("#cateTree"), setting, r.data);
                var node = ztree.getNodeByParam("id", vm.catetree.parId);
                ztree.selectNode(node);

                vm.catetree.parentName = node.name;
            })
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.catetree = {parentName: null, parId: 0, orderNum: 0};
            vm.getCate();
        },

        del: function () {
            var cateId = getCateId();
            if (cateId == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/cate/delete",
                    data: "cateId=" + cateId,
                    success: function (r) {
                        if (r.status === 200) {
                            alert('操作成功', function () {
                                vm.reload();
                            });
                        } else {
                            alert(r.message);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function (event) {
            var url = vm.catetree.cateId == null ? "sys/cate/save" : "sys/cate/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.cat),
                success: function (r) {
                    if (r.status === 200) {
                        alert('操作成功', function () {
                            vm.reload();
                        });
                    } else {
                        alert(r.message);
                    }
                }
            });
        },
        /*-------------------------------------------------------------------------------------------------------------------*/
        catShow(catId){
            var formdata = new FormData();
            var url = '/category/catShow';
            formdata.append("catId", catId);
            $.ajax({
                url: url,
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.status === 200) {
                        alert('修改成功', function (index) {
                            vm.reload();
                            vm.clear();
                        });
                    } else {
                        alert(r.message);
                    }
                }
            });
        },
        create(pid, level) {
            vm.clear();
            vm.title = "添加一级分类";
            vm.cmd = 'create';
            vm.cate.parentId = pid;
            if (level == 1) {
                vm.title = "添加二级分类";
                $.get(baseURL + 'category/getParentCateForCreate?cateId=' + pid + '&level=' + level, function (r) {
                    vm.parentCate = r.cate;
                    $('#cateModal').modal();
                    $('#cateModal2').modal();

                });
            } else if (level == 2) {
                vm.title = "添加三级分类";
                $.get(baseURL + 'category/getParentCateForCreate?cateId=' + pid + '&level=' + level,function (r) {
                    vm.parentCate = r.data;
                    $('#cateModal3').modal();
                });
            } else {
                $('#cateModal1').modal();
            }
        },
        save(level, event) {
            if(vm.cate.catLogo==''){
                alert('请选择分类图片！');
                return false;
            }
            var formdata = new FormData();
            var url = '/category/create';
            if (vm.cmd == 'update') url = '/category/update';
            formdata.append("catId", vm.cate.catId);
            formdata.append("catName", vm.cate.catName);
            formdata.append("parentId", vm.cate.parentId);
            formdata.append("catLogo", vm.cate.catLogo);
            formdata.append("level", level);
            formdata.append("orderSort", vm.cate.orderSort);
            console.log('save='+vm.cate.catLogo);
            $.ajax({
                url: url,
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        alert('保存成功', function (index) {
                            $("#cateModal1").modal('hide');
                            $("#cateModal2").modal('hide');
                            $("#cateModal3").modal('hide');
                            document.getElementById('file1').value = '';
                            document.getElementById('file2').value = '';
                            vm.reload();
                            vm.clear();
                        });
                    } else {
                        alert(r.message);
                    }
                }
            });
        },

        update(id, level) {
            vm.title = "编辑分类";
            vm.cmd = 'update';
            if (level == 1) {
                $.get(baseURL + 'category/update/' + id, function (r) {
                    if (r.code == 0) {
                        vm.cate = r.cate;
                        $('#cateModal1').modal();
                    } else {
                        alert(r.message);
                        vm.clear();
                    }
                });
            } else if (level == 2) {
                $.get(baseURL + 'category/getParentCateForUpdate?cateId=' + id + '&level=' + level, function (r) {
                    if (r.code == 0) {
                        vm.parentCate = r.cate;
                        vm.two_parId = r.cate.parentId;
                    } else {
                        alert(r.message);
                    }
                });
                $.get(baseURL + 'category/update/' + id, function (r) {
                    if (r.code == 0) {
                        vm.cate = r.cate;
                        $('#cateModal2').modal();
                    } else {
                        alert(r.message);
                        vm.clear();
                    }
                });
            } else {
                $.get(baseURL + 'category/getParentCateForUpdate?cateId=' + id + '&level=' + level, function (r) {
                    if (r.code == 0) {
                        vm.parentCate = r.cate;
                        vm.three_parId = r.data.parentId;
                    } else {
                        alert(r.message);
                    }
                });
                $.get(baseURL + 'category/update/' + id, function (r) {
                    if (r.code == 0) {
                        vm.cate = r.cate;
                        $('#cateModal3').modal();
                    } else {
                        alert(r.message);
                        vm.clear();
                    }
                });
            }

        },
        clear() {
            vm.cate = {
                catId: '',
                catName: '',
                parentId: '',
                platformFee: '',
                guaranteeMoney: '',
                catServiceRates: '',
                catTemplate: '',
                catLogoFile: '',
                catLogo: '',
                sort: '',
                orderSort: ''
            };
        },
        change1(id, disable) {
            var str = "是否停用？";
            if (disable) {
                str = "是否启用？";
            }
            confirm(str, function () {
                var url = "category/updateCatStatus?catId=" + id;
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    success: function (r) {
                        if (r.status === 200) {
                            alert("操作成功");
                            vm.reload();
                            vm.clear();
                        } else {
                            alert(r.message);
                        }
                    }
                });
            });
        },
        delete(id) {
            confirm('确认删除吗？', function () {
                var url = 'category/delete/' + id;
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    success: function (r) {
                        if (r.code === 0) {
                            alert("删除成功");
                            vm.reload();
                            vm.clear();
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },

        //发布三级分类到redis中
        release(){
            $.get(baseURL + "category/releaseCate", function (r) {
                if(r.status == 200){
                    alert('发布成功！');
                }else{
                    alert('发布失败！');
                }
            });
        },
        upload1: function(){
            var that = this;
            var inputDOM = that.$refs.inputer1;
            var file = inputDOM.files;
            var formData = new FormData();
            formData.append('file', file[0]);
            var url=baseURL + "file/upload";
            $.ajax({
                url: url,
                type: "post",
                data: formData ,
                processData: false,
                contentType: false,
                success: function(data) {
                    console.log(data.msg);
                    vm.cate.catLogo='';
                    vm.cate.catLogo=data.msg;
                    console.log('upload1='+vm.cate.catLogo);
                },
                error: function(error){}
            });
        },
        upload2: function(){
            var that = this;
            var inputDOM = that.$refs.inputer2;
            var file = inputDOM.files;
            var formData = new FormData();
            formData.append('file', file[0]);
            var url=baseURL + "file/upload";
            $.ajax({
                url: url,
                type: "post",
                data: formData ,
                processData: false,
                contentType: false,
                success: function(data) {
                    console.log(data.msg);
                    vm.cate.catLogo='';
                    vm.cate.catLogo=data.msg;
                    console.log('upload2='+vm.cate.catLogo);
                },
                error: function(error){}
            });
        },
        /*-------------------------------------------------------------------------------------------------------------------*/


        reload: function () {
            vm.showList = true;
            Cate.table.refresh();
        }
    }
});


function getCateId () {
    var selected = $('#cateTable').bootstrapTreeTable('getSelections');
    if (selected.length == 0) {
        alert("请选择一条记录");
        return null;
    } else {
        return selected[0].id;
    }
}


$(function () {
    var colunms = Cate.initColumn();
    var table = new TreeTable(Cate.id, baseURL + "category/list", colunms);
    table.setExpandColumn(2);
    table.setIdField("id");
    table.setCodeField("id");
    table.setParentCodeField("pid");
    table.setExpandAll(false);
    table.init();
    Cate.table = table;
});




