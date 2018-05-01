package com.zz.hystrix.test.bizmock;

/**
 * @author zhangzuizui
 * @date 18-5-1 上午11:58
 */
public class SmsRequestVo {
    private String phone;
    private String context;
    private long time;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
