package com.clothrent.craw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Random;

/**
 * 爬虫的抽象父类
 */
public abstract class AbstractCrawService {

    private static final Logger logger= LoggerFactory.getLogger(AbstractCrawService.class);

    private volatile WebDriver webDriver=null;


    /**
     * 渐进式拉到页面底部
     * @param driver
     * @throws InterruptedException
     */
    protected void mockScrollGradualBottom(WebDriver driver) throws InterruptedException {
        String totoalHeightJs="return document.body.scrollHeight;";
        Long totalHeight = (Long) ((JavascriptExecutor) driver).executeScript(totoalHeightJs);
        logger.info("当前滑动窗口初始总高度为：{}",totalHeight);
        // 模拟滑动下拉列表页使其完全加载
        int scrollNum =20 +  (int)(Math.random()*(50));
        Integer currHeight=500;
        Long prevHeight=totalHeight;
        do{
            currHeight+=new Random().nextInt(5)*200;
            String js = "var q=document.documentElement.scrollTop="+ currHeight;
            ((JavascriptExecutor) driver).executeScript(js);
            //随机休眠
            Thread.sleep(new Random().nextInt(5)*300);
            // 更新当前窗口总高度
            totalHeight = (Long) ((JavascriptExecutor) driver).executeScript(totoalHeightJs);
            if(totalHeight.longValue()==prevHeight.longValue()&&currHeight+800>=totalHeight.intValue()){
                break;
            }
            //控制滑动次数，不然像蘑菇街就太久了
            if (scrollNum--<1) {
                break;
            }
            prevHeight=totalHeight;
        }while (true);
    }


    /**
     * 渐进式拉到页面底部
     * @param driver
     * @param scrollNum 设置滑动滚动次数
     * @throws InterruptedException
     */
    protected void mockScrollGradualBottom(WebDriver driver,int scrollNum) throws InterruptedException {
        String totoalHeightJs="return document.body.scrollHeight;";
        Long totalHeight = (Long) ((JavascriptExecutor) driver).executeScript(totoalHeightJs);
        logger.info("当前滑动窗口初始总高度为：{}",totalHeight);
        // 模拟滑动下拉列表页使其完全加载
        Integer currHeight=500;
        Long prevHeight=totalHeight;
        do{
            currHeight+=new Random().nextInt(5)*200;
            String js = "var q=document.documentElement.scrollTop="+ currHeight;
            ((JavascriptExecutor) driver).executeScript(js);
            //随机休眠
            Thread.sleep(new Random().nextInt(5)*300);
            // 更新当前窗口总高度
            totalHeight = (Long) ((JavascriptExecutor) driver).executeScript(totoalHeightJs);
            if(totalHeight.longValue()==prevHeight.longValue()&&currHeight+800>=totalHeight.intValue()){
                break;
            }
            //控制滑动次数，不然像蘑菇街就太久了
            if (scrollNum--<1) {
                break;
            }
            prevHeight=totalHeight;
        }while (true);
    }

    /**
     * 使用selenium  chromdriver获取document
     * @param chromeOptions
     * @param url 目标url
     * @param checkId 检测的元素
     * @param scrollNum 滑块下滑滚动次数
     * @return
     * @throws InterruptedException
     */
    protected Document getDocumentBySelenium(ChromeOptions chromeOptions, String url, String checkId,String chromedrivePath,int scrollNum) throws InterruptedException {
        //ChromeDriver 实现了JavascriptExecutor
        ChromeDriver driver = (ChromeDriver) getWebDriver(chromedrivePath,chromeOptions);
        // 将会强制每个请求等待10秒
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(url);

        // 模拟滑块人工操作下拉
        mockScrollGradualBottom(driver,scrollNum);

        WebDriverWait wait = new WebDriverWait(driver,20);
        // 显示判断等待
        wait.until(new ExpectedCondition<WebElement>(){
            @Override
            public WebElement apply(WebDriver d) {
                WebElement J_UlThumb = d.findElement(By.id(checkId)); // 列表页是J_ItemList  详情页是J_UlThumb
                return J_UlThumb;
            }});

        return Jsoup.parse(driver.getPageSource());
    }

    /**
     * 使用selenium  chromdriver获取document
     * @param chromeOptions
     * @param url 目标url
     * @param checkId
     * @return
     * @throws InterruptedException
     */
    protected Document getDocumentBySelenium(ChromeOptions chromeOptions, String url, String checkId,String chromedrivePath) throws InterruptedException {
        //ChromeDriver 实现了JavascriptExecutor
        ChromeDriver driver = (ChromeDriver) getWebDriver(chromedrivePath,chromeOptions);
        // 将会强制每个请求等待10秒
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(url);

        // 模拟滑块人工操作下拉
        mockScrollGradualBottom(driver);

        WebDriverWait wait = new WebDriverWait(driver,20);
        // 显示判断等待
        wait.until(new ExpectedCondition<WebElement>(){
            @Override
            public WebElement apply(WebDriver d) {
                WebElement J_UlThumb = d.findElement(By.id(checkId)); // 列表页是J_ItemList  详情页是J_UlThumb
                return J_UlThumb;
            }});

        return Jsoup.parse(driver.getPageSource());
    }

    /**
     * 设置chromeOptions
     * @param chromeOptions
     */
    protected void checkChromeOptions(ChromeOptions chromeOptions){
        //此步骤很重要，设置为开发者模式，防止被各大网站识别出来使用了Selenium
//        chromeOptions.setExperimentalOption("dom.webdriver.enabled", false);
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled"); // 版本98.0.4758.81（正式版本） （64 位）有效
        // 移除 控制文字
        chromeOptions.setExperimentalOption("useAutomationExtension", false);
        chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

        // 静默执行，也就是不弹出浏览器窗口
//        chromeOptions.setHeadless(true);
//        chromeOptions.addArguments("no-sandbox");//禁用沙盒
    }


    /**
     * 一步到位--拉到页面底部
     * @param driver
     * @throws InterruptedException
     */
    protected void mockScrollBottom(WebDriver driver) throws InterruptedException {
        String totoalHeightJs="return document.body.scrollHeight;";
        Long totalHeight = (Long) ((JavascriptExecutor) driver).executeScript(totoalHeightJs);
        logger.info("当前滑动窗口总高度为：{}",totalHeight);
        Long prevHeight=totalHeight;
        do{
            String js = "var q=document.documentElement.scrollTop="+ (totalHeight-2000);
            ((JavascriptExecutor) driver).executeScript(js);
            Thread.sleep(new Random().nextInt(5)*500);//随机休眠
            totalHeight=(Long) ((JavascriptExecutor) driver).executeScript(totoalHeightJs);
            if(totalHeight.longValue()==prevHeight.longValue()){
                break;
            }
            prevHeight=totalHeight;
        }while (true);

    }

    // 单例获取WebDriver
    protected  WebDriver getWebDriver(String chromedrivePath, ChromeOptions chromeOptions){
        if(webDriver==null){
            synchronized (CrawlerProxy.class){
                if(webDriver==null){
                    logger.debug("当前webdriver.chrome.driver：{}",chromedrivePath);
                    System.setProperty("webdriver.chrome.driver", chromedrivePath);
                    webDriver = new ChromeDriver(chromeOptions);
                }
            }
        }
        return  webDriver;
    }

    /**
     * 关闭WebDriver
     * @param webDriver
     */
    protected static  void closeWebDriver(WebDriver webDriver){
        if(webDriver==null) return;
        webDriver.close();
        webDriver.quit();
        webDriver=null;
    }

    /**
     * 关闭WebDriver
     */
    protected  void closeWebDriver(){
        if(webDriver==null) return;
        webDriver.close();
        webDriver.quit();
        webDriver=null;
    }
}
