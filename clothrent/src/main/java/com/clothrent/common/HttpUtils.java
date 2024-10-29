package com.clothrent.common;


import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.*;


public class HttpUtils {
    private static final Log logger = LogFactory.getLog(HttpUtils.class.getName());

    public HttpUtils() {
    }

    private static void setPostHead(HttpPost httpPost, Map<String, String> headMap) {
        if (headMap != null && headMap.size() > 0) {
            Set<String> keySet = headMap.keySet();
            Iterator var4 = keySet.iterator();

            while(var4.hasNext()) {
                String key = (String)var4.next();
                httpPost.addHeader(key, (String)headMap.get(key));
            }
        }

    }

    public static String sendGet(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        return sendGet(httpclient, url, (Map)null);
    }

    public static String sendGet(String url, Map<String, String> headMap) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        return sendGet(httpclient, url, headMap);
    }

    public static String sendGet(String url, String proxyName, int proxyPort) {
        CloseableHttpClient httpclient = getHttpClientByProxy(proxyName, proxyPort);
        return sendGet(httpclient, url, (Map)null);
    }

    public static String sendGet(String url, Map<String, String> headMap, String proxyName, int proxyPort) {
        CloseableHttpClient httpclient = getHttpClientByProxy(proxyName, proxyPort);
        return sendGet(httpclient, url, headMap);
    }

    public static String sendPost(String url, Map<String, String> paramMap) {
        CloseableHttpClient httpClient = getHttpClient();
        return sendPost(httpClient, url, paramMap, (Map)null);
    }

    public static String sendPost(String url, Map<String, String> paramMap, Map<String, String> headMap) {
        CloseableHttpClient httpClient = getHttpClient();
        return sendPost(httpClient, url, paramMap, headMap);
    }

    public static String sendPost(String url, Map<String, String> paramMap, String proxyName, int proxyPort) {
        CloseableHttpClient httpClient = getHttpClientByProxy(proxyName, proxyPort);
        return sendPost(httpClient, url, paramMap, (Map)null);
    }

    public static String sendPost(String url, Map<String, String> paramMap, Map<String, String> headMap, String proxyName, int proxyPort) {
        CloseableHttpClient httpClient = getHttpClientByProxy(proxyName, proxyPort);
        return sendPost(httpClient, url, paramMap, headMap);
    }
    private static String sendPost(CloseableHttpClient httpClient, String url, Map<String, String> paramMap, Map<String, String> headMap) {
        String ret = "";

        try {
            HttpPost post = new HttpPost(url);
            if (paramMap != null) {
                setPostHead(post, headMap);
                List<NameValuePair> list = new ArrayList();
                Iterator var8 = paramMap.entrySet().iterator();

                while(var8.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry)var8.next();
                    logger.info((String)entry.getKey() + ":" + (String)entry.getValue());
                    list.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
                }

                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
                post.setEntity(uefEntity);
            }

            logger.info("POST url...." + post.getURI());
            CloseableHttpResponse httpResponse = httpClient.execute(post);

            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    logger.info("response code:" + httpResponse.getStatusLine());
                    ret = getRespString(entity);
                    logger.info("response content:" + ret);
                }
            } catch (Exception var27) {
                logger.error("error:" + var27.getMessage(), var27);
            } finally {
                httpResponse.close();
                post.releaseConnection();
            }
        } catch (UnsupportedEncodingException var29) {
            logger.error("error:" + var29.getMessage());
        } catch (IOException var30) {
            logger.error("error:" + var30.getMessage());
        } finally {
            try {
                closeHttpClient(httpClient);
            } catch (Exception var26) {
                logger.error("error:" + var26.getMessage());
            }

        }

        return ret;
    }



    public static String sendGet(CloseableHttpClient httpClient, String url, Map<String, String> headMap) {
        String ret = "";

        try {
            HttpGet get = new HttpGet(url);
            logger.info("get url:...." + get.getURI());
            setGetHead(get, headMap);
            CloseableHttpResponse httpResponse = httpClient.execute(get);

            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    ret = getRespString(entity);
                    EntityUtils.consume(entity);
                }
            } finally {
                httpResponse.close();
                get.releaseConnection();
            }
        } catch (Exception var22) {
            logger.error("error:" + var22.getMessage());
        } finally {
            try {
                closeHttpClient(httpClient);
            } catch (IOException var20) {
                logger.error("error:" + var20.getMessage());
            }

        }

        return ret;
    }


    private static CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }

    private static CloseableHttpClient getHttpClientByProxy(String proxyName, int proxyPort) {
        HttpHost proxy = new HttpHost(proxyName, proxyPort);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        return HttpClients.custom().setRoutePlanner(routePlanner).build();
    }

    private static void closeHttpClient(CloseableHttpClient client) throws IOException {
        if (client != null) {
            client.close();
        }

    }

    public static String httpGet(String url) {
        return httpGet(url, (Map)null);
    }

    public static String httpGet(String url, Map<String, String> headMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpGet httpGet = new HttpGet(url);
            setGetHead(httpGet, headMap);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);

            try {
                HttpEntity entity = response1.getEntity();
                responseContent = getRespString(entity);
                EntityUtils.consume(entity);
            } finally {
                response1.close();
            }
        } catch (Exception var22) {
            var22.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException var20) {
                var20.printStackTrace();
            }

        }

        return responseContent;
    }




    private static void setGetHead(HttpGet httpGet, Map<String, String> headMap) {
        if (headMap != null && headMap.size() > 0) {
            Set<String> keySet = headMap.keySet();
            Iterator var4 = keySet.iterator();

            while(var4.hasNext()) {
                String key = (String)var4.next();
                httpGet.addHeader(key, (String)headMap.get(key));
            }
        }

    }

    private static String getRespString(HttpEntity entity) throws Exception {
        if (entity == null) {
            return null;
        } else {
            InputStream is = entity.getContent();
            StringBuffer strBuf = new StringBuffer();
            byte[] buffer = new byte[4096];
            boolean var4 = false;

            int r;
            while((r = is.read(buffer)) > 0) {
                strBuf.append(new String(buffer, 0, r, "UTF-8"));
            }

            return strBuf.toString();
        }
    }

    public static Map<String, List<String>> splitQuery(URI url) throws UnsupportedEncodingException {
        Map<String, List<String>> query_pairs = new LinkedHashMap();
        if (url.getRawQuery() == null) {
            return query_pairs;
        } else {
            String[] pairs = url.getRawQuery().split("&");
            String[] var6 = pairs;
            int var5 = pairs.length;

            for(int var4 = 0; var4 < var5; ++var4) {
                String pair = var6[var4];
                int idx = pair.indexOf("=");
                String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, new LinkedList());
                }

                String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                ((List)query_pairs.get(key)).add(value);
            }

            return query_pairs;
        }
    }



    public static long getContentLength(HttpExchange he) {
        List<String> ret_list = he.getRequestHeaders().get("Content-Length");
        if (ret_list != null && !ret_list.isEmpty()) {
            for(int i = 0; i < ret_list.size(); ++i) {
                String ret = ((String)ret_list.get(i)).trim();
                if (!ret.isEmpty()) {
                    try {
                        long tid = Long.parseLong(ret);
                        return tid;
                    } catch (Exception var6) {
                        ;
                    }
                }
            }

            return 0L;
        } else {
            return 0L;
        }
    }

    public static boolean isMultipart(HttpExchange he) {
        List<String> ret_list = he.getRequestHeaders().get("Content-Type");
        if (ret_list != null && !ret_list.isEmpty()) {
            for(int i = 0; i < ret_list.size(); ++i) {
                String ret = ((String)ret_list.get(i)).trim();
                if (!ret.isEmpty() && ret.indexOf("multipart/form-data") >= 0) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static byte[] getBoundary(BufferedInputStream bis) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for(byte[] b = new byte[1]; bis.read(b) != -1; buffer.put(b)) {
            if (b[0] == 13) {
                if (bis.read(b) == -1) {
                    break;
                }

                if (b[0] == 10) {
                    int limit = buffer.position();
                    byte[] r = new byte[limit];
                    buffer.flip();
                    buffer.get(r);
                    return r;
                }
            }
        }

        return null;
    }

    public static long getFormInfo(BufferedInputStream bis, Map<String, String> ret_map) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] b = new byte[1];
        List<String> list_info = new ArrayList();
        long os = 0L;

        int length;
        while(bis.read(b) != -1) {
            ++os;
            if (b[0] == 13) {
                if (bis.read(b) == -1) {
                    break;
                }

                ++os;
                if (b[0] == 10) {
                    length = buffer.position();
                    if (length == 0) {
                        break;
                    }

                    byte[] r = new byte[length];
                    buffer.flip();
                    buffer.get(r);
                    list_info.add(new String(r));
                    buffer.clear();
                    continue;
                }
            }

            buffer.put(b);
        }

        for(length = 0; length < list_info.size(); ++length) {
            String info = (String)list_info.get(length);
            int idx = info.indexOf(":");
            if (idx >= 0) {
                String name = info.substring(0, idx).trim();
                String value = info.substring(idx + 1, info.length()).trim();
                ret_map.put(name, value);
            }
        }

        return os;
    }

    public static long getFormContent(BufferedInputStream bis, byte[] boundary, StringBuffer ret_buffer) throws IOException {
        int min_size = boundary.length * 3;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] b = new byte[min_size];
        long os = 0L;
        int r_size = boundary.length;
        int r_offset = 0;

        int i;
        int r_ret_size;
        for(int bound_size = 0; (r_ret_size = bis.read(b, r_offset, r_size)) != -1; r_size = boundary.length - bound_size) {
            os += (long)r_ret_size;

            for(i = 0; i < r_ret_size; ++i) {
                if (b[i + r_offset] == boundary[bound_size]) {
                    ++bound_size;
                } else {
                    for(int x = 0; x < bound_size; ++x) {
                        buffer.put(boundary[x]);
                    }

                    bound_size = 0;
                    buffer.put(b[i + r_offset]);
                }
            }

            if (bound_size == boundary.length) {
                bis.read(b, 0, 2);
                os += 2L;
                break;
            }

            for(i = 0; i < bound_size; ++i) {
                b[i] = boundary[i];
            }

            r_offset = bound_size;
        }

        i = buffer.position();
        if (i >= 2 && buffer.get(i - 1) == 10 && buffer.get(i - 2) == 13) {
            buffer.position(i - 2);
        }

        i = buffer.position();
        if (i > 0) {
            b = new byte[i];
            buffer.flip();
            buffer.get(b);
            ret_buffer.append(new String(b));
        }

        return os;
    }

    public static long getFormContent(BufferedInputStream bis, byte[] boundary, OutputStream buffer) throws IOException {
        int min_size = boundary.length * 3;
        byte[] b = new byte[min_size];
        long os = 0L;
        int r_size = boundary.length;
        int r_offset = 0;
        int bound_size = 0;

        int r_ret_size;
        for(byte end_tag = 0; (r_ret_size = bis.read(b, r_offset, r_size)) != -1; r_size = boundary.length - bound_size) {
            os += (long)r_ret_size;

            int i;
            for(i = 0; i < r_ret_size; ++i) {
                if (b[i + r_offset] == boundary[bound_size]) {
                    ++bound_size;
                } else {
                    if (bound_size > 0) {
                        if (end_tag > 0) {
                            buffer.write(13);
                        }

                        if (end_tag > 1) {
                            buffer.write(10);
                        }

                        end_tag = 0;
                        buffer.write(boundary, 0, bound_size);
                    }

                    bound_size = 0;
                    if (end_tag == 0 && b[i + r_offset] == 13) {
                        end_tag = 1;
                    } else if (end_tag == 1 && b[i + r_offset] == 10) {
                        end_tag = 2;
                    } else {
                        if (end_tag > 0) {
                            buffer.write(13);
                        }

                        if (end_tag > 1) {
                            buffer.write(10);
                        }

                        end_tag = 0;
                        if (b[i + r_offset] == 13) {
                            end_tag = 1;
                        } else {
                            buffer.write(b[i + r_offset]);
                        }
                    }
                }
            }

            if (bound_size == boundary.length) {
                bis.read(b, 0, 2);
                os += 2L;
                break;
            }

            for(i = 0; i < bound_size; ++i) {
                b[i] = boundary[i];
            }

            r_offset = bound_size;
        }

        return os;
    }

    public static String uploadFileImpl(String serverUrl, Map<String, String> file_map, Map<String, String> params, Map<String, String> headMap) throws Exception {
        String respStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpPost httppost = new HttpPost(serverUrl);
            setPostHead(httppost, headMap);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            setUploadParams(multipartEntityBuilder, params);
            Iterator var9 = file_map.keySet().iterator();

            while(var9.hasNext()) {
                Object o = var9.next();
                String field_name = (String)o;
                String file_name = (String)file_map.get(o);
                File file = new File(file_name);
                if (file.exists()) {
                    FileBody filebody = new FileBody(file);
                    multipartEntityBuilder.addPart(field_name, filebody);
                }
            }

            HttpEntity reqEntity = multipartEntityBuilder.build();
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);

            try {
                HttpEntity resEntity = response.getEntity();
                respStr = getRespString(resEntity);
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
                httppost.releaseConnection();
            }
        } finally {
            httpclient.close();
        }

        return respStr;
    }

    public static String sendPost(String urlParam, String content) throws Exception {
        logger.debug("sendPost:" + urlParam + " content:" + content);
        StringBuilder result = new StringBuilder();
        URL httpUrl = null;
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;

        try {
            httpUrl = new URL(urlParam);
            connection = (HttpURLConnection)httpUrl.openConnection();
            connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setRequestProperty("connection", "keep-alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data;name=media");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            byte[] b = content.getBytes("UTF-8");
            connection.getOutputStream().write(b, 0, b.length);
            connection.getOutputStream().flush();
            connection.getOutputStream().close();
            connection.connect();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";

            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            bufferedReader.close();
            bufferedReader = null;
        } catch (Exception var11) {
            logger.error("Failed to send message to wechat server" + urlParam, var11);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
                bufferedReader = null;
            }

            if (connection != null) {
                connection.disconnect();
                connection = null;
            }

        }

        if (logger.isInfoEnabled()) {
            logger.info("result=" + result);
        }

        return result.toString();
    }

    public static String sendPost(String url, JSONObject content, Map<String, String> headMap) throws Exception {
        String ret = "";
        CloseableHttpClient httpClient = getHttpClient();

        try {
            HttpPost post = new HttpPost(url);
            if (headMap != null && !headMap.isEmpty()) {
                setPostHead(post, headMap);
            }

            StringEntity se = new StringEntity(content.toString(), "UTF-8");
            post.setEntity(se);
            logger.info("POST url...." + post.getURI());
            CloseableHttpResponse httpResponse = httpClient.execute(post);

            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    logger.info("response code:" + httpResponse.getStatusLine());
                    ret = getRespString(entity);
                    logger.info("response content:" + ret);
                }
            } catch (Exception var28) {
                logger.error("error:" + var28.getMessage(), var28);
            } finally {
                httpResponse.close();
                post.releaseConnection();
            }
        } catch (UnsupportedEncodingException var30) {
            logger.error("error:" + var30.getMessage());
        } catch (IOException var31) {
            logger.error("error:" + var31.getMessage());
        } finally {
            try {
                closeHttpClient(httpClient);
            } catch (Exception var27) {
                logger.error("error:" + var27.getMessage());
            }

        }

        return ret;
    }

    private static void setUploadParams(MultipartEntityBuilder multipartEntityBuilder, Map<String, String> params) {
        if (params != null && params.size() > 0) {
            Set<String> keys = params.keySet();
            Iterator var4 = keys.iterator();

            while(var4.hasNext()) {
                String key = (String)var4.next();
                multipartEntityBuilder.addPart(key, new StringBody((String)params.get(key), ContentType.TEXT_PLAIN));
            }
        }

    }
}
