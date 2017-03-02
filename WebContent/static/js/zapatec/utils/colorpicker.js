/* $Id: colorpicker.js 6198 2007-02-07 09:35:37Z vasyl $ */
/**
 * @fileoverview Color Picker widget class derived from Widget. See description
 * of base Widget class at http://trac.zapatec.com:8000/trac/wiki/Widget.
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
* Zapatec ColorPicker object. Creates the element for selecting color
* Displays 216 predefined colors palette, three Sliders and three input fields
* for red, green and blue channels and input field for hex color value.
* @param config [object] - color picker config.
*
* Constructor recognizes the following properties of the config object
* \code
*	property name		| description
*------------------------------------------------------------------------------
*	button		| [string or object] Reference to DOM element.  Optional
*						| Click on this element will popup color picker.
*	inputField		| [string or object] Reference to DOM element. Optional
*						| If option is given - value will be written into this
*						| element.
*	color			| [string] Initial color, format: '#FFCCFF', Optional,
*						| default '#000000'
*	title 		| [string] Title of color picker. Optional, default 'Color Picker'
*	offset  	| [number] offset between button and picker. Optional,
*						| default 10 pixels
* \endcode
*/


/**
 * ColorPicker class.
 *
 * @constructor
 * @extends Zapatec.Widget
 * @param {object} objArgs User configuration
 */
Zapatec.ColorPicker = function(objArgs) {
	if (arguments.length == 0) {
		objArgs = {};
	}
	Zapatec.ColorPicker.SUPERconstructor.call(this, objArgs);
};

/**
 * Unique static id of the widget class. Gives ability for Zapatec#inherit to
 * determine and store path to this file correctly when it is included using
 * Zapatec#include. When this file is included using Zapatec#include or path
 * to this file is gotten using Zapatec#getPath, this value must be specified
 * as script id.
 * @private
 */
Zapatec.ColorPicker.id = 'Zapatec.ColorPicker';
// Inherit Widget
Zapatec.inherit(Zapatec.ColorPicker, Zapatec.Widget);

// Include Zapatec.SliderWidget
Zapatec.include(Zapatec.zapatecPath+'../zpslider/src/slider.js',
		'Zapatec.SliderWidget');

/**
 * Converts decimal value of channel color to hexadecimal value
 *
 * @private
 * @param {number} dec Decimal color channel value
 * @type string
 * @return Hexadecimal value of channel color
 */
Zapatec.ColorPicker.convertChannelColorToHex = function (dec) {
	var hexChars = "0123456789ABCDEF";
	if (dec > 255) {
		return null;
	}
	var i = dec % 16;
	var j = (dec - i) / 16;
	var result = hexChars.charAt(j);
	result += hexChars.charAt(i);
	return result;
}

/**
 * Converts hexadecimal value of color to object with properties:
 * red, green, blue for color channels
 *
 * @private
 * @param {string} hec Hexadecimal color value
 * @type object
 * @return Object that contains values for color channels
 */
Zapatec.ColorPicker.convertHexToColorObject = function (hex) {
	var hexChars = "0123456789ABCDEF";
	var red, green, blue;
	if (hex.match(/#[0123456789ABCDEF]{6}/i)) {
		red = hexChars.indexOf(hex.charAt(1))*16+
			  hexChars.indexOf(hex.charAt(2));
		green = hexChars.indexOf(hex.charAt(3))*16+
				hexChars.indexOf(hex.charAt(4));
		blue = hexChars.indexOf(hex.charAt(5))*16+
			   hexChars.indexOf(hex.charAt(6));
	} else if (hex.match(/[0123456789ABCDEF]{6}/i)) {
		red = hexChars.indexOf(hex.charAt(0))*16+
			  hexChars.indexOf(hex.charAt(1));
		green = hexChars.indexOf(hex.charAt(2))*16+
				hexChars.indexOf(hex.charAt(3));
		blue = hexChars.indexOf(hex.charAt(4))*16+
			   hexChars.indexOf(hex.charAt(5));
	} else {
		red = 0; green = 0; blue = 0;
	}

	return {red:red,green:green,blue:blue};
}


/**
 * Initializes object.
 *
 * @param {object} objArgs User configuration
 */
Zapatec.ColorPicker.prototype.init = function(objArgs) {
	var self = this;
	// Call init method of superclass
	Zapatec.ColorPicker.SUPERclass.init.call(this, objArgs);
	// Generates HTML for ColorPicker
	if (Zapatec.windowLoaded) {
		this.create();
	} else {
		Zapatec.Utils.addEvent(window, "load", function() {
			self.create();
		});
	}
	;

};

/**
 * Configures the widget. Gets called from init and reconfigure methods of
 * superclass.
 *
 * @private
 * @param {object} objArgs User configuration
 */
Zapatec.ColorPicker.prototype.configure = function(objArgs) {
	this.defineConfigOption('button', null);
	this.defineConfigOption('handleButtonClick', true);
	this.defineConfigOption('inputField', null);
	this.defineConfigOption('color','#000000');
	this.defineConfigOption('title','Color picker');
	this.defineConfigOption('offset',10);
	this.defineConfigOption('themePath', Zapatec.zapatecPath +
										 '../zpextra/themes/colorpicker/');

	Zapatec.ColorPicker.SUPERclass.configure.call(this, objArgs);

	// defines field to protect configuration value from changes
	this.currentColor = this.config.color;

	// Gets button and inputField if they are string ID values
	this.config.button = Zapatec.Widget.getElementById(this.config.button);
	this.config.inputField = Zapatec.Widget.getElementById(this.config.inputField);

	// Predefined colors
	this.allColors = [
			['000000', '003300', '006600', '009900', '00CC00', '00FF00',
			'330000', '333300', '336600', '339900', '33CC00', '33FF00',
			'660000', '663300', '666600', '669900', '66CC00', '66FF00'],
			['000033', '003333', '006633', '009933', '00CC33', '00FF33',
			'330033', '333333', '336633', '339933', '33CC33', '33FF33',
			'660033', '663333', '666633', '669933', '66CC33', '66FF33'],
			['000066', '003366', '006666', '009966', '00CC66', '00FF66',
			'330066', '333366', '336666', '339966', '33CC66', '33FF66',
			'660066', '663366', '666666', '669966', '66CC66', '66FF66'],
			['000099', '003399', '006699', '009999', '00CC99', '00FF99',
			'330099', '333399', '336699', '339999', '33CC99', '33FF99',
			'660099', '663399', '666699', '669999', '66CC99', '66FF99'],
			['0000CC', '0033CC', '0066CC', '0099CC', '00CCCC', '00FFCC',
			'3300CC', '3333CC', '3366CC', '3399CC', '33CCCC', '33FFCC',
			'6600CC', '6633CC', '6666CC', '6699CC', '66CCCC', '66FFCC'],
			['0000FF', '0033FF', '0066FF', '0099FF', '00CCFF', '00FFFF',
			'3300FF', '3333FF', '3366FF', '3399FF', '33CCFF', '33FFFF',
			'6600FF', '6633FF', '6666FF', '6699FF', '66CCFF', '66FFFF'],
			['990000', '993300', '996600', '999900', '99CC00', '99FF00',
			'CC0000', 'CC3300', 'CC6600', 'CC9900', 'CCCC00', 'CCFF00',
			'FF0000', 'FF3300', 'FF6600', 'FF9900', 'FFCC00', 'FFFF00'],
			['990033', '993333', '996633', '999933', '99CC33', '99FF33',
			'CC0033', 'CC3333', 'CC6633', 'CC9933', 'CCCC33', 'CCFF33',
			'FF0033', 'FF3333', 'FF6633', 'FF9933', 'FFCC33', 'FFFF33'],
			['990066', '993366', '996666', '999966', '99CC66', '99FF66',
			'CC0066', 'CC3366', 'CC6666', 'CC9966', 'CCCC66', 'CCFF66',
			'FF0066', 'FF3366', 'FF6666', 'FF9966', 'FFCC66', 'FFFF66'],
			['990099', '993399', '996699', '999999', '99CC99', '99FF99',
			'CC0099', 'CC3399', 'CC6699', 'CC9999', 'CCCC99', 'CCFF99',
			'FF0099', 'FF3399', 'FF6699', 'FF9999', 'FFCC99', 'FFFF99'],
			['9900CC', '9933CC', '9966CC', '9999CC', '99CCCC', '99FFCC',
			'CC00CC', 'CC33CC', 'CC66CC', 'CC99CC', 'CCCCCC', 'CCFFCC',
			'FF00CC', 'FF33CC', 'FF66CC', 'FF99CC', 'FFCCCC', 'FFFFCC'],
			['9900FF', '9933FF', '9966FF', '9999FF', '99CCFF', '99FFFF',
			'CC00FF', 'CC33FF', 'CC66FF', 'CC99FF', 'CCCCFF', 'CCFFFF',
			'FF00FF', 'FF33FF', 'FF66FF', 'FF99FF', 'FFCCFF', 'FFFFFF']
			];

};


/**
 * Creates HTML for Color Picker
 * @private
 */
Zapatec.ColorPicker.prototype.create = function() {
	var self = this;
	// is used to process document onClick
	this.isShown = false;

	this.container = Zapatec.Utils.createElement("div");
	document.body.insertBefore(this.container,document.body.firstChild);
	this.container.className = this.getClassName({
		prefix: "zpColorPicker", suffix: "Container"});
	this.container.onclick = function () {
		self.isShown = true;
	}

	// Header DIV
	this.header = Zapatec.Utils.createElement("div", this.container);
	this.header.className = "header";


	this.titleDiv = Zapatec.Utils.createElement("div", this.header);
	this.titleDiv.className = 'title';
	this.titleDiv.innerHTML = this.config.title;
	this.titleDiv.id = "ColorPicker"+this.id+"title";

	// make picker window draggable
	new Zapatec.Utils.Draggable(this.container,{
		handler: this.titleDiv,
		dragCSS:'dragging', 
		onDragInit : function () {
			self.isShown = true;
		},
		onDragMove: function () {
			if (self.WCH) {
				self.WCH.style.left = self.container.style.left;
				self.WCH.style.top = self.container.style.top;
			}
		}
	});

	this.closeDiv = Zapatec.Utils.createElement("div", this.header);
	this.closeDiv.className = 'close';
	this.closeDiv.id = "ColorPicker"+this.id+"close";
	this.closeDiv.onclick = function (){
		self.hide();
	}

	// Palette container
	this.fullPalette = Zapatec.Utils.createElement("div", this.container,true);
	this.fullPalette.className = "fullPalette";

	// RGB input fields container
	this.rgbFields = Zapatec.Utils.createElement("div", this.container,true);
	this.rgbFields.className = "rgbFields";

	// RGB input fields
	this.inputRedContainer =
	Zapatec.Utils.createElement("div", this.rgbFields, true);
	this.inputRedContainer.appendChild(document.createTextNode(' Red: '));
	this.inputRed =
	Zapatec.Utils.createElement("input", this.inputRedContainer, true);
	this.inputRed.size = 3;
	this.inputRed.maxLength = 3;
	this.inputRed.id = "ColorPicker"+this.id+"inputRedField";

	this.inputGreenContainer =
	Zapatec.Utils.createElement("div", this.rgbFields, true);
	this.inputGreenContainer.appendChild(document.createTextNode(' Green: '));
	this.inputGreen =
	Zapatec.Utils.createElement("input", this.inputGreenContainer, true);
	this.inputGreen.size = 3;
	this.inputGreen.maxLength = 3;
	this.inputGreen.id = "ColorPicker"+this.id+"inputGreenField";

	this.inputBlueContainer =
	Zapatec.Utils.createElement("div", this.rgbFields, true);
	this.inputBlueContainer.appendChild(document.createTextNode(' Blue: '));
	this.inputBlue =
	Zapatec.Utils.createElement("input", this.inputBlueContainer, true);
	this.inputBlue.size = 3;
	this.inputBlue.maxLength = 3;
	this.inputBlue.id = "ColorPicker"+this.id+"inputBlueField";

	// Table that contains palette
	this.paletteTable =
	Zapatec.Utils.createElement("table", this.fullPalette, true);
	this.paletteTable.className = 'paletteTable';
	this.paletteTable.cellPadding = 0;
	this.paletteTable.cellSpacing = 0;

	this.paletteTableBody =
		Zapatec.Utils.createElement("tbody", this.paletteTable, true);

	this.paletteRow = [];
	
	for (var ii = 0; ii < this.allColors.length; ii++) {
		this.paletteRow[ii] = Zapatec.Utils.createElement("tr", this.paletteTableBody, true);
		
		this.paletteRow[ii].paletteCell = [];
		
		for (var jj = 0; jj < this.allColors[ii].length; jj++) {
			this.paletteRow[ii].paletteCell[jj] =
				Zapatec.Utils.createElement("td", this.paletteRow[ii]);
			this.paletteRow[ii].paletteCell[jj].style.width = '12px';
			this.paletteRow[ii].paletteCell[jj].style.height = '13px';
			this.paletteRow[ii].paletteCell[jj].id = "ColorPicker"+this.id+"palette_"+ii+"_"+jj;

			var image = Zapatec.Utils.createElement("img", this.paletteRow[ii].paletteCell[jj]);
			image.src = Zapatec.ColorPicker.path + '../zpextra/themes/colorpicker/px.gif';
			image.id = "ColorPicker"+this.id+"palette_"+ii+"_"+jj+"image";
			
			image.height = 13;
			image.width = 12;
			this.colorCell(this.paletteRow[ii].paletteCell[jj], ii, jj);
		}
	}

	this.colorPreview = Zapatec.Utils.createElement("div", this.container);
	this.colorPreview.className = "colorPreview";
	this.colorPreview.id = "ColorPicker"+this.id+"colorPreview";

	var buttonsContainer = Zapatec.Utils.createElement("div", this.container);
	buttonsContainer.className = "buttons";
	var okButton = Zapatec.Utils.createElement("button", buttonsContainer);
	okButton.id = "ColorPicker"+this.id+"ok_button";
	okButton.textContent = "OK";
	okButton.value = "OK";
	okButton.onclick = function () {
		if (self.config.inputField) {
			self.sendValueToinputField();
		}
		self.select();
	}
	var cancelButton = Zapatec.Utils.createElement("button", buttonsContainer);
	cancelButton.id = "ColorPicker"+this.id+"cancel_button";
	cancelButton.value = "Cancel";
	cancelButton.textContent = "Cancel";
	cancelButton.onclick = function () {
		self.hide();
	}

	this.hexFields = Zapatec.Utils.createElement("div", this.container, true);
	this.hexFields.className = "hexFields";
	this.inputHex = Zapatec.Utils.createElement("input", this.hexFields, true);
	this.inputHex.id = "ColorPicker"+this.id+"inputHexField";
	this.inputHex.size = 7;
	this.inputRed.maxLength = 7;

	// Slider containers
	var slidersContainer = Zapatec.Utils.createElement("div", this.container, true);
	slidersContainer.className = "slidersContainer";
	var slidersTable = Zapatec.Utils.createElement("table", slidersContainer, true);
	slidersTable.cellPadding = 1;
	slidersTable.cellSpacing = 0;
	var slidersTableBody = Zapatec.Utils.createElement("tbody", slidersTable, true);

	var redSliderRow = Zapatec.Utils.createElement("tr", slidersTableBody, true);
	var redSliderLabelCol = Zapatec.Utils.createElement("td", redSliderRow);
	this.redSliderSliderCol = Zapatec.Utils.createElement("td", redSliderRow);
	redSliderLabelCol.appendChild(document.createTextNode('Red:'));

	var greenSliderRow = Zapatec.Utils.createElement("tr", slidersTableBody, true);
	var greenSliderLabelCol = Zapatec.Utils.createElement("td", greenSliderRow);
	this.greenSliderSliderCol = Zapatec.Utils.createElement("td", greenSliderRow);
	greenSliderLabelCol.appendChild(document.createTextNode('Green:'));

	var blueSliderRow = Zapatec.Utils.createElement("tr", slidersTableBody, true);
	var blueSliderLabelCol = Zapatec.Utils.createElement("td", blueSliderRow);
	this.blueSliderSliderCol = Zapatec.Utils.createElement("td", blueSliderRow);
	blueSliderLabelCol.appendChild(document.createTextNode('Blue:'));

	// Input fields event handlers
	this.inputRed.onkeyup = function(){
		var value = parseInt(this.value);
		if (isNaN(value)) {
			value = 0;
		} else {
			this.value = value;
		}
		if (this.value < 0 ) {
			this.value=0;
		}
		if (this.value > 255 ) {
			this.value=255;
		}
		self.redSlider.setPos(this.value);
	};
	this.inputGreen.onkeyup = function(){
		var value = parseInt(this.value);
		if (isNaN(value)) {
			value = 0;
		}
		if (this.value < 0 ) {
			this.value=0;
		}
		if (this.value > 255 ) {
			this.value=255;
		}
		self.greenSlider.setPos(this.value);
	};
	this.inputBlue.onkeyup = function(){
		var value = parseInt(this.value);
		if (isNaN(value)) {
			value = 0;
		}
		if (this.value < 0 ) {
			this.value=0;
		}
		if (this.value > 255 ) {
			this.value=255;
		}
		self.blueSlider.setPos(this.value);
	};

	this.inputHex.onchange = function(){
		self.setColor(this.value);
	};

	// Select color handler
	this.colorPreview.onclick = function(){
		if (self.config.inputField) {
			self.sendValueToinputField();
		}
		self.select();
	};

  if (this.config.handleButtonClick) {
    // Show/hide picker event
    this.config.button.onclick = function(){
      self.isShown = true;
      self.show();
    };
  }

	// Hide picker on Esc key and on click at other document element
	Zapatec.Utils.addEvent(document, 'keypress', function(e) {
		if (!e) {
			e = window.event;
		}
		if (e.keyCode == 27) {
			self.hide()
		}
	});
	Zapatec.Utils.addEvent(document, 'click', function() {
		if (!self.isShown) {
			self.hide();
		}
		self.isShown = false;
	});

	// Creating sliders (must be here because sliders needs parent elements
	// to be already created)
	this.createSliders();
}

/**
 * Sets color to palette table sells and attaches event handlers to them.
 * Divided into separate method because of closures.
 * @private
 * @param {object} cell Palette table cell
 * @param {number} iIndex Horizontal index
 * @param {number} jIndex Vertical index
 */
Zapatec.ColorPicker.prototype.colorCell = function(cell, iIndex,jIndex) {
	var self = this;
	cell.style.backgroundColor = '#'+this.allColors[iIndex][jIndex];
	cell.onclick = function () {
		self.setColor(self.allColors[iIndex][jIndex]);
	}
	cell.ondblclick = function () {
		if (self.config.inputField) {
			self.sendValueToinputField();
		}
		self.select();
	}
	cell.onmouseover = function () {
		Zapatec.Utils.addClass(cell,'under');
		Zapatec.Utils.removeClass(self.highlightedCell, 'under');
	}
	cell.onmouseout = function () {
		Zapatec.Utils.removeClass(cell,'under');
	}
}

/**
 * Creates sliders and attaches handlers to them
 * @private
 */
Zapatec.ColorPicker.prototype.createSliders = function() {
	var self = this;
	
	this.redSlider = new Zapatec.Slider({
		div : this.redSliderSliderCol,
		length : 255,
		start: Zapatec.ColorPicker.convertHexToColorObject(this.currentColor).red,
		step: 1,
		orientation : 'H',
		onChange : function (pos) {
			self.inputRed.value = pos;
			self.previewColor();
		},
		newPosition: function(){
			self.isShown = true;
		}
	});

	this.greenSlider = new Zapatec.Slider({
		div : this.greenSliderSliderCol,
		length : 255,
		step: 1,
		start: Zapatec.ColorPicker.convertHexToColorObject(this.currentColor).green,
		orientation : 'H',
		onChange : function (pos) {
			self.inputGreen.value = pos;
			self.previewColor();
		},
		newPosition: function(){
			self.isShown = true;
		}
	});

	this.blueSlider = new Zapatec.Slider({
		div : this.blueSliderSliderCol,
		length : 255,
		start: Zapatec.ColorPicker.convertHexToColorObject(this.currentColor).blue,
		step: 1,
		orientation : 'H',
		onChange : function (pos) {
			self.inputBlue.value = pos;
			self.previewColor();
		},
		newPosition: function(){
			self.isShown = true;
		}
	});
}



/**
 * Calculates top position for picker container based on position of button.
 * @private
 * @type number
 * @return Top position in pixels
 */
Zapatec.ColorPicker.prototype.calculateTopPos = function() {
	var elementOffset = Zapatec.Utils.getElementOffset(this.config.button);
	var top = elementOffset.top - this.container.clientHeight -
			  this.config.offset;
	if (top <= 0) {
		top = elementOffset.top + this.config.button.clientHeight +
			  this.config.offset;
	}
	return top;
}

/**
 * Calculates left position for picker container based on position of button.
 * @private
 * @type number
 * @return Left position in pixels
 */
Zapatec.ColorPicker.prototype.calculateLeftPos = function() {
	var elementOffset = Zapatec.Utils.getElementOffset(this.config.button);
	var left = elementOffset.left + this.config.button.clientWidth +
			   this.config.offset;
	if ((left + this.container.clientWidth) >= document.body.clientWidth) {
		left =
		elementOffset.left - this.container.clientWidth - this.config.offset;
	}
	if (left <= 0) {
		left = elementOffset.left + this.config.button.clientWidth +
			   this.config.offset;
	}

	return left;
}


/**
 * Shows Color Picker
 */
Zapatec.ColorPicker.prototype.show = function () {
	var self = this;
	this.container.style.visibility = 'visible';
	this.container.style.left = this.calculateLeftPos() + "px";
	this.container.style.top =  this.calculateTopPos() + "px";
	// Windowed controls hider
	this.WCH = Zapatec.Utils.createWCH();
	if (this.WCH) {
		this.WCH.style.zIndex = this.container.style.zIndex;
		Zapatec.Utils.setupWCH_el(this.WCH,this.container);
	}
  if (this.config.handleButtonClick) {
    // Attaches hide method to button
    this.config.button.onclick = function(){
      self.isShown = false;
      self.hide();
    };
  }
  this.highlightCell();
}


/**
 * Hides ColorPicker
 */
Zapatec.ColorPicker.prototype.hide = function () {
	var self = this;
	this.container.style.visibility = 'hidden';
	this.container.style.left = '-1000px';
	this.container.style.top = '-1000px';
	if (this.WCH){
		Zapatec.ScrollWithWindow.unregister(this.WCH);
		if (this.WCH.outerHTML) {
			this.WCH.outerHTML = "";
		} else {
			Zapatec.Utils.destroy(this.WCH);
		}
	}
//	Zapatec.Utils.hideWCH(this.WCH);
  if (this.config.handleButtonClick) {
    // Attaches show method to button
    this.config.button.onclick = function() {
      self.isShown = true;
      self.show();
    };
  }
}

/**
 * Returns current color from picker.
 * @type string
 * @return Current color, format '#FFFFFF'
 */

Zapatec.ColorPicker.prototype.getColor = function () {
	return this.currentColor;
}

/**
 * Sets current color. Used to handle changes in Hex input field
 * Sets color channels values to sliders positions
 * @private
 * @param [string] Color, format '#FFFFFF' or 'FFFFFF'
 */
Zapatec.ColorPicker.prototype.setColor = function (hexcolor) {
	var color = Zapatec.ColorPicker.convertHexToColorObject(hexcolor);
	this.redSlider.setPos(color.red);
	this.greenSlider.setPos(color.green);
	this.blueSlider.setPos(color.blue);
}

/**
 * Displays color preview based on RGB input fields value
 * Sets hex field value and current color
 * @private
 * @param [string] Color, format '#FFFFFF' or 'FFFFFF'
 */
Zapatec.ColorPicker.prototype.previewColor = function () {
	var hexColor = '#' +
		   Zapatec.ColorPicker.convertChannelColorToHex(this.inputRed.value) +
		   Zapatec.ColorPicker.convertChannelColorToHex(this.inputGreen.value) +
		   Zapatec.ColorPicker.convertChannelColorToHex(this.inputBlue.value);
	this.colorPreview.style.backgroundColor = hexColor;
	this.inputHex.value = hexColor;
	this.currentColor = hexColor;
}

/**
 * Highlights palette cell if current color is in predefined colors collection
 * @private
 */
Zapatec.ColorPicker.prototype.highlightCell = function () {
	for (var ii = 0; ii < this.allColors.length; ii++) {
		for (var jj = 0; jj < this.allColors[ii].length; jj++) {
			if (((this.currentColor.charAt(0) == '#') &&
				(this.currentColor == '#' + this.allColors[ii][jj])) ||
				(this.currentColor == this.allColors[ii][jj])) {
				Zapatec.Utils.addClass(this.paletteRow[ii].paletteCell[jj],
										'under');
					this.highlightedCell = this.paletteRow[ii].paletteCell[jj];
			} else {
				Zapatec.Utils.removeClass(this.paletteRow[ii].paletteCell[jj],
											'under');
			}
		}
	}
}

/**
 * Sends selected color value to inputField element
 * If inputField is an INPUT or TEXTAREA - current color is set as its value
 * If inputField is a SELECT - options that corresponds to current color is
 * selected (if present)
 * Otherwise - current color is put into element's innerHtml
 * @private
 */
Zapatec.ColorPicker.prototype.sendValueToinputField = function () {
	if (this.config.inputField != null) {
		var tagName = this.config.inputField.tagName.toUpperCase();
		switch (tagName) {
			case "INPUT":
				this.config.inputField.value = this.currentColor;
				break;
			case "TEXTAREA":
				this.config.inputField.value = this.currentColor;
				break;
			case "SELECT":
				for(var i = 0; i < this.config.inputField.options.length; i++){
					if (this.config.inputField.options[i].value == this.currentColor){
						this.config.inputField.selectedIndex = i;
						break;
					}
				}

				break;
			default:
				this.config.inputField.innerHTML = this.currentColor;
				break;
		}
	}
}

/**
 * Fires select event
 * Sends current color value to inputField element
 * Hides color picker
 * @private
 */
Zapatec.ColorPicker.prototype.select = function () {
	this.fireEvent('select', this.currentColor);
	this.hide();
}


