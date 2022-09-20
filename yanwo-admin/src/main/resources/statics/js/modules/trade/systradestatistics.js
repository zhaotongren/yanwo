$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/systradetrade/list',
        datatype: "json",
        colModel: [			
			{ label: '订单编号', name: 'tid', index: 'tid', width: 50, key: true },
			{ label: '用户id', name: 'userId', index: 'user_id', width: 80 }, 			
			{ label: '收货地址id', name: 'addrId', index: 'addr_id', width: 80 }, 			
			{ label: '状态', name: 'status', index: 'status', width: 80 ,formatter:function(cellvalue){
                if(cellvalue == '1'){
                    return "代付款";
                }else if(cellvalue == '2'){
                    return "代发货";
                }else if(cellvalue == '3'){
                    return "待收货";
                }else if(cellvalue == '4'){
                    return "已完成";
                }else{
                    return "已完结";
                }
            }},
			{ label: '商品总金额', name: 'totalFee', index: 'total_fee', width: 80 }, 			
			{ label: '实付金额,订单最终总额', name: 'payment', index: 'payment', width: 80 }, 			
			{ label: '抵扣金额', name: 'welfareFee', index: 'welfare_fee', width: 80 }, 			
			{ label: '订单创建时间', name: 'createdTime', index: 'created_time', width: 80, formatter:function(cellvalue){
                return formatDate(cellvalue);
            }},
			{ label: '付款时间', name: 'payTime', index: 'pay_time', width: 80 , formatter:function(cellvalue){
                return formatDate(cellvalue);
            }},
			{ label: '发货时间', name: 'consignTime', index: 'consign_time', width: 80 , formatter:function(cellvalue){
                return formatDate(cellvalue);
            }},
			{ label: '修改时间', name: 'modifiedTime', index: 'modified_time', width: 80 , formatter:function(cellvalue){
                return formatDate(cellvalue);
            }},
			{ label: '订单完成时间', name: 'endTime', index: 'end_time', width: 80 , formatter:function(cellvalue){
                return formatDate(cellvalue);
            }},
			{ label: '评论', name: 'remark', index: 'remark', width: 80 },
			{ label: '收货人姓名', name: 'receiverName', index: 'receiver_name', width: 80 }, 			
			{ label: '收货人所在省份', name: 'receiverState', index: 'receiver_state', width: 80 }, 			
			{ label: '收货人所在城市', name: 'receiverCity', index: 'receiver_city', width: 80 }, 			
			{ label: '收货人所在地区', name: 'receiverDistrict', index: 'receiver_district', width: 80 }, 			
			{ label: '收货人详细地址', name: 'receiverAddress', index: 'receiver_address', width: 80 }, 			
			{ label: '收货人手机号', name: 'receiverMobile', index: 'receiver_mobile', width: 80 },
            { label: '操作', name: 'statusValue', index: 'status', width: 50 ,edittype:"button", formatter:function(cellvalue){
                    if("1"==cellvalue){
                        return "<button class='btn btn-primary' onclick='updateLogistics()' type='button'><i class=\"fa fa-pencil-square-o\"></i>发货</button>";
                    }else {
                        return""
                    }
                }
            },
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
		systradeTrade: {
            tradeMoney:'',
            afterSaleMoney:''
        },
        q:{
            startime:'',
            endtime:'',
        },
	},
    created:function(){
        this.reload2();
    },
	methods: {
        reload2:function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/systradetrade/statistics",
                data: {
                    'startTime':this.q.startime,
                    'endTime':this.q.endtime
                },
                success: function(r){
                    if(r.tradeMoney!=null){
                        vm.systradeTrade.tradeMoney=r.tradeMoney;
                    }else{
                        vm.systradeTrade.tradeMoney=0;
                    }
                    if(r.afterSaleMoney!=null){
                        vm.systradeTrade.afterSaleMoney=r.afterSaleMoney;
                    }else{
                        vm.systradeTrade.afterSaleMoney=0;
                    }
                }
            })
        },
	}
});