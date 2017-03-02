var TOTAL_PAGE = 0,
	START_PAGE = 1,
	MAX_PAGE = 10,
	END_PAGE = 10,
	CURRENT_PAGE = 1,
	PAGE_DATA = {},
	PAGE = {};

function initialParam(){
	TOTAL_PAGE = 0,
	START_PAGE = 1,
	MAX_PAGE = 10,
	END_PAGE = 10,
	CURRENT_PAGE = 1,
	PAGE_DATA = {},
	PAGE = {};
}

function transform(status_num) {
	var status="";
	switch(status_num) {
	case "INIT": status="未处理";break;
	case "RESERVED": status="已分配";break;
	case "RESERVE_FAILED": status="分配失败";break;
	case "CANCEL": status="已取消";break;
	case "FULFILLED": status="已完成";break;
	case "IN_PROCESS": status="处理中";break;
	case "PART_FULFILLED": status="部分完成";break;
	case "": status="不限";break;
	case "BOX_CLOSED": status="全打包";break;
	case "BOX_OPEN": status="半打包";break;
	case "UNPACK": status="拆分套餐";break;
	default: status="-";break;
	}
	return status;
}

function str2Date(str) {
	var date=new Date(str);
	var month=date.getMonth()+1;
	var year=date.getFullYear();
	var day=date.getUTCDate();
	return year+"-"+month+"-"+day+" "+date.toString().split(' ')[4];
}

function pageConstructor(currentPage,startPage,endPage) {
	var currentPage = currentPage,
		startPage = startPage,
	    endPage = endPage;
	if (TOTAL_PAGE) {
		$(".page-wrap").html("").fadeIn(300);
		var pageHtml = '<div class="lq-row"><nav id="page-search"><ul class="lq-page-control">';
		if (startPage !== 1) {
			pageHtml +='<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>';
		}
		for (var i=startPage;i<startPage+endPage;i++) {
			pageHtml += '<li';
			if (i == currentPage) {
				pageHtml += ' class="active"';
			}
			pageHtml += '><a href="#">' + i + '</a></li>';
		}
		if (startPage+endPage-1 !== TOTAL_PAGE) {
			pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li></ul></nav></div>';
		}
		
		$(".page-wrap").html(pageHtml);
	} else {
		$(".page-wrap").html("").fadeOut(300);
	}
}

function clickPage(obj,url,param,showTable){
	var that=obj;
	if (!that.hasClass("prev") && !that.hasClass("next")) {
		if (!that.hasClass("active")) {
			var index = parseInt(obj.find("a").text());
			CURRENT_PAGE = index;
			var data={
					currentPage:index,
					pageSize: 15
			};
			var data=$.extend(data,param);
			console.log(data);
			$.ajax({
				url: url,
				type : "get",
				dataType : "json",
				data: data,
				beforeSend : function(){
					that.addClass("active").siblings().removeClass("active");
					$(".mw-loading").fadeIn(300);
				},
				success : function(res) {
					console.log(res);
					$(".mw-loading").fadeOut(300);
					if(res.list)
						//由于之前有个页面已经写了暂时放着以后找到修改这个分支
						showTable(res.list);
					else{
						for(var o in res){
							console.log(res[o]+":"+res[o].length);
							if(res[o].length>=1&&typeof(res[o])=='object'){
								showTable(res[o]);
							}
						}
					}
						
				},
				error:function(err){
					$(".mw-loading").fadeOut(300);
					alert(err);
				}
			});
		}
	} else if (that.hasClass("next")) {
		START_PAGE = parseInt(that.prev().find('a').text()) + 1;
		var REST_PAGE = TOTAL_PAGE - parseInt(that.prev().find('a').text()),
		    END_PAGE = 0;
		if (REST_PAGE >= 0 && REST_PAGE <= MAX_PAGE) {
			END_PAGE = REST_PAGE;
			pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE); 
		} else if (REST_PAGE > MAX_PAGE) {
			END_PAGE = MAX_PAGE;
			pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
		}
		
	} else if (that.hasClass("prev")) {
		var END_PAGE = MAX_PAGE;
		START_PAGE = parseInt(that.next().find('a').text()) - MAX_PAGE;
		//alert("END_PAGE:"+END_PAGE+",START_PAGE:"+START_PAGE);
		pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
	}
	
}