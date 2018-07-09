package com.zz.hystrix.test.bizmock;

import org.junit.Before;
import org.junit.Test;

/**
 * @author zhangzuizui
 * @date 18-5-1 下午12:01
 */
public class SmsTest {


    @Test
    public void testSendMessage001(){
        SmsRequestVo smsRequestVo = new SmsRequestVo();
        smsRequestVo.setPhone("18881881818");
        smsRequestVo.setContext("send messages !!!");
        for (int i = 6; i >0; i--) {
            long time = i*200;
            smsRequestVo.setTime(time);
            SmsResponseVo smsResponseVo = (SmsResponseVo) new SmsService(smsRequestVo).execute();
        }
    }

}
