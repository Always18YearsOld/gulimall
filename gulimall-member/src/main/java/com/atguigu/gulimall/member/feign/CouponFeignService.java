package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ：ltb
 * @date ：2020/7/1
 */


@FeignClient("gulimall-coupon")    //告诉springCloud这个接口是个远程客户端
public interface CouponFeignService {


    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
