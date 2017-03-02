//$Id: sizable.js 6834 2007-04-02 06:49:19Z slip $
/**
 * This is a set of functionality used for sizing object
 * and is implemented in interface (mixin) manner.
 */
Zapatec.Sizable = {};

/**
 * Sets the width of the object. Basically the width parameter
 * is connected to the measurement element and its resulting width
 * will be equal to this value, but it doesn't mean that the
 * method will directly set the width of it, it can work with
 * it's childs actually.
 * @param width {number} width to set.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Sizable.setWidth = function(width) {
	return this._setDimension(width, "width");
};

/**
 * Sets the height of the object. Basically the height parameter
 * is connected to the measurement element and its resulting height
 * will be equal to this value, but it doesn't mean that the
 * method will directly set the height of it, it can work with
 * it's childs actually.
 * @param height {number} height to set.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Sizable.setHeight = function(height) {
	return this._setDimension(height, "height");
};

/**
 * This method is used for the objects that use
 * such concept as orientation. Method reacts on
 * two types of orientation: "vertical" and "horizontal".
 * @param width {number} oriented width to set.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Sizable.setOrientedWidth = function(width) {
	//getOrientation should return the orientation of the
	//object. You can extend two types overwriting
	//handleOrientedWidth method.
	if (!this.getOrientation) {
		return false;
	}
	switch (this.getOrientation()) {
		case "vertical" : 
			//vertical orientation means height
			return this._setDimension(width, "height");
		case "horizontal" : 
			//horizontal orientation means width
			return this._setDimension(width, "width");
	}
};

/**
 * This method is used for the objects that use
 * such concept as orientation. Method reacts on
 * two types of orientation: "vertical" and "horizontal".
 * @param height {number} oriented height to set.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Sizable.setOrientedHeight = function(height) {
	//getOrientation should return the orientation of the
	//object. You can extend two types overwriting
	//handleOrientedHeight method.
	if (!this.getOrientation) {
		return false;
	}
	switch (this.getOrientation()) {
		case "vertical" :
			//vertical orientation means width
			return this._setDimension(height, "width");
		case "horizontal" :
			//horizontal orientation means height
			return this._setDimension(height, "height");
	}
};

/**
 * Sets the size of the passed dimension.
 * @param val {number or string} value to set.
 * @param dimension {string} "width" or "height".
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Sizable._setDimension = function(val, dimension) {
	//same string just with first character uper case, it is used for event names
	var evDim = dimension.charAt(0).toUpperCase() + dimension.slice(1);
	//preparations which can depend on the Widget.
	if (!this.isSizableSafely(dimension)) {
		Zapatec.Log({description : "The object " + dimension + " ID '" + this.id + "' was not prepared for sizing! Use obj.makeSizable() to do so!", type : "warning"});
		return false;
	}
	//_parseSize takes the width value and first of all
	//tryes to return the numeric representation in pixels.
	//Also if it returns null value (false, "", 0 are also
	//treated so) method stops execution without making something.
	//This can be useful for implementing restrictions.
	var msgValue = val + "";
	val = this._parseSize(val, dimension);
	if (!val) {
		Zapatec.Log({description : "The " + dimension + " " + msgValue + " can not be set for object with ID '" + this.id + "'!", type : "warning"});
		return false;
	}
	//getSizableElements returns the array of elements
	//that actually are sized. Param passed is the dimension 
	//we size.
	var elements = Zapatec.Array(this.getSizableElements(dimension));
	//this array is used for saving previous widths and associated sizable objects,
	//this way we can restore all widths, if sizing fails on some stage
	var toRestore = [], self = this;
	//firing event
	if (this.fireEvent("before" + evDim + "Change", val) === false) {
		return false;
	}
	if (Zapatec.GlobalEvents.fireEvent("before" + evDim + "Change", val, this) === false) {
		return false;
	}
	//checking if we didn't fail during the loop,
	if (!this._proceedElementsSizes(val, dimension, elements, toRestore)) {
		//if failure we need to return back all heights
		this._rollBackSizing(toRestore, dimension);
		this.fireEvent(dimension + "ChangeFailure", val)
		Zapatec.GlobalEvents.fireEvent(dimension + "ChangeFailure", val, this)
		Zapatec.Log({description : "Impossible to set the " + dimension + " " + msgValue + " for the object with ID '" + this.id + "'!", type : "warning"});
		return false;
	} else {
		if (this.isSizing()) {
			if (dimension == "width") {
				this.fireEvent("onSizing", val, this.getHeight());
				Zapatec.GlobalEvents.fireEvent("onSizing", val, this.getHeight(), this);
			} else {
				this.fireEvent("onSizing", this.getWidth(), val);
				Zapatec.GlobalEvents.fireEvent("onSizing", this.getWidth(), val, this);
			}
		}
		this.fireEvent("on" + evDim + "Change", val);
		Zapatec.GlobalEvents.fireEvent("on" + evDim + "Change", val, this);
	}
	//reporting success.
	return true;
};

/**
 * Strats the event of sizing. This is used
 * to send message to interested objects that
 * sizing was started. This involves the calling
 * of "onSizing" event from setWidth and setHeight.
 * endSizing method stops the action.
 * @return {boolean} true if success, otherwise false
 */
Zapatec.Sizable.startSizing = function() {
	//preparations which can depend on the Widget.
	if (!this.isSizableSafely()) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' was not prepared for sizing! Use obj.makeSizable() to do so!", type : "warning"});
		return false;
	}
	this.fireEvent("sizingStart");
	Zapatec.GlobalEvents.fireEvent("sizingStart", this);
	this._setSizingState(true);
	return true;
};

/**
 * Ends the event of sizing. This is used
 * to send message to interested objects that
 * sizing was ended. This stops the calling
 * of "onSizing" event from setWidth and setHeight.
 * @return {boolean} true if success, otherwise false
 */
Zapatec.Sizable.endSizing = function() {
	if (!this.isSizing()) {
		Zapatec.Log({description : "The sizing for object with ID '" + this.id + "' was not started!", type : "warning"});
		return false;
	}
	this.fireEvent("sizingEnd");
	Zapatec.GlobalEvents.fireEvent("sizingEnd", this);
	this._setSizingState(false);
	return true;
};

/**
 * Gets the state of sizing. It returns
 * true if sizing was started, otherwise
 * it returns false.
 * @return {boolean} true if sizing started, otherwise false
 */
Zapatec.Sizable.isSizing = function() {
	return this.sizingState;
};

/**
 * Sets the state of sizing to true or false.
 * This is used internally and can be overwritten
 * in child classes to use another propety for the flag.
 */
Zapatec.Sizable._setSizingState = function(on) {
	this.sizingState = on;
};

/**
 * Returns the width of the measurement element.
 * If there is no such, then the biggest width
 * of sizable elements will be taken.
 * @return {number or boolean} the width or false iif failure.
 */
Zapatec.Sizable.getWidth = function() {
	//trying to get measurement of sizing
	var el = this.getSizableMeasurement("width");
	//if we success then return its width or its value
	if (Zapatec.isHtmlElement(el) || typeof el == "number") {
		return Zapatec.Utils.getWidth(el) || el;
	}
	//otherwise reporting failure
	Zapatec.Log({description : "Can't calculate width for object with ID '" + this.id + "'!", type : "warning"});
	return false;
};

/**
 * Returns the height of the measurement element.
 * If there is no such, then the biggest height
 * of sizable elements will be taken.
 * @return {number or boolean} the height or false iif failure.
 */
Zapatec.Sizable.getHeight = function() {
	//trying to get measurement of sizing
	var el = this.getSizableMeasurement("height");
	//if we success then return its height or its value
	if (Zapatec.isHtmlElement(el) || typeof el == "number") {
		return Zapatec.Utils.getHeight(el) || el;
	}
	//otherwise reporting failure
	Zapatec.Log({description : "Can't calculate height for object with ID '" + this.id + "'!", type : "warning"});
	return false;
};

/**
 * Is object prepared for sizing.
 * Mostly you will need to redefine this method,
 * to use some other property as a flag.
 * @return {boolean} true if prepared, otherwise false.
 */
Zapatec.Sizable.isSizableSafely = function(dimension) {
	return this.safelySizable;
};

/**
 * Makes all sizable elements safely sizable.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Sizable.makeSizable = function() {
	if (!this.hasInterface("Zapatec.CommandEvent")) {
		Zapatec.Log({description : "The object with ID '" + this.id + "' has no Zapatec.CommandEvent interface!"});
		return false;
	}
	if (this.isSizableSafely()) {
		return true;
	}
	//getting array of elements which should be sizable
	//without parameters this function should always
	//return all possible sizable elements
	var elements = Zapatec.Array(this.getSizableElements()), 
	success = true, self = this;
	//trying to prepare each element
	elements.each(function(index, sizable) {
		sizable = Zapatec.Array(sizable);
		sizable.each(function(index, sizable) {
			if (Zapatec.isHtmlElement(sizable)) {
				//prepareing HTML elements
				if (!Zapatec.Utils.makeSafelySizable(sizable)) {
					success = false;
					return "break";
				}
				self.createProperty(sizable, "sizingObj", self);
			} else if (Zapatec.isSizableObj(sizable)) {
				//prepareing sizable objects
				if (!sizable.makeSizable()) {
					success = false;
					return "break";
				}
			}
		});
	});
	//if there was failure we need roll back all the preparations
	if (!success) {
		//restoreing elements or objects
		this.restoreOfSizing();
		//reporting error
		Zapatec.Log({description : "Can not make the object with ID '" + this.id + "' sizable!"});
		return false;
	}
	//reporting success
	this._setSizableSafely(true);
	return true;
};

/**
 * Restoreing object of sizable state.
 * @return {boolean} always returns true.
 */
Zapatec.Sizable.restoreOfSizing = function() {
	if (!this.isSizableSafely()) {
		return true;
	}
	//getting all sizable elements
	var elements = Zapatec.Array(this.getSizableElements());
	//restoreing each item
	elements.each(function(index, sizable) {
		sizable = Zapatec.Array(sizable);
		sizable.each(function(index, sizable) {
			if (Zapatec.isHtmlElement(sizable)) {
				Zapatec.Utils.restoreOfSizing(sizable);
				sizable.sizingObj = null;
			} else if (Zapatec.isSizableObj(sizable)) {
				sizable.restoreOfSizing();
			}
		});
	});
	//returning success
	this._setSizableSafely(false);
	return true;
};

/**
 * Replaces the element with the given
 * one. The given one will be made
 * sizable.
 * @param element {HTML element} element to replace.
 * @param withEl {HTML element} replacement element.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Sizable.replaceWithSizable = function(element, withEl) {
	if (!Zapatec.isHtmlElement(element) || !Zapatec.isHtmlElement(withEl)) {
		return false;
	}
	var width = Zapatec.Utils.getWidth(element);
	var height = Zapatec.Utils.getHeight(element);
	element.parentNode.insertBefore(withEl, element.nextSibling);
	element.parentNode.removeChild(element);
	if (!Zapatec.Utils.makeSafelySizable(withEl)) {
		return false;
	}
	if (Zapatec.Utils.setWidth(withEl, width) && Zapatec.Utils.setHeight(withEl, height)) {
		return true;
	}
	return false;
};

/**
 * Fills some flag with the true or false value.
 * Same flag should be used in isSafelySizable method.
 * @param on {boolean} turn on or turn of.
 */
Zapatec.Sizable._setSizableSafely = function(on) {
	this.safelySizable = on;
};

/**
 * Parses the value of the size argument
 * to calculate the number in pixels.
 * if this method returns null or false
 * or "" or 0 nothing is done at all.
 * Currently recognizes "auto", px and % size.
 * @param width {string} string representation of the size.
 * @param dimension {string} dimension in which we should parse.
 * @return {number or null} null if nothing should be done 
 * by setWidth or setHeight method, otherwise returns the size in pixels.
 */
Zapatec.Sizable._parseSize = function(size, dimension) {
	switch (true) {
		case (size == "auto") : {
			size = this._parseAutoSize(dimension);
			break;
		}
		case ((/^\d+px$/).test(String(size))) : {
			size = parseInt(size, 10);
			break;
		}
		case ((/^\d+%$/).test(String(size))) : {
			size = this._parsePercentSize(size, dimension);
			break;
		}
		case (typeof size == "number") : {
			break;
		}
	}
	return this._canSetSize(size, dimension);
};

/**
 * Returns the value to be used as size in pixels for 
 * the measure element used for auto sizing the element
 * returned by _getAutoSizableElement.
 * @param dimension {string} dimension to parse for value.
 * @return {number or null} needed width or null if can not
 * do this.
 */
Zapatec.Sizable._parseAutoSize = function(dimension) {
	//getting measurement and autosizable element. Auto sizable means
	//we'll try to size element to fit this elements content.
	var measurement = this.getSizableMeasurement(dimension);
	var autoSizable = this._getAutoSizableElement(dimension);
	if (!Zapatec.isHtmlElement(autoSizable)) {
		return null;
	}
	var dim = dimension.charAt(0).toUpperCase() + dimension.slice(1).toLowerCase();
	//calculateing difference between this element and measurement (if there is one)
	var diff = 0;
	if (Zapatec.isHtmlElement(measurement) || typeof measurement == "number") {
		diff = (Zapatec.Utils["get" + dim](measurement) || measurement) - Zapatec.Utils["get" + dim](autoSizable);
	}
	//workaround for iframe
	var el = autoSizable;
	if (el.tagName.toLowerCase() == "iframe") {
		try {
			if (el.contentDocument != null) {
				el = el.contentDocument.body;
			} else if (el.contentWindow.document != null) {
				el = el.contentWindow.document.body;
			}
			if (!Zapatec.isHtmlElement(el)) {
				throw "No element to calculate auto size!";
			}
		} catch(e) {
			Zapatec.Log({description : "Can't calculate auto size for the IFRAME in the object with ID '" + this.id + "'!", type : "warning"});
			return null;
		}
	}
	//returning new size.
	return el["scroll" + dim] + diff;
};

/**
 * Returns the value to be used as size in pixels for 
 * the measure element used for sizing it in percents
 * to _getSizableParent.
 * @param value {string} value to parse.
 * @param dimension {string} dimension to parse in.
 * @return {number or null} needed width or null if can not
 * do this.
 */
Zapatec.Sizable._parsePercentSize = function(value, dimension) {
	//getting the parent element to calculate percents of its size.
	var sizableParent = this._getSizableParent(dimension);
	if (!Zapatec.isHtmlElement(sizableParent)) {
		return null;
	}
	var dim = dimension.charAt(0).toUpperCase() + dimension.slice(1).toLowerCase();
	value = parseInt(value, 10);
	//calculating percents
	return Math.round((value / 100) * Zapatec.Utils["get" + dim](sizableParent));
};

/**
 * Returns value if this value for this dimension can be set.
 * This is currently default method, but you can overwrite it 
 * for implementing your way of restriction.
 * @param value {string} value to check.
 * @param dimension {string} dimension to check.
 * @return {number or boolean} coordinate if the size can be set, 
 * otherwise false.
 */
Zapatec.Sizable._canSetSize = function(value, dimension) {
	if (typeof value != "number") {
		return false;
	}
	var dim = dimension.charAt(0).toUpperCase() + dimension.slice(1).toLowerCase();
	var sizingConfig = this.getSizingConfig();
	var limit = sizingConfig.limit;
	var direction = sizingConfig.direction;
	if (dim == "Width" && direction == "vertical") {
		return this._handleSizeOverflow(this.getWidth());
	}
	if (dim == "Height" && direction == "horizontal") {
		return this._handleSizeOverflow(this.getHeight());
	}
	if (typeof limit["min" + dim] == "number" && value < limit["min" + dim]) {
		return this._handleSizeOverflow(limit["min" + dim]);
	}
	if (typeof limit["max" + dim] == "number" && value > limit["max" + dim]) {
		return this._handleSizeOverflow(limit["max" + dim]);
	}
	return value;
};

/**
 * A method to handle size overflow of the limit.
 * @param limit {number} limit that was overflowed.
 * @param dimension {string} dimension of sizing.
 * @return {boolean or number} false if can not set,
 * otherwise new coordinate.
 */
Zapatec.Sizable._handleSizeOverflow = function(limit, dimension) {
	return false;
};

/**
 * Returns the configuration of sizable object.
 * @return {object} object with configuration.
 */
Zapatec.Sizable.getSizingConfig = function() {
	return this.getConfiguration();
};

/**
 * Sets the sizing configuration.
 * @param config {object} a set of new configuration.
 */
Zapatec.Sizable.setSizingConfig = function(config) {
	this.reconfigure(config);
};

/**
 * Gets the element to be used for getting size which fits
 * its content without overflow. It will mostly always 
 * be overwritten by your function.
 * @param dimension {string} dimension we are working with.
 * @return {HTML element} should always be HTML element.
 */
Zapatec.Sizable._getAutoSizableElement = function(dimension) {
	return this.getContainer();
};

/**
 * Gets the element to be used for getting size in percents
 * of its size. It will mostly always be overwritten by your function.
 * @param dimension {string} dimension we are working with.
 * @return {HTML element} should always be HTML element.
 */
Zapatec.Sizable._getSizableParent = function(dimension) {
	return this.getContainer().parentNode;
};

/**
 * Returns the measurement element for this sizable
 * object. This a generous method, and mostly will
 * be overwritten in implementing class.
 * @param dimension {string} string pointing to dimension that is
 * setting or calculating. You can use this to make difference
 * between dimensions.
 * @return {null or number or HTML element} method should return
 * null if there is no measure, or number or HTML element to 
 * calculate measurement.
 */
Zapatec.Sizable.getSizableMeasurement = function(dimension) {
	return this.getContainer();
};

/**
 * Returns the array of elements to be sized in this
 * object. This a generous method, and mostly will
 * be overwritten in implementing class.
 * @param dimension {string} string pointing to dimension that is
 * setting or calculating. You can use this to make difference
 * between dimensions.
 * @return {mixed} method should return null if there is no sizable 
 * elements, or array of HTML elements or Zapatec.Sizable objects
 * to size them or get their size or prepare them.
 */
Zapatec.Sizable.getSizableElements = function(dimension) {
	return this.getContainer();
};

/**
 * Returns the container element.
 * @return {HTML element} container element.
 */
Zapatec.Sizable.getContainer = function() {
	return this.getSizingConfig().container;
};

/**
 * Sets the size of the particular element.
 * @param size {number} the size to set.
 * @param sizable {mixed} element or object to be sized.
 * @param dimension {string} dimension to set.
 * @return {object or boolean} object with two properties: "sizable" - sizable object,
 * "oldSize" - its old size for restoring on failure; or it returns false if failure
 * or true if this was not sizable element.
 */
Zapatec.Sizable._setElementSize = function(size, sizable, dimension) {
	var dim = dimension.charAt(0).toUpperCase() + dimension.slice(1).toLowerCase();
	//we take each element
	if (Zapatec.isHtmlElement(sizable)) {
		//trying to set the height of the element
		if (Zapatec.Utils["set" + dim](sizable, size)) {
			return true;
		} else {
			return false;
		}
	} else if (Zapatec.isSizableObj(sizable)) {
		//trying to set the height of the Zapatec.Sizable object
		if (sizable["set" + dim](size)) {
			return true;
		} else {
			return false;
		}
	}

	return true;
};

/**
 * Proceeds the matrix (2dim array) of elements to size them accordingly.
 * @param value {number} value of the size to be proceed.
 * @param dimension {string} dimension to be proceed.
 * @param elArr {array} array of elements to be proceed.
 * @param restArr {array} array of elements to be restored if failure 
 * (it is filled during the method work).
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Sizable._proceedElementsSizes = function(value, dimension, elArr, restArr) {
	var diff = null, self = this, sizes = Zapatec.Array();
	//getSizableMeasurement is used to get the
	//measurement element or some number. This
	//variable is used as a measurement of current
	//size. Param passed is the dimension we size.
	var measurement = this.getSizableMeasurement(dimension);
	var dim = dimension.charAt(0).toUpperCase() + dimension.slice(1).toLowerCase();
	if (typeof measurement == "number" || Zapatec.isHtmlElement(measurement)) {
		//if there is measurement then height of our element is changed by the following
		//value - (height - measurement)
		diff = value - (Zapatec.Utils["get" + dim](measurement) || measurement);
	}
	//elements array should be 2 dimensional, at least it will be
	//emulated so. Second dimension holds the elements that share one difference.
	elArr = Zapatec.Array(elArr);
	//calculating the array of sizes to be set
	elArr.each(function(row, val) {
		var oneDiff = diff, size = value;
		val = Zapatec.Array(val);
		//if there is difference we share it between elements,
		//otherwise we share size!
		if (diff) {
			oneDiff = Math.round(oneDiff / val.length);
		} else {
			size = Math.round(size / val.length);
		}
		//adding new row to sizes array
		sizes[row] = [];
		//iterating through second dimension
		val.each(function(index, sizable) {
			var setSize = null;
			var elSize = null;
			//taking element size
			if (Zapatec.isHtmlElement(sizable)) {
				elSize = Zapatec.Utils["get" + dim](sizable)
			} else if (Zapatec.isSizableObj(sizable)) {
				elSize = sizable["get" + dim]();
			} else {
				return;
			}
			if (oneDiff || oneDiff === 0) {
				//can't make mistakes :)
				if (index != val.length - 1) {
					setSize = elSize + oneDiff;
				} else {
					setSize = elSize + (diff - oneDiff * index);
				}
			} else {
				//can't make mistakes :)
				if (index != val.length - 1) {
					setSize = size;
				} else {
					setSize = value - size * index;
				}
			}
			//saving size
			sizes[row][index] = {setSize : setSize, elSize : elSize};
		});
	});
	var res = elArr.each(function(row, val) {
		val = Zapatec.Array(val);
		//iterating through second dimension
		var res = val.each(function(index, sizable) {
			if (!sizes[row][index]) {
				return;
			}
			var res = self._setElementSize(sizes[row][index].setSize, sizable, dimension);
			if (!res && res === false) {
				return "break";
			} else {
				restArr.push({sizable : sizable, oldSize : sizes[row][index].elSize});
			}
		});
		if (!res && res === false) {
			return "break";
		}
	});
	return res;
};
 
/**
 * Rolls back the size of elements from the array.
 * @param rollBackArr {array} array of objects of the following structure:
 * {sizable : HTML element or Sizable object, oldSize : size to restore}.
 * @param dimension {string} dimension to roll back.
 */
Zapatec.Sizable._rollBackSizing = function(rollBackArr, dimension) {
	var dim = dimension.charAt(0).toUpperCase() + dimension.slice(1).toLowerCase();
	rollBackArr = Zapatec.Array(rollBackArr);
	rollBackArr.each(function(index, obj) {
		if (Zapatec.isHtmlElement(obj.sizable)) {
			Zapatec.Utils["set" + dim](obj.sizable, obj.oldSize);
		} else if (Zapatec.isSizableObj(obj.sizable)) {
			obj.sizable["set" + dim](obj.oldSize);
		}
	});
};

/**
 * This is Zapatec.Utils.Sizable object definition.
 * This is done in the "interface" manner.
 * It represents most of the routines and
 * events connected to sizing of the object.
 * @param config {object} - all parameters are passed as the properties of this object.
 * 
 * Constructor recognizes the following properties of the config object
 * \code
 *    prop. name     | description
 *  -------------------------------------------------------------------------------------------------
 *  container        | {HTML element} The main element to be sizable (default null).
 *  syncVertically   | {array} Elements to be synchronize in vertical sizing. The important thing 
 *                   | about this is that each element of this array can be array too and this means 
 *                   | that elements from that sub array will share the height change proportionally,
 *                   | otherwise, if there is only one element or it is not sub array but one element,
 *                   | it will tkae all the height change. To illustrate this: if you pass [el1, [el2, el3]]
 *                   | and your container has height 200px, after you cahnge the height to 300px
 *                   | el1 will change its height for 100px, but el2 and el3 will change their height
 *                   | for 50px both. (default []).
 *  syncHorizontally | {array} Elements to be synchronize in horizontal sizing. The important thing 
 *                   | about this is that each element of this array can be array too and this means 
 *                   | that elements from that sub array will share the width change proportionally,
 *                   | otherwise, if there is only one element or it is not sub array but one element,
 *                   | it will tkae all the width change. To illustrate this: if you pass [el1, [el2, el3]]
 *                   | and your container has width 200px, after you cahnge the width to 300px
 *                   | el1 will change its width for 100px, but el2 and el3 will change their width
 *                   | for 50px both. (default []).
 *  direction        | {string} Enabled direction of sizing. Can be "vertical", "horizontal" or "both"
 *                   | (default "both").
 *  limit            | {object} Object with properties "minWidth", "maxWidth", "minHeight" and "maxHeight",
 *                   | that are used as restrictions for setting size.
 * 
 * \endcode
 */
Zapatec.Utils.Sizable = function(config) {
	Zapatec.Utils.Sizable.SUPERconstructor.call(this, config);
};

Zapatec.Utils.Sizable.id = "Zapatec.Utils.Sizable";
Zapatec.inherit(Zapatec.Utils.Sizable, Zapatec.Widget);
//implementing Zapatec.CommandEvent interface
Zapatec.implement(Zapatec.Utils.Sizable, "Zapatec.CommandEvent");
//implementing Zapatec.Sizable interface
Zapatec.implement(Zapatec.Utils.Sizable, "Zapatec.Sizable");

/**
 * Inits the object with set of config options.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Sizable.prototype.init = function(config) {
	//calling parent init
	Zapatec.Utils.Sizable.SUPERclass.init.call(this, config);
	//makes object sizable (means all elements that are sizable)
	this.makeSizable();
};

/**
 * Sets the default configuration of the object and
 * inits it with user defined values.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Sizable.prototype.configure = function(config) {
	//set of elements to be sized with container vertically
	this.defineConfigOption("syncVertically", []);
	//set of elements to be sized with container horizontally
	this.defineConfigOption("syncHorizontally", []);
	//container, which is actually sized too and also 
	//is used as a measurement
	this.defineConfigOption("container", null);
	//direction of sizing (you can enable sizing only in one direction)
	this.defineConfigOption("direction", "both");
	//limitations of the sizing
	this.defineConfigOption("limit", {
		minWidth : 10, 
		maxWidth : null, 
		minHeight : 10, 
		maxHeight : null
	});
	//no theme
	this.defineConfigOption("theme", null);
	// Call parent method
	Zapatec.Utils.Sizable.SUPERclass.configure.call(this, config);
	config = this.getConfiguration();
	//checking if limit object wasn't overwritten
	if (!config.limit || typeof config.limit != "object") {
		config.limit = {
			minWidth : null, 
			maxWidth : null, 
			minHeight : null, 
			maxHeight : null
		};
	}
	var self = this;
	//making sure that config.syncVertically will be Array.
	config.syncVertically = Zapatec.Array(config.syncVertically);
	config.syncVertically.each(function(index, subArr) {
		config.syncVertically[index] = Zapatec.Array(subArr);
		config.syncVertically[index].each(function(elIndex, element) {
			if (element === null) {
				return;
			}
			element = Zapatec.Widget.getElementById(element);
			if (!element) {
				Zapatec.Log({description : "Wrong element in syncVertically array for the sizable object with ID '" + self.id + "'!"});
			} else {
				config.syncVertically[index][elIndex] = element;
			}
		});
	});
	//making sure that config.syncVertically will be Array.
	config.syncHorizontally = Zapatec.Array(config.syncHorizontally);
	config.syncHorizontally.each(function(index, subArr) {
		config.syncHorizontally[index] = Zapatec.Array(subArr);
		config.syncHorizontally[index].each(function(elIndex, element) {
			if (element === null) {
				return;
			}
			element = Zapatec.Widget.getElementById(element);
			if (!element) {
				Zapatec.Log({description : "Wrong element in syncHorizontally array for the sizable object with ID '" + self.id + "'!"});
			} else {
				config.syncHorizontally[index][elIndex] = element;
			}
		});
	});
	//setting default container element
	if (!Zapatec.isHtmlElement(config.container = Zapatec.Widget.getElementById(config.container))) {
		config.container = null;
	}
};

/**
 * Reconfigures the object with new parameters.
 * @param config {object} new configuration parameters.
 */
Zapatec.Utils.Sizable.prototype.reconfigure = function(config) {
	// Call parent method
	Zapatec.Utils.Sizable.SUPERclass.reconfigure.call(this, config);
};

/**
 * We overwrite the method of Zapatec.Sizable interface
 * to return the array of needed sizable elements.
 * @param {array} array of sizable elements.
 */
Zapatec.Utils.Sizable.prototype.getSizableElements = function(dimension) {
	var config = this.getConfiguration();
	if (dimension && dimension.toLowerCase() == "width") {
		return config.syncHorizontally.concat(config.container);
	} else if (dimension && dimension.toLowerCase() == "height") {
		return config.syncVertically.concat(config.container);
	} else {
		return config.syncVertically.concat(config.syncHorizontally).concat(config.container);
	}
};

