//$Id: zpobjects.js 6804 2007-03-30 11:43:26Z slip $
/**
 * This is a constructor for Zapatec Array.
 * It can create new array or extend the given
 * one with some useful methods. You can use
 * it with new opertaor or as simple function.
 * Here is the list of extended methods:
 *    method name   | description
 *  -------------------------------------------------------------------------------------------------
 *   clear          | empties the array and returns itself
 *   compact        | deletes the null and undefined items from resulting array
 *   indexOf        | returns the index of the item with the value pointed
 *   without        | returns array without values pointed as all arguments
 *   remove         | returns array without elements that were under poited indexes.
 *                  | You can pass arguments or you can pass an array.
 * @param arr {array} if given this array will be 
 * extended.
 * @return {array} extended array.
 */
Zapatec.Array = function(arr) {
	//if array is not given then creating new one
	if (!Zapatec.isArray(arr)) {
		var array = [];
		for(var i = 0; i < arguments.length; ++i) {
			array.push(arguments[i]);
		}
		arr = array;
	}
	//copying all internal methods to array instance.
	//clears the array
	arr.clear = function() {
		Zapatec.Array.clear(this);
	};
	//returns compact array
	arr.compact = function() {
		var compact = Zapatec.Array.compact(this);
		return Zapatec.Array(compact);
	};
	//returns index of element, otherwise -1
	arr.indexOf = function(value) {
		return Zapatec.Array.indexOf(this, value);
	};
	//returns array without some values
	arr.without = function() {
		var args = [].slice.call(arguments, 0);
		args.unshift(this);
		var without = Zapatec.Array.without.apply(Zapatec.Array, args);
		return Zapatec.Array(without);
	};
	//returns array without pointed indexes
	arr.remove = function() {
		var args = [].slice.call(arguments, 0);
		args.unshift(this);
		var cut = Zapatec.Array.remove.apply(Zapatec.Array, args);
		return Zapatec.Array(cut);
	};
	//iterates through array, first argument is iterator
	//function, second one can switch reverse iteration,
	//iterator function is passed: index, value, array
	arr.each = function(func, reverse) {
		var result;
		for(var index = reverse ? this.length - 1 : 0; 
		    reverse ? (index >= 0) : (index < this.length); 
		    reverse ? --index : ++index) {
				if (typeof func == "function") {
					result = func(index, this[index], this);
					if (result == "break") {
						break;
					}
				}
		}
		//need a way to return the break indicator
		if (result == "break") {
			return false;
		}
		return true;
	};
	//indicator of Zapatec.Array
	arr.isZpArray = true;
	
	return arr;
};

/**
 * Clears the given array from null and undefined items.
 * @param arr {array} array to compact.
 * @return {array} cleared array.
 */
Zapatec.Array.compact = function(arr) {
	var newArr = [];
	for(var item = 0; item < arr.length; ++item) {
		if (arr[item] !== null && typeof arr[item] != "undefined") {
			newArr.push(arr[item]);
		}
	}
	return newArr;
};

/**
 * Returns the empty array.
 * @param arr {array} array to clear.
 */
Zapatec.Array.clear = function(arr) {
	arr.length = 0;
};

/**
 * Returns the index of the value in the given array.
 * If value was not found returns -1.
 * @param arr {array} array to walk through.
 * @param value {mixed} value to seek for.
 * @return {number} index of found item or -1.
 */
Zapatec.Array.indexOf = function(arr, value) {
	//iterating through items
	for(var item = 0; item < arr.length; ++item) {
		if (arr[item] === value) {
			//returning found index
			return item;
		}
	}
	//returning failure.
	return -1;
};

/**
 * Returns the array without pointed items.
 * All the arguments that were passed after
 * arr will be used as values to seek.
 * @param arr {array} array to cut.
 * @return {array} array without elements which were passed.
 */
Zapatec.Array.without = function(arr) {
	var newArr = [], without;
	//iterating through items
	for(var item = 0; item < arr.length; ++item) {
		without = false;
		for(var value = 1; value < arguments.length; ++value) {
			if (arr[item] === arguments[value]) {
				//flag that we need to exclude this element
				without = true;
				break;
			}
		}
		//if not excluding lets push this value to result
		if (!without) {
			newArr.push(arr[item]);
		}
	}
	//returning array.
	return newArr;
};

/**
 * Returns the array without items that were under pointed
 * indexes. Second and next parameters are treated as indexes
 * to remove. You can also pass array of indexes as second argument.
 * @param arr {array} array to cut.
 * @return {array} array without items with pointed indexes.
 */
Zapatec.Array.remove = function(arr) {
	var newArr = [], without, value, start = 1;
	if (arguments[1] && arguments[1].length && typeof arguments[1] == "object") {
		args = arguments[1];
		start = 0;
	} else {
		args = arguments;
	}
	//iterating through items
	for(var item = 0; item < arr.length; ++item) {
		without = false;
		for(value = start; value < args.length; ++value) {
			if (item === args[value]) {
				//flag that we need to exclude this element
				without = true;
				break;
			}
		}
		//if not excluding lets push this value to result
		if (!without) {
			newArr.push(arr[item]);
		}
	}
	//returning array.
	return newArr;
};

/**
 * This is a constructor for Zapatec Hash.
 * It can create new hash or extend the given
 * one with some useful methods. You can use
 * it with new opertaor or as simple function.
 * Here is the list of extended methods:
 *    method name   | description
 *  -------------------------------------------------------------------------------------------------
 *   hashRemove     | returns hash without properties pointed as all arguments or array as an argument
 *   hashEach       | iterates through all hash pairs of key and value. Internal methods are skiped.
 *   hashIsEmpty    | returns true if hash has no items. Internal methods are not taken in account.
 *
 * @param hash {object} if given this hash will be extended.
 * @return {object} extended hash.
 */
Zapatec.Hash = function(hash) {
	//if hash is not given then creating new one
	if (!hash || typeof hash != "object") {
		hash = {};
	}
	//copying all internal methods to array instance.
	//returns hash without pointed properties
	hash.hashRemove = function() {
		var args = [].slice.call(arguments, 0);
		args.unshift(this);
		var without = Zapatec.Hash.remove.apply(Zapatec.Hash, args);
		return Zapatec.Hash(without);
	};
	//iterating through hash properties, first parameter
	//is iterator function
	hash.hashEach = function(func) {
		//flag if we should execute iteration function
		var result = null;
		//looping through all properties
		for(var prop in this) {
			//excluding Zapatec.Hash properties
			if (prop == "hashRemove" || prop == "hashEach" 
			    || prop == "hashIsEmpty" || prop == "isZpHash") {
					continue;
			}
			//excluding Object properties
			if (typeof Object.prototype[prop] != "undefined") {
				continue;
			}
			//if this is not Object or Zapatec.Hash property
			//lets call iteration function.
			result = func(prop, this[prop], this);
			if (result == "break") {
				break;
			}
		}
		//need a way to return the break indicator
		if (result == "break") {
			return false;
		}
		return true;
	};
	//checks if hash is empty
	hash.hashIsEmpty = function() {
		var empty = true;
		this.hashEach(function() {
			empty = false;
			return "break";
		});
		return empty;
	};
	//indicator of Zapatec.Hash
	hash.isZpHash = true;
	
	return hash;
};

/**
 * Returns the hash without properties which has names
 * pointed as second and other arguments.
 * You can also pass array of strings as second argument.
 * @param hash {object} hash to remove property from.
 * @return {object} cut hash.
 */
Zapatec.Hash.remove = function(hash) {
	var newHash = {}, without, value, start = 1;
	if (arguments[1] && arguments[1].length && typeof arguments[1] == "object") {
		args = arguments[1];
		start = 0;
	} else {
		args = arguments;
	}
	//iterating through items
	for(var item in hash) {
		without = false;
		for(value = start; value < args.length; ++value) {
			if (item === args[value]) {
				//flag that we need to exclude this element
				without = true;
				break;
			}
		}
		//if not excluding lets add this value to result
		if (!without) {
			newHash[item] = hash[item];
		}
	}
	//returning hash.
	return newHash;
};

/**
 * Gets the value from the hash tree by given path.
 * Path can include method calls and array indexing.
 * @param hash {object} hash to seek in.
 * @param path {string} path to the element.
 * @return {object} object with two properties:
 *                  - value - value by the path.
 *                  - result - success or failure.
 */
Zapatec.Hash.getByPath = function(hash, path) {
	//if no path - no action
	if (!path || typeof path != "string") {
		Zapatec.Log({description : "Not a path passed to Zapatec.Hash.getByPath function!", type : "warning"});
		return {
			result : false
		};
	}
	//splitting the path
	var paths = path.split(".");
	//if no items splitted then something is wrong
	if (!paths.length) {
		Zapatec.Log({description : "Wrong path passed to Zapatec.Hash.getByPath function!", type : "warning"});
		return {
			result : false
		};
	}
	//iterateing through each item
	var item = 0;
	var value = hash;
	var name = "";
	var scope = null;
	while(paths[item]) {
		//if there is still part of the path current element can not be null
		if (value === null || typeof value == "undefined") {
			Zapatec.Log({description : "Incorrect path passed to Zapatec.Hash.getByPath function!", type : "warning"});
			return {
				result : false
			};
		}
		//getting property name
		name = paths[item].replace(/(\(\)|\[[^\[\]]+\])+/, "");
		try {
			//saving the parent object as scope for its methods if there are any
			scope = value;
			//getting the property value
			value = value[name];
		} catch (e) {
			Zapatec.Log({description : "Incorrect path passed to Zapatec.Hash.getByPath function!", type : "warning"});
			return {
				result : false
			};
		}
		//taking the rest part to seek for array indexes or method execution brackets
		paths[item] = paths[item].replace(name, "");
		//cyclically getting the value by array indexes or method execution
		//there can be many of the in one property name
		while (paths[item] != "") {
			//getting first one
			name = paths[item].match(/(\(\)|\[[^\[\]]+\])/)[1];
			//if it is method lets try to execute it
			if (name && /\(\)$/.test(name)) {
				try {
					//we use parent object scope or if its not a direst method
					//but just a function we use itself as a scope
					value = value.call(scope || value);
					//scope becomes null until we get another property by name
					scope = null;
				} catch (e) {
					Zapatec.Log({description : "Incorrect path passed to Zapatec.Hash.getByPath function!", type : "warning"});
					return {
						result : false
					};
				}
			} else if (name && /\[["']?[^\[\]"']+["']?\]$/.test(name)) {// if this is an array lets take its item
				try {
					//taking the item by index
					value = value[name.match(/\[["']?([^\[\]"']+)["']?\]/)[1]];
					//scope becomes null until we get another property by name
					scope = null;
				} catch (e) {
					Zapatec.Log({description : "Incorrect path passed to Zapatec.Hash.getByPath function!", type : "warning"});
					return {
						result : false
					};
				}
			}
			//removing index or brackets from the prop path
			paths[item] = paths[item].replace(name, "");
		}
		++item;
	}
	if (typeof value == "undefined") {
		Zapatec.Log({description : "Incorrect path passed to Zapatec.Hash.getByPath function!", type : "warning"});
		return {
			result : false
		};
	}
	return {
		result : true,
		value : value
	};
};

/**
 * Sets the value to the hash tree by given path.
 * Path can include method calls and array indexing.
 * @param hash {object} hash to set in.
 * @param path {string} path to the element.
 * @param val {mixed} value to set.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Hash.setByPath = function(hash, path, val) {
	//if no path - no action
	if (!path || typeof path != "string") {
		Zapatec.Log({description : "Not a path passed to Zapatec.Hash.setByPath function!", type : "warning"});
		return false;
	}
	//splitting the path
	var paths = path.split(".");
	//if no items splitted then something is wrong
	if (!paths.length) {
		Zapatec.Log({description : "Wrong path passed to Zapatec.Hash.setByPath function!", type : "warning"});
		return false;
	}
	//taking last item in path (splitted by '.')
	var lastItem = paths[paths.length - 1];
	var obj = hash;
	//making the rest of the path available for
	//Zapatec.Hash.getByPath
	var getPath = paths.slice(0, -1).join(".");
	//if there are array indexes lets save just the last one
	//as the last item and add the rest to the getPath
	var arrIndexReg = /\[[^\[\]]+\]$/;
	if (arrIndexReg.test(lastItem)) {
		getPath += (getPath == "" ? "" : ".") + lastItem.replace(arrIndexReg, "");
		lastItem = lastItem.match(/\[["']?([^\[\]"']+)["']?\]$/)[1];
	}
	//seeking the object property of which we need to set
	if (getPath != "") {
		var obj = Zapatec.Hash.getByPath(hash, getPath).value;
	}
	//trying to set the value
	try {
		obj[lastItem] = val;
	} catch (e) {
		Zapatec.Log({description : "Incorrect path passed to Zapatec.Hash.setByPath function!", type : "warning"});
		return false;
	}
	return true;
};

/** 
 * Returns true if this is HTML element.
 * return {boolean} true if HTML element, otherwise false.
 */
Zapatec.isHtmlElement = function(el) {
	if (!el || el.nodeType != 1) {
		return false;
	}
	
	return true;
};

/**
 * Returns true if object has sizable interface.
 * return {boolean} true if sizable, otherwise false.
 */
Zapatec.isSizableObj = function(obj) {
	if (obj && obj.hasInterface && obj.hasInterface("Zapatec.Sizable")) {
		return true;
	}
	
	return false;
};

/**
 * Returns true if object has movable interface.
 * return {boolean} true if sizable, otherwise false.
 */
Zapatec.isMovableObj = function(obj) {
	if (obj && obj.hasInterface && obj.hasInterface("Zapatec.Movable")) {
		return true;
	}
	
	return false;
};

/**
 * Returns true if object is an array.
 * return {boolean} true if array, otherwise false.
 */
Zapatec.isArray = function(arr) {
	if (arr && typeof arr == "object" && arr.constructor == Array) {
		return true;
	}
	
	return false;
};

/**
 * Returns true if object is a Date object.
 * return {boolean} true if Date object, otherwise false.
 */
Zapatec.isDate = function(date) {
	if (date && typeof date == "object" && date.constructor == Date) {
		return true;
	}
	
	return false;
};

/**
 * object for saving and restoring properties of the given object - SRProp (Save Restore Object Properties)
 * @param obj [object] - object to work with
 * this class adds methods to any given object which enable to save and restore properties using following
 * \code
 *   var object = {};
 *   ob = new Zapatec.SRProp(obj);
 *   object.prop = "";
 *   ob.saveProp("prop");
 *   object.prop = "ttt";
 *	 ob.restoreProp("prop");
 *   ob.restoreAll();
 * \endcode
 */
Zapatec.SRProp = function(obj) {
	//storeing of an object to inspect
	this.obj = obj;
	//array of stored properties
	this.savedProps = new Zapatec.Hash();
	//giving the object itself the refference to SRProp obj
	Zapatec.Utils.createProperty(obj, "restorer", this);
}

/**
 * Returns the hash of the saved properties. This method can be easily
 * overwritten for extending object.
 * @return {object} object containing all saved properties.
 */
Zapatec.SRProp.prototype.getSavedProps = function() {
	return this.savedProps;
};

/**
 * Returns the object we are working with.
 * @return {object} object ot work with.
 */
Zapatec.SRProp.prototype.getObject = function() {
	return this.obj;
};

/**
 * Saves the named property to savedProp array
 * You should not overwrite this method as it is core of the object.
 * @param propName [string] - name of the property to save - can be the followin "prop", "level1.level2" and so on.
 * @return true if successful, otherwise false
 */
Zapatec.SRProp.prototype.saveProp = function (propName) {
	//property name should definately be string, I think :)
	if (typeof propName != "string") {
		return false;
	}
	//getting the value of the property
	var value = Zapatec.Hash.getByPath(this.getObject(), propName);
	//checking if method doesn't failed
	if (value.result) {
		if (typeof this.getProp(propName) != "undefined") {
			var prop = this.getSavedProps()[propName] = Zapatec.Array(this.getSavedProps()[propName]);
			prop.push(value.value);
			prop.combination = true;
			Zapatec.Log({description : "The property '" + propName + "' now contains more than one value!", type : "warning"});
		} else {
			//if not saving value to the table
			this.getSavedProps()[propName] = value.value;
		}
		//returning success
		return true;
	} else {
		//returning failure
		return false;
	}
}

/**
 * Saves the named properties to savedProp array
 * You should not overwrite this method as it is core of the object.
 * @param can be any set of params, but only strings will be parsed.
 * @return an array of saved properties names
 */
Zapatec.SRProp.prototype.saveProps = function () {
	//this array will store a set of really saved properties
	var result = [];
	for(var i = 0; i < arguments.length; ++i) {
		if (this.saveProp(arguments[i])) {
			//if saved successfuly lets push propety name
			//to result array.
			result.push(arguments[i]);
		}
	}
	return result;
}

/**
 * Restores the named property from savedProp array.
 * You should not overwrite this method as it is core of the object.
 * @param propName [string] - name of the property to restore - can be the followin "prop", "level1.level2" and so on.
 * @return true if successful, otherwise false
 */
Zapatec.SRProp.prototype.restoreProp = function (propName) {
	//property name should definately be string, I think :)
	if (typeof propName != "string" || typeof this.getSavedProps()[propName] == "undefined") {
		return false;
	}
	var prop = this.getSavedProps()[propName];
	var combination = false, nextSibling = null;
	if (Zapatec.isArray(prop) && prop.combination) {
		prop = prop[prop.length - 1];
		combination = true;
	}
	//Its a workaround for HTML properties such as parentNode to be handled correctly
	if (propName.match(/parentNode$/) !== null && prop && typeof prop == "object" && prop.appendChild) {
		nextSibling = this.getSavedProps()[propName.replace(/parentNode/, "nextSibling")] || null;
		if (nextSibling && nextSibling.parentNode == prop) {
			prop.insertBefore(this.getObject(), nextSibling);
		} else {
			prop.appendChild(this.getObject());
		}
		//removing nextSibling property.
		this.savedProps = this.getSavedProps().hashRemove(propName.replace(/parentNode/, "nextSibling"));
	} else {
		//restoreing property
		if (!Zapatec.Hash.setByPath(this.getObject(), propName, prop)) {
			return false;
		}
	}
	//and now free the position in the hash as the property was restored
	if (!combination) {
		this.savedProps = this.getSavedProps().hashRemove(propName);
	} else {
		prop = this.getSavedProps()[propName];
		this.getSavedProps()[propName] = Zapatec.Array.without(prop, prop.length - 1);
	}
	
	return true;
}

/**
 * Restores the named properties from savedProp array
 * You should not overwrite this method as it is core of the object.
 * @param can be any set of params, but only strings will be parsed.
 * @return true if successful, otherwise false
 */
Zapatec.SRProp.prototype.restoreProps = function (propName) {
	//this array will store a set of really restored properties
	var result = [];
	for(var i = 0; i < arguments.length; ++i) {
		if (this.restoreProp(arguments[i])) {
			//if restored successfuly lets push propety name
			//to result array.
			result.push(arguments[i]);
		}
	}
	return result;
}

/**
 * Restores all properties from the savedProp array
 * You should not overwrite this method as it is core of the object.
 */
Zapatec.SRProp.prototype.restoreAll = function() {
	//just iterating through all saved proerties and restoreing them
	var self = this;
	this.getSavedProps().hashEach(function(i) {
		self.restoreProp(i);
	});
}

/**
 * Gets property value by name from the savedProp array
 * You should not overwrite this method as it is core of the object.
 * @param propName [string] - name of the property to get - can be the followin "prop", "level1.level2" and so on.
 */
Zapatec.SRProp.prototype.getProp = function(propName) {
	//Should I expalin this? :)
	return this.getSavedProps()[propName];
}

/**
 * Checks if hash of saved properties is empty.
 * @return {boolean} true if there are no saved properties,
 * otherwise false.
 */
Zapatec.SRProp.prototype.isEmpty = function() {
	return this.getSavedProps().hashIsEmpty();
};

/**
 * Destroys an object.
 */
Zapatec.SRProp.prototype.destroy = function() {
	//destroing cross-reference
	this.getObject().restorer = null;
	//deleteing each property of the object
	for(var iProp in this) {
		this[iProp] = null;
	}
	
	return null;
};

/**
 * Command event interface. It lets you controll
 * the listeners execution, like stoping, re-executing.
 * It is thought that implementing object inherits
 * Zapatec.EventDriven.
 */
Zapatec.CommandEvent = {};

/**
 * New fireEvent method to fit new functionality.
 * @param strEvent [string] - event name.
 */
Zapatec.CommandEvent.fireEvent = function(strEvent) {
	//checking if there is such event
	if (!this.events[strEvent]) {
		return;
	}
	//getting listeners
	var arrListeners = this.events[strEvent].listeners.slice();
	//clearing previously returned value
	this._setReturnedValue(null);
	//enabling event propogation.
	this._setEventPropagation(true);
	for (var iListener = 0; iListener < arrListeners.length; iListener++) {
		// Remove first argument
		var arrArgs = [].slice.call(arguments, 1);
		// Call in scope of this object
		arrListeners[iListener].apply(this, arrArgs);
		//getting the returned result
		var result = this._getReturnedValue();
		//if needed we stop propogation
		if (!this._getEventPropagation()) {
			return result;
		}
		//proceeding the returned value
		if (result == "re-execute") {
			this.fireEvent(strEvent);
			break;
		} else if (result == "parent-re-execute") {
			return result;
		}
	}
	//returning value
	return this._getReturnedValue();
};

/**
 * This method can be used by listeners to return some value.
 * @param val {mixed} any value to return.
 */
Zapatec.CommandEvent.returnValue = function(val) {
	this._setReturnedValue(val);
};

/**
 * Sets returned value to internal variable.
 * @param val {mixed} value to set.
 */
Zapatec.CommandEvent._setReturnedValue = function(val) {
	this.returnedValue = val;
};

/**
 * Gets returned value from internal variable.
 * @return {mixed} value.
 */
Zapatec.CommandEvent._getReturnedValue = function() {
	return this.returnedValue;
};

/**
 * Stops the chain of execution of event listenrs.
 */
Zapatec.CommandEvent.stopPropagation = function() {
	this._setEventPropagation(false);
};

/**
 * Saves propgation state to internal variable.
 * @param on {boolean} true to turn on, false to turn off.
 */
Zapatec.CommandEvent._setEventPropagation = function(on) {
	this.eventPropagation = on;
};

/**
 * Gets propgation state from internal variable.
 * @return {boolean} true if turned on, otherwise false.
 */
Zapatec.CommandEvent._getEventPropagation = function() {
	return this.eventPropagation;
};

/**
 * The object working with global controlled events.
 */
Zapatec.GlobalEvents = new Zapatec.EventDriven();
Zapatec.implement(Zapatec.GlobalEvents, "Zapatec.CommandEvent");
Zapatec.GlobalEvents.init();