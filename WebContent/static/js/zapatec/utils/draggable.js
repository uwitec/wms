//$Id: draggable.js 6839 2007-04-03 08:51:13Z slip $
/**
 * This is a set of functionality used for dragging object
 * and is implemented in interface (mixin) manner.
 */
Zapatec.Draggable = {};

/**
 * Makes the element draggable. This means attaching events, 
 * and making some preparations.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Draggable.makeDraggable = function() {
	if (!this.hasInterface("Zapatec.Movable")) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' has no Zapatec.Movable interface so can not be draggable!"});
		return false;
	}
	if (!this.hasInterface("Zapatec.CommandEvent")) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' has no Zapatec.CommandEvent interface!"});
		return false;
	}
	var draggables = Zapatec.Array(this.getDraggableElements());
	var hooks = Zapatec.Array(this._getDraggableHooks());
	var self = this, result = false;
	var config = this.getDragConfig();
	//if method is not "cut" we can not imitate event capture
	if (config.method != "cut") {
		this.setDragConfig({eventCapture : false});
	}
	//we need to save references to anonymous event listenrs
	var listenersObj = this._getRestorer().getSavedProps()["dragListeners"] = {
		mousedown : function(ev) {
			return self.dragStart(ev);
		},
		mousemove : function(ev) {
			if (self.isDragging()) {
				return self.dragMove(ev);
			}
		},
		mouseup : function(ev) {
			if (self.isDragging()) {
				return self.dragEnd(ev);
			}
		}
	};
	hooks.each(function(index, hook) {
		if (!Zapatec.isHtmlElement(hook)) {
			return;
		}
		//if we are here than there is at least one hook :)
		result = true;
		//this for disabling text selection on drag
		if (Zapatec.is_gecko) {
			hook.style.setProperty("-moz-user-select", "none", "");
		}
		//adding drag start event
		Zapatec.Utils.addEvent(hook, 'mousedown', listenersObj.mousedown);
		//imitating capturing of some events. This is used when mouse event
		//is involved with hook element, cause there won't be a click event for
		//that element if cover will appear over it.
		if (config.eventCapture) {
			Zapatec.Utils.addEvent(hook, 'mousemove', listenersObj.mousemove);
			Zapatec.Utils.addEvent(hook, 'mouseup', listenersObj.mouseup);
		}
	});
	draggables.each(function(index, draggable) {
		if (!Zapatec.isHtmlElement(draggable)) {
			return;
		}
		//this is for backward compability and to be able to
		//get draggable object with only a reference to HTML element
		self.createProperty(draggable, "dragObj", self);
	});
	//returning result
	return result;	
};

/**
 * This is an event handler for the mouse down event 
 * of the hook element.
 * @param ev {MouseEvent} this is mouse event triggered by
 * mouse down action.
 * @return {false} false for stoping events in some browsers.
 */
Zapatec.Draggable.dragStart = function(ev) {
	if (!this.canDrag()) {
		return true;
	}
	//getting event object
	ev = ev || window.event;
	//check mouse button, only left one is valid
	var iButton = ev.button || ev.which;
	if (iButton > 1) {
		return false;
	}
	var self = this;
	var config = this.getDragConfig();
	//calling the handler
	if (this.fireEvent("beforeDragInit", ev) === false) {
		return true;
	}
	//calling global event handler
	if (Zapatec.GlobalEvents.fireEvent("beforeDragInit", ev, this) === false) {
		return true;
	}
	//now we started dragging!
	this._setDragging(true);
	//workaround for dummy and copy methods to capture events correctly
	if (config.eventCapture && (config.method == "dummy" || config.method == "copy")) {
		var draggables = Zapatec.Array(this.getDraggableElements());
		draggables.each(function(index, draggable) {
			draggable.restorer.saveProp("style.zIndex");
			draggable.style.zIndex = 2000001;
		});
	}
	//preparing elements
	this._proceedDraggableElements("dragStart");
	//lets get the starting position of the mouse
	var mouseX = ev.clientX + (Zapatec.is_khtml ? 0 : (Zapatec.Utils.getPageScrollX() || 0));
	var mouseY = ev.clientY + (Zapatec.is_khtml ? 0 : (Zapatec.Utils.getPageScrollY() || 0));
	//making object movable
	this.makeMovable();
	//starting movement
	this.startMove();
	//preparing movable elements
	var elements = Zapatec.Array(this.getMovableElements());
	elements.each(function(index, movable) {
		if (Zapatec.isHtmlElement(movable)) {
			//giving z-index
			movable.restorer.saveProp("style.zIndex");
			movable.style.zIndex = 1000000 + (parseInt(movable.style.zIndex, 10) || 0);
			//applying styles
			self._proceedDragStyles(movable, "dragStart");
			//assigning proper cursor if event capture
			if (config.eventCapture) {
				movable.restorer.saveProp("style.cursor");
				movable.style.cursor = "move";
			}
		} else if (Zapatec.isMovableObj(movable)) {
			//making recursive iterating through all child movables
			var elems = Zapatec.Array(movable.getMovableElements());
			elems.each(arguments.calee);
		}
	});
	Zapatec.Utils.cover.show(
		config.eventCapture ? 999999 : 2000000,
		"move",
		function(ev) {
			return self.dragMove(ev);
		},
		function(ev) {
			return self.dragEnd(ev);
		}
	);
	this._setMovingPoint(mouseX, mouseY);
	//calling the handler
	this.fireEvent("onDragInit", ev);

	//calling the global handler
	Zapatec.GlobalEvents.fireEvent("onDragInit", ev, this);
	
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
Zapatec.Draggable.dragMove = function(ev){
	if (!this.isDragging()) {
		return true;
	}
	//getting event object
	ev = ev || window.event;
	//calling the handler
	if (this.fireEvent("beforeDragMove", ev) === false) {
		return true;
	}
	//calling global event handler
	if (Zapatec.GlobalEvents.fireEvent("beforeDragMove", ev, this) === false) {
		return true;
	}
	if (Zapatec.Utils.cover.style.zIndex != 2000000) {
		var config = this.getDragConfig();
		//workaround for dummy and copy methods to handle eventCapture corectly
		if (config.eventCapture && (config.method == "dummy" || config.method == "copy")) {
			var draggables = Zapatec.Array(this.getDraggableElements());
			draggables.each(function(index, draggable) {
				draggable.restorer.restoreProp("style.zIndex");
			});
		}
		//putting cover over everything
		Zapatec.Utils.cover.style.zIndex = 2000000;
	}
	//mouse position
	var mouseX = ev.clientX + (Zapatec.is_khtml ? 0 : (Zapatec.Utils.getPageScrollX() || 0));
	var mouseY = ev.clientY + (Zapatec.is_khtml ? 0 : (Zapatec.Utils.getPageScrollY() || 0));
	var movePoint = this.getMovingPoint();
	this.moveFor(mouseX - movePoint.x, mouseY - movePoint.y);
	this._setMovingPoint(mouseX, mouseY);
	//calling the handler
	this.fireEvent("onDragMove", ev);

	//calling global event handler
	Zapatec.GlobalEvents.fireEvent("onDragMove", ev, this);

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
Zapatec.Draggable.dragEnd = function(ev){
	if (!this.isDragging()) {
		return true;
	}
	//getting event object
	ev = ev || window.event;
	var self = this;
	//calling the handler
	if (this.fireEvent("beforeDragEnd", ev) === false) {
		return true;
	}
	//calling global event handler
	if (Zapatec.GlobalEvents.fireEvent("beforeDragEnd", ev, this) === false) {
		return true;
	}
	var config = this.getDragConfig();
	//workaround for dummy and copy methods to handle eventCapture corectly
	if (config.eventCapture && (config.method == "dummy" || config.method == "copy")) {
		var draggables = Zapatec.Array(this.getDraggableElements());
		draggables.each(function(index, draggable) {
			draggable.restorer.restoreProp("style.zIndex", true);
		});
	}
	//restoring movable elements
	var elements = Zapatec.Array(this.getMovableElements());
	elements.each(function(index, movable) {
		if (Zapatec.isHtmlElement(movable)) {
			movable.restorer.restoreProp("style.zIndex");
			movable.restorer.restoreProp("style.cursor");
			//clearing styles
			self._proceedDragStyles(movable, "dragEnd");
		} else if (Zapatec.isMovableObj(movable)) {
			//making recursive iterating through all child movables
			var elems = Zapatec.Array(movable.getMovableElements());
			elems.each(arguments.calee);
		}
	});

	//unprepareing elements
	this._proceedDraggableElements("dragEnd");
	//hiding the cover
	Zapatec.Utils.cover.hide();
	//clearing moving point
	this._setMovingPoint(null, null);
	//now we ended dragging!
	this._setDragging(false);
	//ending movement
	this.endMove();
	//calling the handler
	this.fireEvent("onDragEnd", ev);

	//calling global event handler
	Zapatec.GlobalEvents.fireEvent("onDragEnd", ev, this);

	// Stop event
	return Zapatec.Utils.stopEvent(ev);
};

/** 
 * returns element to its original place where it was taken from.
 * Applicable only for the cut method!
 */
Zapatec.Draggable.restorePos = function(){
	this.restoreOfMove();
};

/**
 * Restores object from dragging.
 */
Zapatec.Draggable.restoreOfDrag = function() {
	this.restoreOfMove();
	var hooks = Zapatec.Array(this._getDraggableHooks());
	var draggables = Zapatec.Array(this.getDraggableElements());
	var self = this;
	var config = this.getDragConfig();
	//we need to save references to anonymous event listenrs
	var listenersObj = this._getRestorer().getSavedProps()["dragListeners"];
	hooks.each(function(index, hook) {
		if (!Zapatec.isHtmlElement(hook)) {
			return;
		}
		if (Zapatec.is_gecko) {
			hook.style.setProperty("-moz-user-select", "", "");
		}
		Zapatec.Utils.removeEvent(hook, 'mousedown', listenersObj.mousedown);
		if (config.eventCapture) {
			Zapatec.Utils.removeEvent(hook, 'mousemove', listenersObj.mousemove);
			Zapatec.Utils.removeEvent(hook, 'mouseup', listenersObj.mouseup);
		}
	});
	draggables.each(function(index, draggable) {
		if (!Zapatec.isHtmlElement(draggable)) {
			return;
		}
		draggable.dragObj = null;
	});
	return true;
};

/**
 * This method should return the array of elements that
 * are to be draggable. These are not always elements
 * that are actually moved. This is almost empty method
 * that should be overwritten in implementing class.
 * @return {array} array of draggable elements.
 */
Zapatec.Draggable.getDraggableElements = function() {
	return this.getContainer();
};

/**
 * This method should return the array of elements that
 * are handles for dragging. These are elements that 
 * trigger drag start. This is almost empty method
 * that should be overwritten in implementing class.
 * @return {array} array of hook elements.
 */
Zapatec.Draggable._getDraggableHooks = function() {
	return this.getContainer();
};

/**
 * Returns container for this object.
 */
Zapatec.Draggable.getContainer = function() {
	return this.getDragConfig().container;
};

/**
 * This method returns true if object started
 * dragging, otherwise false.
 * @return {boolean} true if object is dragging,
 * otherwise false.
 */
Zapatec.Draggable.isDragging = function() {
	return this.dragging;
};

/**
 * This method returns true if dragging is enabled,
 * otherwise it should return false.
 * @return {boolean} true if dragging enabled, otherwise false.
 */
Zapatec.Draggable.canDrag = function() {
	return this.canDrag;
};

/**
 * Turns on or off dragging.
 * @param on {boolean} whether we want to 
 * turn on or off dragging.
 */
Zapatec.Draggable._setCanDrag = function(on) {
	this.canDrag = on;
};

/**
 * Should returns the dragging configuration.
 * @return {object} configuration object.
 */
Zapatec.Draggable.getDragConfig = function() {
	return this.getConfiguration();
};

/**
 * Sets the dragging configuration.
 * @param config {object} a set of new configuration.
 */
Zapatec.Draggable.setDragConfig = function(config) {
	this.reconfigure(config);
};

/**
 * Sets the flag of dragging to on or off.
 * @param on {boolean} true to turn on, otherwise false.
 */
Zapatec.Draggable._setDragging = function(on) {
	this.dragging = on;
};

/**
 * This method handles overflow of the dragging coordinate.
 * @param limit {number} limit that was overflowed.
 * @param dimension {string} dimension of coordinate.
 * @return {number or boolean} new coordinate or false.
 */
Zapatec.Draggable._handleCoordOverflow = function(limit, dimension) {
	if (!this.isDragging()) {
		Zapatec.Movable._handleCoordOverflow.call(this, limit, dimension);
	}
	return limit;
};

/**
 * Returns the Zapatec.SRProp object.
 * It is used for saving and restoring
 * object properties.
 * @return {object} Zapatec.SRProp object.
 */
Zapatec.Draggable._getRestorer = function() {
	if (!this.restorer) {
		this.restorer = new Zapatec.SRProp(this);
	}
	return this.restorer;
};

/**
 * Proceeds the array of draggable elements, to create
 * a separate array for elements that are actually moving.
 * This is used to handle method of dragging.
 * @param dragState {string} "dragStart" or "dragEnd" string 
 * to point the state of dragging.
 */
Zapatec.Draggable._proceedDraggableElements = function(dragState) {
	//drag configuration
	var config = this.getDragConfig(), 
	restorer = this._getRestorer(),
	copies = null, measurement = null, self = this,
	listenersObj = restorer.getProp("dragListeners");
	function toggleEvents(action, hooks, listenersObj) {
		hooks.each(function(index, hook) {
			Zapatec.Utils[action + "Event"](hook, "mousedown", listenersObj.mousedown);
			if (config.eventCapture) {
				Zapatec.Utils[action + "Event"](hook, 'mousemove', listenersObj.mousemove);
				Zapatec.Utils[action + "Event"](hook, 'mouseup', listenersObj.mouseup);
			}
		});
	}
	switch (config.method) {
		case "copy" : case "dummy" : {
			if (dragState == "dragStart") {
				var elements = Zapatec.Array(this.getDraggableElements());
				var hooks = Zapatec.Array(this._getDraggableHooks());
				copies = Zapatec.Array();
				//removing listeners so the elements will be cloned
				//without them.
				toggleEvents("remove", hooks, listenersObj);
				//copying elements
				elements.each(function(index, movable) {
					if (Zapatec.isHtmlElement(movable)) {
						//cloning a node
						var newNode = movable.cloneNode(config.copyChilds);
						newNode.dragObj = null;
						newNode.within = null;
						//adding event listeners
						if (config.eventCapture) {
							Zapatec.Utils.addEvent(newNode, 'mousemove', listenersObj.mousemove);
							Zapatec.Utils.addEvent(newNode, 'mouseup', listenersObj.mouseup);
						}
						//inserting it to the place where the original one is
						movable.parentNode.insertBefore(newNode, movable);
						//fix for unknown problem in IE6
						newNode.style.visibility = "visible";
						//adding copy to array
						copies.push(newNode);
						//assigning new measurement
						if (movable == self.getMovableMeasurement()) {
							measurement = newNode;
						}
					} else if (Zapatec.isMovableObj(movable)) {
						//making recursive iterating through all child movables
						var elems = Zapatec.Array(movable.getMovableElements());
						elems.each(arguments.calee);
					}
				});
				//adding listeners back
				toggleEvents("add", hooks, listenersObj);
				//if there was no measurement copied we take the
				//original one
				if (!measurement) {
					measurement = this.getMovableMeasurement();
				}
				//we substitute methods of Zapatec.Movable
				//interface to get needed result
				restorer.saveProp("getMovableElements");
				restorer.saveProp("isMovableSafely()");
				//new elements need to be movable safely
				this._setMovableSafely(false);
				//we need to move copies
				this.getMovableElements = function(resetArray) {
					var arr = copies;
					copies = resetArray ? null : copies;
					return arr;
				};
				restorer.saveProp("getMovableMeasurement");
				//we need to use copied measurement if such exists
				this.getMovableMeasurement = function() {
					return measurement;
				};
			} else if (dragState == "dragEnd") {
				//destroying elements if needed
				var elements = Zapatec.Array(this.getMovableElements(true));
				elements.each(function(index, movable) {
					if (config.method == "dummy") {
						movable.parentNode.removeChild(movable);
					}
					//removing event listeners
					if (config.eventCapture) {
						Zapatec.Utils.removeEvent(movable, 'mousemove', listenersObj.mousemove);
						Zapatec.Utils.removeEvent(movable, 'mouseup', listenersObj.mouseup);
					}
				});
				this.restoreOfMove();
				//returning old methods and values
				restorer.restoreProp("getMovableElements");
				this._setMovableSafely(restorer.getProp("isMovableSafely()"));
				restorer.restoreProp("isMovableSafely()");
				restorer.restoreProp("getMovableMeasurement");
			}
			break;
		}
		default : {
			break;
		}
	}
};

/**
 * Proceeds each movable element with some
 * styling routines on drag start and end.
 * @param movable {HTML element} element to proceed.
 * @param dragState {string} string pointing to drag state.
 */
Zapatec.Draggable._proceedDragStyles = function(movable, dragState) {
	var config = this.getDragConfig();
	//overwriting class with new one if needed
	if (config.overwriteCSS) {
		if (dragState == "dragStart") {
			movable.restorer.saveProp("className");
			movable.className = config.overwriteCSS;
		} else if (dragState == "dragEnd") {
			movable.restorer.restoreProp("className");
		}
	}
	//adding drag class if needed
	if (config.dragCSS) {
		if (dragState == "dragStart") {
			Zapatec.Utils.addClass(movable, config.dragCSS);
		} else if (dragState == "dragEnd") {
			Zapatec.Utils.removeClass(movable, config.dragCSS);
		}
	}
};

/**
 * Sets the coordinates of the point which is thought as
 * the center of movement of the object.
 * @param x {number} x coordinate of the point.
 * @param y {number} y coordinate of the point.
 */
Zapatec.Draggable._setMovingPoint = function(x, y) {
	var movingPoint = this._getMovingPointObject();
	//this means drag end, so we should reset the object
	if (x === null || y === null) {
		movingPoint.x = null;
		movingPoint.y = null;
		movingPoint.offsetX = null;
		movingPoint.offsetY = null;
		return;
	}
	//this means drag start, so we fill object
	if (movingPoint.x === null || movingPoint.y === null) {
		var pos = this.getPagePosition();
		movingPoint.x = x;
		movingPoint.y = y;
		movingPoint.offsetX = x - pos.x;
		movingPoint.offsetY = y - pos.y;
	} else {//changing point coordinates as this is drag move
		var pos = this.getPagePosition();
		movingPoint.x = pos.x + movingPoint.offsetX;
		movingPoint.y = pos.y + movingPoint.offsetY;
	}
	return;
};

/**
 * Returns the coordinate of the moving point.
 * Its the point which is thought to be the 
 * center of movement.
 * @return {object} object with x and y properties.
 */
Zapatec.Draggable.getMovingPoint = function() {
	var movingPoint = this._getMovingPointObject();
	return {x : movingPoint.x, y : movingPoint.y};
};

/**
 * Returns the object which holds the information about moving point.
 * @return {object} object with x, y, offsetX and offsetY properties.
 */
Zapatec.Draggable._getMovingPointObject = function() {
	if (!this.movingPoint || typeof this.movingPoint != "object") {
		this.movingPoint = {x : null, y : null, offsetX : null, offsetY : null};
	}
	return this.movingPoint;
};

/**
 * This is Zapatec.Utils.Draggable object definition.
 * It represents most of the routines and
 * events connected to dragging of the object.
 * @param config {object} - all parameters are passed as the properties of this object.
 * 
 * Constructor recognizes the following properties of the config object (rest options are inherited
 * from Zapatec.Utils.Movable object, see movable.js comments)
 * \code
 *    prop. name     | description
 *  -------------------------------------------------------------------------------------------------
 *  method           | {string} Method of dragging, can be "cut", "copy" or "dummy" (default "cut").
 *  stopEvent        | {boolean} Should we stop mousedown event of the handler (default true). 
 *  eventCapture     | {boolean} This is a workaround for capturing some events like click or double
 *                   | click for the element that triggers dragging. Otherwise there will happen no
 *                   | such event at all (default false).
 *  handler          | {HTML element} Handle of dragging, otherwise container will be used (default null).
 *  dragCSS          | {string} Class name that will be added to the element while it is dragging (default "").
 *  overwriteCSS     | {string} Class name that will overwrite all the classes of the element (default "").
 *
 * \endcode
 * There have been made some changes into the structure of DnD routines, so many options were changed.
 * They are still working for keeping backward compability, but are depricated for the future use.
 * Here is the list of the options and their equivalents:
 * \code
 *    prop. name     | description
 *  -------------------------------------------------------------------------------------------------
 *  left, top, right,| Use "limit" option of the Zapatec.Utils.Movable object!
 *  bottom           |
 *  dragLayer        | Use "moveLayer" option of the Zapatec.Utils.Movable object!
 *  beforeDragInit,  | Use standart Zapatec.Widget option "listeners"
 *  beforeDragMove,  |
 *  beforeDragEnd,   |
 *  onDragInit,      |
 *  onDragMove,      |
 *  onDragEnd        |
 *  stopEv           | Use "stopEvent" form instead.
 *
 * \endcode
 */
Zapatec.Utils.Draggable = function(config) {
	if (arguments.length > 1) {
		var args = arguments[1];
		args.container = config;
		config = args;
	}
	//for backward compability
	if (typeof config.left != "undefined" || typeof config.right != "undefined" ||
	    typeof config.top != "undefined" || typeof config.bottom != "undefined") {
			config.limit = {
				minX : config.left, 
				maxX : config.right, 
				minY : config.top, 
				maxY : config.bottom
			};
	}
	if (config.dragLayer) {config.moveLayer = config.dragLayer;}
	if (!config.eventListeners) {
		config.eventListeners = {};
	}
	if (config.beforeDragInit) {config.eventListeners.beforeDragInit = config.beforeDragInit;}
	if (config.beforeDragMove) {config.eventListeners.beforeDragMove = config.beforeDragMove;}
	if (config.beforeDragEnd) {config.eventListeners.beforeDragEnd = config.beforeDragEnd;}
	if (config.onDragInit) {config.eventListeners.onDragInit = config.onDragInit;}
	if (config.onDragMove) {config.eventListeners.onDragMove = config.onDragMove;}
	if (config.onDragEnd) {config.eventListeners.onDragEnd = config.onDragEnd;}
	if (config.stopEv) {config.stopEvent = config.stopEv;}
	config = Zapatec.Hash.remove(config, 
		"left", "top", "right", "bottom", "dragLayer",
		"beforeDragInit", "beforeDragMove", "beforeDragEnd",
		"onDragInit", "onDragMove", "onDragEnd", "stopEv"
	);
	Zapatec.Utils.Draggable.SUPERconstructor.call(this, config);
};

Zapatec.Utils.Draggable.id = "Zapatec.Utils.Draggable";
Zapatec.inherit(Zapatec.Utils.Draggable, Zapatec.Utils.Movable);
Zapatec.implement(Zapatec.Utils.Draggable, "Zapatec.Draggable");

/**
 * Inits the object with set of config options.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Draggable.prototype.init = function(config) {
	//calling parent init
	Zapatec.Utils.Draggable.SUPERclass.init.call(this, config);
	//making draggable
	this.makeDraggable();
};

/**
 * Sets the default configuration of the object and
 * inits it with user defined values.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Draggable.prototype.configure = function(config) {
	//method of dragging
	this.defineConfigOption("method", "cut");
	//should we stop mouse down event!
	this.defineConfigOption("stopEvent", true);
	//should we capture mouse down event!
	this.defineConfigOption("eventCapture", false);
	//the handler for dragging
	this.defineConfigOption("handler", null);
	//dragging class name, will be added to all present classes
	this.defineConfigOption("dragCSS", null);
	//dragging overwrite class name, will overwrite all present 
	//classes, but they will be restored on drag end
	this.defineConfigOption("overwriteCSS", null);
	//this option is only useful for dummy or copy methods
	//and determines whether we copy child content of the elements
	this.defineConfigOption("copyChilds", true);
	//redefining the makeMovable option to cancel the action
	//defined in Zapatec.Movable class
	this.defineConfigOption("makeMovable", false);
	// Call parent method
	Zapatec.Utils.Draggable.SUPERclass.configure.call(this, config);
	config = this.getConfiguration();
	//Opera has strange behaviour - when you just click element starts draging,
	//although mouseup should have been called.
	if (Zapatec.is_opera) {
		config.eventCapture = true;
	}
	//correcting handler option
	config.handler = Zapatec.Widget.getElementById(config.handler);
	config.handler = Zapatec.Utils.img2div(config.handler);
	if (!Zapatec.isHtmlElement(config.handler)) {
		config.handler = config.container;
	}
};

/**
 * Reconfigures the object with new parameters.
 * @param config {object} new configuration parameters.
 */
Zapatec.Utils.Draggable.prototype.reconfigure = function(config) {
	// Call parent method
	Zapatec.Utils.Draggable.SUPERclass.reconfigure.call(this, config);
};

/**
 * Returns the array of all draggable elements.
 * @return {array} array of draggable elements.
 */
Zapatec.Utils.Draggable.prototype.getDraggableElements = function() {
	return this.movableElements;
};

/**
 * Returns the handler.
 * @return {HTML element} handler element.
 */
Zapatec.Utils.Draggable.prototype._getDraggableHooks = function() {
	return this.getConfiguration().handler;
};

/**
 * searches elements with className=class and makes them draggable(useful to call it on document load)
 * @param class {string} searcable element's CSSclassname.
 * @param el {HTMLElement} reference to the element.
 * @param recursive {boolean} searches in childs.
 * @param config {object} config object for draggable.
 * @return {array} array of draggable objects
 */
Zapatec.Utils.initDragObjects = function(className, el, recursive, config){
	//only by class name :)
	if (!className) return;
	//array of elements to make draggable
	var elements = Zapatec.Utils.getElementsByAttribute('className', className, el, recursive, true);
	//returning result
	return Zapatec.Utils.applyToElements(Zapatec.Utils.Draggable, elements, config);
}
