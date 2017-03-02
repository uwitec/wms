// $Id: indicator.js 6596 2007-03-13 09:09:25Z slip $
/*
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/**
 * Zapatec modal object.
 * It represents the element that imitates
 * modal behaviour.
 *
 * The constructor recognizes the following options:
 * \code
 *    prop. name     | description
 *  -------------------------------------------------------------------------------------------------
 *   zIndex          | {number} z-index for the element. Default 1000.
 *   x               | {number} X coordinate of the element. Default null.
 *   y               | {number} Y coordinate of the element. Default null.
 *   width           | {number} width of the element. Default null.
 *   height          | {number} height of the element. Default null.
 *   container       | {HTML element or Default View} container for the element. Default window.
 *   scroll          | {boolean} if the element is fixed on screen. Default false.
 *   
 * \endcode
 */
Zapatec.Modal = function (config) {
	if (arguments.length == 0) {
		config = {};
	}
	//visibility is false on start
	this.visible = false;
	//calling super constructor
	Zapatec.Modal.SUPERconstructor.call(this, config);
}

//assigning ID
Zapatec.Modal.id = "Zapatec.Indicator";

// Inherit SuperClass
Zapatec.inherit(Zapatec.Modal, Zapatec.Widget);

/**
 * Inits the object with config options.
 * @param config {object} object with configuration.
 */
Zapatec.Modal.prototype.init = function(config){
	// processing Widget functionality
	Zapatec.Modal.SUPERclass.init.call(this, config);
};

/**
 * Configurates the object.
 * @param config {object} object with configuration.
 */
Zapatec.Modal.prototype.configure = function(config) {
	//z-index
	this.defineConfigOption("zIndex", 1000);
	//x coordinate
	this.defineConfigOption("x", null);
	//y coordinate
	this.defineConfigOption("y", null);
	//width
	this.defineConfigOption("width", null);
	//height
	this.defineConfigOption("height", null);
	//container of the modal cover
	//IMPORTANT NOTE: if container is not window
	//x, y, width and height params are ignored
	//and modal tries to stick to it.
	this.defineConfigOption("container", window);
	//if modal is fixed on the screen
	this.defineConfigOption("fixed", false);
	// processing Widget functionality
	Zapatec.Modal.SUPERclass.configure.call(this, config);
	//getting configuration
	config = this.getConfiguration();
	//checking container
	if (config.container != window) {
		config.x = null;
		config.y = null;
		config.width = null;
		config.height = null;
		config.fixed = false;
	}
};

/**
 * Creates the actual element and assigns
 * classes and styles to it.
 */
Zapatec.Modal.prototype.create = function () {
	//trying to get config.container
	var config = this.getConfiguration();
	config.container = Zapatec.Widget.getElementById(config.container) || window;
	//creating WCH
	this.WCH = Zapatec.Utils.createWCH();

	//creating container
	this.container = Zapatec.Utils.createElement("div", document.body);
	//applying theme
	this.container.className = this.getClassName({prefix: "zpModal" + (Zapatec.is_opera ? "Opera" : "")})
	//making it absolutely positioned
	var st = this.container.style;
	st.dispaly = "none";
	st.position = "absolute";
	st.zIndex = config.zIndex;
};

/**
 * Shows the modal cover, creates it
 * if it still wasn't.
 * @param zIndex {number} z-ndex for the cover.
 */
Zapatec.Modal.prototype.show = function (zIndex) {
	//if modal element wasn't created let's do it
	if (!this.container) {
		this.create();
	}
	//setting new z-index
	zIndex = zIndex || this.config.zIndex;
	this.container.style.zIndex = zIndex;
	if (this.WCH) {
		this.WCH.style.visibility = "";
		this.WCH.style.zIndex = zIndex - 1;
	}
	//making it visible
	this.container.style.display = "block";
	//pointing that modal cover is visible
	this.visible = true;
	//getting configuration
	var config = this.getConfiguration();
	//if container is not window then we need start sticking
	if (config.container != window) {
		var self = this;
		//we try to update the size and position of
		//modal cover to cover container
		var update = function() {
			self.update();
		}
		if (!this.interval) {
			this.interval = setInterval(update, 100);
		}
		this.update();
	} else {
		//getting sizes
		var dim = Zapatec.Utils.getWindowSize();
		var width = config.width || dim.width;
		var height = config.height || dim.height;
		//getting position
		var x = config.x || Zapatec.Utils.getPageScrollX();
		var y = config.y || Zapatec.Utils.getPageScrollY();
		//setting sizes
		this.setWidth(width);
		this.setHeight(height);
		//setting position
		this.setPosition(x, y);
	}
	//fixating element if needed
	if(this.config.fixed == true){
		Zapatec.FixateOnScreen.register(this.container);
		if (this.WCH){
			Zapatec.FixateOnScreen.register(this.WCH);
		}
	}
};

/**
 * Updates the size and position of
 * modal cove due to container option.
 */
Zapatec.Modal.prototype.update = function() {
	var config = this.getConfiguration();
	if (config.container != window && this.visible) {
		var offs = Zapatec.Utils.getElementOffset(config.container);
		this.setWidth(offs.width);
		this.setHeight(offs.height);
		this.setPosition(offs.x, offs.y);
	}
};

/**
 * Hides the modal cover.
 * @param destroy {boolean} if true the cover will be destroyed.
 */
Zapatec.Modal.prototype.hide = function (destroy) {
	//getting configuration
	var config = this.getConfiguration();
	//unfixating element if needed
	if(config.fixed == true){
		Zapatec.FixateOnScreen.unregister(this.container);
		if (this.WCH){
			Zapatec.FixateOnScreen.unregister(this.WCH);
		}
	}
	//clearing interval
	if (config.container != window) {
		clearInterval(this.interval);
		this.interval = null;
	}
	//hiding elements
	if (this.container) this.container.style.display = "none";
	Zapatec.Utils.hideWCH(this.WCH);
	//destroying if needed
	if (destroy) {
		if (this.WCH){
			if (this.WCH.outerHTML) {
				this.WCH.outerHTML = "";
			} else {
				Zapatec.Utils.destroy(this.WCH);
			}
		}
		if (this.container.outerHTML) {
			this.container.outerHTML = "";
		} else {
			Zapatec.Utils.destroy(this.container);
		}
		this.WCH = null;
		this.container = null;
	}
	//pointing that modal cover is visible
	this.visible = false;
};

/**
 * Sets the width of the modal cover.
 * @param width {number} new width.
 */
Zapatec.Modal.prototype.setWidth = function(width) {
	if (!this.container) {
		return;
	}
	//if dom.js is included lets use its possibilities
	if (Zapatec.Utils.setWidth) {
		Zapatec.Utils.setWidth(this.container, width);
		Zapatec.Utils.setWidth(this.WCH, width);
	} else {
		//otherwise let's try to do it manually
		this.container.style.width = width + "px";
		if (this.WCH) {
			this.WCH.style.width = width + "px";
		}
	}
};

/**
 * Sets the height of the modal cover.
 * @param height {number} new height.
 */
Zapatec.Modal.prototype.setHeight = function(height) {
	if (!this.container) {
		return;
	}
	//if dom.js is included lets use its possibilities
	if (Zapatec.Utils.setHeight) {
		Zapatec.Utils.setHeight(this.container, height);
		Zapatec.Utils.setHeight(this.WCH, height);
	} else {
		//otherwise let's try to do it manually
		this.container.style.height = height + "px";
		if (this.WCH) {
			this.WCH.style.height = height + "px";
		}
	}
};

/**
 * Sets the position of the modal cover.
 * @param x {number} x coordinate of the cover.
 * @param y {number} y coordinate of the cover.
 */
Zapatec.Modal.prototype.setPosition = function(x, y) {
	if (!this.container) {
		return;
	}
	//if dom.js is included lets use its possibilities
	if (Zapatec.Utils.moveTo) {
		Zapatec.Utils.moveTo(this.container, x, y);
		Zapatec.Utils.moveTo(this.WCH, x, y);
	} else {
		//otherwise let's try to do it manually
		this.container.style.left = x + "px";
		this.container.style.top = y + "px";
		if (this.WCH) {
			this.WCH.style.left = x + "px";
			this.WCH.style.top = y + "px";
		}
	}
};

/**
 * Zapatec indicator object
 * The constructor recognizes same options 
 * as Zapatec.Modal constructor.
 */
Zapatec.Indicator = function (config) {
	if(arguments.length == 0){
		config = {};
	}

	//it is not active at start
	this.active = false;
	// processing Modal functionality
	Zapatec.Indicator.SUPERconstructor.call(this, config);
}

//assiging ID
Zapatec.Indicator.id = "Zapatec.Indicator";

// Inherit SuperClass
Zapatec.inherit(Zapatec.Indicator, Zapatec.Modal);

/**
 * Inits the object.
 * @param config {object} object with configuration.
 */
Zapatec.Indicator.prototype.init = function(config){
	// processing Modal functionality
	Zapatec.Indicator.SUPERclass.init.call(this, config);
};

/**
 * Configurates the object.
 * @param config {object} object with configuration.
 */
Zapatec.Indicator.prototype.configure = function(config) {
	//defining theme path
	this.defineConfigOption("themePath", Zapatec.zapatecPath + "../zpextra/themes/indicator/");
	// processing Modal functionality
	Zapatec.Indicator.SUPERclass.configure.call(this, config);
};

/**
 * Overwriting parent method.
 */
Zapatec.Indicator.prototype.create = function() {
	//calling parent method
	Zapatec.Indicator.SUPERclass.create.call(this);
	//creating indicator
	this.indicator = Zapatec.Utils.createElement("div", this.container);
	this.indicator.className = "zpIndicator";
	//styling it
	var st = this.indicator.style;
	st.position = "absolute";
	st.zIndex = this.getConfiguration().zIndex;
	st.backgroundColor = "#aaaaaa";
};

/**
 * Overwriting parent method.
 * @param width {number} width to set.
 */
Zapatec.Indicator.prototype.setWidth = function(width) {
	if (!this.container) {
		return;
	}
	//calling parent method
	Zapatec.Indicator.SUPERclass.setWidth.call(this, width);
	//updating indicator position.
	var left = Math.round((this.container.offsetWidth - this.indicator.offsetWidth) / 2);
	this.indicator.style.left = left + "px";
}; 

/**
 * Overwriting parent method.
 * @param height {number} height to set.
 */
Zapatec.Indicator.prototype.setHeight = function(height) {
	if (!this.container) {
		return;
	}
	//calling parent method
	Zapatec.Indicator.SUPERclass.setHeight.call(this, height);
	//updating indicator position.
	var top = Math.round((this.container.offsetHeight - this.indicator.offsetHeight) / 2);
	this.indicator.style.top = top + "px";
}; 

/**
 * Overwriting parent method.
 * @param destroy {boolean} if we destroy the indicator.
 */
Zapatec.Indicator.prototype.hide = function(destroy) {
	if (destroy) {
		this.indicator = null;
	}
	//calling parent method
	Zapatec.Indicator.SUPERclass.hide.call(this, destroy);
};

/**
 * Starts the indicator, filling it with some
 * message and showing. Also puts it to active state.
 * @param message {}
 */
Zapatec.Indicator.prototype.start = function (message) {
	//pointing that it is active
	this.active = true;
	if (!this.indicator) {
		this.create();
	}
	//fillig with message
	this.indicator.innerHTML = message;
	//showing it
	this.show();
};

/**
 * Stops the indicator.
 */
Zapatec.Indicator.prototype.stop = function () {
	//pointing that it is inactive
	this.active = false;
	//destroying it
	this.hide(true);
};

/**
 * Checks if indicator is active.
 * @return {boolean} true if it is active, otherwise false.
 */
Zapatec.Indicator.prototype.isActive = function () {
	return this.active;
};