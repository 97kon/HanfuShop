package com.clothrent.craw;

import com.alibaba.fastjson.JSONObject;
import com.clothrent.common.HttpUtils;
import com.clothrent.common.RegexUtil;
import com.clothrent.common.file.FileUtils;
import com.clothrent.entity.SysCategory;
import com.clothrent.entity.SysDictValue;
import com.clothrent.entity.SysGoods;
import com.clothrent.service.SysCategoryService;
import com.clothrent.service.SysDictValueService;
import com.clothrent.service.SysGoodsService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 京东商品爬虫
 商品列表页

 https://search.jd.com/Search?keyword=%E5%8F%91%E5%A4%B9&enc=utf-8&wq=%E5%8F%91%E5%A4%B9

 关键字 keyword  wq

 销量从高到低  psort=3

 评论数从高到低  psort=4

 新品从高到低 psort=5

 价格从低到高 psort=2

 价格从高到低 psort=1

 页数 page   page =(i*2)+1  i从0开始

 sku  s     s=page*60+1
 *
 */
@Service
public class CrawlJingDongService extends  AbstractCrawService{
    private static  final Logger logger= LoggerFactory.getLogger(CrawlJingDongService.class);

    @Autowired
    SysDictValueService dictValueService;

    @Autowired
    SysCategoryService categoryService;

    // 原始的商品ID
    private volatile List<String> goodsIdList=new ArrayList<>();

    // 一些自己收集的独立ip代理
    private volatile Set<String> ipproxySet=new HashSet<>();

    // 是否需要用代理池
    private volatile boolean ifProxyPoll=false;

    // 是否需要用独立IP代理
    private volatile boolean if_ip_proxy=false;

    // 商品搜索列表页
    private  String listurl="https://search.jd.com/Search?enc=utf-8&click=0";
    // 商品详情页 https://detail.tmall.com/item.htm?id=568524031684
    private String detailurl="https://detail.tmall.com/item.htm?id=";

    @Value("${com.jane.chromdriver.path:C://Program Files//Python37//chromedriver.exe}")
    private String chromedrivePath;

    @Value("${com.jane.file.baseFilePath}")
    private String baseFilePath;

    @Autowired
    SysGoodsService goodsService;

    /**
     * 抓取方法入口
     * @param number
     * @param orderby 0 默认排序  1 价格升序  2 价格倒序
     * @param goodsName
     * @return
     * @throws InterruptedException
     */
    public Integer reptile(@RequestParam(required = false,defaultValue = "20") Integer number,@RequestParam(required = false,defaultValue = "0") String orderby, String goodsName,Integer secondCId) {
        // 记录本次抓取的数目
        AtomicInteger atomicInteger=new AtomicInteger(0);
        try {
            String originUrl=listurl+"&keyword="+goodsName+"&wq="+goodsName;
            switch (orderby){
                case "0":break;
                case "1":originUrl+="&psort=2";break;
                case "2":originUrl+="&psort=5";break;
                default:break;
            }
            logger.debug("当前要抓取的京东商城商品列表页是：{}",originUrl);
            String currurl=originUrl;
            Integer page=0;
            while(true){
                getPageData( currurl, atomicInteger,number,secondCId);
                if(atomicInteger.get()<number&&atomicInteger.get()!=99999999){
                    page++;
                    int currPage=(page*2)+1;
                    currurl=originUrl+"&page="+currPage;
                    currurl=currurl+"&s="+(page*60-4);

                }else{
                    break;
                }
            }

            logger.info("本次抓取京东平台数据条数为：{}",atomicInteger.get());
            return atomicInteger.get();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
            logger.error("京东商品抓取出错：{}",e.getMessage());
        } finally {
            //关闭WebDriver
            closeWebDriver();
        }
        return atomicInteger.get();
    }

    /**
     * 抓取每一页数据
     * @param sendUrl
     * @param atomicInteger
     * @return 返回当前页 number
     */
    private void getPageData(String sendUrl, AtomicInteger atomicInteger,Integer number,Integer secondCId) throws InterruptedException {
        logger.info("抓取京东商品列表页数据：{},当前累积抓取数量：{}",sendUrl,atomicInteger.get());
        ChromeOptions chromeOptions=new ChromeOptions();
        checkChromeOptions(chromeOptions);
        // 检测并处理代理
//        checkDictProxy();
//        if(ifProxyPoll) {
//            CrawlerProxy.wrapperChromeOptionsProxyPoll(chromeOptions);
//        }else if(if_ip_proxy){
//            //不使用代理
//            CrawlerProxy.wrapperChromeOptionsIpProxy(chromeOptions,ipproxySet);
//        }

        Document document = getDocumentBySelenium(chromeOptions, sendUrl, "J_goodsList", chromedrivePath,10);

        Element itemList = document.getElementById("J_goodsList");
        Element ulGoodsList = itemList.getElementsByClass("gl-warp clearfix").get(0);
        Elements children = ulGoodsList.children();
        logger.debug("当前页：{}，商品数量是：{}",sendUrl, children.size());
        String currDetailUrl="";
        for(Element element:children){
            try {
                String productId = element.attr("data-sku");//京东商品sku
                goodsIdList = goodsService.getOriginGoodsIdList();
                if(goodsIdList.contains(productId)){
                    logger.warn("当前京东商品已经存在，将开始抓取下一条数据，商品ID：{}",productId);
                    continue;
                }
                String dataspu = element.attr("data-spu");//京东商品spu
                Element elementImgDiv = element.getElementsByClass("p-img").get(0);//列表-item=商品图片层
                Element elemetChildImg = elementImgDiv.getElementsByTag("img").get(0);
                String incompleteSrc = elemetChildImg.attr("src");
                if (StringUtils.isEmpty(incompleteSrc)) {
                    incompleteSrc=elemetChildImg.attr("data-lazy-img");
                }
                incompleteSrc=incompleteSrc.replaceAll("n5","n1");
                incompleteSrc=incompleteSrc.replaceAll("n7","n1");
                String imgSrc ="https:"+incompleteSrc ;// 图片完整URL
                logger.debug("当前要抓取的商品图片为：{}",imgSrc);
//                String goodsPic1 = FileUtils.saveFileByUrl(imgSrc, baseFilePath);

                //详情URL
                currDetailUrl ="https:"+ elementImgDiv.getElementsByTag("a").get(0).attr("href");

                //价格
                String priceText = element.getElementsByClass("p-price").text();
                String productPrice = RegexUtil.parseGoalFromStr(priceText, "\\d+(\\.?\\d*)");

                //商品名称
                String productTitle = element.getElementsByClass("p-name p-name-type-2").get(0).getElementsByTag("em").text();

                //店铺名称
                String productShop = element.getElementsByClass("p-shop").text();

                // 评价数
                String regex="\\d+(\\.?\\d*)万?";
                String commentText = element.getElementsByClass("p-commit").text();
                logger.debug("获取当前商品的评价数为：{}",commentText);
                Double commentNum = RegexUtil.parseDoubleFromUnit(RegexUtil.parseGoalFromStr(commentText, regex));

                // 封装商品信息
                SysGoods sysGoods=new SysGoods();
                SysCategory secCategory = categoryService.getById(secondCId);
                sysGoods.setCategoryId(secCategory.getParentId());
                sysGoods.setCategoryName(secCategory.getParentName());
                sysGoods.setSecondCId(secondCId.longValue());
                sysGoods.setSecondCName(secCategory.getName());
//                sysGoods.setGoodsImage1(goodsPic1);
                sysGoods.setField0(productId);//原始商品ID
                sysGoods.setCode(productId);
                sysGoods.setField1(currDetailUrl);//详情页
                if(!StringUtils.isEmpty(productPrice)){
                    sysGoods.setPrice(new BigDecimal(productPrice));
                }
                sysGoods.setName(productTitle);
                sysGoods.setCommentNum(new BigDecimal(commentNum).intValue());
                if(sysGoods.getSaleNumber()==null||sysGoods.getSaleNumber()<1){
                    sysGoods.setSaleNumber(sysGoods.getCommentNum());
                }
//                sysGoods.setShopName(productShop);
//                sysGoods.setPlatform("京东");
//                sysGoods.setLink(currDetailUrl);
                // 多线程需要在这里检测、判断
//                goodsIdList = goodsService.getOriginGoodsIdList();
                if(!goodsIdList.contains(productId)){
                    //抓取详情
                    getGoodsDetail(sysGoods,currDetailUrl,chromeOptions);
                    //抓取好评率
                    getGoodsDetailComment(sysGoods,currDetailUrl+"#none",chromeOptions);
                    goodsService.save(sysGoods);
                    atomicInteger.getAndIncrement();
                    if(number<=atomicInteger.get()){
                        logger.warn("本次已经爬取足够数量商品，将结束爬取....,爬取数量是：{}",atomicInteger.get());
                        break;
                    }
                    logger.info("开始抓取当前页：{}，下一条数据：{}",sendUrl,atomicInteger.get());
                }else{
                    logger.warn("当前数据:{}已经存在，跳过进行下个爬取!!!!!!!",productId);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(),e);
                logger.error("抓取商品{},出错,开始抓取当前页：{}，下一条数据",currDetailUrl,sendUrl);
            }
            // 随机休眠
            Thread.sleep(new Random().nextInt(5)*500);
        }
    }

    /**
     * 抓取商品详情--好评率
     * @param sysGoods
     * @param detailUrl
     */
    public void getGoodsDetailComment(SysGoods sysGoods,String detailUrl,ChromeOptions chromeOptions)  {
        try {
            logger.info("开始抓取京东详情好评数据：{}", detailUrl);
            String originProductId = sysGoods.getField0();//原始商品ID
            //https://club.jd.com/comment/productCommentSummaries.action?referenceIds=100005907830
            String baseUrl="https://club.jd.com/comment/productCommentSummaries.action?referenceIds=";
            Map<String,String> headermap=new HashMap<>();
            headermap.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");
            String httpGetResult = HttpUtils.httpGet(baseUrl + originProductId,headermap);
            logger.debug("获取当前商品请求评论统计为：{}",httpGetResult);
            JSONObject parseObject = JSONObject.parseObject(httpGetResult);
            String goodRateShow = parseObject.getJSONArray("CommentsCount").getJSONObject(0).getString("GoodRateShow");
            sysGoods.setFeedbackRate(new BigDecimal(goodRateShow));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("抓取当前商品评论统计失败！");
        }

//        Document document = getDocumentBySelenium(chromeOptions, detailUrl, "comment", chromedrivePath, 3);
//
//        Elements percentEls = document.getElementsByClass("percent-con");
//
//        if (percentEls!=null&&!percentEls.isEmpty()) {
//            String positiveRate = percentEls.get(0).text();
//            logger.debug("当前详情抓取的好评率为：{}，{}",detailUrl,positiveRate);
////            sysGoods.setField0(positiveRate);
//            sysGoods.setFeedbackRate(new BigDecimal(positiveRate.replaceAll("%","")));
//        }else{
//            //尝试实现鼠标左键点击
//
//            document=Jsoup.parse(driver.getPageSource());
//
//        }

        //ChromeDriver 实现了JavascriptExecutor
//        ChromeDriver driver = (ChromeDriver) getWebDriver(chromedrivePath,chromeOptions);
        // 将会强制每个请求等待10秒
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        driver.get(detailUrl);
        // 模拟滑块人工操作下拉
//        mockScrollGradualBottom(driver,3);
//        WebDriverWait wait = new WebDriverWait(driver,20);
//        // 显示判断等待
//        wait.until(new ExpectedCondition<WebElement>(){
//            @Override
//            public WebElement apply(WebDriver d) {
//                WebElement J_UlThumb = d.findElement(By.id("comment")); // 列表页是J_ItemList  详情页是J_UlThumb
//                return J_UlThumb;
//            }});
//        Actions actions = new Actions(driver);
//        By.cssSelector()
//        WebElement webElement = driver.findElement("shangpin|keycount|product|shangpinpingjia_2").get(0);
//        actions.click(webElement).perform();
//        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);//等三秒
//        Document document=Jsoup.parse(driver.getPageSource());
//        Elements percentEls = document.getElementsByClass("percent-con");
//        if (percentEls!=null&&!percentEls.isEmpty()) {
//            String positiveRate = percentEls.get(0).text();
//            logger.debug("当前详情抓取的好评率为：{}，{}",detailUrl,positiveRate);
////            sysGoods.setField0(positiveRate);
//            sysGoods.setFeedbackRate(new BigDecimal(positiveRate.replaceAll("%","")));
//        }
    }

    /**
     * 抓取商品详情
     * @param sysGoods
     * @param detailUrl
     */
    public void getGoodsDetail(SysGoods sysGoods,String detailUrl,ChromeOptions chromeOptions) throws IOException, InterruptedException {
        logger.info("开始抓取详情数据：{}",detailUrl);
        Document document = getDocumentBySelenium(chromeOptions, detailUrl, "spec-list",chromedrivePath,10);

        String tags = document.getElementById("p-ad").text();
        // 获取图片
        Element speclist = document.getElementById("spec-list");
        Elements lichildre = speclist.getElementsByTag("li");
        for(int i=1;i<=lichildre.size();i++){
            Element element = lichildre.get(i-1);
            String imgsrc ="https:"+ element.getElementsByTag("img").eq(0).attr("src").replaceAll("n5","n1");
            String saveFileByUrl = FileUtils.saveFileByUrl(imgsrc, baseFilePath);
            switch (i){
                case 1:sysGoods.setGoodsPic1(saveFileByUrl);break;
                case 2:sysGoods.setGoodsPic2(saveFileByUrl);break;
                case 3:sysGoods.setGoodsPic3(saveFileByUrl);break;
                case 4:sysGoods.setGoodsPic4(saveFileByUrl);break;
                case 5:sysGoods.setGoodsPic5(saveFileByUrl);break;
            }
        }
        //产品参数
        String attributes = document.getElementsByClass("p-parameter").html();
        // 详情描述
        String descriptionNew = document.getElementById("J-detail-pop-tpl-top-new").html();
        String descriptionContent = document.getElementsByClass("detail-content clearfix").html();
        String description="<div>"+attributes+"</div><div>"+descriptionNew+"</div><div>"+descriptionContent+"</div";
        sysGoods.setContent(description);
        sysGoods.setTags(tags);
    }


    //从字典配置里面获取代理启用信息
    private void checkDictProxy(){
        List<SysDictValue> ifProxyPollList = dictValueService.getValueByCode("if_proxy_poll");
        if(ifProxyPollList!=null&&!ifProxyPollList.isEmpty()){
            ifProxyPoll=Boolean.parseBoolean(ifProxyPollList.get(0).getDictValue());
        }

        List<SysDictValue> ifIPProxyList = dictValueService.getValueByCode("if_ip_proxy");
        if(ifIPProxyList!=null&&!ifIPProxyList.isEmpty()){
            if_ip_proxy=Boolean.parseBoolean(ifIPProxyList.get(0).getDictValue());
        }
        logger.info("当前设置ifProxyPoll：{}，if_ip_proxy：{}",ifProxyPoll,if_ip_proxy);
    }
}
