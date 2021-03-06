<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    String base = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <title>豆包客服</title>
    <script>
        window.base = "<%=base%>";
    </script>
    <jsp:include page="/tpl/common/commonjs.jsp"/>
    <jsp:include page="/tpl/visitorMsgList.jsp"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resouces/ajax/common.js"></script>

</head>
<body>
<div align="center" id="tipDiv"></div>
</body>
<script>

    $(function () {
        talk('${user.username}', '${user.to}');
    });

    function talk(username, to) {
        if (!username || !to) {
            alert('客户名或者用户为空');
            return false;
        }
        var url = window.base || '' + '/api/talkFromUEC?username=' + username + '&to=' + to;
        $.ajax({
            url: url,
            type: 'POST',
            success: function (data) {
                console.log(data);
                if (data.success) {
                    $('#tipDiv').html("用户已接入，3秒后关闭");
                    setTimeout(function () {
                        window.close();
                    }, 3000);
                } else {
                    if (data.code == -1) {//客服未登录
                        url = window.base || '' + '/api/doLoginForUecUser?username=' + username + '&to=' + to;
                        window.location.href = url;
                    } else {//异常提示
                        alert(data.msg);
                    }
                }

            }
        });
    }
</script>
</html>
