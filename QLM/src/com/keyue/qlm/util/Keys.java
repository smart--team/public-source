/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.keyue.qlm.util;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	//合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088311464880052";

	//收款支付宝账号
	public static final String DEFAULT_SELLER = "kyeqms@163.com";

	//商户私钥，自助生成
	public static final String PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKuQ2AXgif8dRktWO/CnJLCAT10E5qDNwbKoJRi+Lunao8z7y4cGylFhhqo2dH+/Jt8YIimtG2J91W9t8Al7pSm79qXl1FePPzhE6cb/cZW8dhvQ/iJwIz5IrpSX2KOsuA/cJiCHDQNeCkvX4zZtTQV/qcYMN+4AwbKYBZexPjQxAgMBAAECgYAl5AVxtteDWmnN9aujSKbXvF7KwmxVE1w2IuCeiFJAH6ORgALBPYStWIavTPuJwyPIncHdxneH1xauV29uCLq/Ef8Z66Vfuvv7vcHXpcEIBjndQ4pDeCGRfcYpV5UsONEfYEAztQhavZ48BYhD0k6h1dowAuZKE4grX+bgAKwt6QJBAOMo+evkX8VpZice5vT0QK/dxYWOOXeh5Vnr44UIk6zKYEoUML7FpCqr3wn1B6P/TPC4OzX2wj4u213xNJerpw8CQQDBWPmk1y6TeIb8sKNq3wDNUDgPhbfCQkhanwzxlnEr8e1hMOOysx/YwmXN9fDoUYDfGZ+L9t5mmPHBhXqPinC/AkBzgkmbbtRYQOEl8WvFkVI3W1DuOcT1FUjXscBOzG6zRXEzGhMzXrK81AqlOIi7Fr3cBgJtzV02W4NGwLlainl3AkBKRjvgrykdTbfNaq3caD6OoLpofB3TalMRPPhj9j8TeqSOKZHPHDCnvEYPkOMy29x92AIKapOVJjv486XZbn1RAkByVxeh0aFijy/Dt9ftgWI5h2iJhIC6guurwjBa9N4BxXNMM8fA/dpNVAMKpQyXYAaw1IKV6EW+3tBKZqRFZtSy";

	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
}
