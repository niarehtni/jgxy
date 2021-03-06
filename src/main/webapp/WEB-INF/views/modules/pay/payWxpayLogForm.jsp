<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信支付通知编辑</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		function doSubmit(){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		  if(validateForm.form()){
			  $("#inputForm").submit();
			  return true;
		  }
	
		  return false;
		}
		$(document).ready(function() {
			//$("#name").focus();
			validateForm=$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body class="hideScroll">
	<form:form id="inputForm" modelAttribute="payWxpayLog" action="${ctx}/pay/payWxpayLog/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>			
		<table class="table table-bordered table-condensed dataTables-example dataTable no-footer">
		<tbody>
			<tr>
				<td class="width-15 active"><label class="pull-right">公众账号：</label></td>
				<td class="width-35">
					<form:input path="appid" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
				<td class="width-15 active"><label class="pull-right">商户号：</label></td>
				<td class="width-35">
					<form:input path="mchId" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">签名：</label></td>
				<td class="width-35">
					<form:input path="sign" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
				<td class="width-15 active"><label class="pull-right">签名类型：</label></td>
				<td class="width-35">
					<form:input path="signType" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">业务结果：</label></td>
				<td class="width-35">
					<form:input path="resultCode" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
				<td class="width-15 active"><label class="pull-right">错误代码：</label></td>
				<td class="width-35">
					<form:input path="errCode" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">交易类型：</label></td>
				<td class="width-35">
					<form:input path="tradeType" htmlEscape="false" maxlength="30" class="form-control input-xlarge"/>
				</td>
				<td class="width-15 active"><label class="pull-right">付款银行：</label></td>
				<td class="width-35">
					<form:input path="bankType" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">用户标识：</label></td>
				<td class="width-35">
					<form:input path="openid" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
				<td class="width-15 active"><label class="pull-right">订单金额：</label></td>
				<td class="width-35">
					<form:input path="totalFee" htmlEscape="false" class="form-control input-xlarge"/>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">现金支付金额：</label></td>
				<td class="width-35">
					<form:input path="cashFee" htmlEscape="false" class="form-control input-xlarge"/>
				</td>
				<td class="width-15 active"><label class="pull-right">微信支付订单号：</label></td>
				<td class="width-35">
					<form:input path="transactionId" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">商户订单号：</label></td>
				<td class="width-35">
					<form:input path="outTradeNo" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
				<td class="width-15 active"><label class="pull-right">支付完成时间：</label></td>
				<td class="width-35">
					<form:input path="timeEnd" htmlEscape="false" maxlength="50" class="form-control input-xlarge"/>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">付款通知处理状态：</label></td>
				<td class="width-35">
					<form:select path="status" cssClass="form-control input-xlarge">
						<form:option value="" label=""/>
						<form:options items="${fns:getDictList('paylog_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</td>
			</tr>
		</tbody>
		</table>
		
	</form:form>
</body>
</html>