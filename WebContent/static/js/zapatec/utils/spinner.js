// $Id: spinner.js 5700 2006-12-19 20:28:55Z vkulov $
/**
 * @fileoverview A spinner widget.
 *
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/**
 * Zapatec.Spinner constructor. Creates a new spinner object with
 * given parameters.
 *
 * @constructor
 * @extends Zapatec.Widget
 * @param {object} objArgs Spinner configuration
 */
Zapatec.Spinner = function(objArgs) {
  if(arguments.length == 0){
    objArgs = {};
  }

  // Call constructor of superclass
  Zapatec.Spinner.SUPERconstructor.call(this, objArgs);
};

/**
 * Unique static id of the widget class. Gives ability for Zapatec#inherit to
 * determine and store path to this file correctly when it is included using
 * Zapatec#include. When this file is included using Zapatec#include or path
 * to this file is gotten using Zapatec#getPath, this value must be specified
 * as script id.
 * @private
 */
Zapatec.Spinner.id = 'Zapatec.Spinner';

// Inherit Widget
Zapatec.inherit(Zapatec.Spinner, Zapatec.Widget);

/**
 * Initializes object.
 *
 * @param {object} objArgs User configuration
 */
Zapatec.Spinner.prototype.init = function(objArgs)
{
  // Call parent init
  Zapatec.Spinner.SUPERclass.init.call(this, objArgs);

  this.createSpinner();

  this.updateText();
};

/**
 * Creates elements needed by the spinner
 */
Zapatec.Spinner.prototype.createSpinner = function ()  {
  this.fireEvent("beforeCreate");

  var content = [];

  // Generate id and class for table element
  var tableId = Zapatec.Spinner.generateString(
          "zpSpin", this.id, "Table");
  var tableClass = this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Table'
  });
  content.push("<table id='");
  content.push(tableId);
  content.push("' class='");
  content.push(tableClass);
  content.push("' cellSpacing='0' cellPadding='0' >");
  content.push("<tbody>");

  // Generate id and class for text field element
  var inputId = Zapatec.Spinner.generateString(
          "zpSpin", this.id, "Input");
  var inputClass = this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Input'
  });
  content.push("<tr><td rowspan='2' id='");
  content.push(inputId);
  content.push("' class='");
  content.push(inputClass);
  content.push("'></td>");

  // Generate id and class for up element
  var upId = Zapatec.Spinner.generateString(
          "zpSpin", this.id, "Up");
  var upClass = this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Up'
  });
  content.push("<td id='");
  content.push(upId);
  content.push("' class='");
  content.push(upClass);
  content.push("'");

  this.addEvents(content, "up");

  content.push("></td></tr>");

  // Generate id and class for down element
  var downId = Zapatec.Spinner.generateString(
          "zpSpin", this.id, "Down");
  var downClass = this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Down'
  });
  content.push("<tr><td id='");
  content.push(downId);
  content.push("' class='");
  content.push(downClass);
  content.push("'");

  this.addEvents(content, "down");

  content.push("></td></tr>");

  content.push("<tr></tr></tbody></table>");

  var html = content.join("");

  this.fireEvent("afterCreate");

  // Make an intermediate html element that will hold our spinner html
  var wrapper = document.createElement("span");
  wrapper.innerHTML = html;
  // Append html wrapper to the specified parent
  this.config.parent.appendChild(wrapper);

  // Store reference to spin text box
  this.createProperty(this, "input", wrapper.childNodes[0].
          childNodes[0].childNodes[0].childNodes[0]);
  // Store reference to spin up arrow button
  this.createProperty(this, "upButton", wrapper.childNodes[0].
          childNodes[0].childNodes[0].childNodes[1]);
  // Store reference to spin down arrow button
  this.createProperty(this, "downButton", wrapper.childNodes[0].
          childNodes[0].childNodes[1].childNodes[0]);
}

/**
 * Gets current spinner value
 */
Zapatec.Spinner.prototype.getValue = function()  {
  return this.config.value;
}

/**
 * Sets current spinner value
 *
 * @param {number} value New spinner value
 */
Zapatec.Spinner.prototype.setValue = function(value)  {
  this.config.value = value;

  this.fireEvent("valueChanged");

  this.updateText();
}

/**
 * Update spinner text based on current value
 *
 * @param {number} value New spinner value
 */
Zapatec.Spinner.prototype.updateText = function()  {
  // Get current value
  var value = this.getValue();

  var str = '';
  // If spinner value is set
  if (null != value) {
    str += value;
    // If trailing zeros are to be added
    if (this.config.minTextLength) {
      while (str.length < this.config.minTextLength) {
        // Add a trailing zero
        str = '0' + str;
      }
    }
  }
  // Set value
  this.input.innerHTML = str;
}

/**
 * Configures the widget. Gets called from init and reconfigure methods of
 * superclass.
 *
 * @private
 * @param {object} objArgs User configuration
 */
Zapatec.Spinner.prototype.configure = function(objArgs) {
  // Id or reference to html element that is to contain our spinner
  this.defineConfigOption('parent', null);
  // Current value
  this.defineConfigOption('value', null);
  // Delta value
  this.defineConfigOption('delta', 1);
  // Minimum value
  this.defineConfigOption('min', null);
  // Maximum value
  this.defineConfigOption('max', null);
  // Allow trailing zeros to maintain minimum text length
  this.defineConfigOption('minTextLength', null);
  // Event repeat timeout
  this.defineConfigOption('repeatTimeout', 500);
  // Event repeat timeout acceleration
  this.defineConfigOption('repeatAcceleration', 0.8);

  // Call parent method
  Zapatec.Spinner.SUPERclass.configure.call(this, objArgs);

  // Check passed config options and correct them if needed
  this.config.parent = Zapatec.Widget.getElementById(this.config.parent);
  if (!this.config.parent) {
    Zapatec.Log({description: "Can't find parent for spinner (\"parent\" config option)"});
    return;
  }

  this.pressedArrow = 0;
  this.repeatTimer = null;
  this.repeatTimeout = this.config.repeatTimeout;
  this.repeatAcceleration = this.config.repeatAcceleration;
};

/**
 * Attaches mouse event listeners to provided html element
 *
 * @private
 * @param {object} el html element
 */
Zapatec.Spinner.prototype.addEvents = function(content, arg) {

  content.push(" onmouseover='Zapatec.Widget.callMethod(");
  content.push(this.id);
  content.push(", \"onMouseOver\", \"");
  content.push(arg);
  content.push("\")'");

  content.push(" onmousedown='return Zapatec.Widget.callMethod(");
  content.push(this.id);
  content.push(", \"onMouseDown\", \"");
  content.push(arg);
  content.push("\");'");

  content.push(" onmouseup='Zapatec.Widget.callMethod(");
  content.push(this.id);
  content.push(", \"onMouseUp\", \"");
  content.push(arg);
  content.push("\")'");

  content.push(" onmouseout='Zapatec.Widget.callMethod(");
  content.push(this.id);
  content.push(", \"onMouseOut\", \"");
  content.push(arg);
  content.push("\")'");

  // Handle double clicks in IE
  if (Zapatec.is_ie) {
    content.push(" ondblclick='Zapatec.Widget.callMethod(");
    content.push(this.id);
    content.push(", \"onMouseDblClick\", \"");
    content.push(arg);
    content.push("\")'");
  }
}

/**
 * Change spinner value in specified direction
 *
 * @private
 * @param {number} direction direction in which to change value
 */
Zapatec.Spinner.prototype.adjustValue = function(direction) {
  var value = this.getValue();
  if (0 < direction) {
    value += this.config.delta;
  }
  else if (direction < 0) {
    value -= this.config.delta;
  }

  // Check for lower limit
  if (this.config.min) {
    if (value < this.config.min) {
      value = this.config.min;
    }
  }

  // Check for upper limit
  if (this.config.max) {
    if (this.config.max < value) {
      value = this.config.max;
    }
  }
  
  this.setValue(value);
}

/**
 * Handles timeout of repeat timer
 *
 * @private
 * @param {number} direction direction in which to change value
 */
Zapatec.Spinner.prototype.onTimeout = function() {
  // If no arrow is pressed
  if (!this.pressedArrow) {
    // Reset repeat timer
    this.resetTimer();

    return;
  }

  this.adjustValue(this.pressedArrow);

  // Schedule next timeout
  this.invokeTimer();
}

/**
 * Reset repeat timer to initial values.
 *
 * @private
 * @param {number} direction direction in which to change value
 */
Zapatec.Spinner.prototype.resetTimer = function() {
  // Reset repeat timeout
  this.repeatTimeout = this.config.repeatTimeout;
  if (this.repeatTimer) {
    clearTimeout(this.repeatTimer);
  }
}

/**
 * Change spinner value in specified direction
 *
 * @private
 * @param {number} direction direction in which to change value
 */
Zapatec.Spinner.prototype.invokeTimer = function() {
  var self = this;
  this.repeatTimer = setTimeout(function() {
    self.onTimeout();
  }, this.repeatTimeout );

  // Adjust repeat timer timeout
  this.repeatTimeout = Math.round(
          this.repeatTimeout * this.config.repeatAcceleration);
}

/**
 * Get source of event
 *
 * @private
 * @param {object} el html element
 */
Zapatec.Spinner.prototype.getEventSource = function(arg) {
  var source = null;
  if (arg == "up") {
    source = this.upButton;
  }
  else if (arg == "down") {
    source = this.downButton;
  }
  return source;
}

/**
 * Handles mouse over events
 *
 * @private
 * @param {object} ev Event
 */
Zapatec.Spinner.prototype.onMouseOver = function(arg) {
  var source = this.getEventSource(arg);

  Zapatec.Utils.addClass(source, this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Hover'
  }));
}

/**
 * Handles mouse down events
 *
 * @private
 * @param {object} ev Event
 */
Zapatec.Spinner.prototype.onMouseDown = function(arg) {
  var source = this.getEventSource(arg);

  Zapatec.Utils.addClass(source, this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Pressed'
  }));

  var direction;
  if (arg == "up") {
    direction = +1;
  }
  else if (arg == "down") {
    direction = -1;
  }

  // Mark that arrow is pressed
  this.pressedArrow = direction;
  // Change spinner value as a result to this mouse click
  this.adjustValue(direction);

  // Reset repeat timer
  this.resetTimer();
  // Start a timer for eventual event repeats
  this.invokeTimer();

  // Create mouse up listener for the whole body
  var self = this;
  var mouseUpGlobalHandler = function(ev) {
    self.onMouseUp(arg);

    Zapatec.Utils.removeEvent(document.body, "mouseup", mouseUpGlobalHandler);
  }
  // Add mouae up listener to document body
  Zapatec.Utils.addEvent(document.body, "mouseup", mouseUpGlobalHandler);
  
  return false;
}

/**
 * Handles mouse double click events
 *
 * @private
 * @param {object} ev Event
 */
Zapatec.Spinner.prototype.onMouseDblClick = function(arg) {
  var source = this.getEventSource(arg);

  var direction;
  if (arg == "up") {
    direction = +1;
  }
  else if (arg == "down") {
    direction = -1;
  }

  // Change spinner value as a result to this mouse click
  this.adjustValue(direction);

  return false;
}

/**
 * Handles mouse up events
 *
 * @private
 * @param {object} ev Event
 */
Zapatec.Spinner.prototype.onMouseUp = function(arg) {
  var source = this.getEventSource(arg);

  Zapatec.Utils.removeClass(source, this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Pressed'
  }));

  // Mark that arrow is released
  this.pressedArrow = 0;
}

/**
 * Handles mouse out events
 *
 * @private
 * @param {object} ev Event
 */
Zapatec.Spinner.prototype.onMouseOut = function(arg) {
  var source = this.getEventSource(arg);

  Zapatec.Utils.removeClass(source, this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Pressed'
  }));
  Zapatec.Utils.removeClass(source, this.getClassName({
    prefix: 'zpSpinner',
    suffix: 'Hover'
  }));
}

Zapatec.Spinner.generateString = function(){
	var arr = [];

	for(var ii = 0; ii < arguments.length; ii++){
		arr.push(arguments[ii]);
	}

	return arr.join("");
}
