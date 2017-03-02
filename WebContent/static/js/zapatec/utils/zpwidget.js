/**
 * @fileoverview Zapatec Widget library. Base Widget class.
 *
 * <pre>
 * Copyright (c) 2004-2007 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/* $Id: zpwidget.js 6652 2007-03-19 10:19:30Z andrew $ */

if (typeof Zapatec == 'undefined') {
  /**
   * @ignore Namespace definition.
   */
  Zapatec = function() {};
}

/**
 * Base widget class.
 *
 * <pre>
 * Defines following config options:
 *
 * <b>theme</b> [string] Theme name that will be used to display the widget.
 * Corresponding CSS file will be picked and added into the HTML document
 * automatically. Case insensitive. Default: "default".
 * May also contain relative or absolute URL of themes directory.
 * E.g. "../themes/default.css" or "http://my.web.host/themes/default.css".
 *
 * <b>themePath</b> [string] Relative or absolute URL to themes directory.
 * Trailing slash is required. Default: path to child widget's file +
 * "../themes/". You may also include path into "theme" option instead of using
 * "themePath" option.
 *
 * <b>asyncTheme</b> [boolean] Load theme asynchronously. This means that script
 * execution will not be suspended until theme is loaded. Theme will be applied
 * once it is loaded. Default: false.
 *
 * <b>source</b> Depends on "sourceType" option. Possible sources:
 * -----------------------------------------------------------------------------
 * sourceType     | source
 * ---------------|-------------------------------------------------------------
 * 1) "html"      | [object or string] HTMLElement or its id.
 * 2) "html/text" | [string] HTML fragment.
 * 3) "html/url"  | [string] URL of HTML fragment.
 * 4) "json"      | [object or string] JSON object or string (http://json.org).
 * 5) "json/url"  | [string] URL of JSON data source.
 * 6) "xml"       | [object or string] XMLDocument object or XML string.
 * 7) "xml/url"   | [string] URL of XML data source.
 * -----------------------------------------------------------------------------
 *
 * <b>sourceType</b> [string] Used together with "source" option to specify how
 * source should be processed. Possible source types:
 * "html", "html/text", "html/url", "json", "json/url", "xml", "xml/url".
 * JSON format is described at http://www.json.org.
 *
 * <b>callbackSource</b> [function] May be used instead of "source" and
 * "sourceType" config options to get source depending on passed arguments.
 * Receives object with passed arguments. Must return following object:
 * {
 *   source: [object or string] see table above for possible sources,
 *   sourceType: [string] see table above for possible source types
 * }
 *
 * <b>asyncSource</b> [boolean] Load source asynchronously. This means that
 * script execution will not be suspended until source is loaded. Source will be
 * processed once it is loaded. Default: true.
 *
 * <b>reliableSource</b> [boolean] Used together with "json" or "json/url"
 * sourceType to skip JSON format verification. It saves a lot of time for large
 * data sets. Default: true.
 *
 * <b>eventListeners</b> [object] Associative array with event listeners:
 * {
 *   [string] event name: [function] event listener,
 *   ...
 * }
 *
 * Defines internal property <b>config</b>.
 * </pre>
 *
 * @constructor
 * @extends Zapatec.EventDriven
 * @param {object} oArg User configuration
 */
Zapatec.Widget = function(oArg) {
  // User configuration
  this.config = {};
  // Call constructor of superclass
  Zapatec.Widget.SUPERconstructor.call(this);
  // Initialize object
  this.init(oArg);
};

// Inherit EventDriven
Zapatec.inherit(Zapatec.Widget, Zapatec.EventDriven);

/**
 * Holds path to this file.
 * @private
 */
Zapatec.Widget.path = Zapatec.getPath('Zapatec.Widget');

/**
 * Initializes object.
 *
 * <pre>
 * Important: Before calling this method, define config options for the widget.
 * Initially "this.config" object should contain all config options with their
 * default values. Then values of config options will be changed with user
 * configuration in this method. Config options provided by user that were not
 * found in "this.config" object will be ignored.
 *
 * Defines internal property <b>id</b>.
 * </pre>
 *
 * @param {object} oArg User configuration
 */
Zapatec.Widget.prototype.init = function(oArg) {
  // Call parent method
  Zapatec.Widget.SUPERclass.init.call(this);
  // Add this widget to the list
  if (typeof this.id == 'undefined') {
    // Find id
    var iId = 0;
    while (Zapatec.Widget.all[iId]) {
      iId++;
    }
    this.id = iId;
    Zapatec.Widget.all[iId] = this;
  }
  // Configure
  this.configure(oArg);
  // Add custom event listeners
  this.addUserEventListeners();
  // Add standard event listeners
  this.addStandardEventListeners();
  // Load theme
  this.loadTheme();
};

/**
 * Reconfigures the widget with new config options after it was initialized.
 * May be used to change look or behavior of the widget after it has loaded
 * the data. In the argument pass only values for changed config options.
 * There is no need to pass config options that were not changed.
 *
 * <pre>
 * Note: "eventListeners" config option is ignored by this method because it is
 * useful only on initialization. To add event listener after the widget was
 * initialized, use addEventListener method instead.
 * </pre>
 *
 * @param {object} oArg Changes to user configuration
 */
Zapatec.Widget.prototype.reconfigure = function(oArg) {
  // Configure
  this.configure(oArg);
  // Load theme
  this.loadTheme();
};

/**
 * Configures widget.
 *
 * @param {object} oArg User configuration
 */
Zapatec.Widget.prototype.configure = function(oArg) {
  // Default configuration
  this.defineConfigOption('theme', 'default');
  if (typeof this.constructor.path != 'undefined') {
    this.defineConfigOption('themePath', this.constructor.path + '../themes/');
  } else {
    this.defineConfigOption('themePath', '../themes/');
  }
  this.defineConfigOption('asyncTheme', false);
  this.defineConfigOption('source');
  this.defineConfigOption('sourceType');
  this.defineConfigOption('callbackSource');
  this.defineConfigOption('asyncSource', true);
  this.defineConfigOption('reliableSource', true);
  this.defineConfigOption('eventListeners', {});
  // Get user configuration
  if (oArg) {
    for (var sOption in oArg) {
      if (typeof this.config[sOption] != 'undefined') {
        this.config[sOption] = oArg[sOption];
      } else {
        Zapatec.Log({
          description: "Unknown config option: " + sOption
        });
      }
    }
  }
};

/**
 * Returns current configuration of the widget.
 *
 * @return Current configuration
 * @type object
 */
Zapatec.Widget.prototype.getConfiguration = function() {
  return this.config;
};

/**
 * Array to access any widget on the page by its id number.
 * @private
 */
Zapatec.Widget.all = [];

/**
 * Finds a widget by id.
 *
 * @param {number} Widget id
 * @return Widget or undefined if not found
 * @type object
 */
Zapatec.Widget.getWidgetById = function(iId) {
  return Zapatec.Widget.all[iId];
};

/**
 * Saves a property that must be set to null on window unload event. Should be
 * used for properties that can't be deleted by garbage collector in IE 6 due to
 * circular references.
 *
 * <pre>
 * Defines internal property <b>widgetCircularRefs</b>.
 * </pre>
 *
 * @param {object} oElement DOM object
 * @param {string} sProperty Property name
 */
Zapatec.Widget.prototype.addCircularRef = function(oElement, sProperty) {
  if (!this.widgetCircularRefs) {
    // Holds properties of DOM objects that must be set to null on window unload
    // event to prevent memory leaks in IE 6
    this.widgetCircularRefs = [];
  }
  this.widgetCircularRefs.push([oElement, sProperty]);
};

/**
 * Assigns a value to a custom property of DOM object. This property will be
 * set to null on window unload event. Use this function to create properties
 * that can't be deleted by garbage collector in IE 6 due to circular
 * references.
 *
 * @param {object} oElement DOM object
 * @param {string} sProperty Property name
 * @param {any} val Property value
 */
Zapatec.Widget.prototype.createProperty = function(oElement, sProperty, val) {
  oElement[sProperty] = val;
  this.addCircularRef(oElement, sProperty);
};

/**
 * Removes circular references previously defined with method
 * {@link Zapatec.Widget#addCircularRef} or
 * {@link Zapatec.Widget#createProperty} to prevent memory leaks in IE 6.
 * @private
 */
Zapatec.Widget.prototype.removeCircularRefs = function() {
  if (!this.widgetCircularRefs) {
    return;
  }
  for (var iRef = this.widgetCircularRefs.length - 1; iRef >= 0; iRef--) {
    var oRef = this.widgetCircularRefs[iRef];
    oRef[0][oRef[1]] = null;
    oRef[0] = null;
  }
};

/**
 * Deletes a reference to the object from the internal list and calls method
 * {@link Zapatec.Widget#removeCircularRefs}. This lets JavaScript garbage
 * collector to delete an object uless there are any external references to it.
 * Id of discarded object is reused. When you create new instance of Widget,
 * it obtains id of discarded object.
 */
Zapatec.Widget.prototype.discard = function() {
  Zapatec.Widget.all[this.id] = null;
  this.removeCircularRefs();
};

/**
 * Calls method {@link Zapatec.Widget#removeCircularRefs} for each instance of
 * Widget on the page. Should be called only on window uload event.
 * @private
 */
Zapatec.Widget.removeCircularRefs = function() {
  for (var iWidget = Zapatec.Widget.all.length - 1; iWidget >= 0; iWidget--) {
    var oWidget = Zapatec.Widget.all[iWidget];
    if (oWidget) {
      oWidget.removeCircularRefs();
    }
  }
};

// Remove circular references on window uload event to prevent memory leaks in
// IE 6
Zapatec.Utils.addEvent(window, 'unload', Zapatec.Widget.removeCircularRefs);

/**
 * Defines config option if it is not defined yet. Sets default value of new
 * config option. If default value is not specified, it is set to null.
 *
 * @param {string} sOption Config option name
 * @param {any} val Optional. Config option default value
 */
Zapatec.Widget.prototype.defineConfigOption = function(sOption, val) {
  if (typeof this.config[sOption] == 'undefined') {
    if (typeof val == 'undefined') {
      this.config[sOption] = null;
    } else {
      this.config[sOption] = val;
    }
  }
};

/**
 * Adds custom event listeners.
 */
Zapatec.Widget.prototype.addUserEventListeners = function() {
  for (var sEvent in this.config.eventListeners) {
    if (this.config.eventListeners.hasOwnProperty(sEvent)) {
      this.addEventListener(sEvent, this.config.eventListeners[sEvent]);
    }
  }
};

/**
 * Adds standard event listeners.
 */
Zapatec.Widget.prototype.addStandardEventListeners = function() {
  this.addEventListener('loadThemeError', Zapatec.Widget.loadThemeError);
};

/**
 * Displays the reason why the theme was not loaded.
 *
 * @private
 * @param {object} oError Error received from Zapatec.Transport.loadCss
 */
Zapatec.Widget.loadThemeError = function(oError) {
  var sDescription = "Can't load theme.";
  if (oError && oError.errorDescription) {
    sDescription += ' ' + oError.errorDescription;
  }
  Zapatec.Log({
    description: sDescription
  });
};

/**
 * Loads specified theme.
 *
 * <pre>
 * Fires events:
 * <ul>
 * <li><i>loadThemeStart</i> before starting to load theme</li>
 * <li><i>loadThemeEnd</i> after theme is loaded or theme load failed</li>
 * <li><i>loadThemeError</i> after theme load failed. Passes one argument to the
 * listener: error object received from {@link Zapatec.Transport#loadCss}.</li>
 * </ul>
 *
 * Defines internal property <b>themeLoaded</b>.
 * </pre>
 */
Zapatec.Widget.prototype.loadTheme = function() {
  // Correct theme config option
  if (typeof this.config.theme == 'string' && this.config.theme.length) {
    // Remove path
    var iPos = this.config.theme.lastIndexOf('/');
    if (iPos >= 0) {
      iPos++; // Go to first char of theme name
      this.config.themePath = this.config.theme.substring(0, iPos);
      this.config.theme = this.config.theme.substring(iPos);
    }
    // Remove file extension
    iPos = this.config.theme.lastIndexOf('.');
    if (iPos >= 0) {
      this.config.theme = this.config.theme.substring(0, iPos);
    }
    // Make lower case
    this.config.theme = this.config.theme.toLowerCase();
  } else {
    this.config.theme = '';
  }
  // Load theme
  if(this.config.theme){
    this.fireEvent('loadThemeStart');
    this.themeLoaded = false;
    var oWidget = this;
    var sUrl = this.config.themePath + this.config.theme + '.css';
    Zapatec.Transport.loadCss({
      // URL of theme file
      url: sUrl,
      // Suspend script execution until theme is loaded or error received
      async: this.config.asyncTheme,
      // Onload event handler
      onLoad: function() {
        oWidget.fireEvent('loadThemeEnd');
        oWidget.themeLoaded = true;
        oWidget.hideLoader();
      },
      onError: function(oError) {
        oWidget.fireEvent('loadThemeEnd');
        oWidget.fireEvent('loadThemeError', oError);
        oWidget.themeLoaded = true;
        oWidget.hideLoader();
      }
    });
  }
}

/**
 * Forms class name from theme name and provided prefix and suffix.
 *
 * <pre>
 * Arguments object format:
 * {
 *   prefix: [string, optional] prefix,
 *   suffix: [string, optional] suffix
 * }
 * E.g. if this.config.theme == 'default' and following object provided
 * {
 *   prefix: 'zpWidget',
 *   suffix: 'Container'
 * },
 * class name will be 'zpWidgetDefaultContainer'.
 * </pre>
 *
 * @param oArg [object] Arguments object
 * @return Class name
 * @type string
 */
Zapatec.Widget.prototype.getClassName = function(oArg) {
  var aClassName = [];
  if (oArg && oArg.prefix) {
    aClassName.push(oArg.prefix);
  }
  if (this.config.theme != '') {
    aClassName.push(this.config.theme.charAt(0).toUpperCase());
    aClassName.push(this.config.theme.substr(1));
  }
  if (oArg && oArg.suffix) {
    aClassName.push(oArg.suffix);
  }
  return aClassName.join('');
};

/**
 * Forms unique element id from widget id, unique counter and provided prefix
 * and suffix.
 *
 * <pre>
 * Arguments object format:
 * {
 *   prefix: [string, optional] prefix, default: 'zpWidget',
 *   suffix: [string, optional] suffix, default: '-'
 * }
 * E.g. if widget id is 0, unique counter is 1 and following object provided
 * {
 *   prefix: 'zpWidget',
 *   suffix: 'Item'
 * },
 * id will be 'zpWidget0Item1'.
 *
 * Defines internal property <b>widgetUniqueIdCounter</b>.
 * </pre>
 *
 * @param oArg [object] Arguments object
 * @return Element id
 * @type string
 */
Zapatec.Widget.prototype.formElementId = function(oArg) {
  var aId = [];
  if (oArg && oArg.prefix) {
    aId.push(oArg.prefix);
  } else {
    aId.push('zpWidget');
  }
  aId.push(this.id);
  if (oArg && oArg.suffix) {
    aId.push(oArg.suffix);
  } else {
    aId.push('-');
  }
  if (typeof this.widgetUniqueIdCounter == 'undefined') {
    this.widgetUniqueIdCounter = 0;
  } else {
    this.widgetUniqueIdCounter++;
  }
  aId.push(this.widgetUniqueIdCounter);
  return aId.join('');
};

/**
 * @private if theme for current widget is not loaded yet - this method will
 * hide widget container and show loading indicator instead of it.
 */
Zapatec.Widget.prototype.showLoader = function(message){
  if(this.container != null && this.config.theme && !this.themeLoaded){
    // if window content is not fulle loaded - iloading indicator can resize
    // incorrectly
    if(!Zapatec.windowLoaded){
      var self = this;
      Zapatec.Utils.addEvent(window, "load", function(){self.showLoader(message)});
      return null;
    }

    if(typeof(Zapatec.Indicator) == 'undefined'){
      var self = this;

      Zapatec.Transport.loadJS({
        module: 'indicator',
        onLoad: function() {
          // if theme is already loaded - do not show loading indicator
          if(self.themeLoaded){
            return null;
          }

          self.showLoader(message);
        }
      });

      return null;
    }

    this.loader = new Zapatec.Indicator({
      container: this.container,
      themePath: Zapatec.zapatecPath + "../zpextra/themes/indicator/"
    });

    this.loader.start(message || 'loading');
    this.container.style.visibility = 'hidden';
  }
}

/**
 * @private Hides loading indicator created using #showLoader and shows widget.
 * This method will be called automatically on theme loading.
 */
Zapatec.Widget.prototype.hideLoader = function(){
  if(this.loader && this.loader.isActive()){
    this.container.style.visibility = '';
    this.loader.stop();
  }
}

/**
 * Shows widget using given effects and animation speed. You need to define
 * this.container to use this method.
 * @param {object} effects list of effects to apply
 * @param {number} animSpeed possible values - 1..100. Bigger value - more fast animation.
 * @param {function} onFinish Function to call on effect end.
 */
Zapatec.Widget.prototype.showContainer = function(effects, animSpeed, onFinish){
  return this.showHideContainer(effects, animSpeed, onFinish, true);
}

/**
 * Hides widget using given effects and animation speed. You need to define
 * this.container to use this method.
 * @param {object} effects list of effects to apply
 * @param {number} animSpeed possible values - 1..100. Bigger value - more fast animation.
 */
Zapatec.Widget.prototype.hideContainer = function(effects, animSpeed, onFinish){
  return this.showHideContainer(effects, animSpeed, onFinish, false);
}

/**
 * Show/hides widget using given effects and animation speed. You need to define
 * this.container to use this method.
 * @param {object} effects list of effects to apply
 * @param {number} animSpeed possible values - 1..100. Bigger value - more fast animation.
 * @param {boolean} show if true - show widget. Otherwise - hide.
 */
Zapatec.Widget.prototype.showHideContainer = function(effects, animSpeed, onFinish, show){
  if(this.container == null){
    return null;
  }

  if(effects && effects.length > 0 && typeof(Zapatec.Effects) == 'undefined'){
    var self = this;

    Zapatec.Transport.loadJS({
      url: Zapatec.zapatecPath + '../zpeffects/src/effects.js',
      onLoad: function() {
        self.showHideContainer(effects, animSpeed, onFinish, show);
      }
    });

    return false;
  }

  if(animSpeed == null && isNaN(parseInt(animSpeed))){
    animSpeed = 5;
  }

  if(!effects || effects.length == 0){
    if(show){
      this.container.style.display = this.originalContainerDisplay;
      this.originalContainerDisplay = null;
    } else {
      this.originalContainerDisplay = this.container.style.display;
      this.container.style.display = 'none';
    }

    if (onFinish) {
      onFinish();
    }
  } else {
    if(show){
      Zapatec.Effects.show(this.container, animSpeed, effects, onFinish);
    } else {
      Zapatec.Effects.hide(this.container, animSpeed, effects, onFinish);
    }
  }

  return true;
}

/**
 * Loads data from the specified source.
 *
 * <pre>
 * If source is URL, fires events:
 * <ul>
 * <li><i>fetchSourceStart</i> before fetching of source</li>
 * <li><i>fetchSourceError</i> if fetch failed. Passes one argument to the
 * listener: error object received from {@link Zapatec.Transport#fetch}.</li>
 * <li><i>fetchSourceEnd</i> after source is fetched or fetch failed</li>
 * </ul>
 *
 * Fires events:
 * <ul>
 * <li><i>loadDataStart</i> before parsing of data</li>
 * <li><i>loadDataEnd</i> after data are parsed or error occured during
 * fetch</li>
 * </ul>
 *
 * <i>fetchSourceError</i> is fired before <i>fetchSourceEnd</i> and
 * <i>loadDataEnd</i>.
 * </pre>
 *
 * @param {object} oArg Arguments object passed to callbackSource function
 */
Zapatec.Widget.prototype.loadData = function(oArg) {
  // Get source using callback function
  if (typeof this.config.callbackSource == 'function') {
    var oSource = this.config.callbackSource(oArg);
    if (oSource) {
      if (typeof oSource.source != 'undefined') {
        this.config.source = oSource.source;
      }
      if (typeof oSource.sourceType != 'undefined') {
        this.config.sourceType = oSource.sourceType;
      }
    }
  }
  // Process source
  if (this.config.source != null && this.config.sourceType != null) {
    var sSourceType = this.config.sourceType.toLowerCase();
    if (sSourceType == 'html') {
      this.fireEvent('loadDataStart');
      this.loadDataHtml(Zapatec.Widget.getElementById(this.config.source));
      this.fireEvent('loadDataEnd');
    } else if (sSourceType == 'html/text') {
      this.fireEvent('loadDataStart');
      this.loadDataHtmlText(this.config.source);
      this.fireEvent('loadDataEnd');
    } else if (sSourceType == 'html/url') {
      this.fireEvent('fetchSourceStart');
      // Fetch source
      var oWidget = this;
      Zapatec.Transport.fetch({
        // URL of the source
        url: this.config.source,
        // Suspend script execution until source is loaded or error received
        async: this.config.asyncSource,
        // Onload event handler
        onLoad: function(oRequest) {
          oWidget.fireEvent('fetchSourceEnd');
          oWidget.fireEvent('loadDataStart');
          oWidget.loadDataHtmlText(oRequest.responseText);
          oWidget.fireEvent('loadDataEnd');
        },
        // Onerror event handler
        onError: function(oError) {
          oWidget.fireEvent('fetchSourceError', oError);
          oWidget.fireEvent('fetchSourceEnd');
          oWidget.fireEvent('loadDataEnd');
        }
      });
    } else if (sSourceType == 'json') {
      this.fireEvent('loadDataStart');
      if (typeof this.config.source == 'object') {
        this.loadDataJson(this.config.source);
      } else if (this.config.reliableSource) {
        this.loadDataJson(eval('(' + this.config.source + ')'));
      } else {
        this.loadDataJson(Zapatec.Transport.parseJson({
          strJson: this.config.source
        }));
      }
      this.fireEvent('loadDataEnd');
    } else if (sSourceType == 'json/url') {
      this.fireEvent('fetchSourceStart');
      // Fetch source
      var oWidget = this;
      Zapatec.Transport.fetchJsonObj({
        // URL of the source
        url: this.config.source,
        // Suspend script execution until source is loaded or error received
        async: this.config.asyncSource,
        // Skip JSON format verification
        reliable: this.config.reliableSource,
        // Onload event handler
        onLoad: function(oResult) {
          oWidget.fireEvent('fetchSourceEnd');
          oWidget.fireEvent('loadDataStart');
          oWidget.loadDataJson(oResult);
          oWidget.fireEvent('loadDataEnd');
        },
        // Onerror event handler
        onError: function(oError) {
          oWidget.fireEvent('fetchSourceError', oError);
          oWidget.fireEvent('fetchSourceEnd');
          oWidget.fireEvent('loadDataEnd');
        }
      });
    } else if (sSourceType == 'xml') {
      this.fireEvent('loadDataStart');
      if (typeof this.config.source == 'object') {
        this.loadDataXml(this.config.source);
      } else {
        this.loadDataXml(Zapatec.Transport.parseXml({
          strXml: this.config.source
        }));
      }
      this.fireEvent('loadDataEnd');
    } else if (sSourceType == 'xml/url') {
      this.fireEvent('fetchSourceStart');
      // Fetch source
      var oWidget = this;
      Zapatec.Transport.fetchXmlDoc({
        // URL of the source
        url: this.config.source,
        // Suspend script execution until source is loaded or error received
        async: this.config.asyncSource,
        // Onload event handler
        onLoad: function(oResult) {
          oWidget.fireEvent('fetchSourceEnd');
          oWidget.fireEvent('loadDataStart');
          oWidget.loadDataXml(oResult);
          oWidget.fireEvent('loadDataEnd');
        },
        // Onerror event handler
        onError: function(oError) {
          oWidget.fireEvent('fetchSourceError', oError);
          oWidget.fireEvent('fetchSourceEnd');
          oWidget.fireEvent('loadDataEnd');
        }
      });
    }
  } else {
    this.fireEvent('loadDataStart');
    this.loadDataHtml(Zapatec.Widget.getElementById(this.config.source));
    this.fireEvent('loadDataEnd');
  }
};

/**
 * Loads data from the HTML source. Override this in child class.
 *
 * @param {object} oSource Source HTMLElement object
 */
Zapatec.Widget.prototype.loadDataHtml = function(oSource) {};

/**
 * Loads data from the HTML fragment source.
 *
 * @param {string} sSource Source HTML fragment
 */
Zapatec.Widget.prototype.loadDataHtmlText = function(sSource) {
  // Parse HTML fragment
  var oTempContainer = Zapatec.Transport.parseHtml(sSource);
  // Load data
  this.loadDataHtml(oTempContainer.firstChild);
};

/**
 * Loads data from the JSON source. Override this in child class.
 *
 * @param {object} oSource Source JSON object
 */
Zapatec.Widget.prototype.loadDataJson = function(oSource) {};

/**
 * Loads data from the XML source. Override this in child class.
 *
 * @param {object} oSource Source XMLDocument object
 */
Zapatec.Widget.prototype.loadDataXml = function(oSource) {};

/**
 * Loads data passed from other widget to view or edit them. Override this in
 * child class.
 *
 * <pre>
 * Argument object format:
 * {
 *   data: [any] data in format specific for each widget
 * }
 *
 * Fires event:
 * <ul>
 * <li><i>editData</i>. Listener receives argument object passed to this
 * method.</li>
 * </ul>
 * </pre>
 *
 * @param {object} oArg Argument object
 */
Zapatec.Widget.prototype.editData = function(oArg) {
  this.fireEvent('editData', oArg);
};

/**
 * Returns edited data. Override this in child class.
 *
 * @return Edited data in format specific for each widget.
 * @type any
 */
Zapatec.Widget.prototype.editDataGet = function() {
  return null;
};

/**
 * Cancels editing. Ususally just hides the widget.
 *
 * <pre>
 * Fires event:
 * <ul>
 * <li><i>editDataCancel</i> before hiding of widget</li>
 * </ul>
 * </pre>
 */
Zapatec.Widget.prototype.editDataCancel = function() {
  this.fireEvent('editDataCancel');
  if (typeof this.hide == 'function') {
    this.hide();
  }
};

/**
 * Returns edited data back to the widget from which they were received. Passes
 * data to {@link Zapatec.Widget#editDataReceive} method of that widget. Then
 * calls {@link Zapatec.Widget#editDataCancel} to hide this widget.
 *
 * <pre>
 * Argument object format:
 * {
 *   widget: [object] widget object
 * }
 *
 * Fires event:
 * <ul>
 * <li><i>editDataReturn</i> before passing data to the specified widget.
 * Listener receives argument object passed to this method.</li>
 * </ul>
 * </pre>
 *
 * @param {object} oArg Argument object
 */
Zapatec.Widget.prototype.editDataReturn = function(oArg) {
  this.fireEvent('editDataReturn', oArg);
  if (!oArg.widget || typeof oArg.widget.editDataReceive != 'function') {
    return;
  }
  oArg.widget.editDataReceive({
    data: this.editDataGet()
  });
  this.editDataCancel();
};

/**
 * Receives data back from other widget previosly passed to it using its
 * {@link Zapatec.Widget#editData} method. Override this in child class.
 *
 * <pre>
 * Argument object format:
 * {
 *   data: [any] data in format specific for each widget
 * }
 *
 * Fires event:
 * <ul>
 * <li><i>editDataReceive</i>. Listener receives argument object passed to this
 * method.</li>
 * </ul>
 * </pre>
 *
 * @param {object} oArg Argument object
 */
Zapatec.Widget.prototype.editDataReceive = function(oArg) {
  this.fireEvent('editDataReceive', oArg);
};

/**
 * Finds a widget by id and calls specified method with specified arguments and
 * returns value from that method.
 *
 * @param {number} iWidgetId Widget id
 * @param {string} sMethod Method name
 * @param {any} any Any number of arguments
 * @return Value returned from the method
 * @type any
 */
Zapatec.Widget.callMethod = function(iWidgetId, sMethod) {
  // Get Widget object
  var oWidget = Zapatec.Widget.getWidgetById(iWidgetId);
  if (oWidget && typeof oWidget[sMethod] == 'function') {
    // Remove first two arguments
    var aArgs = [].slice.call(arguments, 2);
    // Call method
    return oWidget[sMethod].apply(oWidget, aArgs);
  }
};

/**
 * Converts element id to reference.
 *
 * @param {string} element Element id
 * @return Reference to element
 * @type object
 */
Zapatec.Widget.getElementById = function(element) {
  if (typeof element == 'string') {
    return document.getElementById(element);
  }
  return element;
};

/**
 * Returns style attribute of the specified element.
 *
 * @param {object} element Element
 * @return Style attribute value
 * @type string
 */
Zapatec.Widget.getStyle = function(element) {
  var style = element.getAttribute('style') || '';
  if (typeof style == 'string') {
    return style;
  }
  return style.cssText;
};
