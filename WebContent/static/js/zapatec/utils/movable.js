//$Id: movable.js 6834 2007-04-02 06:49:19Z slip $
/**
 * This is a set of functionality used for moving object
 * and is implemented in interface (mixin) manner.
 */
Zapatec.Movable = {};

/**
 * Moves the object ot specified position.
 * Object can consist of group of elements,
 * that will be moved together. This method can
 * recognize not only "px" values, but "left", "top",
 * "bottom", "right", etc. This depends on your
 * _parseCoordinate implementation, if you don't
 * want to use standart one.
 * @param x {number or string} X coordinate.
 * @param y {number or string} Y coordinate.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Movable.setPosition = function(x, y) {
	//preparations which can depend on the Widget.
	if (!this.isMovableSafely()) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' was not prepared for moving! Use obj.makeMovable() to do so!", type : "warning"});
		return false;
	}
	var msgValue = null, moveConfig = this.getMoveConfig();
	//_parseCoordinate takes the coordinate value and first of all
	//tryes to return the numeric representation in pixels.
	//Also if it returns null value (false and "" are also
	//treated so) method stops execution without making something.
	//This can be useful for implementing restrictions.
	if (x || x === 0) {
		msgValue = x;
		x = this._parseCoordinate(x, "x", moveConfig.moveLayer);
		if (!x && x !== 0) {
			Zapatec.Log({description : "The X coordinate " + msgValue + " can not be set for object with ID '" + this.id + "'!", type : "warning"});
			return false;
		}
	}
	if (y || y === 0) {
		msgValue = y;
		y = this._parseCoordinate(y, "y", moveConfig.moveLayer);
		if (!y && y !== 0) {
			Zapatec.Log({description : "The Y coordinate " + msgValue + " can not be set for object with ID '" + this.id + "'!", type : "warning"});
			return false;
		}
	}
	//getMovableElements returns the array of elements
	//that actually are moved. Param passed is the dimension 
	//in which we move.
	var elements = Zapatec.Array(this.getMovableElements());
	if (this.fireEvent("beforePositionChange", x, y) === false) {
		return false;
	}
	if (Zapatec.GlobalEvents.fireEvent("beforePositionChange", x, y, this) === false) {
		return false;
	}
	//proceeding coordinates
	this._proceedElementsCoords(x, y, elements);
	//firing events
	if (this.isMoving()) {
		this.fireEvent("onMove", x || this.getPosition().x, y || this.getPosition().y);
		Zapatec.GlobalEvents.fireEvent("onMove", x || this.getPosition().x, y || this.getPosition().y, this);
	}
	this.fireEvent("onPositionChange", x || this.getPosition().x, y || this.getPosition().y);
	Zapatec.GlobalEvents.fireEvent("onPositionChange", x || this.getPosition().x, y || this.getPosition().y, this);
	//reporting success
	return true;
};

/**
 * This method is used for the objects that use
 * such concept as orientation. Method reacts on
 * two types of orientation: "vertical" and "horizontal".
 * @param x {number} oriented X to set.
 * @param y {number} oriented Y to set.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Movable.setOrientedPosition = function(x, y) {
	switch (this.getMoveConfig().orientation) {
		case "vertical" : {
			//vertical orientation means height
			return this.setPosition(y, x);
		}
		case "horizontal" : {
			//horizontal orientation means width
			return this.setPosition(x, y);
		}
	}
	return false;
};

/**
 * Sets the position of the object relatively
 * to the document page(BODY).
 * @param x {number or string} X coordinate on the page.
 * @param y {number or string} Y coordinate on the page.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Movable.setPagePosition = function(x, y) {
	var moveConfig = this.getMoveConfig();
	if (moveConfig.moveLayer == document.body) {
		return this.setPosition(x, y);
	}
	var msgValue = null;
	//_parseCoordinate takes the coordinate value and first of all
	//tryes to return the numeric representation in pixels.
	//Also if it returns null value (false, "", 0 are also
	//treated so) method stops execution without making something.
	//This can be useful for implementing restrictions.
	if (x || x === 0) {
		msgValue = x;
		x = this._parseCoordinate(x, "x", document.body);
		if (!x && x !== 0) {
			Zapatec.Log({description : "The X page coordinate " + msgValue + " can not be set for object with ID '" + this.id + "'!", type : "warning"});
			return false;
		}
	}
	if (y || y === 0) {
		msgValue = y;
		y = this._parseCoordinate(y, "y", document.body);
		if (!y && y !== 0) {
			Zapatec.Log({description : "The Y page coordinate " + msgValue + " can not be set for object with ID '" + this.id + "'!", type : "warning"});
			return false;
		}
	}
	var pos = Zapatec.Utils.getElementOffset(moveConfig.moveLayer);
	return this.setPosition((x || x === 0) ? (x - pos.x) : x, (y || y === 0) ? (y - pos.y) : y);
};

/**
 * Sets the position of the object relatively
 * to the client area of the browser.
 * @param x {number or string} X coordinate on the screen.
 * @param y {number or string} Y coordinate on the screen.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Movable.setScreenPosition = function(x, y) {
	var moveConfig = this.getMoveConfig();
	var msgValue = null;
	//_parseCoordinate takes the coordinate value and first of all
	//tryes to return the numeric representation in pixels.
	//Also if it returns null value (false, "", 0 are also
	//treated so) method stops execution without making something.
	//This can be useful for implementing restrictions.
	if (x || x === 0) {
		msgValue = x;
		x = this._parseCoordinate(x, "x", window);
		if (!x && x !== 0) {
			Zapatec.Log({description : "The X screen coordinate " + msgValue + " can not be set for object with ID '" + this.id + "'!", type : "warning"});
			return false;
		}
	}
	if (y || y === 0) {
		msgValue = y;
		y = this._parseCoordinate(y, "y", window);
		if (!y && y !== 0) {
			Zapatec.Log({description : "The Y screen coordinate " + msgValue + " can not be set for object with ID '" + this.id + "'!", type : "warning"});
			return false;
		}
	}
	if (moveConfig.moveLayer != document.body) {
		var pos = Zapatec.Utils.getElementOffset(moveConfig.moveLayer);
	} else {
		var pos = {x : 0, y : 0};
	}
	return this.setPosition((x || x === 0) ? (x - pos.x) : x, (y || y === 0) ? (y - pos.y) : y);
};

/**
 * Moves the object for the given offset from
 * the place it is currently in.
 * @param offsetLeft {number} the offset value for X coordinate.
 * @param offsetTop {number} the offset value for Y coordinate.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Movable.moveFor = function(offsetLeft, offsetTop) {
	var pos = this.getPosition();
	return this.setPosition(
		offsetLeft == null ? null : pos.x + offsetLeft, 
		offsetTop == null ? null : pos.y + offsetTop
	);
};

/**
 * Returns the position of the main element.
 * If there is no such, we take the one that is
 * the most left and top :).
 * This value is offset from movable parent.
 * @return {object} object with x and y properties.
 */
Zapatec.Movable.getPosition = function() {
	//getMovableMeasurement returns the element
	//that is the measurement of moving for all others.
	var el = this.getMovableMeasurement();
	//if we succeed with this lets return coords
	if (Zapatec.isHtmlElement(el) || 
	    (el && typeof el == "object" && typeof el.x == "number" && typeof el.y == "number")) {
			return Zapatec.Utils.getPos(el) || el;
	}
	//otherwise reporting failure
	Zapatec.Log({description : "Can't calculate position for object with ID '" + this.id + "'!", type : "warning"});
	return false;
};

/**
 * Gets the position of the element on the page.
 * @return {object} object with x and y properties.
 */
Zapatec.Movable.getPagePosition = function() {
	//getMovableMeasurement returns the element
	//that is the measurement of moving for all others.
	var el = this.getMovableMeasurement();
	//if we succeed with this lets return coords
	if (Zapatec.isHtmlElement(el) || 
	    (el && typeof el == "object" && typeof el.x == "number" && typeof el.y == "number")) {
			return Zapatec.Utils.getElementOffset(el) || el;
	}
	//otherwise reporting failure
	Zapatec.Log({description : "Can't calculate screen position for object with ID '" + this.id + "'!", type : "warning"});
	return false;
};

/**
 * Gets the position of the element on the screen.
 * @return {object} object with x and y properties.
 */
Zapatec.Movable.getScreenPosition = function() {
	//getting page position
	var pos = this.getPagePosition();
	//excluding scrolling
	pos.x -= Zapatec.Utils.getPageScrollX();
	pos.y -= Zapatec.Utils.getPageScrollY();
	//returning position
	return pos;
};

/**
 * Strats the event of moving. This is used
 * to send message to interested objects that
 * moving was started. This involves the calling
 * of "onMove" event from setPosition.
 * endMove method stops the action.
 * @return {boolean} true if success, otherwise false
 */
Zapatec.Movable.startMove = function() {
	//preparations which can depend on the Widget.
	if (!this.isMovableSafely()) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' was not prepared for moving! Use obj.makeMovable() to do so!", type : "warning"});
		return false;
	}
	this.fireEvent("moveStart");
	Zapatec.GlobalEvents.fireEvent("moveStart", this);
	this._setMovingState(true);
	return true;
};

/**
 * Ends the event of moving. This is used
 * to send message to interested objects that
 * moving was ended. This stops the calling
 * of "onMove" event from setPosition.
 * @return {boolean} true if success, otherwise false
 */
Zapatec.Movable.endMove = function() {
	if (!this.isMoving()) {
		Zapatec.Log({description : "The moving for object with ID '" + this.id + "' was not started!", type : "warning"});
		return false;
	}
	this.fireEvent("moveEnd");
	Zapatec.GlobalEvents.fireEvent("moveEnd", this);
	this._setMovingState(false);
	return true;
};

/**
 * Gets the state of moving. It returns
 * true if moving was started, otherwise
 * it returns false.
 * @return {boolean} true if moving started, otherwise false
 */
Zapatec.Movable.isMoving = function() {
	return this.movingState;
};

/**
 * Sets the state of moving to true or false.
 * This is used internally and can be overwritten
 * in child classes to use another propety for the flag.
 */
Zapatec.Movable._setMovingState = function(on) {
	this.movingState = on;
};

/**
 * Is object prepared for moving.
 * Mostly you will need to redefine this method,
 * to use some other property as a flag.
 * @return {boolean} true if prepared, otherwise false.
 */
Zapatec.Movable.isMovableSafely = function() {
	return this.safelyMovable;
};

/**
 * Fills some flag with the true or false value.
 * Same flag should be used in isMovableSafely method.
 * @param on {boolean} turn on or turn of.
 */
Zapatec.Movable._setMovableSafely = function(on) {
	this.safelyMovable = on;
};

/**
 * Makes all movable elements safely movable.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Movable.makeMovable = function() {
	if (!this.hasInterface("Zapatec.CommandEvent")) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' has no Zapatec.CommandEvent interface!"});
		return false;
	}
	if (this.isMovableSafely()) {
		return true;
	}
	var elements = Zapatec.Array(this.getMovableElements()), self = this,
	moveConfig = this.getMoveConfig();
	//trying to prepare each element
	success = elements.each(function(index, movable) {
		if (Zapatec.isHtmlElement(movable)) {
			//prepareing HTML elements
			if (moveConfig.preserveSizes &&
			    !Zapatec.Utils.makeSafelySizable(movable)) {
					return "break";
			}
			if (!Zapatec.Utils.makeSafelyMovable(movable, moveConfig.moveLayer)) {
				return "break";
			}
			self.createProperty(movable, "moveObj", self);
		} else if (Zapatec.isMovableObj(movable)) {
			//prepareing movable objects
			if (!movable.makeMovable()) {
				return "break";
			}
		}
	});
	//if there was failure we need roll back all the preparations
	if (!success) {
		//restoreing elements or objects
		this.restoreOfMove();
		//reporting error
		Zapatec.Log({description : "Can not make the object with ID '" + this.id + "' movable!"});
		return false;
	}
	//reporting success
	this._setMovableSafely(true);
	return true;
};

/**
 * Restoreing object of movable state.
 * @return {boolean} always returns true.
 */
Zapatec.Movable.restoreOfMove = function() {
	if (!this.isMovableSafely()) {
		return true;
	}
	//getting all movable elements
	var elements = Zapatec.Array(this.getMovableElements());
	var self = this;
	//restoreing each item
	elements.each(function(index, movable) {
		if (Zapatec.isHtmlElement(movable)) {
			Zapatec.Utils.restoreOfMove(movable);
			if (self.getMoveConfig().preserveSizes) {
				Zapatec.Utils.restoreOfSizing(movable);
			}
			movable.moveObj = null;
		} else if (Zapatec.isMovableObj(movable)) {
			movable.restoreOfMove();
		}
	}, true);
	//returning success
	this._setMovableSafely(false);
	return true;
};

/**
 * Returns the measurement element for this movable
 * object. This a generous method, and mostly will
 * be overwritten in implementing class.
 * @return {null or number or HTML element} method should return
 * null if there is no measure, or number or HTML element to 
 * calculate measurement.
 */
Zapatec.Movable.getMovableMeasurement = function() {
	return this.getContainer();
};

/**
 * Returns the configuration object for movement.
 * @return {object} configuration object.
 */
Zapatec.Movable.getMoveConfig = function() {
	return this.getConfiguration();
};

/**
 * Sets the moving configuration.
 * @param config {object} a set of new configuration.
 */
Zapatec.Movable.setMoveConfig = function(config) {
	this.reconfigure(config);
};

/**
 * Returns the array of elements to be moved in this
 * object. This a generous method, and mostly will
 * be overwritten in implementing class.
 * @return {mixed} method should return null if there is no movable 
 * elements, or array of HTML elements or Zapatec.Movable objects
 * to size them or get their size or prepare them.
 */
Zapatec.Movable.getMovableElements = function() {
	return this.getContainer();
};

/**
 * Returns container for this object.
 */
Zapatec.Movable.getContainer = function() {
	return this.getMoveConfig().container;
};

/**
 * Parses the value of the coord argument
 * to calculate the number in pixels.
 * If this method returns null or false
 * or "" or 0 nothing is done at all.
 * @param coord {string} string representation of the coord.
 * @param dimension {string} dimension in which we should parse.
 * @return {number or null} null if nothing should be done 
 * by setPosition method, otherwise returns the coordinate in pixels.
 */
Zapatec.Movable._parseCoordinate = function(coord, dimension, within) {
	switch (true) {
		case (typeof coord == "number") : {
			if (within == window) {
				coord += Zapatec.Utils["getPageScroll" + dimension.toUpperCase()]();
			}
			break;
		}
		case ((/^\d+px$/).test(String(coord))) : {
			coord = parseInt(coord, 10);
			if (within == window) {
				coord += Zapatec.Utils["getPageScroll" + dimension.toUpperCase()]();
			}
			break;
		}
		case ((/^(left|top|bottom|right|center)$/i).test(String(coord))) : {
			coord = this._parseWordCoordinate(coord, dimension, within);
			break;
		}
	}
	return this._canSetCoordinate(coord, dimension, within);
};

/**
 * Parses coordinate that is represented with one of the 
 * following words : "left", "top", "right", "bottom", "center".
 * All of the words (except center) have special meaning for
 * upper case. If lower case means top to top (left to left, etc.), 
 * then upper case means bottom to top.
 * @param coord {string} string representing coordinate.
 * @param dimension {string} dimension in which we parse.
 * @param within {HTML element} this is the basis on which we parse.
 * @return {number or false} numeric representation of the coordinate
 * or false if can not parse.
 */
Zapatec.Movable._parseWordCoordinate = function(coord, dimension, within) {
	//preventing errors
	if ((/(left|right)/i).test(String(coord)) && dimension.toUpperCase() != "X") {
		return false;
	}
	if ((/(top|bottom)/i).test(String(coord)) && dimension.toUpperCase() != "Y") {
		return false;
	}
	//additional helpfull strings to make code shorter
	var dim = (dimension.toUpperCase() == "X") ? "Left" : "Top";
	var sizeDim = (dimension.toUpperCase() == "X") ? "Width" : "Height";
	//at first leave it as zero
	var parsedCoord = 0;
	//getting needed within size for calculating coordinate
	var wSize = null;
	if (Zapatec.isHtmlElement(within)) {
		//include scrolling
		parsedCoord = within["scroll" + dim];
		//getting size
		wSize = Zapatec.Utils["get" + sizeDim](within);
	} else if (within == window) {
		//include scrolling
		parsedCoord = Zapatec.Utils["getPageScroll" + dimension.toUpperCase()]();
		//getting size
		wSize = Zapatec.Utils.getWindowSize()[sizeDim.toLowerCase()];
	} else if (within && typeof within == "object") {
		//changing coordinate
		parsedCoord += within["scroll" + dim] || 0;
		//getting size
		wSize = within[sizeDim.toLowerCase()];
	} else {
		return false;
	}
	//getting measurement
	var measurement = this.getMovableMeasurement();
	//getting needed measurement size for calculating coordinate
	var mSize = null;
	if (Zapatec.isHtmlElement(measurement)) {
		mSize = Zapatec.Utils["get" + sizeDim](measurement);
	} else if (measurement && typeof measurement == "object") {
		mSize = measurement[sizeDim.toLowerCase()];
	}
	//calculating actual coordinate
	switch (coord) {
		case "left" : case "top" : {
			break;
		}
		case "LEFT" : case "TOP" : {
			parsedCoord -= mSize;
			break;
		}
		case "right" : case "bottom" : {
			parsedCoord += wSize - mSize;
			break;
		}
		case "RIGHT" : case "BOTTOM" : {
			parsedCoord += wSize;
			break;
		}
		case "center" : {
			parsedCoord += Math.round((wSize - mSize) / 2);
			break;
		}
		case "CENTER" : {
			parsedCoord += Math.round(wSize / 2);
			break;
		}
		default : {
			parsedCoord = null;
			break;
		}
	}
	//returning result
	if (!parsedCoord && parsedCoord !== 0) {
		return false;
	} else {
		return parsedCoord;
	}
};

/**
 * This method restricts coordinate setting.
 * @param coord {string} coordinate.
 * @param dimension {string} dimension in which we check.
 * @param within {HTML element} this is the basis on which we check.
 * @return {number or false} the coordinate or false if can not set.
 */
Zapatec.Movable._canSetCoordinate = function(coord, dimension, within) {
	if (typeof coord != "number") {
		return false;
	}
	//getting move configuration
	var moveConfig = this.getMoveConfig();
	//we don't check for other withins than moveLayer
	if (within != moveConfig.moveLayer) {
		return coord;
	}
	//getting limit object
	var limitObj = moveConfig.limit;
	//coordinate dimension ("X" or "Y") to be used for getting values
	var dim = dimension.toUpperCase();
	//direction of possible movement
	var direction = moveConfig.direction.toLowerCase();
	//getting corrections to restriction due to moving shape
	var correction = this._getMovableShape();
	//if dimension is opposite to possible direction of movement
	//returning overflow handling
	if (dim == "X" && direction == "vertical") {
		return this._handleCoordOverflow(this.getPosition().x);
	} else if (dim == "Y" && direction == "horizontal") {
		return this._handleCoordOverflow(this.getPosition().y);
	}
	//checking the lower bound
	if ((limitObj["min" + dim] || limitObj["min" + dim] === 0) && coord < limitObj["min" + dim] + correction["min" + dim]) {
		return this._handleCoordOverflow(limitObj["min" + dim] + correction["min" + dim]);
	}
	//checking the upper bound
	if ((limitObj["max" + dim] || limitObj["max" + dim] === 0) && coord > limitObj["max" + dim] + correction["max" + dim]) {
		return this._handleCoordOverflow(limitObj["max" + dim] + correction["max" + dim]);
	}
	//just returning value
	return coord;
};

/**
 * A method to handle coordinate overflow of the limit.
 * @param limit {number} limit that was overflowed.
 * @param dimension {string} dimension of coordinate.
 * @return {boolean or number} false if can not set,
 * otherwise new coordinate.
 */
Zapatec.Movable._handleCoordOverflow = function(limit, dimension) {
	return false;
};

/**
 * Returns the object with 4 properties. Each
 * of them means the correction to upper or lower
 * measure of the limit.
 * @return {object} object with minX, maxX, minY and maxY properties.
 */
Zapatec.Movable._getMovableShape = function() {
	//getting measurement element
	var measurement = this.getMovableMeasurement();
	//default corrections are equal 0
	var obj = {minX : 0, maxX : 0, minY : 0, maxY : 0};
	//currently we only support true value
	switch (this.getMoveConfig().followShape) {
		case (true) : {
			//not an HTML element no corrections
			if (!Zapatec.isHtmlElement(measurement)) {
				return obj;
			}
			//correcting upper bounds
			obj.maxX = -Zapatec.Utils.getWidth(measurement);
			obj.maxY = -Zapatec.Utils.getHeight(measurement);
			return obj
		}
		default : {
			return obj;
		}
	}
};

/**
 * Returns the coordinate of the moving point.
 * Its the point which is thought to be the 
 * center of movement.
 * @return {object} object with x and y properties.
 */
Zapatec.Movable.getMovingPoint = function() {
	return this.getScreenPosition();
};

/**
 * Iterates through all the elements in the
 * array and tryes to set their coordinates.
 * @param x {number} x coordinate.
 * @param y {number} y coordinate.
 * @param elements {array} array of elements to proceed.
 */
Zapatec.Movable._proceedElementsCoords = function(x, y, elements) {
	//making sure this is Zapatec.Array
	elements = Zapatec.Array(elements);
	//calculateing measurement position
	var measurement = this.getMovableMeasurement();
	var pos = null;
	if (Zapatec.isHtmlElement(measurement)) {
		pos = Zapatec.Utils.getPos(measurement);
	} else if (measurement && typeof measurement == "object") {
		pos = {
			x : (typeof measurement.x == "number") ? measurement.x : null, 
			y : (typeof measurement.y == "number") ? measurement.y : null
		};
		if (!measurement.x || !measurement.y) {
			pos = null;
		}
	}
	//iterating through all items
	elements.each(function(index, movable) {
		var mX = x, mY = y;
		//if there is measurement calculating position
		if (pos) {
			var mPos = null;
			if (Zapatec.isHtmlElement(movable)) {
				mPos = Zapatec.Utils.getPos(movable);
			} else if (Zapatec.isMovableObj(movable)) {
				mPos = movable.getPosition();
			} else {
				return;
			}
			if (x || x === 0) {
				mX = mPos.x + (x - pos.x);
			}
			if (y || y === 0) {
				mY = mPos.y + (y - pos.y);
			}
		}
		//setting position
		if (Zapatec.isHtmlElement(movable)) {
			Zapatec.Utils.moveTo(movable, mX, mY);
		} else if (Zapatec.isMovableObj(movable)) {
			movable.setPosition(mX, mY);
		}
	});
};

/**
 * Sorts elements in array by their path in the DOM
 * tree. This is usefull when you have synchronized 
 * elements that influence each other in the DOM tree
 * when you try to make them movable. The sort order
 * is descending.
 * @param elements {array} array of elements to be sorted.
 * @return {boolean} true if successful, otherwise false.
 */
Zapatec.Movable.sortElementsByPath = function(elements) {
	//making array of elements Zapatec.Array
	elements = Zapatec.Array(elements);
	//iterating through each item to get path and compare with others
	elements.each(function(index, element) {
		//no path - no action for this element
		var path = Zapatec.Utils.getElementPath(element, document.body);
		if (!path) {
			return;
		}
		//splitting path into array
		path = Zapatec.Array(path.split("-"));
		//compareing path with path of all other elements
		for(var i = index; i < elements.length; ++i) {
			//no path - no action for this element
			var compPath = Zapatec.Utils.getElementPath(elements[i], document.body);
			if (!compPath) {
				continue;
			}
			//splitting path into array
			compPath = Zapatec.Array(compPath.split("-"));
			//compareing parts of both pahs
			path.each(function(k, pathPiece, whole) {
				//if our part is bigger than breaking the cycle
				if (parseInt(pathPiece, 10) > (parseInt(compPath[k], 10) || 0)) {
					return "break";
				} else if (parseInt(pathPiece, 10) < parseInt(compPath[k], 10)) {
					//if our part is smaller then exchanging elements with
					//their places in array and breaking the cycle
					var el = elements[index]
					elements[index] = elements[i];
					elements[i] = el;
					path = compPath;
					return "break";
				}
				//continuing compareing if parts are equal
			});
		}
	});
	return true;
};

/**
 * This is Zapatec.Utils.Movable object definition.
 * This is done in the "interface" manner.
 * It represents most of the routines and
 * events connected to moving of the object.
 * @param config {object} - all parameters are passed as the properties of this object.
 * 
 * Constructor recognizes the following properties of the config object
 * \code
 *    prop. name     | description
 *  -------------------------------------------------------------------------------------------------
 *  container        | {HTML element} The main element to be movable (default null).
 *  synchronize      | {array} Elements to be synchronize in moving (default []).
 *  direction        | {string} Enabled direction of moving. Can be "vertical", "horizontal" or "both"
 *                   | (default "both").
 *  limit            | {object} Object with properties "minX", "maxX", "minY" and "maxY",
 *                   | that are used as restrictions for setting position.
 *  moveLayer        | {HTML element} Element to be used as movement parent (default document.body).
 *
 * \endcode
 */
Zapatec.Utils.Movable = function(config) {
	Zapatec.Utils.Movable.SUPERconstructor.call(this, config);
};

Zapatec.Utils.Movable.id = "Zapatec.Utils.Movable";
Zapatec.inherit(Zapatec.Utils.Movable, Zapatec.Widget);
//implementing Zapatec.CommandEvent interface
Zapatec.implement(Zapatec.Utils.Movable, "Zapatec.CommandEvent");
//implementing Zapatec.Movable interface
Zapatec.implement(Zapatec.Utils.Movable, "Zapatec.Movable");

/**
 * Inits the object with set of config options.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Movable.prototype.init = function(config) {
	//calling parent init
	Zapatec.Utils.Movable.SUPERclass.init.call(this, config);
	var self = this;
	var elements = null;
	//makes object movable (means all elements that are movable)
	if (this.getConfiguration().makeMovable) {
		this.makeMovable();
	} else {
		elements = Zapatec.Array(this.getMovableElements());
		elements.each(function(index, movable) {
			if (Zapatec.isHtmlElement(movable)) {
				self.createProperty(movable, "moveObj", self);
			}
		});
	}
};

/**
 * Sets the default configuration of the object and
 * inits it with user defined values.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Movable.prototype.configure = function(config) {
	//set of elements to be moved with container
	this.defineConfigOption("synchronize", []);
	//container, which is actually moved too and also 
	//is used as a measurement
	this.defineConfigOption("container", null);
	//limitations of the moving
	this.defineConfigOption("limit", {
		minX : null, 
		maxX : null, 
		minY : null, 
		maxY : null
	});
	//direction of move (you can enable move only in one direction)
	this.defineConfigOption("direction", "both");
	//moving layer, the element to be used as offset parent
	this.defineConfigOption("moveLayer", document.body);
	//restriction shape for the element
	this.defineConfigOption("followShape", "LT");
	//should we preserve element's sizes
	this.defineConfigOption("preserveSizes", true);
	//if false, init doesn't make element movable
	this.defineConfigOption("makeMovable", true);
	//no theme
	this.defineConfigOption("theme", null);
	// Call parent method
	Zapatec.Utils.Movable.SUPERclass.configure.call(this, config);
	config = this.getConfiguration();
	//checking if limit object wasn't overwritten
	if (!config.limit || typeof config.limit != "object") {
		config.limit = {
			minX : null, 
			maxX : null, 
			minY : null, 
			maxY : null
		};
	}
	var self = this;
	//making sure that config.syncronize will be Array.
	config.synchronize = Zapatec.Array(config.synchronize);
	config.synchronize.each(function(index, element) {
		if (element === null) {
			return;
		}
		element = Zapatec.Widget.getElementById(element);
		element = Zapatec.Utils.img2div(element);
		if (!element) {
			Zapatec.Log({description : "Wrong element in synchronize array for the movable object with ID '" + self.id + "'!"});
		} else {
			config.synchronize[index] = element;
		}
	});
	//setting default container element
	config.container = Zapatec.Widget.getElementById(config.container);
	config.container = Zapatec.Utils.img2div(config.container);
	if (!Zapatec.isHtmlElement(config.container)) {
		Zapatec.Log({description : "Wrong element passed as container for the movable object with ID '" + self.id + "'!"});	
	}
	this.movableElements = Zapatec.Array(config.synchronize.concat(config.container));
	if (this.movableElements.length > 1) {
		this.sortElementsByPath(this.movableElements);
	}
	//setting default move layer
	if (!Zapatec.isHtmlElement(config.moveLayer = Zapatec.Widget.getElementById(config.moveLayer))) {
		config.moveLayer = document.body;
	}
};

/**
 * Reconfigures the object with new parameters.
 * @param config {object} new configuration parameters.
 */
Zapatec.Utils.Movable.prototype.reconfigure = function(config) {
	// Call parent method
	Zapatec.Utils.Movable.SUPERclass.reconfigure.call(this, config);
};

/**
 * Returns the array of synchronized moving elements.
 * @return {array} array of elements that are synchronized
 */
Zapatec.Utils.Movable.prototype.getMovableElements = function() {
	return this.movableElements;
};
