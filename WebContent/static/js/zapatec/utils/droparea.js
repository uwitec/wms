//$Id: droparea.js 6839 2007-04-03 08:51:13Z slip $
/** 
 * This is Zapatec.Utils.DropArea object definition.
 * It represents most of the routines connected to 
 * dropping to some area and tracking global events 
 * connected to dragging of the object.
 * @param config {object} - all parameters are passed as the properties of this object.
 * 
 * Constructor recognizes the following properties of the config object 
 * \code
 *    prop. name     | description
 *  -------------------------------------------------------------------------------------------------
 *  container        | {string or HTML element} Element to be made the droparea (required).
 *  dropName         | {string} id of droparea
 *  listeners        | recognizes the following listeners:
 *                   |  - onDrop - drop event listener;
 *                   |  - onDragInit - dragging start event listener;
 *                   |  - onDragOver - drag over the droparea event listener (is fired only first 
 *                   |                 time the element came over);
 *                   |  - onDragOut - drag out of the droparea event listener (is fired only first 
 *                   |                 time the element went out);
 *                   |  - onDragEnd - dragging end event listener;
 *
 * This is the new interface of this object, but for backward compability we leave old one, where
 * all this parameters are passed as arguments in the following order: container, dropname, onDrop,
 * onDragInit, onDragOver, onDragOut, onDragEnd. This way is depricated on the current moment.
 */
Zapatec.Utils.DropArea = function(config) {
	//this is for backward compability
	if (arguments.length > 1) {
		config = {container : config};
		config.dropName = arguments[1];
		config.eventListeners = {
			onDrop : arguments[2],
			onDragInit : arguments[3],
			onDragOver : arguments[4],
			onDragOut : arguments[5],
			onDragEnd : arguments[6]
		}
	}
	Zapatec.Utils.DropArea.SUPERconstructor.call(this, config);
	
	
};

Zapatec.Utils.DropArea.id = "Zapatec.DropArea";
Zapatec.inherit(Zapatec.Utils.DropArea, Zapatec.Widget);
Zapatec.implement(Zapatec.Utils.DropArea, "Zapatec.CommandEvent");

/**
 * Inits the object with set of config options.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.DropArea.prototype.init = function(config) {
	//calling parent init
	Zapatec.Utils.DropArea.SUPERclass.init.call(this, config);
	//backward compability.
	this.dropArea = this.getConfiguration().container;
	this.dropArea.dropAtr = {};
	//a flag for the element which is currently over the droparea.
	//This is used to prevent endless calls to onDragOver and onDragOut.
	//Now this handlers are called only once.
	this.elementOver = false;
	//saving all the given handlers
	this.addListeners();
};

/**
 * Sets the default configuration of the object and
 * inits it with user defined values.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.DropArea.prototype.configure = function(config) {
	//drop area element
	this.defineConfigOption("container", null);
	//name of the drop area
	this.defineConfigOption("dropName", null);
	//no theme
	this.defineConfigOption("theme", null);
	// Call parent method
	Zapatec.Utils.DropArea.SUPERclass.configure.call(this, config);
	config = this.getConfiguration();
	//Retreiveing droparea HTMLElement
	config.container = Zapatec.Widget.getElementById(config.container);
};

/**
 * Reconfigures the object with new parameters.
 * @param config {object} new configuration parameters.
 */
Zapatec.Utils.DropArea.prototype.reconfigure = function(config) {
	// Call parent method
	Zapatec.Utils.DropArea.SUPERclass.reconfigure.call(this, config);
};

/**
 * Drag Init event
 */
Zapatec.Utils.DropArea.prototype.dragInit = function(ev, movable) {
	//retreiving the move point
	this.mousePos = movingPoint = movable.getMovingPoint();
	//retreiving the droparea position
	var dropArea = Zapatec.Utils.getElementOffset(this.dropArea);
	//getting movable elements
	var elems = movable.getMovableElements();
	if (elems.length == 1) {
		elems = elems[0];
	}
	//if the mouse is over the droparea so lets point it in elementOver flag
	if ((movingPoint.x > dropArea.x) && (movingPoint.x < dropArea.x + dropArea.width) && 
	    (movingPoint.y > dropArea.y) && (movingPoint.y < dropArea.y + dropArea.height)) {
			this.elementOver = elems;
	}
	//if we have dragInit handler fire it
	if (this.fireEvent("onDragInit", elems, this) == "parent-re-execute") {
		return "re-execute";
	}
	//if we have dragOver handler and eventually the element was over the droparea on the start
	//lets fire it 
	if (this.elementOver) {
		if (this.fireEvent("onDragOver", elems, this) == "parent-re-execute") {
			return "re-execute";
		}
	}
};

/**
 * Drag Move event
 */
Zapatec.Utils.DropArea.prototype.dragMove = function(ev, movable) {
	//retreiving the move point
	this.mousePos = movingPoint = movable.getMovingPoint();
	//retreiving the droparea position
	var dropArea = Zapatec.Utils.getElementOffset(this.dropArea);
	//getting movable elements
	var elems = movable.getMovableElements();
	if (elems.length == 1) {
		elems = elems[0];
	}
	//if the mouse is over the droparea so lets point it in elementOver flag
	//otherwise lets give it value null
	if ((movingPoint.x > dropArea.x) && (movingPoint.x < dropArea.x + dropArea.width) && 
	    (movingPoint.y > dropArea.y) && (movingPoint.y < dropArea.y + dropArea.height)) {
		//if we have dragOver handler fire it
		if (!this.elementOver) {
			if (this.fireEvent("onDragOver", elems, this) == "parent-re-execute") {
				return "re-execute";
			}
		}
		this.elementOver = elems;
	} else {
		//if we have dragOut handler fire it
		if (this.elementOver) {
			if (this.fireEvent("onDragOut", elems, this) == "parent-re-execute") {
				return "re-execute";
			}
		}
		this.elementOver = null;
	}
};

/**
 * Drag End event
 */
Zapatec.Utils.DropArea.prototype.dragEnd = function(ev, movable) {
	//getting movable elements
	var elems = movable.getMovableElements();
	if (elems.length == 1) {
		elems = elems[0];
	}
	//if we have drop handler fire it
	if (this.elementOver) {
		if (this.fireEvent("onDrop", elems, this) == "parent-re-execute") {
			return "re-execute";
		}
	}
	//if we have dragEnd handler fire it
	if (this.fireEvent("onDragEnd", elems, this) == "parent-re-execute") {
		return "re-execute";
	}
	//cleaning the values
	this.movingPoint = null;
	this.elementOver = null;
};

/**
 * removes all drag listeners
 */
Zapatec.Utils.DropArea.prototype.removeListeners = function() {
	//removes all the handlers set by this droparea
	Zapatec.GlobalEvents.removeEventListener("onDragInit", this.tmp.onDragInit);
	Zapatec.GlobalEvents.removeEventListener("onDragMove", this.tmp.onDragMove);
	Zapatec.GlobalEvents.removeEventListener("onDragEnd", this.tmp.onDragEnd);
}

/**
 * adds all drag listeners
 */
Zapatec.Utils.DropArea.prototype.addListeners = function() {
	//Sets Draggable global handlers
	var self = this;
	if (!this.tmp) {
		this.tmp = {};
	}
	this.tmp.onDragInit = function(ev, movable) {
		this.returnValue(self.dragInit(ev, movable));
	};
	this.tmp.onDragMove = function(ev, movable) {
		this.returnValue(self.dragMove(ev, movable));
	};
	this.tmp.onDragEnd = function(ev, movable) {
		this.returnValue(self.dragEnd(ev, movable));
	};
	Zapatec.GlobalEvents.addEventListener("onDragInit", this.tmp.onDragInit);
	Zapatec.GlobalEvents.addEventListener("onDragMove", this.tmp.onDragMove);
	Zapatec.GlobalEvents.addEventListener("onDragEnd", this.tmp.onDragEnd);
}
