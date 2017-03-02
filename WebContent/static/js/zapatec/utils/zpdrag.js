/**
 * @fileoverview Zapatec Drag library. Used to drag elements. Requires utils.js.
 *
 * <pre>
 * Copyright (c) 2004-2007 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/* $Id: zpdrag.js 6142 2007-01-31 23:31:32Z alex $ */

/**
 * @constructor
 */
Zapatec.Drag = {};

// Emulate window.event in Mozilla for some events. Required for Zapatec.Drag.
Zapatec.Utils.emulateWindowEvent(['mousedown', 'mousemove', 'mouseup']);

/**
 * Holds id of an element that is currently dragged.
 * @private
 */
Zapatec.Drag.currentId = null;

/**
 * Starts dragging an element.
 *
 * <xmp>
 * Synopsis:
 * <div id="myDiv"
 *  onmousedown="return Zapatec.Drag.start(window.event, this.id)">
 * </xmp>
 *
 * <pre>
 * Fires static events:
 * <b>dragStart</b> before dragging is started. Listener receives following
 * object:
 * {
 *   id: [string] element id passed to this function
 * }
 *
 * <b>dragMove</b> on every mouse move while element is dragged. Listener
 * receives following object:
 * {
 *   id: [string] element id passed to this function,
 *   startLeft: [number] initial left offset,
 *   startTop: [number] initial top offset,
 *   prevLeft: [number] previous left offset,
 *   prevTop: [number] previous top offset,
 *   left: [number] current left offset,
 *   top: [number] current top offset
 * }
 *
 * <b>dragEnd</b> after element was dropped. Listener receives following object:
 * {
 *   id: [string] element id passed to this function,
 *   startLeft: [number] initial left offset,
 *   startTop: [number] initial top offset,
 *   left: [number] current left offset,
 *   top: [number] current top offset
 * }
 *
 * Offsets are in pixels and relative to offsetParent of the element.
 *
 * Additional arguments format:
 * {
 *   vertical: [boolean] if true, moves only vertically,
 *   horizontal: [boolean] if true, moves only horizontally,
 *   limitTop: [number] doesn't go beyond this limit when moved up,
 *   limitBottom: [number] doesn't go beyond this limit when moved down,
 *   limitLeft: [number] doesn't go beyond this limit when moved to the left,
 *   limitRight: [number] doesn't go beyond this limit when moved to the right
 * }
 * Limits are compared with offsetTop and offsetLeft properties of the element.
 * </pre>
 *
 * @param {object} oEv Event object
 * @param {object} sId Element id
 * @param {object} oArg Optional. Additional arguments
 * @return Always true
 * @type boolean
 */
Zapatec.Drag.start = function(oEv, sId, oArg) {
  // Check arguments
  if (Zapatec.Drag.currentId) {
    return true;
  }
  var oEl = document.getElementById(sId);
  if (!oEl || oEl.zpDrag) {
    return true;
  }
  if (!oArg) {
    oArg = {};
  }
  // Get mouse position
  var oPos = Zapatec.Utils.getMousePos(oEv || window.event);
  // Notify all that element is dragged
  Zapatec.EventDriven.fireEvent('dragStart', {id: sId});
  // Set properties
  oEl.zpDrag = true;
  oEl.zpDragPageX = oPos.pageX;
  oEl.zpDragPageY = oPos.pageY;
  // offsetLeft doesn't work properly in IE
  if (oEl.offsetParent) {
    var oPos = Zapatec.Utils.getElementOffset(oEl);
    var oPosParent = Zapatec.Utils.getElementOffset(oEl.offsetParent);
    oEl.zpDragLeft = oPos.left - oPosParent.left;
    oEl.zpDragTop = oPos.top - oPosParent.top;
  } else {
    oEl.zpDragLeft = oEl.offsetLeft;
    oEl.zpDragTop = oEl.offsetTop;
  }
  oEl.zpDragPrevLeft = oEl.zpDragLeft;
  oEl.zpDragPrevTop = oEl.zpDragTop;
  oEl.zpDragV = oArg.vertical;
  oEl.zpDragH = oArg.horizontal;
  oEl.zpDragLimTop =
   typeof oArg.limitTop == 'number' ? oArg.limitTop : -Infinity;
  oEl.zpDragLimBot =
   typeof oArg.limitBottom == 'number' ? oArg.limitBottom : Infinity;
  oEl.zpDragLimLft =
   typeof oArg.limitLeft == 'number' ? oArg.limitLeft : -Infinity;
  oEl.zpDragLimRgh =
   typeof oArg.limitRight == 'number' ? oArg.limitRight : Infinity;
  Zapatec.Drag.currentId = sId;
  // Set event listeners
  Zapatec.Utils.addEvent(document, 'mousemove', Zapatec.Drag.move);
  Zapatec.Utils.addEvent(document, 'mouseup', Zapatec.Drag.end);
  // Continue event
  return true;
};

/**
 * Moves element to the current mouse position. Gets called on document
 * mousemove event.
 *
 * @private
 * @param {object} oEv Event object
 * @return Always false
 * @type boolean
 */
Zapatec.Drag.move = function(oEv) {
  // Check arguments
  oEv || (oEv = window.event);
  if (!Zapatec.Drag.currentId) {
    return Zapatec.Utils.stopEvent(oEv);
  }
  var oEl = document.getElementById(Zapatec.Drag.currentId);
  if (!(oEl && oEl.zpDrag)) {
    return Zapatec.Utils.stopEvent(oEv);
  }
  // Get mouse position
  var oPos = Zapatec.Utils.getMousePos(oEv);
  // Calculate element position
  var oOffset = {
    id: Zapatec.Drag.currentId,
    startLeft: oEl.zpDragLeft,
    startTop: oEl.zpDragTop,
    prevLeft: oEl.zpDragPrevLeft,
    prevTop: oEl.zpDragPrevTop,
    left: 0,
    top: 0
  };
  // Check if it is vertical
  if (!oEl.zpDragV) {
    var iLeft = oEl.zpDragLeft + oPos.pageX - oEl.zpDragPageX;
    // Check limits
    if (oEl.zpDragLimLft <= iLeft && oEl.zpDragLimRgh >= iLeft) {
      oEl.style.right = '';
      oEl.style.left = iLeft + 'px';
      oOffset.left = iLeft;
      oEl.zpDragPrevLeft = iLeft;
    } else {
      oOffset.left = oOffset.prevLeft;
    }
  }
  // Check if it is horizontal
  if (!oEl.zpDragH) {
    var iTop = oEl.zpDragTop + oPos.pageY - oEl.zpDragPageY;
    // Check limits
    if (oEl.zpDragLimTop <= iTop && oEl.zpDragLimBot >= iTop) {
      oEl.style.bottom = '';
      oEl.style.top = iTop + 'px';
      oOffset.top = iTop;
      oEl.zpDragPrevTop = iTop;
    } else {
      oOffset.top = oOffset.prevTop;
    }
  }
  // Notify all that element was moved
  Zapatec.EventDriven.fireEvent('dragMove', oOffset);
  // Stop event
  return Zapatec.Utils.stopEvent(oEv);
};

/**
 * Finishes dragging. Gets called on document mouseup event.
 *
 * @private
 * @param {object} oEv Event object
 * @return Always false
 * @type boolean
 */
Zapatec.Drag.end = function(oEv) {
  // Check arguments
  oEv || (oEv = window.event);
  if (!Zapatec.Drag.currentId) {
    return Zapatec.Utils.stopEvent(oEv);
  }
  var oEl = document.getElementById(Zapatec.Drag.currentId);
  if (!(oEl && oEl.zpDrag)) {
    return Zapatec.Utils.stopEvent(oEv);
  }
  // Remove event listeners
  Zapatec.Utils.removeEvent(document, 'mousemove', Zapatec.Drag.move);
  Zapatec.Utils.removeEvent(document, 'mouseup', Zapatec.Drag.end);
  // Get element position
  var oOffset = {
    id: Zapatec.Drag.currentId,
    startLeft: oEl.zpDragLeft,
    startTop: oEl.zpDragTop,
    left: oEl.zpDragPrevLeft,
    top: oEl.zpDragPrevTop
  };
  // Remove properties
  Zapatec.Drag.currentId = null;
  oEl.zpDrag = null;
  oEl.zpDragPageX = null;
  oEl.zpDragPageY = null;
  oEl.zpDragLeft = null;
  oEl.zpDragTop = null;
  oEl.zpDragPrevLeft = null;
  oEl.zpDragPrevTop = null;
  oEl.zpDragV = null;
  oEl.zpDragH = null;
  oEl.zpDragLimTop = null;
  oEl.zpDragLimBot = null;
  oEl.zpDragLimLft = null;
  oEl.zpDragLimRgh = null;
  // Notify all that element was dropped
  Zapatec.EventDriven.fireEvent('dragEnd', oOffset);
  // Stop event
  return Zapatec.Utils.stopEvent(oEv);
};
