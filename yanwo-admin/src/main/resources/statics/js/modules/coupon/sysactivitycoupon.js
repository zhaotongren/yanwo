$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'yanwo/sysactivitycoupon/list',
        datatype: "json",
        colModel: [			
			{ label: 'couponId', name: 'couponId', index: 'coupon_id', width: 50, key: true, hidden:true },
			{ label: '优惠券名称', name: 'couponName', index: 'coupon_name', width: 80 }, 			
			{ label: '详细说明', name: 'couponDetails', index: 'coupon_details', width: 80 },
			{ label: '金额', name: 'money', index: 'money', width: 50 },
            { label: '使用条件', name: 'restrictMoney', index: 'money', width: 50 },
			{ label: '数量', name: 'count', index: 'count', width: 50 },
			{ label: '每人限领数量', name: 'restrictNumber', index: 'restrict_number', width: 50},
			{ label: '到期时间', name: 'expireTime', index: 'expire_time', width: 80, formatter:function(cellvalue){
                    return formatDate(cellvalue,1);
                }},
			{ label: '状态', name: 'couponStatus', index: 'coupon_status', width: 40, formatter:function (cellvalue) {
                    if (cellvalue==0){
                        return '正常';
                    }else if (cellvalue==1) {
                        return '禁用';
                    }else {
                        return '已过期';
                    }
                }},
			{ label: '已领取', name: 'alreadyGet', index: 'alreadyGet', width: 40},
            { label: '已使用', name: 'alreadyUse', index: 'alreadyUse', width: 40}
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
function formatDate(value,type) {
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
    var t;
    if(type==1){
        t = Y+'-'+m+'-'+d+' '+H+':'+i+':'+s;
    }else{
        t = Y+'-'+m+'-'+d;
    }
    <!-- 获取时间格式 2017-01-03 -->
    //var t = Y + '-' + m + '-' + d;
    return t;
};

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		sysactivityCoupon: {
            couponStatus:0,
            expireTime:''
        },
        expireTime:''
	},
	methods: {
	    dateToTimestamp:function(timeStr){
            var date = new Date(timeStr+' 23:59:59');
            var time3 = Date.parse(date);
            return time3/1000;
        },
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.sysactivityCoupon = {
                couponStatus:0,
                expireTime:''
            };
			this.expireTime='';
		},
		update: function (event) {
            this.sysactivityCoupon= {
                couponStatus:0,
                expireTime:''
            }
            this.expireTime='';
			var couponId = getSelectedRow();
			if(couponId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(couponId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.sysactivityCoupon.couponId == null ? "yanwo/sysactivitycoupon/save" : "yanwo/sysactivitycoupon/update";
                if (vm.expireTime==null||vm.expireTime==''){
                    alert("请选择到期时间！");
                    $('#btnSaveOrUpdate').button('reset');
                    $('#btnSaveOrUpdate').dequeue();
                    return false;

                }
                if (vm.sysactivityCoupon.money==null||vm.sysactivityCoupon.money==''){
                    alert("优惠券金额不能为空！");
                    $('#btnSaveOrUpdate').button('reset');
                    $('#btnSaveOrUpdate').dequeue();
                    return false;
                }
                if (vm.sysactivityCoupon.restrictMoney==null||vm.sysactivityCoupon.restrictMoney==''){
                    alert("优惠券不能使用金额不能为空！");
                    $('#btnSaveOrUpdate').button('reset');
                    $('#btnSaveOrUpdate').dequeue();
                    return false;
                }
                if (vm.sysactivityCoupon.count==null||vm.sysactivityCoupon.count==''){
                    alert("优惠券数量不能为空！");
                    $('#btnSaveOrUpdate').button('reset');
                    $('#btnSaveOrUpdate').dequeue();
                    return false;
                }
                if (vm.sysactivityCoupon.restrictNumber==null||vm.sysactivityCoupon.restrictNumber==''){
                    alert("每人限领数量不能为空！");
                    $('#btnSaveOrUpdate').button('reset');
                    $('#btnSaveOrUpdate').dequeue();
                    return false;
                }
                vm.sysactivityCoupon.expireTime=vm.dateToTimestamp(vm.expireTime);
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.sysactivityCoupon),
                    success: function(r){
                        if(r.code === 0){
                             layer.msg("操作成功", {icon: 1});
                            vm.sysactivityCoupon= {
                                    couponStatus:0,
                                    expireTime:''
                                }
                            vm.expireTime='';
                             vm.reload();
                             $('#btnSaveOrUpdate').button('reset');
                             $('#btnSaveOrUpdate').dequeue();
                        }else{
                            layer.alert(r.msg);
                            vm.sysactivityCoupon= {
                                couponStatus:0,
                                expireTime:''
                            }
                            vm.expireTime='';
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        }
                    }
                });
			});
		},
		del: function (event) {
			var couponIds = getSelectedRows();
			if(couponIds == null){
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
                        url: baseURL + "yanwo/sysactivitycoupon/delete",
                        contentType: "application/json",
                        data: JSON.stringify(couponIds),
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
		getInfo: function(couponId){
	        var _this = this;
			$.get(baseURL + "yanwo/sysactivitycoupon/info/"+couponId, function(r){
                r.sysactivityCoupon.expireTime=formatDate(r.sysactivityCoupon.expireTime,2);
                vm.sysactivityCoupon = r.sysactivityCoupon;
                vm.expireTime=new Date(r.sysactivityCoupon.expireTime);
                _this.$forceUpdate();
            });
		},
		reload: function (event) {
			vm.showList = true;
            vm.sysactivityCoupon= {
                couponStatus:0,
                expireTime:''
            }
            vm.expireTime='';
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});