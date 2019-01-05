package test;

import java.text.MessageFormat;
import java.text.NumberFormat;

import org.junit.Test;

public class Demo1 {
	@Test
	public void fun1() {
		/*
		 * 包含了点位符的字符串就是模板！
		 * 点位符：{0}、{1}、{2}
		 * 可变参数，需要指定模板中的点位符的值！有几个点位符就要提供几个参数
		 */
		String s = MessageFormat.format("{0}或{1}错误！", "用户名", "密码");
		System.out.println(s);
	}
	
	@Test
	public void fun2(){
		String hmac=PaymentUtil.buildHmac("Buy", "10001126856", "123456", "10", "CNY", "", "", "",
				"hhtp://localhost:8080/bookStore/OrderServlet?method=back",
				"", "", "ICBC-NEF-B2C", "1", "69cl522AV6q613Ii4W6u8K6XuW8vM1N6bFgyv769220IuYe9u37N4y7rI4Pl");
		System.out.println(hmac);
	}
}
