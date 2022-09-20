$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysaftersalesrefunds/list',
        datatype: "json",
        colModel: [
            {
                label: '操作', width: 200, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    var r = "<a style='width:10px' class='label label-success' href='#' onclick='vm.getInfo("+row['refundsId']+")' >查看</a>&nbsp;&nbsp;&nbsp;";
                    if (row['status'] == "1" && row['refundsType'] == "1") {
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.audit("+row['refundsId']+")' >审核</a>&nbsp;&nbsp;&nbsp;" + r;
                    } else if (row['status'] == '4') {
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.refund("+row['refundsId']+")' >退款</a>&nbsp;&nbsp;&nbsp;" + r;
                    } else {
                        return r;
                    }
                }
            },
            /*{ label: '退款申请编号', name: 'refundBn', index: 'refund_bn', width: 80 },*/
            {label: '会员名', name: 'userName', index: 'user_id', width: 80},
            {label: '订单编号', name: 'tid', index: 'tid', width: 80},
            {label: '子订单编号', name: 'oid', index: 'oid', width: 80},
            {label: '售后类型', name: 'refundsType', index: 'refunds_type', width: 80,
                formatter: function (cellvalue) {
                    if (cellvalue == '0') {
                        return "取消订单";
                    }else {
                        return "退货退款";
                    }
                }
            },
            /*{ label: '0取消订单  1退货退款', name: 'refundsType', index: 'refunds_type', width: 80 },*/
            {
                label: '状态', name: 'status', index: 'status', width: 80,
                formatter: function (cellvalue) {
                    if (cellvalue == '1') {
                        return "待平台审核";
                    } else if (cellvalue == '2') {
                        return "待回寄";
                    } else if (cellvalue == '3') {
                        return "审核驳回";
                    } else if (cellvalue == '4') {
                        return "待退款";
                    } else if (cellvalue == '5') {
                        return "退款完成";
                    } else {
                        return "退款驳回";
                    }
                }
            },
            /*{ label: '', name: 'refundsReason', index: 'refunds_reason', width: 80 },*/
            {label: '支付金额', name: 'payment', index: 'payment', width: 80},
            {label: '商品金额', name: 'totalPrice', index: 'total_price', width: 80},
            {label: '退款金额', name: 'refundFee', index: 'refund_fee', width: 80},
            // {label: '运费金额', name: 'postFee', index: 'post_fee', width: 80},
            {label: '退款余额', name: 'rechargeFee', index: 'recharge_fee', width: 80},
            {label: '申请时间', name: 'createdTime', index: 'created_time', width: 80,
                formatter:function(cellvalue){
                    return formatDate(cellvalue);
                }
            },
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "list",
            page: "currPage",
            total: "totalPage",
            records: "totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

function loadLocalData(page){// page参数，显示 第几页
    var rowNum = $("#jqGrid").getGridParam('rowNum'); //获取显示配置记录数量
    var mydata = {};
    mydata.rows = vm.q.savedData;
    mydata.page = page;
    mydata.records = mydata.rows.length;
    mydata.total = Math.ceil(mydata.records/rowNum);

    if(!mydata.rows || mydata.rows.length ==0 ){
        $("#jqGrid").jqGrid("clearGridData");
    }

    $("#jqGrid").jqGrid("setGridParam", {
        data: mydata.rows,
        localReader:{
            rows:function(object){ return mydata.rows ;},
            page:function(object){ return mydata.page ;},
            total:function(object){ return mydata.total ;},
            records:function(object){ return mydata.records ;},
            repeatitems : false
        }
    }).trigger("reloadGrid");
}

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
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        sysaftersalesRefunds: {},
        q: {
            tid: '',
            status: ''
        },
        refunds:'',
        orderData:[],
        picArray:[],
        returnAdd:{
            returnId :null,
            address:''

        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        getInfo(refundsId){
            $.get(baseURL + "sys/sysaftersalesrefunds/getInfo?refundsId="+refundsId, function(r){
                vm.refunds = r.refunds;
                if(r.refunds.evidencePic!=null && r.refunds.evidencePic!=''){
                    vm.pics=r.refunds.evidencePic;
                    vm.picArray=vm.pics.split(",");
                }else{
                    vm.picArray=[];
                }
                vm.orderData = r.orderData;
                $('#refundsModal').modal();
            });
        },
        excel:function(){
            var itemIds = getSelectedRows();
            if(itemIds == null){
                return ;
            }
            window.location.href=baseURL + "sys/sysaftersalesrefunds/excel?refundsIds="+itemIds;
        },
        img(pic){
            $("#evidencepicww").attr('src',pic);
            $("#photomax_modal").modal();
        },
        audit(refundsId) {
            $.get(baseURL + "sys/sysaftersalesrefunds/getReturn", function(r){
                vm.returnAdd=r.returnAdd;
                vm.sysaftersalesRefunds.refundsId = refundsId;
                $('#projectModal2').modal();
            });
        },
        audit_pass(refundsId){
            var formdata = new FormData();
            if(vm.returnAdd.address==null || vm.returnAdd.address==''){
                alert("回寄地址不能为空");
                return;
            }
            formdata.append("refundsId", refundsId);
            formdata.append("returnAddress", vm.returnAdd.address);
            $.ajax({
                url: baseURL + "sys/sysaftersalesrefunds/pass",
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        alert('保存成功', function (index) {
                            $("#projectModal2").modal('hide');
                            vm.reload();
                            vm.clear();
                        });
                    } else {
                        alert(r.message);
                    }
                }
            });
        },
        refund(refundsId){
            var formdata = new FormData();
            formdata.append("refundsId", refundsId);
            $.ajax({
                url: baseURL + "sys/sysaftersalesrefunds/refund",
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        alert('退款成功', function (index) {
                            vm.reload();
                            vm.clear();
                        });
                    } else {
                        alert(r.message);
                    }
                }
            });
        },
        returnAddress(){
            $.get(baseURL + "sys/sysaftersalesrefunds/getReturn", function(r){
                vm.returnAdd=r.returnAdd;
                $("#returnModal").modal();
            });
        },
        saveAddress(){
            var formdata = new FormData();
            if(vm.returnAdd.returnId != null && vm.returnAdd.returnId != 'null' && vm.returnAdd.returnId != ''){
                formdata.append("returnId", vm.returnAdd.returnId);
            }
            formdata.append("address", vm.returnAdd.address);
            $.ajax({
                url: baseURL + "sys/sysaftersalesrefunds/updateReturn",
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        $("#returnModal").modal('hide');
                        vm.reload();
                        vm.clear();
                        alert('保存成功');
                    } else {
                        alert(r.message);
                    }
                }
            });
        },
        audit_fail(refundsId){
            var formdata = new FormData();
            formdata.append("refundsId", refundsId);
            formdata.append("adminExplanation", vm.sysaftersalesRefunds.adminExplanation);
            $.ajax({
                url: baseURL + "sys/sysaftersalesrefunds/fail",
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        alert('保存成功', function (index) {
                            $("#projectModal2").modal('hide');
                            vm.reload();
                            vm.clear();
                        });
                    } else {
                        alert(r.message);
                    }
                }
            });
        },
        saveOrUpdate: function (event) {
            $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function () {
                var url = vm.sysaftersalesRefunds.refundsId == null ? "sys/sysaftersalesrefunds/save" : "sys/sysaftersalesrefunds/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.sysaftersalesRefunds),
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
        clear(){
            vm.sysaftersalesRefunds={};
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'tid': vm.q.tid, 'status': vm.q.status},
                page: page
            }).trigger("reloadGrid");

        }
    }
});