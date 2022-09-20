$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysitemseckill/list',
        datatype: "json",
        colModel: [
            /**{
                label: '操作', width: 80, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    if(row['status']=='0' || row['status']=='1'){
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.getInfo("+row['id']+")' >编辑</a>&nbsp;&nbsp;&nbsp;";
                    }else{
                        return "";
                    }
                }
            },**/
			{ label: '秒杀ID', name: 'id', index: 'id', width: 50, key: true },
            { label: '商品主图', name: 'goodsImg', index: 'goods_img', width: 80,formatter:function(cellvalue){
                    return "<img src="+cellvalue+" style='width:60px'>";
                } },
            // { label: '商品ID', name: 'itemId', index: 'item_id', width: 80 },
			// { label: '规格ID', name: 'skuId', index: 'sku_id', width: 80 },
			{ label: '商品名', name: 'goodsTitle', index: 'goods_title', width: 80 },
            { label: '商品规格', name: 'goodsSpec', index: 'goods_spec', width: 80 },
            { label: '秒杀价', name: 'seckillPrice', index: 'seckill_price', width: 80 },
            { label: '秒杀剩余库存', name: 'seckillStock', index: 'seckill_stock', width: 80 },
			{ label: '商品售价', name: 'goodsPrice', index: 'goods_price', width: 80 },
			// { label: '商品库存', name: 'goodsStock', index: 'goods_stock', width: 80 },
            { label: '开始时间', name: 'startTime', index: 'start_time', width: 80 , formatter:function(cellvalue){
                    return formatDate(cellvalue);
                }},
            { label: '结束时间', name: 'endTime', index: 'end_time', width: 80 , formatter:function(cellvalue){
                    return formatDate(cellvalue);
                }},
            { label: '创建时间', name: 'createTime', index: 'create_time', width: 80 , formatter:function(cellvalue){
                    return formatDate(cellvalue);
                }},
            { label: '状态', name: 'status', index: 'status', width: 80 ,formatter:function(cellvalue){
                    if(cellvalue == 0){
                        return "未开始";
                    }else if(cellvalue == 1){
                        return "秒杀中";
                    }else if(cellvalue == 2){
                        return "已结束";
                    }else{
                        return "已关闭";
                    }
                }}
        ],
		viewrecords: true,
        height: 600,
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
		sysitemSeckill: {
            startTime:'',
            endTime:''
        },
        goodstitle: '',
        status: '',
        startTime:'',
        endTime:'',

	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.sysitemSeckill = {
                startTime:'',
                endTime:''
            };
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
                var url = vm.sysitemSeckill.id == null ? "sys/sysitemseckill/save" : "sys/sysitemseckill/update";
                var Data = {
                    "id":vm.sysitemSeckill.id,
                    "seckillStock":vm.sysitemSeckill.seckillStock,
                    "seckillPrice":vm.sysitemSeckill.seckillPrice,
                    "startTime":vm.sysitemSeckill.startTime/1000,
                    "endTime":vm.sysitemSeckill.endTime/1000
                }
                console.log('传参='+JSON.stringify(Data));
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
                            vm.imageList=[];
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
            layer.confirm('确定要移除选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
		            $.ajax({
                        type: "POST",
                        url: baseURL + "sys/sysitemseckill/delete",
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
        close: function (event) {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }
            var lock = false;
            layer.confirm('确定要关闭选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
                if(!lock) {
                    lock = true;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "sys/sysitemseckill/close",
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
		    vm.showList = false;
			$.get(baseURL + "sys/sysitemseckill/info/"+id, function(r){
                r.sysitemSeckill.endTime=new Date(r.sysitemSeckill.endTime).getTime();
                r.sysitemSeckill.startTime=new Date(r.sysitemSeckill.startTime).getTime();
                vm.sysitemSeckill = r.sysitemSeckill;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{'goodsTitle':vm.goodsTitle,'status':vm.status},
                page:page
            }).trigger("reloadGrid");
		}
	}
});