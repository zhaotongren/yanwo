

$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/sysfreight/list',
        datatype: "json",
        colModel: [			
			{ label: '操作', name: 'freightId', index: 'freight_id', width: 50, key: true,
                formatter:function (cellvalue){
                    return "<a style='width:10px' class='label label-success' href='#' onclick='vm.getInfo("+cellvalue+")' >编辑</a>&nbsp;&nbsp;&nbsp;";
                }
            },
			{ label: '名称', name: 'name', index: 'name', width: 80 },
			{ label: '基础邮费价格', name: 'basicsPostage', index: 'basics_postage', width: 80 },
			/*{ label: '状态', name: 'status', index: 'status', width: 80 }*/
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
		sysFreight: {},
        provice:[],//选择省列表
        city:[],//选择市列表
        provice_box_show:false,//省列表是否显示
        city_box_show:false,//市列表是否显示
        provice_actived:'',//当前省选择id
        city_actived:'',//当前市选择id
        provice_actived_txt:'',//当前省选择文字
        city_actived_txt:'',//当前市选择文字
        tongyi_tit:'',//统一邮费名称
        tongyi_txt:'',//统一邮费描述
        teshu_list:[],//特殊邮费list
        teshu_tit:'',//当前输入特殊邮费名称
        teshu_txt:'',//当前输入特殊邮费描述
        city_txt:'',//选择完成结合后的省-市，
        teshu_code:'',//选择完成结合后的省-市code，
        freightId:''
	},
    created:function(){
	    this.provice=cityData;
	    // console.log(this.provice.length+'=='+JSON.stringify(this.provice))

    },
	methods: {
        choose_provice:function(){
            this.provice_box_show=true;
            this.provice=cityData;
            console.log(JSON.stringify(this.provice));
            this.provice_actived='';//当前省选择id
            this.city_actived='';//当前市选择id
            this.provice_actived_txt='';//当前省选择文字
            this.city_actived_txt='';//当前市选择文字
            this.city_txt='';
            this.teshu_code=''
        },
        choose_city:function(index){
            this.city=this.provice[index].children;
            this.provice_actived=this.provice[index].value;
            this.provice_actived_txt=this.provice[index].text;
            this.city_box_show=true;

            if(this.city_actived_txt==''){
                this.city_txt=this.provice_actived_txt;
                this.teshu_code=this.provice_actived;
            }else{
                this.city_txt=this.provice_actived_txt+'-'+this.city_actived_txt;
                this.teshu_code=this.provice_actived+'/'+this.city_actived

            }

        },
        cityChoose_over:function(index){
            this.city_actived=this.city[index].value;
            this.city_actived_txt=this.city[index].text;
            this.city_box_show=false;
            this.provice_box_show=false;
            this.city_txt=this.provice_actived_txt+'-'+this.city_actived_txt;
            if(this.city_actived==''){
                this.teshu_code=this.provice_actived;
            }else{
                this.teshu_code=this.provice_actived+'/'+this.city_actived;
            }
        },
        add_teshu_list:function(){
            if(this.teshu_code==''||this.teshu_txt==''||this.city_txt==''){
                alert('请填写完整信息！');
                return false;
            }
            this.teshu_list.push({
                cityCode:this.teshu_code,
                cityName:this.city_txt,
                fee:this.teshu_txt
            })
            this.teshu_tit='';
            this.teshu_txt='';
            this.city_txt='';
            this.provice_actived='';//当前省选择id
            this.city_actived='';//当前市选择id
            this.provice_actived_txt='';//当前省选择文字
            this.city_actived_txt='';//当前市选择文字
            this.city_txt='';
            this.teshu_code=''
        },
        del_teshu_list:function(index){
            this.teshu_list.splice(index,1)
        },
		query: function () {
			vm.reload();
		},
		add: function(){
            vm.clear();
			vm.showList = false;
			vm.title = "新增";
			vm.sysFreight = {};
		},
         sels: function(data) {
            console.log(JSON.stringify(data));
        },
		update: function (event) {
			var freightId = getSelectedRow();
			if(freightId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(freightId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url='';
                var freightId=vm.freightId;
                if(freightId!=null && freightId !=''){
                    url="sys/sysfreight/update";
                }else{
                    url="sys/sysfreight/save"
                }
                var Data = {
                    "freightId":freightId,
                    "name": vm.tongyi_tit,
                    "basicsPostage":vm.tongyi_txt,
                    "specialPostage":JSON.stringify(vm.teshu_list)
                }
                /*JSON.stringify(Data)*/
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
			var freightIds = getSelectedRows();
			if(freightIds == null){
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
                        url: baseURL + "sys/sysfreight/delete",
                        contentType: "application/json",
                        data: JSON.stringify(freightIds),
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

        getInfo: function(freightId){
            vm.showList = false;
			$.get(baseURL + "sys/sysfreight/info/"+freightId, function(r){
                vm.freightId = r.sysFreight.freightId;
                vm.tongyi_tit = r.sysFreight.name;
                vm.tongyi_txt = r.sysFreight.basicsPostage;
                vm.teshu_list = r.sysFreight.specialPostage;
                vm.teshu_list=JSON.parse(vm.teshu_list);
            });
		},
        clear() {
            vm.sysFreight={};
            vm.provice=[];//选择省列表
            vm.city=[];//选择市列表
            vm.provice_box_show=false;//省列表是否显示
            vm.city_box_show=false;//市列表是否显示
            vm.provice_actived='';//当前省选择id
            vm.city_actived='';//当前市选择id
            vm.provice_actived_txt='';//当前省选择文字
            vm.city_actived_txt='';//当前市选择文字
            vm.tongyi_tit='';//统一邮费名称
            vm.tongyi_txt='';//统一邮费描述
            vm.teshu_list=[];//特殊邮费list
            vm.teshu_tit='';//当前输入特殊邮费名称
            vm.teshu_txt='';//当前输入特殊邮费描述
            vm.city_txt='';//选择完成结合后的省-市，
            vm.teshu_code='';//选择完成结合后的省-市code，
            vm.freightId=''
        },
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});
