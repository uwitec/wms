<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>打印盘点任务单</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style type="text/css">
		.navMarginB {
			margin: 0 0 20px 0;
		}
		.lq-form-group .num4OnePage {
			width: 50px;
		}
		#iptBlock {
			display: none;
		}
		.tbody_2 .markIpt {
			width: 80px;
		}
		.thead_2 {
			display: none;
		}
		.btns {
			display: none;
			text-align: center;
			margin: 20px 0;
		}
		.save {
			margin: 0 50px 0 0;
		}
		#print{
		    width: 0px;
		    height: 0px;
		    border: 0px;
		}
    </style>
</head>
<body>
	<iframe id="print" src=""></iframe>
	<div class="main-container">
		<ul class="lq-tab-nav navMarginB">
			<li class="active" data-type="printBlock">打印盘点任务单</li>
			<li data-type="iptBlock">盘点任务信息录入</li>
		</ul>
		<div class="lq-form-inline">
			<div id="printBlock">
				<div class="lq-row">
					<div class="lq-col-4">
						<div class="lq-form-group">
							<label>盘点任务号:</label>
							<input id="countSn" type="text" name="" class="lq-form-control" />
						</div>
						<div class="lq-form-group">
							<label>盘点类型:</label>
							<span class="checkType"></span>
						</div>
						<div class="lq-form-group">
							<button id="printBtn" class="lq-btn lq-btn-sm lq-btn-primary" disabled="disabled">打印
							</button>
						</div>
					</div>
					<div class="lq-col-4">
						<div class="lq-form-group">
							<button class="lq-btn lq-btn-sm lq-btn-primary OK">确认</button>
						</div>
						<div class="lq-form-group">
							<label>任务总数:</label>
							<span class="taskSum"></span>
						</div>
						<div class="lq-form-group">
							<label>一页打印</label>
							<input type="text" name="" class="lq-form-control num4OnePage" value="16" />
							<span>(1~16)条盘点任务</span>
						</div>
					</div>
				</div>
				<table class="lq-table">
					<thead>
						<th>盘点任务ID</th>
						<th style="min-width:6rem;">盘点库位</th>
						<th>货主</th>
						<th>渠道</th>
						<th>商品条码</th>
						<th>商品名称</th>
						<th>生产日期</th>
						<th>批次号</th>
						<th>商品属性</th>
						<th>理论数量</th>
						<th>盘点数量</th>
						<th>任务状态</th>
					</thead>
					<tbody class="tbody_1">
						
					</tbody>
				</table>
			</div>
			<div id="iptBlock">
				<div class="lq-row">
					<div class="lq-col-4">
						<div class="lq-form-group">
							<label>盘点任务ID:</label>
							<input type="text" name="" class="lq-form-control task_id" />
						</div>
						<div class="lq-form-group">
							<label>盘点类型:</label>
							<span class="checkType_2"></span>
						</div>
					</div>
					<div class="lq-col-4">
						<div class="lq-form-group">
							<label>盘点任务编号:</label>
							<input type="text" name="" class="lq-form-control batch_task_sn" />
						</div>
					</div>
					<div class="lq-col-4">
						<div class="lq-form-group">
							<button class="lq-btn lq-btn-sm lq-btn-primary OK_2">确认</button>
						</div>
					</div>
				</div>
				<table class="lq-table">
					<thead class="thead_2">
						<th>盘点ID</th>
						<th sytle="min-width: 6rem;">盘点库位</th>
						<th>货主</th>
						<th>渠道</th>
						<th>商品条码</th>
						<th>商品名称</th>
						<th>批次号</th>
						<th>生产日期</th>
						<th class="mark"></th>
					</thead>
					<tbody class="tbody_2">
						
					</tbody>
				</table>
				<div class="btns">
					<button class="lq-btn lq-btn-sm lq-btn-primary save">保存</button>
					<button class="lq-btn lq-btn-sm lq-btn-primary getAdjustmentNote">确认调整</button>
				</div>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript">
	$(function () {
		// active切换
		$('.navMarginB').find('li').click(function () {
			$('.navMarginB').find('li').removeClass('active');
			$('#printBlock').hide();
			$('#iptBlock').hide();
			$(this).addClass('active');
			var this_text = $(this).data('type');
			$('#' + this_text).show();
		});

		// 打印盘点任务单
		var physicalWarehouseId = '${physicalWarehouseId}';
		var couldPrint;
		var onOff = true;
		var countSn;
		// 确认_打印盘点任务单
		$('.OK').click(function () {
			var countSn_P = $.trim($('#countSn').val()).substring(0,1). toUpperCase();
			if (countSn_P != 'P') {
				alert('请填写正确的盘点任务号');
				onOff = false;
			} else {
				countSn = $.trim($('#countSn').val());
			}
			if (onOff) {
				$.ajax({
					url: '../countTask/searchCountSn',
					type: 'post',
					dataType: 'json',
					data: {
						countSn: countSn,
						physicalWarehouseId: physicalWarehouseId
					},
					success: function (res) {
						console.log(res);
						if (res.result == 'failure') {
							alert(res.note);
						}
						$('.checkType').html(res.taskType);
						$('.taskSum').html(res.taskNum);
						couldPrint = res.couldPrint;
						var trHtml = '';
						for (var i in res.taskList) {
							if (!res.taskList[i].hide_real_num) { // hide_real_num: 0
								show_num_real = '*';
							} else {
								show_num_real = res.taskList[i].num_real;
							}
							if (!res.taskList[i].hide_batch_sn) {
								// hide_batch_sn: 0
								show_product_status = '/';
								show_validity = '/';
							} else {
								show_product_status = res.taskList[i].product_status;
								if (res.taskList[i].validity) {
									show_validity = res.taskList[i].validity.substring(0,10);
								} else {
									show_validity = '/';
								}
							}
							trHtml += '<tr><td>'+res.taskList[i].task_id+'</td><td>'+insert_flg(res.taskList[i].location_barcode)+'</td><td>'+res.taskList[i].name+'</td><td>'+res.taskList[i].warehouse_name+'</td><td>'+res.taskList[i].barcode+'</td><td>'+res.taskList[i].product_name+'</td><td>'+show_validity+'</td><td>'+res.taskList[i].batch_sn+'</td><td>'+show_product_status+'</td><td>'+show_num_real+'</td><td></td><td>'+res.taskList[i].status+'</td></tr>';
						}
						$('.tbody_1').html(trHtml);
						// 打印按钮是否置灰
						if (couldPrint.toUpperCase() == 'N') {
							$('#printBtn').attr('disabled', true);
						} else if (couldPrint.toUpperCase() == 'Y') {
							$('#printBtn').attr('disabled', false);
						}
					}
				});
			}
		});
		// 打印
		$('#printBtn').click(function () {
			if (!couldPrint) {
				alert('请确认盘点任务号');
			} else if(parseInt($('.num4OnePage').val())>16||parseInt($('.num4OnePage').val())<1){
				alert('请重新输入每页任务数');
			}
			else if (couldPrint.toUpperCase() == 'Y' && onOff) {
				var src = '../countTask/printBatchStockTask?countSn=' + countSn + '&taskNumPerPage=' + $('.num4OnePage').val();
				$('#print').attr('src', src);
				$(this).attr('disabled', true);
			} else {
				$(this).attr('disabled', true);
				alert('任务状态不统一！');
			}
		});

		// 盘点任务信息录入
		// 确认_盘点任务信息录入
		var batch_task_sn;
		var task_id;
		var mark_Ipt;
		var markIpt_i = [];
		var task_ids = '';
		var nums = '';
		
		function dealWithValidity(obj){

		if(obj.hide_batch_sn==1)
			return obj.validity;
		else
			return '*';
			
		}
		$('.OK_2').click(function () {
			$('.save').attr('disabled', false);
			// 盘点任务ID的校验
			var reg = /^[0-9]\d*$/;
			if (reg.test($('.task_id').val()) || $('.task_id').val() == '') {
				batch_task_sn = $('.batch_task_sn').val();
				task_id = $('.task_id').val();
				$.ajax({
					url: '../countTask/queryBatchTaskCount',
					type: 'post',
					dataType: 'json',
					data: {
						batch_task_sn: batch_task_sn,
						task_id: task_id
					},
					success: function (res) {
						console.log(res);
						if (!res.success) {
							alert(res.message);
							return false;
						} else {
							$('.thead_2').show();
							$('.btns').show();
							mark_Ipt = res.mark;
							$('.checkType_2').html(res.task_type);
							switch (res.mark) {
								case 1:
									$('.mark').html('初盘数量');
									break;
								case 2:
									$('.mark').html('二盘数量');
									break;
								case 3:
									$('.mark').html('三盘数量');
									break;
							}
							var trHtml_2 = '';
							var markIptVal = '';
							var is_disabled = '';
							for (var i in res.list) {
								if (res.list[i].status != '初盘中' && res.list[i].mark == 1) {
									markIptVal = res.list[i].num_first;
									is_disabled = 'disabled="true"';
								} else if (res.list[i].status != '复盘中' && res.list[i].mark == 2) {
									markIptVal = res.list[i].num_second;
									is_disabled = 'disabled="true"';
								} else if (res.list[i].status != '终盘中' && res.list[i].mark == 3) {
									markIptVal = res.list[i].num_third;
									is_disabled = 'disabled="true"';
								}
								trHtml_2 += '<tr><td class="task_id">'+res.list[i].task_id+'</td><td>'+insert_flg(res.list[i].location_barcode)+'</td><td>'+res.list[i].customer_name+'</td><td>'+res.list[i].warehouse_name+'</td><td>'+res.list[i].barcode+'</td><td>'+res.list[i].product_name+'</td><td>'+res.list[i].batch_sn+'</td><td>'+dealWithValidity(res.list[i])+'</td><td><input type="text" name="" class="lq-form-control markIpt" value="'+markIptVal+'" '+is_disabled+'  /></td></tr>';
								markIptVal = '';
							}
							$('.tbody_2').html(trHtml_2);
							markIpt_i = [];
							$('.markIpt').each(function (i, val) {
								if (!$(val).val()) {
									markIpt_i.push(i);
								}
							});
							if (!res.needImprove) {
								$('.getAdjustmentNote').attr('disabled', true);
							} else {
								$('.getAdjustmentNote').attr('disabled', false);
							}
						}
					}
				});
			} else {
				alert('请输入正确的盘点任务ID');
			}
		});
		// 保存
		$('.save').click(function () {
			var that_save = $(this);
			console.log(markIpt_i);
			// 清楚输入框数据
			task_ids = '';
			nums = '';
			for (var j in markIpt_i) {
				task_ids += $('.tbody_2').find('tr').eq(markIpt_i[j]).find('.task_id').text() + ',';
				nums += $('.tbody_2').find('tr').eq(markIpt_i[j]).find('.markIpt').val() + ',';
			}
			task_ids = task_ids.substring(0, task_ids.length - 1);
			nums = nums.substring(0, nums.length - 1);
			console.log(task_ids);
			console.log(nums);
			var reg = new RegExp (',,');
			var is_save = true;
			if (reg.test(nums)) {
				alert('请填写所有的盘点数量');
			} else {
				var num_reg = /^[1-9]\d*|0$/;
				var nums_arr = nums.split(',')
				for (var i in nums_arr) {
					if (!num_reg.test(nums_arr[i])) {
						if ($('.markIpt').attr('disabled')) {
							alert('此盘点任务已回单');
						} else {
							alert('盘点数必须为整数');
							is_save = false;
							break;
						}
					}
				}
				if (is_save) {
					$.ajax({
						url: '../countTask/countIn',
						type: 'post',
						dataType: 'json',
						data: {
							batch_task_sn: batch_task_sn,
							task_id: task_id,
							mark: mark_Ipt,
							task_ids: task_ids,
							nums: nums
						},
						success: function (res) {
							console.log(res);
							if (!res.success) {
								alert(res.message);
							} else {
								alert('保存成功');
								that_save.attr('disabled', true);
							}
							if (!res.needImprove) {
								$('.getAdjustmentNote').attr('disabled', true);
								$('.markIpt').attr('disabled', true);
							} else {
								$('.getAdjustmentNote').attr('disabled', false);
								$('.markIpt').attr('disabled', false);
							}
						}
					});
				}
			}
		});
		// 生成调整单
		$('.getAdjustmentNote').click(function () {
			var that = $(this);
			console.log($('.tbody_2').first().find('.task_id').html());
			$.ajax({
				url: '../countTask/createChangeTask',
				type: 'post',
				dataType: 'json',
				data: {
					task_id: $('.tbody_2').first().find('.task_id').html()
				},
				success: function (res) {
					console.log(res);
					alert(res.message);
					if (!res.needImprove) {
						that.attr('disabled', true);
					} else {
						that.attr('disabled', false);
					}
				}
			});
		});
		// 库位转换 
		function insert_flg(str){
		    var newstr="";
		    var before = str.substring(0,3), after = str.substring(3,7);
		    newstr = before + "-" + after;
		    str = newstr;
		    var before = str.substring(0,6), after = str.substring(6,8);
		    newstr = before + "-" + after;
		    str = newstr;
		    return newstr;
		}

		// ctrl键跳转下个input
		$(document).on('keyup', '.markIpt', function (e) {
			if (e.keyCode == 17) {
				var that = $(this);
				var thatIndex = that.parents('tr').index();
				var nextNode = $('.markIpt').eq(thatIndex + 1).focus();
			}
		});
	});
</script>
</html>