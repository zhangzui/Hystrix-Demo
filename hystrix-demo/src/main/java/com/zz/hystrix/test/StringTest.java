package com.zz.hystrix.test;

/**
 * @author zhangzuizui
 * @date 2018/12/7 17:05
 */
public class StringTest {
    private static final String b = "hello";//不可变，常量-> 常量池

    public static void main(String[] args) {

        String a = "hellozzz"; //运行时变量->堆中

        String d = "hello"; //变量---引用是指向常量池---即b,所以b==b：true

        String c = b + "z" + "zz";//常量b+2，指向堆中a

        String e = d + "zzz"; //变量+2，新创建一个字符,new String（"hellozzz"），指向堆中的新字符

        //判断引用 是否是同一个
        System.out.println((b == d));

        System.out.println((a == c));

        System.out.println((a == e));
    }

}
