/*
 *
 * Copyright (c) 2004-2005 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 *
 *
 */
Zapatec.tabsPath = Zapatec.getPath("Zapatec.TabsWidget");

Zapatec.Modal=function(config){if(arguments.length==0){config={};}
this.visible=false;Zapatec.Modal.SUPERconstructor.call(this,config);}
Zapatec.Modal.id="Zapatec.Indicator";Zapatec.inherit(Zapatec.Modal,Zapatec.Widget);Zapatec.Modal.prototype.init=function(config){Zapatec.Modal.SUPERclass.init.call(this,config);};Zapatec.Modal.prototype.configure=function(config){this.defineConfigOption("zIndex",1000);this.defineConfigOption("x",null);this.defineConfigOption("y",null);this.defineConfigOption("width",null);this.defineConfigOption("height",null);this.defineConfigOption("container",window);this.defineConfigOption("fixed",false);Zapatec.Modal.SUPERclass.configure.call(this,config);config=this.getConfiguration();if(config.container!=window){config.x=null;config.y=null;config.width=null;config.height=null;config.fixed=false;}};Zapatec.Modal.prototype.create=function(){var config=this.getConfiguration();config.container=Zapatec.Widget.getElementById(config.container)||window;this.WCH=Zapatec.Utils.createWCH();this.container=Zapatec.Utils.createElement("div",document.body);this.container.className=this.getClassName({prefix:"zpModal"+(Zapatec.is_opera?"Opera":"")})
var st=this.container.style;st.dispaly="none";st.position="absolute";st.zIndex=config.zIndex;};Zapatec.Modal.prototype.show=function(zIndex){if(!this.container){this.create();}
zIndex=zIndex||this.config.zIndex;this.container.style.zIndex=zIndex;if(this.WCH){this.WCH.style.visibility="";this.WCH.style.zIndex=zIndex-1;}
this.container.style.display="block";this.visible=true;var config=this.getConfiguration();if(config.container!=window){var self=this;var update=function(){self.update();}
if(!this.interval){this.interval=setInterval(update,100);}
this.update();}else{var dim=Zapatec.Utils.getWindowSize();var width=config.width||dim.width;var height=config.height||dim.height;var x=config.x||Zapatec.Utils.getPageScrollX();var y=config.y||Zapatec.Utils.getPageScrollY();this.setWidth(width);this.setHeight(height);this.setPosition(x,y);}
if(this.config.fixed==true){Zapatec.FixateOnScreen.register(this.container);if(this.WCH){Zapatec.FixateOnScreen.register(this.WCH);}}};Zapatec.Modal.prototype.update=function(){var config=this.getConfiguration();if(config.container!=window&&this.visible){var offs=Zapatec.Utils.getElementOffset(config.container);this.setWidth(offs.width);this.setHeight(offs.height);this.setPosition(offs.x,offs.y);}};Zapatec.Modal.prototype.hide=function(destroy){var config=this.getConfiguration();if(config.fixed==true){Zapatec.FixateOnScreen.unregister(this.container);if(this.WCH){Zapatec.FixateOnScreen.unregister(this.WCH);}}
if(config.container!=window){clearInterval(this.interval);this.interval=null;}
if(this.container)this.container.style.display="none";Zapatec.Utils.hideWCH(this.WCH);if(destroy){if(this.WCH){if(this.WCH.outerHTML){this.WCH.outerHTML="";}else{Zapatec.Utils.destroy(this.WCH);}}
if(this.container.outerHTML){this.container.outerHTML="";}else{Zapatec.Utils.destroy(this.container);}
this.WCH=null;this.container=null;}
this.visible=false;};Zapatec.Modal.prototype.setWidth=function(width){if(!this.container){return;}
if(Zapatec.Utils.setWidth){Zapatec.Utils.setWidth(this.container,width);Zapatec.Utils.setWidth(this.WCH,width);}else{this.container.style.width=width+"px";if(this.WCH){this.WCH.style.width=width+"px";}}};Zapatec.Modal.prototype.setHeight=function(height){if(!this.container){return;}
if(Zapatec.Utils.setHeight){Zapatec.Utils.setHeight(this.container,height);Zapatec.Utils.setHeight(this.WCH,height);}else{this.container.style.height=height+"px";if(this.WCH){this.WCH.style.height=height+"px";}}};Zapatec.Modal.prototype.setPosition=function(x,y){if(!this.container){return;}
if(Zapatec.Utils.moveTo){Zapatec.Utils.moveTo(this.container,x,y);Zapatec.Utils.moveTo(this.WCH,x,y);}else{this.container.style.left=x+"px";this.container.style.top=y+"px";if(this.WCH){this.WCH.style.left=x+"px";this.WCH.style.top=y+"px";}}};Zapatec.Indicator=function(config){if(arguments.length==0){config={};}
this.active=false;Zapatec.Indicator.SUPERconstructor.call(this,config);}
Zapatec.Indicator.id="Zapatec.Indicator";Zapatec.inherit(Zapatec.Indicator,Zapatec.Modal);Zapatec.Indicator.prototype.init=function(config){Zapatec.Indicator.SUPERclass.init.call(this,config);};Zapatec.Indicator.prototype.configure=function(config){this.defineConfigOption("themePath",Zapatec.zapatecPath+"../zpextra/themes/indicator/");Zapatec.Indicator.SUPERclass.configure.call(this,config);};Zapatec.Indicator.prototype.create=function(){Zapatec.Indicator.SUPERclass.create.call(this);this.indicator=Zapatec.Utils.createElement("div",this.container);this.indicator.className="zpIndicator";var st=this.indicator.style;st.position="absolute";st.zIndex=this.getConfiguration().zIndex;st.backgroundColor="#aaaaaa";};Zapatec.Indicator.prototype.setWidth=function(width){if(!this.container){return;}
Zapatec.Indicator.SUPERclass.setWidth.call(this,width);var left=Math.round((this.container.offsetWidth-this.indicator.offsetWidth)/2);this.indicator.style.left=left+"px";};Zapatec.Indicator.prototype.setHeight=function(height){if(!this.container){return;}
Zapatec.Indicator.SUPERclass.setHeight.call(this,height);var top=Math.round((this.container.offsetHeight-this.indicator.offsetHeight)/2);this.indicator.style.top=top+"px";};Zapatec.Indicator.prototype.hide=function(destroy){if(destroy){this.indicator=null;}
Zapatec.Indicator.SUPERclass.hide.call(this,destroy);};Zapatec.Indicator.prototype.start=function(message){this.active=true;if(!this.indicator){this.create();}
this.indicator.innerHTML=message;this.show();};Zapatec.Indicator.prototype.stop=function(){this.active=false;this.hide(true);};Zapatec.Indicator.prototype.isActive=function(){return this.active;};Zapatec.Pane=function(objArgs){this.config={};if(arguments.length==0){objArgs={};}
this.widgetType="pane";this.ready=false;this.loading=false;this.prepared=false;Zapatec.Utils.createProperty(this,"container",null);Zapatec.Utils.createProperty(this,"contentElement",null);Zapatec.Utils.createProperty(this,"iframeDocument",null);Zapatec.Pane.SUPERconstructor.call(this,objArgs);}
Zapatec.Pane.id="Zapatec.Pane";Zapatec.inherit(Zapatec.Pane,Zapatec.Widget);Zapatec.Pane.prototype.init=function(objArgs){Zapatec.Utils.createProperty(this.config,"parent",document.body);this.config.theme=null;this.config.width=null;this.config.height=null;this.config.containerType="div";this.config.sourceType=null;this.config.source=null;this.config.autoContentWidth=false;this.config.autoContentHeight=false;this.config.onlyInit=false;this.config.showLoadingIndicator=true;Zapatec.Pane.SUPERclass.init.call(this,objArgs);if(this.config.containerType==null){this.config.containerType="div";}
if(!this.config.onlyInit){this.create(this.config.width,this.config.height);}
var self=this;function update(){if(self.loader){self.loader.update();}}
this.addEventListener("fetchSourceStart",update);this.addEventListener("fetchSourceEnd",update);}
Zapatec.Pane.prototype.prepareHtml=function(){if(this.config.containerType.toLowerCase()=='iframe'){var iframe=document.createElement("iframe");iframe.src=Zapatec.zapatecPath+"pane_files/blank.html#"+this.id;this.container=iframe;iframe=null;}else if(this.config.containerType.toLowerCase()=='div'){this.container=document.createElement("div");this.contentElement=this.container;}else if(this.config.parent&&this.config.parent.nodeType==1){this.container=this.config.parent;this.contentElement=this.container;}
this.prepared=true;};Zapatec.Pane.prototype.create=function(width,height){if(!this.prepared){this.prepareHtml();}
if(!(this.config.parent=Zapatec.Widget.getElementById(this.config.parent))){Zapatec.Log({description:"No reference to parent element."});return null;}
if(this.config.parent.requestParentFor&&!(this.config.parent=this.config.parent.requestParentFor(this))){Zapatec.Log({description:"No reference to parent element after request to the Parent Widget!"});return null;}
if(this.config.containerType.toLowerCase()=='div'||this.config.containerType.toLowerCase()=='iframe'){this.ready=false;this.config.parent.appendChild(this.container);if(this.config.containerType.toLowerCase()!='iframe'){this.initPane();}}else if(this.config.containerType.toLowerCase()=='current'){this.container=this.config.parent;this.contentElement=this.container;this.initPane();}else{Zapatec.Log({description:"Unknown container type: "+this.config.containerType+". Possible values: iframe|div"})}
Zapatec.Utils.addClass(this.container,this.getClassName({prefix:"zpPane"}));if(width||this.config.width){this.setWidth(width||this.config.width);}
if(height||this.config.height){this.setHeight(height||this.config.height);}
this.getContainer().style.display="block";this.setPaneContent();};Zapatec.Pane.prototype.initPane=function(){if(this.config.containerType.toLowerCase()=='iframe'){var doc=null;var sameDomain=true;var url=this.getContainer().src;var anchorEl=document.createElement("a");var protocolSeparatorPos=url.indexOf("://");if(protocolSeparatorPos!=-1){var domainSeparatorPos=url.indexOf("/",protocolSeparatorPos+3);var domain=url.substring((protocolSeparatorPos>0?protocolSeparatorPos+3:0),(domainSeparatorPos>0?domainSeparatorPos:url.length));if(domain!=window.location.host){sameDomain=false;}}
if(sameDomain){if(this.container.contentDocument!=null){doc=this.container.contentDocument;}else if(this.container.contentWindow&&this.container.contentWindow.document!=null){doc=this.container.contentWindow.document;}
var self=this;anchorEl.href=url;url=anchorEl.href;if(doc==null||doc.body==null||(Zapatec.is_gecko&&url!=this.container.contentWindow.location.href)){setTimeout(function(){self.initPane()},50);return false;}
this.iframeDocument=doc;this.contentElement=doc.body;if(typeof this.container.contentWindow.Zapatec!="object"&&typeof this.container.contentWindow.Zapatec!="function"){this.container.contentWindow.Zapatec={};this.container.contentWindow.Zapatec.windowLoaded=typeof(doc.readyState)!='undefined'?(doc.readyState=='loaded'||doc.readyState=='complete'):doc.getElementsByTagName!=null&&typeof(doc.getElementsByTagName('body')[0])!='undefined';Zapatec.Utils.addEvent(this.container.contentWindow,"load",function(){self.container.contentWindow.Zapatec.windowLoaded=true;});}
if(!this.container.contentWindow.Zapatec||!this.container.contentWindow.Zapatec.windowLoaded){setTimeout(function(){self.initPane()},50);return false;}
doc=null;}}
this.getContainer().style.overflow="auto";this.ready=true;this.fireEvent("onReady",this);this.hideIndicator();this.loading=false;this.removeEvent("onReady");}
Zapatec.Pane.prototype.getContainer=function(){return this.container;}
Zapatec.Pane.prototype.getIframeDocument=function(){return this.iframeDocument;}
Zapatec.Pane.prototype.getContentElement=function(){return this.contentElement;}
Zapatec.Pane.prototype.isReady=function(){return this.ready;}
Zapatec.Pane.prototype.loadDataJson=function(objSource){return objSource!=null?this.setContent(objSource.content):null;}
Zapatec.Pane.prototype.setContent=function(content){if(!this.isReady()){var self=this;setTimeout(function(){self.setContent(content)},50);return null;}
this.loading=false;if(!this.getContentElement()){this.hideIndicator();return false;}
if(content===null){this.hideIndicator();return null;}else{if(this.config.containerType.toLowerCase()!="iframe"){var oldOverflow=this.getContainer().style.overflow;if(this.config.autoContentWidth){this.getContainer().style.overflow="visible";this.getContainer().style.width="auto";}
if(this.config.autoContentHeight){this.getContainer().style.overflow="visible";this.getContainer().style.height="auto";}}
if(typeof(content)=='string'){Zapatec.Transport.setInnerHtml({container:this.getContentElement(),html:content});}else{try{if((Zapatec.is_ie||Zapatec.is_opera)&&this.config.containerType.toLowerCase()=="iframe"){Zapatec.Transport.setInnerHtml({container:this.getContentElement(),html:content.outerHTML});}else{if(content.parentNode!=this.getContentElement()){this.getContentElement().innerHTML="";this.getContentElement().appendChild(content);}}}catch(ex){this.hideIndicator();return null;}}
if(this.config.containerType.toLowerCase()!="iframe"){var newWidth=this.getWidth();var newHeight=this.getHeight();}else{var newWidth=this.getContentElement().scrollWidth+5;var newHeight=this.getContentElement().scrollHeight+5;}
if(typeof oldOverflow!="undefined")this.getContainer().style.overflow=oldOverflow;if(this.config.autoContentWidth){this.setWidth(newWidth);}
if(this.config.autoContentHeight){this.setHeight(newHeight);}}
this.fireEvent("contentLoaded",this);this.hideIndicator();return true;}
Zapatec.Pane.prototype.loadDataHtml=Zapatec.Pane.prototype.loadDataXml=Zapatec.Pane.prototype.setContent;Zapatec.Pane.prototype.loadDataHtmlText=function(content){this.setContent(content);};Zapatec.Pane.prototype.setWidth=function(width){var self=this;this.fireWhenReady(function(){self.getContainer().style.width=width+"px";if(self.getContainer().offsetWidth!=width){var newWidth=width-(self.getContainer().offsetWidth-width);if(newWidth<0)newWidth=0;self.getContainer().style.width=newWidth+"px";}});}
Zapatec.Pane.prototype.getWidth=function(){return this.getContainer().offsetWidth;}
Zapatec.Pane.prototype.setHeight=function(height){var self=this;this.fireWhenReady(function(){self.getContainer().style.height=height+"px";if(self.getContainer().offsetHeight!=height){var newHeight=height-(self.getContainer().offsetHeight-height);if(newHeight<0)newHeight=0;self.getContainer().style.height=newHeight+"px";}});}
Zapatec.Pane.prototype.getHeight=function(){return this.getContainer().offsetHeight;}
Zapatec.Pane.prototype.removeBorder=function(){if(this.config.containerType.toLowerCase()!="iframe"){return false;}
var self=this;this.fireWhenReady(function(){if(!Zapatec.is_ie){self.getContainer().style.border="none";}else{if(self.getContentElement()){self.getContentElement().style.border="none";}}});};Zapatec.Pane.prototype.setPaneContent=function(content,type){if(!content&&content!==""){content=this.config.source;}
if(!type){type=this.config.sourceType;}
this.config.source=content;this.config.sourceType=type;var self=this;if(this.config.showLoadingIndicator){this.showIndicator();this.loading=true;}
if(this.config.containerType.toLowerCase()=="iframe"&&type=="html/url"){this.ready=false;this.fireWhenReady(function(){if(self.getContentElement()){try{var newWidth=self.getContentElement().scrollWidth;var newHeight=self.getContentElement().scrollHeight;if(self.config.autoContentWidth){self.setWidth(newWidth);}
if(self.config.autoContentHeight){self.setHeight(newHeight);}}catch(e){}}
self.fireEvent("contentLoaded",self);if(self.events["contentLoaded"]){self.events["contentLoaded"].listeners=[];}});this.getContainer().src=content;setTimeout(function(){self.initPane()},50);return true;}
if(this.config.containerType.toLowerCase()=="iframe"&&this.getContainer().src.indexOf((Zapatec.zapatecPath+"pane_files/blank.html#"+this.id).replace(/\.\.\//g,""))<0){this.ready=false;this.getContainer().src=Zapatec.zapatecPath+"pane_files/blank.html#"+this.id;}
this.loadData();return true;};Zapatec.Pane.prototype.show=function(){this.getContainer().style.display="";if(this.loading){this.showIndicator();}};Zapatec.Pane.prototype.hide=function(){this.getContainer().style.display="none";if(this.loading){this.hideIndicator();}};Zapatec.Pane.prototype.showIndicator=function(message){if(Zapatec.Indicator){this.hideIndicator();if(!this.loader){this.loader=new Zapatec.Indicator({container:this.container,themePath:Zapatec.zapatecPath+"../zpextra/themes/indicator/"});}
this.loader.start(message||'loading');}};Zapatec.Pane.prototype.hideIndicator=function(){if(this.loader&&this.loader.isActive()){this.loader.stop();}};Zapatec.Pane.prototype.fireWhenReady=function(func){if(!this.isReady()){this.addEventListener("onReady",func);}else{func.call(this,this);}}
Zapatec.Pane.prototype.destroy=function(){this.hideIndicator();this.config.parent=null;this.contentElement=null;this.iframeDocument=null;if(Zapatec.is_ie&&this.config.containerType.toLowerCase()=='iframe'){this.container.src="javascript:void(0)";}
if(this.container.outerHTML){this.container.outerHTML="";}else{Zapatec.Utils.destroy(this.container);}
this.container=null;this.ready=false;this.prepared=false;}
Zapatec.Tab=function(objArgs){if(arguments.length==0){objArgs={};}
Zapatec.Tab.SUPERconstructor.call(this,objArgs);};Zapatec.Tab.id='Zapatec.Tab';Zapatec.inherit(Zapatec.Tab,Zapatec.Widget);Zapatec.Tab.prototype.init=function(objArgs){Zapatec.Tab.SUPERclass.init.call(this,objArgs);this.createTab();};Zapatec.Tab.prototype.createTab=function(){this.createProperty(this,'linkNode',null);this.createProperty(this,'container',null);this.createProperty(this,'focusOn',null);this.createProperty(this,'linkHash',null);this.createProperty(this,'pendingOnTabChange',null);this.container=new Zapatec.Pane({containerType:this.config.tabType,parent:this.config.tabParent});this.container.removeBorder();var self=this;var onContentLoaded=function(){if(self.pendingOnTabChange){self.pendingOnTabChange(self);self.pendingOnTabChange=null;}
self.fireEvent('tabOnLoad');setTimeout(function(){self.container.addEventListener('contentLoaded',self.onContentLoaded);},10);}
this.createProperty(this,'onContentLoaded',onContentLoaded);this.container.addEventListener('contentLoaded',onContentLoaded);this.container.hide();this.container.getContainer().style.width='100%';this.container.getContainer().style.height='100%';this.container.getContainer().style.overflow=this.config.overflow;if(!this.config.url){this.setPaneContent();}
else{if(this.config.shouldLoadOnInit){this.setPaneContent();}}
if(this.config.id){this.id=this.config.id;}else{this.id=this.container.getContainer().getAttribute('id');if(typeof this.id=='string'){this.container.getContainer().removeAttribute('id');}}
if(typeof this.id!='string'||!this.id.length){this.id=Zapatec.Utils.generateID('tab');}
this.linkNode=Zapatec.Utils.createElement('a');this.linkNode.name=this.id;this.linkNode.innerHTML='<span><span>'+this.config.linkInnerHTML+'</span></span>';if(this.config.accessKey){this.linkNode.accessKey=this.config.accessKey;}
if(this.config.title){this.linkNode.setAttribute('title',this.config.title);}
var self=this;var onActivate=function(e){self.fireEvent('activateTab');if(this.blur){this.blur();}
var doNavigate=false;if(self.config.changeUrl){if(Zapatec.is_khtml){doNavigate=true;}
else{window.location.hash=self.linkHash;}}
return doNavigate;};if(this.config.mouseOverChangesTab){this.linkNode.onmouseover=onActivate;}
else{this.linkNode.onclick=onActivate;}
this.linkNode.tabIndex=Zapatec.Tab.tabIndex;if(!this.tab2tab){Zapatec.Tab.tabIndex+=2;}
this.linkNode.onfocus=function(){self.fireEvent('activateTab');return true;};this.linkNode.onkeydown=function(ev){ev||(ev=window.event);switch(ev.keyCode){case 13:case 32:if(self.focusOn&&self.focusOn.focus){self.focusOn.focus();}
return false;}
return true;}
if(this.container.getContainer().hasChildNodes()){this.getFocusOn();}}
Zapatec.Tab.prototype.setLinkHash=function(linkHash){this.linkHash=linkHash;this.linkNode.setAttribute('href','#'+linkHash);}
Zapatec.Tab.prototype.configure=function(objArgs){this.defineConfigOption('id',null);this.defineConfigOption('index',-1);this.defineConfigOption('linkInnerHTML','');this.defineConfigOption('accessKey','');this.defineConfigOption('content',null);this.defineConfigOption('contentType',null);this.defineConfigOption('url','');this.defineConfigOption('tabType',"div");this.defineConfigOption('tab2tab',false);this.defineConfigOption('tabParent',null);this.defineConfigOption('title',null);this.defineConfigOption('changeUrl',true);this.defineConfigOption('overflow',"hidden");this.defineConfigOption('mouseOverChangesTab',false);this.defineConfigOption('refreshOnTabChange',false);this.defineConfigOption('shouldLoadOnInit',false);this.defineConfigOption('theme','');this.defineConfigOption('themePath','');Zapatec.Tab.SUPERclass.configure.call(this,objArgs);if(typeof(this.config.tabParent)=="undefined"){Zapatec.Log({description:"No reference to tab parent!"})
return false;}
if(this.config.tab2tab&&false!=this.config.tab2tab){this.config.tab2tab=true;}
if(typeof this.config.tabType=="string"){this.config.tabType=this.config.tabType.toLowerCase();}
if(this.config.tabType!="div"&&this.config.tabType!="iframe")
{this.config.tabType="div";}
if(this.config.overflow==""){this.config.overflow="hidden";}
if(this.config.index<0){this.config.index=-1;}};Zapatec.Tab.tabIndex=1000;Zapatec.Tab.prototype.getFocusOn=function(){this.focusOn=null;if(this.tab2tab){return;}
var self=this;setTimeout(function(){var iTabIndex=0;function parse(objNode){var objChild=objNode.firstChild;while(objChild){if(objChild.nodeType==1){var strTag=objChild.tagName.toLowerCase();if(strTag=='a'||strTag=='input'||strTag=='select'||strTag=='textarea'||strTag=='button'){if(!self.focusOn){self.focusOn=objChild;}else if(objChild.tabIndex&&objChild.tabIndex>0&&(!iTabIndex||iTabIndex>objChild.tabIndex)){self.focusOn=objChild;iTabIndex=objChild.tabIndex;}
if(!objChild.tabIndex){objChild.tabIndex=self.linkNode.tabIndex+1;}}
parse(objChild);}
objChild=objChild.nextSibling;}};parse(self.container);},0);};Zapatec.Tab.prototype.setInnerHtml=function(strHtml){Zapatec.Transport.setInnerHtml({html:strHtml,container:this.container.getContainer()});this.getFocusOn();}
Zapatec.Tab.prototype.setPaneContent=function(content,type){var self=this;var paneContent=null;var paneContentType=null;if(content){paneContent=content;paneContentType=type;}
else if(this.config.url&&0<this.config.url.length){paneContent=this.config.url;paneContentType='html/url';}else{paneContent=this.config.content;paneContentType=this.config.contentType;}
if(paneContent){this.config.content=content;this.config.contentType=type;if(paneContentType=="html/url"){this.config.url=paneContent;this.lastUrlSet=paneContent;}
this.container.setPaneContent(paneContent,paneContentType);}}
Zapatec.Tabs=function(objArgs){if(arguments.length==0){objArgs={};}
Zapatec.Tabs.SUPERconstructor.call(this,objArgs);};Zapatec.Tabs.id='Zapatec.Tabs';Zapatec.inherit(Zapatec.Tabs,Zapatec.Widget);Zapatec.Tabs.prototype.init=function(objArgs){Zapatec.Tabs.SUPERclass.init.call(this,objArgs);this.createTabs();this.initTabBar();};Zapatec.Tabs.prototype.createTabs=function(){this.createProperty(this,'tabs',{});this.createProperty(this,'tabsArray',[]);if(null==this.tabsThemeSuffix){this.tabsThemeSuffix='Content';}
Zapatec.Utils.addClass(this.config.tabs,this.getClassName({prefix:'zpTabs',suffix:this.tabsThemeSuffix}));this.loadData();this.currentIndex=-1;if(typeof this.config.onInit=='function'){this.config.onInit();}
if(this.tabsArray.length){var strId=this.getInitialActiveTabId();this.changeTab(strId);}
if(this.config.windowOnLoad!=null)
{this.config.windowOnLoad();}
if(true!=this.noTabBar){this.addEventListener('loadThemeEnd',function(){this.config.tabBar.style.display='block';});}}
Zapatec.Tabs.prototype.configure=function(objArgs){this.defineConfigOption('tabBar',null);this.defineConfigOption('tabs',null);this.defineConfigOption('onInit',null);this.defineConfigOption('onTabChange',null);this.defineConfigOption('onBeforeTabChange',null);this.defineConfigOption('ignoreUrl',false);this.defineConfigOption('changeUrl',true);this.defineConfigOption('tab2tab',false);this.defineConfigOption('scrollMultiple',null);this.defineConfigOption('iframeContent',null);this.defineConfigOption('tabType','div');this.defineConfigOption('windowOnLoad',null);this.defineConfigOption('scrolls',false);this.defineConfigOption('noMoreTabsLeft',false);this.defineConfigOption('lastIndexLeft',0);this.defineConfigOption('noMoreTabsRight',true);this.defineConfigOption('lastIndexRight',null);this.defineConfigOption('showEffect',null);this.defineConfigOption('showEffectSpeed',null);this.defineConfigOption('mouseOverChangesTab',false);this.defineConfigOption('refreshOnTabChange',false);this.defineConfigOption('overflow',"hidden");this.defineConfigOption('shouldLoadOnInit',false);this.defineConfigOption('activeTabId',null);if(this.config.scroll==null)
{this.config.scrollMultiple=false;}
else if(this.config.scrollMultiple!=true&&this.config.scrollMultiple!=false)
{this.config.scrollMultiple=false;}
Zapatec.Tabs.SUPERclass.configure.call(this,objArgs);if(true!=this.noTabBar){this.config.tabBar=Zapatec.Widget.getElementById(this.config.tabBar);if(!this.config.tabBar){Zapatec.Log({description:"Can't find container for tab bar (\"tabBar\" config option)"});return;}}
if("string"==typeof this.config.tabs){this.config.tabs=Zapatec.Widget.getElementById(this.config.tabs);}
if(!this.config.tabs){Zapatec.Log({description:"Can't find container for tab contents (\"tabs\" config option)"});return;}
if(typeof this.config.tabType=="string"){this.config.tabType=this.config.tabType.toLowerCase();}
if(this.config.tabType!="div"&&objArgs.tabType!="iframe")
{this.config.tabType="div";}
if(true==this.config.iframeContent){this.config.tabType="iframe";}};Zapatec.Tabs.prototype.initTabBar=function(){if(true==this.noTabBar){return;}
Zapatec.Utils.addClass(this.config.tabBar,this.getClassName({prefix:'zpTabs'}));var _tabBarContentWidth=0;var items=this.config.tabBar.childNodes;var tmp='';for(var i=0;i<items.length;i++){if(items[i].nodeType!=1){continue;}
tmp=items[i].style.position;items[i].style.position='absolute';items[i].originalDisplayType=items[i].style.display!=''?items[i].style.display:'inline';_tabBarContentWidth+=items[i].offsetWidth;items[i].realWidth=items[i].offsetWidth;items[i].style.display='none';items[i].style.position=tmp;items[i].arrayPosition=i;}
for(var i=0;i<items.length;i++)
{if(items[i].nodeType!=1){continue;}
items[i].style.display=items[i].originalDisplayType;}
var _tabBarWidth=this.config.tabBar.offsetWidth;if(_tabBarContentWidth>_tabBarWidth)
{this.config.scrolls=true;var tmp=0;var i=0;while(tmp<_tabBarWidth)tmp+=items[i++].realWidth;i--;for(i;i<items.length;i++)
{items[i].style.display='none';}
var _leftScrolly=Zapatec.Utils.createElement('span');_leftScrolly.innerHTML='&lt;';var _rightScrolly=Zapatec.Utils.createElement('span');_rightScrolly.innerHTML='&gt;';var self=this;_rightScrolly.onclick=(this.config.scrollMultiple)?function(){self.scrollTabsLeft(true)}:function(){self.scrollOneTabLeft(true)};_leftScrolly.onclick=(this.config.scrollMultiple)?function(){self.scrollTabsRight(true)}:function(){self.scrollOneTabRight(true)};var mouseoverFunc=function()
{this.style.color='black';}
_leftScrolly.onmouseover=mouseoverFunc;_rightScrolly.onmouseover=mouseoverFunc;var mouseoutFunc=function()
{this.style.color='#aaa';}
_leftScrolly.onmouseout=mouseoutFunc;_rightScrolly.onmouseout=mouseoutFunc;var _scrollyContainer=Zapatec.Utils.createElement('div');_scrollyContainer.className='zpTabsScrolly';_scrollyContainer.appendChild(_leftScrolly);_scrollyContainer.appendChild(_rightScrolly);if(this.config.scrollMultiple)
{this.config.tabBar.parentNode.insertBefore(_scrollyContainer,this.config.tabBar.nextSibling);}}}
Zapatec.Tabs.prototype.addToTabBar=function(objTab){if(true==this.noTabBar){return;}
var configIndex=objTab.config.index;var insertBefore=null;if(-1!=configIndex){var actualIndex=objTab.index;var children=this.config.tabBar.childNodes;var childrenCount=children.length;if(0<childrenCount&&actualIndex<childrenCount){insertBefore=children[actualIndex];}}
if(!insertBefore){this.config.tabBar.appendChild(objTab.linkNode);}
else{this.config.tabBar.insertBefore(objTab.linkNode,insertBefore);}}
Zapatec.Tabs.prototype.attachTab=function(objTab){var index=objTab.config.index;if(this.tabsArray.length<=index){index=-1;}
if(-1==index){objTab.index=this.tabsArray.length;}
else{objTab.index=index;for(var i=this.tabsArray.length-1;index<=i;--i){var oldTab=this.tabsArray[i];oldTab.index=oldTab.index+1;this.tabsArray[i+1]=oldTab;}
if(index<=this.currentIndex){++this.currentIndex;}}
this.tabsArray[objTab.index]=objTab;this.tabs[objTab.id]=objTab;var linkHash=this.config.tabs.id+"="+objTab.id;objTab.setLinkHash(linkHash);var self=this;objTab.addEventListener("activateTab",function(){self.changeTab(objTab.id);});}
Zapatec.Tabs.prototype.scrollOneTabLeft=function(setTab){var tabBar=this.config.tabBar;var items=tabBar.childNodes;var i=0;for(i;i<items.length;i++)
{if(items[i].style.display!='none')
{break;}}
var j=i;for(j;j<items.length;j++)
{if(items[j].style.display=='none')
{break;}}
if(j>=items.length)
{return;}
items[i].style.display='none';if(j<items.length)
{items[j].style.display=items[j].originalDisplayType;}}
Zapatec.Tabs.prototype.scrollOneTabRight=function(setTab){var tabBar=this.config.tabBar;var items=tabBar.childNodes;var i=0;for(i;i<items.length;i++)
{if(items[i].style.display!='none')
{break;}}
if(i==0)
{return;}
var j=i;for(j;j<items.length;j++)
{if(items[j].style.display=='none')
{break;}}
items[j-1].style.display='none';items[i-1].style.display=items[i-1].originalDisplayType;}
Zapatec.Tabs.prototype.scrollTabsLeft=function(setTab){if(this.config.noMoreTabsLeft)
{return;}
this.config.noMoreTabsRight=false;if(!this.config.tabBar)
{return;}
var tabBar=this.config.tabBar;var contentWidth=parseInt(tabBar.style.width);var items=tabBar.childNodes;var i=this.config.lastIndexLeft-1;while(++i<items.length)
{if(items[i].style.display!='none')
{items[i].style.display='none';}
else
{this.config.lastIndexLeft=i;break;}}
var contentWidth=0;var tabBarWidth=parseInt(tabBar.style.width);for(i=this.config.lastIndexLeft;i<items.length;i++)
{items[i].style.display=items[i].originalDisplayType;contentWidth+=items[i].realWidth;if(contentWidth>tabBarWidth)
{items[i].style.display='none';this.config.lastIndexRight=i-1;if(setTab)
{this.changeTab(items[this.config.lastIndexLeft].name);}
return;}}
this.config.lastIndexRight=i-1;if(setTab)
{this.changeTab(items[this.config.lastIndexLeft].name);}
this.config.noMoreTabsLeft=true;}
Zapatec.Tabs.prototype.scrollTabsRight=function(setTab){if(this.config.noMoreTabsRight)
{return;}
this.config.noMoreTabsLeft=false;if(!this.config.tabBar)
{return;}
var tabBar=this.config.tabBar;var contentWidth=parseInt(tabBar.style.width);var items=tabBar.childNodes;var i=this.config.lastIndexRight+1;while(--i>=0)
{if(items[i].style.display!='none')
{items[i].style.display='none';}
else
{this.config.lastIndexRight=i;break;}}
var contentWidth=0;var tabBarWidth=parseInt(tabBar.style.width);for(i=this.config.lastIndexRight;i>=0;i--)
{items[i].style.display=items[i].originalDisplayType;contentWidth+=items[i].realWidth;if(contentWidth>tabBarWidth)
{items[i].style.display='none';this.config.lastIndexLeft=i+1;if(setTab)
{this.changeTab(items[this.config.lastIndexRight].name);}
return;}}
this.config.lastIndexLeft=i+1;if(setTab)
{this.changeTab(items[this.config.lastIndexRight].name);}
this.config.noMoreTabsRight=true;}
Zapatec.Tabs.prototype.loadDataJson=function(objSource){if((true!=this.noTabBar&&!this.config.tabBar)||!this.config.tabs){return;}
if(!objSource||!objSource.tabs||!objSource.tabs.length){return;}
var iLen=objSource.tabs.length;for(var iTab=0;iTab<iLen;iTab++){var objTabDef=objSource.tabs[iTab];this.addTab(objTabDef);}};Zapatec.Tabs.prototype.newTab=function(objArgs){var objTab=new Zapatec.Tab(objArgs);return objTab;}
Zapatec.Tabs.prototype.loadDataHtml=function(objSource){if((true!=this.noTabBar&&!this.config.tabBar)||!this.config.tabs){return;}
if(!objSource){objSource=this.config.tabs;}
var childs=[];for(var ii=0;ii<objSource.childNodes.length;ii++){childs.push(objSource.childNodes[ii]);}
for(var iChild=0;iChild<childs.length;iChild++){var objChild=childs[iChild];if(objChild.nodeType==1){var objLabel=Zapatec.Utils.getFirstChild(objChild,'label');if(!objLabel){continue;}
objLabel.parentNode.removeChild(objLabel);objChild.parentNode.removeChild(objChild);var objArgs={tabParent:this.config.tabs,id:objChild.getAttribute('id'),linkInnerHTML:objLabel.innerHTML,accessKey:objLabel.getAttribute('accesskey'),title:objLabel.getAttribute('title'),content:objChild,tabType:objChild.getAttribute('tabType'),url:objChild.getAttribute('url'),overflow:objChild.style.overflow,refreshOnTabChange:objChild.getAttribute('refreshOnTabChange')};var objTab=this.addTab(objArgs);if(objTab.id){objTab.container.getContainer().setAttribute('id',objTab.id);}}}};Zapatec.Tabs.prototype.addTab=function(objTabDef){if((true!=this.noTabBar&&!this.config.tabBar)||!this.config.tabs){return;}
if(!objTabDef){return;}
if(!objTabDef.tabType){objTabDef.tabType=this.config.tabType;}
objTabDef.tabParent=this.config.tabs;objTabDef.changeUrl=this.config.changeUrl;if(typeof objTabDef.mouseOverChangesTab=="undefined"){objTabDef.mouseOverChangesTab=this.config.mouseOverChangesTab;}
if(typeof objTabDef.refreshOnTabChange=="undefined"){objTabDef.refreshOnTabChange=this.config.refreshOnTabChange;}
if(typeof objTabDef.shouldLoadOnInit=="undefined"){objTabDef.shouldLoadOnInit=this.config.shouldLoadOnInit;}
if(typeof objTabDef.overflow=="undefined"||objTabDef.overflow==""){objTabDef.overflow=this.config.overflow;}
var objTab=this.newTab(objTabDef);if(objTab.id){objTabDef.id=objTab.id;}
var index=objTabDef.index;this.attachTab(objTab);this.addToTabBar(objTab);return objTab;};Zapatec.Tabs.prototype.removeTab=function(strTabId){var objTab=this.getTab(strTabId);if(!objTab){return;}
var isRemovingCurrent=objTab.index==this.currentIndex;var newCurrentIndex;if(objTab.index<=this.currentIndex){newCurrentIndex=this.currentIndex-1;if(newCurrentIndex<0){newCurrentIndex=0;}}
this.config.tabBar.removeChild(objTab.linkNode);this.tabs[strTabId]=null;var newTabsArray=[];var toIndex=0;for(var fromIndex=0;fromIndex<this.tabsArray.length;++fromIndex){var fromTab=this.tabsArray[fromIndex];if(objTab.index!=fromTab.index){fromTab.index=toIndex;newTabsArray[toIndex]=fromTab;++toIndex;}}
this.tabsArray=newTabsArray;var container=objTab.container.getContainer();objTab.container.getContainer().parentNode.removeChild(objTab.container.getContainer());if(isRemovingCurrent){this.currentIndex=-1;if(newCurrentIndex<this.tabsArray.length){this.changeTab(this.tabsArray[newCurrentIndex].id);}}
if(newCurrentIndex){this.currentIndex=newCurrentIndex;}}
Zapatec.Tabs.prototype.changeTab=function(strNewTabId){var strCurrTabId=null;var objTab=null;if(this.tabsArray[this.currentIndex]){strCurrTabId=this.tabsArray[this.currentIndex].id;objTab=this.tabsArray[this.currentIndex];}
if(strCurrTabId!=strNewTabId){var boolChangeTab=true;if(typeof this.config.onBeforeTabChange=='function'){boolChangeTab=this.config.onBeforeTabChange({oldTabId:strCurrTabId,newTabId:strNewTabId});}
if(!boolChangeTab){if(objTab&&objTab.linkNode.focus){objTab.linkNode.focus();setTimeout(function(){if(objTab.focusOn&&objTab.focusOn.focus){objTab.focusOn.focus();}},0);}
return;}
if(this.config.scrolls)
{var _newTab=this.tabsArray[this.tabs[strNewTabId].index].linkNode;if(this.tabsArray[this.currentIndex])
{var _curTab=this.tabsArray[this.currentIndex].linkNode;}
else
{var _curTab=this.tabsArray[0].linkNode;}
if(_curTab.arrayPosition<_newTab.arrayPosition)
{if(this.config.scrollMultiple)
{while(_newTab.style.display=='none')
{this.scrollTabsLeft(false);}}
else
{while(_newTab.style.display=='none')
{this.scrollOneTabLeft(false);}}}
else if(_curTab.arrayPosition>_newTab.arrayPosition)
{if(this.config.scrollMultiple)
{while(_newTab.style.display=='none')
{this.scrollTabsRight(false);}}
else
{while(_newTab.style.display=='none')
{this.scrollOneTabRight(false);}}}}
if(objTab){objTab.container.hide();Zapatec.Utils.removeClass(objTab.linkNode,'zpTabsActive');}
objTab=this.tabs[strNewTabId];if(!this.config.showEffect){objTab.container.show();}
else{Zapatec.Effects.init(objTab.container.getContainer(),true,this.config.showEffectSpeed,this.config.showEffect);}
if(this.config.refreshOnTabChange)
{var iframes=objTab.container.getContainer().getElementsByTagName('iframe');for(var i=0;i<iframes.length;i++)
{window.parent.frames[iframes[i].name].location.reload(true)}}
Zapatec.Utils.addClass(objTab.linkNode,'zpTabsActive');this.currentIndex=objTab.index;this.refreshTab(objTab,strCurrTabId,strNewTabId);}};Zapatec.Tabs.prototype.refreshTab=function(objTab,strCurrTabId,strNewTabId){var url=null;if(objTab.config.contentType=="html/url"){url=objTab.config.content;}
else{url=objTab.config.url;}
if(url){var isIframeDiffSrc=objTab.config.tabType=='iframe'&&objTab.lastUrlSet!=url;var isEmptyDiv=objTab.config.tabType=='div'&&!objTab.container.getContainer().childNodes.length;if(Zapatec.is_ie){var tabContents=objTab.container.getContainer().childNodes;if(objTab.config.tabType=='div'&&1==tabContents.length&&tabContents[0].id.match(/wch/gi)){isEmptyDiv=true;}}
if(isEmptyDiv||isIframeDiffSrc||objTab.config.refreshOnTabChange){if(typeof this.config.onTabChange=='function'){var self=this;objTab.pendingOnTabChange=function(){self.config.onTabChange({oldTabId:strCurrTabId,newTabId:strNewTabId});}}
objTab.setPaneContent(url,'html/url');}}
else{if(!url){objTab.setPaneContent();}
if(typeof this.config.onTabChange=='function'){this.config.onTabChange({oldTabId:strCurrTabId,newTabId:strNewTabId});}}}
Zapatec.Tabs.prototype.getTab=function(tabId){var objTab=this.tabs[tabId];return objTab;};Zapatec.Tabs.prototype.getTabByIndex=function(tabIndex){var objTab=this.tabsArray[tabIndex];return objTab;};Zapatec.Tabs.prototype.nextTab=function(){if(this.currentIndex<this.tabsArray.length-1){this.changeTab(this.tabsArray[this.currentIndex+1].id);}else{this.firstTab();}};Zapatec.Tabs.prototype.prevTab=function(){if(this.currentIndex>0){this.changeTab(this.tabsArray[this.currentIndex-1].id);}else{this.lastTab();}};Zapatec.Tabs.prototype.firstTab=function(){this.changeTab(this.tabsArray[0].id);};Zapatec.Tabs.prototype.lastTab=function(){this.changeTab(this.tabsArray[this.tabsArray.length-1].id);};Zapatec.Tabs.prototype.isFirstTab=function(){return this.currentIndex==0;};Zapatec.Tabs.prototype.isLastTab=function(){return this.currentIndex==this.tabsArray.length-1;};Zapatec.Tabs.prototype.getInitialActiveTabId=function(){var strId=null;if(this.config.activeTabId){strId=this.config.activeTabId;}
else{strId=this.tabsArray[0].id;}
if(!this.config.ignoreUrl){var str=this.config.tabs.id+"=";var url=document.URL;var pos=url.lastIndexOf(str);if(-1!=pos){pos+=str.length;str=url.substring(pos);var tabId=str.split("&")[0];if(this.tabs[tabId]){strId=tabId;}}}
return strId;};Zapatec.TabsWizard=function(objArgs){Zapatec.TabsWizard.SUPERconstructor.call(this,objArgs);};Zapatec.TabsWizard.id='Zapatec.TabsWizard';Zapatec.inherit(Zapatec.TabsWizard,Zapatec.Tabs);Zapatec.TabsWizard.prototype.init=function(objArgs){this.config.action='';this.config.submitTabId='';this.config.method='';this.config.contentType='';this.config.formThemePath='';this.config.formTheme='';this.config.formStatusImgPos='';this.config.showErrors='';this.config.onError=null;this.config.onValid=null;this.config.onSuccess=null;var objWizard=this;var funcOnInit=objArgs.onInit;objArgs.onInit=function(){var objHiddenForm=Zapatec.Utils.createElement('form');objHiddenForm.style.display='none';objHiddenForm.setAttribute('action',objWizard.config.action);objHiddenForm.setAttribute('method',objWizard.config.method);objHiddenForm.setAttribute('enctype',objWizard.config.contentType);objWizard.config.tabs.appendChild(objHiddenForm);new Zapatec.Form({form:objHiddenForm,theme:objWizard.config.formTheme,submitErrorFunc:function(objArgs){if(typeof objWizard.config.onError!='function'){return;}
if(objArgs.fieldErrors&&objArgs.fieldErrors.length){for(var iTab=0;iTab<objWizard.tabsArray.length;iTab++){var objTab=objWizard.tabsArray[iTab];if(!objTab.form){continue;}
var arrFieldErrors=[];var objFields=objTab.formSource.elements;for(var iField=0;iField<objFields.length;iField++){var objField=objFields[iField];if(!Zapatec.Form.Utils.isInputField(objField)){continue;}
var strName=objField.getAttribute('name');if(!strName.length){continue;}
for(var iFerr=0;iFerr<objArgs.fieldErrors.length;iFerr++){if(objArgs.fieldErrors[iFerr].field.name==strName){arrFieldErrors.push(objArgs.fieldErrors[iFerr]);break;}}}
if(arrFieldErrors.length){objWizard.changeTab(objTab.id);break;}}}else{objWizard.changeTab(objWizard.tabsArray[0].id);}
objArgs.tabId=objWizard.tabsArray[objWizard.currentIndex].id;objWizard.config.onError(objArgs);},asyncSubmitFunc:objWizard.config.onSuccess});var funcOnBeforeTabChange=objWizard.config.onBeforeTabChange;objWizard.config.onBeforeTabChange=function(objArgs){var iNewTabIndex=-1;for(var iTab=0;iTab<objWizard.tabsArray.length;iTab++){if(objWizard.tabsArray[iTab].id==objArgs.newTabId){iNewTabIndex=iTab;break;}}
if(iNewTabIndex>objWizard.currentIndex){var objOldTab=objWizard.tabs[objArgs.oldTabId];if(objOldTab&&objOldTab.form&&!objOldTab.form.submit()){return false;}}
if(objArgs.newTabId==objWizard.config.submitTabId){for(var iTab=0;iTab<objWizard.tabsArray.length;iTab++){var objTab=objWizard.tabsArray[iTab];if(objTab.id==objArgs.newTabId){continue;}
if(!objTab.parsed){objWizard.changeTab(objTab.id);return false;}
if(objTab.formSource){if(!objTab.form||!objTab.form.submit()){objWizard.changeTab(objTab.id);return false;}}}}
if(typeof funcOnBeforeTabChange=='function'){return funcOnBeforeTabChange(objArgs);}
return true;};var funcOnTabChange=objWizard.config.onTabChange;objWizard.config.onTabChange=function(objArgs){var strNewTabId=objArgs.newTabId;var objNewTab=objWizard.tabs[strNewTabId];if(!objNewTab){return;}
if(!objNewTab.parsed){var objSource=objNewTab.container.getContentElement();var objForm=Zapatec.Utils.getFirstChild(objSource,'form');if(objForm&&objForm.className.indexOf('zpForm')<0){while(objForm=Zapatec.Utils.getNextSibling(objSource,'form')){if(objForm.className.indexOf('zpForm')>=0){break;}}}
if(objForm){objNewTab.formSource=objForm;}
objNewTab.parsed=true;}
if(!objNewTab.form&&objNewTab.formSource){objNewTab.form=new Zapatec.Form({form:objNewTab.formSource,themePath:objWizard.config.formThemePath,theme:objWizard.config.formTheme,statusImgPos:objWizard.config.formStatusImgPos,showErrors:objWizard.config.showErrors,submitErrorFunc:function(objArgs){objArgs.tabId=strNewTabId;if(typeof objWizard.config.onError=='function'){objWizard.config.onError(objArgs);if(objArgs.fieldErrors&&objArgs.fieldErrors.length){objNewTab.focusOn=objArgs.fieldErrors[0].field;}}},submitValidFunc:function(){if(typeof objWizard.config.onValid=='function'){objWizard.config.onValid({tabId:strNewTabId});}}});}
if(strNewTabId==objWizard.config.submitTabId){for(var iTab=0;iTab<objWizard.tabsArray.length;iTab++){var objTab=objWizard.tabsArray[iTab];if(!objTab.form){continue;}
var objForm=objTab.formSource;if(!objForm){continue;}
for(var iField=0;iField<objForm.elements.length;iField++){var objField=objForm.elements[iField];if(!Zapatec.Form.Utils.isInputField(objField)){continue;}
var strName=objField.getAttribute('name');if(strName&&!strName.length){continue;}
var objHiddenField=objHiddenForm[strName];if(!objHiddenField){var objHiddenField=Zapatec.Utils.createElement('input');objHiddenField.setAttribute('type','hidden');objHiddenField.setAttribute('name',strName);objHiddenForm.appendChild(objHiddenField);}
objHiddenField.setAttribute('value',objField.value);}}
objHiddenForm.onsubmit();}
if(typeof funcOnTabChange=='function'){funcOnTabChange(objArgs);}};if(typeof funcOnInit=='function'){funcOnInit(objArgs);}};Zapatec.TabsWizard.SUPERclass.init.call(this,objArgs);};Zapatec.AccordionTab=function(objArgs){if(arguments.length==0){objArgs={};}
Zapatec.AccordionTab.SUPERconstructor.call(this,objArgs);};Zapatec.AccordionTab.id='Zapatec.AccordionTab';Zapatec.inherit(Zapatec.AccordionTab,Zapatec.Tab);Zapatec.AccordionTab.prototype.createTab=function()
{var tabParent=this.config.tabParent;this.tabContainer=document.createElement('div');tabParent.appendChild(this.tabContainer);this.config.tabParent=this.tabContainer;Zapatec.AccordionTab.SUPERclass.createTab.call(this);this.container.getContainer().style.display='block';this.container.getContainer().style.width='';this.tabContainer.tabId=this.id;this.linkNode.tabIndex=Zapatec.AccordionTab.tabIndex;if(!this.tab2tab)
{Zapatec.AccordionTab.tabIndex+=2;}
this.chooser=Zapatec.Utils.createElement('div');this.chooser.className='tabChooser';this.tabContainer.insertBefore(this.chooser,this.tabContainer.childNodes[0]);this.chooser.appendChild(this.linkNode);this.wch=Zapatec.Utils.createWCH(this.container.getContainer());if(this.wch){this.wch.style.zIndex=-1;}};Zapatec.AccordionTab.prototype.getFocusOn=function()
{this.focusOn=null;if(this.tab2tab)
{return;}
var self=this;setTimeout(function()
{var iTabIndex=0;function parse(objNode)
{var objChild=objNode.firstChild;while(objChild)
{if(objChild.nodeType==1)
{var strTag=objChild.tagName.toLowerCase();if(strTag=='a'||strTag=='input'||strTag=='select'||strTag=='textarea'||strTag=='button')
{if(!self.focusOn)
{self.focusOn=objChild;}
else if(objChild.tabIndex&&objChild.tabIndex>0&&(!iTabIndex||iTabIndex>objChild.tabIndex))
{self.focusOn=objChild;iTabIndex=objChild.tabIndex;}
if(!objChild.tabIndex)
{objChild.tabIndex=self.linkNode.tabIndex+1;}}
parse(objChild);}
objChild=objChild.nextSibling;}};parse(self.container.getContainer());},0);};Zapatec.AccordionTabs=function(objArgs){Zapatec.AccordionTabs.SUPERconstructor.call(this,objArgs);};Zapatec.AccordionTabs.id='Zapatec.AccordionTabs';Zapatec.inherit(Zapatec.AccordionTabs,Zapatec.Tabs);Zapatec.AccordionTabs.prototype.init=function(objArgs,i)
{var self=this;var funcOnInit=objArgs.onInit;objArgs.onInit=function()
{var _tabContainer=self.config.tabs;var items=_tabContainer.childNodes;for(var i=items.length-1;i>=0;i--)
{if(items[i].tagName=='div'||items[i].tagName=='DIV')
{self.config._tabArray.push(items[i]);}}
topPos=self.config._tabArray[self.config._tabArray.length-1].offsetTop;var items=_tabContainer.getElementsByTagName('div');for(var i=0;i<items.length;i++)
{if(items[i].className=='tabChooser')
{var localItem=items[i];items[i].onclick=function()
{self.changeTab(this.parentNode.tabId);if(this.blur)
{this.blur();}
return true;};}}
var _tabZIndex=100;for(var i=0;i<self.config._tabArray.length;i++)
{var tab=self.getTabByIndex(i);var tabContainer=tab.tabContainer;var contentContainer=tab.container.getContainer();contentContainer.style.height='1px';contentContainer.style.overflow="hidden";tabContainer.style.width=_tabContainer.style.width;tabContainer.style.zIndex=_tabZIndex--;tabContainer.arrayPosition=i;tabContainer.viewingPosition=topPos+((self.config._tabArray.length-1-i)*self.config.tabBarHeight);tabContainer.hiddenPosition=topPos+parseInt(_tabContainer.style.height)-((i+1)*self.config.tabBarHeight);tabContainer.viewingHeight=parseInt(_tabContainer.style.height)-((self.config._tabArray.length)*self.config.tabBarHeight);tabContainer.hiddenHeight=self.config.tabBarHeight;}
var activeTabId=self.getInitialActiveTabId();var activeTab=self.getTab(activeTabId);var tabContainer=activeTab.tabContainer;var contentContainer=activeTab.container.getContainer();contentContainer.style.height=tabContainer.viewingHeight+'px';var setOverflowFunc=function(){contentContainer.style.overflow=activeTab.config.overflow;};if(Zapatec.is_ie){setTimeout(setOverflowFunc,0);}
else{setOverflowFunc();}
self.currentIndex=activeTab.index;self.refreshTab(activeTab,null,activeTabId);var funcOnBeforeTabChange=self.config.onBeforeTabChange;self.config.onBeforeTabChange=function(objArgs)
{if(typeof funcOnBeforeTabChange=='function')
{return funcOnBeforeTabChange(objArgs);}
return true;};var funcOnTabChange=self.config.onTabChange;self.config.onTabChange=function(objArgs)
{var strNewTabId=objArgs.newTabId;var objNewTab=self.tabs[strNewTabId];if(!objNewTab)
{return;}
if(typeof funcOnTabChange=='function')
{funcOnTabChange(objArgs);}};if(typeof funcOnInit=='function')
{funcOnInit(objArgs);}};this.noTabBar=true;this.config.windowOnLoad=null;this.config._tabArray=new Array();this.config.IN_MOTION=false;this.config.tabBarHeight=24;this.config.topPos=null;this.config.indexOfWidget=i;this.tabsThemeSuffix='AccordionContent';Zapatec.AccordionTabs.SUPERclass.init.call(this,objArgs);};Zapatec.AccordionTabs.prototype.addTab=function(objTabDef){var objTab=Zapatec.AccordionTabs.SUPERclass.addTab.call(this,objTabDef);objTab.tabContainer.index=objTab.index;return objTab;}
Zapatec.AccordionTabs.prototype.newTab=function(objArgs){var objTab=new Zapatec.AccordionTab(objArgs);return objTab;}
Zapatec.AccordionTabs.prototype.changeTab=function(strNewTabId){var strCurrTabId=null;var objTab=null;if(this.tabsArray[this.currentIndex])
{strCurrTabId=this.tabsArray[this.currentIndex].id;objTab=this.tabsArray[this.currentIndex];}
if(strCurrTabId!=strNewTabId&&!this.config.IN_MOTION)
{if(objTab)
{Zapatec.Utils.removeClass(objTab.linkNode,'zpTabsActive');}
objTab=this.getTab(strNewTabId);var oOffset=Zapatec.Utils.getElementOffset(this.config.tabs);Zapatec.Utils.setupWCH(objTab.wch,0,0,oOffset.width,oOffset.height);Zapatec.Utils.addClass(objTab.linkNode,'zpTabsActive');this.currentIndex=objTab.index;this.slide(objTab.tabContainer.arrayPosition,5,10);this.refreshTab(objTab,strCurrTabId,strNewTabId);}};Zapatec.AccordionTabs.prototype.slide=function(index,pxInc,timeInc){if(false==this.config.IN_MOTION){var date=new Date();this.moveStartTime=date.getTime();this.lastTime=this.moveStartTime-timeInc;this.isDecreaseHeight=true;}
if(isNaN(index)||index<0||index>=this.config._tabArray.length)
{this.config.IN_MOTION=false;return;}
var date=new Date();var time=date.getTime();var diffTime=time-this.lastTime;var inc=Math.round((diffTime/timeInc)*pxInc);if(0==inc){var self=this;setTimeout(function(){self.slide(index,pxInc,timeInc);},timeInc);return;}
this.lastTime=time;var resizeTabs={};var isAdjust=false;for(var tries=0;tries<2;tries++){for(var i=0;i<this.config._tabArray.length;i++)
{var tab=this.getTabByIndex(i);var tabContainer=tab.tabContainer;var contentContainer=tab.container.getContainer();var newHeight=-1;var oldHeight=parseInt(contentContainer.style.height);var isCurrent=tab.index==this.currentIndex;if(!isCurrent&&this.isDecreaseHeight){newHeight=oldHeight-inc;if(newHeight<1){newHeight=1;}}
else if(isCurrent&&!this.isDecreaseHeight){newHeight=oldHeight+inc;if(tabContainer.viewingHeight<=newHeight){newHeight=tabContainer.viewingHeight;}}
if(-1!=newHeight&&oldHeight!=newHeight){if(this.isDecreaseHeight){resizeTabs.decreaseTab=contentContainer;resizeTabs.decreaseDif=oldHeight-newHeight;}
else{resizeTabs.increaseTab=contentContainer;resizeTabs.increaseDif=newHeight-oldHeight;}
isAdjust=true;break;}}
this.isDecreaseHeight=!this.isDecreaseHeight;}
var dif=Math.min(resizeTabs.decreaseDif,resizeTabs.increaseDif);var incTab=resizeTabs.increaseTab;var decTab=resizeTabs.decreaseTab;if(incTab){var incOldHeight=parseInt(incTab.style.height);incTab.style.height=(incOldHeight+dif)+'px';}
if(decTab){var decOldHeight=parseInt(decTab.style.height);decTab.style.height=(decOldHeight-dif)+'px';if(decTab.style.overflow!="hidden"){decTab.style.overflow="hidden";}}
if(!isAdjust){this.config.IN_MOTION=false;var currentTab=this.getTabByIndex(this.currentIndex);if(currentTab.config.overflow){currentTab.container.getContainer().style.overflow=currentTab.config.overflow;}
return;}
this.config.IN_MOTION=true;var self=this;setTimeout(function(){self.slide(index,pxInc,timeInc);},timeInc);}