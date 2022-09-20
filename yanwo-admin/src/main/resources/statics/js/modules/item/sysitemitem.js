$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysitemitem/list',
        datatype: "json",
        colModel: [			
			{ label: 'itemId', name: 'itemId', index: 'item_id', width: 90, key: true,align: "center", hidden:true},
            {
                label: '操作', width: 250, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    var str = "<a style='width:10px' class='label label-success' href='#' onclick='vm.getInfo("+row['itemId']+")' >编辑</a>&nbsp;&nbsp;&nbsp;"
                    + "<a style='width:10px' class='label label-success' href='#' onclick='vm.getCate("+row['itemId']+")' >修改分类</a>&nbsp;&nbsp;&nbsp;";
                    if(row['itemType']=='0'){
                        str += "<a style='width:10px' class='label label-success' href='#' onclick='vm.getItem("+row['itemId']+")' >加入积分商品</a>&nbsp;&nbsp;&nbsp;";
                    }else{
                        str += "<a style='width:10px' class='label label-success' href='#' onclick='vm.getItem("+row['itemId']+")' >删除积分商品</a>&nbsp;&nbsp;&nbsp;"
                    }
                    return str;
                }
            },
			{ label: '商品标题', name: 'title', index: 'title', width: 80 },
            {label: '是否是积分商品', name: 'itemType', index: 'item_type', width: 80,
                formatter: function (cellvalue) {
                    if (cellvalue == '0') {
                        return "否";
                    }else{
                        return "是";
                    }

                }
            },
            {label: '是否是推荐商品', name: 'isRecommend', index: 'is_recommend', width: 80,
                formatter: function (value, options, row) {
                    if(row['isRecommend']=='0'){
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.openRecommend("+row['itemId']+","+row['isRecommend']+")' >加入推荐商品</a>&nbsp;&nbsp;&nbsp;";
                    }else{
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.openRecommend("+row['itemId']+","+row['isRecommend']+")' >移除推荐商品</a>&nbsp;&nbsp;&nbsp;"
                    }
                }
            },
            {label: '是否是秒杀商品', name: 'isSeckill', index: 'is_seckill', width: 80,
                formatter: function (value, options, row) {
                    if(row['isSeckill'] == '0') {
                        return "<a style='width:10px' class='label label-success' href='#' onclick='vm.openSeckill(" + row['itemId'] + "," + row['isSeckill'] + ")' >加入秒杀</a>&nbsp;&nbsp;&nbsp;";
                    }
                    return "";
                }
            },
            {
                label: '商品分类', width: 150, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    return row['parentCatName']+"-->>"+row['catName'];
                }
            },
            { label: '图片', name: 'imageDefault', index: 'image_default', width: 80,formatter:function(cellvalue){
                    return "<img src="+cellvalue+" style='width:60px'>";
                } },
            { label: '商品信息',  width: 80,
                formatter: function (value, options, row) {
                    return '<a style="width:10px" class="label label-success" href="#" onclick="vm.itemLink('+row.itemId+','+row.itemType+')">商品链接</a>&nbsp;&nbsp;&nbsp;'
                        /*+'<a style="width:10px" class="label label-success" href="#" onclick="vm.catLink('+row.catId+')">分类链接</a>&nbsp;&nbsp;&nbsp;'*/;
             }
            },
            {label: '销售价', width: 100, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                    if(row['minPrice']==row['maxPrice']){
                        return row['minPrice'];
                    }else{
                        return row['minPrice']+"~"+row['maxPrice'];
                    }
                }
            },
            {label: '积分价', width: 100, align: "center", valign: 'middle',
                formatter: function (value, options, row) {
                if(row['itemType']==1){
                    if(row['minIntegral']==row['maxIntegral']){
                        return row['minIntegral'];
                    }else{
                        return row['minIntegral']+"~"+row['maxIntegral'];
                    }
                }else{
                    return "";
                }

                }
            },
			{ label: '状态', name: 'approveStatus', index: 'approve_status', width: 80 ,formatter:function(cellvalue){
                    if(cellvalue == '0'){
                        return "已删除";
                    }else if(cellvalue == '1'){
                        return "未上架";
                    }else{
                        return "已上架";
                    }
                }},
            { label: '实际销售量', name: 'soldNum', index: 'sold_num', width: 80 },
            { label: '自定义销售量', name: 'customSales', index: 'custom_sales', width: 80 },
            { label: '库存', name: 'store', index: 'store', width: 80 },
            { label: '权重', name: 'itemSort', index: 'item_sort', width: 80 },
            { label: '创建时间', name: 'createdTime', index: 'created_time', width: 150 , formatter:function(cellvalue){
                    return formatDate(cellvalue);
                }},
            /*{ label: '最后更新时间', name: 'modifiedTime', index: 'modified_time', width: 80 , formatter:function(cellvalue){
                return formatDate(cellvalue);
            }},*/
            /*{label: '权重', name: 'itemSort', index: 'item_sort'}*/
        ],
		viewrecords: true,
        height: 600,
        rowNum: 10,
        shrinkToFit:false,
        autoScroll: true,
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
        	//$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "auto" }); //需要拖拉才能显示水平滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "scroll" });// 一直显示水平滚动条

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
		sysitemItem: {},
        itemtitle:'',
        approveStatus:'',
        imageList:[],
        isShow:true,
        fcatdata:[],
        fCatId: '',
        scatdata:[],
        sku_list:[{
            itemId:'',
            skuId:'',
            title:'',
            price:'',
            costPrice:'',
            mktPrice:'',
            store:'',
            img:'',
            integral:'',
            seckillPrice:'',
            seckillStock:'',
            startTime:'',
            endTime:'',
            barcode:''
        }],
        freights:'',
        sku:{},
        sku_startTime:'',
        sku_endTime:'',
        isIntegral:'',
        itemType:'',
        isRecommend:''
	},
    created:function(){
	   console.log('123')
    },
	methods: {

        itemLink(itemId,catId){
            alert("商品链接："+"/pages/home/shop_detail/shop_detail?id="+itemId+"&itemtype="+catId);
        },

        guize_add:function(){
            if(this.sku_list.length==0){
                this.sku_list.push({
                    skuId:'',
                    title:'',
                    price:'',
                    costPrice:'',
                    mktPrice:'',
                    store:'',
                    img:'',
                    integral:''
                })
                return false;
            }
            for(var i = 0;i<this.sku_list.length;i++){
                if(this.sku_list[i].title==''||this.sku_list[i].price==''||this.sku_list[i].costPrice==''||this.sku_list[i].mktPrice==''
                    ||this.sku_list[i].img==''||this.sku_list[i].store==''){
                    alert('上面信息不能为空！');
                    return false
                }
                if(i==this.sku_list.length-1){//说明全部不为空
                    this.sku_list.push({
                        skuId:'',
                        title:'',
                        price:'',
                        costPrice:'',
                        mktPrice:'',
                        store:'',
                        img:'',
                        integral:''
                    })
                    console.log(JSON.stringify(this.sku_list));
                    break;
                }
            }
        },
        guize_del:function(index){
            this.sku_list.splice(index,1);

        },
		query: function () {
			vm.reload();
		},
		add: function(){
            vm.clear();
			vm.showList = false;
			vm.title = "新增";
			vm.sysitemItem = {};
            vm.isShow=true;
            $.get(baseURL + "sys/sysfreight/freightList", function(r){
                vm.freights = r.freights;
            });
            $.get(baseURL + "/category/firstCat", function(r){
                vm.fcatdata = r.cate;
                $('#addCourseModal').modal();
            });
            $('.summernote').summernote("code",'');
		},
        getSecondCat(catId){
            if(typeof (catId) != "undefined" || catId !== undefined){
                $.get(baseURL + "/category/getSecondCat?catId="+catId, function(r){
                    vm.scatdata = r.cate;
                });
            }
        },
/*        changeFreight(ele){
            vm.sysitemItem.freightId = $(ele.target).find("option:selected").val();
        },*/
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
                var imgUrl=vm.imageList.join().slice(0,vm.imageList.join().length);
                var Data = {
                    "itemId":vm.sysitemItem.itemId,
                    "catId":vm.sysitemItem.catId,
                    "title":vm.sysitemItem.title,
                    "subTitle":vm.sysitemItem.subTitle,
                    "freightId":vm.sysitemItem.freightId,
                    "listImage":imgUrl,
                    "unit":vm.sysitemItem.unit,
                    "itemType":vm.sysitemItem.itemType,
                    "approveStatus": 1,
                    "price":vm.sysitemItem.price,
                    "description":encodeURI(vm.contentHtml),
                    "itemSort":vm.sysitemItem.itemSort,
                    "customSales":vm.sysitemItem.customSales,
                    "sysitemSkuEntities":vm.sku_list,
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
        getCate(itemId){
            vm.clear();
            vm.sysitemItem.itemId=itemId;
            $.get(baseURL + "/category/firstCat", function(r){
                vm.fcatdata = r.cate;
                $('#itemCateModal').modal();
            });
        },
        updateItemCat(itemId){
            var formdata = new FormData();
            formdata.append("itemId", itemId);
            formdata.append("catId", vm.sysitemItem.catId);
            $.ajax({
                url: baseURL + "sys/sysitemitem/updateCate",
                type: "POST",
                data: formdata,
                contentType: false,
                processData: false,
                success: function (r) {
                    if (r.code === 0) {
                        alert('保存成功', function (index) {
                            $("#itemCateModal").modal('hide');
                            vm.reload();
                            vm.clear();
                        });
                    } else {
                        alert(r.message);
                    }
                }
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
            vm.showList = false;
            $.get(baseURL + "sys/sysfreight/freightList", function(r){
                vm.freights = r.freights;
            });
			$.get(baseURL + "sys/sysitemitem/info/"+itemId, function(r){
                $('.summernote').summernote("code",decodeURI(r.sysitemItem.description));
                vm.sysitemItem = r.sysitemItem;
                if(vm.sysitemItem.itemType=='1'){
                    vm.isIntegral=true;
                }else{
                    vm.isIntegral=false;
                }
                vm.sku_list = r.sku;
                vm.isShow=false;
                vm.imageList = r.sysitemItem.listImage.split(",");
            });
		},
        getItem(itemId){
		    vm.clear();
            $.get(baseURL + "sys/sysitemitem/info/"+itemId, function(r){
                vm.sysitemItem = r.sysitemItem;
                vm.sku_list = r.sku;
                vm.isShow=false;
                if(vm.sysitemItem.itemType=='0'){
                    $('#skuModal').modal();
                }else{
                    $('#skuModal2').modal();
                }

            });
        },
        addIntegral(){
		    var arr=[];
            for(var i=0;i<vm.sku_list.length;i++){
                arr.push({
                    skuId:vm.sku_list[i].skuId,
                    integral:vm.sku_list[i].integral,
                })
                if(i==vm.sku_list.length-1){
                }
            }

            var Data = {
                "sysitemSkuEntities":arr,
                "itemId":vm.sysitemItem.itemId
            }
            console.log('传参='+JSON.stringify(Data));
            $.ajax({
                type: "POST",
                url: baseURL + "sys/sysitemitem/addIntegralItem",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(Data),
                success: function(r){
                    if(r.code === 0){
                        alert("修改成功");
                        $('#skuModal').modal('hide');
                        vm.reload();
                    }else{
                        alert(r.msg);
                    }
                }
            });

        },
        deleteIntegral(){
            var arr=[];
            for(var i=0;i<vm.sku_list.length;i++){
                arr.push({
                    skuId:vm.sku_list[i].skuId,
                    integral:vm.sku_list[i].integral,
                    price:vm.sku_list[i].price,
                    costPrice:vm.sku_list[i].costPrice,
                    mktPrice:vm.sku_list[i].mktPrice,
                })
                if(i==vm.sku_list.length-1){
                }
            }

            var Data = {
                "sysitemSkuEntities":arr,
                "itemId":vm.sysitemItem.itemId
            }
            console.log('传参='+JSON.stringify(Data));
            $.ajax({
                type: "POST",
                url: baseURL + "sys/sysitemitem/cancelIntegralItem",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(Data),
                success: function(r){
                    if(r.code === 0){
                        alert("修改成功");
                        $('#skuModal2').modal('hide');
                        vm.reload();
                    }else{

                    }
                }
            });
        },
		reload: function (event) {
		    vm.imageList=[];
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{'title':vm.itemtitle,'approveStatus':vm.approveStatus,"itemType":vm.itemType,"isRecommend":vm.isRecommend},
                page:page
            }).trigger("reloadGrid");
		},
        del_pic:function(index){
		    vm.imageList.splice(index,1);
            console.log('123=='+index+'删除之后=='+JSON.stringify(vm.imageList));
        },

        upload1: function(event,index){
            console.log('111='+index);
            var formData = new FormData();
            // formData.append("file", $("#file1")[index].files[0]);
            formData.append("file", event.target.files[0]);
            var url=baseURL + "file/upload";
            $.ajax({
                url: url,
                type: "post",
                data: formData ,
                processData: false,
                contentType: false,
                success: function(data) {
                    console.log("-------"+data.msg)
                    vm.sku_list[index].img=data.msg;
                },
                error: function(error){}
            });
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
                        if(data.msg!=''){
                            vm.imageList.push(data.msg)
                        }
                        // vm.sysitemItem.imageDefaultId=data.msg;
                        // $("#itemImage").attr("src",vm.sysitemItem.imageDefaultId);
                        // $("#itemImage").append('<img :src='+vm.sysitemItem.imageDefaultId+' width="60px"/>');
                    }else{
                    }
                }
            });

        },
        clear() {
            vm.sku_list=[{}];
            vm.isIntegral='';
        },
        upload2:function(file){
            var formData = new FormData();
            formData.append("file", file);
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
        openRecommend: function (itemId,isRecommend) {
            vm.sysitemItem.itemSort = '';
            vm.sysitemItem.itemId = itemId;
		    if(isRecommend == 0){
                $('#recommendModal').modal();
            }else{
                confirm('确定要移除推荐商品？', function(){
                    vm.setRecommend();
                });
            }
        },
        setRecommend(){
            var Data = {
                "itemId":vm.sysitemItem.itemId,
                "itemSort":vm.sysitemItem.itemSort
            }
            $.ajax({
                type: "POST",
                url: baseURL + "sys/sysitemitem/setRecommend",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(Data),
                success: function(r){
                    if(r.code === 0){
                        alert("操作成功");
                        $('#recommendModal').modal('hide');
                        vm.reload();
                    }else{

                    }
                }
            });
        },
        openSeckill: function (itemId,isSeckill) {
            vm.sysitemItem.itemId = itemId;
            if(isSeckill == 0){
                //是否是单规格商品
                $.get(baseURL + "sys/sysitemitem/info/"+itemId, function(r){
                    vm.sysitemItem = r.sysitemItem;
                    vm.sku_list = r.sku;
                    if(vm.sku_list.length > 1){
                        alert("只有单规格商品才能加入秒杀专区哦！");
                    }else{
                        $('#seckillModal').modal();
                    }
                });
            }
        },
        setSeckill(){
            var arr=[];
            console.log(JSON.stringify(vm.sku_list));
            for(var i=0;i<vm.sku_list.length;i++){

                arr.push({
                    itemId:vm.sku_list[i].itemId,
                    skuId:vm.sku_list[i].skuId,
                    seckillStock:vm.sku_list[i].seckillStock,
                    seckillPrice:vm.sku_list[i].seckillPrice,
                    startTime:vm.sku_startTime/1000,
                    endTime:vm.sku_endTime/1000,
                })
            }
            console.log(JSON.stringify(arr));
            var Data = {
                "sysitemSeckills":arr
            }
            console.log('传参='+JSON.stringify(Data));
            $.ajax({
                type: "POST",
                url: baseURL + "sys/sysitemitem/addSeckillItem",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(Data),
                success: function(r){
                    if(r.code === 0){
                        alert("操作成功");
                        $('#seckillModal').modal('hide');
                        vm.reload();
                    }else{
                        layer.alert(r.msg);
                    }
                }
            });
        },

	}
});