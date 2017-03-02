/**
 * @fileoverview EditInline class derived from Widget. See description of
 * base Widget class at http://trac.zapatec.com:8000/trac/wiki/Widget.
 *
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/* $Id: edit_inline.js 6617 2007-03-15 09:53:11Z andrew $ */

/**
 * EditInline class.
 *
 * @constructor
 * @extends Zapatec.Widget
 * @param {object} objArgs User configuration
 */
Zapatec.EditInline = function(objArgs) {
  // Call constructor of superclass
  Zapatec.EditInline.SUPERconstructor.call(this, objArgs);
};

/**
 * Unique static id of the widget class. Gives ability for Zapatec#inherit to
 * determine and store path to this file correctly when it is included using
 * Zapatec#include. When this file is included using Zapatec#include or path
 * to this file is gotten using Zapatec#getPath, this value must be specified
 * as script id.
 * @private
 */
Zapatec.EditInline.id = 'Zapatec.EditInline';

// Inherit Widget
Zapatec.inherit(Zapatec.EditInline, Zapatec.Widget);

/**
 * Initializes object.
 *
 * @param {object} objArgs User configuration
 */
Zapatec.EditInline.prototype.init = function(objArgs) {
	// Call init method of superclass
	Zapatec.EditInline.SUPERclass.init.call(this, objArgs);

	this.isActive = false;
	this.isClicked = false;

	var self = this;
	
	this.container = document.createElement("textarea");
	this.createProperty(this.container, "zpEditInline", this);
	this.container.style.position = "absolute";
	this.container.style.border = "1px solid black";
	this.container.onclick = function(){
		self.isClicked = true;
	}
	
	// process all document key hits
	// For IE we must use "keydown" instead of "keypress"
	Zapatec.Utils.addEvent(document, "keydown", 
		function(evt){
			self.keyEvent(evt);
	});
		
	if(this.config.hideOnClick){
		// process all document clicks
		Zapatec.Utils.addEvent(document, 'click', function(evt){
			setTimeout(function(evt){self.documentClick(evt)}, 1);
		});
	}
	
	// resize textarea on clipboard events
	var changeEvent = null;

	if(Zapatec.is_ie){
		changeEvent = 'paste';
	} else if(Zapatec.is_gecko) {
		changeEvent = 'input';
	} else {
		changeEvent = 'change';
	}
	
	Zapatec.Utils.addEvent(this.container, changeEvent, 
		function(){self.resizeToContent()});

	if(this.config.showImmediately && this.config.target){
		this.show(this.config.target);
	}
};

Zapatec.EditInline.prototype.documentClick = function(evt){
	if(!this.isActive){
		return;
	}

	if(!this.isClicked){
		if(this.config.saveOnClick){
			this.saveAndHide();
		} else {
			this.hide();
		}
	}
	
	this.isClicked = false;
}

Zapatec.EditInline.prototype.keyEvent = function(evt){
	// do nothing is widget is not active
	if(!this.isActive){
		return;
	}
	
	if(!evt){
		evt = window.event;
	}
	
	var res = Zapatec.Utils.getCharFromEvent(evt);

	if(!res){
		return;
	}
	if(this.config.hideOnEsc){
		if(res.charCode == 27){ // ESC key
			this.hide();
			return;
		}
	}

	// save content and close on ctrl+enter
	if(
		(
			evt.ctrlKey || 
			(typeof(evt.metaKey) != 'undefined' && evt.metaKey)
		) && 
		res.charCode == 13
	){
		this.saveAndHide();
		return;
	}
	
	// resize textarea according to content
	this.resizeToContent();
}

/**
 * Configures the widget. Gets called from init and reconfigure methods of
 * superclass.
 *
 * @private
 * @param {object} objArgs User configuration
 */
Zapatec.EditInline.prototype.configure = function(objArgs) {
	this.defineConfigOption("theme", ""); // this widget does not uses themes.
	this.defineConfigOption("target", null); // target element.
	this.defineConfigOption("showImmediately", true); // show textarea when calling constructor using "target" config option
	this.defineConfigOption("hideOnEsc", true); // hide when ESC button is pressed.
	this.defineConfigOption("hideOnClick", true); // hide when user clicks outside element.
	this.defineConfigOption("editAsText", false); // convert HTML entities into plain symbols on editing
	this.defineConfigOption("loadCallback"); // this function will be called when loading content from element. Function must accept one argument and return transformed content.
	this.defineConfigOption("saveCallback"); // this function will be called when saving content to element. Function must accept one argument and return transformed content.
	this.defineConfigOption("saveOnClick", true); // save content if user clicks outside textarea. This function is used only if hideOnClick is true.
	this.defineConfigOption("hideTarget", true); // hide target element when showing textarea.
	
	// Call parent method
	Zapatec.EditInline.SUPERclass.configure.call(this, objArgs);
};

/**
 * Use this function to show EditInline under element. 
 * @param {Object} target Reference to target element to edit. 
 * @param {String} newContent Default textarea content.
 */
Zapatec.EditInline.prototype.show = function(target){
	this.fireEvent("showStart");

	if(target){
		this.hide();
		
		this.config.target = target;
		this.setContent("");
	}
	
	this.isActive = true;
	this.isClicked = true;
	this.container.value = "";
	
	this.setContent(this.config.target.innerHTML);

	if(this.config.hideTarget){
		this.config.target.zpOrigVisibility = this.config.target.style.visibility;
		this.config.target.style.visibility = "hidden"; 
	}
	document.body.appendChild(this.container);

	var parentCoords = Zapatec.Utils.getElementOffset(this.config.target);
	this.container.style.left = parentCoords.left + "px";
	this.container.style.top = parentCoords.top + "px";

	this.resizeToContent();
	this.container.focus();

	this.fireEvent("showFinish");
}

/**
 * Function to set textarea content
 * @param {String} newContent
 */
Zapatec.EditInline.prototype.setContent = function(newContent){
	if(typeof(newContent) != "string"){
		return;
	}
	
	if(this.config.loadCallback){
		newContent = this.config.loadCallback(newContent);
	}

	if(this.config.editAsText){
		newContent = newContent.replace(/\<br(\s*\/)?\>/ig, "\n").replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&amp;/g, "&");
	}

	this.container.value = newContent;

	this.fireEvent("setContent", newContent);
}

/**
 * Function to copy value from textarea back to element. innerHTML property 
 * will be redefined.
 * @return Edited content
 * @type String
 */
Zapatec.EditInline.prototype.saveContent = function(){
	var val = this.container.value;
	
	
	if(this.config.saveCallback){
		val = this.config.saveCallback(val);
	}

	if(this.config.editAsText){
		val = val.replace(/&/g, "&amp;").replace(/\</g, "&lt;").replace(/\>/g, "&gt;").replace(/\<br(\s*\/)?\>\n/ig, "\n").replace(/\n/g, "<br />");
	}
	
	this.config.target.innerHTML = val;
	
	this.fireEvent("saveContent", val);
	return this.container.value;
}

/**
 * Hide EditInline element
 */
Zapatec.EditInline.prototype.hide = function(){
	if(!this.isActive){
		return;
	}
	
	this.fireEvent("hideStart");
	
	this.isActive = false;
	
	this.container.parentNode.removeChild(this.container);
	
	if(this.config.hideTarget){
		if(Zapatec.is_opera && !this.config.target.zpOrigVisibility){
			this.config.target.zpOrigVisibility = "";
		}
		
		this.config.target.style.visibility = this.config.target.zpOrigVisibility; 
		this.config.target.zpOrigVisibility = null;
	}

	this.config.target = null;
	
	this.fireEvent("hideFinish");
}

/**
 * Copy textarea content back to element (innerHTML property will be used) and 
 * hide widget.
 */
Zapatec.EditInline.prototype.saveAndHide = function(){
	this.saveContent();
	this.hide();
	
	this.fireEvent("saveAndHide");
}

/**
 * @private
 * Resize textarea size according to content.
 */
Zapatec.EditInline.prototype.resizeToContent = function(){
	var val = this.container.value;
	var tmp = val.split('\n');

	var maxLength = tmp[0].length;
	
	for(var ii = 1; ii < tmp.length; ii++){
		if(tmp[ii].length > maxLength){
			maxLength = tmp[ii].length; 
		}
	}
	this.container.cols = maxLength + (Zapatec.is_ie || Zapatec.is_khtml ? 1 : 0);
	this.container.rows = tmp.length + (Zapatec.is_ie || Zapatec.is_khtml || Zapatec.is_opera ? 1 : 0);
}