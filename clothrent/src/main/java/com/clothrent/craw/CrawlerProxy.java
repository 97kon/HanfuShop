package com.clothrent.craw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * 爬虫代理
 */
public class CrawlerProxy {

    private static  final Logger logger= LoggerFactory.getLogger(CrawlerProxy.class);

    // 存放代理ip的队列
    static LinkedBlockingQueue<String> queue = new  LinkedBlockingQueue(1000);

    private volatile static   WebDriver webDriver=null;
    /**
     * 常用 user agent 列表
     */
    static List<String> USER_AGENT = new ArrayList<String>(10) {
        {
            add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.81 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.360");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
            add("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
            add("Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
            add("Mozilla/5.0 (iPod; U; CPU like Mac OS X; en) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/3A101a Safari/419.3");
            add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)");
            add("Chrome/45.0.2454.85 Safari/537.36 115Browser/6.0.3");
            add("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
            add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
            add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)");
            add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
            add("Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
            add("Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11");
            add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
            add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)");
            add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0");
            add("Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
        }
    };



    // 使用jsoup 获取document
    private  Document getDocumentByJsoup(String detailUrl,boolean ifProxyPoll,boolean if_ip_proxy,Set<String> ipproxySet) throws IOException {
        Connection conn = Jsoup.connect(detailUrl);
        if(ifProxyPoll) {
            CrawlerProxy.wrapperConnectionProxyPoll(conn);
        }else if(if_ip_proxy){
            //不使用代理
            CrawlerProxy.wrapperConnectionIpProxy(conn,ipproxySet);
        }else{
            //不使用代理
            CrawlerProxy.wrapperConnection(conn);
        }
        return conn.get();
    }

    /**
     * 随机获取 user agent
     * @return
     */
    public static String randomUserAgent() {
        Random random = new Random();
        int num = random.nextInt(USER_AGENT.size());
        return USER_AGENT.get(num);
    }

    /**
     * 设置代理ip池
     * @throws IOException
     */
    public static void proxyIpPool(){
        try {
            // 获取所有代理
            String proxyUrl = "http://192.144.230.234:5010/all/";

            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(proxyUrl);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String body = EntityUtils.toString(entity, "utf-8");

                JSONArray jsonArray = JSON.parseArray(body);
                for (int i = 0; i < jsonArray.size(); i++) {
                    // 将请求结果格式化成json
                    JSONObject data = jsonArray.getJSONObject(i);
                    String proxy = data.getString("proxy");
                    queue.add(proxy);
                }
            }
            response.close();
            httpclient.close();
            logger.info("初始化代理队列成功，queue:{}",queue);
        } catch (IOException e) {
            logger.error("初始化代理队列报错！");
            logger.error(e.getMessage(),e);
        }
    }


    /**
     * 随机获取一个代理ip
     * @return
     * @throws IOException
     */
    public static String randomProxyIp() throws IOException {

        // 每次能随机获取一个代理ip
        String proxyUrl = "http://192.144.230.234:5010/get/";

        String proxy = "";

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(proxyUrl);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity, "utf-8");
            // 将请求结果格式化成json
            JSONObject data = JSON.parseObject(body);
            proxy = data.getString("proxy");
        }
        return proxy;
    }


    // 不设置代理
    public static void wrapperConnection(Connection connection){
        connection.timeout(120000);
        // 随机user_agent
        connection.userAgent(randomUserAgent());
        connection.ignoreContentType(true) // 忽略类型验证
//                .followRedirects(false) // 禁止重定向
                .postDataCharset("utf-8");
    }
    // 使用代理池包装connection
    public static void wrapperConnectionProxyPoll(Connection connection){
        // 如果代理ip队列为空，则重新获取ip代理
        if (queue.size() == 0) {
            proxyIpPool();
        }
        // 从队列中获取代理ip
        String proxyStr = queue.poll();
        // 解析代理ip
        String[] proxySplit = proxyStr.split(":");
        Proxy proxy=new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxySplit[0], Integer.parseInt(proxySplit[1])));
        wrapperConnection(connection,proxy);
    }

    // 使用单独IP代理包装connection
    public static void wrapperConnectionIpProxy(Connection connection, Set<String> ipproxySet){
        List<String> collect = ipproxySet.stream().collect(Collectors.toList());
        int i = new Random().nextInt(collect.size());
        // 从集合中获取代理ip
        String proxyStr = collect.get(i);
        // 解析代理ip
        String[] proxySplit = proxyStr.split(":");
        Proxy proxy=new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxySplit[0], Integer.parseInt(proxySplit[1])));
        wrapperConnection(connection,proxy);
    }

    public static void wrapperConnection(Connection connection, Proxy proxy){
        connection.timeout(120000);
        // 随机user_agent
        connection.userAgent(randomUserAgent());
        // 设置代理ip
        connection.ignoreContentType(true) // 忽略类型验证
//                .followRedirects(false) // 禁止重定向
                .postDataCharset("utf-8")
                .proxy(proxy);
    }


    // 给chromeOptions 设置代理---使用免费代理池
    public static void wrapperChromeOptionsProxyPoll(ChromeOptions chromeOptions) {
        // 如果代理ip队列为空，则重新获取ip代理
        if (queue.size() == 0) {
            proxyIpPool();
        }
        // 从队列中获取代理ip
        String proxyStr = queue.poll();
        // 解析代理ip
        String[] proxySplit = proxyStr.split(":");
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        proxy.setHttpProxy(proxySplit[0]+":"+proxySplit[1]);
        chromeOptions.setProxy(proxy);

    }

    // 给chromeOptions 设置代理---使用独立代理
    public static void wrapperChromeOptionsIpProxy(ChromeOptions chromeOptions, Set<String> ipproxySet) {
        List<String> collect = ipproxySet.stream().collect(Collectors.toList());
        int i = new Random().nextInt(collect.size());
        // 从集合中获取代理ip
        String proxyStr = collect.get(i);
        // 解析代理ip
        String[] proxySplit = proxyStr.split(":");
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        proxy.setHttpProxy(proxySplit[0]+":"+proxySplit[1]);
        chromeOptions.setProxy(proxy);
    }
}

