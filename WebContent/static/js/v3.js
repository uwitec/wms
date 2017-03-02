// JavaScript Document
/*
 时间校验
 * */
function validate(str){
	var reg=new RegExp("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
	if(!reg.test(str)){
	alert("请输入正确日期格式(yyyy-mm-dd hh:mm:ss)!")
		return false;
	}else{
		return true;
	}
}
function validateMore(str,mod){
	var reg_1=new RegExp("^((?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-02-29)$");
	var reg_2=new RegExp("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
	if(mod=="1"){
		if(!reg_1.test(str)){
			alert("请输入正确日期格式(yyyy-mm-dd)!")
				return false;
			}else{
				return true;
			}
	}else{
		if(!reg_2.test(str)){
			alert("请输入正确日期格式(yyyy-mm-dd hh:mm:ss)!")
				return false;
			}else{
				return true;
			}
	}
}
function resultValidate(mod){
	var flag=true;
	$(".form_datetime").each(function(){
		var dt=$(this).val();
		if($(this).index==0){
			if(dt==""){
				alert("起始时间不能为空!");
				$(this).focus();
				flag=false;
				return false;
			}else{
				if(!validateMore(dt,mod)){
					$(this).focus();
					flag=false;
					return false;
				}
			}
		}else{
			if(dt!=""){
				if(!validateMore(dt,mod)){
					$(this).focus();
					flag=false;
					return false;
				}
			}
		}
		
	})
	
	return flag;
}
/*
bootstrap-datetimepicker控件
 * */
//重绘时间控件左右滑动按钮
$(".form-control").on("focus",function(){
	$("th.prev span").text("<");
	$("th.next span").text(">");
});

/*
*
*页面载入运行函数
*
*/
$(function(){
	
	var global_arr = new Array();
     // @param options 传入的参数，一般指定列序号,判断某一列中的值是否相等，若相等则将下一列中对应单元格的值进行求和，同时合并单元格
     $.fn.rowspan=function(options){
         var defaults = {};
         var options = $.extend(defaults, options);
         this.each(function () {
             var tds=$(this).find("tbody td:nth-child("+options.td+")");
             var current_td=tds.eq(0);
             var k=1;
             tds.each(function(index, element) {
                 if($(this).text()==current_td.text()&&index!=0){
                     k++;
                     var total = Number($(this).next().text()) + Number(current_td.next().text());
                     $(this).next().remove();
                     current_td.next().attr("rowspan",k);
                     current_td.next().css("vertical-align","middle");
                     current_td.next().text(total);
                 }else{
                     current_td=$(this);
                     k=1;
                 }
             });
         });
    };
    //调用rowspan函数，对相同商品的新库存入库数进行合并显示 
    $(".goods").each(function(){
        $(this).rowspan({td:1});
    });
    
    $(".goodsBychen").each(function(){
        $(this).rowspan({td:2});
    });
  
    
    
    //调用遮罩插件
    $("a.pop").mask();
    $('#track_status > a').toggle(function(){
        $('#track_status ul').show();
        $(this).html('取消');
    },function(){
        $('#track_status ul').hide();
        $(this).html('跟踪其他状态');
    })

    $('.service_box').each(function(){
        var service = $(this);
        //咨询回复功能和gmail一样
        service.find('.c_reply').hide();
        service.find('.c_reply').next('.operate').hide();
        service.find('.c_reply').next('.operate').each(function(){
            var last = $(this).attr('last');
            if(last == 0){
                $(this).hide();
                $(this).prev().hide();
            }else if(last == 1){
                $(this).show();
                $(this).prev().show();
            }
        })
        service.find('.summary').not(':last').each(function(){
            $(this).click(function(){
                $(this).find('span:last').css('display') == 'none' ? $(this).find('span:last').show() : $(this).find('span:last').hide();
                $(this).parent().find('.operate').hide();
                $(this).next('.c_reply').slideToggle('fast');
            });
        });
//        service.find('.confirm_refund').each(function(){
//        	$(this).click(function(){
//        		alert("hello everyone！");
//        		return false;
//        	});
//        });
        //退款、红包
        service.find('.money').click(function(){
            service.find('.b_amount').each(function(){
                $(this).show();
                $(this).find('span').text("退款金额：");
                $(this).find('input').val($(this).attr('amount'));
                $(this).find('.pop').attr('is_backbonus','0').text('确认退款');
                service.find('.back_account_a').show();
                service.find('.bank_info').show();
            });
            return false;
        });
        service.find('.bonus').click(function(){
            service.find('.b_amount').each(function(){
                $(this).show();
                $(this).find('span').text("红包金额：");
                var a = $(this).find(':input[name="amount"]').val();
                $(this).find('.pop').attr('is_backbonus','1').text('发送红包').bind('click', function(event){
                    if (a == '' || a <= 0) {
                        alert('金额不能为0');
                        event.stopPropagation();
                    }
                });
            });
            return false;
        });
        service.find('.no_sub').click(function(){
            service.find('.b_amount').hide();
            service.find('.back_account_a').hide();
            service.find('.bank_info').hide();
            return false;
        });
        //ajax提交回复内容
        service.find('.givePost').click(function(){
            //寻找到一个订单的hidden Input值传递给popDiv里的hidden
            var hInput = [];
            var givePost = $(this);
            givePost.parent().parent().find("input[type='hidden']").each(function(i){
                hInput[i] = $(this).val();
            });
            var comment = {};
            if(hInput[0] == 'reply'){
                comment.act = hInput[0];
                comment.service_id = hInput[1];
                comment.review_remark = $(this).parent().parent().find('textarea').val();
                if(comment.review_remark == ""){
                    $(this).nextAll('strong').html('请输入回复内容').fadeIn(1000).fadeOut(4000);
                    return false;
                }
            }else if(hInput[0] == 'reply_comment'){
                comment.act = hInput[0];
                comment.service_comment_id = hInput[4];
                comment.reply = $(this).parent().parent().find('textarea').val();
                if(comment.reply == ""){
                    $(this).nextAll('strong').html('请输入回复内容').fadeIn(1000).fadeOut(4000);
                    return false;
                }
            }
            Ajax.call('sale_service_actionV3.php',comment,postResponse,'post','json');
            function postResponse(r){
                alert(r.message);
                var html = '';
                givePost.parent().parent().find('.write').remove();
                if(givePost.parent().parent().find('.summary')) givePost.parent().parent().find('.summary').remove();
                if(givePost.parent().parent().find('.s_content')) givePost.parent().parent().find('.s_content').remove();
                if(r.review_remark != undefined){
                    html = '<div class="summary "><span class="s_first">'+r.review_datetime+'</span><strong class="reply">'+r.review_username+'</strong><span style="display:none;">'+r.review_remark+'</span></div><div class="s_content c_reply">'+r.review_remark+'<br/><a href="#" class="edit">修改回复</a></div>';
                }
                if(r.reply  != undefined ){
                    html = '<div class="summary "><span class="s_first">'+r.replied_datetime+'</span><strong class="reply">'+r.replied_username+'</strong><span style="display:none;">'+r.reply+'</span></div><div class="s_content c_reply">'+r.reply+'<br/><a href="#" class="edit">修改回复</a></div>';
                }
                givePost.parent().before(html);
                if(givePost.nextAll('.cancel')) givePost.nextAll('.cancel').hide();
                givePost.hide();
                edit();
            }
            return false;
        });
        service.find("a.pop").each(function(){
            $(this).click(function(){
                var title = $(this).html();
                var service_order_goods_id = [], service_amount=[] , order_goods_id=[] , is_approved=[] , service_order_goods_json=[];
                $('.popw h2').html(title);
                $('.popw input[name="act"]').val($(this).attr('actVal'));
                $('.popw input[name="service_id"]').val($(this).parent().attr('sId'));
                $('.popw input[name="service_type"]').val($(this).attr('type'));
                $('.popw input[name="service_status"]').val($(this).attr('status'));
                $('.popw input[name="service_call_status"]').val($(this).attr('call'));
                $('.popw input[name="apply_amount"]').val($(this).parent().find('input[name="amount"]').val());
                $('.popw input[name="is_backbonus"]').val($(this).attr('is_backbonus'));
                $('.popw input[name="misc_fee"]').val($(this).attr('misc_fee'));
                var amount_info = {
                    is_back_shipping : $(this).attr('is_back_shipping'),
                    is_shipping_fee : $(this).attr('is_shipping_fee'),
                    is_goods_amount : $(this).attr('is_goods_amount'),
                    is_backbonus : $(this).attr('is_backbonus')
                };
                $('.popw input[name="amount_info_json"]').val(JSON.stringify(amount_info));
                $(this).parent().parent().parent().parent().find('input[name$="[service_order_goods_id]"]').each(function(i){
                    service_order_goods_id[i] = $(this).val();
                });
                $('.popw input[name="service_order_goods_id[]"]').val(service_order_goods_id);

                $(this).parent().parent().parent().parent().find('input[name$="[service_amount]"]').each(function(i){
                	service_amount[i] = $(this).val();
                });
                $('.popw input[name="service_amount[]"]').val(service_amount);
                
                $(this).parent().parent().parent().parent().find('input[name$="[order_goods_id]"]').each(function(i){
                    order_goods_id[i] = $(this).val();
                });
                $('.popw input[name="order_goods_id[]"]').val(order_goods_id);
                
                for(var i=0;i<service_order_goods_id.length;i++){
                    var tmp = {service_order_goods_id:service_order_goods_id[i],service_amount:service_amount[i],order_goods_id:order_goods_id[i]};
                    service_order_goods_json.push(tmp);
                };

                $('.popw input[name="service_order_goods_json"]').val(JSON.stringify(service_order_goods_json));
                //删除同意退回弹出框里的输入框功能，直接显示提交即可
                if(title == '同意退回'){
                    $('.popw p:first').html("<b>是否"+title+"?</b>"+"<textarea name='track_result' style='display:none'></textarea>");
                    $('body,html').animate({scrollTop:0},10); 
                    $('.popw p:eq(1) input:first').val("确认");
                    $('.popw textarea').val('');
                }
                else{
                    $('.popw p:first').html("<textarea name='track_result'></textarea>");
                    $('.popw textarea').val('');
                    $('.popw textarea').focus();
                }
            });
        });
        //选择售后的商品
        service.find('th input[name="select_order_goods_id"]').change(function(){
            if($(this).attr('checked') == true){
                service.find('td input[name="is_approved[]"]').attr('checked','checked');
            }else{
                service.find('td input[name="is_approved[]"]').removeAttr('checked');
            }
            service.find('td input[name="is_approved[]"]').each(function(){
                if($(this).attr('checked') == true){
                    $(this).parent().prevAll().css('background','#E8F7E1');
                    $(this).parent().css('background','#E8F7E1');
                }else{
                    $(this).parent().prevAll().css('background','#fff');
                    $(this).parent().css('background','#fff');
                }
            });
        });
        service.find('td input[name="is_approved[]"]').each(function(){
            $(this).change(function(){
                all_checkbox();
            });
        });
        //检查串号
        service.find('.check_erp_sn').click(function(){
            var erp_goods_sns = service.find('input[name="erp_goods_sn"]');
            var is_ok = true;
            if (erp_goods_sns.length != 0){
                for (var i = 0; i < erp_goods_sns.length; i++) {
                    var erp_goods_sn = service.find('input[name="erp_goods_sn"]').eq(i);
                    if (erp_goods_sn.val() != erp_goods_sn.parent().find('span').text()) {
                        is_ok = false;
                        break;
                    }
                }
            }
            if(!is_ok){
                alert("输入的串口与商品串号不统一！");
                return false;
            }
        });
        service.find('.price_info .b_amount').show();
        service.find('.price_info .money').hide();
        service.find('.price_info .pop').attr('is_goods_amount',1);
        service.find('.price_info .pop').attr('is_shipping_fee',1);
        service.find('.price_info .pop').attr('is_back_shipping',1);
        service.find('.price_info .pop').attr('is_backbonus',1);
        misc_fee = $.trim(service.find('.price_info :input[name="misc_fee"]').val());
        misc_fee = misc_fee.length == 0 ? 0 : parseInt(misc_fee) ;
        service.find('.price_info .pop').attr('misc_fee', misc_fee);
        service.find('.price_info :input[name="misc_fee"]').bind('keyup', function(){
            var iAmount = 0;
            service.find('.price_info input:hidden').each(function(){
                var oCheckBox = $(this);
                iAmount += parseInt(oCheckBox.val());
            });
            v = $.trim($(this).val());
            v = v.length == 0 ? 0 : parseInt(v);
            service.find('.price_info .pop').attr('misc_fee', v);
            iAmount += v;
            service.find('.price_info input[name="amount"]').val(iAmount);
        });

    });
    //提交更改状态
    $('.sub').each(function(){
        $(this).click(function(){
            var track = {};
            track.act = $(this).parent().parent().find('input[name="act"]').val();
            track.track_result = $(this).parent().parent().find('textarea').val();
            track.apply_note = track.track_result;
            track.remark = track.track_result;
            track.apply_amount = $(this).parent().parent().find('input[name="apply_amount"]').val();
            track.service_id = $(this).parent().parent().find('input[name="service_id"]').val();
            track.service_type = $(this).parent().parent().find('input[name="service_type"]').val();
            track.service_status = $(this).parent().parent().find('input[name="service_status"]').val();
            track.service_order_goods_id = $(this).parent().parent().find('input[name="service_order_goods_id[]"]').val();
            track.order_goods_id = $(this).parent().parent().find('input[name="order_goods_id[]"]').val();
            track.service_amount = $(this).parent().parent().find('input[name="service_amount[]"]').val();
            track.service_order_goods_json = $(this).parent().parent().find('input[name="service_order_goods_json"]').val();
            track.service_call_status = $(this).parent().parent().find('input[name="service_call_status"]').val();
            track.apply_amount = $(this).parent().parent().find('input[name="apply_amount"]').val();
            track.is_backbonus = $(this).parent().parent().find('input[name="is_backbonus"]').val();
            track.misc_fee = $(this).parent().parent().find('input[name="misc_fee"]').val();
            track.amount_info_json = $(this).parent().parent().find('input[name="amount_info_json"]').val();
            Ajax.call('sale_service_actionV3.php',track,trackResponse,'post','json');
        });
    });
    function trackResponse(r){
        if(r.error == 0){
            alert("成功提交！");
            location.reload();
        }
        if(r.error == 1){
            alert("提交失败，请重新提交 \n以下内容请截图给ERP：\ncontent="+r.content+' error='+r.error+' message='+r.message);
        }
        if(r.error == 2){  // 当输入的商品数量超过了可以申请的数量,阻止提交
            alert("提交失败:输入的商品数量超过了可以申请的数量，请重新提交！ \nERP debug info：\ncontent="+r.content+' error='+r.error+' message='+r.message);
            location.reload();
        }
        if(r.error == 3){  // 当数据库操作失败并回滚时
            alert("对不起，操作失败！请联系ERP \n以下内容请截图给ERP：\ncontent="+r.content+' error='+r.error+' message='+r.message);
            location.reload();
        }
        if(r.error == 4){  // 当数据库操作失败并回滚时
            alert("对不起，生成退货订单失败！请联系ERP \nERP debug info：\ncontent="+r.content+' error='+r.error+' message='+r.message);
            location.reload();
        }
        if(r.error == 5){
            alert("All Hail Sinri Edogawa! \n以下内容请截图给ERP：\ncontent="+r.content+' error='+r.error+' message='+r.message);
            location.reload();
        }
    }
    //预先检测表单
    all_checkbox();
    //表单选择
    function all_checkbox(){
        $('.back_goods').each(function(){
            var is_all = $(this).find('td input[name="is_approved[]"]').length == $(this).find('td input[name="is_approved[]"]:checked').length;
            if(is_all){
                $(this).find('th input[name="select_order_goods_id"]').attr('checked','checked');
            }else{
                $(this).find('th input[name="select_order_goods_id"]').removeAttr('checked');
            }

            $(this).find('td input[name="is_approved[]"]').each(function(){
                if($(this).attr('checked') == true){
                    $(this).parent().prevAll().css('background','#EEF7FF');
                    $(this).parent().css('background','#EEF7FF');
                }else{
                    $(this).parent().prevAll().css('background','#fff');
                    $(this).parent().css('background','#fff');
                }
            });
        });
    }
    //对提交的银行信息进行修改
    $('.edit_bank_info').each(function(){
        var sid = $(this).attr('sId');
        $('#c_'+sid).click(function(){
            $('#edit_'+sid).hide();
            $('#a_'+sid).html('信息不对？立刻修改');
        })
        $(this).click(function(){
            $('#edit_'+sid).css('display') == 'none' ? $(this).html('取消修改') : $(this).html('信息不对？立刻修改');
            $('#edit_'+sid).css('display') == 'none' ? $('#edit_'+sid).show() : $('#edit_'+sid).hide();
            return false;
        })
    })
    //隐藏操作记录
    $('.tr_tab').click(function(){
        //$(this).parent().find('td').parent().css('display') == 'none' ?  $(this).parent().find('td').parent().show() : $(this).parent().find('td').parent().hide();
        //$(this).parent().find('td').parent().css('display') == 'none' ?  $(this).find('a').html('展开') : $(this).find('a').html('关闭');
        return false;
    });
    $('.action_class').each(function(){
        var action_class = $(this);
        action_class.nextAll('.remak_table').css('display') == 'none' ? action_class.find('.action_remak').css({fontWeight:"normal",background:"#EEF7FF"}) :  action_class.find('.action_remak').css({fontWeight:"bold",background:"#BFE1FF"});
        action_class.nextAll('.log_table').css('display') == 'none' ? action_class.find('.action_log').css({fontWeight:"normal",background:"#EEF7FF"}) :  action_class.find('.action_log').css({fontWeight:"bold",background:"#BFE1FF"});
        action_class.nextAll('.rmatracker_table').css('display') == 'none' ? action_class.find('.rmatracker').css({fontWeight:"normal",background:"#EEF7FF"}) :  action_class.find('.rmatracker').css({fontWeight:"bold",background:"#BFE1FF"});
        $(this).find('.action_remak').click(function(){
            $(this).parent().find('.action_log').css({fontWeight:"normal",background:"#EEF7FF"});
            $(this).parent().find('.rmatracker').css({fontWeight:"normal",background:"#EEF7FF"});
            $(this).css({fontWeight:"bold",background:"#BFE1FF"});
            action_class.nextAll('.remak_table').show();
            action_class.nextAll('.log_table').hide();
            action_class.nextAll('.rmatracker_table').hide();
            return false;
        })
        $(this).find('.action_log').click(function(){
            $(this).parent().find('.action_remak').css({fontWeight:"normal",background:"#EEF7FF"});
            $(this).parent().find('.rmatracker').css({fontWeight:"normal",background:"#EEF7FF"});
            $(this).css({fontWeight:"bold",background:"#BFE1FF"});
            action_class.nextAll('.log_table').show();
            action_class.nextAll('.remak_table').hide();
            action_class.nextAll('.rmatracker_table').hide();
            return false;
        })
        $(this).find('.rmatracker').click(function(){
            $(this).parent().find('.action_log').css({fontWeight:"normal",background:"#EEF7FF"});
            $(this).parent().find('.action_remak').css({fontWeight:"normal",background:"#EEF7FF"});
            $(this).css({fontWeight:"bold",background:"#BFE1FF"});
            action_class.nextAll('.rmatracker_table').show();
            action_class.nextAll('.remak_table').hide();
            action_class.nextAll('.log_table').hide();
            return false;
        })
        //切换
        if (action_class.next().find('.bank_info').attr('is_amount') != 1) {
            action_class.next().find('.bank_info').css('display') == 'none' ? action_class.find('.back_account_a').css({
                fontWeight: "normal",
                background: "#EEF7FF"
            }) : action_class.find('.back_account_a').css({
                fontWeight: "bold",
                background: "#BFE1FF"
            });
            action_class.nextAll('.back_express_info').css('display') == 'none' ? action_class.find('.back_express_a').css({
                fontWeight: "normal",
                background: "#EEF7FF"
            }) : action_class.find('.back_express_a').css({
                fontWeight: "bold",
                background: "#BFE1FF"
            });
        }
        $(this).find('.back_account_a').click(function(){
            $(this).next().css({fontWeight:"normal",background:"#EEF7FF"});
            $(this).css({fontWeight:"bold",background:"#BFE1FF"});
            action_class.next().find('.bank_info').show();
            action_class.nextAll('.back_express_info').hide();
            return false;
        })

        $(this).find('.back_express_a').click(function(){
            $(this).prev().css({fontWeight:"normal",background:"#EEF7FF"});
            $(this).css({fontWeight:"bold",background:"#BFE1FF"});
            action_class.nextAll('.back_express_info').show();
            action_class.next().find('.bank_info').hide();
            return false;
        })

    })
    //修改回复
    function edit(){
        $('.edit').each(function(){
            $(this).click(function(){
                var edit = $(this);
                edit.text('');
                var text = $(this).parent().text();
                var html= '<div class="write"><textarea name="reply">'+text+'</textarea></div>';
                edit.parent().hide();
                edit.parent().after(html);
                edit.parent().prevAll('.summary').hide();
                edit.parent().nextAll('.operate').show().find('.givePost').show();
                edit.parent().nextAll('.operate').find('strong').before('<input type="button" class="cancel button_1" value="取消" />');
                edit.parent().nextAll('.write').find('textarea').focus();
                edit.parent().nextAll('.operate').find('.cancel').click(function(){
                    edit.parent().prevAll('.summary').show();
                    edit.parent().prevAll('.s_content').show();
                    edit.parent().nextAll('.write').remove();
                    $(this).parent().find('.givePost').hide();
                    edit.parent().show();
                    edit.text('修改回复');
                    $(this).remove();
                });
                return false;
            });
        });
    }
    edit();

    //送检
    $('.form_submit').each(function(){
        var form = $(this);
        var checked_new = false;
        form.find('.open_check').click(function(){
            form.find('.check_goods_result').show();
        });
        form.find('.check').click(function(){
            form.find('input[name="shipping_type"]').val('pre_outer_check');
            return confirm("确定入送检库吗？");
        });
        form.find('.have_problem').click(function(){
            form.find('input[name="shipping_type"]').val('outer_check');
            form.find('input[name="outer_check"]').val('haveproblem');
            check_new();
            if(!checked_new){
                alert('请选择商品的新旧情况');
                return false;
            }
            if(form.find('textarea').val() == ''){
                alert("请输入检测结果");
                return false;
            }
            if(confirm("确定检测有质量问题，入库吗？")){
                return true;
            }else{
                form.find('input[name="back_goods_ids[]"]').removeAttr('checked');
                return false;
            };
        });
        form.find('.no_problem').click(function(){
            form.find('input[name="shipping_type"]').val('outer_check');
            form.find('input[name="outer_check"]').val('noproblem');
            return (confirm("确定无质量问题，拒绝吗？"));
        });
        form.find('.huajia_all').click(function(){
            form.find('input[name="shipping_type"]').val('warranty_huajia');
            return (confirm("确定所有维修费用吗？确认后数据无法恢复"));
        });
        form.find('.baoxiu_all').click(function(){
            form.find('input[name="shipping_type"]').val('warranty_baoxiu');
            return (confirm("确定要免费保修以上所有商品吗？确认后数据无法恢复"));
        });
        form.find('.wancheng_all').click(function(){
            form.find('input[name="shipping_type"]').val('warranty_wancheng');
            return (confirm("确定已完成送修？"));
        });
        form.find('.warranty_check').click(function(){
            form.find('input[name="shipping_type"]').val('warranty_check');
            form.find('input[name="inner_check"]').val('pass');
            if(confirm("确定送修吗？")){
                return true;
            }else{
                return false;
            };
        });
        //form.find('.examine_refuse').click(function(){
            //form.find('input[name="inner_check"]').val('refused');
         //   alert('这是验货拒绝哦！');
       // });
    });

    /*ajax函数*/
    var Ajax = {
        call : function(transferUrl,params,callback,transferMode,responseType,asyn){
            $.ajax({
                url : transferUrl,
                data : params,
                //beforeSend : Ajax.showLoader,
                //complete : Ajax.hideLoader,
                success : callback,
                type : transferMode,
                dataType : responseType
            });
        },
        showLoader : function (){
            $('body').append("<div id='loader'>"+正在加载+"</div>");
        },
        hideLoader : function(){
            $('#loader').remove();
        }
    };

    

    var AjaxForGoods = {
        call : function(transferUrl,params,callback,transferMode,responseType,asyn){
            $.ajax({
                url : transferUrl,
                data : params,
                beforeSend : function(){
                    $("#load1").fadeOut("slow");
                    $("#shipment_num").fadeOut("slow");
                    $("#load1").slideDown("slow");
                },
                success : callback,
                error:function(){
                    $("#error1").siblings().fadeOut("slow");
                    $("#error1").fadeIn("slow");
                },
                type : transferMode,
                dataType : responseType
            });
        }
    };
    $('.get_carrier').each(function(){
        var oHref = $(this).attr('href');
        var oTd = $(this).parent();
        $(this).click(function(){
            Ajax.call(oHref,'',getCarrierFn,'get','json');
            function getCarrierFn(r){
                var oHtml = '<div style="position:relative;z-index:9999;"><div style="position:absolute;top:0;right:0;background:#fff;border:1px solid green;width:300px;line-height:150%;">' + r + '<a href="#" onclick="$(this).parent().parent().remove();return false;">关闭</a></div></div>';
                oTd.append(oHtml);
            }
            return false;
        });

    });
    $('.create_service').each(function(){
        var order_sn = $(this).attr('sn');
        $(this).click(function(){
            $('#new_'+order_sn).show();
            $('#order_sn_'+order_sn).val(order_sn);
            return false;
        });
        $('#cancel_'+order_sn).click(function(){
            $('#new_'+order_sn).hide();
        });
        $('#new_goods_'+order_sn).click(function(){
            searchOrderGoods(order_sn);
        });
    })
    searchOrderGoods(0);
    function searchOrderGoods(sid){
        /* 填充列表 */
        var order_sn = $("#order_sn_"+sid).val();
        var obj = {act:'get_order_goods_item',order_sn:order_sn,service_id:sid};
        if (order_sn != ''){
            AjaxForGoods.call('sale_service_actionV3.php' , obj, searchOrderGoodsResponse, 'post', 'json');
        }
    }

    /**
     * 退换货搜索商品的返回结果函数，主要用来显示商品的数量信息
     * 
     * @author zjli at 2014.1.20（废老库存）
     * */
    function searchOrderGoodsResponse(result){
    	var arr = result.order_goods;
        var s_id = result.service_id;
        var html = "";
        var available_facility = result.facility_list;
        var facility_name = result.origin_facility_name[0].facility_name;
        var facilityCnt = available_facility.length;  
        if (result.message > 0){
            alert(result.message);
        }
        if (result.error == 0){
        	 $('#goods_list_'+s_id).html('');
        	 // 表头
             html="<table style='width:100%' id='service_goods'>" +
             		"<tr>" +
             		"	<th style='width:60%;text-align:center;'>商品名称</th>" +
             		"	<th style='width:10%;text-align:center;'>商品单价</th>" +
             		"	<th style='width:10%;text-align:center;'>商品总数</th>" +
             		"	<th style='width:10%;text-align:center;'>可操作数</th>" +
             		"	<th style='width:10%;text-align:center;'>申请数量</th>" +
             		"</tr>";
             var titleHtml = '订单号：<a href="order_edit.php?order_id='+result.order.order_id+'">'+ result.order.order_sn+'</a><span style="margin-left:30px;">'+result.order.status_text+'</span>';
             $('#order_title_'+s_id).html(titleHtml);
             var goodsCnt = arr.length;
             if (goodsCnt > 0){
                 for (var i = 0; i < goodsCnt; i++)
                 {
                	 global_arr[i] = arr[i].rec_id;
                	 // 显示商品条目
                     html += "<tr>" +
                     		"	<td style='text-align:center;'><a href='http://www.ouku.com/goods"+arr[i].goods_id+"/' target='_blank'>" + arr[i].goods_name + "</a></td>" +
                     		"	<td style='text-align:center;'><span style='margin:0 10px;'>￥"+parseInt(arr[i].goods_price)+"</span></td>" +
                     		"	<td style='text-align:center;'>" + arr[i].goods_number + "<input type='hidden' name='service_order_goods["+ arr[i].rec_id + "][goods_number]' value='" + arr[i].goods_number + "'/></td>" +
                     		"	<td style='text-align:center;'>" + arr[i].service_amount_available + "<input type='hidden' class='service_amount_available' name='service_aval_goods_amount[" + arr[i].rec_id + "][service_goods_amount]' value='" + arr[i].service_amount_available + "'/></td>" +
                     		"	<td style='text-align:center;'><input type='text' style='text-align:center;width:100%' class='service_order_goods' name='service_order_goods[" + arr[i].rec_id + "][service_goods_amount]' " + (arr[i].cat_name == "耗材商品" ? "disabled='true'" : "") + " value='0'></td>" +
                     		"</tr>";
                 }
                 html += "</table>";
             }//制作HTML结构
             $('#goods_list_'+s_id).html(html);//画表格
             var content = '受理仓库:<select name="facility_id">';
             content += '<option label="未指定仓库" value="0">未指定仓库</option>';
             for (var j = 0;j < facilityCnt;j++){
                    if(available_facility[j].facility_name == facility_name){
                        content += "  <option labl = '"+available_facility[j].facility_name+"' selected='selected' value = '"+available_facility[j].facility_id+"'>"+available_facility[j].facility_name+"</option>";
                    }
                    if(j == facilityCnt-1){
                        content += "  <option labl = '"+available_facility[j].facility_name+"' value = '"+available_facility[j].facility_id+"'>"+available_facility[j].facility_name+"</option></select>";
                    }
                    else{
                        content += "  <option labl = '"+available_facility[j].facility_name+"' value = '"+available_facility[j].facility_id+"'>"+available_facility[j].facility_name+"</option>";
                    }
             }
             $('#origin_facility_name').html('原订单发货仓库:<b>'+facility_name+'</b>');//受理仓库
             $('#available_facility').html(content);
             $("#shipment_num").fadeOut("slow");
             $("#load1").fadeOut("slow");
             $("#shipment_num").html("<p>共搜索到"+ goodsCnt +"件商品</p>").fadeIn("slow");
        }
    }
    
    var html = $('#show_form').html();
    $('#new_goods_0').click(function(){
        searchOrderGoods(0);
    });
    $('#show_form').click(function(){
        $('#new_0').css('display') == 'none' ? $('#new_0').show() : $('#new_0').hide();
        $('#new_0').css('display') == 'none' ? $('#show_form').html(html) : $('#show_form').html('取消新建');
        return false;
    });
    $('#cancel_0').click(function(){
        $('#new_0').hide();
        return false;
    });
    
    $("#all_return").click(function(){    	
        for(var i = 0; i < global_arr.length; i++) {     	
        	if($("input[name = 'service_order_goods[" + global_arr[i] + "][service_goods_amount]']").attr("disabled") != true ) {
	        	var service_goods_amount = $("input[name = 'service_aval_goods_amount[" + global_arr[i] + "][service_goods_amount]']").val();
	        	$("input[name = 'service_order_goods[" + global_arr[i] + "][service_goods_amount]']").val(service_goods_amount);  
        	}
        	     	
        }
    })

});
$(".agree").click(function(){
    var regNumber = new RegExp("^[0-9]*$");
    var n = $(".editAmount").length;
    for(var i=0;i<n;i++){
        if(!regNumber.test($(".editAmount").eq(i).val())){
            alert("请输入数字");
            $("#maskIsShow").val("0");
            return false;

        }
        var editAmount = Number($(".editAmount").eq(i).val());
        var allowAmount = Number($(".editAmount").eq(i).parent().prev().text());
        if(editAmount < 0 || editAmount > allowAmount){
            alert("请输入大于或等于零且小于可操作值的数值,请点击稍后弹出的框框内的取消按钮，重新操作！谢谢");
            $("#maskIsShow").val("0");
            return false;
        }
    }
    
});
// 确认退款时判断杂项费用输入是否合法
$(".confirm_refund").click(function(){
    var n = $(".misc_fee").length;
    for(var i=0;i<n;i++){
    	var misc_fee = Number($(".misc_fee").eq(i).val());
    	var goods_amount = Number($(".misc_fee").eq(i).parent().parent().parent().find(':hidden[name="goods_amount_0"]').val());
    	if(isNaN(misc_fee)){
    		alert("请输入正确的杂项费用（合法的数字）！谢谢~");
    		$("#maskIsShow").val("0");
            return false;
    	}
    	if(misc_fee > goods_amount){
            alert("请输入小于商品总金额的杂项费用,谢谢！");
            $("#maskIsShow").val("0");
            return false;
        }
    }
});
/*
*弹出窗口插件
*问题：弹出窗口插件，在IE下第二次点击关闭会失效，有待解决
*描述：可以通过定义settings对象来设置不同的参数,cClass是关闭的class,way是载入窗口的方法，默认是点击，load为随页面载入
*version:1.0;
*/

(function($){
    $.fn.mask = function(settings,cClass,way){
        var oMask = this;
        if($('#oDiv').length == 0)
        $('body').append('<div id="oDiv" style="display:none;position:absolute;top:0;left:0;height:100%;width:100%;z-index:999;"></div><div id="jump" style="display:none;position:absolute;z-index:1000;"></div>');//body增加弹出层
        settings = $.extend({
            jump:$('#jump'),//弹出窗口id
            mask:$('#oDiv'),//mask的id
            from:$('#popDiv'),//弹出窗口来源
            jWidth: '0',//弹出窗口宽度
            jBg:'#faf3db',//弹出窗口背景
            oBg:'#9FC1BC',//mask背景
            oOpacity:'0.3',//mask透明度
            oFilter:'alpha(opacity=30)',//mask IE下透明度
            H:3,
            W:2.5
        }, settings);
        var jump = settings.jump;
        var mask = settings.mask;
        var from = settings.from;
        var H = settings.H;
        var W = settings.W;
        var showMsg = {
            sTop: document.documentElement.scrollTop,
            sHeight: document.documentElement.scrollHeight,
            cWidth: document.documentElement.clientWidth,
            cHeight: document.documentElement.clientHeight,
            po:function(){
                jump.css('top' , (document.documentElement.scrollTop + document.documentElement.clientHeight/H+ "px"));
                jump.css('left', (document.documentElement.clientWidth/W+"px"));
            },//判断弹出层位置
            mask:function(){
                if(showMsg.sHeight >= showMsg.cHeight){
                    mask.height(showMsg.sHeight + "px");
                }else{
                    mask.height(showMsg.cHeight + "px");
                }
            },//保证mask全屏
            fClose:function(){
                jump.hide();
                //jump.empty();
                mask.hide();
                $('select').each(function(){
                    $(this).css('visibility','');
                });
            },//关闭窗口
            fOpen:function(h){
                jump.css({width:settings.jWidth,background:settings.jBg});
                jump.show();
                from.show();
                mask.css({background:settings.oBg,opacity:settings.oOpacity,filter:settings.OFilter});
                mask.show();
                $('select').each(function(){
                    $(this).css('visibility','hidden');
                });
                jump.append(h);

                var oClose = cClass ? $('.' + cClass) : $('.closeMask');
                oClose.bind('click',
                function(){
                    showMsg.fClose();
                });
            },//打开弹出窗口
            dbClose:function(){
                mask.dblclick(function(){
                    showMsg.fClose();
                })
            },//双击关闭窗口
            showFn:function(){
                showMsg.po();
                showMsg.mask();
                showMsg.dbClose();
                window.onscroll=showMsg.po;
                window.onresize=showMsg.po;
            }//初始化窗口
        };
        return oMask.each(function(){
            switch(way){
                case 'load':
                var h = from.html();
                showMsg.fOpen(h);

                showMsg.showFn();
                break;
                default:
                $(this).click(function(){
                    //var h = from.html();
                    if($("#maskIsShow").val() == "1"){
                        showMsg.fOpen(from);
                        showMsg.showFn();
                        return false;
                    }else{
                        $("#maskIsShow").val("1");
                    }
                    
                });
                break;
            }
        });
    }
})(jQuery);



