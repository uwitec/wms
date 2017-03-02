/**
 * @fileoverview Sample of widget class derived from Widget. See description of
 * base Widget class at http://trac.zapatec.com:8000/trac/wiki/Widget.
 *
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/* $Id: zpwidget-sample.js 4439 2006-09-14 20:51:31Z alex $ */

/**
 * Sample widget class.
 *
 * @constructor
 * @extends Zapatec.Widget
 * @param {object} objArgs User configuration
 */
Zapatec.ChildWidget = function(objArgs) {
  // Call constructor of superclass
  Zapatec.ChildWidget.SUPERconstructor.call(this, objArgs);
};

/**
 * Unique static id of the widget class. Gives ability for Zapatec#inherit to
 * determine and store path to this file correctly when it is included using
 * Zapatec#include. When this file is included using Zapatec#include or path
 * to this file is gotten using Zapatec#getPath, this value must be specified
 * as script id.
 * @private
 */
Zapatec.ChildWidget.id = 'Zapatec.ChildWidget';

// Inherit Widget
Zapatec.inherit(Zapatec.ChildWidget, Zapatec.Widget);

/**
 * Initializes object.
 *
 * @param {object} objArgs User configuration
 */
Zapatec.ChildWidget.prototype.init = function(objArgs) {
  // Call init method of superclass
  Zapatec.ChildWidget.SUPERclass.init.call(this, objArgs);
  // Event sample
  this.fireEvent('initStart');
  // Continue initialization
  // ...
  // Call parent method to load data from the specified source
  this.loadData();
  // ...
  // Event sample
  this.fireEvent('initDone', 'sample argument1', 'sample argument2');
};

/**
 * Reconfigures the widget with new config options after it was initialized.
 * May be used to change look or behavior of the widget after it has loaded
 * the data. In the argument pass only values for changed config options.
 * There is no need to pass config options that were not changed.
 *
 * @param {object} objArgs Changes to user configuration
 */
Zapatec.ChildWidget.prototype.reconfigure = function(objArgs) {
  // Call parent method
  Zapatec.ChildWidget.SUPERclass.reconfigure.call(this, objArgs);
  // Redraw or do something else
  // ...
};

/**
 * Configures the widget. Gets called from init and reconfigure methods of
 * superclass.
 *
 * @private
 * @param {object} objArgs User configuration
 */
Zapatec.ChildWidget.prototype.configure = function(objArgs) {
  // Overwrite default config options if needed
  this.defineConfigOption('theme', '');
  // Define new config options
  this.defineConfigOption('option1', 'default value');
  this.defineConfigOption('option2', 'default value');
  // Call parent method
  Zapatec.ChildWidget.SUPERclass.configure.call(this, objArgs);
  // Check passed config options and correct them if needed
  // ...
};

/**
 * Loads data from the HTML source. Transforms input data from the HTMLElement
 * object. If needed, loadDataHtmlText, loadDataJson and loadDataXml methods can
 * be implemented as well.
 *
 * @param {object} objSource HTMLElement object
 */
Zapatec.ChildWidget.prototype.loadDataHtml = function(objSource) {
  // Parse source
  var objChild = objSource.firstChild;
  while (objChild) {
    // Do something with child element
    // ...
    // Go to next element
    objChild = objChild.nextSibling;
  }
};
