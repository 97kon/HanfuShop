package com.clothrent.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.common.ToolsUtils;
import com.clothrent.entity.*;
import com.clothrent.mapper.SysOrderMapper;
import com.clothrent.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 */
@Service
public class SysOrderServiceImpl extends ServiceImpl<SysOrderMapper, SysOrder> implements SysOrderService {

    private static  final Logger LOGGER= LoggerFactory.getLogger(SysOrderServiceImpl.class);

    @Autowired
    SysOrderMapper orderMapper;

    @Autowired
    SysGoodsService goodsService;

    @Autowired
    SysOrderItemService orderItemService;

    @Autowired
    SysCartService cartService;

    @Autowired
    UserInventoryService userInventoryService;

    @Autowired
    SysUserService userService;

    @Autowired
    UserScoreService userScoreService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysOrder generateOrder(List<SysCart> cartList, UserAddress userAddress,String remark) {
        //计算总价格
        double totalMoney=0;
        for(SysCart cart:cartList){
            totalMoney+=cart.getNumber()*cart.getPrice().doubleValue();
        }

        // 当前用户
        SysUser sysuser = userService.getById(userAddress.getUserId());
        // 生成订单号
        String orderNum = ToolsUtils.createOrderNum();

        // 生成订单对象
        SysOrder sysOrder=new SysOrder();
        // 收货人，手机号，地址
        String fullAddress=userAddress.getReceiverName()+","+userAddress.getMobile()+","+userAddress.getAddress();
        sysOrder.setAddress(fullAddress);
        sysOrder.setCode(orderNum); //订单号
        sysOrder.setRemark(remark);
        sysOrder.setUserId(userAddress.getUserId());//订单所属用户ID
        sysOrder.setUserName(userAddress.getUserName()); // 订单所属用户名称
        sysOrder.setPrice(new BigDecimal(totalMoney)); // 订单总金额
        sysOrder.setUserAddressId(userAddress.getId());// 收货地址ID
        sysOrder.setState(1);// 状态：0 待支付；1 已支付；2 已确认；3 已发货 ；4 已收货；5 申请归还；6 已归还；7 归还驳回
        orderMapper.insert(sysOrder);
        LOGGER.debug("生成的订单结果为：{}",sysOrder);

        // 生成订单明细对象
        for(SysCart cart:cartList){
            SysOrderItem orderItem=new SysOrderItem();
            orderItem.setOrderId(sysOrder.getId());
            orderItem.setCode(orderNum);
            Long goodsId = cart.getGoodsId();
            SysGoods goods = goodsService.getById(goodsId);
            orderItem.setGoodsId(goodsId);
            orderItem.setGoodsName(goods.getName());
            orderItem.setCategoryId(goods.getCategoryId());
            orderItem.setCategoryName(goods.getCategoryName());
            orderItem.setNumber(cart.getNumber());
            orderItem.setPrice(cart.getPrice());
            orderItem.setClothSize(cart.getClothSize());
            orderItem.setUserId(cart.getUserId());
            orderItem.setUserName(cart.getUserName());
            orderItem.setField0(JSONObject.toJSONString(goods));
            orderItem.setField1(goods.getGoodsPic1());
            orderItemService.save(orderItem);
            LOGGER.debug("生成订单明细结果为：{}",orderItem);


            //tb_user_inventory 插入库存变动记录--出库记录
            UserInventory userInventory=new UserInventory();
            userInventory.setGoodsId(goodsId);
            userInventory.setGoodsName(goods.getName());
            userInventory.setUserId(userAddress.getUserId());
            userInventory.setUserName(userAddress.getUserName());
            userInventory.setType(2);// 1 入库  2 出库
            userInventory.setPrice(new BigDecimal(cart.getNumber()*cart.getPrice().doubleValue()));
            userInventory.setBeforeStock(goods.getStock().intValue());
            userInventory.setChangeStock(-cart.getNumber());
            // 商品原先库存-number
            Long currentStock = goods.getStock() - cart.getNumber();
            userInventory.setCurrentStock(currentStock.intValue());
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("用户").append(userAddress.getUserName()).append("购买了")
                    .append(cart.getNumber()).append("个/件").append(goods.getName());
            String reason=stringBuilder.toString();
            userInventory.setReason(reason);
            userInventoryService.save(userInventory);

            // 更新商品累积销量 库存
            goods.setSaleNumber(goods.getSaleNumber()+cart.getNumber());
            goods.setStock(currentStock);
            goodsService.updateById(goods);

            // 更新tb_user_score
            UserScore userScore=new UserScore();
            userScore.setUserId(userAddress.getUserId());
            userScore.setUserName(userAddress.getUserName());
            userScore.setReason(reason);
            userScore.setBeforeScore(new BigDecimal(sysuser.getScore()).intValue());
            userScore.setChangeScore(userInventory.getPrice().intValue());// 简单以金额 = 积分计算
            userScore.setCurrentScore(userScore.getBeforeScore()+userScore.getChangeScore());
            userScoreService.save(userScore);
            // 记录下用户当前积分
            sysuser.setScore(userScore.getCurrentScore()*1f);
        }
        // 从购物车移除数据
        cartService.removeByIds(cartList.stream().map(SysCart::getId).collect(Collectors.toList()));

        // 更新用户数据
        userService.updateById(sysuser);

        return sysOrder;
    }


    /**从商品列表或商品详情--确认订单--生成订单
     * @param userAddress 用户收货地址
     * @param goodsId 商品ID
     * @param number 商品数量
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysOrder generateOrder(Long goodsId, Long number, UserAddress userAddress,String remark,String clothSize) {
        SysGoods goods = goodsService.getById(goodsId);
        //计算总价格
        double totalMoney=goods.getPrice().doubleValue()*number;

        // 当前用户
        SysUser sysuser = userService.getById(userAddress.getUserId());
        // 生成订单号
        String orderNum = ToolsUtils.createOrderNum();

        // 生成订单对象
        SysOrder sysOrder=new SysOrder();
        // 收货人，手机号，地址
        String fullAddress=userAddress.getReceiverName()+","+userAddress.getMobile()+","+userAddress.getAddress();
        sysOrder.setAddress(fullAddress);
        sysOrder.setRemark(remark);
        sysOrder.setCode(orderNum); //订单号
        sysOrder.setUserId(userAddress.getUserId());//订单所属用户ID
        sysOrder.setUserName(userAddress.getUserName()); // 订单所属用户名称
        sysOrder.setPrice(new BigDecimal(totalMoney)); // 订单总金额
        sysOrder.setUserAddressId(userAddress.getId());// 收货地址ID
        sysOrder.setState(1);// 状态：0 待支付；1 已支付；2 已确认；3 已发货 ；4 已收货；5 申请归还；6 已归还；7 归还驳回
        orderMapper.insert(sysOrder);
        LOGGER.debug("生成的订单结果为：{}",sysOrder);

        // 生成订单明细对象
        SysOrderItem orderItem=new SysOrderItem();
        orderItem.setClothSize(clothSize);
        orderItem.setOrderId(sysOrder.getId());
        orderItem.setCode(orderNum);
        orderItem.setGoodsId(goodsId);
        orderItem.setGoodsName(goods.getName());
        orderItem.setCategoryId(goods.getCategoryId());
        orderItem.setCategoryName(goods.getCategoryName());
        orderItem.setNumber(number.intValue());
        orderItem.setPrice(new BigDecimal(totalMoney));
        orderItem.setUserId(sysuser.getId());
        orderItem.setUserName(sysuser.getName());
        orderItem.setField0(JSONObject.toJSONString(goods));
        orderItem.setField1(goods.getGoodsPic1());
        orderItemService.save(orderItem);
        LOGGER.debug("生成订单明细结果为：{}",orderItem);


        //tb_user_inventory 插入库存变动记录--出库记录
        UserInventory userInventory=new UserInventory();
        userInventory.setGoodsId(goodsId);
        userInventory.setGoodsName(goods.getName());
        userInventory.setUserId(userAddress.getUserId());
        userInventory.setUserName(userAddress.getUserName());
        userInventory.setType(2);// 1 入库  2 出库
        userInventory.setPrice(new BigDecimal(totalMoney));
        userInventory.setBeforeStock(goods.getStock().intValue());
        userInventory.setChangeStock(-number.intValue());
        // 当前库存 = 商品原先库存-number
        Long currentStock = goods.getStock() - number;
        userInventory.setCurrentStock(currentStock.intValue());
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("用户").append(userAddress.getUserName()).append("购买了")
                .append(number).append("个/件").append(goods.getName());
        String reason=stringBuilder.toString();
        userInventory.setReason(reason);
        userInventoryService.save(userInventory);

        // 更新商品累积销量 库存
        goods.setSaleNumber(goods.getSaleNumber()+number.intValue());
        goods.setStock(currentStock);
        goodsService.updateById(goods);

        // 更新tb_user_score
        UserScore userScore=new UserScore();
        userScore.setUserId(userAddress.getUserId());
        userScore.setUserName(userAddress.getUserName());
        userScore.setReason(reason);
        userScore.setBeforeScore(new BigDecimal(sysuser.getScore()).intValue());
        userScore.setChangeScore(userInventory.getPrice().intValue());// 简单以金额 = 积分计算
        userScore.setCurrentScore(userScore.getBeforeScore()+userScore.getChangeScore());
        userScoreService.save(userScore);
        // 记录下用户当前积分
        sysuser.setScore(userScore.getCurrentScore()*1f);

        // 更新用户数据
        userService.updateById(sysuser);

        return sysOrder;
    }
}
