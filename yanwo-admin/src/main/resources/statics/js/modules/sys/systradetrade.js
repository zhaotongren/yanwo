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
function updateLogistics() {

    vm.updateLogistics();
}
function delivery() {

    vm.delivery();
}
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		systradeTrade: {},
        q:{
            mobile:'',
            status:'',
            tid:'',
            userName:'',
            startime:'',
            endtime:'',
            pageNum:'',
            pageSize:'10'
        },
        logistics:[],
        page:{

        },
        showOrderFlag:0,
        tids:[],
        capitalFeeSum:'',
        totalFeeSum:''
	},

    created:function(){
        this.getLogisticsList();
        this.sumMoney();
        this.pageList();
    },
	methods: {
        sumMoney:function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/systradetrade/statistics",
                data: {
                    'status':this.q.status,
                    'tid':this.q.tid,
                    'userName':this.q.userName,
                    'startime':this.q.startime,
                    'endtime':this.q.endtime,
                    'mobile':this.q.mobile
                },
                success: function(r){
                    console.info("==============="+JSON.stringify(r));
                    vm.capitalFeeSum = r.data.capitalFeeSum;
                    vm.totalFeeSum = r.data.totalFeeSum;
                }
            })
        },
        excel:function(){
            window.location.href=baseURL + "excel/tradeTreadExcel?tids="+vm.tids;
        },
        checkall:function(cmd){
            if(cmd===1){
                $(".checkbox-diy").css("display","none");
                $(".checkbox-diy-hide").css("display","block");
                for(var i in vm.page.list){
                    vm.tids.push(vm.page.list[i].tid);
                }
            }else{
                $(".checkbox-diy").css("display","block");
                $(".checkbox-diy-hide").css("display","none");
                vm.tids=[];
            }

        },
        checkboxs:function(name){
            var value=$("span[name="+name+"]").css("display");
            if(value=="block"){
                vm.tids.push(name);

                $("span[name="+name+"]").css("display","none");
                $("span[name="+name+"]").next().css("display", "block");
            }else{
                for(var i in vm.tids){
                    if(vm.tids[i]==name){
                        vm.tids.splice(i,1)
                    }
                }
                $("span[name="+name+"]").css("display","block");
                $("span[name="+name+"]").next().css("display", "none");
            }

            console.log(vm.tids.toString());
        },
        pageList:function(page,totalpage){
            if(page>totalpage){
                return ;
            }
            if(page<0){
                return;
            }
            this.q.pageNum=page;
            this.reload2();
        },
        reload2:function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/systradetrade/list",
                data: {
                    'status':this.q.status,
                    'tid':this.q.tid,
                    'userName':this.q.userName,
                    'startime':this.q.startime,
                    'mobile':this.q.mobile,
                    'endtime':this.q.endtime,
                    'pageSize':this.q.pageSize,
                    'pageCurrent':this.q.pageNum
                },
                success: function(r){

                    vm.page=r.page;
                    vm.sumMoney();

                }

            })
        },
        getLogisticsList:function(){
            var _this=this;
            $.ajax({
                type: "POST",
                url: baseURL + 'sys/systradelogistics/list2',
                contentType: "application/json",
                success: function(r){
                    if(r.code === 0){
                        _this.logistics=r.list
                    }else{

                    }
                }
            });
        },
		query: function () {
			vm.reload();
            vm.sumMoney();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.systradeTrade = {};
		},
		update: function (event) {
			var tid = getSelectedRow();
			if(tid == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(tid)
		},
        showOrders:function(tid){

            $("i[name="+tid+"]").css("display","none")
            $("#"+tid).css("display","block");
            $("#"+tid).css("position","inherit");
            $("i[name="+tid+"]").next().css("display","block")
        },
        hideOrders:function(tid){
            $("i[name="+tid+"]").css("display","block")
            $("#"+tid).css("display","none");
            $("#"+tid).css("position","absolute");
            $("i[name="+tid+"]").next().css("display","none")
        },
        delivery:function(tid){
            $.get(baseURL + "sys/systradetrade/info/"+tid, function(r){
                vm.systradeTrade = r.systradeTrade;
                if (vm.systradeTrade.status=="2"){
                    $(".popupyer1").show();
                    $(".popup").show();
                    vm.title = "发货";
                }else {
                    if (vm.systradeTrade.status=="1") {
                        alert("商品未支付，不能发货");
                        return;
                    }
                    if (vm.systradeTrade.status=="3") {
                        alert("商品已发货");
                        return;
                    }
                    if (vm.systradeTrade.status=="4") {
                        alert("交易已完成");
                        return;
                    }
                    if (vm.systradeTrade.status=="5") {
                        alert("交易已关闭");
                        return;
                    }
                    if (vm.systradeTrade.status=="6") {
                        alert("交易已取消");
                        return;
                    }
                }
            });
        },
        goods:function () {
            $.ajax({
                type: "POST",
                url: baseURL + "sys/systradetrade/updateDeliverGoods",
                contentType: "application/json",
                data: JSON.stringify(vm.systradeTrade),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            $(".popup").hide();
                            $(".popupyer1").hide();
                            vm.reload2();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        updateLogistics:function(tid){
            /* var tid = getSelectedRow();
             if(tid == null){
                 return ;
             }*/
            $.get(baseURL + "sys/systradetrade/info/"+tid, function(r){
                vm.systradeTrade = r.systradeTrade;
                if (vm.systradeTrade.status=="2"){
                    //vm.showList = false;
                    $(".popupyer").show();
                    $(".popup").show();
                    vm.title = "发货";
                }else {
                    if (vm.systradeTrade.status=="1") {
                        alert("商品未支付，不能发货");
                        return;
                    }
                    if (vm.systradeTrade.status=="3") {
                        alert("商品已发货");
                        return;
                    }
                    if (vm.systradeTrade.status=="4") {
                        alert("交易已完成");
                        return;
                    }
                    if (vm.systradeTrade.status=="6") {
                        alert("交易已取消");
                        return;
                    }
                    if (vm.systradeTrade.status=="5") {
                        alert("交易已关闭");
                        return;
                    }
                }
            });


        },
        deliverGoods:function(){
            if (vm.systradeTrade.courierName==null||vm.systradeTrade.courierName==""||vm.systradeTrade.courierName=="undefined"){
                alert("请选择快递公司");
                return;
            }
            if (vm.systradeTrade.courierNumber==null||vm.systradeTrade.courierNumber==""||vm.systradeTrade.courierNumber=="undefined"){
                alert("请填写快递单号");
                return;
            }
            $.ajax({
                type: "POST",
                url: baseURL + "sys/systradetrade/updateDeliverGoods",
                contentType: "application/json",
                data: JSON.stringify(vm.systradeTrade),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            $(".popup").hide();
                            $(".popupyer").hide();
                            vm.reload2();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.systradeTrade.tid == null ? "sys/systradetrade/save" : "sys/systradetrade/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.systradeTrade),
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
			var tids = getSelectedRows();
			if(tids == null){
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
                        url: baseURL + "sys/systradetrade/delete",
                        contentType: "application/json",
                        data: JSON.stringify(tids),
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
        returns:function(){
            $(".popup").hide();
            $(".popupyer").hide();
            $(".popupyer1").hide();
        },
		getInfo: function(tid){
			$.get(baseURL + "sys/systradetrade/info/"+tid, function(r){
                vm.systradeTrade = r.systradeTrade;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		},
        shooesstyle:function(str){

            if(str=="1"){
                return "background-color: rgba(3, 172, 95, 0.12)";
            }else if(str=="2"){
                return "background-color: rgba(255, 121, 61, 0.59)";
            }else if(str=="3"){
                return "background-color: rgba(207, 207, 207, 0.41)";
            }else if(str=="4"){
                return "background-color: rgba(255, 121, 61, 0.59)";
            }else{
                return "background-color: red(3, 172, 95, 0.12)";
            }

        }
	}
});