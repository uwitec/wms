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
Zapatec.tooltipPath = Zapatec.getPath("Zapatec.TooltipWidget");

Zapatec.Tooltip=function(objArgs){if(arguments.length==0){objArgs={};}
Zapatec.Tooltip.SUPERconstructor.call(this,objArgs);};Zapatec.Tooltip.id='Zapatec.Tooltip';Zapatec.inherit(Zapatec.Tooltip,Zapatec.Widget);Zapatec.Tooltip.prototype.init=function(objArgs)
{Zapatec.Tooltip.SUPERclass.init.call(this,objArgs);this.createTooltip();};Zapatec.Tooltip.prototype.configure=function(objArgs){this.defineConfigOption('target',null);this.defineConfigOption('tooltip',null);this.defineConfigOption('parent',null);this.defineConfigOption('movable',false);this.defineConfigOption('content',null);this.defineConfigOption('offsetX',2);this.defineConfigOption('offsetY',20);Zapatec.Tooltip.SUPERclass.configure.call(this,objArgs);if(typeof this.config.target=="string"){this.config.target=Zapatec.Widget.getElementById(this.config.target);}
if(!this.config.target){Zapatec.Log({description:"Can't find tooltip target (\"target\" config option)"});return false;}
if(typeof this.config.tooltip=="string"){this.config.tooltip=Zapatec.Widget.getElementById(this.config.tooltip);}
if(!this.config.tooltip){if(this.config.content){this.config.tooltip=Zapatec.Utils.createElement("div");}
else{Zapatec.Log({description:"Can't find \"tooltip\" config option"});return false;}}
if(this.config.content){this.setContent(this.config.content);}
if(typeof this.config.parent=="string"){this.config.parent=Zapatec.Widget.getElementById(this.config.parent);}};Zapatec.Tooltip.prototype.createTooltip=function(){var self=this;this.visible=false;this.config.tooltip.style.position='absolute';this.config.tooltip.style.display='none';this.inTooltip=false;this.timeout=null;if(!this.config.parent){this.config.parent=this.config.target;}
this.config.parent.appendChild(this.config.tooltip);Zapatec.Utils.addClass(this.config.tooltip,this.getClassName({prefix:"zpTooltip",suffix:""}));if(this.config.tooltip.title){var title=Zapatec.Utils.createElement("div");this.config.tooltip.insertBefore(title,this.config.tooltip.firstChild);title.className=this.getClassName({prefix:"zpTooltip",suffix:"Title"});title.innerHTML=unescape(this.config.tooltip.title);this.config.tooltip.title="";}
this.wch=Zapatec.Utils.createWCH(this.config.tooltip);if(this.wch){this.wch.style.zIndex=-1;}
this.createProperty(this,'_targetMouseOverListener',function(ev){return self._onMouseMove(ev);});Zapatec.Utils.addEvent(this.config.target,"mouseover",this._targetMouseOverListener);if(this.config.movable){this.createProperty(this,'_targetMouseMoveListener',function(ev){return self._onMouseMove(ev);});Zapatec.Utils.addEvent(this.config.target,"mousemove",this._targetMouseMoveListener);}
this.createProperty(this,'_targetMouseOutListener',function(ev){return self._onMouseOut(ev);});Zapatec.Utils.addEvent(this.config.target,"mouseout",this._targetMouseOutListener);this.createProperty(this,'_tooltipMouseOverListener',function(ev){self.inTooltip=true;return true;});Zapatec.Utils.addEvent(this.config.tooltip,"mouseover",this._tooltipMouseOverListener);this.createProperty(this,'_tooltipMouseOutListener',function(ev){ev||(ev=window.event);if(!Zapatec.Utils.isRelated(self.config.tooltip,ev)){self.inTooltip=false;self.hide();}
return true;});Zapatec.Utils.addEvent(this.config.tooltip,"mouseout",this._tooltipMouseOutListener);}
Zapatec.Tooltip.prototype.destroy=function(){this.hide();Zapatec.Utils.removeEvent(this.config.target,"mouseover",this._targetMouseOverListener);Zapatec.Utils.removeEvent(this.config.target,"mousemove",this._targetMouseMoveListener);Zapatec.Utils.removeEvent(this.config.target,"mouseout",this._targetMouseOutListener);Zapatec.Utils.removeEvent(this.config.tooltip,"mouseover",this._tooltipMouseOverListener);Zapatec.Utils.removeEvent(this.config.tooltip,"mouseout",this._tooltipMouseOutListener);if(this.wch){this.wch.parentNode.removeChild(this.wch);}}
Zapatec.Tooltip.setupFromDFN=function(class_re){var dfns=document.getElementsByTagName("dfn");if(typeof class_re=="string")
class_re=new RegExp("(^|\\s)"+class_re+"(\\s|$)","i");for(var i=0;i<dfns.length;++i){var dfn=dfns[i];if(!class_re||class_re.test(dfn.className)){var div=document.createElement("div");if(dfn.title){div.title=dfn.title;dfn.title="";}
while(dfn.firstChild)
div.appendChild(dfn.firstChild);dfn.innerHTML="?";var oTooltip=new Zapatec.Tooltip({target:dfn,parent:document.body,tooltip:div});dfn.className=oTooltip.getClassName({prefix:"zpTooltip",suffix:"Dfn"});}}};Zapatec.Tooltip._currentTooltip=null;Zapatec.Tooltip.prototype._onMouseMove=function(ev){ev||(ev=window.event);if(this.timeout){clearTimeout(this.timeout);this.timeout=null;}
if((!this.visible||this.config.movable)&&!Zapatec.Utils.isRelated(this.config.target,ev)){var oPos=Zapatec.Utils.getMousePos(ev);this.show(oPos.pageX+this.config.offsetX,oPos.pageY+this.config.offsetY);}
return true;};Zapatec.Tooltip.prototype._onMouseOut=function(ev){ev||(ev=window.event);var self=this;if(!Zapatec.Utils.isRelated(this.config.target,ev)){if(this.timeout){clearTimeout(this.timeout);this.timeout=null;}
this.timeout=setTimeout(function(){self.hide();},150);}
return true;};Zapatec.Tooltip.prototype.show=function(x,y){if(Zapatec.Tooltip._currentTooltip){if(Zapatec.Tooltip._currentTooltip.timeout){clearTimeout(Zapatec.Tooltip._currentTooltip.timeout);Zapatec.Tooltip._currentTooltip.timeout=null;}
Zapatec.Tooltip._currentTooltip.hide();}
this.config.tooltip.style.display='block';if(null==x&&null==y){var targetOffset=Zapatec.Utils.getElementOffset(this.config.target);x=targetOffset.left;y=targetOffset.top;}
this.config.tooltip.style.left=x+'px';this.config.tooltip.style.top=y+'px';var oOffset=Zapatec.Utils.getElementOffset(this.config.tooltip);var iDiffLeft=x-oOffset.left;if(iDiffLeft){x+=iDiffLeft;this.config.tooltip.style.left=x+'px';}
var iDiffTop=y-oOffset.top;if(iDiffTop){y+=iDiffTop;this.config.tooltip.style.top=y+'px';}
oOffset=Zapatec.Utils.getElementOffset(this.config.tooltip);var iRight=oOffset.left+oOffset.width;var iBottom=oOffset.top+oOffset.height;var oWindowSize=Zapatec.Utils.getWindowSize();var iWinW=Zapatec.Utils.getPageScrollX()+oWindowSize.width;var iWinH=Zapatec.Utils.getPageScrollY()+oWindowSize.height;if(iRight>iWinW){x+=iWinW-iRight;this.config.tooltip.style.left=x+'px';}
if(iBottom>iWinH){y+=iWinH-iBottom;this.config.tooltip.style.top=y+'px';}
Zapatec.Utils.setupWCH(this.wch,0,0,oOffset.width,oOffset.height);Zapatec.Utils.addClass(this.config.target,this.getClassName({prefix:"zpTooltip",suffix:"Hover"}));this.visible=true;Zapatec.Tooltip._currentTooltip=this;};Zapatec.Tooltip.prototype.hide=function(){if(!this.inTooltip){this.config.tooltip.style.display="none";Zapatec.Utils.hideWCH(this.wch);Zapatec.Utils.removeClass(this.config.target,this.getClassName({prefix:"zpTooltip",suffix:"Hover"}));this.visible=false;}};Zapatec.Tooltip.prototype.setContent=function(html){this.config.tooltip.innerHTML=html;}