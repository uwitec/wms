/**
 * @fileoverview Common functions used in all Zapatec widgets.
 *
 * <pre>
 * Copyright (c) 2004-2007 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/* $Id: utils.js 6903 2007-04-06 22:04:33Z alex $ */

if (typeof Zapatec == 'undefined') {
  /**
   * @ignore Namespace definition.
   */
  Zapatec = function() {};
}

/// define the Utils namespace
Zapatec.Utils = {};

/// Deprecated. Use Zapatec.Utils.getElementOffset instead.
/// Retrieves the absolute position (relative to <body>) of a given element.
///
/// @param el [HTMLElement] reference to the element.
/// @return [object] { x, y } containing the position.
Zapatec.Utils.getAbsolutePos = function(el, scrollOff) {
	var SL = 0, ST = 0;
	if (!scrollOff) {
		var is_div = /^div$/i.test(el.tagName);
		if (is_div && el.scrollLeft)
			SL = el.scrollLeft;
		if (is_div && el.scrollTop)
			ST = el.scrollTop;
	}
	var r = { x: el.offsetLeft - SL, y: el.offsetTop - ST };
	if (el.offsetParent) {
		var tmp = this.getAbsolutePos(el.offsetParent);
		r.x += tmp.x;
		r.y += tmp.y;
	}
	return r;
};

/**
 * Returns absolute position of an element on the page and its width and height.
 *
 * @private
 * @param {object} oEl Element object
 * @return Offset: left or x - left offset; top or y - top offset,
 * width - element width, height - element height.
 * @object
 */
Zapatec.Utils.getElementOffset = function(oEl) {
  var iLeft = iTop = iWidth = iHeight = 0;
  if (oEl.getBoundingClientRect) {
    // IE
    var oRect = oEl.getBoundingClientRect();
    iLeft = oRect.left;
    iTop = oRect.top;
    iWidth = oRect.right - iLeft;
    iHeight = oRect.bottom - iTop;
    // getBoundingClientRect returns coordinates relative to the window
    // Why "- 2" is explained here:
    // http://msdn.microsoft.com/library/default.asp?url=/workshop/author/dhtml/reference/methods/getboundingclientrect.asp
    iLeft += Zapatec.Utils.getPageScrollX() - 2;
    iTop += Zapatec.Utils.getPageScrollY() - 2;
  } else {
    // Others
    iWidth = oEl.offsetWidth;
    iHeight = oEl.offsetHeight;
    var sPos = Zapatec.Utils.getStyleProperty(oEl, 'position');
    if (sPos == 'fixed') {
      iLeft = oEl.offsetLeft + Zapatec.Utils.getPageScrollX();
      iTop = oEl.offsetTop + Zapatec.Utils.getPageScrollY();
    } else if (sPos == 'absolute') {
      while (oEl) {
        var sTag = oEl.tagName;
        if (sTag) {
          sTag = sTag.toLowerCase();
          // Safari adds margin of the body
          if (sTag != 'body' && sTag != 'html') {
            iLeft += parseInt(oEl.offsetLeft, 10) || 0;
            iTop += parseInt(oEl.offsetTop, 10) || 0;
          }
        }
        oEl = oEl.offsetParent;
        var sTag = oEl ? oEl.tagName : null;
        if (sTag) {
          sTag = sTag.toLowerCase();
          if (sTag != 'body' && sTag != 'html') {
            iLeft -= oEl.scrollLeft;
            iTop -= oEl.scrollTop;
          }
        }
      }
    } else {
      var oP = oEl;
      while (oP) {
        // -moz-box-sizing must be "border-box" to prevent subtraction of body
        // border in Mozilla
        iLeft += parseInt(oP.offsetLeft, 10) || 0;
        iTop += parseInt(oP.offsetTop, 10) || 0;
        oP = oP.offsetParent;
      }
      oP = oEl;
      while (oP.parentNode) {
        oP = oP.parentNode;
        var sTag = oP.tagName;
        if (sTag) {
          sTag = sTag.toLowerCase();
          if (sTag != 'body' && sTag != 'html' && sTag != 'tr') {
            iLeft -= oP.scrollLeft;
            iTop -= oP.scrollTop;
          }
        }
      }
    }
  }
  return {
    left: iLeft,
    top: iTop,
    x: iLeft,
    y: iTop,
    width: iWidth,
    height: iHeight
  };
};

/**
 * Returns offset of content of a scrollable element relative to the document
 * body. Offset is calulated as offset of an element minus scrollLeft/scrollTop
 * value.
 *
 * @private
 * @param {object} oEl Element object
 * @return Offset: left or x - left offset; top or y - top offset
 * @object
 */
Zapatec.Utils.getElementOffsetScrollable = function(oEl) {
  var oPos = Zapatec.Utils.getElementOffset(oEl);
  if (oEl.scrollLeft) {
    oPos.left -= oEl.scrollLeft;
    oPos.x = oPos.left;
  }
  if (oEl.scrollTop) {
    oPos.top -= oEl.scrollTop;
    oPos.y = oPos.top;
  }
  return oPos;
};

/// Modify the position of a box to fit in browser's view.  This function will
/// modify the passed object itself, so it doesn't need to return a value.
///
/// @param [object] box { x, y, width, height } specifying the area.
Zapatec.Utils.fixBoxPosition = function(box, leave) {
	var screenX = Zapatec.Utils.getPageScrollX();
	var screenY = Zapatec.Utils.getPageScrollY();
	var sizes = Zapatec.Utils.getWindowSize();
	leave = parseInt(leave, 10) || 0;
	if (box.x < screenX) {
		box.x = screenX + leave;
	}
	if (box.y < screenY) {
		box.y = screenY + leave;
	}
	if (box.x + box.width > screenX + sizes.width) {
		box.x = screenX + sizes.width - box.width - leave;
	}
	if (box.y + box.height > screenY + sizes.height) {
		box.y = screenY + sizes.height - box.height - leave;
	}
};

/// Determines if an event is related to a certain element.  This is a poor
/// substitute for some events that are missing from DOM since forever (like
/// onenter, onleave, which MSIE provides).  Basically onmouseover and
/// onmouseout are fired even if the mouse was already in the element but moved
/// from text to a blank area, so in order not to close a popup element when
/// onmouseout occurs in this situation, one would need to first check if the
/// event is not related to that popup element:
///
/// \code
///      function handler_onMouseOut(event) {
///         if (!Zapatec.Utils.isRelated(this, event)) {
///            /// can safely hide it now
///            this.style.display = "none";
///         }
///      }
/// \endcode
///
/// @param el [HTMLElement] reference to the element to check the event against
/// @param evt [Event] reference to the Event object
/// @return [boolean] true if the event is related to the element
Zapatec.Utils.isRelated = function (el, evt) {
	evt || (evt = window.event);
	var related = evt.relatedTarget;
	if (!related) {
		var type = evt.type;
		if (type == "mouseover") {
			related = evt.fromElement;
		} else if (type == "mouseout") {
			related = evt.toElement;
		}
	}
	try {
		while (related) {
			if (related == el) {
				return true;
			}
			related = related.parentNode;
		}
	} catch(e) {};
	return false;
};

/// Remove a certain [CSS] class from the given element.
/// @param el [HTMLElement] reference to the element.
/// @param className [string] the class to remove.
Zapatec.Utils.removeClass = function(el, className) {
	if (!(el && el.className)) {
		return;
	}
	var cls = el.className.split(" ");
	var ar = [];
	for (var i = cls.length; i > 0;) {
		if (cls[--i] != className) {
			ar[ar.length] = cls[i];
		}
	}
	el.className = ar.join(" ");
};

/// Appends a certain [CSS] class to the given element.
/// @param el [HTMLElement] reference to the element.
/// @param className [string] the class to append.
Zapatec.Utils.addClass = function(el, className) {
	Zapatec.Utils.removeClass(el, className);
	el.className += " " + className;
};

/// Retrieves the current target element for some event (useful when bubbling).
/// This function is not actually very useful, but it's legacy from the old calendar code.
/// @param ev [Event] the event object.
/// @return [HTMLElement] window.event.srcElement for MSIE, ev.currentTarget for other browsers.
Zapatec.Utils.getElement = function(ev) {
	if (Zapatec.is_ie) {
		return window.event.srcElement;
	} else {
		return ev.currentTarget;
	}
};

/// Retrieves the target element for some event (useful when bubbling).
/// This function is not actually very useful, but it's legacy from the old calendar code.
/// @param ev [Event] the event object.
/// @return [HTMLElement] window.event.srcElement for MSIE, ev.target for other browsers.
Zapatec.Utils.getTargetElement = function(ev) {
	if (Zapatec.is_ie) {
		return window.event.srcElement;
	} else {
		return ev.target;
	}
};

/**
 * Returns mouse position during the event.
 *
 * @param {object} oEv Optional. Event object
 * @return Mouse position during the event:
 * <pre>
 * {
 *   pageX: [number] x coordinate relative to the document,
 *   pageY: [number] y coordinate relative to the document,
 *   clientX: [number] x coordinate relative to the window,
 *   clientY: [number] y coordinate relative to the window
 * }
 * </pre>
 * @type object
 */
Zapatec.Utils.getMousePos = function(oEv) {
  oEv || (oEv = window.event);
  var oPos = {
    pageX: 0,
    pageY: 0,
    clientX: 0,
    clientY: 0
  };
  if (oEv) {
    var bIsPageX = (typeof oEv.pageX != 'undefined');
    var bIsClientX = (typeof oEv.clientX != 'undefined');
    if (bIsPageX || bIsClientX) {
      if (bIsPageX) {
        oPos.pageX = oEv.pageX;
        oPos.pageY = oEv.pageY;
      } else {
        oPos.pageX = oEv.clientX + Zapatec.Utils.getPageScrollX();
        oPos.pageY = oEv.clientY + Zapatec.Utils.getPageScrollY();
      }
      if (bIsClientX) {
        oPos.clientX = oEv.clientX;
        oPos.clientY = oEv.clientY;
      } else {
        oPos.clientX = oEv.pageX - Zapatec.Utils.getPageScrollX();
        oPos.clientY = oEv.pageY - Zapatec.Utils.getPageScrollY();
      }
    }
  }
  return oPos;
};

/// Stops bubbling and propagation of some event.
/// @param ev [Event] the event object
/// @return false
Zapatec.Utils.stopEvent = function(ev) {
	ev || (ev = window.event);
	if (ev) {
		if (Zapatec.is_ie) {
			ev.cancelBubble = true;
			ev.returnValue = false;
		} else {
			ev.preventDefault();
			ev.stopPropagation();
		}
	}
	return false;
};

Zapatec.Utils.removeOnUnload = [];

/**
 * Adds event handler to certain element or widget using DOM addEventListener or
 * MSIE attachEvent method. Doing this means that you can add multiple handlers
 * for the same object and event, and they will be called in order.
 *
 * @param {object} oElement Element object
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 * @param {boolean} bUseCapture Optional. Default: false. For details see
 * http://www.w3.org/TR/2000/REC-DOM-Level-2-Events-20001113/events.html#Events-EventTarget-addEventListener
 */
Zapatec.Utils.addEvent = function(oElement, sEvent, fListener, bUseCapture) {
  if (oElement.addEventListener) {
    // W3C
    if (!bUseCapture) {
      bUseCapture = false;
    }
    oElement.addEventListener(sEvent, fListener, bUseCapture);
  } else if (oElement.attachEvent) {
    // IE
    oElement.detachEvent('on' + sEvent, fListener);
    oElement.attachEvent('on' + sEvent, fListener);
    if (bUseCapture) {
      oElement.setCapture(false);
    }
  }
  Zapatec.Utils.removeOnUnload.push({
    'element': oElement,
    'event': sEvent,
    'listener': fListener,
    'capture': bUseCapture
  });
};

/**
 * Removes event handler added with {@link Zapatec.Utils#addEvent}.
 *
 * @param {object} oElement Element object
 * @param {string} sEvent Event name
 * @param {function} fListener Event listener
 * @param {boolean} bUseCapture Optional. Default: false. For details see
 * http://www.w3.org/TR/2000/REC-DOM-Level-2-Events-20001113/events.html#Events-EventTarget-removeEventListener
 */
Zapatec.Utils.removeEvent = function(oElement, sEvent, fListener, bUseCapture) {
  if (oElement.removeEventListener) {
    // W3C
    oElement.removeEventListener(sEvent, fListener, bUseCapture);
  } else if (oElement.detachEvent) {
    // IE
    oElement.detachEvent('on' + sEvent, fListener);
  }
  for (var iLis = Zapatec.Utils.removeOnUnload.length - 1; iLis >= 0; iLis--) {
    var oParams = Zapatec.Utils.removeOnUnload[iLis];
    if (!oParams) {
      continue;
    }
    if (oElement == oParams['element'] && sEvent == oParams['event'] &&
     fListener == oParams['listener'] && bUseCapture == oParams['capture']) {
      Zapatec.Utils.removeOnUnload[iLis] = null;
      Zapatec.Utils.removeEvent(
       oParams['element'],
       oParams['event'],
       oParams['listener'],
       oParams['capture']
      );
    }
  }
};

/// Create an element of a certain type using document.createElement().  A
/// function was needed in order to add some common attributes to all created
/// elements, but also in order to be able to use it in XHTML too (Gecko and
/// other W3C-compliant browsers).
///
/// This function will create an element of the given type and set certain
/// properties to it: unselectable for IE, and the CSS "-moz-user-select" for
/// Gecko, in order to make the element unselectable in these browsers.
/// Optionally, if the second argument is passed, it will appendChild() the
/// newly created element to its parent.
///
/// @param type [string] the tag name of the new element.
/// @param parent [HTMLElement, optional] a parent for the new element.
/// @param selectable [boolean] the flag to indicate wether element is selectable(rather usefull).
/// @return [HTMLElement] reference to the new element.
Zapatec.Utils.createElement = function(type, parent, selectable) {
	var el = null;
	if (window.self.document.createElementNS)
		// use the XHTML namespace; IE won't normally get here unless
		// _they_ "fix" the DOM2 implementation.
		el = window.self.document.createElementNS("http://www.w3.org/1999/xhtml", type);
	else
		el = document.createElement(type);
	if (typeof parent != "undefined" && parent != null)
		parent.appendChild(el);
	if (!selectable) {
		if (Zapatec.is_ie)
			el.setAttribute("unselectable", true);
		if (Zapatec.is_gecko)
			el.style.setProperty("-moz-user-select", "none", "");
	}
	return el;
};

// Cookie management

/// Sets a cooke given certain specifications.  It overrides any existing
/// cookie with the same name.
///
/// @param name [string] the cookie name.
/// @param value [string] the cookie value.
/// @param domain [string, optional] the cookie domain.
/// @param path [string, optional] the cookie path.
/// @param exp_days [number, optional] number of days of cookie validity.
Zapatec.Utils.writeCookie = function(name, value, domain, path, exp_days) {
	value = escape(value);
	var ck = name + "=" + value, exp;
	if (domain)
		ck += ";domain=" + domain;
	if (path)
		ck += ";path=" + path;
	if (exp_days) {
		exp = new Date();
		exp.setTime(exp_days * 86400000 + exp.getTime());
		ck += ";expires=" + exp.toGMTString();
	}
	document.cookie = ck;
};

/**
 * Retrieves the value of a cookie.
 *
 * @param name [string] the cookie name
 * @return [string or null] a string with the cookie value, or null if it can't be found.
 */

/* ? inside regular expression is not supported in IE 5.0
Zapatec.Utils.getCookie = function(name) {
	var re = new RegExp("(^|;\\s*)" + name + "\\s*=(.*?)(;|$)");
	if (re.test(document.cookie)) {
		var value = RegExp.$2;
		value = unescape(value);
		return (value);
	}
	return null;
};
*/

Zapatec.Utils.getCookie = function(name) {
	var pattern = name + "=";
	var tokenPos = 0;
	while (tokenPos < document.cookie.length) {
		var valuePos = tokenPos + pattern.length;
		if (document.cookie.substring(tokenPos, valuePos) == pattern) {
			var endValuePos = document.cookie.indexOf(";", valuePos);
			if (endValuePos == -1) { // Last cookie
				endValuePos = document.cookie.length;
			}
			return unescape(document.cookie.substring(valuePos, endValuePos));
		}
		tokenPos=document.cookie.indexOf(" ",tokenPos)+1;
		if (tokenPos == 0) { // No more tokens
			break;
		}
	}
	return null;
};

/**
 * Given an object, create a string suitable for saving the object in a cookie.
 * This is similar to serialization.  WARNING: it does not support nested
 * objects.
 *
 * @param obj [Object] reference to the object to serialize.
 * @return [string] the serialized object.
 */
Zapatec.Utils.makePref = function(obj) {
	function stringify(val) {
		if (typeof val == "object" && !val)
			return "null";
		else if (typeof val == "number" || typeof val == "boolean")
			return val;
		else if (typeof val == "string")
			return '"' + val.replace(/\x22/, "\\22") + '"';
		else return null;
	};
	var txt = "", i;
	for (i in obj)
		txt += (txt ? ",'" : "'") + i + "':" + stringify(obj[i]);
	return txt;
};

/**
 * The reverse of Zapatec.Utils.makePref(), this function unserializes the
 * given string and creates an object from it.
 *
 * @param txt [string] the serialized value.
 * @return [Object] a new object if it was created successfully or null otherwise.
 */
Zapatec.Utils.loadPref = function(txt) {
	var obj = null;
	try {
		eval("obj={" + txt + "}");
	} catch(e) {}
	return obj;
};

/**
 * Merges the values of the source object into the destination object.
 *
 * @param dest [Object] the destination object.
 * @param src [Object] the source object.
 */
Zapatec.Utils.mergeObjects = function(dest, src) {
	for (var i in src)
		dest[i] = src[i];
};

// based on the WCH idea
// http://www.aplus.co.yu/WCH/code3/WCH.js

/// \defgroup WCH functions
//@{

Zapatec.Utils.__wch_id = 0;	/**< [number, static] used to create ID-s for the WCH objects */

/**
 * Create an WCH object.  This function does nothing if the browser is not
 * IE5.5 or IE6.0.  A WCH object is one of the most bizarre tricks to avoid a
 * notorious IE bug: IE normally shows "windowed controls" on top of any HTML
 * elements, regardless of any z-index that might be specified in CSS.  This
 * technique is described at: http://www.aplus.co.yu/WCH/
 *
 * A "WCH object" is actually an HTMLIFrame element having a certain "CSS
 * filter" (proprietary MSIE extension) that forces opacity zero.  This object,
 * displayed on top of a windowed control such as a select box, will completely
 * hide the select box, allowing us to place other HTMLElement objects above.
 *
 * WCH stands for "Windowed Controls Hider".
 *
 * @param element [HTMLElement, optional] -- Create the WCH IFRAME inside this.
 *
 *
 * @return [HTMLIFrame or null] a new WCH object if the browser is "supported", null otherwise.
 */
Zapatec.Utils.createWCH = function(element) {
	var f = null;
	element = element || document.body;
	if (Zapatec.is_ie && !Zapatec.is_ie5) {
		var filter = 'filter:progid:DXImageTransform.Microsoft.alpha(style=0,opacity=0);';
		var id = "WCH" + (++Zapatec.Utils.__wch_id);
		element.insertAdjacentHTML
			('beforeEnd', '<iframe id="' + id + '" scrolling="no" frameborder="0" ' +
			 'style="z-index:0;position:absolute;visibility:hidden;' + filter +
			 'border:0;top:0;left:0;width:0;height:0" ' +
       // javascript:false instead of about:blank prevents non-secure alert
       // on ssl server in IE 6
			 'src="javascript:false"></iframe>');
		f = document.getElementById(id);
	}
	return f;
};

/**
 * Configure a given WCH object to be displayed on top of the given element.
 * Optionally, a second element can be passed, and in this case it will setup
 * the WCH object to cover both elements.
 *
 * @param f [HTMLIFrame] the WCH object
 * @param el [HTMLElement] the element to cover.
 * @param el2 [HTMLElement, optional] another element to cover.
 */
Zapatec.Utils.setupWCH_el = function(f, el, el2) {
	if (f) {
		var pos = Zapatec.Utils.getAbsolutePos(el),
			X1 = pos.x,
			Y1 = pos.y,
			X2 = X1 + el.offsetWidth,
			Y2 = Y1 + el.offsetHeight;
		if (el2) {
			var p2 = Zapatec.Utils.getAbsolutePos(el2),
				XX1 = p2.x,
				YY1 = p2.y,
				XX2 = XX1 + el2.offsetWidth,
				YY2 = YY1 + el2.offsetHeight;
			if (X1 > XX1)
				X1 = XX1;
			if (Y1 > YY1)
				Y1 = YY1;
			if (X2 < XX2)
				X2 = XX2;
			if (Y2 < YY2)
				Y2 = YY2;
		}
		Zapatec.Utils.setupWCH(f, X1, Y1, X2-X1, Y2-Y1);
	}
};

/**
 * Configure a WCH object to cover a certain part of the screen.
 *
 * @param f [HTMLIFrame] the WCH object.
 * @param x [number] the X coordinate.
 * @param y [number] the Y coordinate.
 * @param w [number] the width of the area.
 * @param h [number] the height of the area.
 */
Zapatec.Utils.setupWCH = function(f, x, y, w, h) {
	if (f) {
		var s = f.style;
		(typeof x != "undefined") && (s.left = x + "px");
		(typeof y != "undefined") && (s.top = y + "px");
		(typeof w != "undefined") && (s.width = w + "px");
		(typeof h != "undefined") && (s.height = h + "px");
		s.visibility = "inherit";
	}
};

/**
 * Hide a WCH object.
 *
 * @param f [HTMLIFrame] object to hide.
 */
Zapatec.Utils.hideWCH = function(f) {
	if (f)
		f.style.visibility = "hidden";
};

//@}

/**
 * Returns current document vertical scroll position.
 *
 * @return Current document vertical scroll position
 * @type number
 */
Zapatec.Utils.getPageScrollY = function() {
  if (window.pageYOffset) {
    return window.pageYOffset;
  } else if (document.body && document.body.scrollTop) {
    return document.body.scrollTop;
  } else if (document.documentElement && document.documentElement.scrollTop) {
    return document.documentElement.scrollTop;
  }
  return 0;
};

/**
 * Returns current document horizontal scroll position.
 *
 * @return Current document horizontal scroll position
 * @type number
 */
Zapatec.Utils.getPageScrollX = function() {
  if (window.pageXOffset) {
    return window.pageXOffset;
  } else if (document.body && document.body.scrollLeft) {
    return document.body.scrollLeft;
  } else if (document.documentElement && document.documentElement.scrollLeft) {
    return document.documentElement.scrollLeft;
  }
  return 0;
};

/**
 * Zapatec ScrollWithWindow Widget. Used to produce effect similar to
 * "position: fixed".
 */
Zapatec.ScrollWithWindow = {};

/**
 * Holds references to fixed elements.
 * @private
 */
Zapatec.ScrollWithWindow.list = [];

/**
 * Stickiness parameter. Set to a number between 0 and 1, lower means longer
 * scrolling.
 */
Zapatec.ScrollWithWindow.stickiness = 0.25;

/**
 * Registers a given object to have its style.top set equal to the window
 * scroll position as the browser scrolls.
 *
 * @param {object} oElement A reference to the element to scroll
 */
Zapatec.ScrollWithWindow.register = function(oElement) {
  var iTop = oElement.offsetTop || 0;
  var iLeft = oElement.offsetLeft || 0;
  Zapatec.ScrollWithWindow.list.push({
    node: oElement,
    origTop: iTop,
    origLeft: iLeft
  });
  // Turn on scrolling
  if (!Zapatec.ScrollWithWindow.interval) {
    Zapatec.ScrollWithWindow.on();
  }
};

/**
 * Unregisters a given object.
 *
 * @param {object} oElement A reference to the element to scroll
 */
Zapatec.ScrollWithWindow.unregister = function(oElement) {
  for (var iItem = 0; iItem < Zapatec.ScrollWithWindow.list.length; iItem++) {
    var oItem = Zapatec.ScrollWithWindow.list[iItem];
    if (oElement == oItem.node) {
      Zapatec.ScrollWithWindow.list.splice(iItem, 1);
      // Turn off scrolling when list becomes empty
      if (!Zapatec.ScrollWithWindow.list.length) {
        Zapatec.ScrollWithWindow.off();
      }
      return;
    }
  }
};

/**
 * Called each time the window is scrolled to set objects' top offset.
 *
 * @private
 * @param {number} iTop New top offset
 */
Zapatec.ScrollWithWindow.moveTop = function(iTop) {
  Zapatec.ScrollWithWindow.top += (iTop - Zapatec.ScrollWithWindow.top) *
   Zapatec.ScrollWithWindow.stickiness;
  if (Math.abs(Zapatec.ScrollWithWindow.top - iTop) <= 1) {
    Zapatec.ScrollWithWindow.top = iTop;
  }
  for (var iItem = 0; iItem < Zapatec.ScrollWithWindow.list.length; iItem++) {
    var oItem = Zapatec.ScrollWithWindow.list[iItem];
    var oElement = oItem.node;
    oElement.style.position = 'absolute';
    if (!oItem.origTop && oItem.origTop !== 0) {
      oItem.origTop = parseInt(oElement.style.top) || 0;
    }
    oElement.style.top = oItem.origTop +
     parseInt(Zapatec.ScrollWithWindow.top) + 'px';
  }
};

/**
 * Called each time the window is scrolled to set objects' left offset.
 *
 * @private
 * @param {number} iLeft New left offset
 */
Zapatec.ScrollWithWindow.moveLeft = function(iLeft) {
  Zapatec.ScrollWithWindow.left += (iLeft - Zapatec.ScrollWithWindow.left) *
   Zapatec.ScrollWithWindow.stickiness;
  if (Math.abs(Zapatec.ScrollWithWindow.left - iLeft) <= 1) {
    Zapatec.ScrollWithWindow.left = iLeft;
  }
  for (var iItem = 0; iItem < Zapatec.ScrollWithWindow.list.length; iItem++) {
    var oItem = Zapatec.ScrollWithWindow.list[iItem];
    var oElement = oItem.node;
    oElement.style.position = 'absolute';
    if (!oItem.origLeft && oItem.origLeft !== 0) {
      oItem.origLeft = parseInt(oElement.style.left) || 0;
    }
    oElement.style.left = oItem.origLeft +
     parseInt(Zapatec.ScrollWithWindow.left) + 'px';
  }
};

/**
 * Gets called every 50 ms when scrolling is on.
 * @private
 */
Zapatec.ScrollWithWindow.cycle = function() {
  var iTop = Zapatec.Utils.getPageScrollY();
  var iLeft = Zapatec.Utils.getPageScrollX();
  if (iTop != Zapatec.ScrollWithWindow.top) {
    Zapatec.ScrollWithWindow.moveTop(iTop);
  }
  if (iLeft != Zapatec.ScrollWithWindow.left) {
    Zapatec.ScrollWithWindow.moveLeft(iLeft);
  }
};

/**
 * Turns scrolling on.
 * @private
 */
Zapatec.ScrollWithWindow.on = function() {
  if (Zapatec.ScrollWithWindow.interval) {
    // Already on
    return;
  }
  Zapatec.ScrollWithWindow.top = Zapatec.Utils.getPageScrollY();
  Zapatec.ScrollWithWindow.left = Zapatec.Utils.getPageScrollX();
  Zapatec.ScrollWithWindow.interval =
   setInterval(Zapatec.ScrollWithWindow.cycle, 50);
};

/**
 * Turns scrolling off.
 * @private
 */
Zapatec.ScrollWithWindow.off = function() {
  if (!Zapatec.ScrollWithWindow.interval) {
    // Already off
    return;
  }
  clearInterval(Zapatec.ScrollWithWindow.interval);
  Zapatec.ScrollWithWindow.interval = null;
};

//Fixates the element on the screen.
Zapatec.FixateOnScreen = {};

/**
 * Gets IE's exspression string.
 * @param coord {number or string} coordinate or string to include.
 * @param direction {string} which coordinate we form - x or y.
 * @return {string} formed expression string.
 */
Zapatec.FixateOnScreen.getExpression = function(coord, direction) {
	return "Zapatec.Utils.getPageScroll" + direction.toUpperCase() + "() + " + coord;
};


/**
 * Gets the actual coordinates, as if the
 * element wasn't fixated.
 * @param element {HTML element} element to seek coordinate of.
 * @return {number} the actual coordinate.
 */
Zapatec.FixateOnScreen.parseCoordinates = function(element) {
	//maybe it is not registered
	if (!this.isRegistered(element)) {
		return false;
	}
	//getting coordinates on screen
	var x = 0;
	var y = 0;
	var style = element.style;
	if (Zapatec.is_ie && !Zapatec.is_ie7) {
		//parsing expression's value
		x = style.getExpression("left").split(" ");
		x = parseInt(x[x.length - 1], 10);
		y = style.getExpression("top").split(" ");  
		y = parseInt(y[y.length - 1], 10);
	} else {
		//just parsing coordinates
		x = parseInt(style.left, 10);
		y = parseInt(style.top, 10);
	}
	//adding page scrolling
	x += Zapatec.Utils.getPageScrollX();
	y += Zapatec.Utils.getPageScrollY();
	return {x : x, y : y};
};


/**
 * Corects coordinates of the element to become fixed.
 * @param x {number} x coordinate.
 * @param y {number} y coordinate.
 * @return {object} corrected object.
 */
Zapatec.FixateOnScreen.correctCoordinates = function(x, y) {
	//calculating screen coordinate
	position = {x : x, y : y};
	if (position.x || position.x === 0) {
		position.x -= Zapatec.Utils.getPageScrollX();
		if (Zapatec.is_ie && !Zapatec.is_ie7) {
			//for IE we use expression as CSS property value
			position.x = this.getExpression(position.x, "X");;
		} else {
			position.x += "px";
		}
	}
	if (position.y || position.y === 0) {
		position.y -= Zapatec.Utils.getPageScrollY();
		if (Zapatec.is_ie && !Zapatec.is_ie7) {
			//for IE we use expression as CSS property value
			position.y = this.getExpression(position.y, "Y");;
		} else {
			position.y += "px";
		}
	}
	return position;
};

/**
 * Registers the element to be fixated on the screen.
 * @param element {HTML element} element to be fixated.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.FixateOnScreen.register = function(element) {
	//if not HTML element return false
	if (!Zapatec.isHtmlElement(element)) {
		return false;
	}
	//maybe it is already registered
	if (this.isRegistered(element)) {
		return true;
	}
	//getting element coordinates
	var pos = Zapatec.Utils.getElementOffset(element);
	pos = {
		x : parseInt(element.style.left, 10) || pos.x,
		y : parseInt(element.style.top, 10) || pos.y
	}
	pos = this.correctCoordinates(pos.x, pos.y);
	//fixating element
	if (!Zapatec.is_ie || Zapatec.is_ie7) {
		//using fixed positioning for all others
		var restorer = element.restorer;
		if (!restorer || !restorer.getObject || restorer.getObject() != element) {
			restorer = element.restorer = new Zapatec.SRProp(element);
		}
		restorer.saveProp("style.position");
		element.style.position = "fixed";
		element.style.left = pos.x;
		element.style.top = pos.y;
	} else {
		element.style.setExpression("left", pos.x);
		element.style.setExpression("top", pos.y);
	}
	//adding to the list
	element.zpFixed = true;
	return true;
};


/**                        
 * Unregisters the element from being fixated
 * on the screen.
 * @param element {HTML element} element to be unregistered.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.FixateOnScreen.unregister = function(element) {
	//if not HTML element return false
	if (!Zapatec.isHtmlElement(element)) {
		return false;
	}
	var pos = this.parseCoordinates(element);
	if (pos === false) {
		return true;
	}
	if (Zapatec.is_ie && !Zapatec.is_ie7) {
		element.style.removeExpression("left");
		element.style.removeExpression("top");
	}
	//setting new coordinates
	element.style.left = pos.x + "px";
	element.style.top = pos.y + "px";
	//restoring position
	if (!Zapatec.is_ie || Zapatec.is_ie7) {
		element.restorer.restoreProp("style.position", true);
	}
	element.zpFixed = false;
	return true;
};

/**
 * Checks if element is registered.
 * @param element {HTML element} element to check.
 * @return {boolean} true if it is registered, otherwise false.
 */
Zapatec.FixateOnScreen.isRegistered = function(element) {
	if (element.zpFixed) {
		return true;
	}
	return false;
};

/**
 * Destroys the given element (remove it from the DOM tree) if it's not null
 * and it's parent is not null.
 *
 * @param el [HTMLElement] the element to destroy.
 */
Zapatec.Utils.destroy = function(el) {
	if (el && el.parentNode)
		el.parentNode.removeChild(el);
};

/**
 * Opens a new window at a certain URL and having some properties.
 *
 * @param url [string] the URL to open a new window to.
 * @param windowName [string] the name of the new window (as for target attribute).
 * @param width [number] the width of the new window in pixels.
 * @param height [number] the height of the new window in pixels.
 * @param scrollbars [string] "yes" or "no" for scrollbars.
 *
 * @return [object] the new window
 */
Zapatec.Utils.newCenteredWindow = function(url, windowName, width, height, scrollbars){
	var leftPosition = 0;
	var topPosition = 0;
	if (screen.width)
		leftPosition = (screen.width -  width)/2;
	if (screen.height)
		topPosition = (screen.height -  height)/2;
	var winArgs =
		'height=' + height +
		',width=' + width +
		',top=' + topPosition +
		',left=' + leftPosition +
		',scrollbars=' + scrollbars +
		',resizable';
	var win = window.open(url,windowName,winArgs);
	return win;
};

/**
 * Finds the size of the current web page. This is the usable size
 * and does not include the browser's menu and buttons.
 *
 * @return [object] dimension with the height and width of the window
 */
Zapatec.Utils.getWindowSize = function() {
	var iWidth = 0;
	var iHeight = 0;

	if (Zapatec.is_opera) {
		iWidth = document.body.clientWidth || 0;
		iHeight = document.body.clientHeight || 0;
	} else if (Zapatec.is_khtml) {
		iWidth = window.innerWidth || 0;
		iHeight = window.innerHeight || 0;
	} else if (document.compatMode && document.compatMode == 'CSS1Compat') {
	    // Standards-compliant mode
		iWidth = document.documentElement.clientWidth || 0;
		iHeight = document.documentElement.clientHeight || 0;
	} else {
	    // Non standards-compliant mode
		iWidth = document.body.clientWidth || 0;
		iHeight = document.body.clientHeight || 0;
	}

	return {
		width: iWidth,
		height: iHeight
	};
};


/**
 * Given a reference to a select element, this function will select the option
 * having the given value and optionally will call the default handler for
 * "onchange".
 *
 * @param sel [HTMLSelectElement] reference to the SELECT element.
 * @param val [string] the value that we should select.
 * @param call_default [boolean] true if the default onchange should be called.
 */
Zapatec.Utils.selectOption = function(sel, val, call_default) {
	var a = sel.options, i, o;
	for (i = a.length; --i >= 0;) {
		o = a[i];
		o.selected = (o.value == val);
	}
	sel.value = val;
	if (call_default) {
		if (typeof sel.onchange == "function")
			sel.onchange();
		else if (typeof sel.onchange == "string")
			eval(sel.onchange);
	}
};

/**
 * A more flexible way to get the "nextSibling" of a given element. If the
 * "tag" argument is passed, then this function will return the next sibling
 * that has a certain tag. Otherwise it will simply return el.nextSibling.
 *
 * @param {object} el Element object
 * @param {string} tag Optional. Tag name of the returned element
 * @param {string} alternateTag Optional. Alternate tag name of the returned
 * element
 * @return First element after el having the specified tag; null if element
 * is not found; nextSibling if tag is not specified
 * @type object
 */
Zapatec.Utils.getNextSibling = function(el, tag, alternateTag) {
	el = el.nextSibling;
	if (!tag) {
		return el;
	}
	tag = tag.toLowerCase();
	if (alternateTag) alternateTag = alternateTag.toLowerCase();
	while (el) {
		if (el.nodeType == 1 && (el.tagName.toLowerCase() == tag ||
		 (alternateTag && el.tagName.toLowerCase() == alternateTag))) {
			return el;
		}
		el = el.nextSibling;
	}
	return el;
};

/**
 * A more flexible way to get the "previousSibling" of a given element. If the
 * "tag" argument is passed, then this function will return the previous sibling
 * that has a certain tag. Otherwise it will simply return el.previousSibling.
 *
 * @param {object} el Element object
 * @param {string} tag Optional. Tag name of the returned element
 * @param {string} alternateTag Optional. Alternate tag name of the returned
 * element
 * @return First element before el having the specified tag; previousSibling if
 * tag is not specified; null if desired element is not found
 * @type object
 */
Zapatec.Utils.getPreviousSibling = function(el, tag, alternateTag) {
	el = el.previousSibling;
	if (!tag) {
		return el;
	}
	tag = tag.toLowerCase();
	if (alternateTag) alternateTag = alternateTag.toLowerCase();
	while (el) {
		if (el.nodeType == 1 && (el.tagName.toLowerCase() == tag ||
		 (alternateTag && el.tagName.toLowerCase() == alternateTag))) {
			return el;
		}
		el = el.previousSibling;
	}
	return el;
};

/**
 * Returns first child of the given element that has a specified tag.
 *
 * @param {object} el Element object
 * @param {string} tag Optional. Tag name of the returned element
 * @param {string} alternateTag Optional. Alternate tag name of the returned
 * element
 * @return First child of the element that has a specified tag; firstChild if
 * tag is not specified; null if desired element is not found
 * @type object
 */
Zapatec.Utils.getFirstChild = function(el, tag, alternateTag) {
  if (!el) {
    return null;
  }
	el = el.firstChild;
  if (!el) {
    return null;
  }
	if (!tag) {
		return el;
	}
	tag = tag.toLowerCase();
	if (el.nodeType == 1) {
		if (el.tagName.toLowerCase() == tag) {
			return el;
		} else if (alternateTag) {
			alternateTag = alternateTag.toLowerCase();
			if (el.tagName.toLowerCase() == alternateTag) {
				return el;
			}
		}
	}
	return Zapatec.Utils.getNextSibling(el, tag, alternateTag);
};

/**
 * Returns last child of the given element that has a specified tag.
 *
 * @param {object} el Element object
 * @param {string} tag Optional. Tag name of the returned element
 * @param {string} alternateTag Optional. Alternate tag name of the returned
 * element
 * @return Last child of the element that has a specified tag; lastChild if
 * tag is not specified; null if desired element is not found
 * @type object
 */
Zapatec.Utils.getLastChild = function(el, tag, alternateTag) {
  if (!el) {
    return null;
  }
	el = el.lastChild;
  if (!el) {
    return null;
  }
	if (!tag) {
		return el;
	}
	tag = tag.toLowerCase();
	if (el.nodeType == 1) {
		if (el.tagName.toLowerCase() == tag) {
			return el;
		} else if (alternateTag) {
			alternateTag = alternateTag.toLowerCase();
			if (el.tagName.toLowerCase() == alternateTag) {
				return el;
			}
		}
	}
	return Zapatec.Utils.getPreviousSibling(el, tag, alternateTag);
};

/**
 * Function that concatenates and returns all text child nodes of the
 * specified node.
 *
 * @param objNode [Node] -- reference to the node.
 * @return [string] -- concatenated text child nodes
 */
Zapatec.Utils.getChildText = function(objNode) {
	if (objNode == null) {
		return '';
	}
	var arrText = [];
	var objChild = objNode.firstChild;
	while (objChild != null) {
		if (objChild.nodeType == 3) { // Node.TEXT_NODE
			arrText.push(objChild.data);
		}
		objChild = objChild.nextSibling;
	}
	return arrText.join(' ');
};

/**
 * Similar to the DOM's built in insertBefore.
 * Insert a node after an existing node.
 *
 * @param el [oldNode] The existing element
 * @param el [newNode] the new element to insert after the old one.
 *
 */
Zapatec.Utils.insertAfter = function(oldNode, newNode) {
	if(oldNode.nextSibling) {
		oldNode.parentNode.insertBefore(newNode, oldNode.nextSibling);
	} else {
		oldNode.parentNode.appendChild(newNode);
	}
}

Zapatec.Utils._ids = {};	/**< [number, static] maintains a list of generated IDs */

/**
 * Generates an unique ID, for a certain code (let's say "class").  If the
 * optional "id" argument is passed, then it just returns the id for that code
 * (no generation).  This function is sometimes useful when we need to create
 * elements and be able to access them later by ID.
 *
 * @param code [string] the class of ids.  User defined, can be anything.
 * @param id [string, optional] specify if the ID is already known.
 *
 * @return [string] the unique ID
 */
Zapatec.Utils.generateID = function(code, id) {
	if (typeof id == "undefined") {
		if (typeof this._ids[code] == "undefined")
			this._ids[code] = 0;
		id = ++this._ids[code];
	}
	return "zapatec-" + code + "-" + id;
};

/**
*  Add a tooltip to the specified element.
*
*  Function that adds a custom tooltip for an element.  The "target" is the
*  element to where the tooltip should be added to, and the "tooltip" is a DIV
*  that contains the tooltip text.  Optionally, the tooltip DIV can have the
*  "title" attribute set; if so, its value will be displayed highlighted as
*  the title of the tooltip.
*
*  @param target  reference to or ID of the target element
*  @param tooltip reference to or ID of the tooltip content element
*/

Zapatec.Utils.addTooltip = function(target, tooltip) {
return new Zapatec.Tooltip({target: target, tooltip: tooltip});
};

Zapatec.isLite=true;

Zapatec.Utils.checkLinks = function(){
	var anchors = document.getElementsByTagName('A');
	
	for(var ii = 0; ii < anchors.length; ii++){
		if(Zapatec.Utils.checkLink(anchors[ii])){
			return true;
		}
	}
	
	return false;
}

Zapatec.Utils.checkLink = function(lnk){
	if(!lnk){
		return false;
	}

	if(!/^https?:\/\/((dev|www)\.)?zapatec\.com/i.test(lnk.href)){
		return false;
	}

	// checking if link has some text inside
	var textContent = ""
	for(var ii = 0; ii < lnk.childNodes.length; ii++){
		if(lnk.childNodes[ii].nodeType == 3){
			textContent += lnk.childNodes[ii].nodeValue;
		}
	}

	if(textContent.length < 4){
		return false;
	}
	
	var parent = lnk;

	while(parent && parent.nodeName.toLowerCase() != "html"){
		// check if element or any of parent nodes are invisible
		if(
			Zapatec.Utils.getStyleProperty(parent, "display") == "none" ||
			Zapatec.Utils.getStyleProperty(parent, "visibility") == "hidden" ||
			Zapatec.Utils.getStyleProperty(parent, "opacity") == "0" ||
			Zapatec.Utils.getStyleProperty(parent, "-moz-opacity") == "0" ||
			/alpha\(opacity=0\)/i.test(Zapatec.Utils.getStyleProperty(parent, "filter"))
			
		){
			return false;
		}
		
		
		parent = parent.parentNode;
	}

	// check if link is outside visible screen area
	var coords = Zapatec.Utils.getElementOffset(lnk);
	if(coords.left < 0 || coords.top < 0){
		return false;
	}

	return true;
}

Zapatec.Utils.checkActivation = function() {
	if (!Zapatec.isLite)	return true;

	var arrProducts=[]

	add_product=function(script, webdir_in, name_in)
	{
	arrProducts[script]={webdir:webdir_in, name:name_in, bActive:false}
	}

	add_product('calendar.js', 'prod1',   'Calendar')
	add_product('zpmenu.js',   'menu',   'Menu')
	add_product('tree.js',     'prod3',   'Tree')
	add_product('form.js',     'forms',   'Forms')
	add_product('effects.js',  'effects', 'Effects')
	add_product('hoverer.js',  'effects', 'Effects - Hoverer')
	add_product('slideshow.js','effects', 'Effects - Slideshow')
	add_product('zpgrid.js',   'grid',    'Grid')
	add_product('slider.js',   'slider',  'Slider')
	add_product('zptabs.js',   'tabs',    'Tabs')
	add_product('zptime.js',   'time',    'Time')
	add_product('window.js',   'windows', 'Window')


	var strName, arrName, i
	var bProduct=false // Flag yes if we have a zapatec script
	var scripts = document.getElementsByTagName('script');
	for (i=0; i<scripts.length; i++)
	{
		// If wizard then do NOT do link back check, which makes wizard err out
		if (/wizard.js/i.test(scripts[i].src))
			return true

		arrName=scripts[i].src.split('/')
		if (arrName.length==0)
			strName=scripts[i]
		else
			strName=arrName[arrName.length-1]
		strName=strName.toLowerCase()
		// Get each active product
		if (typeof arrProducts[strName] != 'undefined')
			{
			bProduct=true
			arrProducts[strName].bActive=true
			}
	}

	// Is a LITE product even being used or back link is wrong?
	if(!bProduct || Zapatec.Utils.checkLinks()){
		return true;
	}

	return true;
	/*
	var strMsg='You are using the Free version of the Zapatec Software.\n'+
	'While using the Free version, a link to www.zapatec.com in this page is required.'

	for (i in arrProducts)
		// Get each active product
		if (arrProducts[i].bActive==true)
			strMsg+='\nTo purchase the Zapatec ' + arrProducts[i].name + ' visit www.zapatec.com/website/main/products/' + arrProducts[i].webdir + '/'

	alert(strMsg)

	return false;
	*/
}

/**
 * Makes a copy of an object.
 *
 * @param {object} oSource Source object to clone
 */
Zapatec.Utils.clone = function(oSource) {
  var oClone;
  if (!oSource && typeof oSource == 'object'){
    return null;
  } else if (typeof oSource == 'undefined'){
    return oClone;
  }
  if ((oSource instanceof String) || (oSource instanceof Number) ||
   (oSource instanceof Boolean)) {
    oClone = new oSource.constructor(oSource.valueOf());
  } else {
    oClone = new oSource.constructor();
  }
  for (var sProperty in oSource) {
    if (typeof oSource[sProperty] == 'object') {
      oClone[sProperty] = Zapatec.Utils.clone(oSource[sProperty], true);
    } else {
      oClone[sProperty] = oSource[sProperty];
    }
  }
  return oClone;
};

// Browser sniffing

/// detect Opera browser
Zapatec.is_opera = /opera/i.test(navigator.userAgent);

/// detect a special case of "web browser"
Zapatec.is_ie = ( /msie/i.test(navigator.userAgent) && !Zapatec.is_opera );

/// detect IE5.0/Win
Zapatec.is_ie5 = ( Zapatec.is_ie && /msie 5\.0/i.test(navigator.userAgent) );

/// detect IE7.0/Win
Zapatec.is_ie7 = ( Zapatec.is_ie && /msie 7\.0/i.test(navigator.userAgent) );

/// detect IE for Macintosh
Zapatec.is_mac_ie = ( /msie.*mac/i.test(navigator.userAgent) && !Zapatec.is_opera );

/// detect KHTML-based browsers
Zapatec.is_khtml = /Konqueror|Safari|KHTML/i.test(navigator.userAgent);

/// detect Konqueror
Zapatec.is_konqueror = /Konqueror/i.test(navigator.userAgent);

/// detect Gecko
Zapatec.is_gecko = /Gecko/i.test(navigator.userAgent);

/// detect WebKit
Zapatec.is_webkit = /WebKit/i.test(navigator.userAgent);

/// detect WebKit version
Zapatec.webkitVersion = Zapatec.is_webkit?parseInt(navigator.userAgent.replace(
        /.+WebKit\/([0-9]+)\..+/, "$1")):-1;

/**
 * Simulation of Object hasOwnProperty() method that is missing in IE 5.0.
 */
if (!Object.prototype.hasOwnProperty) {
  Object.prototype.hasOwnProperty = function(strProperty) {
    try {
      var objPrototype = this.constructor.prototype;
      while (objPrototype) {
        if (objPrototype[strProperty] == this[strProperty]) {
          return false;
        }
        objPrototype = objPrototype.prototype;
      }
    } catch (objException) {}
    return true;
  };
}

/**
 * Simulation of Function call() method that is missing in IE 5.0.
 */
if (!Function.prototype.call) {
	Function.prototype.call = function() {
		var objThis = arguments[0];
		objThis._this_func = this;
		var arrArgs = [];
		for (var iArg = 1; iArg < arguments.length; iArg++) {
			arrArgs[arrArgs.length] = 'arguments[' + iArg + ']';
		}
		var ret = eval('objThis._this_func(' + arrArgs.join(',') + ')');
		objThis._this_func = null;
		return ret;
	};
}

/**
 * Simulation of Function apply() method that is missing in IE 5.0.
 */
if (!Function.prototype.apply) {
	Function.prototype.apply = function() {
		var objThis = arguments[0];
		var objArgs = arguments[1];
		objThis._this_func = this;
		var arrArgs = [];
		if (objArgs) {
			for (var iArg = 0; iArg < objArgs.length; iArg++) {
				arrArgs[arrArgs.length] = 'objArgs[' + iArg + ']';
			}
		}
		var ret = eval('objThis._this_func(' + arrArgs.join(',') + ')');
		objThis._this_func = null;
		return ret;
	};
}

/**
 * Simulation of Array pop() method that is missing in IE 5.0.
 */
if (!Array.prototype.pop) {
	Array.prototype.pop = function() {
		var last;
		if (this.length) {
			last = this[this.length - 1];
			this.length -= 1;
		}
		return last;
	};
}

/**
 * Simulation of Array push() method that is missing in IE 5.0
 */
if (!Array.prototype.push) {
	Array.prototype.push = function() {
		for (var i = 0; i < arguments.length; i++) {
			this[this.length] = arguments[i];
		}
		return this.length;
	};
}

/**
 * Simulation of Array shift() method that is missing in IE 5.0.
 */
if (!Array.prototype.shift) {
	Array.prototype.shift = function() {
		var first;
		if (this.length) {
			first = this[0];
			for (var i = 0; i < this.length - 1; i++) {
				this[i] = this[i + 1];
			}
			this.length -= 1;
		}
		return first;
	};
}

/**
 * Simulation of Array unshift() method that is missing in IE 5.0.
 */
if (!Array.prototype.unshift) {
	Array.prototype.unshift = function() {
		if (arguments.length) {
			var i, len = arguments.length;
			for (i = this.length + len - 1; i >= len; i--) {
				this[i] = this[i - len];
			}
			for (i = 0; i < len; i++) {
				this[i] = arguments[i];
			}
		}
		return this.length;
	};
}

/**
 * Simulation of Array splice() method that is missing in IE 5.0.
 */
if (!Array.prototype.splice) {
	Array.prototype.splice = function(index, howMany) {
		var elements = [], removed = [], i;
		for (i = 2; i < arguments.length; i++) {
			elements.push(arguments[i]);
		}
		for (i = index; (i < index + howMany) && (i < this.length); i++) {
			removed.push(this[i]);
		}
		for (i = index + howMany; i < this.length; i++) {
			this[i - howMany] = this[i];
		}
		this.length -= removed.length;
		for (i = this.length + elements.length - 1; i >= index + elements.length;
		 i--) {
			this[i] = this[i - elements.length];
		}
		for (i = 0; i < elements.length; i++) {
			this[index + i] = elements[i];
		}
		return removed;
	};
}

/**
 * Crossbrowser replacement for Array indexOf() method. See:
 * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Objects:Array:indexOf
 *
 * @param {object} arr Array to search in
 * @param {any} searchElement Element to locate in the array
 * @param {any} fromIndex The index at which to begin the search
 * @return The first index at which a given element can be found in the array,
 * or -1 if it is not present
 * @type number
 */
Zapatec.Utils.arrIndexOf = function(arr, searchElement, fromIndex) {
  if (Array.prototype.indexOf) {
    return arr.indexOf(searchElement, fromIndex);
  }
  if (!fromIndex) {
    fromIndex = 0;
  }
  for (var iElement = fromIndex; iElement < arr.length; iElement++) {
    if (arr[iElement] == searchElement) {
      return iElement;
    }
  }
  return -1;
};

/**
 * Displays error message. Override this if needed.
 *
 * \param objArgs [number] error object:
 * {
 *   severity: [string, optional] error severity,
 *   description: [string] human readable error description
 * }
 */
Zapatec.Log = function(objArgs) {
  // Check arguments
  if (!objArgs) {
    return;
  }
  // Form error message
  var strMessage = objArgs.description;
  if (objArgs.severity) {
    strMessage = objArgs.severity + ':\n' + strMessage;
  }
  // Display error message
  if (objArgs.type != "warning") {
    alert(strMessage);
  }
};

/// Zapatec.Utils.Array object which contains additional for arrays method
Zapatec.Utils.Array = {};

/**
 * Inserts the element into array. 
 * It influences the order in which the elements will be iterated in the for...in cycle.
 *
 * @param arr [array] array to work with.
 * @param el [mixed] element to insert.
 * @param key [string] element to insert.
 * @param nextKey [string] element to be inserted before.
 * @return [string] new Array.
 */
Zapatec.Utils.Array.insertBefore = function (arr, el, key, nextKey) {
	var tmp = new Array();
	for(var i in arr) {
		if (i == nextKey) {
			if (key) {
				tmp[key] = el;
			} else {
				tmp.push(el);
			}
		}
		tmp[i] = arr[i];
	}
	return tmp;
}

/**
 * Extends one class with another. Gives ability to access properties and
 * methods of the superclass having the same names as in subclass.
 *
 * <pre>
 * Zapatec.Widget specific feature: If superclass has static property
 * <b>path</b>, by default subclass will get path to js file from which this
 * function is called into <b>path</b> property instead of value from
 * superclass. This behaviour can be changed with <b>leavePath</b> option below.
 *
 * To determine path correctly when widget module is included using
 * Zapatec.include function, static property <b>id</b> should be set in widget
 * class. Value of this property must be passed to Zapatec.include function as
 * script id.
 *
 * Following additional options are accepted:
 * {
 *   keepPath: [boolean] if true, static property <b>path</b> is inherited from
 *    superclass; useful, for example, when you inherit a widget and want to use
 *    the same path to themes as superclass
 * }
 *
 * Should be used as follows:
 *
 * // Define SuperClass and its methods
 * Zapatec.SuperClass = function(oArg) {
 *   ...
 * };
 *
 * Zapatec.SuperClass.prototype.init = function(oArg) {
 *   ...
 * };
 *
 * // Define SubClass and its methods
 * Zapatec.SubClass = function(oArg) {
 *   ...
 *   // Call constructor of superclass
 *   Zapatec.SubClass.SUPERconstructor.call(this, oArg);
 *   ...
 * };
 *
 * // Unique static id of the widget class to determine path correctly
 * // Neded only if class is derived from Zapatec.Widget class
 * Zapatec.SubClass.id = 'Zapatec.SubClass';
 *
 * // Inherit SuperClass
 * Zapatec.inherit(Zapatec.SubClass, Zapatec.SuperClass);
 *
 * Zapatec.SubClass.prototype.init = function(oArg) {
 *   ...
 *   // Call method of superclass
 *   Zapatec.SubClass.SUPERclass.init.call(this, oArg);
 *   ...
 * };
 *
 * Note: This function must be invoked during page load to determine path to js
 * file from which it is called correctly.
 * </pre>
 *
 * @param {object} oSubClass Inheriting class
 * @param {object} oSuperClass Class from which to inherit
 * @param {object} oArg Additional options (see above)
 */
Zapatec.inherit = function(oSubClass, oSuperClass, oArg) {
  // Duplicate prototype of superclass
  var Inheritance = function() {};
  Inheritance.prototype = oSuperClass.prototype;
  oSubClass.prototype = new Inheritance();
  // Fix constructor property to point to the self constructor because it is
  // pointing to the nested Inheritance function
  oSubClass.prototype.constructor = oSubClass;
  // Reference to constructor of superclass to be able to invoke it
  oSubClass.SUPERconstructor = oSuperClass;
  // Reference to prototype of superclass to be able to invoke its methods
  oSubClass.SUPERclass = oSuperClass.prototype;
  // Path to js file from which this function is called
  if (typeof oSuperClass.path != 'undefined') {
    if (oArg && oArg.keepPath) {
      oSubClass.path = oSuperClass.path;
    } else {
      oSubClass.path = Zapatec.getPath(oSubClass.id);
    }
  }
};

/**
 * Returns path from the script element with specified id. If id is not
 * specified or specified id is not found on the page, returns path from the
 * last loaded script element. Splits src attribute value and returns path
 * without js file name. Returns empty string if path is not found.
 *
 * <pre>
 * If specified id is not found on the page, returns last script element instead
 * of alerting. This gives ability for developer to include script using
 * Zapatec#include and specify id because in some cases it may be needed to get
 * correct path; and for user to include script directly into the page without
 * remembering internal Zapatec ids because id is not needed to determine path
 * correctly in this case.
 *
 * Developer is responsible for checking that the same script id is passed to
 * Zapatec#include and Zapatec#getPath because there is no alert if they are
 * different and result may be wrong.
 *
 * Note: This function must be invoked during page load to determine path to js
 * file from which it is called correctly.
 * </pre>
 *
 * @param {string} sId Optional. Id of the script element
 * @return Path to the script, e.g. '../src/' or '' if path is not found
 * @type string
 */
Zapatec.getPath = function(sId) {
  // Get script source
  var sSrc;
  if (typeof sId == 'string') {
    // Try to use element with specified id
    var oScript = document.getElementById(sId);
    if (oScript) {
      sSrc = oScript.getAttribute('src');
    }
  }
  if (!sSrc) {
    if (typeof Zapatec.lastLoadedModule == 'string') {
      return Zapatec.lastLoadedModule;
    }
    // Get src from last script element
    // Simply document.getElementsByTagName('script') doesn't work here because
    // FF 2 delays with adding a script to the list
    if (document.documentElement) {
      var sHtml = document.documentElement.innerHTML;
      // Value from innerHTML doesn't contain redundant spaces
      var aMatch = sHtml.match(/<script[^>]+src=[^>]+>/gi);
      if (aMatch && aMatch.length) {
        sHtml = aMatch[aMatch.length - 1];
        // Value from innerHTML is always inside double quotes
        aMatch = sHtml.match(/src="([^"]+)/i);
        if (aMatch && aMatch.length == 2) {
          sSrc = aMatch[1];
        }
      }
    }
    if (!sSrc) {
      return '';
    }
  }
  // Backslashes are allowed in IE
  sSrc = sSrc.replace(/\\/g, '/');
  // Get path
  var aTokens = sSrc.split('?');
  aTokens = aTokens[0].split('/');
  // Remove last token
  aTokens = aTokens.slice(0, -1);
  if (!aTokens.length) {
    return '';
  }
  return aTokens.join('/') + '/';
};

/**
 * Assigns event object to "window.event". Used in
 * {@link Zapatec.Utils#emulateWindowEvent} to prevent multiple identical event
 * listeners. For details see:
 * http://developer.mozilla.org/en/docs/DOM:element.addEventListener
 *
 * @private
 * @param {object} oEvent Event object
 */
Zapatec.Utils.setWindowEvent = function(oEvent) {
  if (oEvent) {
    window.event = oEvent;
  }
};

/**
 * Emulates "window.event" for certain events in Mozilla. To be able to access
 * Event object when event handler is set using element attribute, e.g.
 * &lt;a onclick="eventHandler()"&gt;.
 *
 * @param {object} aEventNames Array of event names where "window.event" is
 * needed, e.g. ['click', 'dblclick'].
 */
Zapatec.Utils.emulateWindowEvent = function(aEventNames) {
  if (document.addEventListener) {
    // Set up emulation for the events
    for (var iEvent = 0; iEvent < aEventNames.length; iEvent++) {
      document.addEventListener(aEventNames[iEvent],
       Zapatec.Utils.setWindowEvent, true);
    }
  }
};

/**
 * This is some kind of indicator that window has already loaded.
 */
Zapatec.windowLoaded = typeof(document.readyState) != 'undefined' ?
	(
		document.readyState == 'loaded' || // Konqueror
		document.readyState == 'complete' // IE/Opera
	) :
	// Mozilla
	document.getElementsByTagName != null && typeof(document.getElementsByTagName('body')[0]) != 'undefined'
;

Zapatec.Utils.addEvent(window, "load", function() {Zapatec.windowLoaded = true;});

/*
 * Use this method to display your custom message before unload event.
 * For example you could warn user that he has some unsaved changes
 * on page.
 *
 * \param msg [string] - message to display. Default value - "All your changes will be lost.":
 * \param win [object] - reference to window object. By default - current window
 */
Zapatec.Utils.warnUnload = function(msg, win){
    Zapatec.Utils.warnUnloadFlag = true;
	if(typeof(msg) != "string"){
		msg = "All your changes will be lost.";
	}

	if(typeof(win) == 'undefined'){
		win = window;
	}

	Zapatec.Utils.addEvent(win, 'beforeunload', function(ev){
		if(Zapatec.Utils.warnUnloadFlag != true){
			return true;
		}

		if(typeof(ev) == 'undefined'){
			ev = window.event;
	    }

		ev.returnValue = msg;
		
		return false;
	});
}

/*
 * Using this method you can remove displaying of your message on page unload.
 */
Zapatec.Utils.unwarnUnload = function(msg, win){
    Zapatec.Utils.warnUnloadFlag = false;
}

/*
 * \internal Variable that determines if unload handler should be used.
 */
Zapatec.Utils.warnUnloadFlag = false;

/**
 * @return Max z-index value
 * @type number
 */
Zapatec.Utils.getMaxZindex = function() {
  if (window.opera || Zapatec.is_khtml) {
    return 2147483583;
  } else if (Zapatec.is_ie){
    return 2147483647;
  } else {
    return 10737418239;
  }
};

/**
 * Corrects CSS length.
 *
 * @param {any} val Value to correct
 * @return Valid CSS length
 * @type string
 */
Zapatec.Utils.correctCssLength = function(val) {
  if (typeof val == 'undefined' || (typeof val == 'object' && !val)) {
    // Undefined or null
    return 'auto';
  }
  // Convert to string
  val += '';
  if (!val.length) {
    // Empty
    return 'auto';
  }
  if (/\d$/.test(val)) {
    // Number
    val += 'px';
  }
  return val;
};

/**
 * @ignore Holds properties of DOM objects that must be set to null on window
 * unload event to prevent memory leaks in IE.
 */
Zapatec.Utils.destroyOnUnload = [];

/**
 * Saves a property that must be set to null on window unload event. Should be
 * used for properties that can't be destroyed by garbage collector due to
 * circular references.
 *
 * @param {object} objElement DOM object
 * @param {string} strProperty Property name
 */
Zapatec.Utils.addDestroyOnUnload = function(objElement, strProperty) {
  Zapatec.Utils.destroyOnUnload.push([objElement, strProperty]);
};

/**
 * Assigns a value to a custom property of DOM object. This property will be
 * set to null on window unload event. Use this function to create properties
 * that can't be destroyed by garbage collector due to circular references.
 *
 * @param {object} objElement DOM object
 * @param {string} strProperty Property name
 * @param {any} val Property value
 */
Zapatec.Utils.createProperty = function(objElement, strProperty, val) {
  objElement[strProperty] = val;
  Zapatec.Utils.addDestroyOnUnload(objElement, strProperty);
};

// Remove circular references to prevent memory leaks in IE
Zapatec.Utils.addEvent(window, 'unload', function() {
  for (var iObj = Zapatec.Utils.destroyOnUnload.length - 1; iObj >= 0; iObj--) {
    var objDestroy = Zapatec.Utils.destroyOnUnload[iObj];
    objDestroy[0][objDestroy[1]] = null;
    objDestroy[0] = null;
  }

  for (var iLis = Zapatec.Utils.removeOnUnload.length - 1; iLis >= 0; iLis--) {
    var oParams = Zapatec.Utils.removeOnUnload[iLis];
    if (!oParams) {
      continue;
    }
    Zapatec.Utils.removeOnUnload[iLis] = null;
    Zapatec.Utils.removeEvent(
     oParams['element'],
     oParams['event'],
     oParams['listener'],
     oParams['capture']
    );
  }
});

/**
 * Escapes all special HTML characters so string can be added via innerHTML property as-is
 *
 * @param {string} str String to escape
 * @return escaped string
 * @type string
 */
Zapatec.Utils.htmlEncode = function(str) {
	// we don't need regexp for that, but.. so be it for now.
	str = str.replace(/&/ig, "&amp;");
	str = str.replace(/</ig, "&lt;");
	str = str.replace(/>/ig, "&gt;");
	str = str.replace(/\x22/ig, "&quot;");
	// \x22 means '"' -- we use hex reprezentation so that we don't disturb
	// JS compressors (well, at least mine fails.. ;)

	return str;
};


/**
 * Applies given style to given element.
 *	@param {any} elRef reference to target element. Required.
 *	@param {string} style string value of style. Required.
 */
Zapatec.Utils.applyStyle = function(elRef, style){
	if(typeof(elRef) == 'string'){
		elRef = document.getElementById(elRef);
	}

	if(elRef == null || style == null || elRef.style == null){
		return null;
	}

	if(Zapatec.is_opera){
		var pairs = style.split(";");

		for(var ii =0; ii < pairs.length; ii++){
			var kv = pairs[ii].split(":");

			// trim value
			if (!kv[1]) {
				continue;
			}
			var value = kv[1].replace(/^\s*/, '').replace(/\s*$/, '');
			var key = "";

			for(var jj = 0; jj < kv[0].length; jj++){
				if(kv[0].charAt(jj) == "-"){
					jj++;

					if(jj < kv[0].length){
						key += kv[0].charAt(jj).toUpperCase();
					}

					continue;
				}

				key += kv[0].charAt(jj);
			}

			switch(key){
				case "float":
					key = "cssFloat";
					break;
			}

			try{
				elRef.style[key] = value;
			} catch(e){}
		}
	} else {
		elRef.style.cssText = style;
	}

	return true;
}

/**
 * Retrieves computed CSS property values from an element.
 *
 * @param {object} Element object
 * @param {string} Property name of element's style, e.g. "borderLeftWidth"
 * @return Computed CSS property value
 * @type string
 */
Zapatec.Utils.getStyleProperty = function(objElement, strProperty) {
  if (document.defaultView && document.defaultView.getComputedStyle) {
    // Standard
    strProperty = strProperty.replace(/([A-Z])/g, '-$1').toLowerCase();
    var computedStyle = document.defaultView.getComputedStyle(objElement, '');
    if (computedStyle) {
      return computedStyle.getPropertyValue(strProperty);
    }
  } else if (objElement.currentStyle) {
    // IE
    return objElement.currentStyle[strProperty];
  }
  // None
  return objElement.style[strProperty];
};

/**
 * Returns precision of a number with floating point.
 *
 * @param {number} dFloat Number with floating point
 * @return Precision
 * @type number
 */
Zapatec.Utils.getPrecision = function(dFloat){
  return (dFloat + '').replace(/^\d*\.*/, '').length;
};

/**
 * Sets precision of a number with floating point. Useful to correct
 * precision before displaying the result of arithmetic operation. E.g.
 * 2.7 + 0.2 == 2.9000000000000004 and
 * Zapatec.Utils.setPrecision(2.7 + 0.2, 1) == 2.9 .
 *
 * If param iPrecision is 0, returns integer.
 *
 * @param {number} dFloat Number with floating point
 * @param {number} iPrecision Desired precision
 * @return Number with floating point with desired precision
 * @type number
 */
Zapatec.Utils.setPrecision = function(dFloat, iPrecision){
  // Convert to number
  dFloat *= 1;
  if (dFloat.toFixed) {
    return (dFloat * 1).toFixed(iPrecision) * 1;
  }
  var iPow = Math.pow(10, iPrecision);
  return parseInt(dFloat * iPow, 10) / iPow;
};

/**
 * Converts a number with floating point to string with desired precision.
 * Uses Zapatec.Utils.setPrecision to set precision. Then converts result into
 * string and adds zeros to the end if needed.
 *
 * @param {number} dFloat Number with floating point
 * @param {number} iPrecision Desired precision
 * @return String with floating point with desired precision
 * @type string
 */
Zapatec.Utils.setPrecisionString = function(dFloat, iPrecision){
  var sFloat = Zapatec.Utils.setPrecision(dFloat, iPrecision) + '';
  var iZeros = iPrecision - Zapatec.Utils.getPrecision(sFloat);
  for (var iZero = 0; iZero < iZeros; iZero++) {
    sFloat += '0';
  }
  return sFloat;
};

/**
 * Creates nested hash elements with given name. For example for arguments
 * Zapatec.Utils.createNestedHash(Zapatec, ["Module", "key", "anotherKey"])
 * Element Zapatec.Module.key.anotherKey will be created. If property does not 
 * exist - it will be created and "{}" (new Object()) will be used as default 
 * value.
 *
 * @param {Object} parent Reference to element where nested properties will be 
 *	created.
 * @param {Array} keys Array of properties to create.
 * @param {Object} value Value to assign to deepest element. Optional.
 */
Zapatec.Utils.createNestedHash = function(parent, keys, value){
	if(parent == null || keys == null){
		return null;
	}

	var tmp = parent;

	for(var ii = 0; ii < keys.length; ii++){
		if(typeof(tmp[keys[ii]]) == 'undefined'){
			tmp[keys[ii]] = {};
		}

		if(ii == keys.length - 1 && typeof(value) != 'undefined'){
			tmp[keys[ii]] = value;
		}

		tmp = tmp[keys[ii]];
    }
}

/**
 * This is a type of interface technology (mixin).
 * It takes the methods and properties of interface
 * object and add them to our class or object instance.
 * If property is present in our class and is not
 * core method of interface then it will not be added.
 * Our class or object will also be added hasInterface
 * method to work with interfaces.
 * @param classOrObject {object or function} the object or
 * class to enhance with the interface.
 * @param interfaceObj {object} object holding interface logic.
 * @return {boolean} true if success, otherwise false.
 */
Zapatec.implement = function(classOrObject, interfaceStr) {
	if (typeof interfaceStr != "string") {
		return false;
	}
	//if this is class we take its prototype
	if (typeof classOrObject == "function") {
		classOrObject = classOrObject.prototype;
	}
	//if itis still not object then this is some mistake
	if (!classOrObject || typeof classOrObject != "object") {
		return false;
	}
	var interfaceObj = window;
	var objs = interfaceStr.split(".");
	try {
		for(var i = 0; i < objs.length; ++i) {
			interfaceObj = interfaceObj[objs[i]];
		}
	} catch(e) {
		return false;
	}
	//creating interface array if it doesn't exists and new interface there
	if (typeof classOrObject.interfaces != "object") {
		classOrObject.interfaces = {};
		classOrObject.interfaces[interfaceStr] = true;
	} else if (classOrObject.interfaces[interfaceStr] !== true) {
		classOrObject.interfaces = Zapatec.Utils.clone(classOrObject.interfaces);
		classOrObject.interfaces[interfaceStr] = true;
	} else {
		//if object already has interface why executing this?
		return true;
	}
	//adding new properties and methods
	for(var iProp in interfaceObj) {
		classOrObject[iProp] = interfaceObj[iProp];
	}
	//adding hasInterface method
	classOrObject.hasInterface = function(interfaceStr) {
		if (this.interfaces[interfaceStr] === true) {
			return true;
		}
		return false;
	}
	
	return true;
};

/**
 * Processes input data and returns key code and corresponding character.
 * @param event {Object} Event
 * @return returns hash with two keys:
 * charCode - [int] code of key that was pressed(could be null for mozilla(when
 *	alphanumeric key was pressed) and opera(when unknown key (charCode == 0)
 *	was pressed))
 * chr - [char] letter, that was pressed(could be null for mozilla(when
 *	nonalphanumeric key was pressed) and opera(when unknown key (charCode == 0)
 *	was pressed))
 * @type Object
 */
Zapatec.Utils.getCharFromEvent = function(evt){
	if(!evt) {
		evt = window.event;
	}

	var response = {};

	if(Zapatec.is_gecko && !Zapatec.is_khtml && evt.type != "keydown" && evt.type != "keyup"){
		if(evt.charCode){
			response.chr = String.fromCharCode(evt.charCode);
		} else {
			response.charCode = evt.keyCode;
		}
	} else {
		response.charCode = evt.keyCode || evt.which;
		response.chr = String.fromCharCode(response.charCode);
	}

	if(Zapatec.is_opera && response.charCode == 0){
		response.charCode = null;
		response.chr = null;
	}

	if(Zapatec.is_khtml && response.charCode == 63272){
		response.charCode = 46;
		response.chr = null;
	}

	return response;
}
