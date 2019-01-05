package com.item.order.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.item.cart.domain.Cart;
import com.item.cart.domain.CartItems;
import com.item.order.domain.Order;
import com.item.order.domain.OrderItem;
import com.item.order.service.OrderException;
import com.item.order.service.OrderService;
import com.item.user.domain.User;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import test.PaymentUtil;

/**
 * Servlet implementation class OrderServlet
 */
@WebServlet("/OrderServlet")
public class OrderServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private OrderService orderService=new OrderService();
	
	public String zhiFu(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Properties props=new Properties();
		InputStream input=this.getClass().getClassLoader().getResourceAsStream("merchantInfo.properties");
		props.load(input);
		/*
		 * 准备13参数
		 */
		String p0_Cmd="Buy";
		String p1_MerId=props.getProperty("oid");
		String p2_Order=request.getParameter("oid");
		String p3_Amt="0.01";
		String p4_Cur="CNY";
		String p5_Pid="";
		String p6_Pcat="";
		String p7_Pdesc="";
		String p8_Url=props.getProperty("p8_Url");
		String p9_SAF="";
		String pa_MP="";
		String pd_FrpId=request.getParameter("pd_FrpId");
		String pr_NeedResponse="1";
		/*
		 * 计算hmac
		 */
		String keyValue=props.getProperty("keyValue");
		String hmac=PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur,
				p5_Pid, p6_Pcat, p7_Pdesc, 
				p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);
		/*
		 * 连接易宝的网址和13+1个参数
		 */
		StringBuilder url=new StringBuilder(props.getProperty("url"));
		url.append("?p0_Cmd=").append(p0_Cmd);
		url.append("&p1_MerId=").append(p1_MerId);
		url.append("&p2_Order=").append(p2_Order);
		url.append("&p3_Amt=").append(p3_Amt);
		url.append("&p4_Cur=").append(p4_Cur);
		url.append("&p5_Pid=").append(p5_Pid);
		url.append("&p6_Pcat=").append(p6_Pcat);
		url.append("&p7_Pdesc=").append(p7_Pdesc);
		url.append("&p8_Url=").append(p8_Url);
		url.append("&p9_SAF=").append(p9_SAF);
		url.append("&pa_MP=").append(pa_MP);
		url.append("&pd_FrpId=").append(pd_FrpId);
		url.append("&pr_NeedResponse=").append(pr_NeedResponse);
		url.append("&hmac=").append(hmac);
		System.out.println(url);
		
		/*
		 *	重定向到易宝
		 */
		response.sendRedirect(url.toString());
		return null;
	}
	
	public String back(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 修改订单的状态
		 */
		/*
		 * 获取11+1参数
		 */
		String p1_MerId=request.getParameter("p1_MerId");
		String r0_Cmd=request.getParameter("r0_Cmd");
		String r1_Code=request.getParameter("r1_Code");
		String r2_TrxId=request.getParameter("r2_TrxId");
		String r3_Amt=request.getParameter("r3_Amt");
		String r4_Cur=request.getParameter("r4_Cur");
		String r5_Pid=request.getParameter("r5_Pid");
		String r6_Order=request.getParameter("r6_Order");
		String r7_Uid=request.getParameter("r7_Uid");
		String r8_MP=request.getParameter("r8_MP");
		String r9_BType=request.getParameter("r9_BType");
		
		String hmac=request.getParameter("hmac");
		/*
		 * 校验访问者是否为易宝！
		 */
		Properties props=new Properties();
		InputStream input=this.getClass().getClassLoader().getResourceAsStream("merchantInfo.properties");
		props.load(input);
		String keyValue=props.getProperty("keyValue");
		boolean bool=PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd, r1_Code, r2_TrxId,
				r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid, r8_MP, r9_BType, keyValue);
		
		if(!bool){
			request.setAttribute("msg", "你的订单有错！");
			return	"f:/jsps/msg.jsp";
		}
		/*
		 * 获取订单状态
		 */
		orderService.zhiFu(r6_Order);	//有可能对数据库进行操作，也可能不操作
		/*
		 * 判断当前回掉方式
		 */
		if(r9_BType.equals("2")){
			response.getWriter().print("success");
			
		}
		/*
		 * 保存成功信息，转发到msg.jsp
		 */
		request.setAttribute("msg", "支付成功，等着卖家发货，请稍等！");
		return "f:/jsps/msg.jsp";
		
	}
	
	public String confirm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String oid=request.getParameter("oid");
		try{
			orderService.confirm(oid);
			request.setAttribute("msg", "恭喜，交易成功！");
		}catch(OrderException e){
				request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}
	
	public String load(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	
		request.setAttribute("order", orderService.load(request.getParameter("oid")));
		return "f:/jsps/order/desc.jsp";
	}
	
	public String add(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		/*
		 * 1.从session中得到cart
		 * 2.使用cart生成Order对象
		 * 3.调用service方法完成添加订单
		 * 4.保存order到request域中，转发到/jsps/order/desc.jsp
		 */
		//从session中获取cart
		Cart cart =(Cart) request.getSession().getAttribute("cart");
		//把cart转换成Order对象
		//创建Order对象，并设置属性 		Cart-->Order
		Order order=new Order();
		order.setOid(CommonUtils.uuid());	//设置编号
		order.setOrderTime(new Date());	//设置下单时间
		order.setState(1);	 //设置订单状态为1，表示未付款
		User user =(User) request.getSession().getAttribute("session_user");
		order.setOwner(user);	//设置订单所有者
		order.setTotal(cart.getTotal());	//设置订单的合计，从cart中获取合计
		/*
		 * 创建订单条目集合
		 * cartItemList-->orderItemList
		 */
		List<OrderItem>orderItemList=new ArrayList<OrderItem>();
		//遍历循环cart中的 所有cart Item，使用每一个cart Item对象创建OrderItem对象，并添加到集合中
		for(CartItems cartItem : cart.getCartItems()){
			OrderItem oi=new OrderItem();
			oi.setIid(CommonUtils.uuid());
			oi.setCount(cartItem.getCount());
			oi.setBook(cartItem.getBook());	//设置条目的图书
			oi.setSubtotal(cartItem.getSubtotal());
			oi.setOrder(order);
			orderItemList.add(oi);
		}
		order.setOrderItemList(orderItemList);
		
		//清空购物车
		cart.clear();
		//添加订单
		orderService.add(order);
		//保存并转发到/jsps/order/desc.jsp
		request.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}
	
	public String myOrders(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		User user= (User) request.getSession().getAttribute("session_user");
		List<Order>orderList=orderService.myOrders(user.getUid());	
		request.setAttribute("orderList", orderList);
		return "f:/jsps/order/list.jsp";
	}
}
