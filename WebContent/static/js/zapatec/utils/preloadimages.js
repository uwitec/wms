/**
 * @fileoverview Zapatec PreloadImages class definition. Preloads one or several
 * images at once.
 *
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/* $Id: preloadimages.js 4726 2006-10-12 14:21:15Z shacka $ */

/**
 * Zapatec PreloadImages class.
 *
 * @constructor
 * @param {object} objArgs Object properies
 */
Zapatec.ImagePreloader = function(objArgs) {
  // Zapatec.PreloadImages object
  this.job = null;
  // Image object
  this.image = null;
  // Initialize
  if (arguments.length > 0) this.init(objArgs);
};

/**
 * Initializes object.
 *
 * <pre>
 * Takes in following object:
 * {
 *   job: [object] Zapatec.PreloadImages object,
 *   url: [string] image URL,
 *   timeout: [number, optional] number of milliseconds to wait for onload
 *    event before forcing it
 * }
 * </pre>
 *
 * @private
 * @param {object} objArgs Object properies
 */
Zapatec.ImagePreloader.prototype.init = function(objArgs) {
  // Check arguments
  if (!objArgs || !objArgs.job) {
    return;
  }
  // Attach to PreloadImages
  this.job = objArgs.job;
  // Create new Image
  this.image = new Image();
  this.job.images.push(this.image);
  var objPreloader = this;
  this.image.onload = function() {
    objPreloader.job.loadedUrls.push(objArgs.url);
    // IE needs timeout to set image dimensions correctly
    setTimeout(function() {
      objPreloader.onLoad();
    }, 0);
  };
  this.image.onerror = function() {
    objPreloader.job.invalidUrls.push(objArgs.url);
    objPreloader.onLoad();
  };
  this.image.onabort = function() {
    objPreloader.job.abortedUrls.push(objArgs.url);
    objPreloader.onLoad();
  };
  this.image.src = objArgs.url;
  if (typeof objArgs.timeout == 'number') {
    setTimeout(function() {
      if (objPreloader.job) {
        // Onload didn't fire
        if (objPreloader.image.complete) {
          objPreloader.job.loadedUrls.push(objArgs.url);
        } else {
          objPreloader.job.abortedUrls.push(objArgs.url);
        }
        objPreloader.onLoad();
      }
    }, objArgs.timeout);
  }
};

/**
 * Image onload event handler.
 * @private
 */
Zapatec.ImagePreloader.prototype.onLoad = function() {
  // May be called more than once
  if (!this.job) {
    return;
  }
  // Remove handlers to prevent further calls
  this.image.onload = null;
  this.image.onerror = null;
  this.image.onabort = null;
  // Detach from PreloadImages
  var objJob = this.job;
  this.job = null;
  // Reduce counter
  objJob.leftToLoad--;
  // If this was last image
  if (objJob.leftToLoad == 0 && typeof objJob.onLoad == 'function') {
    // We don't need onload handler any more after last image was loaded
    var funcOnLoad = objJob.onLoad;
    objJob.onLoad = null;
    // onLoad callback
    funcOnLoad(objJob);
  }
};

/**
 * @constructor
 * @param {object} objArgs Object properies
 */
Zapatec.PreloadImages = function(objArgs) {
  // Array of Image objects
  this.images = [];
  // Counter to know when all images are loaded
  this.leftToLoad = 0;
  // Array of successfully loaded URLs
  this.loadedUrls = [];
  // Array of invalid URLs
  this.invalidUrls = [];
  // Array of aborted URLs
  this.abortedUrls = [];
  // Onload event handler
  this.onLoad = null;
  // Initialize
  if (arguments.length > 0) this.init(objArgs);
};

/**
 * Initializes object.
 *
 * <pre>
 * Takes in following object:
 * {
 *   urls: [object] array of absolute or relative image URLs to preload,
 *   onLoad: [function, optional] onload event handler,
 *   timeout: [number, optional] number of milliseconds to wait for onload
 *    event before forcing it
 * }
 *
 * onLoad callback function will be called in any case, even if errors occured
 * during loading or loading process was aborted. onLoad callback function
 * receives Zapatec.Transport.PreloadImages object. Its various properies can
 * be used:
 * {
 *   images: [object] array of Image objects,
 *   loadedUrls: [object] array of successfully loaded URLs,
 *   invalidUrls: [object] array of URLs that were not loaded due to errors,
 *   abortedUrls: [object] array of URLs that were not loaded due to abort,
 *   leftToLoad: [number] how many images left to load if event was forced
 * }
 *
 * If onLoad event doesn't fire during long period of time, it can be forced
 * using "timeout" argument.
 * </pre>
 *
 * @private
 * @param {object} objArgs Object properies
 */
Zapatec.PreloadImages.prototype.init = function(objArgs) {
  // Check arguments
  if (!objArgs) {
    return;
  }
  if (!objArgs.urls || !objArgs.urls.length) {
    if (typeof objArgs.onLoad == 'function') {
      // onLoad callback
      objArgs.onLoad(this);
    }
    return;
  }
  // Run job
  this.images = [];
  this.leftToLoad = objArgs.urls.length;
  this.loadedUrls = [];
  this.invalidUrls = [];
  this.abortedUrls = [];
  this.onLoad = objArgs.onLoad;
  // Go through URLs array
  for (var iUrl = 0; iUrl < objArgs.urls.length; iUrl++) {
    // Preload URL
    new Zapatec.ImagePreloader({
      job: this,
      url: objArgs.urls[iUrl],
      timeout: objArgs.timeout
    });
  }
};
