// $Id: calendar-zh.js 4434 2006-09-14 08:01:19Z shacka $
// ** Translated by ATang ** I18N
Zapatec.Calendar._DN = new Array
("周日",
 "周一",
 "周二",
 "周三",
 "周四",
 "周五",
 "周六",
 "周日");
Zapatec.Calendar._MN = new Array
("一月",
 "二月",
 "三月",
 "四月",
 "五月",
 "六月",
 "七月",
 "八月",
 "九月",
 "十月",
 "十一月",
 "十二月");

// tooltips
Zapatec.Calendar._TT_zh = Zapatec.Calendar._TT = {};
Zapatec.Calendar._TT["TOGGLE"] = "切换周开始的一天";
Zapatec.Calendar._TT["PREV_YEAR"] = "上一年 (按住出菜单)";
Zapatec.Calendar._TT["PREV_MONTH"] = "上一月 (按住出菜单)";
Zapatec.Calendar._TT["GO_TODAY"] = "到今日";
Zapatec.Calendar._TT["NEXT_MONTH"] = "下一月 (按住出菜单)";
Zapatec.Calendar._TT["NEXT_YEAR"] = "下一年 (按住出菜单)";
Zapatec.Calendar._TT["SEL_DATE"] = "选择日期";
Zapatec.Calendar._TT["DRAG_TO_MOVE"] = "拖动";
Zapatec.Calendar._TT["PART_TODAY"] = " (今日)";
Zapatec.Calendar._TT["MON_FIRST"] = "首先显示星期一";
Zapatec.Calendar._TT["SUN_FIRST"] = "首先显示星期日";
Zapatec.Calendar._TT["CLOSE"] = "关闭";
Zapatec.Calendar._TT["TODAY"] = "今日";

// date formats
Zapatec.Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Zapatec.Calendar._TT["TT_DATE_FORMAT"] = "%a, %b %e";

Zapatec.Calendar._TT["WK"] = "周";

/* Preserve data */
	if(Zapatec.Calendar._DN) Zapatec.Calendar._TT._DN = Zapatec.Calendar._DN;
	if(Zapatec.Calendar._SDN) Zapatec.Calendar._TT._SDN = Zapatec.Calendar._SDN;
	if(Zapatec.Calendar._SDN_len) Zapatec.Calendar._TT._SDN_len = Zapatec.Calendar._SDN_len;
	if(Zapatec.Calendar._MN) Zapatec.Calendar._TT._MN = Zapatec.Calendar._MN;
	if(Zapatec.Calendar._SMN) Zapatec.Calendar._TT._SMN = Zapatec.Calendar._SMN;
	if(Zapatec.Calendar._SMN_len) Zapatec.Calendar._TT._SMN_len = Zapatec.Calendar._SMN_len;
	Zapatec.Calendar._DN = Zapatec.Calendar._SDN = Zapatec.Calendar._SDN_len = Zapatec.Calendar._MN = Zapatec.Calendar._SMN = Zapatec.Calendar._SMN_len = null
