/**
 * @fileoverview Zapatec EventDriven library. Base EventDriven class. Contains
 * functions to handle events and basic methods for event-driven class.
 *
 * <pre>
 * Copyright (c) 2004-2007 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/* $Id: zpeventdriven.js 6605 2007-03-13 22:29:20Z alex $ */

if (typeof Zapatec == 'undefined') {
  /**
   * @ignore Namespace definition.
   */
  Zapatec = function() {};
}

/**
 * Base event-driven class.
 * @constructor
 */
Zapatec.EventDriven = function() {};

/**
 * Initializes object.
 * @private
 */
Zapatec.EventDriven.prototype.init = function() {
  // Holds events of this object
  this.events = {};
};

/**
 * Adds event listener to the end of list.
 *
 * <pre>
 * If multiple identical event listeners are registered on the same event, the
 * duplicate instances are discarded. They do not cause the event listener to be
 * called twice, and since the duplicates are discarded, they do not need to be
 * removed manually with the removeEventListener method.
 *
 * Synopsis:
 *
 * oEventDriven.addEventListener('eventName', fEventListener);
 *
 * There is also static method doing the same with global events:
 *
 * Zapatec.EventDriven.addEventListener('globalEventName', fEventListener);
 * </pre>
 *
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 */
Zapatec.EventDriven.prototype.addEventListener = function(sEvent, fListener) {
  if (typeof fListener != "function") {
    return false;
  }
  if (!this.events[sEvent]) {
    this.events[sEvent] = {
      listeners: []
    };
  } else {
    this.removeEventListener(sEvent, fListener);
  }
  this.events[sEvent].listeners.push(fListener);
};

/**
 * Adds event listener to the beginning of list. Note that there is no guarantee
 * that it will be always first in the list. It will become second once this
 * method is called again. Never rely on that!
 *
 * <pre>
 * If multiple identical event listeners are registered on the same event, the
 * duplicate instances are discarded. They do not cause the event listener to be
 * called twice, and since the duplicates are discarded, they do not need to be
 * removed manually with the removeEventListener method.
 *
 * Synopsis:
 *
 * oEventDriven.unshiftEventListener('eventName', fEventListener);
 *
 * There is also static method doing the same with global events:
 *
 * Zapatec.EventDriven.unshiftEventListener('globalEventName', fEventListener);
 * </pre>
 *
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 */
Zapatec.EventDriven.prototype.unshiftEventListener = function(sEvent, fListener) {
  if (typeof fListener != "function") {
    return false;
  }
  if (!this.events[sEvent]) {
    this.events[sEvent] = {
      listeners: []
    };
  } else {
    this.removeEventListener(sEvent, fListener);
  }
  this.events[sEvent].listeners.unshift(fListener);
};

/**
 * Removes event listener.
 *
 * <pre>
 * Synopsis:
 *
 * oEventDriven.removeEventListener('eventName', fEventListener);
 *
 * There is also static method doing the same with global events:
 *
 * Zapatec.EventDriven.removeEventListener('globalEventName', fEventListener);
 * </pre>
 *
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 * @return Number of listeners removed
 * @type number
 */
Zapatec.EventDriven.prototype.removeEventListener = function(sEvent, fListener) {
  if (!this.events[sEvent]) {
    return 0;
  }
  var aListeners = this.events[sEvent].listeners;
  var iRemoved = 0;
  for (var iListener = aListeners.length - 1; iListener >= 0; iListener--) {
    if (aListeners[iListener] == fListener) {
      aListeners.splice(iListener, 1);
      iRemoved++;
    }
  }
  return iRemoved;
};

/**
 * Returns array of listeners for the specified event.
 *
 * <pre>
 * Synopsis:
 *
 * oEventDriven.getEventListeners('eventName');
 *
 * There is also static method doing the same with global events:
 *
 * Zapatec.EventDriven.getEventListeners('globalEventName');
 * </pre>
 *
 * @param {string} sEvent Event name
 * @return Array of function references
 * @type object
 */
Zapatec.EventDriven.prototype.getEventListeners = function(sEvent) {
  if (!this.events[sEvent]) {
    return [];
  }
  return this.events[sEvent].listeners;
};

/**
 * Checks if the event listener is attached to the event.
 *
 * <pre>
 * Synopsis:
 *
 * oEventDriven.isEventListener('eventName', fEventListener);
 *
 * There is also static method doing the same with global events:
 *
 * Zapatec.EventDriven.isEventListener('globalEventName', fEventListener);
 * </pre>
 *
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 * @return True if event listener is attached to the event
 * @type boolean
 */
Zapatec.EventDriven.prototype.isEventListener = function(sEvent, fListener) {
  if (!this.events[sEvent]) {
    return false;
  }
  var aListeners = this.events[sEvent].listeners;
  for (var iListener = aListeners.length - 1; iListener >= 0; iListener--) {
    if (aListeners[iListener] == fListener) {
      return true;
    }
  }
  return false;
};

/**
 * Checks if the event exists.
 *
 * <pre>
 * Synopsis:
 *
 * oEventDriven.isEvent('eventName');
 *
 * There is also static method doing the same with global events:
 *
 * Zapatec.EventDriven.isEvent('globalEventName');
 * </pre>
 *
 * @param {string} sEvent Event name
 * @return Exists
 * @type boolean
 */
Zapatec.EventDriven.prototype.isEvent = function(sEvent) {
  if (this.events[sEvent]) {
    return true;
  }
  return false;
};

/**
 * Removes all listeners for the event.
 *
 * <pre>
 * Synopsis:
 *
 * oEventDriven.removeEvent('eventName');
 *
 * There is also static method doing the same with global events:
 *
 * Zapatec.EventDriven.removeEvent('globalEventName');
 * </pre>
 *
 * @param {string} sEvent Event name
 */
Zapatec.EventDriven.prototype.removeEvent = function(sEvent) {
  if (this.events[sEvent]) {
    var undef;
    this.events[sEvent] = undef;
  }
};

/**
 * Fires event. Takes in one mandatory argument sEvent and optionally
 * any number of other arguments that should be passed to the listeners.
 *
 * <pre>
 * Synopsis:
 *
 * oEventDriven.fireEvent('eventName');
 *
 * There is also static method doing the same with global events:
 *
 * Zapatec.EventDriven.fireEvent('globalEventName');
 * </pre>
 *
 * @param {string} sEvent Event name
 */
Zapatec.EventDriven.prototype.fireEvent = function(sEvent) {
  if (!this.events[sEvent]) {
    return;
  }
  // Duplicate array because it may be modified from within the listeners
  var aListeners = this.events[sEvent].listeners.slice();
  for (var iListener = 0; iListener < aListeners.length; iListener++) {
    // Remove first argument
    var aArgs = [].slice.call(arguments, 1);
    // Call in scope of this object
    aListeners[iListener].apply(this, aArgs);
  }
};

/**
 * Holds global events.
 * @private
 */
Zapatec.EventDriven.events = {};

/**
 * Adds event listener to global event to the end of list.
 *
 * <pre>
 * If multiple identical event listeners are registered on the same event, the
 * duplicate instances are discarded. They do not cause the event listener to be
 * called twice, and since the duplicates are discarded, they do not need to be
 * removed manually with the removeEventListener method.
 * </pre>
 *
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 */
Zapatec.EventDriven.addEventListener = function(sEvent, fListener) {
  if (typeof fListener != "function") {
    return false;
  }
  if (!Zapatec.EventDriven.events[sEvent]) {
    Zapatec.EventDriven.events[sEvent] = {
      listeners: []
    };
  } else {
    Zapatec.EventDriven.removeEventListener(sEvent, fListener);
  }
  Zapatec.EventDriven.events[sEvent].listeners.push(fListener);
};

/**
 * Adds event listener to global event to the beginning of list. Note that there
 * is no guarantee that it will be always first in the list. It will become
 * second once this method is called again. Never rely on that!
 *
 * <pre>
 * If multiple identical event listeners are registered on the same event, the
 * duplicate instances are discarded. They do not cause the event listener to be
 * called twice, and since the duplicates are discarded, they do not need to be
 * removed manually with the removeEventListener method.
 * </pre>
 *
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 */
Zapatec.EventDriven.unshiftEventListener = function(sEvent, fListener) {
  if (typeof fListener != "function") {
    return false;
  }
  if (!Zapatec.EventDriven.events[sEvent]) {
    Zapatec.EventDriven.events[sEvent] = {
      listeners: []
    };
  } else {
    Zapatec.EventDriven.removeEventListener(sEvent, fListener);
  }
  Zapatec.EventDriven.events[sEvent].listeners.unshift(fListener);
};

/**
 * Removes event listener from global event.
 *
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 * @return number of listeners removed
 * @type number
 */
Zapatec.EventDriven.removeEventListener = function(sEvent, fListener) {
  if (!Zapatec.EventDriven.events[sEvent]) {
    return 0;
  }
  var aListeners = Zapatec.EventDriven.events[sEvent].listeners;
  var iRemoved = 0;
  for (var iListener = aListeners.length - 1; iListener >= 0; iListener--) {
    if (aListeners[iListener] == fListener) {
      aListeners.splice(iListener, 1);
      iRemoved++;
    }
  }
  return iRemoved;
};

/**
 * Returns array of listeners for the specified global event.
 *
 * @param {string} sEvent Event name
 * @return Array of function references
 * @type object
 */
Zapatec.EventDriven.getEventListeners = function(sEvent) {
  if (!Zapatec.EventDriven.events[sEvent]) {
    return [];
  }
  return Zapatec.EventDriven.events[sEvent].listeners;
};

/**
 * Checks if the event listener is attached to the global event.
 *
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 * @return True if event listener is attached to the event
 * @type boolean
 */
Zapatec.EventDriven.isEventListener = function(sEvent, fListener) {
  if (!Zapatec.EventDriven.events[sEvent]) {
    return false;
  }
  var aListeners = Zapatec.EventDriven.events[sEvent].listeners;
  for (var iListener = aListeners.length - 1; iListener >= 0; iListener--) {
    if (aListeners[iListener] == fListener) {
      return true;
    }
  }
  return false;
};

/**
 * Checks if the global event exists.
 *
 * @param {string} sEvent Event name
 * @return Exists
 * @type boolean
 */
Zapatec.EventDriven.isEvent = function(sEvent) {
  if (Zapatec.EventDriven.events[sEvent]) {
    return true;
  }
  return false;
};

/**
 * Removes all listeners for the global event.
 *
 * @param {string} sEvent Event name
 */
Zapatec.EventDriven.removeEvent = function(sEvent) {
  if (Zapatec.EventDriven.events[sEvent]) {
    var undef;
    Zapatec.EventDriven.events[sEvent] = undef;
  }
};

/**
 * Fires global event. Takes in one mandatory argument sEvent and optionally
 * any number of other arguments that should be passed to the listeners.
 *
 * @param {string} sEvent Event name
 */
Zapatec.EventDriven.fireEvent = function(sEvent) {
  if (!Zapatec.EventDriven.events[sEvent]) {
    return;
  }
  // Duplicate array because it may be modified from within the listeners
  var aListeners = Zapatec.EventDriven.events[sEvent].listeners.slice();
  for (var iListener = 0; iListener < aListeners.length; iListener++) {
    // Remove first argument
    var aArgs = [].slice.call(arguments, 1);
    // Call listener
    aListeners[iListener].apply(aListeners[iListener], aArgs);
  }
};
