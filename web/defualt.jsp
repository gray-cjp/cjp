<%--
  Created by IntelliJ IDEA.
  User: lenovo
  Date: 2019/4/27
  Time: 23:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
         <%
         response.sendRedirect(request.getContextPath()+"/product?method=index");
         %>
</body>
</html>
