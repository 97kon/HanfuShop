package com.clothrent.controller;



import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clothrent.config.AliPayConfig;
import com.clothrent.entity.SysOrder;
import com.clothrent.service.SysOrderService;
import org.apache.commons.collections4.Get;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AliPayController {
    // 支付宝沙箱网关地址
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    //签名方式
    private static final String SIGN_TYPE = "RSA2";
    @Resource
    private AliPayConfig aliPayConfig;
    @Resource
    private SysOrderService sysOrderService;

    @GetMapping("/pay")
    public void pay(String orderNo, HttpServletResponse httpResponse) throws Exception{
//        通过序列号获取订单信息
        SysOrder sysOrder = sysOrderService.getByCode(orderNo);
        System.out.println("sysOrder：");
        System.out.println(sysOrder);
        System.out.println(aliPayConfig.getAppId());
        System.out.println(aliPayConfig.getAlipayPublicKey());
        System.out.println(aliPayConfig.getMerchantPrivateKey());
        System.out.println(aliPayConfig.getNotifyUrl());
        if (sysOrder == null){
            return;
        }
//        1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),aliPayConfig.getMerchantPrivateKey(), FORMAT,CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
//        2. 创建Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();//发送请求的request类
//        设置异步回调的url
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", sysOrder.getCode()); //自己生成的订单号
        bizContent.set("total_amount", sysOrder.getPrice());
        bizContent.set("subject", "商品支付");
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        request.setReturnUrl("http://localhost:8081/home/order/homeListPage");
//        执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e){
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @PostMapping("/notity")
    public void payNotify(HttpServletRequest request) throws Exception{
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")){
            System.out.println("=========支付宝异步回调========");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }
            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8"); // 验证签名
//          支付宝验签
            if (checkSignature){
//                验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));
                String tradeNo = params.get("out_trade_no");//获取订单号
                String gmtPayment = params.get("gmt_payment");
                String alipayTradeNo = params.get("trade_no");
//                更新订单状态为已支付，设置支付信息
                SysOrder sysOrder = sysOrderService.getOne(new LambdaQueryWrapper<SysOrder>().eq(SysOrder::getCode, tradeNo));
                sysOrder.setState(1);//设置为已支付
                sysOrderService.updateById(sysOrder);

            }
        }
    }

}
