// $Id: dropdown.js 6862 2007-04-04 14:59:52Z vasyl $
/*
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/**
*  This is simple class for creating drop-down lists.
* @param config [object] - pane config.
*
* Constructor recognizes the following properties of the config object
* \code
*	property name			| description
*------------------------------------------------------------------------------
*	element					| [string or object] Reference to DOM element.
*							|	Created list will be displayed below it.
*							|	Required.
*	hook					| [string or object] Element that invokes dropdown.
*							|	Optional.
*	onselect				| [function] Function that will be called when user
*							|	click on some row in dropdown. One argument
*							|	will be passed to function: array of value in
*							|	clicked row.
* \endcode
*/
Zapatec.DropDown = function(objArgs){
	if(arguments.length == 0){
		objArgs = {};
	}

	Zapatec.DropDown.SUPERconstructor.call(this, objArgs);
}

// Inherit SuperClass
Zapatec.inherit(Zapatec.DropDown, Zapatec.Widget);

Zapatec.DropDown.prototype.init = function(objArgs){
	this.config.element = null;
	this.config.hook = null;
	this.config.onselect = null;

	// processing Widget functionality
	Zapatec.DropDown.SUPERclass.init.call(this, objArgs);

	if(typeof(this.config.element) == 'string'){
		this.config.element = document.getElementById(this.config.element);
	}

	if(this.config.element == null){
		throw "No target element given";
	}

	// dropdown container
	this.container = null;

	// table that will contain list
	this.table = null;

	// table header
	this.header = null;

	// table body
	this.body = null;

	// two-dimensional array with values.
	this.content = {};

	var self = this;

	if(this.config.hook){
		// do not hide dropdown if user clicks on hood element
		Zapatec.Utils.addEvent(this.config.hook, 'click', function(){self.isVisible = true;});
	}

	// hide dropdown if ESC key was pressed
	Zapatec.Utils.addEvent(document, 'keypress', function(e) {
		if (!e){
			e = window.event;
		}

		if (e.keyCode == 27){
			self.hide();
		}
	});

	// hide dropdown if user clicks anywhere except dropdown or hook.
	Zapatec.Utils.addEvent(document, 'click', function(e) {
		if(!self.isVisible){
			self.hide();
		}

		self.isVisible = false;
	});
}

/**
* Returns reference to DropDown container element
* @return reference to DropDown container element
*/
Zapatec.DropDown.prototype.getContainer = function(){
	return this.container;
}

/**
* Set dropdown content to given array
*	@param objSource - [object] JSON object with structure like:
*	{
*		"header": [ // describes list header. Optional
*			{
*				name: "Col name1", // column name
*				style: "color: blue", // apply this style to current column header
*				colStyle: "color: blue" // apply this style to all cells in this col
*				colClassName: "customCol" // add this class to all cells in this col
*			},
*			...
*		],
*		"body": [ // describes list content. Required.
*			["str1, col1", "str1, col2"], // array with values
*			...
*		]
*	}
*/
Zapatec.DropDown.prototype.setContent = function(objSource){
	if(objSource == null){
		return null;
	}

	this.content = objSource;
}

/**
* Shows dropdown list.
*/
Zapatec.DropDown.prototype.show = function(){
	if(this.container != null){
		this.hide();
	}

	this.isVisible = false;

	// create dropdown container
	this.container = Zapatec.Utils.createElement("div");
	this.container.className = this.getClassName({prefix: "zpDropDown", suffix: "Container"})
	this.container.style.position = 'absolute';
	this.container.style.display = 'none';
	this.table = Zapatec.Utils.createElement("table");
	this.table.border = 0;
	this.table.cellSpacing = 0;
	this.container.appendChild(this.table);

	this.container.style.zIndex = Zapatec.Utils.getMaxZindex();
	var self = this;
	this.container.onclick = function(){self.isVisible = true;}

	this.header = this.table.appendChild(document.createElement("thead"));
	this.body = this.table.appendChild(document.createElement("tbody"));

	document.body.appendChild(this.container);

	// fill dropdown list with data
	this.fillRows();

	// position container to config.element
	var pos = Zapatec.Utils.getElementOffset(this.config.element);
	this.container.style.left = pos.x + "px";
	this.container.style.top = (pos.y + this.config.element.offsetHeight) + "px";
	this.container.style.visibility = "hidden";
	this.container.style.display = 'block';
	this.table.width = "";
	var oldTableWidth = this.table.scrollWidth;
	this.container.style.width = this.table.scrollWidth + "px";
	this.table.width = "100%";

	// if container height is more them 200px - put scroller.
	if(this.container.clientHeight > 200){
		var scrollEl = Zapatec.is_gecko ? this.body : this.container;
		scrollEl.className += " " + this.getClassName({prefix: "zpDropDown", suffix: "Overflowed"});

		var wid = (2*scrollEl.scrollWidth - scrollEl.clientWidth) + "px";
		this.container.style.width = wid;
		this.table.width = wid;
	}

	// use WCH for hide SELECTs under dropdown.
	this.WCH = Zapatec.Utils.createWCH();
	if(this.WCH){
		Zapatec.Utils.setupWCH(this.WCH, parseInt(this.container.style.left), parseInt(this.container.style.top), this.container.clientWidth, this.container.clientHeight);
		this.WCH.style.zIndex = this.container.style.zIndex - 1;
	}

	this.container.style.visibility = "";
}

/**
* Hide dropdown list.
*/
Zapatec.DropDown.prototype.hide = function(){
	if(this.container != null){
		Zapatec.Utils.destroy(this.container);
		this.container = null;

		if(this.WCH){
			Zapatec.Utils.destroy(this.WCH);
		}
	}
}

/**
* \internal Clear table content.
*/

Zapatec.DropDown.prototype.clear = function(){
	if(this.header != null){
		for(var ii = this.header.childNodes.length - 1; ii >= 0 ; ii--){
			this.header.removeChild(this.header.childNodes[ii]);
		}
	}

	if(this.body != null){
		for(var ii = this.body.childNodes.length - 1; ii >= 0 ; ii--){
			this.body.removeChild(this.body.childNodes[ii]);
		}
	}
}

/**
* \internal Fills table with data.
*/
Zapatec.DropDown.prototype.fillRows = function(){
	this.clear();
	var tr = null;
	var td = null;

	if(
		this.content.header != null &&
		this.content.header.length > 0
	){
		tr = this.header.appendChild(document.createElement("tr"));
		tr.className = "dropDownHeader";

		for(var ii = 0; ii < this.content.header.length; ii++){
			var col = this.content.header[ii];

			td = tr.appendChild(document.createElement("td"));

			if(col.className != null){
				td.className = col.className
			}

			if(col.style != null){
				Zapatec.Utils.applyStyle(td, col.style);
			}

			td.appendChild(document.createElement("span")).innerHTML = col.name;
		}
	}

	var colspan = 1;
	if(
		this.content.header != null
	){
		colspan = this.content.header.length;
	} else if(this.content.body != null){
		colspan = this.content.body[0].length;
	}

	// display "No records" if there is no records for content.body
	if(
		this.content.body == null ||
		this.content.body.length == 0
	){
		tr = this.header.appendChild(document.createElement("tr"));
		td = tr.appendChild(document.createElement("td"));
		td.className = "noRecords";
		td.colSpan = colspan;

		td.appendChild(document.createTextNode("No records!"))
	} else {
		var isOdd = false;

		for(var ii = 0; ii < this.content.body.length; ii++){
			tr = this.body.appendChild(document.createElement("tr"));
			tr.style.width = "100%"
			tr.className = "dropDown" + ((isOdd = !isOdd) ? "Odd" : "Even");

			var self = this;

			// when user clicks on row - select it.
			tr.onclick = new Function("var self = Zapatec.Widget.all['" + this.id + "']; self.selectValue(" + ii + "); return true;")

			if(Zapatec.is_ie){
				tr.onmouseover = function(){this.className += ' dropDownHighlighted'}
				tr.onmouseout = function(){this.className = this.className.replace(' dropDownHighlighted', "")}
			}

			for(var jj = 0; jj < this.content.body[ii].length; jj++){
				td = tr.appendChild(document.createElement("td"));

				if(
					this.content.header != null &&
					this.content.header[jj] != null
				){
					if(this.content.header[jj].colStyle != null){
						Zapatec.Utils.applyStyle(td, this.content.header[jj].colStyle);
					}

					if(this.content.header[jj].colClassName != null){
						td.className = this.content.header[jj].colClassName;
					}
				}

				td.appendChild(document.createElement("span")).innerHTML = this.content.body[ii][jj];
			}
		}
	}
}

/**
* \internal calls user defined function and send values from clicked string to
* it
*/
Zapatec.DropDown.prototype.selectValue = function(currentRow){
	if(
		this.config.onselect != null &&
		this.content != null &&
		currentRow < this.content.body.length
	){
		this.config.onselect(this.content.body[currentRow])
		this.hide();
	}
}

Zapatec.DropDown.prototype.setWidth = function(width){
	width = parseInt(width);

	if(isNaN(width)){
		return false;
	}

	this.container.style.width = width + "px";
	this.table.style.width = width + "px";

}
