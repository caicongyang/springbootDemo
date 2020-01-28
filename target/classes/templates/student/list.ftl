<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>学生列表</title>
</head>
<body>
	 <#if page??>
		<ul>
	   <#list page.list as student>
			<li>${student.id} || ${student.name} || ${student.age}	</li>
 	   </#list>
		<ul>
	</#if>
	<div>
	<#if page.hasPreviousPage>
		<a href="/student/list.html?startPage=1&pageSize=${page.pageSize}">首页</a>|<a href="/student/list.html?startPage=${page.prePage}&pageSize=${page.pageSize}">上一页</a>|
	</#if>
	<#if page.hasNextPage>
		<a href="/student/list.html?startPage=${page.nextPage}&pageSize=${page.pageSize}">下一页</a>|<a href="/student/list.html?startPage=${page.pages}&pageSize=${page.pageSize}">尾页</a>
	</#if>
	</div>
</body>
</html>