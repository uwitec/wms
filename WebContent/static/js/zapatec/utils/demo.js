Zapatec.demo=function() {
this.init()
}

Zapatec.demo.prototype.init=function() {
this.demoTab=null
this.htmlTab=null
this.cssTab=null
}
//--------------------------------------------------------------------------
/*
Function called when a demo is clicked on in the tree
*/
Zapatec.demo.prototype.changeDemo=function(url, css, demo) {
   // are tabs build yet?
	if (!this.demoTab)
		return;
	var _this=this
	if (this.strPath != undefined)
		url=this.strPath + url
	if (this.idAlternate)
		document.getElementById(this.idAlternate).style.display = "none";
	document.getElementById("tabs_content").style.display = "block";
	this.demoTab.container.getContainer().style.height = "700px";
	this.demoTab.setPaneContent(url, "html/url");
	this.demoTab.container.fireWhenReady(function(pane) {
		var height = 0;
		var doc = this.iframeDocument;
		if (!doc) {return null;}
		if (doc.compatMode && doc.compatMode == 'CSS1Compat') {
			height = doc.documentElement.scrollHeight || doc.documentElement.offsetHeight;
		} else {
			height = doc.body.scrollHeight || doc.documentElement.scrollHeight;
		}
		this.getContainer().style.height = (height + 30) + "px";
	});

	var this_htmlTab=this.htmlTab
	Zapatec.Transport.fetch({
		url : url + (Zapatec.is_opera ? "?" : ""),
		onLoad : function(response) {
			this_htmlTab.value = response.responseText;
		},
		onError : function(error) {
			alert(error.errorDescription);
		}
	});
	this.cssTab.value = "";
	var this_cssTab=this.cssTab
	function fetchCSS(theme) {
		Zapatec.Transport.fetch({
			url : (_this.strPath == undefined ? '../themes/' : _this.strPath + '../themes/') + theme + '.css',
			onLoad : function(response) {
				this_cssTab.value += "/* " + theme + " */\n\n" + response.responseText + "\n";
			},
			onError : function(error) {
				alert(error.errorDescription);
			}
		});
	}
	for (var i = 0; i < css.length; ++i) {
		if (css[i] == 'none') {
			this_cssTab.value = "There is no theme for this demo";
		} else {
			fetchCSS(css[i]);
		}
	}
	document.title = "AJAX " + this.strWidget + " - " + this.zpdemos[demo].title;
	this.zptree.sync(demo);
}
		
//--------------------------------------------------------------------------
//--------------------------------------------------------------------------
/*
Build the demo tree
el - append the tree under this element
*/
Zapatec.demo.prototype.tree_build=function() {
// add top level tree
//document.write('<ul id="zpDemoList" style="list-style-image: url(../../website/images/arrow-grey.gif); margin-left: 20px; padding-left: 0px;">\n')
document.write('<ul id="zpDemoList" style="list-style-image: url('+ Zapatec.zapatecPath + 'dhelp/themes/img/arrow-grey.gif); margin-left: 20px; padding-left: 0px;">\n')

var this_zpdemos=this.zpdemos
var func_add=function(demo)
{
document.write('\t\t<li id="' + demo + '"><a href="#' + demo + '" onclick="zpDemo.changeDemo(zpDemo.zpdemos[\'' + demo + '\'].url, zpDemo.zpdemos[\'' + demo + '\'].css, \'' + demo + '\');">' + this_zpdemos[demo].title + '</a></li>\n');
}

// Some demos might be in Basic and other groups
var demo, i, bHasBasicOnly=true
// Default that all demos go in Basic if NOT in a group
for (demo in this.zpdemos)
	this.zpdemos[demo].bBasic=true

// If in group other then Basic then define Basic group not needed
for (var group in this.zpdemogroups) {
	if (group=='Basic')
		continue
	bHasBasicOnly=false
	for (i=0; i<this.zpdemogroups[group].length; i++)
	{
		demo=this.zpdemogroups[group][i]
		if (this.zpdemos[demo] != undefined)
			this.zpdemos[demo].bBasic=false
	}
}

// Check if demo is in group Basic 
group='Basic'
if (this.zpdemogroups[group] != undefined)
{
for (i=0; i<this.zpdemogroups[group].length; i++)
	{
		demo=this.zpdemogroups[group][i]
		if (this.zpdemos[demo] != undefined)
			this.zpdemos[demo].bBasic=true
	}
}

// Now add Basic first
var bHasBasic=false
for (demo in this.zpdemos)
{
	if (!this.zpdemos[demo].bBasic)
		continue
	if (!bHasBasic) {
		bHasBasic=true
		document.write('\t\t<li ' + (bHasBasicOnly ? 'class="expanded"' : '') + '>Basic<ul>\n')
	}
	func_add(demo)
}
if (bHasBasic)
	document.write('\t\t</ul></li>\n');

// Last - add all non-Basic demos
for (var group in this.zpdemogroups) {
	if (group=='Basic')
		continue
	document.write('\t\t<li>' + group + '<ul>\n');
	for (i=0; i<this.zpdemogroups[group].length; i++)
		func_add(this.zpdemogroups[group][i])
	document.write('\t\t</ul></li>\n');
}

document.write('\t\t</ul>');
}

// Load first demo, done after tree and tabs loaded
Zapatec.demo.prototype.demo_load=function() {
			var hash = "";
			if (hash = window.location.hash.slice(1)) {
				this.changeDemo(this.zpdemos[hash].url, this.zpdemos[hash].css, hash);
			} else {
				if (this.zpdefDemo) {
				location.href += "#" + this.zpdefDemo;
				this.changeDemo(this.zpdemos[this.zpdefDemo].url, this.zpdemos[this.zpdefDemo].css, this.zpdefDemo);
				}
				else { // no anchor, disable tab pane, enable alternate
					document.getElementById("tabs_content").style.display = "none";
					if (this.idAlternate)
						document.getElementById(this.idAlternate).style.display = "block";
				}
			}
}
Zapatec.demo.prototype.tree_load=function() {
			this.zptree = new Zapatec.Tree("zpDemoList", {'expandOnLabel': true, theme: 'demos'});
}

//--------------------------------------------------------------------------


Zapatec.demo.prototype.tab_build=function() 
{
			var tabs = {
				tabs : [
					{
						id : "demo",
						linkInnerHTML : "<span style='text-decoration : underline; border : none; padding : 0px;'>D</span>emo",
						accessKey : "d",
						title : "demo",
						tabType : "iframe"
					},
					{
						id : "html",
						linkInnerHTML : "HTM<span style='text-decoration : underline; border : none; padding : 0px;'>L</span>",
						accessKey : "l",
						title : "HTML part of the demo.",
						content : document.getElementById("html_source"),
						tabType : "div"
					},
					{
						id : "css",
						linkInnerHTML : "CS<span style='text-decoration : underline; border : none; padding : 0px;'>S</span>",
						accessKey : "s",
						title : "CSS part of the demo.",
						content : document.getElementById("css_source"),
						tabType : "div"
					}
				]
			};
      		// Create a new demo|css|html tabs widget instance
			var objTabs = new Zapatec.Tabs({
			  tabBar : "tabBar",
			  tabs : "tabs",
			  source: tabs,
			  sourceType : "json",
			  themePath : Zapatec.zapatecPath + '/dhelp/themes/',
			  changeUrl : false,
			  theme: 'default',
			  ignoreUrl: true
			});
			
			this.demoTab = objTabs.tabs["demo"];
			this.htmlTab = document.getElementById("html_source");
			this.cssTab = document.getElementById("css_source");

			var hash = "";
			this.demoTab.container.getContainer().style.width = "100%";
			this.demoTab.container.getContainer().style.height = "700px";
}
