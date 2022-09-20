$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/syscapitalcapitalintegral/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 80,hidden:true},
            {label: '资产id', name: 'capitalId', index: 'capitalId', width: 80},
            {label: '用户名', name: 'userName', index: 'userName', width: 80},
            {label: '积分状态', name: 'integralType', index: 'integralType', width: 80,
                formatter: function (cellvalue) {
                   if(cellvalue=='1'){
                        return "产生";
                   }else{
                       return "消耗";
                   }
                }
            },
            { label: '积分', width: 50, key: true,
                formatter: function (value, options, row) {
                    if(row['integralType']=='1'){
                        return "+"+row['integralFee'];
                    }else{
                        return "-"+row['integralFee'];
                    }
                }
            },
            {label: '订单号', name: 'tid', index: 'tid', width: 80},
            {label: '子订单号', name: 'oid', index: 'oid', width: 80},
            /*{label: '状态', name: 'status', index: 'status', width: 80},*/
            {
                label: '创建时间', name: 'createdTime', index: 'created_time', width: 80, formatter: function (cellvalue) {
                    return formatDate(cellvalue);
                }
            }
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
        syscapitalCapitalDetail: {},
        q: {
            searchType: '',
            searchContent: ''
        },
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'searchType': vm.q.searchType, 'searchContent': vm.q.searchContent},
                page: page
            }).trigger("reloadGrid");
        }
    }
});