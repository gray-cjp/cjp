package com.cjp.web.servlet;

import com.cjp.dao.CategoryListService;
import com.cjp.domain.*;
import com.cjp.service.AdminService;
import com.cjp.service.CategoryService;
import com.cjp.service.ProductService;
import com.cjp.utils.BeanFactory;
import com.cjp.utils.CommonsUtils;
import com.cjp.utils.JedisPoolUtils;
import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@WebServlet(name = "ProductServlet", urlPatterns = {"/product"})
public class ProductServlet extends BaseServlet {
    //提交订单
    public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        HttpSession session = request.getSession();//获得seesion域里面存着购物车对象
        //判断用户是否登录，没有登录不得提交订单
        User user = (User) session.getAttribute("user");
        if (user==null){
            response.sendRedirect(request.getContextPath()+"/login.jsp");
            return;//重定向后后面代码依然会执行，所以需要加return停止后面运行
        }
        //用户登录，封装一个订单对象Order传递到Dao层将数据存储到数据库
        Order order =new Order();
        //封装数据
        Cart cart = (Cart) session.getAttribute("cart");
//        private String oid;
        order.setOid(UUID.randomUUID().toString());
//        private Date ordertiem;
        order.setOrdertiem(new Date());
//        private double total;
        order.setTotal(cart.getTotal());
//        private int state;
        order.setState(0);
//        private String address;
        order.setAddress(null);
//        private String name;
        order.setName(null);
//        private String telepone;
        order.setTelepone(null);
//        private User user;
        order.setUser(user);
//        private List<OrderItem> orderItems =new ArrayList<OrderItem>();
        Map<String, CartItem> cartItem = cart.getCartItem();
        for (String str:cartItem.keySet()){
            OrderItem orderItem =new OrderItem();//创建订单项
            orderItem.setItemid(UUID.randomUUID().toString());//封装订单项id
            orderItem.setCount(cartItem.get(str).getBuyNum());//封装订单项商品数量
            orderItem.setSubtotal(cartItem.get(str).getSubtotal());//封装订单项商品小计
            orderItem.setProduct(cartItem.get(str).getProduct());//封装订单项商品对象
            orderItem.setOrder(order);//封装订单项所属订单
            order.getOrderItems().add(orderItem);//将订单项添加至订单的订单项集合中便于传递数据
        }
        ProductService service =new ProductService();
        service.submitOrder(order);
        session.setAttribute("order",order);
        response.sendRedirect(request.getContextPath()+"/order_info.jsp");
    }
    //显示商品信息
    public void categoryInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("Pid");
        String currentPage = request.getParameter("currentPage");
        String CategoryCid = request.getParameter("CategoryCid");
        ProductService service = new ProductService();
        Product ProductListInfo = service.findProductInfo(pid);
        request.setAttribute("ProductListInfo", ProductListInfo);
        request.setAttribute("CategoryCid", CategoryCid);
        request.setAttribute("currentPage", currentPage);
        String pids = pid;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    pids = cookie.getValue();
                    String[] split = pids.split("-");
                    List<String> asList = Arrays.asList(split);
                    LinkedList<String> list = new LinkedList<String>(asList);
                    if (list.contains(pid)) {
                        //包含当前查看商品的pid
                        list.remove(pid);
                    }
                    list.addFirst(pid);
                    //将[3,1,2]转成3-1-2字符串
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < list.size() && i < 7; i++) {
                        sb.append(list.get(i) + "-");
//                        sb.append("-");//3-1-2-
                    }
                    //去掉3-1-2-后的-
                    pids = sb.substring(0, sb.length() - 1);
                }

            }
        }
        Cookie cookie_pids = new Cookie("pids", pids);
        cookie_pids.setPath(request.getContextPath());
        cookie_pids.setMaxAge(30 * 1000);
        response.addCookie(cookie_pids);
        request.getRequestDispatcher("/product_info.jsp").forward(request, response);
    }
    //显示商品类别的功能
    public void categoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Jedis jedis = JedisPoolUtils.getJedis();
        String categoryListJson =jedis.get("categoryList");
        if (categoryListJson==null){
            System.out.println("数据没缓存，连接数据库取数据");
            CategoryService service =new CategoryService();
            List<Category> categoryList =service.findCategoryList();
            Gson gson =new Gson();
            categoryListJson =gson.toJson(categoryList);
            jedis.set("categoryList",categoryListJson);
        }
        response.setContentType("type/html;charset=UTF-8");
        response.getWriter().write(categoryListJson);
    }
    //商城首页商品列表
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductService service =new ProductService();
        List<Product> HotProductList = service.findHotProductList();
        List<Product> NewProductList = service.findNewProductList();
        request.setAttribute("HotProductList",HotProductList);
        request.setAttribute("NewProductList",NewProductList);
        request.getRequestDispatcher("/index.jsp").forward(request,response);
    }
    //显示商品类别的列表
    public void productListByCid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String currentPageStr = request.getParameter("currentPage");
        if (currentPageStr==null){
            currentPageStr="1";
        }
        int currentPage = Integer.parseInt(currentPageStr);
        int currentCount=12;
        String CategoryCid =request.getParameter("CategoryCid");
        CategoryListService service =new CategoryListService();
        PageBean pageBean = service.findProductListByCid(CategoryCid,currentPage,currentCount);
        request.setAttribute("CategoryCid",CategoryCid);
        request.setAttribute("pageBean",pageBean);
        List<Product> historyProdcutList = new ArrayList<Product>();
        ProductService service1 =new ProductService();
        Cookie[] cookies = request.getCookies();
        if (cookies!=null){
            for (Cookie cookie : cookies){
                if ("pids".equals(cookie.getName())){
                    String pids = cookie.getValue();//3-2-1
                    String[] split = pids.split("-");
                    for(String pid : split){
                        Product pro = service1.findProductInfo(pid);
                        historyProdcutList.add(pro);
                    }
                }
            }
        }
        request.setAttribute("historyProdcutList",historyProdcutList);
        request.getRequestDispatcher("/product_list.jsp").forward(request,response);
    }
    //添加商品到购物车
    public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String pid = request.getParameter("pid");
          String buyNum =request.getParameter("buyNum");
          ProductService service =new ProductService();
          Product product = service.findProductInfo(pid);
          double subtotal = product.getShop_price()*(Double.parseDouble(buyNum));
        CartItem cartItem =new CartItem();
        cartItem.setBuyNum(Integer.parseInt(buyNum));
        cartItem.setSubtotal(subtotal);
        cartItem.setProduct(product);
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart==null){
            cart =new Cart();
        }
        Map<String, CartItem> cartItems = cart.getCartItem();
        if (cartItems.containsKey(pid)){
            CartItem item = cartItems.get(pid);
            int oldbuyNum = item.getBuyNum();
            oldbuyNum += Integer.parseInt(buyNum);
            item.setBuyNum(oldbuyNum);
            double newsubtotal = oldbuyNum*(product.getShop_price());
            item.setSubtotal(newsubtotal);
            cartItems.put(pid,item);
        }else {
            cartItems.put(pid, cartItem);
        }
       double total = cart.getTotal()+subtotal;
        cart.setTotal(total);
        session.setAttribute("cart", cart);
        response.sendRedirect(request.getContextPath() + "/cart.jsp");

    }
    //删除单一商品
    public void delCartItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        cart.setTotal(cart.getTotal()-cart.getCartItem().get(pid).getSubtotal());
        cart.getCartItem().remove(pid);
        response.sendRedirect(request.getContextPath()+"/cart.jsp");
    }
    //清空购物车
    public void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        cart.getCartItem().clear();
        response.sendRedirect(request.getContextPath()+"/cart.jsp");
    }
    public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //更新erder数据库信息
        HttpSession session = request.getSession();
        Order order = (Order) session.getAttribute("order");
        String address = request.getParameter("address");
        String telephone = request.getParameter("telephone");
        String name = request.getParameter("name");
        order.setTelepone(telephone);
        order.setAddress(address);
        order.setName(name);
        ProductService service = new ProductService();
        service.upDateOrders(order);
        //在线支付
// 获得 支付必须基本数据
        String orderid = request.getParameter("oid");
        String money = request.getParameter("1");
        // 银行
        String pd_FrpId = request.getParameter("pd_FrpId");

        // 发给支付公司需要哪些数据
        String p0_Cmd = "Buy";
        String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
        String p2_Order = orderid;
        String p3_Amt = money;
        String p4_Cur = "CNY";
        String p5_Pid = "";
        String p6_Pcat = "";
        String p7_Pdesc = "";
        // 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
        // 第三方支付可以访问网址
        String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
        String p9_SAF = "";
        String pa_MP = "";
        String pr_NeedResponse = "1";
        // 加密hmac 需要密钥
        String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
                "keyValue");
        String hmac = cn.itcast.utils.PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
                p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
                pd_FrpId, pr_NeedResponse, keyValue);


        String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="+pd_FrpId+
                "&p0_Cmd="+p0_Cmd+
                "&p1_MerId="+p1_MerId+
                "&p2_Order="+p2_Order+
                "&p3_Amt="+p3_Amt+
                "&p4_Cur="+p4_Cur+
                "&p5_Pid="+p5_Pid+
                "&p6_Pcat="+p6_Pcat+
                "&p7_Pdesc="+p7_Pdesc+
                "&p8_Url="+p8_Url+
                "&p9_SAF="+p9_SAF+
                "&pa_MP="+pa_MP+
                "&pr_NeedResponse="+pr_NeedResponse+
                "&hmac="+hmac;

        //重定向到第三方支付平台
        response.sendRedirect(url);

    }
    public void callBack(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String p1_MerId = request.getParameter("p1_MerId");
        String r0_Cmd = request.getParameter("r0_Cmd");
        String r1_Code = request.getParameter("r1_Code");
        String r2_TrxId = request.getParameter("r2_TrxId");
        String r3_Amt = request.getParameter("r3_Amt");
        String r4_Cur = request.getParameter("r4_Cur");
        String r5_Pid = request.getParameter("r5_Pid");
        String r6_Order = request.getParameter("r6_Order");
        String r7_Uid = request.getParameter("r7_Uid");
        String r8_MP = request.getParameter("r8_MP");
        String r9_BType = request.getParameter("r9_BType");
        String rb_BankId = request.getParameter("rb_BankId");
        String ro_BankOrderId = request.getParameter("ro_BankOrderId");
        String rp_PayDate = request.getParameter("rp_PayDate");
        String rq_CardNo = request.getParameter("rq_CardNo");
        String ru_Trxtime = request.getParameter("ru_Trxtime");
        // 身份校验 --- 判断是不是支付公司通知你
        String hmac = request.getParameter("hmac");
        String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
                "keyValue");

        // 自己对上面数据进行加密 --- 比较支付公司发过来hamc
        boolean isValid = cn.itcast.utils.PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd,
                r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid,
                r8_MP, r9_BType, keyValue);


        if (isValid) {
            // 响应数据有效
            if (r9_BType.equals("1")) {
                // 浏览器重定向
                ProductService service =new ProductService();
                service.upDateOrdersState(r6_Order);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().println("<h1>付款成功！等待商城进一步操作！等待收货...</h1>");
            } else if (r9_BType.equals("2")) {
                // 服务器点对点 --- 支付公司通知你
                System.out.println("付款成功！");
                // 修改订单状态 为已付款
                // 回复支付公司
                response.getWriter().print("success");
            }
        } else {
            // 数据无效
            System.out.println("数据被篡改！");
        }
    }
    //关键字搜索
    public void searchWord(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String word = request.getParameter("word").trim();
        if (word==null){
            return;
        }
        ProductService service =new ProductService();
        List<Object> productName = service.searchWord(word);
        Gson gson =new Gson();
        String json = gson.toJson(productName);
        response.setContentType("type/html;charset=UTF-8");
        response.getWriter().write(json);
    }
    //搜索跳转商品页面
    public void adminProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        Product product =new Product();
        Map<String,Object> map = new HashMap<String, Object>();
        try {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> parseRequest = upload.parseRequest(request);
            for (FileItem item :parseRequest){
                boolean formField = item.isFormField();
                if (formField){
                    String fieldName = item.getFieldName();
                    String filevalue = item.getString("UTF-8");
                    map.put(fieldName,filevalue);
                }else {
                    String fieldName = item.getFieldName();
                    String path = this.getServletContext().getRealPath("upload");
                    InputStream in = item.getInputStream();
                    OutputStream out = new FileOutputStream(path+"/"+fieldName);
                    IOUtils.copy(in,out);
                    out.close();
                    in.close();
                    item.delete();
                    map.put("pimage","upload/"+fieldName);
                }
            }
            BeanUtils.populate(product,map);
            //是否product对象封装数据完全
            //private String pid;
            product.setPid(CommonsUtils.getUUID());
            //private Date pdate;
            product.setPdate(new Date());
            //private int pflag;
            product.setPflag(0);
            //private Category category;
            Category category = new Category();
            category.setCid(map.get("cid").toString());
            product.setCategory(category);
            AdminService service = (AdminService) BeanFactory.getBean("adminService");
            service.saveProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


     /*  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String methodName = request.getParameter("method");
        if ("categoryInfo".equals(methodName)){
            categoryInfo(request,response);
        }else if ("categoryList".equals(methodName)){
            categoryList(request,response);
        }else if ("productListByCid".equals(methodName)){
            productListByCid(request,response);
        }else if ("index".equals(methodName)){
            index(request,response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }*/
}
