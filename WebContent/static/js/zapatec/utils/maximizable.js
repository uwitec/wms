//$Id: maximizable.js 6643 2007-03-18 20:10:03Z vkulov $
/**
 * This is Zapatec.Utils.Maximizer object definition.
 * This is done in the "interface" manner.
 * It represents most of the routines and
 * events connected to maximizing of the object.
 * @param config {object} - all parameters are passed as the properties of this object.
 *
 * Constructor recognizes the following properties of the config object
 * \code
 *    prop. name     | description
 *  -------------------------------------------------------------------------------------------------
 *  container        | {HTML element} The main element to be maximized (default null).
 *  maximizedBorder | [number] A margin between the widget when in maximized
 *                  | mode and the browser window (pixels)
 *
 * Each maximizable objects fires the following events during operation:
 * \code
 *    event. name     | description
 *  -------------------------------------------------------------------------------------------------
 *  onBeforeMaximize    | {event} Called when object is about to be maximized.
 *  onBeforeRestore     | {event} Called when object is about to be restored.
 *  onAfterRestore      | {event} Called after object has been restored.
 *  onAfterSize        | {event} Called on each window resize.
 *
 * \endcode
 */
Zapatec.Utils.Maximizable = function(config) {
	Zapatec.Utils.Maximizable.SUPERconstructor.call(this, config);
};

Zapatec.Utils.Maximizable.id = "Zapatec.Utils.Maximizable";
Zapatec.inherit(Zapatec.Utils.Maximizable, Zapatec.Widget);

/**
 * Inits the object with set of config options.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Maximizable.prototype.init = function(config) {
	//calling parent init
	Zapatec.Utils.Maximizable.SUPERclass.init.call(this, config);

  this.isMaximized = false;
};

/**
 * Sets the default configuration of the object and
 * inits it with user defined values.
 * @param config {object} configuration parameters.
 */
Zapatec.Utils.Maximizable.prototype.configure = function(config) {
	//container, which is actually sized too and also
	//is used as a measurement
	this.defineConfigOption("container", null);
	this.defineConfigOption("theme", null);
  this.defineConfigOption('maximizedBorder', 6);

	// Call parent method
	Zapatec.Utils.Maximizable.SUPERclass.configure.call(this, config);

  config = this.getConfiguration();

  // if required param "container" is a string
  if(typeof(config.container) == "string"){
    // Get DOM element with specified id
    config.container = document.getElementById(this.config.container);
  }

};

/**
 * Reconfigures the object with new parameters.
 * @param config {object} new configuration parameters.
 */
Zapatec.Utils.Maximizable.prototype.reconfigure = function(config) {
	// Call parent method
	Zapatec.Utils.Maximizable.SUPERclass.reconfigure.call(this, config);
};

/**
 * Returns the container of the maximizable object.
 * @return {HTML element or null} container element.
 */
Zapatec.Utils.Maximizable.prototype.getContainer = function() {
	return this.getConfiguration().container;
};

/**
 * Sets if widget is maximized
 *
 * @param isMaximized {boolean} maximized state
 */
Zapatec.Utils.Maximizable.prototype.setMaximized = function(isMaximized) {
  // If maximized state is already set to the same value
  if (isMaximized == this.isMaximized) {
    return;
  }

  this.isMaximized = isMaximized;

  var body;
  if (Zapatec.is_ie)  {
    body = document.getElementsByTagName('html');
  }
  else {
    body = document.getElementsByTagName('body');
  }

  if (true == isMaximized) {
    // Create an object for saving the state before maximization
    this.restoreState = {};

    this.restoreState.containerRestorer = new Zapatec.SRProp(
            this.config.container);

    // saving properties that will be changed
    this.restoreState.containerRestorer.saveProps("style.left", "style.top",
            "style.width", "style.height", "style.position");

    this.fireEvent('onBeforeMaximize', this.restoreState);

    // Store current vertical scroller position
    this.restoreState.scrollTop = Zapatec.Utils.getPageScrollY();
    // Store current onresize handler
    this.restoreState.onresize = window.onresize;

    // Position container
    this.config.container.style.position = 'absolute';
    this.config.container.style.left = this.config.maximizedBorder + 'px';
    this.config.container.style.top = this.config.maximizedBorder + 'px';

    // Scroll to top
    window.scroll(0,0);

    // Store original body overflow style
    this.restoreState.oldBodyOverflow = body[0].style.overflow;
    // Remove body scroller (if any)
    body[0].style.overflow = "hidden";

    var self = this;
    var resizeFunc = function() {
      // Get current browser window size
      var size = Zapatec.Utils.getWindowSize();

      // Determine editor container dimensions
      var width = size.width - 2*self.config.maximizedBorder -4;
      var height = size.height - 2*self.config.maximizedBorder;

      // Set editor container size
      self.setSize(width, height);
    }

    // Size editor according to current browser window size
    resizeFunc();

    // Attach as a window resize event listener
    window.onresize = resizeFunc;
  }
  else {
    this.fireEvent('onBeforeRestore', this.restoreState);

    // Restore old window resize event listener
    window.onresize = this.restoreState.onresize;

    // Restore original body overflow style
    body[0].style.overflow = this.restoreState.oldBodyOverflow;

    // Restore container size and position
    this.restoreState.containerRestorer.restoreProps("style.left", "style.top",
            "style.width", "style.height", "style.position");

    // Scroll to where we were before maximizing
    window.scroll(0, this.restoreState.scrollTop);

    this.fireEvent('onAfterRestore', this.restoreState);

    this.restoreState = null;
  }
}

/**
 * Sets widget size in pixels
 *
 * @public
 * @param {number} width Editor container width
 * @param {number} height Editor container height
 */
Zapatec.Utils.Maximizable.prototype.setSize = function(width, height) {
  if (!width && !height) {
    width = parseInt(this.config.container.style.width);
    height = parseInt(this.config.container.style.height);
  }

  // Set container dimensions
  this.config.container.style.width = width + 'px';
  this.config.container.style.height = height + 'px';

  this.fireEvent('onAfterSize', width, height);
}