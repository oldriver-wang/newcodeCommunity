$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	// 逻辑
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();



	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName, "content":content},
		function (data) {
			data = $.parseJSON(data);
			 // post返回的参数  json格式字符串

			if(data.code = 0) {
				$("#hintBody").text("发送成功");
			} else{
				$("#hintBody").text(data.msg);
			}

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 重载当前页面
				location.reload();
			}, 2000);
		}
	)

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}