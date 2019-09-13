package com.leyou.cart.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import com.leyou.cart.threadlocal.UserHolder;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.client.ItemClient;
import com.leyou.pojo.SkuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserHolder userHolder;
    private static final String CART_PREFIX = "ly:cart:";

    @Override
    public void addCart(Cart cart) {
        //1、需要用户id-》需要解析token,解析过程抽取到过滤器中 封装进localThread
        UserInfo userInfo = userHolder.getTl().get();
        String key = CART_PREFIX + userInfo.getId();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        String hk = cart.getSkuId().toString();
        //2、判断当前用户购物车是否存在或者购物车存在当前商品不存在
        if (!redisTemplate.hasKey(key) || !hashOps.hasKey(hk)) {
            hashOps.put(hk, JsonUtils.toString(cart));
            //2.1、不存在则执行添加
            return;
        }
        //3、购物城存在且有当前商品修改数量
        Cart oldCart = JsonUtils.nativeRead(hashOps.get(hk), new TypeReference<Cart>() {
        });
        oldCart.setNum(oldCart.getNum()+cart.getNum());
        hashOps.put(hk, JsonUtils.toString(oldCart));
    }

    @Autowired
    private ItemClient itemClient;

    @Override
    public List<Cart> queryCartsByUid() {
        //1、从本地线程中获取用户信息
        UserInfo userInfo = userHolder.getTl().get();
        String key = CART_PREFIX + userInfo.getId();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        //2、根据用户名查询购物车
        Map<String, String> cartsMap = hashOps.entries();
        if (cartsMap==null || cartsMap.isEmpty()) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        //获取skuIdList
        List<Long> skuIds = cartsMap.keySet().stream().map(Long::new).collect(Collectors.toList());
        //查询skuList
        List<SkuDTO> skus = itemClient.querySkusByIds(skuIds);
        //遍历skuList集合封装Cart对象 收集成list集合
        List<Cart> carts = skus.stream()
                .map(skuDTO -> {
                    String cartStr = cartsMap.get(skuDTO.getId().toString());
                    Cart cart = JsonUtils.nativeRead(cartStr, new TypeReference<Cart>() {
                    });
                    cart.setPrice(skuDTO.getPrice());
                    return cart;
                }).collect(Collectors.toList());
        //将结果返回
        return carts;
    }

    @Override
    public void incrNum(Long id, Integer num) {
        //从redis中查询当前用户购物车
        String key = CART_PREFIX+userHolder.getTl().get().getId();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        if (!hashOps.hasKey(id.toString())) {
            throw new LyException(ExceptionEnum.PARAM_ERROR);
        }
        Cart cart = JsonUtils.nativeRead(hashOps.get(id.toString()), new TypeReference<Cart>() {
        });
        if (cart == null) {
            throw new LyException(ExceptionEnum.PARAM_ERROR);
        }
        //将购物车中sku num 数量修改
        cart.setNum(num);
        //将修改后的数据写会redis
        hashOps.put(id.toString(),JsonUtils.toString(cart));
    }

    @Override
    public void deleteCartByskuId(String skuId) {
        String key = CART_PREFIX + userHolder.getTl().get().getId();
        BoundHashOperations<String, String, Cart> hashOps = redisTemplate.boundHashOps(key);
        Long count = hashOps.delete(skuId);
        if (count!=1) {
            throw new LyException(ExceptionEnum.DELETE_FAIL);
        }
    }

    @Override
    public void mergeCarts(List<Cart> carts) {
        if (CollectionUtils.isEmpty(carts)) {
            throw new LyException(ExceptionEnum.PARAM_ERROR);
        }
        carts.forEach(this::addCart);
    }

}
