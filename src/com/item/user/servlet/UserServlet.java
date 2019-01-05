package com.item.user.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.item.cart.domain.Cart;
import com.item.mailProps.Mails;
import com.item.user.domain.User;
import com.item.user.service.UserException;
import com.item.user.service.UserService;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
    private UserService userService=new UserService();
	
    public UserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public String active(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	/*
		 * 1. 获取参数激活码
		 * 2. 调用service方法完成激活
		 *   > 保存异常信息到request域，转发到msg.jsp
		 * 3. 保存成功信息到request域，转发到msg.jsp
		 */
    	String code=request.getParameter("code");
    	try{
    		userService.active(code);
    		request.setAttribute("msg", "恭喜你，激活成功了，请马上登录！");
    	}catch(UserException e){
    		request.setAttribute("msg", e.getMessage());
    	}
    	return "f:/jsps/msg.jsp";
    	
    }
    public String quit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	/*
    	 *退出 
    	 */
    	request.getSession().invalidate();
		return "r:/index.jsp";
    }
    public String login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	/*
    	 * 1. 封装表单数据到form中
    	 * 2. 输入校验（不写了）
    	 * 3. 调用service完成激活
    	 *   > 保存错误信息、form到request，转发到login.jsp
    	 * 4. 保存用户信息到session中，然后重定向到index.jsp
    	 */
    	User form=CommonUtils.toBean(request.getParameterMap(), User.class);
		try {
			User user = userService.login(form);
			request.getSession().setAttribute("session_user", user);
			request.getSession().setAttribute("cart", new Cart());
			return "r:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/login.jsp";
		}
    }
    
    
	public String regist(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	/*
		 * 1. 封装表单数据到form对象中
		 * 2. 补全：uid、code
		 * 3. 输入校验
		 *   > 保存错误信息、form到request域，转发到regist.jsp
		 * 4. 调用service方法完成注册
		 *   > 保存错误信息、form到request域，转发到regist.jsp
		 * 5. 发邮件
		 * 6. 保存成功信息转发到msg.jsp
		 */
		// 封装表单数据
    	User form=CommonUtils.toBean(request.getParameterMap(), User.class);
    	// 补全
    			form.setUid(CommonUtils.uuid());
    			form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
    			/*
    			 * 输入校验
    			 * 1. 创建一个Map，用来封装错误信息，其中key为表单字段名称，值为错误信息
    			 */
    			Map<String,String> errors = new HashMap<String,String>();
    			/*
    			 * 2. 获取form中的username、password、email进行校验
    			 */
    			String username = form.getUsername();
    			if(username == null || username.trim().isEmpty()) {
    				errors.put("username", "用户名不能为空！");
    			} else if(username.length() < 3 || username.length() > 10) {
    				errors.put("username", "用户名长度必须在3~10之间！");
    			}
    			
    			String password = form.getPassword();
    			if(password == null || password.trim().isEmpty()) {
    				errors.put("password", "密码不能为空！");
    			} else if(password.length() < 3 || password.length() > 10) {
    				errors.put("password", "密码长度必须在3~10之间！");
    			}
    			
    			String email = form.getEmail();
    			if(email == null || email.trim().isEmpty()) {
    				errors.put("email", "Email不能为空！");
    			} else if(!email.matches("\\w+@\\w+\\.\\w+")) {
    				errors.put("email", "Email格式错误！");
    			}
    			/*
    			 * 3. 判断是否存在错误信息
    			 */
    			if(errors.size() > 0) {
    				// 1. 保存错误信息
    				// 2. 保存表单数据
    				// 3. 转发到regist.jsp
    				request.setAttribute("errors", errors);
    				request.setAttribute("form", form);
    				return "f:/jsps/user/regist.jsp";
    			}
    			/*
    			 * 调用service的regist()方法
    			 */
    			try {
					userService.regist(form);
				} catch (UserException e) {
					/*
					 * 1. 保存异常信息
					 * 2. 保存form
					 * 3. 转发到regist.jsp
					 */
					request.setAttribute("msg", e.getMessage());
					request.setAttribute("form", form);
					return "f:/jsps/user/regist.jsp";
				}
    			/*
    			 * 发邮件
    			 * 准备配置文件！
    			 */
    			// 获取配置文件内容
    			Properties proper=new Properties();
    			//proper.put("mail.smtp.ssl.enable", "true");//开启ssl验证
    			//proper.setProperty("mail.smtp.host", "smtp.qq.com");
    			//proper.setProperty("mail.smtp.auth", "true");
    			
    			proper.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
    			String host = proper.getProperty("host");//获取服务器主机
    			String uname = proper.getProperty("uname");//获取用户名
    			String pwd = proper.getProperty("pwd");//获取密码
    			String from = proper.getProperty("from");//获取发件人
    			String to = form.getEmail();//获取收件人
    			String subject = proper.getProperty("subject");//获取主题
    			String content = proper.getProperty("content");//获取邮件内容
    			
    			content = MessageFormat.format(content, form.getCode());//替换{0}
    			
    			Session session = Mails.createSession(host, uname, pwd);//得到session
    			Mail mail = new Mail(from, to, subject, content);//创建邮件对象
    			try {
					Mails.send(session, mail);
				} catch (MessagingException e) {
				}//发邮件！
    			/*
    			 * 1. 保存成功信息
    			 * 2. 转发到msg.jsp
    			 */
    			request.setAttribute("msg", "恭喜，注册成功！请马上到邮箱激活");
    			return "f:/jsps/msg.jsp";
    }

}
