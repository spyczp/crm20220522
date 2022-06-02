<%@page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){
		
		pageList(1, 2);

		//点击查询按钮，刷新交易列表
		//把查询条件保存到隐藏标签中。目的是：当用户改变输入框中的查询条件，但没有点击查询按钮时，依然以之前的查询条件为查询参数。
		$("#searchBtn").click(function(){

			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-customerName").val($.trim($("#search-customerName").val()));
			$("#hidden-stage").val($.trim($("#search-stage").val()));
			$("#hidden-transactionType").val($.trim($("#search-transactionType").val()));
			$("#hidden-source").val($.trim($("#create-clueSource").val()));
			$("#hidden-contactsName").val($.trim($("#search-contactsName").val()));

			pageList(1, 2);

			//当我输入查询条件，点击查询按钮后，正常调用了一次pageList。
			//但是不知道为啥页面又刷了一次pageList，且是没有查询条件的结果。(猜测：页面重新加载了)
			//临时处理，添加return false; 在执行完pageList后中断事件。
			return false;
		})


		/*复选框，当对$("#qx")打勾后，会对当前页的结果全部打勾*/
		$("#qx").click(function () {

			$("input[name=xz]").prop("checked", this.checked);
		})

		$("#tranListBody").on("click", $("input[name=xz]"), function () {

			//当xz复选框2个都选上的时候，总的xz复选框数量和打勾的xz复选框数量相等，这时返回true，则qx复选框会打勾
			$("#qx").prop("checked", $("input[name=xz]").length == $("input[name=xz]:checked").length);
		})
		
	});

	//展示交易列表
	function pageList(pageNo, pageSize){

		/*把全选的复选框的✔去掉*/
		$("#qx").prop("checked", false);

		//每次调用pageList，都会把之前的查询条件作为参数赋值给查询框。
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-customerName").val($.trim($("#hidden-customerName").val()));
		$("#search-stage").val($.trim($("#hidden-stage").val()));
		$("#search-transactionType").val($.trim($("#hidden-transactionType").val()));
		$("#create-clueSource").val($.trim($("#hidden-source").val()));
		$("#search-contactsName").val($.trim($("#hidden-contactsName").val()));

		/*
		* 1.到数据库拿数据：所有交易信息列表
		* 2.拼接字符串，铺html标签
		* 3.插入分页组件
		* */
		$.ajax({
			url: "workbench/transaction/getTransactionList.do",
			data:{
				"pageNo": pageNo,
				"pageSize": pageSize,
				"owner": $.trim($("#search-owner").val()),
				"name": $.trim($("#search-name").val()),
				"customerName": $.trim($("#search-customerName").val()),
				"stage": $.trim($("#search-stage").val()),
				"transactionType": $.trim($("#search-transactionType").val()),
				"source": $.trim($("#create-clueSource").val()),
				"contactsName": $.trim($("#search-contactsName").val()),
			},
			type: "post",
			dataType: "json",
			success: function (response) {
				/*response:{total:总条数, dataList:[{交易1},{交易2},{交易3},...]}
				* 其中，customerId的值需要在sql中替换为客户名称
				* 其中，owner的值需要在sql中替换为名字
				* 其中，contactsId的值需要在sql中替换为联系人名称
				*
				* 20220601写到这里
				* 把stage、type、source换成名称
				* */

				var html = "";

				$.each(response.dataList, function(i, v){

					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" id="'+v.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/transaction/detail.jsp\';">'+v.name+'</a></td>';
					html += '<td>'+v.customerId+'</td>';
					html += '<td>'+v.stage+'</td>';
					html += '<td>'+v.type+'</td>';
					html += '<td>'+v.owner+'</td>';
					html += '<td>'+v.source+'</td>';
					html += '<td>'+v.contactsId+'</td>';
					html += '</tr>';

				})

				$("#tranListBody").html(html);

				//计算总页数
				var totalPages = response.total % pageSize == 0 ? response.total / pageSize : parseInt(response.total / pageSize) + 1;

				//加入分页组件
				//数据处理完毕后，结合分页查询，对前端展现分页信息
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码，我们提供
					rowsPerPage: pageSize, // 每页显示的记录条数，我们提供
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: response.total, // 总记录条数，我们提供

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})

	}
	
</script>
</head>
<body>

	<%--当用户点击查询按钮后，把查询条件保存到隐藏标签中。--%>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-customerName"/>
	<input type="hidden" id="hidden-stage"/>
	<input type="hidden" id="hidden-transactionType"/>
	<input type="hidden" id="hidden-source"/>
	<input type="hidden" id="hidden-contactsName"/>

	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>交易列表</h3>
			</div>
		</div>
	</div>
	
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
	
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">客户名称</div>
				      <input class="form-control" type="text" id="search-customerName">
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">阶段</div>
					  <select class="form-control" id="search-stage">
					  	<option></option>
						  <c:forEach items="${stage}" var="s">

							  <option value="${s.id}">${s.value}</option>

						  </c:forEach>
					  	<%--<option>资质审查</option>
					  	<option>需求分析</option>
					  	<option>价值建议</option>
					  	<option>确定决策者</option>
					  	<option>提案/报价</option>
					  	<option>谈判/复审</option>
					  	<option>成交</option>
					  	<option>丢失的线索</option>
					  	<option>因竞争丢失关闭</option>--%>
					  </select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">类型</div>
					  <select class="form-control" id="search-transactionType">
					  	<option></option>
						  <c:forEach items="${transactionType}" var="tt">

							  <option value="${tt.id}">${tt.value}</option>

						  </c:forEach>
					  	<%--<option>已有业务</option>
					  	<option>新业务</option>--%>
					  </select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">来源</div>
				      <select class="form-control" id="create-clueSource">
						  <option></option>
						  <c:forEach items="${source}" var="src">

							  <option value="${src.id}">${src.value}</option>

						  </c:forEach>
						  <%--<option>广告</option>
						  <option>推销电话</option>
						  <option>员工介绍</option>
						  <option>外部介绍</option>
						  <option>在线商场</option>
						  <option>合作伙伴</option>
						  <option>公开媒介</option>
						  <option>销售邮件</option>
						  <option>合作伙伴研讨会</option>
						  <option>内部研讨会</option>
						  <option>交易会</option>
						  <option>web下载</option>
						  <option>web调研</option>
						  <option>聊天</option>--%>
						</select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">联系人名称</div>
				      <input class="form-control" type="text" id="search-contactsName">
				    </div>
				  </div>
				  
				  <button type="searchBtn" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" onclick="window.location.href='workbench/transaction/getUserList.do';"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" onclick="window.location.href='edit.html';"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
							<td>客户名称</td>
							<td>阶段</td>
							<td>类型</td>
							<td>所有者</td>
							<td>来源</td>
							<td>联系人名称</td>
						</tr>
					</thead>
					<tbody id="tranListBody">
						<%--<tr>
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/transaction/detail.jsp';">动力节点-交易01</a></td>
							<td>动力节点</td>
							<td>谈判/复审</td>
							<td>新业务</td>
							<td>zhangsan</td>
							<td>广告</td>
							<td>李四</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.html';">动力节点-交易01</a></td>
                            <td>动力节点</td>
                            <td>谈判/复审</td>
                            <td>新业务</td>
                            <td>zhangsan</td>
                            <td>广告</td>
                            <td>李四</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 20px;">

				<div id="activityPage"></div>

			</div>
			
		</div>
		
	</div>
</body>
</html>