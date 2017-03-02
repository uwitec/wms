/* $Id: progressbar.js 4907 2006-10-25 15:09:26Z andrew $ */
/**
 * @fileoverview Progress Bar widget class derived from Widget. See 
 * description of base Widget class at 
 * http://trac.zapatec.com:8000/trac/wiki/Widget.
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
* Zapatec ProgressBar object. Creates the element for displaying progress
* of some process and gives an interface to work with it.
* @param config [object] - progress bar config.
*
* Constructor recognizes the following properties of the config object
* \code
*	property name	| description
*-------------------------------------------------------------------------------------------------
*	parent				| [string or object] Reference to DOM element where
*								| newly created ProgressBar will be placed.
*	width					| [number] width of the ProgressBar in "px"
*	height				| [number] height of the ProgressBar in "px"
*	minValue			| [number] minimal value for ProgressBar values
*	maxValue			| [number] maximal value for ProgressBar values
*	value					| [number] actual value for ProgressBar values
*	showLabels		| [string] defines the way of labels displaying
*								| 'none' - no labels is shown
*								| 'values' - label with actual values is shown
*								| 'percents' - label with percentage is shown
*	title					| [string] defines title that will be shown before label if
* 							| showLabels is set to 'values' or 'percents'
* \endcode
*/
/**
 * ProgressBar class.
 *
 * @constructor
 * @extends Zapatec.Widget
 * @param {object} objArgs User configuration
 */
Zapatec.ProgressBar = function(objArgs) {
	if (arguments.length == 0) {
		objArgs = {};
	}
	Zapatec.ProgressBar.SUPERconstructor.call(this, objArgs);
};

/**
 * Unique static id of the widget class. Gives ability for Zapatec#inherit to
 * determine and store path to this file correctly when it is included using
 * Zapatec#include. When this file is included using Zapatec#include or path
 * to this file is gotten using Zapatec#getPath, this value must be specified
 * as script id.
 * @private
 */
Zapatec.ProgressBar.id = 'Zapatec.ProgressBar';
// Inherit Widget
Zapatec.inherit(Zapatec.ProgressBar, Zapatec.Widget);

/**
 * Initializes object.
 *
 * @param {object} objArgs User configuration
 */
Zapatec.ProgressBar.prototype.init = function(objArgs) {
	// Call init method of superclass
	Zapatec.ProgressBar.SUPERclass.init.call(this, objArgs);
	// Generates HTML form ProgressBar
	this.create();
};

/**
 * Configures the widget. Gets called from init and reconfigure methods of
 * superclass.
 *
 * @private
 * @param {object} objArgs User configuration
 */
Zapatec.ProgressBar.prototype.configure = function(objArgs) {
	this.defineConfigOption('parent', null);
	this.defineConfigOption('width', null);
	this.defineConfigOption('height', null);
	this.defineConfigOption('minValue', 0);
	this.defineConfigOption('maxValue', 100);
	this.defineConfigOption('value', null);
	this.defineConfigOption('title', null);
	this.defineConfigOption('showLabels', 'none');
	this.defineConfigOption('themePath', Zapatec.zapatecPath+
										 '../zpextra/themes/progressbar/');

	Zapatec.ProgressBar.SUPERclass.configure.call(this, objArgs);

	// Checks if there is parent element
	this.config.parent = Zapatec.Widget.getElementById(this.config.parent);
	if (!this.config.parent) {
		Zapatec.Log({description: "No reference to parent element."});
		return null;
	}

	/*
	Checks if minimal value is lower than maximal -
	if true minValue is set to maxValue
	*/
	if (this.config.minValue >= this.config.maxValue) {
		Zapatec.Log({description: "Minimal value is greater "+
						 "than or equals to maximal value."});
		throw "Minimal value is greater than or equals to maximal value.";
		return null;
	}


	/*
	Checks if current value is lower than maximal -
	if true current value is set to maxValue
	*/
	if (this.config.value > this.config.maxValue) {
		Zapatec.Log({description: "Actual value is out of bounds."});
		this.config.value = this.config.maxValue;
		return null;
	}

	/*
	 Checks if current value is greater than minimal -
	 if true current value is set to maxValue
	 */
	if (this.config.value < this.config.minValue) {
		Zapatec.Log({description: "Actual value is out of bounds."});
		this.config.value = this.config.minValue;
		return null;
	}

	// defines field to protect configuration value from changes
	this.currentValue = this.config.value;

	// Checks if showLabels parameter has correct value
	if ((this.config.showLabels != 'none') &&
		(this.config.showLabels != 'values') &&
		(this.config.showLabels != 'percents')) {
		Zapatec.Log(
		{description: "Wrong showLabels value. Must be 'none',"+
			"'values' or 'percents'."});
		return null;
	}

};


/**
 * Creates HTML for ProgressBar
 * @private
 */
Zapatec.ProgressBar.prototype.create = function() {
	// Create DIV for all parts of ProgressBar

	// Parent container
	this.container = Zapatec.Utils.createElement("div", this.config.parent);
	this.container.className = this.getClassName({
		prefix: "zpProgressBar", suffix: "Container"});
	this.container.style.width = this.config.width + "px";

	// Container for ProgressBar stripe
	this.internalContainer = Zapatec.Utils.createElement("div",this.container);
	this.internalContainer.className = "internal";
	this.internalContainer.style.height = this.config.height - 4 + "px";
	this.internalContainer.style.width = this.calculatePercentage() + "%";

	// Additional variable to store height if labels are shown
	var additionalHeightForLabels = 0;
	// Container for Labels
	if (this.config.showLabels != 'none') {
		this.labelsContainer = Zapatec.Utils.createElement("div",
				this.container);
		this.labelsContainer.className = "labels";
		this.labelsContainer.style.width = this.config.width - 4 + "px";
		additionalHeightForLabels = this.labelsContainer.offsetHeight;
	}

	this.container.style.height = this.config.height +
								  additionalHeightForLabels +
								  "px";

	this.displayLabel();
}

/*
 * Calculates width for current value
 * @return width in pixels
 * @type int
*/
Zapatec.ProgressBar.prototype.calculateWidth = function() {
	return Math.round(
			(this.currentValue / (this.config.maxValue - this.config.minValue))
		) * this.config.width;
}

/*
  Calculates percentage for current value
 * @return width in percents
 * @type float
*/
Zapatec.ProgressBar.prototype.calculatePercentage = function() {
	return (this.currentValue / (this.config.maxValue - this.config.minValue))
			* 100;
}

/**
 * Redraws Progressbar after changing of currentValue
 */
Zapatec.ProgressBar.prototype.redraw = function() {
	this.internalContainer.style.width = this.calculatePercentage() + "%";
	this.displayLabel();
}

/**
 * Displays labels
 */
Zapatec.ProgressBar.prototype.displayLabel = function(){
	var text = '';
	if (this.config.showLabels == 'values') {
		text = this.currentValue+"/"+
			   (this.config.maxValue-this.config.minValue);
	}
	if (this.config.showLabels == 'percents') {
		text = Math.round(this.calculatePercentage())+"%";
	}
	if (this.config.showLabels != 'none') {
		if (this.config.title != null) {
			text = this.config.title + text; 
		}
		this.labelsContainer.innerHTML = text;
	}

}

/**
 * Set new progress value
 * @param value Progress value
 */
Zapatec.ProgressBar.prototype.setProgress = function (value) {
	value = parseFloat(value);
	if (isNaN(value)) {
		Zapatec.Log({description:'Progress value is not a number!'});
		return false;
	}
	this.currentValue = value;
	this.redraw();
	return null;
}

/**
 * Get current progress
 * @return Current progress value
 * @type float
 */
Zapatec.ProgressBar.prototype.getProgress = function () {
	return this.currentValue;
}

/**
 * Fires cancel event
 */
Zapatec.ProgressBar.prototype.cancel = function () {
	this.fireEvent('cancel', this.currentValue);
}


/**
 * Hides ProgressBar
 */
Zapatec.ProgressBar.prototype.hide = function () {
	this.container.style.display = 'none';
}
