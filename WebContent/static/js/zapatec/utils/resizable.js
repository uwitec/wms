//$Id: resizable.js 6679 2007-03-20 16:39:44Z slip $
/**
 * This is a set of functionality used for resizing object
 * and is implemented in interface (mixin) manner.
 */
Zapatec.Resizable = {};

/**
 * Makes the element resizable. This means attaching events, 
 * and making some preparations.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Resizable.makeResizable = function() {
	if (!this.hasInterface("Zapatec.Sizable")) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' has no Zapatec.Sizable interface so can not be resizable!"});
		return false;
	}
	if (!this.hasInterface("Zapatec.CommandEvent")) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' has no Zapatec.CommandEvent interface!"});
		return false;
	}
	var hooks = Zapatec.Array(this._getResizableHooks());
	var measurement = this.getSizableMeasurement();
	var self = this, result = false;
	var config = this.getResizeConfig();
	//if direction includes top and left resizing we need Zapatec.Movable interface
	if (/(vertical|horizontal|top|left|all)/.test(config.direction) && !this.hasInterface("Zapatec.Movable")) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' has no Zapatec.Movable interface so can not be resizable!"});
		return false;
	}
	//we need to save references to anonymous event listenrs
	var listenersObj = this._getRestorer().getSavedProps()["resizeListeners"] = {
		mousedown : function(ev) {
			return self.resizeStart(ev);
		},
		hover : function(ev) {
			if (!self.isResizing() && self.canResize()) {
				self._toggleResizeCursor(ev);
			}
		},
		mouseover : function(ev) {
			if (self.hasInterface("Zapatec.Draggable")) {
				self._setCanDrag(false);
			}
		},
		mouseout : function(ev) {
			if (self.hasInterface("Zapatec.Draggable")) {
				self._setCanDrag(true);
			}
		}
	};
	hooks.each(function(index, hook) {
		if (!Zapatec.isHtmlElement(hook)) {
			return;
		}
		//if we are here than there is at least one hook :)
		result = true;
		//this for disabling text selection on resize
		if (Zapatec.is_gecko) {
			hook.style.setProperty("-moz-user-select", "none", "");
		}
		//adding resize start event
		Zapatec.Utils.addEvent(hook, 'mousedown', listenersObj.mousedown);
		Zapatec.Utils.addEvent(hook, 'mouseover', listenersObj.mouseover);
		Zapatec.Utils.addEvent(hook, 'mouseout', listenersObj.mouseout);
	});
	//if there are no hooks, we use measurement border as trigger
	if (!result) {
		//if it is not an HTML element then we have no trigger
		if (!Zapatec.isHtmlElement(measurement)) {
			return false;
		}
		//result is true now
		result = true;
		//adding resize events
		Zapatec.Utils.addEvent(measurement, 'mousedown', listenersObj.mousedown);
		Zapatec.Utils.addEvent(measurement, 'mousemove', listenersObj.hover);
	}
	//returning result
	return result;	
};

/**
 * This method is used for determining cursor 
 * for current mouse position if the resizing 
 * trigger is elements border.
 * @param ev {object} event object.
 */
Zapatec.Resizable._toggleResizeCursor = function(ev) {
	ev = ev || window.event;
	var el = this.getSizableMeasurement();
	//lets get the position of the mouse
	var config = this.getResizeConfig();
	//mouse position
	var posX = ev.clientX + Zapatec.Utils.getPageScrollX() || 0;
	var posY = ev.clientY + Zapatec.Utils.getPageScrollY() || 0;
	//element boundaries
	var pos = Zapatec.Utils.getElementOffset(el);
	var left = pos.x, right = pos.x + Zapatec.Utils.getWidth(el);
	var top = pos.y, bottom = pos.y + Zapatec.Utils.getHeight(el);
	//resizing border thickness
	var borderThickness = config.borderThickness;
	var cornerThickness = config.cornerThickness;
	//checks if point is in rectangle
	function inRect(x, y, left, top, right, bottom) {
		if ((x > left) && (x < right) && (y > top) && (y < bottom)) {
			return true;
		}
		return false;
	}
	//checks if value is in range
	function inRange(value, minVal, maxVal) {
		if ((value > minVal) && (value < maxVal)) {
			return true;
		}
		return false;
	}
	//sets cursor and disables/enables dragging
	var self = this;
	function toggleResizing(cursor) {
		el.style.cursor = cursor;
		if (self.hasInterface("Zapatec.Draggable")) {
			self._setCanDrag((cursor === "") ? true : false);
		}
	}
	//getting the needed direction cursor and toggling drag
	switch (true) {
		case (inRect(posX, posY, left, top, left + borderThickness, top + borderThickness)) : {
			toggleResizing("nw-resize");
			break;
		}
		
		case (inRect(posX, posY, right - cornerThickness, bottom - cornerThickness, right, bottom)) : {
			toggleResizing("se-resize");
			break;
		}
	
		case (inRect(posX, posY, right - cornerThickness, top, right, top + borderThickness)) : {
			toggleResizing("ne-resize");
			break;
		}
		
		case (inRect(posX, posY, left, bottom - cornerThickness, left + cornerThickness, bottom)) : {
			toggleResizing("sw-resize");
			break;
		}
	
		case (inRange(posX, left, left + borderThickness)) : {
			toggleResizing("w-resize");
			break;
		}
		
		case (inRange(posX, right - borderThickness, right)) : {
			toggleResizing("e-resize");
			break;
		}
	
		case (inRange(posY, top, top + borderThickness)) : {
			toggleResizing("n-resize");
			break;
		}
		
		case (inRange(posY, bottom - borderThickness, bottom)) : {
			toggleResizing("s-resize");
			break;
		}
	
		default : {
			toggleResizing("");
			break;
		}
	}
	//correcting direction
	el.style.cursor = this._correctDirection(el.style.cursor);
	if (el.style.cursor == "") {
		if (this.hasInterface("Zapatec.Draggable")) {
			this._setCanDrag(true);
		}
	}
};

/**
 * Corrects the cursor value to fit the
 * direction config option.
 * @param direction {string} cursor value, 
 * meaning the direction of resizing.
 * @return {string} new cursor, meaning the
 * enabled direction.
 */
Zapatec.Resizable._correctDirection = function(direction) {
	//getting config
	var config = this.getResizeConfig();
	//taking direction part
	direction = direction.replace("-resize", "");
	//excluding impossible action
	for (var i = 0; i < direction.length; ++i) {
		switch(direction.charAt(i)) {
			case "n" : {
				if (!/(all|vertical|top)/.test(config.direction)) {
					direction = direction.replace("n", "");
				}
			}
			case "s" : {
				if (!/(all|vertical|bottom)/.test(config.direction)) {
					direction = direction.replace("s", "");
				}
			}
			case "w" : {
				if (!/(all|horizontal|left)/.test(config.direction)) {
					direction = direction.replace("w", "");
				}
			}
			case "e" : {
				if (!/(all|horizontal|right)/.test(config.direction)) {
					direction = direction.replace("e", "");
				}
			}
		}
	}
	//returning new cursor
	if (direction != "") {
		return direction + "-resize";
	}
	return "";
};
 
/**
 * This is an event handler for the mouse down event 
 * of the hook element.
 * @param ev {MouseEvent} this is mouse event triggered by
 * mouse down action.
 * @return {false} false for stoping events in some browsers.
 */
Zapatec.Resizable.resizeStart = function(ev, deb) {
	if (!this.canResize()) {
		return true;
	}
	//getting event object
	ev = ev || window.event;
	//check mouse button, only left one is valid
	var iButton = ev.button || ev.which;
	if (iButton > 1) {
		return true;
	}
	var self = this;
	var config = this.getResizeConfig();
	//getting the direction of resizement
	var target = ev.currentTarget || ev.srcElement;
	while(target != document.body && target.style.cursor.indexOf("resize") == -1) {
		target = target.parentNode;
	}
	//it is pointed in the cursor style property of trigger element
	if (target.style.cursor.indexOf("resize") == -1) {
		return true;
	}
	//correction direction
	var direction = this._correctDirection(target.style.cursor);
	if (direction == "") {
		return true;
	}
	//calling the handler
	if (this.fireEvent("beforeResizeInit", ev) === false) {
		return true;
	}
	//calling global event handler
	if (Zapatec.GlobalEvents.fireEvent("beforeResizeInit", ev, this) === false) {
		return true;
	}
	//now we started resizing!
	this._setResizing(true);
	//preparing elements
	this._proceedResizableElements("resizeStart");
	//lets get the starting position of the mouse
	var mouseX = ev.clientX + Zapatec.Utils.getPageScrollX() || 0;
	var mouseY = ev.clientY + Zapatec.Utils.getPageScrollY() || 0;
	//making object sizable
	this.makeSizable();
	//starting sizing
	this.startSizing();
	if (/(vertical|horizontal|top|left|all)/.test(config.direction)) {
		//making object movable
		this.makeMovable();
		//starting movement
		this.startMove();
	}
	
	Zapatec.Utils.cover.show(
		1000001,
		direction,
		function(ev) {
			return self.resizeMove(ev);
		},
		function(ev) {
			return self.resizeEnd(ev);
		}
	);
	this._setResizingPoint(mouseX, mouseY, direction.replace("-resize", ""));
	//calling the handler
	this.fireEvent("onResizeInit", ev);

	//calling the global handler
	Zapatec.GlobalEvents.fireEvent("onResizeInit", ev, this);
	
	if (config.stopEvent) {
		return Zapatec.Utils.stopEvent(ev);
	} else {
		return true;
	}
};

/**
 * This is an event handler for the mouse move event 
 * of the cover element.
 * @param ev {MouseEvent} this is mouse event triggered by
 * mouse move action.
 * @return {false} false for stoping events in some browsers.
 */
Zapatec.Resizable.resizeMove = function(ev){
	if (!this.isResizing()) {
		return true;
	}
	//getting event object
	ev = ev || window.event;
	//calling the handler
	if (this.fireEvent("beforeResize", ev) === false) {
		return true;
	}
	//calling global event handler
	if (Zapatec.GlobalEvents.fireEvent("beforeResize", ev, this) === false) {
		return true;
	}
	//mouse position
	var x = ev.clientX + Zapatec.Utils.getPageScrollX() || 0;
	var y = ev.clientY + Zapatec.Utils.getPageScrollY() || 0;
	var movePoint = this.getResizingPoint();
	var direction = Zapatec.Utils.cover.style.cursor;
	direction = direction.replace("-resize", "");
	if (direction.indexOf("w") > -1) {
		var width = this.getWidth();
		this.setWidth(width + (movePoint.x - x));
		if (width != this.getWidth()) {
			this.moveFor(width - this.getWidth(), null);
		}
	}
	if (direction.indexOf("e") > -1) {
		this.setWidth(this.getWidth() + (x - movePoint.x));
	}
	if (direction.indexOf("n") > -1) {
		var height = this.getHeight();
		this.setHeight(height + (movePoint.y - y));
		if (height != this.getHeight()) {
			this.moveFor(null, height - this.getHeight());
		}
	}
	if (direction.indexOf("s") > -1) {
		this.setHeight(this.getHeight() + (y - movePoint.y));
	}
	this._setResizingPoint(x, y, direction);
	//calling the handler
	this.fireEvent("onResize", ev);

	//calling global event handler
	Zapatec.GlobalEvents.fireEvent("onResize", ev, this);

	// Stop event
	return Zapatec.Utils.stopEvent(ev);
};

/**
 * This is an event handler for the mouse up event 
 * of the cover element.
 * @param ev {MouseEvent} this is mouse event triggered by
 * mouse up action.
 * @return {false} false for stoping events in some browsers.
 */
Zapatec.Resizable.resizeEnd = function(ev){
	if (!this.isResizing()) {
		return true;
	}
	//getting event object
	ev = ev || window.event;
	var self = this;
	var config = this.getResizeConfig();
	//calling the handler
	if (this.fireEvent("beforeResizeEnd", ev) === false) {
		return true;
	}
	//calling global event handler
	if (Zapatec.GlobalEvents.fireEvent("beforeResizeEnd", ev, this) === false) {
		return true;
	}
	//unprepareing elements
	this._proceedResizableElements("resizeEnd");
	//hiding the cover
	Zapatec.Utils.cover.hide();
	//clearing moving point
	this._setResizingPoint(null, null);
	//now we ended dragging!
	this._setResizing(false);
	//ending sizing
	this.endSizing();
	if (/(vertical|horizontal|top|left|all)/.test(config.direction)) {
		//ending movement
		this.endMove();
	}
	//calling the handler
	this.fireEvent("onResizeEnd", ev);

	//calling global event handler
	Zapatec.GlobalEvents.fireEvent("onResizeEnd", ev, this);

	// Stop event
	return Zapatec.Utils.stopEvent(ev);
};

/**
 * Restores object from resizing.
 */
Zapatec.Resizable.restoreOfResize = function() {
	var config = this.getResizeConfig();
	this.restoreOfSizing();
	if (/(vertical|horizontal|top|left|all)/.test(config.direction)) {
		this.restoreOfMove();
	}
	var hooks = Zapatec.Array(this._getResizableHooks());
	var self = this;
	var result = false;
	//we need to save references to anonymous event listenrs
	var listenersObj = this._getRestorer().getSavedProps()["resizeListeners"];
	hooks.each(function(index, hook) {
		if (!Zapatec.isHtmlElement(hook)) {
			return;
		}
		result = true;
		if (Zapatec.is_gecko) {
			hook.style.setProperty("-moz-user-select", "", "");
		}
		Zapatec.Utils.removeEvent(hook, 'mousedown', listenersObj.mousedown);
		Zapatec.Utils.removeEvent(hook, 'mouseover', listenersObj.mouseover);
		Zapatec.Utils.removeEvent(hook, 'mouseout', listenersObj.mouseout);
	});
	if (!result) {
		var measurement = this.getSizableMeasurement();
		if (!Zapatec.isHtmlElement(measurement)) {
			return false
		}
		Zapatec.Utils.removeEvent(measurement, 'mousedown', listenersObj.mousedown);
		Zapatec.Utils.removeEvent(measurement, 'mousemove', listenersObj.hover);
	}
	return true;
};

/**
 * This method should return the array of elements that
 * are handles for resizing. These are elements that 
 * trigger resize start. This is almost empty method
 * that should be overwritten in implementing class.
 * @return {array} array of hook elements.
 */
Zapatec.Resizable._getResizableHooks = function() {
	if (this.getContainer) {
		return this.getContainer();
	} else {
		return null;
	}
};

/**
 * This method returns true if object started
 * resizing, otherwise false.
 * @return {boolean} true if object is resizing,
 * otherwise false.
 */
Zapatec.Resizable.isResizing = function() {
	return this.resizing;
};

/**
 * This method returns true if resizing is enabled,
 * otherwise it should return false.
 * @return {boolean} true if resizing enabled, otherwise false.
 */
Zapatec.Resizable.canResize = function() {
	return this.canResize;
};

/**
 * Turns on or off resizing.
 * @param on {boolean} whether we want to 
 * turn on or off resizing.
 */
Zapatec.Resizable._setCanResize = function(on) {
	this.canResize = on;
};

/**
 * Should returns the resizing configuration.
 * @return {object} configuration object.
 */
Zapatec.Resizable.getResizeConfig = function() {
	return this.getConfiguration();
};

/**
 * Sets the resizing configuration.
 * @param config {object} a set of new configuration.
 */
Zapatec.Resizable.setResizeConfig = function(config) {
	this.reconfigure(config);
};

/**
 * Sets the flag of resizing to on or off.
 * @param on {boolean} true to turn on, otherwise false.
 */
Zapatec.Resizable._setResizing = function(on) {
	this.resizing = on;
};

/**
 * This method handles overflow of the size.
 * @param limit {number} limit that was overflowed.
 * @param dimension {string} dimension of size.
 * @return {number or boolean} new size or false.
 */
Zapatec.Resizable._handleSizeOverflow = function(limit, dimension) {
	if (!this.isResizing()) {
		Zapatec.Sizable._handleSizeOverflow.call(this, limit, dimension);
	}
	return limit;
};

/**
 * Returns the Zapatec.SRProp object.
 * It is used for saving and restoring
 * object properties.
 * @return {object} Zapatec.SRProp object.
 */
Zapatec.Resizable._getRestorer = function() {
	if (!this.restorer || this.restorer.constructor != Zapatec.SRProp) {
		 this.restorer = new Zapatec.SRProp(this);
	}
	return this.restorer;
};

/**
 * Proceeds the resizable elements, to create
 * a separate array for elements that are actually sizing.
 * This is used to handle method of resizing.
 * @param resizeState {string} "resizeStart" or "resizeEnd" string 
 * to point the state of resizing.
 */
Zapatec.Resizable._proceedResizableElements = function(resizeState) {
	//drag configuration
	var config = this.getResizeConfig(), 
	copy = null, measurement = null, self = this;
	switch (config.method) {
		case "copy" : {
			if (resizeState == "resizeStart") {
				var measurement = this.getSizableMeasurement();
				if (!Zapatec.isHtmlElement(measurement)) {
					return false;
				}
				copy = measurement.cloneNode(false);
				measurement.parentNode.insertBefore(copy, measurement);
				//we substitute method of Zapatec.Sizable
				//interface to get needed result
				this._getRestorer().saveProp("getSizableElements");
				//we need to move copy
				this.getSizableElements = function() {
					return copy;
				};
				this._getRestorer().saveProp("getSizableMeasurement");
				//we need to use copy as measurement
				this.getSizableMeasurement = function() {
					return copy;
				};
				if (/(vertical|horizontal|top|left|all)/.test(config.direction)) {
					//we substitute method of Zapatec.Movable
					//interface to get needed result
					this._getRestorer().saveProp("getMovableElements");
					//we need to move copy
					this.getMovableElements = function(resetArray) {
						return copy;
					};
					this._getRestorer().saveProp("getMovableMeasurement");
					//we need to use copy as measurement
					this.getMovableMeasurement = function() {
						return copy;
					};
				}
			} else if (resizeState == "resizeEnd") {
				//destroying elements if needed
				copy = this.getMovableElements();
				var width = this.getWidth();
				var height = this.getHeight();
				var pos = null;
				if (/(vertical|horizontal|top|left|all)/.test(config.direction)) {
					pos = this.getPosition();
				}
				copy.parentNode.removeChild(copy);
				//returning old methods
				this._getRestorer().restoreProp("getSizableElements");
				this._getRestorer().restoreProp("getSizableMeasurement");
				this._getRestorer().restoreProp("getMovableElements");
				this._getRestorer().restoreProp("getMovableMeasurement");
				this.setWidth(width);
				this.setHeight(height);
				if (pos) {
					this.setPosition(pos.x, pos.y);
				}
			}
			break;
		}
		default : {
			break;
		}
	}
};

/**
 * Sets the coordinates of the point which is thought as
 * the center of resizement of the object.
 * @param x {number} x coordinate of the point.
 * @param y {number} y coordinate of the point.
 * @param direction {string} direction of resizing.
 */
Zapatec.Resizable._setResizingPoint = function(x, y, direction) {
	var resizingPoint = this._getResizingPointObject();
	//this means resize end, so we should reset the object
	if (x === null || y === null) {
		resizingPoint.x = null;
		resizingPoint.y = null;
		resizingPoint.offsetX = null;
		resizingPoint.offsetY = null;
		return;
	}
	//this means resize start, so we fill object
	if (resizingPoint.x === null || resizingPoint.y === null) {
		resizingPoint.x = x;
		resizingPoint.y = y;
		var width = this.getWidth();
		var height = this.getHeight();
		if (direction.match(/(n|w)/)) {
			var pos = this.getScreenPosition();
		}
		if (direction.indexOf("n") != -1) {
			resizingPoint.offsetY = y - pos.y;
		}
		if (direction.indexOf("w") != -1) {
			resizingPoint.offsetX = x - pos.x;
		}
		if (direction.indexOf("e") != -1) {
			resizingPoint.offsetX = x - width;
		}
		if (direction.indexOf("s") != -1) {
			resizingPoint.offsetY = y - height;
		}
		resizingPoint.offsetX = resizingPoint.offsetX || 0;
		resizingPoint.offsetY = resizingPoint.offsetY || 0;
	} else {//changing point coordinates as this is drag move
		var diffX = 0;
		var diffY = 0;
		var width = this.getWidth();
		var height = this.getHeight();
		if (direction.match(/(n|w)/)) {
			var pos = this.getScreenPosition();
		}
		if (direction.indexOf("n") != -1) {
			diffY = pos.y;
		}
		if (direction.indexOf("w") != -1) {
			diffX = pos.x;
		}
		if (direction.indexOf("e") != -1) {
			diffX = width;
		}
		if (direction.indexOf("s") != -1) {
			diffY = height;
		}
		resizingPoint.x = diffX + resizingPoint.offsetX;
		resizingPoint.y = diffY + resizingPoint.offsetY;
	}
	return;
};

/**
 * Returns the coordinate of the resizing point.
 * Its the point which is thought to be the 
 * center of resizement.
 * @return {object} object with x and y properties.
 */
Zapatec.Resizable.getResizingPoint = function() {
	var resizingPoint = this._getResizingPointObject();
	return {x : resizingPoint.x, y : resizingPoint.y};
};

/**
 * Returns the object which holds the information about resizing point.
 * @return {object} object with x, y, offsetX and offsetY properties.
 */
Zapatec.Resizable._getResizingPointObject = function() {
	if (!this.resizingPoint || typeof this.resizingPoint != "object") {
		this.resizingPoint = {x : null, y : null, offsetX : null, offsetY : null};
	}
	return this.resizingPoint;
};

/**
 * This is Zapatec.Utils.Resizable object definition.
 * It represents most of the routines and
 * events connected to resizing of the object.
 * @param config {object} - all parameters are passed as the properties of this object.
 * 
 * Constructor recognizes the following properties of the config object (rest options are inherited
 * from Zapatec.Utils.Sizable object, see sizable.js comments)
 * \code
 *    prop. name     | description
 *  -------------------------------------------------------------------------------------------------
 *  method           | {string} Method of resizing, can be "self" or "copy" (default "self").
 *  stopEvent        | {boolean} Should we stop mousedown event of the handler (default true). 
 *  handlers         | {array} Array of handles of resizing, otherwise container border will be 
 *                   | used as a trigger (default null).
 *  direction        | {string} Direction in which you can resize. Can be "all", "vertical", 
 *                   | "horizontal", "top", "left", "right", "bottom" and their combinations
 *                   | using "-" separator (default "all").
 *  borderThickness  | {number} If there is no handler for resizing, this is the thickness of 
 *                   | the container border which still triggers resizing.
 *  cornerThickness  | {number} If there is no handler for resizing, this is the thickness of 
 *                   | the container corner which still triggers resizing.
 *
 * \endcode
 */
Zapatec.Utils.Resizable = function(config) {
	Zapatec.Utils.Resizable.SUPERconstructor.call(this, config);
};

Zapatec.Utils.Resizable.id = "Zapatec.Utils.Resizable";
Zapatec.inherit(Zapatec.Utils.Resizable, Zapatec.Utils.Sizable);
Zapatec.implement(Zapatec.Utils.Resizable, "Zapatec.Movable");
Zapatec.implement(Zapatec.Utils.Resizable, "Zapatec.Resizable");

/**
 * Inits the object with set of config options.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Resizable.prototype.init = function(config) {
	//calling parent init
	Zapatec.Utils.Resizable.SUPERclass.init.call(this, config);
	this.restoreOfSizing();
	//makes object resizable 
	this.makeResizable();
};

/**
 * Sets the default configuration of the object and
 * inits it with user defined values.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Resizable.prototype.configure = function(config) {
	//method of resizing
	this.defineConfigOption("method", "self");
	//should we stop mouse down event!
	this.defineConfigOption("stopEvent", true);
	//the handlers for resizing
	this.defineConfigOption("handlers", null);
	//the border thickness for resizing, when no handler is ussed
	this.defineConfigOption("borderThickness", 10);
	//the corner thickness for resizing, when no handler is ussed
	this.defineConfigOption("cornerThickness", 10);
	//direction of resizing
	this.defineConfigOption("direction", "all");
	// Call parent method
	Zapatec.Utils.Resizable.SUPERclass.configure.call(this, config);
	config = this.getConfiguration();
	//correcting handlers option
	config.handlers = Zapatec.Array(config.handlers);
	config.handlers.each(function(index, handler) {
		handler = Zapatec.Widget.getElementById(handler);
		handler = Zapatec.Utils.img2div(handler);
		config.handlers[index] = handler;
	});
};

/**
 * Reconfigures the object with new parameters.
 * @param config {object} new configuration parameters.
 */
Zapatec.Utils.Resizable.prototype.reconfigure = function(config) {
	// Call parent method
	Zapatec.Utils.Resizable.SUPERclass.reconfigure.call(this, config);
};

/**
 * Returns the handlers.
 * @return {array} handler elements.
 */
Zapatec.Utils.Resizable.prototype._getResizableHooks = function() {
	return this.getConfiguration().handlers;
};
