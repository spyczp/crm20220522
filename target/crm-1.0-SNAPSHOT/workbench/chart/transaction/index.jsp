<%@page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
    <title> mytitle</title>
    <script src="ECharts/echarts.min.js"></script>
    <script src="jquery/jquery-1.11.1-min.js"></script>

    <script>

        $(function(){

            getChart();

        })

        function getChart() {

            $.ajax({
                url: "workbench/transaction/getChart.do",
                type: "get",
                dataType: "json",
                success: function (response) {
                    /*
                    response: {
                        "total": 总交易数,
                        "dataList":
                            [{ value: 交易数, name: '阶段1' },
                            { value: 交易数, name: '阶段2' },
                            { value: 交易数, name: '阶段3' },...]
                            }
                        */

                    // 基于准备好的dom，初始化echarts实例
                    var myChart = echarts.init(document.getElementById('main'));

                    option = {
                        title: {
                            text: '交易阶段漏斗图',
                            subtext: '统计各个阶段的交易数'
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: '{a} <br/>{b} : {c}'
                        },
                        toolbox: {
                            feature: {
                                dataView: { readOnly: false },
                                restore: {},
                                saveAsImage: {}
                            }
                        },
                        legend: {
                            data: ['Show', 'Click', 'Visit', 'Inquiry', 'Order']
                        },
                        series: [
                            {
                                name: '交易阶段',
                                type: 'funnel',
                                left: '10%',
                                top: 60,
                                bottom: 60,
                                width: '80%',
                                min: 0,
                                max: response.total,
                                minSize: '0%',
                                maxSize: '100%',
                                sort: 'descending',
                                gap: 2,
                                label: {
                                    show: true,
                                    position: 'inside'
                                },
                                labelLine: {
                                    length: 10,
                                    lineStyle: {
                                        width: 1,
                                        type: 'solid'
                                    }
                                },
                                itemStyle: {
                                    borderColor: '#fff',
                                    borderWidth: 1
                                },
                                emphasis: {
                                    label: {
                                        fontSize: 20
                                    }
                                },
                                data: response.dataList
                                /*[
                                    { value: 60, name: 'Visit' },
                                    { value: 40, name: 'Inquiry' },
                                    { value: 20, name: 'Order' },
                                    { value: 80, name: 'Click' },
                                    { value: 100, name: 'Show' }
                                ]*/
                            }
                        ]
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }
            })



        }

    </script>
</head>
<body>

    <!-- 为 ECharts 准备一个定义了宽高的 DOM -->
    <div id="main" style="width: 600px;height:400px;"></div>

</body>
</html>