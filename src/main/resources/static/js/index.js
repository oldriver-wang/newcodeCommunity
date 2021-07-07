//  在页面加载完以后  点击事件
$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	// 输入框隐藏了
	$("#publishModal").modal("hide");
	//  提示框显示出来
	$("#hintModal").modal("show");

	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title, "content":content},
		function (data) {
			data = $.parseJSON(data);
			// 在提示框中显示返回的消息
			$("hintBody").text(data.msg);
			// 显示提示框
			$("#hintMoodal").modal("show");
			// 两秒后自动隐藏提示框
			setTimeout(function(){
				$("#hintMoodal").modal("hide");
				// 成功的时候刷新页面
				if(data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	)



}