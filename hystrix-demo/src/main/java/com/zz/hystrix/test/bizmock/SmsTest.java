package com.zz.hystrix.test.bizmock;

import org.junit.Before;
import org.junit.Test;

/**
 * @author zhangzuizui
 * @date 18-5-1 下午12:01
 */
public class SmsTest {

    private SmsRequestVo smsRequestVo;
    @Before
    public void before(){
        SmsRequestVo smsRequestVo = new SmsRequestVo();
        smsRequestVo.setPhone("18881881818");
        smsRequestVo.setContext("send messages !!!");
        this.smsRequestVo = smsRequestVo;
    }
    @Test
    public void testSendMessage001(){
        try {
            for (int i = 10; i >0; i--) {
                long time = i*100;
                smsRequestVo.setTime(time);
                SmsResponseVo smsResponseVo = (SmsResponseVo) new SmsService(smsRequestVo).execute();
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
