<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.atom.crm.settings.domain.DicValue" %>
<%@ page import="java.util.List" %>
<%@ page import="com.atom.crm.workbench.domain.Tran" %>
<%@page contentType="text/html; charset=utf-8" language="java" %>

<%

	//拿到stage类型的所有dicValue
	List<DicValue> stageDicValueList = (List<DicValue>) application.getAttribute("stage");

	//拿到阶段-可能性关系字典
	Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

	//拿到pMap中的key集合
	Set<String> dicTypeSet = pMap.keySet();

	//*准备：前面正常阶段和后面丢失阶段的分界点下标
	int point = 0;
	for(int i=0; i<stageDicValueList.size(); i++){
		DicValue stageDV = stageDicValueList.get(i);
		String value = stageDV.getValue();
		String possibility = pMap.get(value);

		if("0".equals(possibility)){
			point = i;
			break;
		}
	}


%>
<!DOCTYPE html>
<html>
<head>
	<base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

<style type="text/css">
.mystage{
	font-size: 20px;
	vertical-align: middle;
	cursor: pointer;
}
.closingDate{
	font-size : 15px;
	cursor: pointer;
	vertical-align: middle;
}
</style>
	
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;

	/*
	* json = {"阶段1": 可能性1, "阶段2": 可能性2, "阶段3": 可能性3, ...}
	* */
	var json = {

		<%

        Set<String> keys = pMap.keySet();

        for(String key: keys){

            String value = pMap.get(key);

        %>

		"<%=key%>": <%=value%>,

		<%

        }
        %>

	}
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});
		
		
		//阶段提示框
		$(".mystage").popover({
            trigger:'manual',
            placement : 'bottom',
            html: 'true',
            animation: false
        }).on("mouseenter", function () {
                    var _this = this;
                    $(this).popover("show");
                    $(this).siblings(".popover").on("mouseleave", function () {
                        $(_this).popover('hide');
                    });
                }).on("mouseleave", function () {
                    var _this = this;
                    setTimeout(function () {
                        if (!$(".popover:hover").length) {
                            $(_this).popover("hide")
                        }
                    }, 100);
                });

		//下面的代码，是获取页面上的“阶段”数据，
		//然后把应用域中的“阶段-可能性”数据读取为json变量。
		//最后，根据“阶段”名称取得对应的“可能性”数据，赋值给页面上
		//的可能性标签中。

		var stage = $("#stage").text();

		$("#possibility>b").html(json[stage]);

		//展示交易“阶段历史”列表
		showHistoryList();
	});

	function showHistoryList(){

		$.ajax({
			url: "workbench/transaction/getTranHistory.do",
			data:{
				"tranId": "${tran.id}"
			},
			type: "get",
			dataType: "json",
			success: function (response) {
				/*response: [{交易历史1},{交易历史2},{交易历史3},...]*/

				var html = "";

				$.each(response, function(i, v){

					html += '<tr id="'+v.id+'">';
					html += '<td>'+v.stage+'</td>';
					html += '<td>'+v.money+'</td>';
					html += '<td>'+json[v.stage]+'</td>';
					html += '<td>'+v.expectedDate+'</td>';
					html += '<td>'+v.createTime+'</td>';
					html += '<td>'+v.createBy+'</td>';
					html += '</tr>';

				});

				$("#tranHistoryBody").html(html);
			}
		})

	}

	/**
	 * 改变当前交易的阶段
	 * @param stage 这个图标对应的阶段
	 * @param index 这个阶段的下标
	 */
	function changeStage(stage, index){

		/*
		* 1.先改变数据库中这条交易的阶段数据,生成一条新的交易历史
		* 2.改变页面上的阶段和可能性数据
		* 3.刷新所有阶段图标
		* */
		$.ajax({
			url: "workbench/tran/changeStage.do",
			data: {
				"id": "${tran.id}",
				"stage": stage,
				"money": "${tran.money}",
				"expectedDate": "${tran.expectedDate}",
			},
			type: "post",
			dataType: "json",
			success: function (response) {
				/*response: {"success": true/false, "tran": tran}*/
				if(response.success){

					//2.改变页面上的阶段和可能性数据
					$("#stage>b").html(stage);
					$("#possibility>b").html(json[stage]);
					$("#editBy").html(response.tran.editBy);
					$("#editTime").html(response.tran.editTime);

					//刷新交易历史列表
					showHistoryList();

					//3.刷新所有阶段图标
					//改变阶段成功后，将所有的阶段图标重新判断，重新赋予样式及颜色
					//stage:当前阶段；index:当前阶段的索引
					changeIcon(stage, index);

				}else{
					alert("阶段变更失败");
				}
			}
		})

	}

	function changeIcon(stage, index){

		//当前阶段
		var currentStage = stage;

		//当前阶段的可能性，从页面上的标签提取.
		var currentPossibility = $("#possibility>b").html();

		//当前阶段的索引下标
		var currentIndex = index;

		//分界点下标
		//注意：
		//	1.因为这里是java数据类型转为js类型，只能是8大基本数据类型和String类型。其它引用类型转不了。
		//	2.< %= % > 必须套在引号中。
		var point = "<%=point%>";

		//stageDicValueList的长度
		var stageListLength = "<%=stageDicValueList.size()%>";

		/*alert("当前阶段: " + currentStage);
		alert("当前阶段的可能性: " + currentPossibility);
		alert("当前阶段的索引下标: " + currentIndex);
		alert("分界点下标: " + point);
		alert("长度: " + stageListLength);*/

		//下面，又要开始画所有图标了。这次是采用js来写。
		//如果当前阶段可能性为0，则阶段图标为：7个黑圈，2个×：一个黑×，一个红×（红×代表当前阶段）
		if("0" == currentPossibility){

			//point分界点之前都是黑圈
			//注意：这里i的值实则对应了stageDicValueList中的各个阶段的索引下标。
			for(var i=0; i<point; i++){

				/*黑圈--------------------------*/
				//移除掉原有的样式
				$("#"+i).removeClass();
				//添加新样式
				$("#"+i).addClass("glyphicon glyphicon-record mystage");
				//为新样式赋予颜色
				$("#"+i).css("color", "#000000");

			}

			//point分界点之后都是叉：有红叉和黑叉
			//注意：这里i的值实则对应了stageDicValueList中的各个阶段的索引下标。
			for(var i=point; i<stageListLength; i++){
				//如果遍历到的i等于当前阶段的索引下标，则是红叉
				if(i == currentIndex){
					/*红叉----------------------*/
					//移除掉原有的样式
					$("#"+i).removeClass();
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					//为新样式赋予颜色
					$("#"+i).css("color", "#FF0000");

				}else{
					//否则是黑叉
					/*黑叉------------------------*/
					//移除掉原有的样式
					$("#"+i).removeClass();
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					//为新样式赋予颜色
					$("#"+i).css("color", "#000000");
				}


			}
			//当前阶段可能性不为0时
		}else{
			//如果当前阶段可能性不为0，则阶段图标为：本阶段之前的阶段为“绿圈”，本阶段为“绿下标”，本阶段之后的可能性不为0的阶段为“黑圈”，可能性为0的阶段为黑×。
			for(var i=0; i<point; i++){
				//绿圈，绿标，黑圈
				if(i == currentIndex){
					/*绿标--------------------------*/
					//移除掉原有的样式
					$("#"+i).removeClass();
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-map-marker mystage");
					//为新样式赋予颜色
					$("#"+i).css("color", "#90F790");

				}else if(i < currentIndex){
					/*绿圈--------------------------*/
					//移除掉原有的样式
					$("#"+i).removeClass();
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-ok-circle mystage");
					//为新样式赋予颜色
					$("#"+i).css("color", "#90F790");

				}else{
					/*黑圈-----------------------------*/
					//移除掉原有的样式
					$("#"+i).removeClass();
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-record mystage");
					//为新样式赋予颜色
					$("#"+i).css("color", "#000000");

				}
			}

			for(var i=point; i<stageListLength; i++){
				/*黑叉-----------------------*/
				//移除掉原有的样式
				$("#"+i).removeClass();
				//添加新样式
				$("#"+i).addClass("glyphicon glyphicon-remove mystage");
				//为新样式赋予颜色
				$("#"+i).css("color", "#000000");
			}

		}

	}
	
</script>

</head>
<body>
	
	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${tran.customerId}-${tran.name} <small>￥${tran.money}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" onclick="window.location.href='edit.html';"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>

	<!-- 阶段状态 -->
	<div style="position: relative; left: 40px; top: -50px;">
		阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

		<%

			//准备当前阶段和可能性
			Tran tran = (Tran) request.getAttribute("tran");
			String currentStage = tran.getStage();
			String currentPossibility = pMap.get(currentStage);

			//判断当前阶段
			//如果当前阶段的可能性为0，前7个一定是黑圈。后面2个一个是黑×，一个是红×
			//以下代码的核心作用是为了动态铺下面的span标签
			if("0".equals(currentPossibility)){
				//遍历阶段-可能性
				//阶段图标的定义：
				// 	如果当前阶段可能性为0，则阶段图标为：7个黑圈，2个×：一个黑×，一个红×（红×代表当前阶段）
				//	如果当前阶段可能性不为0，则阶段图标为：本阶段之前的阶段为“绿圈”，本阶段为“绿下标”，本阶段之后的可能性不为0的阶段为“黑圈”，可能性为0的阶段为黑×。

				//遍历所有阶段-可能性
				for(int i=0; i<stageDicValueList.size(); i++){
					DicValue cycleStageDicValue = stageDicValueList.get(i);
					String cycleStage = cycleStageDicValue.getValue();
					String cyclePossibility = pMap.get(cycleStage);

					//如果遍历到的下标大于等于分界点下标，
					// 		且遍历到的阶段等于当前阶段，则遍历到的阶段为红叉；
					// 		否则为黑叉。
					/*if(i >= point){
						if(cycleStage.equals(currentStage)){
							*//*红叉----------------------*//*
						}else{
							*//*黑叉------------------------*//*
						}
						//i<point的情况，都是黑圈。
					}else{
						*//*黑圈-----------------------*//*
					}*/
					//方法2
					if("0".equals(cyclePossibility)){
						if(cycleStage.equals(currentStage)){
							/*红叉-----------------------*/
		%>

			<span id="<%=i%>" onclick="changeStage('<%=cycleStage%>', '<%=i%>')" class="glyphicon glyphicon-remove mystage" data-toggle="popover" data-placement="bottom" data-content="<%=cycleStageDicValue.getText()%>" style="color: #FF0000;"></span>
		-----------
		<%
						}else{
							/*黑叉-------------------------*/

		%>

			<span id="<%=i%>" onclick="changeStage('<%=cycleStage%>', '<%=i%>')" class="glyphicon glyphicon-remove mystage" data-toggle="popover" data-placement="bottom" data-content="<%=cycleStageDicValue.getText()%>" style="color: #000000;"></span>
		-----------
		<%
						}
					}else{
						/*黑圈------------------------*/
		%>

			<span id="<%=i%>" onclick="changeStage('<%=cycleStage%>', '<%=i%>')" class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="<%=cycleStageDicValue.getText()%>" style="color: #000000;"></span>
		-----------
		<%
					}
				}
				//当前阶段可能性不为0时
			}else{
				//获取当前阶段的下标
				int currentStageIndex = 0;

				for(int i=0; i<stageDicValueList.size(); i++) {
					DicValue cycleStageDicValue = stageDicValueList.get(i);
					String cycleStage = cycleStageDicValue.getValue();

					if(cycleStage.equals(currentStage)){
						currentStageIndex = i;
						break;
					}
				}

				//	如果当前阶段可能性不为0，则阶段图标为：本阶段之前的阶段为“绿圈”，本阶段为“绿标”，本阶段之后的可能性不为0的阶段为“黑圈”，可能性为0的阶段为黑×。
				for(int i=0; i<stageDicValueList.size(); i++) {
					DicValue cycleStageDicValue = stageDicValueList.get(i);
					String cycleStage = cycleStageDicValue.getValue();
					String cyclePossibility = pMap.get(cycleStage);

					if(i < currentStageIndex) {
						/*绿圈------------------------*/
		%>

			<span id="<%=i%>" onclick="changeStage('<%=cycleStage%>', '<%=i%>')" class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="<%=cycleStageDicValue.getText()%>" style="color: #90F790;"></span>
		-----------
		<%
					}else if(i == currentStageIndex){
						/*绿标-------------------*/
		%>

			<span id="<%=i%>" onclick="changeStage('<%=cycleStage%>', '<%=i%>')" class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom" data-content="<%=cycleStageDicValue.getText()%>" style="color: #90F790;"></span>
		-----------
		<%
					}else if(i > currentStageIndex && i < point){
						/*黑圈----------------------*/
		%>

			<span id="<%=i%>" onclick="changeStage('<%=cycleStage%>', '<%=i%>')" class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="<%=cycleStageDicValue.getText()%>" style="color: #000000;"></span>
		-----------
		<%
					}else if(i >= point){
						/*黑叉-------------------------*/
		%>

			<span id="<%=i%>" onclick="changeStage('<%=cycleStage%>', '<%=i%>')" class="glyphicon glyphicon-remove mystage" data-toggle="popover" data-placement="bottom" data-content="<%=cycleStageDicValue.getText()%>" style="color: #000000;"></span>
		-----------
		<%
					}
				}
			}

		%>
		<%--<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="资质审查" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="价值建议" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="确定决策者" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom" data-content="提案/报价" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="谈判/复审"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="成交"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="丢失的线索"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="因竞争丢失关闭"></span>
		-------------%>
		<span class="closingDate">${tran.expectedDate}</span>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: 0px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.money}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}-${tran.name}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.expectedDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">客户名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;" id="stage"><b>${tran.stage}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">类型</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.type}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;" id="possibility"><b></b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">来源</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.source}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.activityId}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">联系人名称</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.contactsId}</b></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${tran.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">${tran.editBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;" id="editTime">${tran.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${tran.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					&nbsp;${tran.contactSummary}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 100px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>&nbsp;${tran.nextContactTime}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 100px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		
		<!-- 备注1 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<!-- 备注2 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>呵呵！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary">保存</button>
				</p>
			</form>
		</div>
	</div>
	
	<!-- 阶段历史 -->
	<div>
		<div style="position: relative; top: 100px; left: 40px;">
			<div class="page-header">
				<h4>阶段历史</h4>
			</div>
			<div style="position: relative;top: 0px;">
				<table id="activityTable" class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td>阶段</td>
							<td>金额</td>
							<td>可能性</td>
							<td>预计成交日期</td>
							<td>创建时间</td>
							<td>创建人</td>
						</tr>
					</thead>
					<tbody id="tranHistoryBody">
						<%--<tr>
							<td>资质审查</td>
							<td>5,000</td>
							<td>10</td>
							<td>2017-02-07</td>
							<td>2016-10-10 10:10:10</td>
							<td>zhangsan</td>
						</tr>
						<tr>
							<td>需求分析</td>
							<td>5,000</td>
							<td>20</td>
							<td>2017-02-07</td>
							<td>2016-10-20 10:10:10</td>
							<td>zhangsan</td>
						</tr>
						<tr>
							<td>谈判/复审</td>
							<td>5,000</td>
							<td>90</td>
							<td>2017-02-07</td>
							<td>2017-02-09 10:10:10</td>
							<td>zhangsan</td>
						</tr>--%>
					</tbody>
				</table>
			</div>
			
		</div>
	</div>
	
	<div style="height: 200px;"></div>
	
</body>
</html>