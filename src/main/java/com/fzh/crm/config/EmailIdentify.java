package com.fzh.crm.config;

import cn.hutool.core.util.RandomUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.catalina.connector.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EmailIdentify {

    /**
     * Local Cache  5分钟过期
     */
    static Cache<String, String> localCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(5, TimeUnit.MINUTES).build();


    /**
     * 生成验证码
     */
    public String captcha(String uuid)throws IOException {
        String code = RandomUtil.randomNumbers(4); // 随机一个 4位长度的验证码

        //保存到缓存
        localCache.put(uuid, code);
        return code;
    }

    /**
     * 校验验证码
     * @param uuid
     * @param code
     * @return
     */
    public Boolean validateCaptcha(String uuid, String code) {
        //获取缓存中的验证码
        String cacheCaptcha = localCache.getIfPresent(uuid);

        //效验成功
        if(code.equalsIgnoreCase(cacheCaptcha)){
            //删除验证码
            if(cacheCaptcha != null){
                localCache.invalidate(uuid);
            }
            return true;
        }else {
            return false;
        }
    }

    public int[] sortArray(int[] nums) {
        if (nums.length <= 1) {
            return nums;
        }
        for(int i = 1; i < nums.length; i++) {
            int value = nums[i];
            int j = i - 1;
            while (j >= 0) {
                if (nums[j] > value) {
                    nums[j+1] = nums[j];
                } else {
                    break;
                }
                j -= 1;
            }
            nums[j+1] = value;
        }
        return nums;
    }

}
