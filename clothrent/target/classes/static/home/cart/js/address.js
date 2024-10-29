/**
 * 地址选择页面
 * 1.通过v-for 指令渲染地址书记以及数据过滤
 * 2. 地址卡片的选择
 */
var vm = new Vue({
    el:'.container',
    data:{
        addressList:[],     // 定义新的数组
        limitNum: 3,        //定义当前显示的条数
        currenIndex:0,      //
        shippingMethod:  1   //
    },
    computed:{ // 数组过滤器
        filterAddress:function () {
            // slice() 返回一个全新的数组 和 之前的数组没有关系
            return this.addressList.slice(0,this.limitNum); //分隔数组前3个
        }
    },
    mounted:function () { // 钩子函数
        this.$nextTick(function () {
            this.getAddressList();
        })
    },
    methods:{
        // 页面刚开始加载 请求的数据
        getAddressList:function () {
            var _this = this;
            this.$http.get("data/address.json").then(function (response) {
                var res = response.data;
                if( res.status == 0){ // 先判断是否请求成功
                    _this.addressList = res.result;
                }
            });
        },
        // 点击更多的方法
        loadMore:function () {
            this.limitNum = this.addressList.length;
        },
        // 点击设置为默认地址
        setDefault:function (addressId) {
            var _this = this;
            _this.addressList.forEach(function (item,index) {
                if(item.addressId == addressId){
                    item.isDefault = true;
                }else {
                    item.isDefault = false;
                }
            })
        }
    }
});