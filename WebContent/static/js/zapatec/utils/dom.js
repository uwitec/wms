//$Id: dom.js 6804 2007-03-30 11:43:26Z slip $
/**
 * Gets the width of the HTML element. This
 * is used to take out the measurement of sizing.
 * Currently offset measurement is used, cause
 * its equal for different box sizings.
 * @param el {HTML element} element to get width of.
 * @return {number} the measurement width of the element,
 * if not HTML element then false.
 */
Zapatec.Utils.getWidth = function(el) {
	//checking for valid element type
	if (!Zapatec.isHtmlElement(el)) {
		return false;
	}
	//returning the width
	return el.offsetWidth
};

/**
 * Gets the height of the HTML element. This
 * is used to take out the measurement of sizing.
 * Currently offset measurement is used, cause
 * its equal for different box sizings.
 * @param el {HTML element} element to get height of.
 * @return {number} the measurement height of the element,
 * if not HTML element then false.
 */
Zapatec.Utils.getHeight = function(el) {
	//checking for valid element type
	if (!Zapatec.isHtmlElement(el)) {
		return false;
	}
	//returning the height
	return el.offsetHeight
};

/**
 * Sets the width of the HTML element. This is
 * cross-browser implementation that is based on
 * Zapatec.Utils.getWidth value for this element. 
 * @param el {HTML element} element to set width.
 * @param width {number} width to set.
 * @return {boolean} true if width set, otherwise false.
 */
Zapatec.Utils.setWidth = function(el, width) {
	//getting integer value
	width = Math.round(width);
	//checking for valid element type and valid width
	if (!Zapatec.isHtmlElement(el) || width <= 0) {
		return false;
	}
	//saving old style width to restore if fail
	var oldWidth = el.style.width, newWidth;
	//trying to set width (first try)
	el.style.width = width + "px";
	//checking if it was set correctly
	if (Zapatec.Utils.getWidth(el) != width) {
		//if not lets try to calculate correct value
		newWidth = width - (Zapatec.Utils.getWidth(el) - width);
		if (newWidth > 0) {
			//and if its a valid number for size let's try again to set it
			el.style.width = newWidth + "px";
			//if still wrong result we need to restore old size,
			//report the warning and return false
			if (Zapatec.Utils.getWidth(el) != width) {
				el.style.width = oldWidth;
				Zapatec.Log({description : "Can't set the width - " + width + "px!", type : "warning"});
				return false;
			}
		} else {
			//if number isn't valid we need to restore old size,
			//report the warning and return false
			el.style.width = oldWidth;
			Zapatec.Log({description : "Can't set the width - " + width + "px!", type : "warning"});
			return false;
		}
	}
	//returning success
	return true;
};

/**
 * Sets the height of the HTML element. This is
 * cross-browser implementation that is based on
 * Zapatec.Utils.getHeight value for this element. 
 * @param el {HTML element} element to set height.
 * @param width {number} height to set.
 * @return {boolean} true if height set, otherwise false.
 */
Zapatec.Utils.setHeight = function(el, height) {
	//getting integer value
	height = Math.round(height);
	//checking for valid element type and valid height
	if (!Zapatec.isHtmlElement(el) || height <= 0) {
		return false;
	}
	//saving old style height to restore if fail
	var oldHeight = el.style.height, newHeight;
	//trying to set height (first try)
	el.style.height = height + "px";
	//checking if it was set correctly
	if (Zapatec.Utils.getHeight(el) != height) {
		//if not lets try to calculate correct value
		newHeight = height - (Zapatec.Utils.getHeight(el) - height);
		if (newHeight > 0) {
			//and if its a valid number for size let's try again to set it
			el.style.height = newHeight + "px";
			//if still wrong result we need to restore old size,
			//report the warning and return false
			if (Zapatec.Utils.getHeight(el) != height) {
				el.style.height = oldHeight;
				Zapatec.Log({description : "Can't set the height - " + height + "px!", type : "warning"});
				return false;
			}
		} else {
			//if number isn't valid we need to restore old size,
			//report the warning and return false
			el.style.height = oldHeight;
			Zapatec.Log({description : "Can't set the height - " + height + "px!", type : "warning"});
			return false;
		}
	}
	//returning success
	return true;
};

/**
 * Fixates the width of HTML element in pixels.
 * @param el {HTML element} element to fixate width of.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Utils.fixateWidth = function(el) {
	return Zapatec.Utils.setWidth(el, Zapatec.Utils.getWidth(el));
};

/**
 * Fixates the height of HTML element in pixels.
 * @param el {HTML element} element to fixate height of.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Utils.fixateHeight = function(el) {
	return Zapatec.Utils.setHeight(el, Zapatec.Utils.getHeight(el));
};

/**
 * Makes some routines to make element correctly sizable.
 * This is mostly preserving sizes and making overflow
 * "hidden" or "scroll" (depends on "scrollable" parameter).
 * @param el {HTML element} element to make safely sizable.
 * @param restorer {object} Zapatec.SRProp object.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Utils.makeSafelySizable = function(el, restorer) {
	if (el.sizable) {
		return true;
	}
	//checking if HTML element was passed
	if (!Zapatec.isHtmlElement(el)) {
		return false;
	}
	//trying to take the exsistant restorer
	if (!restorer) {
		restorer = el.restorer;
	}
	//checking for restorer, if there is no or the one given won't do
	//we create new one
	if (!restorer || !restorer.getObject || restorer.getObject() != el) {
		restorer = new Zapatec.SRProp(el);
	}
	//saving properties that will be changed
	restorer.saveProps("style.width", "style.height", "style.overflow");
	//fixateing sizes
	Zapatec.Utils.fixateWidth(el);
	Zapatec.Utils.fixateHeight(el);
	//getting overflow to know if we are sizing correct
	var overflow = Zapatec.Utils.getStyleProperty(el, "overflow");
	if (overflow == "" || overflow == "visible") {
		Zapatec.Log({description : "There is the chance that this element with overflow visible will not be sized correctly!", type : "warning"});
	}
	el.sizable = true;
	
	return true;
};

/**
 * Restores the element of sizing, if it was prepared
 * by Zapatec.Utils.makeSafelySizable function.
 * @param el {HTML element} element to restore.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.Utils.restoreOfSizing = function(el) {
	//checking if something was saved
	if (!el || !el.restorer || !el.sizable) {
		return false;
	}
	//restoreing needed properties
	el.restorer.restoreProps("style.width", "style.height", "style.overflow");
	if (el.restorer.isEmpty()) {
		el.restorer.destroy();
	}
	el.sizable = false;
	
	return true;
};

/**
 * Prepares elelement to be safely moved by Zapatec.Utils.moveTo. To be moved element should be positioned 
 * absolute and should be direct child of within parameter, should have margin set to 0px.
 * @param el {HTML element} the element to move
 * @param within {HTML element} if you want to move the element within some other element, 
 * @param restorer {SRProp object} the SRProp object you want to use to restore our 
 * elements properties, if you have already one.
 * pass the refference to it through this var (default is document.body)
 * @return {boolean} true if success, otherwise false
 */
Zapatec.Utils.makeSafelyMovable = function(el, within, restorer) {
	//Checking if the element is of the right type
	if (!Zapatec.isHtmlElement(el)) {
		return false;
	}
	//take default within element
	if (!within) {
		within = document.body;
	}
	//maybe element is already prepared
	if (el.within == within) {
		return true;
	}
	//trying to take the exsistant restorer
	if (!restorer) {
		restorer = el.restorer;
	}
	//checking for restorer and creating new one if existing does not 
	//fit our needs
	if (!restorer || !restorer.getObject || restorer.getObject() != el) {
		restorer = new Zapatec.SRProp(el);
	} 
	//To make it work we need the 'within' to be positioned relatively
	//except it is BODY (which means screen)
	el.within = within;
	if (within != document.body && within.style.position != "absolute") {
		restorer.saveProp("within.style.position");
		within.style.position = "relative";
	}
	//Need to preserve elements position after we prepare it for move
	if (within != document.body) {
		var pos1 = Zapatec.Utils.getElementOffset(within);
	} else {
		var pos1 = {x : 0, y : 0};
	}
	var pos2 = Zapatec.Utils.getElementOffset(el);
	var x = pos2.x - pos1.x;
	var y = pos2.y - pos1.y;
	restorer.saveProps("style.left", "style.top");
	el.style.left = x + "px";
	el.style.top = y + "px";
	//Checks if the element is positioned absolute.
	if (el.style.position != "absolute") {
		restorer.saveProp("style.position");
		el.style.position = "absolute";
	}
	//withon should be direct parent of the draggable element
	if (el.parentNode != within) {
		restorer.saveProps("parentNode", "nextSibling");
		within.appendChild(el);
	}
	//element must have margin 0px.
	restorer.saveProp("style.margin");
	el.style.margin = "0px";
	
	return true;
};

/**
 * Moves elelement to the specified position. To be moved element should be positioned absolute 
 * and should be direct child of 'within' element. If there is no el.within property, 
 * document.body will be taken. If you want safe move use Zapatec.Utils.makeSafelyMovable.
 * @param el {HTML element} - the element to move
 * @param x {number} - the x coordinate to move to
 * @param y {number} - the y coordinate to move to
 * @return {boolean} true if success, otherwise false
 */
Zapatec.Utils.moveTo = function(el, x, y) {
	//Checking if the element is of the right type
	if (!Zapatec.isHtmlElement(el)) {
		return false;
	}
	var pos = null;
	if (Zapatec.FixateOnScreen.isRegistered(el)) {
		pos = Zapatec.FixateOnScreen.correctCoordinates(x, y);
		if (Zapatec.is_ie && !Zapatec.is_ie7) {
			el.style.setExpression("left", pos.x);
			el.style.setExpression("top", pos.y);
			return true;
		}
	} else {
		pos = {
			x : parseInt(x, 10) + "px", 
			y : parseInt(y, 10) + "px"
		};
	}
	//setting of coordinates
	if (x || x === 0) {
		el.style.left = pos.x;
	}
	if (y || y === 0) {
		el.style.top = pos.y;
	}
	
	return true;
};

/** 
 * Gets the element position within another one (this depends on your implementation). 
 * @param el {HTML element} - we are getting the position of this element
 * @return {object} object with x and y properties or false if failed.
 */
Zapatec.Utils.getPos = function(el) {
	//Checking if the element is of the right type
	if (!Zapatec.isHtmlElement(el)) {
		return false;
	}
	var pos = null;
	if (pos = Zapatec.FixateOnScreen.parseCoordinates(el)) {
		return pos;
	}
	//parsing coordinates
	return {
		x : el.offsetLeft,
		y : el.offsetTop
	}
};

/**
 * Moves element by the given offset in the 'within' element.
 * @param el {HTML element} - element to move
 * @param offsetX {number} - x offset to move for
 * @param offsetY {number} - y offset to move for
 * @return {boolean} true if succeeds, otherwise false
 */
Zapatec.Utils.moveFor = function(el, offsetX, offsetY) {	
	//getting current position
	var oldPos = Zapatec.Utils.getPos(el);
	//assigning the offset
	if (oldPos) {
		return Zapatec.Utils.moveTo(el, oldPos.x + offsetX, oldPos.y + offsetY);
	} else {
		return false;
	}
};

/**
 * Restores all the changes made in the elelement by Zapatec.Utils.makeSafelyMovable. 
 * @param el {HTML element} - the element to restore
 * @return {boolean} true if successful, otherwise false
 */
Zapatec.Utils.restoreOfMove = function(el) {
	if (!el.within) return false;
	//restoreing properies if everything is okay
	el.restorer.restoreProps(
		"style.position",
		"parentNode",
		"style.margin",
		"nextSibling",
		"within.style.position",
		"style.left",
		"style.top"
	);
	if (el.restorer.isEmpty()) {
		el.restorer.destroy();
	}
	el.within = null;
	return true;
};

/**
 * returns array of elements which has attribute 'attr' with value 'val'
 * by giving 'el' you can finetune your search
 * @param attribute {string} attribute to search
 * @param value {mixed} searched attributes value.
 * @param within {HTML element} reference to the element.
 * @param recursive {boolean} searches in childs.
 * @param match {boolean} if true and attribute is string,
 * tries to seek within it.
 * @return {array} array of elements found.
 */
Zapatec.Utils.getElementsByAttribute = function(attribute, value, within, recursive, match){
	//no attributte to seek - no action
	if (!attribute) {
		return false;
	}
	//trying to get element within what we need to search
	within = Zapatec.Widget.getElementById(within);
	//if none given taking document.body
	within || (within = document.body);
	//iterating through all the elements childs
	var element = within.firstChild; result = [];
	while (element) {
		//element has attribute???
		if (element[attribute]) {
			//than if there's no value passed or values match adding it to resulting array
			if (typeof value == "undefined" || element[attribute] == value) {
				result.push(element);
			} else if (match && typeof element[attribute] == "string" && element[attribute].indexOf(value) != -1) {
				result.push(element);
			}
		};
		//if recursive, going through all the childs
		if (recursive && element.hasChildNodes()) {
			result = result.concat(Zapatec.Utils.getElementsByAttribute(attribute, value, element, recursive, match));
		}
		//taking next sibling
		element = element.nextSibling
	};
	//returning result
	return Zapatec.Array(result);
};

/**
 * Creates an instance of object using passed
 * constructor, for each element in array.
 * The element will be passed as a "container"
 * option by default, but you can redefine
 * it using parameter configOption. Using
 * third parameter you can pass other config
 * options for the constructor.
 * @param constructor {function} constructor function.
 * @param elements {array} array of elements that the
 * consstructor will be apllied to.
 * @param config {object} config obect for constructor.
 * @param configOption {string} config option that will
 * be filled with the element, default is "container".
 * @return {array} array of created widgets.
 */
Zapatec.Utils.applyToElements = function(constructor, elements, config, configOption) {
	if (typeof constructor != "function" || !Zapatec.isArray(elements)) {
		return false;
	}
	elements = Zapatec.Array(elements);
	if (!configOption) {
		configOption = "container";
	}
	//correcting or creating config object
	if (!config || typeof config != "object") {
		config = {};
	}
	//resulting array of Zapoatec.Utils.Draggable objects
	var result = Zapatec.Array();
	elements.each(function(index, element) {
		//filling config with current element
		config[configOption] = element;
		//saving resulting object
		result.push(new constructor(config));
	});
	//returning result
	return result;
};

/**
 * Replaces the image with DIV element, and sets the image as background-image.
 * this is not good for images with changed size, because you cant change the background-image size and it will tile.
 * @param el {HTML element} reference to the element.
 * @return {HTML element} the needed element.
 */
Zapatec.Utils.img2div = function(el){
	if (!Zapatec.isHtmlElement(el)) {
		return null;
	}
	//FIX ME: maybe this can be changed to div/img from just div with background
	if ((/img/i).test(el.nodeName)) {
		var div = document.createElement('div');
	    // Set div width and height when image is loaded
	    var objImage = new Image();
	    objImage.onload = function() {
	      div.style.width = this.width + 'px';
	      div.style.height = this.height + 'px';
	      div.style.fontSize = '0px';
	      this.onload = null;
	    };
	    objImage.src = el.src;
	    // Replace image with the div
	    div.style.backgroundImage = 'url(' + el.src + ')';
	    div.style.backgroundColor = 'transparent';
		var id = el.id;
		var className = el.className;
		el.parentNode.replaceChild(div, el);
		div.id = id;
		div.className = className;
		return div
	} else {
		return el
	}
};

/**
 * Returns the string with numbers representing 
 * the path in the DOM tree from the element 
 * passed as parent to the element you need.
 * Example - "1-3-1-8".
 * @param element {HTML element} element to get path of.
 * @param parent {HTML element} we stop on this element.
 * @return {string} string representing path.
 */
Zapatec.Utils.getElementPath = function(element, parent) {
	//there always should be parameters passed
	if (!Zapatec.isHtmlElement(element) || !Zapatec.isHtmlElement(parent)) {
		return false;
	}
	//array of numbers from path
	var res = [];
	var el = element;
	//going up the tree and counting previous siblings
	while(el && el != parent) {
		//at first we think that this is the first node :)
		var number = 1;
		//counting previous siblings
		while(el.previousSibling) {
			++number;
			el = el.previousSibling;
		}
		//adding a number in the beggining of the array
		res.unshift(number);
		//taking parent node
		el = el.parentNode;
	}
	//joining the resulting array to have a string value.
	return res.join("-");
};

//This is an HTML element used as a cover to imitate mousemove action correctly.
Zapatec.Utils.cover = Zapatec.Utils.createElement("div");
Zapatec.Utils.cover.style.overflow = "hidden";
//This is the workaround for IE who doesn't firest needed events if the element is empty
Zapatec.Utils.cover.style.backgroundImage = "url(" + Zapatec.zapatecPath + "zpempty.gif)";
Zapatec.Utils.cover.style.display = "none";
Zapatec.Utils.cover.id = "zpCoverControl";

/**
 * Shows the cover.
 * @param zIndex {number} - z-index of the cover.
 * @param cursor {string} - cursor to be assigned.
 * @param mouseMoveHandler {function} - handler function for mouse move
 * @param mouseUpHandler {function} - handler function for mouse up
 */
Zapatec.Utils.cover.show = function(zIndex, cursor, mouseMoveHandler, mouseUpHandler) {
	if (!this.parentNode) {
		document.body.appendChild(this);
	}
	if (this.style.display != "none") {
		this.hide();
	}
	this.style.display = "block";
	//formatting it to be over all the others(even our draggable element)
	Zapatec.Utils.makeSafelyMovable(this, null, document.body);
	//initial position
	var x = 0;
	var y = 0;
	//need to correct it with window scrolling
	y += Zapatec.Utils.getPageScrollY();
	x += Zapatec.Utils.getPageScrollX();
	//moving cover
	Zapatec.Utils.moveTo(this, x, y);
	//setting sizes
	var dim = Zapatec.Utils.getWindowSize();
	this.style.width = dim.width + "px";
	this.style.height = dim.height + "px";
	//registering it to scroll
	Zapatec.FixateOnScreen.register(this);
	//setting proper cursor and z-index
	this.style.zIndex = zIndex;
	this.style.cursor = cursor;
	//adding event listeners
	if (typeof mouseMoveHandler == "function") {
		Zapatec.Utils.addEvent(this, 'mousemove', mouseMoveHandler);
	}
	if (typeof mouseUpHandler == "function") {
		Zapatec.Utils.addEvent(this, 'mouseup', mouseUpHandler);
	}
	//saving references to the event hanlers to remove them on hide.
	this.mouseMoveHandler = mouseMoveHandler;
	this.mouseUpHandler = mouseUpHandler;
};

/**
 * Hides the cover.
 */
Zapatec.Utils.cover.hide = function() {
	//unregistering cover from being scrolled with window
	Zapatec.FixateOnScreen.unregister(this);
	//removing event handlers
	if (typeof this.mouseMoveHandler == "function") {
		Zapatec.Utils.removeEvent(this, 'mousemove', this.mouseMoveHandler);
	}
	if (typeof this.mouseUpHandler == "function") {
		Zapatec.Utils.removeEvent(this, 'mouseup', this.mouseUpHandler);
	}
	//mouse move handler
	this.mouseMoveHandler = null;
	this.mouseUpHandler = null;
	//clearing the cursor and z-index
	this.style.zIndex = "";
	this.style.cursor = "";
	//hiding elements
	this.style.display = "none";
};
