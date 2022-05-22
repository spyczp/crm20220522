<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<%--http://127.0.0.1:8080/crm/--%>
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

		/*复选框，当对$("#qx")打勾后，会对当前页的结果全部打勾*/
		$("#qx").click(function () {

			$("input[name=xz]").prop("checked", this.checked);
		})

		$("#activityBody").on("click", $("input[name=xz]"), function () {

			//当xz复选框2个都选上的时候，总的xz复选框数量和打勾的xz复选框数量相等，这时返回true，则qx复选框会打勾
			$("#qx").prop("checked", $("input[name=xz]").length == $("input[name=xz]:checked").length);
		})

		/*把修改后的市场活动信息更新到数据库*/
		$("#updateBtn").click(function () {
			/*
			* 1.拿到修改模态窗口中的市场活动信息
			* 2.通过ajax传递给服务器
			* 3.服务器端把市场活动信息更新到数据库
			* */

			$.ajax({
				url: "workbench/activity/update.do",
				data: {
					"id": $.trim($("#edit-id").val()),
					"owner": $.trim($("#edit-owner").val()),
					"name": $.trim($("#edit-name").val()),
					"startDate": $.trim($("#edit-startDate").val()),
					"endDate": $.trim($("#edit-endDate").val()),
					"cost": $.trim($("#edit-cost").val()),
					"description": $.trim($("#edit-description").val()),
				},
				type: "post",
				dataType: "json",
				success: function(response){
					/*
					* 修改成功，则关闭模态窗口，且刷新市场活动表单。否则提示修改失败。
					* 从服务器端返回{"success": true/false}
					* */
					if(response.success){

						//pageList(1, 2);
						//修改成功后，维持在当前页面。保持用户设置的每页显示记录数。
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));


						$("#editActivityModal").modal("hide");

					}else {

						alert("修改失败！");
					}

				}
			})

		})


		/*修改市场活动信息*/
        $("#editBtn").click(function () {

            //模态窗口中的日历
            $(".time").datetimepicker({
                minView: "month",
                language:  'zh-CN',
                format: 'yyyy-mm-dd',
                autoclose: true,
                todayBtn: true,
                pickerPosition: "bottom-left"
            });

            var $xz = $("input[name=xz]:checked");

            if($xz.length == 0){
                alert("请选择需要修改的市场活动，然后在修改。")

            }else if($xz.length > 1){
                alert("只能选中一条市场活动进行修改。")

            }else{

                var id = $xz.val();

                $.ajax({
                    url: "workbench/activity/getUserListAndActivity.do",
                    data: {
                        "id": id
                    },
                    type: "post",
                    dataType: "json",
                    success: function(response){

                        /*需要返回所有者列表，以及当前正在修改的市场活动的信息。
                        response-->{"uList": [{所有者1},{所有者2},{所有者3}], "a": {市场活动}}

                        把返回的值动态创建标签
                        */

                        //处理所有者下拉框
                        var html = "<option></option>";

                        $.each(response.uList, function(i, v){

                            html += "<option value='"+v.id+"'>"+v.name+"</option>";

                        })

                        $("#edit-owner").html(html);

                        //处理单条activity
                        $("#edit-id").val(response.a.id);
						$("#edit-owner").val(response.a.owner);
                        $("#edit-name").val(response.a.name);
                        $("#edit-startDate").val(response.a.startDate);
                        $("#edit-endDate").val(response.a.endDate);
                        $("#edit-cost").val(response.a.cost);
                        $("#edit-description").val(response.a.description);

                        //所有的值填好后，打开修改操作的模态窗口。
                        $("#editActivityModal").modal("show");
                    }
                })
            }



        })


		$("#addBtn").click(function(){

            //模态窗口中的日历
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});


			//从后台拿用户信息，展示到模态窗口中的“所有者”那里。
			$.ajax({
				url: "workbench/activity/getUserList.do",
				async: true,
				type: "get",
				dataType: "json",
				success: function(response){

					var html = "<option></option>";

					//遍历json、json数组、普通数组、dom数组
					$.each(response, function(k, v){

						html += "<option value="+ v.id +">"+ v.name +"</option>";

					})
					$("#create-marketActivityOwner").html(html);

					var id = "${user.id}";
					$("#create-marketActivityOwner").val(id);

					//打开模态窗口
					$("#createActivityModal").modal("show");
				}
			})
		})

		//创建市场活动
		$("#saveBtn").click(function(){

			$.ajax({
				url: "workbench/activity/save.do",
				data: {
					"owner": $.trim($("#create-marketActivityOwner").val()),
					"name": $.trim($("#create-marketActivityName").val()),
					"startDate": $.trim($("#create-startTime").val()),
					"endDate": $.trim($("#create-endTime").val()),
					"cost": $.trim($("#create-cost").val()),
					"description": $.trim($("#create-describe").val())
				},
				async: true,
				type: "post",
				dataType: "json",
				success: function(response){

					/*response: {"success": true/false}*/


					if(response.success){

						//添加成功后，回到首页，保持用户设置的每页显示条数。
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						$("#createActivityModal").modal("hide");

						/*表单重置*/
						$("#activityAddForm")[0].reset();

					}else{

						alert("操作失败");
					}
				}
			})
		})


		$("#searchBtn").click(function(){

			/*点击查询按钮，把搜索框中的内容保存到隐藏域中。*/

			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1, 2);
		})


        /*当点击删除的时候，执行市场活动删除操作。
        * 1.对要删除的市场活动打勾
        * 2.获取打勾的市场活动的value
        * 3.把value传递给服务器
        * 4.在服务器端删除对应的数据
        * 5.局部刷新市场活动列表：pageList(1, 2);
        * */
        $("#deleteBtn").click(function () {

            /*判断是否有✔的条目，有则删除，没有则提示
            因为Jquery对象是一个数组，所以通过判断数组的长度来判断是否有✔的数据。
            * */
            var $xz = $("input[name=xz]:checked");

            if($xz.length == 0){

                alert("请选中需要删除的数据后点击删除操作！")

            }else {

				if(confirm("你确定要删除选中的记录吗？")){

					/*参数拼接
                * 例子：id=8a30a2449273480d8acc1c69bf329193&id=1cedb28ddcda4fd79098b9f44a2395bd
                * */
					var param = "";

					for(i=0; i<$xz.length; i++){

						param += "id=" + $($xz[i]).val();

						if(i<$xz.length-1){
							param += "&";
						}
					}

					$.ajax({
						url: "workbench/activity/delete.do",
						data: param,
						type: "post",
						dataType: "json",
						success: function (response) {
							/*返回response：{"success": true/false}*/
							if(response.success){

								//刷一下pageList()
								//pageList(1, 2);
								pageList(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

							}else {

								alert("删除失败！");
							}
						}
					})

				}


            }
        })
	});

	//分页展示市场活动信息
    //1.根据条件从服务器端拿到数据
    //2.展示数据
	function pageList(pageNo, pageSize) {

        /*把全选的复选框的✔去掉*/
        $("#qx").prop("checked", false);

		/*查询前，将隐藏域中的值取出来，重新赋给搜索框
		* 触发查询有2中情况，1是点击查询，2是点击页码栏。
		* 当我点击页码栏的时候，会直接走pageList(pageNo, pageSize)，
		* 这样，搜索框中的内容是之前保存到隐藏域的，就能解决：
		* 当在页面上修改搜索条件但不点击查询时，结果依然是
		* 按照之前查询条件得出的结果。
		* */
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));


		$.ajax({

			url: "workbench/activity/pageList.do",
			data:{

				"pageNo": pageNo,
				"pageSize": pageSize,
				"name": $.trim($("#search-name").val()),
				"owner": $.trim($("#search-owner").val()),
				"startDate": $.trim($("#search-startDate").val()),
				"endDate": $.trim($("#search-endDate").val()),

			},
			type: "get",
			dataType: "json",
			success: function (response) {

				/*
				* 数据：
				* 	我们需要的：市场活动信息列表：[{市场活动1},{2},{3}]
				*	分页插件需要的，查询出来的总记录数：{"total": 100}
				*
				* 	{"total": 100, "dataList": [{市场活动1},{2},{3}]}
				* */

				var html = "";

				$.each(response.dataList, function(i, v){

					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" value="'+v.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+v.id+'\';">'+v.name+'</a></td>';
					html += '<td>'+v.owner+'</td>';
					html += '<td>'+v.startDate+'</td>';
					html += '<td>'+v.endDate+'</td>';
					html += '</tr>';

				})

				$("#activityBody").html(html);

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

	<%--我们采用隐藏域来保存搜索框的内容。--%>
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="activityAddForm">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startTime" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endTime" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">

                        <%--在浏览器上修改完这条市场活动记录后，我们需要点击更新，把修改后的数据返回给服务器。
                        这时候还要把id返回给服务器，因为id是这条记录的唯一标识。只有这样，服务器才能找到这条记录，
                        并对它进行修改。所以，在表单中添加一个隐藏标签，用来保存这条市场活动的id。点击更新后，
                        从这里取到id--%>
                        <input type="hidden" id="edit-id"/>
					
						<div class="form-group">
							<label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">



								</select>
							</div>
                            <label for="edit-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate" readonly>
							</div>
							<label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate" readonly>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新123</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改123</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称123</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">

				<div id="activityPage"></div>

			</div>
			
		</div>
		
	</div>
</body>
</html>