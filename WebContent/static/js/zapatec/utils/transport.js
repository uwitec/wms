/**
 * @fileoverview Zapatec Transport library. Used to fetch data from the server,
 * parse and serialize XML and JSON data.
 *
 * <pre>
 * Copyright (c) 2004-2007 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

// $Id: transport.js 6606 2007-03-13 22:54:23Z alex $

if (typeof Zapatec == 'undefined') {
  /**
   * @ignore Namespace definition.
   */
  Zapatec = function() {};
}

/**
 * @constructor
 */
Zapatec.Transport = function() {};

// Determine most current versions of ActiveX objects available
if (typeof ActiveXObject != 'undefined') {

  /**
   * String variable with most current version of XMLDOM ActiveX object name
   * available.
   * @private
   */
  Zapatec.Transport.XMLDOM = null;

  /**
   * String variable with Most current version of XMLHTTP ActiveX object name
   * available.
   * @private
   */
  Zapatec.Transport.XMLHTTP = null;

  /**
   * @ignore
   * Returns first available ActiveX object name from the given list.
   *
   * @param {object} aVersions List of ActiveX object names to test
   * @return First available ActiveX object name or null
   * @type string
   */
  Zapatec.Transport.pickActiveXVersion = function(aVersions) {
    for (var iVn = 0; iVn < aVersions.length; iVn++) {
      try {
        var oDoc = new ActiveXObject(aVersions[iVn]);
        if (oDoc) {
          // If it gets to this point, the string worked
          return aVersions[iVn];
        }
      } catch (oExpn) {};
    }
    return null;
  };

  /**
   * Most current version of XMLDOM ActiveX object.
   * @private
   */
  Zapatec.Transport.XMLDOM = Zapatec.Transport.pickActiveXVersion([
    'Msxml2.DOMDocument.4.0',
    'Msxml2.DOMDocument.3.0',
    'MSXML2.DOMDocument',
    'MSXML.DOMDocument',
    'Microsoft.XMLDOM'
  ]);

  /**
   * Most current version of XMLHTTP ActiveX object.
   * @private
   */
  Zapatec.Transport.XMLHTTP = Zapatec.Transport.pickActiveXVersion([
    'Msxml2.XMLHTTP.4.0',
    'MSXML2.XMLHTTP.3.0',
    'MSXML2.XMLHTTP',
    'Microsoft.XMLHTTP'
  ]);

  // We don't need this any more
  Zapatec.Transport.pickActiveXVersion = null;

}

/**
 * Creates cross browser XMLHttpRequest object.
 *
 * @return New XMLHttpRequest object.
 * @type object
 */
Zapatec.Transport.createXmlHttpRequest = function() {
  if (typeof ActiveXObject != 'undefined') {
    try {
      return new ActiveXObject(Zapatec.Transport.XMLHTTP);
    } catch (oExpn) {};
  }
  if (typeof XMLHttpRequest != 'undefined') {
    return new XMLHttpRequest();
  }
  return null;
};

/**
 * Checks if animated GIF is already displayed in the specified div.
 *
 * <pre>
 * Arguments object format:
 * {
 *   busyContainer: [object or string] element where to put animated GIF,
 *   busyImage: [string, optional] standard image name or custom image URL
 * }
 * </pre>
 *
 * @private
 * @param {object} oArg Arguments object
 * @return True if image is displayed
 * @type boolean
 */
Zapatec.Transport.isBusy = function(oArg) {
  // Get container
  var oContr = oArg.busyContainer;
  if (typeof oContr == 'string') {
    oContr = document.getElementById(oContr);
  }
  if (!oContr) {
    return;
  }
  // Get image name
  var sImage = oArg.busyImage;
  if (typeof sImage != 'string') {
    sImage = '';
  }
  sImage = sImage.split('/').pop();
  if (!sImage.length) {
    sImage = 'zpbusy.gif';
  }
  // Check if image is displayed
  var oFC = oContr.firstChild;
  if (oFC) {
    oFC = oFC.firstChild;
    if (oFC) {
      oFC = oFC.firstChild;
      if (oFC && oFC.tagName && oFC.tagName.toLowerCase() == 'img') {
        var sSrc = oFC.getAttribute('src');
        if (typeof sSrc == 'string' && sSrc.length) {
          // Get last token
          sSrc = sSrc.split('/').pop();
          if (sSrc == sImage) {
            return true;
          }
        }
      }
    }
  }
  return false;
};

/**
 * Shows animated GIF in the specified div.
 *
 * <pre>
 * Arguments object format:
 * {
 *   busyContainer: [object or string] element where to put animated GIF,
 *   busyImage: [string, optional] standard image name or custom image URL,
 *   busyImageWidth: [number or string, optional] image width,
 *   busyImageHeight: [number or string, optional] image height
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 */
Zapatec.Transport.showBusy = function(oArg) {
  // Make sure image is not displayed yet
  if (Zapatec.Transport.isBusy(oArg)) {
    return;
  }
  // Get container
  var oContr = oArg.busyContainer;
  if (typeof oContr == 'string') {
    oContr = document.getElementById(oContr);
  }
  if (!oContr) {
    return;
  }
  // Get image name and dimensions
  var sImage = oArg.busyImage;
  var sImageWidth = oArg.busyImageWidth;
  var sImageHeight = oArg.busyImageHeight;
  if (typeof sImage != 'string' || !sImage.length) {
    sImage = 'zpbusy.gif';
  } else {
    if (typeof sImageWidth == 'number' ||
     (typeof sImageWidth == 'string' && /\d$/.test(sImageWidth))) {
      sImageWidth += 'px';
    }
    if (typeof sImageHeight == 'number' ||
     (typeof sImageHeight == 'string' && /\d$/.test(sImageHeight))) {
      sImageHeight += 'px';
    }
  }
  if (!sImageWidth) {
    sImageWidth = '65px';
  }
  if (!sImageHeight) {
    sImageHeight = '35px';
  }
  // Get path
  var sPath = '';
  // Check if path is specified
  if (sImage.indexOf('/') < 0) {
    // Use default path
    if (Zapatec.zapatecPath) {
      sPath = Zapatec.zapatecPath;
    } else {
      sPath = Zapatec.Transport.getPath('transport.js');
    }
  }
  // Form tag
  var aImg = [];
  aImg.push('<img src="');
  aImg.push(sPath);
  aImg.push(sImage);
  aImg.push('"');
  if (sImageWidth || sImageHeight) {
    aImg.push(' style="');
    if (sImageWidth) {
      aImg.push('width:');
      aImg.push(sImageWidth);
      aImg.push(';');
    }
    if (sImageHeight) {
      aImg.push('height:');
      aImg.push(sImageHeight);
    }
    aImg.push('"');
  }
  aImg.push(' />');
  // Get container dimensions
  var iContainerWidth = oContr.offsetWidth;
  var iContainerHeight = oContr.offsetHeight;
  // Display image
  var oBusyContr = Zapatec.Utils.createElement('div');
  oBusyContr.style.position = 'relative';
  oBusyContr.style.zIndex = 2147483583;
  var oBusy = Zapatec.Utils.createElement('div', oBusyContr);
  oBusy.style.position = 'absolute';
  oBusy.innerHTML = aImg.join('');
  if (oContr.firstChild) {
    oContr.insertBefore(oBusyContr, oContr.firstChild);
  } else {
    oContr.appendChild(oBusyContr);
  }
  // Move to the center of container
  var iBusyWidth = oBusy.offsetWidth;
  var iBusyHeight = oBusy.offsetHeight;
  if (iContainerWidth > iBusyWidth) {
    oBusy.style.left = oContr.scrollLeft +
     (iContainerWidth - iBusyWidth) / 2 + 'px';
  }
  if (iContainerHeight > iBusyHeight) {
    oBusy.style.top = oContr.scrollTop +
     (iContainerHeight - iBusyHeight) / 2 + 'px';
  }
};

/**
 * Removes animated GIF which was put by {@link Zapatec.Transport#showBusy}
 * from the specified div.
 *
 * <pre>
 * Arguments object format:
 * {
 *   busyContainer: [object or string] element where to put animated GIF,
 *   busyImage: [string, optional] standard image name or custom image URL
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 */
Zapatec.Transport.removeBusy = function(oArg) {
  // Get container
  var oContr = oArg.busyContainer;
  if (typeof oContr == 'string') {
    oContr = document.getElementById(oContr);
  }
  if (!oContr) {
    return;
  }
  // Make sure image is displayed
  if (Zapatec.Transport.isBusy(oArg)) {
    // Remove image
    oContr.removeChild(oContr.firstChild);
  }
};

/**
 * Fetches specified URL using new XMLHttpRequest object.
 *
 * <pre>
 * Asynchronous mode is recommended because it is safer and there is no risk of
 * having your script hang in case of network problem. Synchronous mode means
 * that the code will hang until a response comes back.
 *
 * When request is completed, one of provided callback functions is called:
 * onLoad on success or onError on error. In synchronous mode onLoad callback
 * can be omitted. Instead use returned object.
 *
 * onLoad callback function receives XMLHttpRequest object as argument and may
 * use its various properties like responseText, responseXML, etc.
 *
 * onError callback function receives following object:
 * {
 *   errorCode: server status number (404, etc.) [number],
 *   errorDescription: human readable error description [string]
 * }
 *
 * Note: Some browsers implement caching for GET requests. Caching can be
 * prevented by adding 'r=' + Math.random() parameter to URL.
 *
 * If you use POST method, content argument should be something like
 * 'var1=value1&var2=value2' with urlencoded values. If you wish to send other
 * content, set appropriate contentType. E.g. 'multipart/form-data', 'text/xml',
 * etc.
 *
 * If server response contains non-ASCII characters, server must send
 * corresponding content-type header. E.g.
 * "Content-type: text/plain; charset=utf-8" or
 * "Content-type: text/plain; charset=windows-1251".
 *
 * Arguments object format:
 * {
 *   url: [string] relative or absolute URL to fetch,
 *   method: [string, optional] method ('GET', 'POST', 'HEAD', 'PUT'),
 *   async: [boolean, optional] use asynchronous mode (default: true),
 *   contentType: [string, optional] content type when using POST,
 *   content: [string or object, optional] postable string or DOM object data
 *    when using POST,
 *   onLoad: [function, optional] function reference to call on success,
 *   onError: [function, optional] function reference to call on error,
 *   username: [string, optional] username,
 *   password: [string, optional] password,
 *   busyContainer: [object or string, optional] element or id of element where
 *    to put "Busy" animated GIF,
 *   busyImage: [string, optional] standard image name or custom image URL,
 *   busyImageWidth: [number or string, optional] image width,
 *   busyImageHeight: [number or string, optional] image height
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 * @return In synchronous mode XMLHttpRequest object or null. In asynchronous
 * mode always null.
 * @type object
 */
Zapatec.Transport.fetch = function(oArg) {
  // Check arguments
  if (oArg == null || typeof oArg != 'object') {
    return null;
  }
  if (!oArg.url) {
    return null;
  }
  if (!oArg.method) {
    oArg.method = 'GET';
  }
  if (typeof oArg.async == 'undefined') {
    oArg.async = true;
  }
  if (!oArg.contentType && oArg.method.toUpperCase() == 'POST') {
    oArg.contentType = 'application/x-www-form-urlencoded';
  }
  if (!oArg.content) {
    oArg.content = null;
  }
  if (!oArg.onLoad) {
    oArg.onLoad = null;
  }
  if (!oArg.onError) {
    oArg.onError = null;
  }
  // Request URL
  var oRequest = Zapatec.Transport.createXmlHttpRequest();
  if (oRequest == null) {
    return null;
  }
  // Show "Busy" animated GIF
  Zapatec.Transport.showBusy(oArg);
  // IE 6 calls onreadystatechange and then raises exception if local file was
  // not found. This flag is used to prevent duplicate onError calls.
  var bErrorDisplayed = false;
  // Onready handler
  var funcOnReady = function () {
    // Remove "Busy" animated GIF
    Zapatec.Transport.removeBusy(oArg);
    // Process response
    try {
      if (oRequest.status == 200 || oRequest.status == 304 ||
       (location.protocol == 'file:' && !oRequest.status)) {
        // OK or found, but determined unchanged and loaded from cache
        if (typeof oArg.onLoad == 'function') {
          oArg.onLoad(oRequest);
        }
      } else if (!bErrorDisplayed) {
        bErrorDisplayed = true;
        // 404 Not found, etc.
        Zapatec.Transport.displayError(oRequest.status,
         "Error: Can't fetch " + oArg.url + '.\n' +
         (oRequest.statusText || ''),
         oArg.onError);
      }
    } catch (oExpn) {
      // Firefox 1.5 raises exception on attempt to access any property of
      // oRequest if URL was not found
      if (!bErrorDisplayed) {
        bErrorDisplayed = true;
        if (oExpn.name && oExpn.name == 'NS_ERROR_NOT_AVAILABLE') {
          Zapatec.Transport.displayError(0,
           "Error: Can't fetch " + oArg.url + '.\nFile not found.',
           oArg.onError);
        } else {
          Zapatec.Transport.displayError(0,
           "Error: Can't fetch " + oArg.url + '.\n' +
           (oExpn.message || ''),
           oArg.onError);
        }
      }
    };
  };
  try {
    // Open request
    if (typeof oArg.username != 'undefined' &&
     typeof oArg.password != 'undefined') {
      oRequest.open(oArg.method, oArg.url, oArg.async,
       oArg.username, oArg.password);
    } else {
      oRequest.open(oArg.method, oArg.url, oArg.async);
    }
    // Prevent duplicate funcOnReady call in synchronous mode
    if (oArg.async) {
      // Set onreadystatechange handler
      oRequest.onreadystatechange = function () {
        if (oRequest.readyState == 4) {
          // Request complete
          funcOnReady();
          // Prevent memory leak
          oRequest.onreadystatechange = {};
        }
      };
    }
    // Set content type if needed
    if (oArg.contentType) {
      oRequest.setRequestHeader('Content-Type', oArg.contentType);
    }
    // Send request
    oRequest.send(oArg.content);
    // In synchronous mode the result is ready on the next line
    if (!oArg.async) {
      funcOnReady();
      return oRequest;
    }
  } catch (oExpn) {
    // Remove "Busy" animated GIF
    Zapatec.Transport.removeBusy(oArg);
    // Process error
    if (!bErrorDisplayed) {
      bErrorDisplayed = true;
      if (oExpn.name && oExpn.name == 'NS_ERROR_FILE_NOT_FOUND') {
        Zapatec.Transport.displayError(0,
         "Error: Can't fetch " + oArg.url + '.\nFile not found.',
         oArg.onError);
      } else {
        Zapatec.Transport.displayError(0,
         "Error: Can't fetch " + oArg.url + '.\n' +
         (oExpn.message || ''),
         oArg.onError);
      }
    }
  };
  return null;
};

/**
 * Parses HTML fragment into HTMLElement object.
 *
 * @param {string} sHtml HTML fragment
 * @return Div element which contains parsed HTML fragment
 * @type object
 */
Zapatec.Transport.parseHtml = function(sHtml) {
  // Convert to string
  sHtml += '';
  // Remove leading whitespace characters because Firefox and Opera don't parse
  // fragment that starts from whitespace character
  sHtml = sHtml.replace(/^\s+/g, '');
  // Create temporaty container
  var oTmpContr;
	if (document.createElementNS) {
		// use the XHTML namespace
		oTmpContr =
		 document.createElementNS('http://www.w3.org/1999/xhtml', 'div');
	} else {
		oTmpContr = document.createElement('div');
	}
  // Parse HTML fragment
  oTmpContr.innerHTML = sHtml;
  // Return container element
  return oTmpContr;
};

/**
 * Evaluates javascript in global scope.
 *
 * <p><b>
 * Note: Global variables must be declared without "var" keyword. Otherwise
 * they will be ignored by Safari.
 * </b></p>
 *
 * @param {string} sScript Script to evaluate
 */
Zapatec.Transport.evalGlobalScope = function(sScript) {
  if (typeof sScript != 'string' || !sScript.match(/\S/)) {
    return;
  }
  if (window.execScript) {
    // IE
    window.execScript(sScript, 'javascript');
  } else if (window.eval) {
    // Others
    window.eval(sScript);
/*
 This should never be reached
  } else {
    var funcScript = new Function(sScript);
    funcScript.call(window);
*/
  }
};

/**
 * Assigns passed HTML fragment to the specified element's innerHTML property
 * and evaluates in global scope javascripts found in the fragment.
 *
 * <pre>
 * Arguments object format:
 * {
 *   html: [string] HTML fragment,
 *   container: [object or string, optional] element or id of element to put
 *    HTML fragment into
 * }
 * </pre>
 *
 * <p><b>
 * Note: Scripts are executed after HTML fragment is assigned to innerHTML.
 * If external scripts are used, they are loaded asynchronously and execution
 * sequence is not preserved.
 * </b></p>
 *
 * <p><b>
 * Note: Global variables must be declared without "var" keyword. Otherwise
 * they will be ignored by Safari.
 * </b></p>
 *
 * @param {object} oArg Arguments object
 */
Zapatec.Transport.setInnerHtml = function(oArg) {
  // Check arguments
  if (!oArg || typeof oArg.html != 'string') {
    return;
  }
  var sHtml = oArg.html;
  // Get container
  var oContr = null;
  if (typeof oArg.container == 'string') {
    oContr = document.getElementById(oArg.container);
  } else if (typeof oArg.container == 'object') {
    oContr = oArg.container;
  }
  // Extract javascripts
  var aScripts = [];
  if (sHtml.match(/<\s*\/\s*script\s*>/i)) {
    // Split whole string by </script>
    var aTokens = sHtml.split(/<\s*\/\s*script\s*>/i);
    var aHtml = [];
    for (var iToken = aTokens.length - 1; iToken >= 0; iToken--) {
      var sToken = aTokens[iToken];
      if (sToken.match(/\S/)) {
        // Search <script ... > in the middle of each token
        var aMatch = sToken.match(/<\s*script([^>]*)>/i);
        if (aMatch) {
          // Separate HTML from javascript
          var aCouple = sToken.split(/<\s*script[^>]*>/i);
          // IE doesn't put empty tokens into the array
          while (aCouple.length < 2) {
            if (sToken.match(/^<\s*script[^>]*>/i)) {
              // HTML part is absent
              aCouple.unshift('');
            } else {
              // javascript part is absent
              aCouple.push('');
            }
          }
          // Save HTML fragment
          aHtml.unshift(aCouple[0]);
          // Get script attributes
          var sAttrs = aMatch[1];
          // Get script text
          var srtScript = aCouple[1];
          // Ignore script text if "src" attribute is present
          if (sAttrs.match(/\s+src\s*=/i)) {
            srtScript = '';
          } else {
            // Fix functions: function aaa() -> aaa = function()
            srtScript = srtScript.replace(/function\s+([^(]+)/g, '$1=function');
          }
          aScripts.push([sAttrs, srtScript]);
        } else if (iToken < aTokens.length - 1) {
          // On error assume this token is a part of previous token
          aTokens[iToken - 1] += '</script>' + sToken;
        } else {
          // If this is last token, assume it is HTML fragment
          aHtml.unshift(sToken);
        }
      } else {
        // Empty token
        aHtml.unshift(sToken);
      }
    }
    // Get HTML part
    sHtml = aHtml.join('');
  }
  // Set inner HTML
  if (oContr) {
    // Opera hack
    if (window.opera) {
      // Without this line Opera will not form correct DOM structure if HTML
      // fragment contains forms
      oContr.innerHTML = '<form></form>';
    }
    oContr.innerHTML = sHtml;
  }
  // Evaluate javascripts
  for (var iScript = 0; iScript < aScripts.length; iScript++) {
    if (aScripts[iScript][1].length) {
      // Evaluate in global scope
      Zapatec.Transport.evalGlobalScope(aScripts[iScript][1]);
    }
    // Load external script
    var sAttrs = aScripts[iScript][0];
    sAttrs = sAttrs.replace(/\s+/g, ' ').replace(/^\s/, '')
     .replace(/\s$/, '').replace(/ = /g, '=');
    if (sAttrs.indexOf('src=') >= 0) {
      // Get container
      var oContr = document.body;
      if (!oContr) {
        oContr = document.getElementsByTagName('head')[0];
        if (!oContr) {
          oContr = document;
        }
      }
      // Get attributes
      var aAttrs = sAttrs.split(' ');
      // Load script
      var oScript = Zapatec.Utils.createElement('script');
      for (var iAttr = 0; iAttr < aAttrs.length; iAttr++) {
        var aAttr = aAttrs[iAttr].split('=');
        if (aAttr.length > 1) {
          oScript.setAttribute(aAttr[0],
           aAttr[1].match(/^[\s|"|']*([\s|\S]*[^'|"])[\s|"|']*$/)[1]);
        } else {
          oScript.setAttribute(aAttr[0], aAttr[0]);
        }
      }
      // It's important for Safari to assign attributes before appending
      oContr.appendChild(oScript);
    }
  }
};

/**
 * Fetches and parses XML document from the specified URL.
 *
 * <pre>
 * When XML document is fetched and parsed, one of provided callback functions
 * is called: onLoad on success or onError on error. In synchronous mode onLoad
 * callback can be omitted. Instead use returned object.
 *
 * onLoad callback function receives XMLDocument object as argument and may use
 * its documentElement and other properties.
 *
 * onError callback function receives following object:
 * {
 *   errorCode: error code [number],
 *   errorDescription: human readable error description [string]
 * }
 * Error code will be 0 unless Zapatec.Transport.fetch was used to fetch URL
 * and there was a problem during fetching.
 *
 * If method argument is not defined, more efficient XMLDOM in IE and
 * document.implementation.createDocument in Mozilla will be used to fetch
 * and parse document. Otherwise Zapatec.Transport.fetch will be used to fetch
 * document and Zapatec.Transport.parseXml to parse.
 *
 * Note: Some browsers implement caching for GET requests. Caching can be
 * prevented by adding 'r=' + Math.random() parameter to URL.
 *
 * If you use POST method, content argument should be something like
 * 'var1=value1&var2=value'. If you wish to send other content, set appropriate
 * contentType. E.g. to send XML string, you should set contentType: 'text/xml'.
 *
 * If server response contains non-ASCII characters, encoding must be specified.
 * E.g. <?xml version="1.0" encoding="utf-8"?> or 
 * <?xml version="1.0" encoding="windows-1251"?>.
 *
 * If server response contains non-ASCII characters, server must send
 * corresponding content-type header. E.g.
 * "Content-type: text/xml; charset=utf-8" or
 * "Content-type: text/xml; charset=windows-1251".
 *
 * Arguments object format:
 * {
 *   url: [string] relative or absolute URL to fetch,
 *   method: [string, optional] method ('GET', 'POST', 'HEAD', 'PUT'),
 *   async: [boolean, optional] use asynchronous mode (default: true),
 *   contentType: [string, optional] content type when using POST,
 *   content: [string or object, optional] postable string or DOM object data
 *    when using POST,
 *   onLoad: [function, optional] function reference to call on success,
 *   onError: [function, optional] function reference to call on error,
 *   username: [string, optional] username,
 *   password: [string, optional] password,
 *   busyContainer: [object or string, optional] element or id of element where
 *    to put "Busy" animated GIF,
 *   busyImage: [string, optional] standard image name or custom image URL,
 *   busyImageWidth: [number or string, optional] image width,
 *   busyImageHeight: [number or string, optional] image height
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 * @return In synchronous mode XMLDocument object or null. In asynchronous mode
 * always null.
 * @type object
 */
Zapatec.Transport.fetchXmlDoc = function(oArg) {
  // Check arguments
  if (oArg == null || typeof oArg != 'object') {
    return null;
  }
  if (!oArg.url) {
    return null;
  }
  if (typeof oArg.async == 'undefined') {
    oArg.async = true;
  }
  if (!oArg.onLoad) {
    oArg.onLoad = null;
  }
  if (!oArg.onError) {
    oArg.onError = null;
  }
  // Try more efficient methods first
  if (!oArg.method && typeof oArg.username == 'undefined' &&
   typeof oArg.password == 'undefined') {
    if (document.implementation && document.implementation.createDocument) {
      // Mozilla
      var oDoc = null;

      if (!oArg.reliable) {
        oArg.reliable = false;
      }
      // Form argument for fetch
      var oFetchArg = {};
      for (var sKey in oArg) {
        oFetchArg[sKey] = oArg[sKey];
      }
      // Prevent duplicate parseXml call in synchronous mode
      if (oArg.async) {
        oFetchArg.onLoad = function(oRequest) {
          // Prevent onload being called more than once
          oFetchArg.onLoad = null;

          // Parse xml response string
          var parser = new DOMParser();
          oDoc = parser.parseFromString(oRequest.responseText, "text/xml");

          // Remove "Busy" animated GIF
          Zapatec.Transport.removeBusy(oArg);
          // Process response
          Zapatec.Transport.onXmlDocLoad(oDoc, oArg.onLoad,
           oArg.onError);
        };
      } else {
        oFetchArg.onLoad = null;
      }

      // Fetch URL
      var oRequest = Zapatec.Transport.fetch(oFetchArg);

      // In synchronous mode the result is ready on the next line
      if (!oArg.async && oRequest) {
        // Parse xml response string
        var parser = new DOMParser();
        oDoc = parser.parseFromString(oRequest.responseText, "text/xml");

        // Remove "Busy" animated GIF
        Zapatec.Transport.removeBusy(oArg);
        // Process response
        Zapatec.Transport.onXmlDocLoad(oDoc, oArg.onLoad,
         oArg.onError);

        return oDoc;
      }

      return null;
    }
    if (typeof ActiveXObject != 'undefined') {
      // IE
      // Show "Busy" animated GIF
      Zapatec.Transport.showBusy(oArg);
      // Load document
      try {
        var oDoc = new ActiveXObject(Zapatec.Transport.XMLDOM);
        oDoc.async = oArg.async;
        // Prevent duplicate onXmlDocLoad call in synchronous mode
        if (oArg.async) {
          oDoc.onreadystatechange = function () {
            if (oDoc.readyState == 4) {
              // Remove "Busy" animated GIF
              Zapatec.Transport.removeBusy(oArg);
              // Process response
              Zapatec.Transport.onXmlDocLoad(oDoc, oArg.onLoad,
               oArg.onError);
              // Prevent memory leak
              oDoc.onreadystatechange = {};
            }
          };
        }
        oDoc.load(oArg.url);
        // In synchronous mode the result is ready on the next line
        if (!oArg.async) {
          // Remove "Busy" animated GIF
          Zapatec.Transport.removeBusy(oArg);
          // Process response
          Zapatec.Transport.onXmlDocLoad(oDoc, oArg.onLoad,
           oArg.onError);
          return oDoc;
        }
        return null;
      } catch (oExpn) {
        // Remove "Busy" animated GIF
        Zapatec.Transport.removeBusy(oArg);
      };
    }
  }
  // Try XMLHttpRequest
  // Form argument for fetch
  var oFetchArg = {};
  for (var sKey in oArg) {
    oFetchArg[sKey] = oArg[sKey];
  }
  // Prevent duplicate parseXml call in synchronous mode
  if (oArg.async) {
    oFetchArg.onLoad = function(oRequest) {
      Zapatec.Transport.parseXml({
        strXml: oRequest.responseText,
        onLoad: oArg.onLoad,
        onError: oArg.onError
      });
    };
  } else {
    oFetchArg.onLoad = null;
  }
  // Fetch URL
  var oRequest = Zapatec.Transport.fetch(oFetchArg);
  // In synchronous mode the result is ready on the next line
  if (!oArg.async && oRequest) {
    return Zapatec.Transport.parseXml({
      strXml: oRequest.responseText,
      onLoad: oArg.onLoad,
      onError: oArg.onError
    });
  }
  return null;
};

/**
 * Parses XML string into XMLDocument object.
 *
 * <pre>
 * When XML string is parsed, one of provided callback functions is called:
 * onLoad on success or onError on error. In synchronous mode onLoad callback
 * can be omitted. Instead use returned object.
 *
 * onLoad callback function receives XMLDocument object as argument and may use
 * its documentElement and other properties.
 *
 * onError callback function receives following object:
 * {
 *   errorCode: error code [number],
 *   errorDescription: human readable error description [string]
 * }
 * Error code will be always 0.
 *
 * Returns XMLDocument object, so onLoad callback function is optional.
 * Returned value and its documentElement property should be checked before
 * use because they can be null or undefined.
 *
 * If XML string contains non-ASCII characters, encoding must be specified.
 * E.g. <?xml version="1.0" encoding="utf-8"?> or 
 * <?xml version="1.0" encoding="windows-1251"?>.
 *
 * Arguments object format:
 * {
 *   strXml: XML string to parse [string],
 *   onLoad: function reference to call on success [function] (optional),
 *   onError: function reference to call on error [function] (optional)
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 * @return XMLDocument object or null
 * @type object
 */
Zapatec.Transport.parseXml = function(oArg) {
  if (oArg == null || typeof oArg != 'object') {
    return null;
  }
  if (!oArg.strXml) {
    return null;
  }
  if (!oArg.onLoad) {
    oArg.onLoad = null;
  }
  if (!oArg.onError) {
    oArg.onError = null;
  }
  if (window.DOMParser) {
    // Mozilla
    try {
      var oDoc = (new DOMParser()).parseFromString(oArg.strXml,
       'text/xml');
      Zapatec.Transport.onXmlDocLoad(oDoc, oArg.onLoad,
       oArg.onError);
      return oDoc;
    } catch (oExpn) {
      Zapatec.Transport.displayError(0,
       "Error: Can't parse.\n" +
       'String does not appear to be a valid XML fragment.',
       oArg.onError);
    };
    return null;
  }
  if (typeof ActiveXObject != 'undefined') {
    // IE
    try {
      var oDoc = new ActiveXObject(Zapatec.Transport.XMLDOM);
      oDoc.loadXML(oArg.strXml);
      Zapatec.Transport.onXmlDocLoad(oDoc, oArg.onLoad,
       oArg.onError);
      return oDoc;
    } catch (oExpn) {};
  }
  return null;
};

/**
 * Checks if there were errors during XML document fetching and parsing and
 * calls onLoad or onError callback function correspondingly.
 *
 * @private
 * @param {object} oDoc XMLDocument object
 * @param {function} onLoad Callback function provided by user
 * @param {function} onError Callback function provided by user
 */
Zapatec.Transport.onXmlDocLoad = function(oDoc, onLoad, onError) {
  var sError = null;
  if (oDoc.parseError) {
    // Parsing error in IE
    sError = oDoc.parseError.reason;
    if (oDoc.parseError.srcText) {
      sError += 'Location: ' + oDoc.parseError.url +
       '\nLine number ' + oDoc.parseError.line + ', column ' +
       oDoc.parseError.linepos + ':\n' +
       oDoc.parseError.srcText + '\n';
    }
  } else if (oDoc.documentElement &&
   oDoc.documentElement.tagName == 'parsererror') {
    // If an error is caused while parsing, Mozilla doesn't throw an exception.
    // Instead, it creates an XML string containing the details of the error:
    // <parsererror xmlns="http://www.w3.org/1999/xhtml">XML Parsing Error: ...
    // Check if strings has been generated.
    sError = oDoc.documentElement.firstChild.data + '\n' +
     oDoc.documentElement.firstChild.nextSibling.firstChild.data;
  } else if (!oDoc.documentElement) {
    sError = 'String does not appear to be a valid XML fragment.';
  }
  if (sError) {
    // Parsing error
    Zapatec.Transport.displayError(0,
     "Error: Can't parse.\n" + sError,
     onError);
  } else {
    // Success
    if (typeof onLoad == 'function') {
      onLoad(oDoc);
    }
  }
};

/**
 * Serializes XMLDocument object into XML string.
 *
 * @param {object} oDoc XMLDocument object
 * @return XML string
 * @type string
 */
Zapatec.Transport.serializeXmlDoc = function(oDoc) {
  if (window.XMLSerializer) {
    // Mozilla
    return (new XMLSerializer).serializeToString(oDoc);
  }
  if (oDoc.xml) {
    // IE
    return oDoc.xml;
  }
};

/**
 * Fetches and parses JSON object from the specified URL.
 *
 * <pre>
 * When JSON object is fetched and parsed, one of provided callback functions
 * is called: onLoad on success or onError on error. In synchronous mode onLoad
 * callback can be omitted. Instead use returned object.
 *
 * onLoad callback function receives JSON object as argument.
 *
 * onError callback function receives following object:
 * {
 *   errorCode: error code [number],
 *   errorDescription: human readable error description [string]
 * }
 * Error code will be 0 unless there was a problem during fetching.
 *
 * Note: Some browsers implement caching for GET requests. Caching can be
 * prevented by adding 'r=' + Math.random() parameter to URL.
 *
 * If you use POST method, content argument should be something like
 * 'var1=value1&var2=value'. If you wish to send other content, set appropriate
 * contentType. E.g. to send XML string, you should set contentType: 'text/xml'.
 *
 * If server response contains non-ASCII characters, server must send
 * corresponding content-type header. E.g.
 * "Content-type: text/plain; charset=utf-8" or
 * "Content-type: text/plain; charset=windows-1251".
 *
 * Arguments object format:
 * {
 *   url: [string] relative or absolute URL to fetch,
 *   reliable: [boolean, optional] false (string will be parsed) or true
 *   (evaluated) (default: false),
 *   method: [string, optional] method ('GET', 'POST', 'HEAD', 'PUT'),
 *   async: [boolean, optional] use asynchronous mode (default: true),
 *   contentType: [string, optional] content type when using POST,
 *   content: [string or object, optional] postable string or DOM object data
 *    when using POST,
 *   onLoad: [function, optional] function reference to call on success,
 *   onError: [function, optional] function reference to call on error,
 *   username: [string, optional] username,
 *   password: [string, optional] password,
 *   busyContainer: [object or string, optional] element or id of element where
 *    to put "Busy" animated GIF,
 *   busyImage: [string, optional] standard image name or custom image URL,
 *   busyImageWidth: [number or string, optional] image width,
 *   busyImageHeight: [number or string, optional] image height
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 * @return In synchronous mode JSON object or null. In asynchronous mode always
 * null.
 * @type object
 */
Zapatec.Transport.fetchJsonObj = function(oArg) {
  // Check arguments
  if (oArg == null || typeof oArg != 'object') {
    return null;
  }
  if (!oArg.url) {
    return null;
  }
  if (typeof oArg.async == 'undefined') {
    oArg.async = true;
  }
  if (!oArg.reliable) {
    oArg.reliable = false;
  }
  // Form argument for fetch
  var oFetchArg = {};
  for (var sKey in oArg) {
    oFetchArg[sKey] = oArg[sKey];
  }
  // Prevent duplicate parseXml call in synchronous mode
  if (oArg.async) {
    oFetchArg.onLoad = function(oRequest) {
      Zapatec.Transport.parseJson({
        strJson: oRequest.responseText,
        reliable: oArg.reliable,
        onLoad: oArg.onLoad,
        onError: oArg.onError
      });
    };
  } else {
    oFetchArg.onLoad = null;
  }
  // Fetch URL
  var oRequest = Zapatec.Transport.fetch(oFetchArg);
  // In synchronous mode the result is ready on the next line
  if (!oArg.async && oRequest) {
    return Zapatec.Transport.parseJson({
      strJson: oRequest.responseText,
      reliable: oArg.reliable,
      onLoad: oArg.onLoad,
      onError: oArg.onError
    });
  }
  return null;
};

/**
 * Parses JSON string into object.
 *
 * <pre>
 * When JSON string is parsed, one of provided callback functions is called:
 * onLoad on success or onError on error.
 *
 * onLoad callback function receives JSON object as argument.
 *
 * onError callback function receives following object:
 * {
 *   errorCode: error code [number],
 *   errorDescription: human readable error description [string]
 * }
 * Error code will be always 0.
 *
 * Returns JSON object, so onLoad callback function is optional.
 * Returned value should be checked before use because it can be null.
 *
 * Arguments object format:
 * {
 *   strJson: JSON string to parse [string],
 *   reliable: false (string will be parsed) or true (evaluated) [boolean]
 *   (optional, false by default),
 *   onLoad: function reference to call on success [function] (optional),
 *   onError: function reference to call on error [function] (optional)
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 * @return JSON object or null
 * @type object
 */
Zapatec.Transport.parseJson = function(oArg) {
  if (oArg == null || typeof oArg != 'object') {
    return null;
  }
  if (!oArg.reliable) {
    oArg.reliable = false;
  }
  if (!oArg.onLoad) {
    oArg.onLoad = null;
  }
  if (!oArg.onError) {
    oArg.onError = null;
  }
  var oJson = null;
  try {
    if (oArg.reliable) {
      if (oArg.strJson) {
        oJson = eval('(' + oArg.strJson + ')');
      }
    } else {
      oJson = Zapatec.Transport.parseJsonStr(oArg.strJson);
    }
  } catch (oExpn) {
    var sError =
     "Error: Can't parse.\nString doesn't appear to be a valid JSON fragment: ";
    sError += oExpn.message;
    if (typeof oExpn.text != 'undefined' && oExpn.text.length) {
      sError += '\n' + oExpn.text;
    }
    sError += '\n' + oArg.strJson;
    Zapatec.Transport.displayError(0, sError, oArg.onError);
    // onLoad should not be called in this case
    return null;
  };
  if (typeof oArg.onLoad == 'function') {
    oArg.onLoad(oJson);
  }
  return oJson;
};

/**
 * Parses JSON string into object.
 *
 * <pre>
 * Was taken with changes from http://json.org/json.js.
 *
 * Throws exception if parsing error occurs.
 *
 * JSON format is described at http://json.org/js.html.
 * </pre>
 *
 * @private
 * @param {string} text JSON string to parse
 * @return JSON object
 * @type object
 */
Zapatec.Transport.parseJsonStr = function(text) {
  var p = /^\s*(([,:{}\[\]])|"(\\.|[^\x00-\x1f"\\])*"|-?\d+(\.\d*)?([eE][+-]?\d+)?|true|false|null)\s*/,
      token,
      operator;
  function error(m, t) {
      throw {
          name: 'JSONError',
          message: m,
          text: t || operator || token
      };
  }
  function next(b) {
      if (b && b != operator) {
          error("Expected '" + b + "'");
      }
      if (text) {
          var t = p.exec(text);
          if (t) {
              if (t[2]) {
                  token = null;
                  operator = t[2];
              } else {
                  operator = null;
                  try {
                      token = eval(t[1]);
                  } catch (e) {
                      error("Bad token", t[1]);
                  }
              }
              text = text.substring(t[0].length);
          } else {
              error("Unrecognized token", text);
          }
      } else {
          // undefined changed to null because it is not supported in IE 5.0
          token = operator = null;
      }
  }
  function val() {
      var k, o;
      switch (operator) {
      case '{':
          next('{');
          o = {};
          if (operator != '}') {
              for (;;) {
                  if (operator || typeof token != 'string') {
                      error("Missing key");
                  }
                  k = token;
                  next();
                  next(':');
                  o[k] = val();
                  if (operator != ',') {
                      break;
                  }
                  next(',');
              }
          }
          next('}');
          return o;
      case '[':
          next('[');
          o = [];
          if (operator != ']') {
              for (;;) {
                  o.push(val());
                  if (operator != ',') {
                      break;
                  }
                  next(',');
              }
          }
          next(']');
          return o;
      default:
          if (operator !== null) {
              error("Missing value");
          }
          k = token;
          next();
          return k;
      }
  }
  next();
  return val();
};

/**
 * Serializes JSON object into JSON string.
 *
 * Was taken with changes from http://json.org/json.js.
 *
 * @param {object} v JSON object
 * @return JSON string
 * @type string
 */
Zapatec.Transport.serializeJsonObj = function(v) {
  var a = [];
  /*
    Emit a string.
  */
  function e(s) {
      a[a.length] = s;
  }
  /*
    Convert a value.
  */
  function g(x) {
      var c, i, l, v;
      switch (typeof x) {
      case 'object':
          if (x) {
              if (x instanceof Array) {
                  e('[');
                  l = a.length;
                  for (i = 0; i < x.length; i += 1) {
                      v = x[i];
                      if (typeof v != 'undefined' &&
                              typeof v != 'function') {
                          if (l < a.length) {
                              e(',');
                          }
                          g(v);
                      }
                  }
                  e(']');
                  return;
              } else if (typeof x.toString != 'undefined') {
                  e('{');
                  l = a.length;
                  for (i in x) {
                      v = x[i];
                      if (x.hasOwnProperty(i) &&
                              typeof v != 'undefined' &&
                              typeof v != 'function') {
                          if (l < a.length) {
                              e(',');
                          }
                          g(i);
                          e(':');
                          g(v);
                      }
                  }
                  return e('}');
              }
          }
          e('null');
          return;
      case 'number':
          e(isFinite(x) ? +x : 'null');
          return;
      case 'string':
          l = x.length;
          e('"');
          for (i = 0; i < l; i += 1) {
              c = x.charAt(i);
              if (c >= ' ') {
                  if (c == '\\' || c == '"') {
                      e('\\');
                  }
                  e(c);
              } else {
                  switch (c) {
                      case '\b':
                          e('\\b');
                          break;
                      case '\f':
                          e('\\f');
                          break;
                      case '\n':
                          e('\\n');
                          break;
                      case '\r':
                          e('\\r');
                          break;
                      case '\t':
                          e('\\t');
                          break;
                      default:
                          c = c.charCodeAt();
                          e('\\u00' + Math.floor(c / 16).toString(16) +
                              (c % 16).toString(16));
                  }
              }
          }
          e('"');
          return;
      case 'boolean':
          e(String(x));
          return;
      default:
          e('null');
          return;
      }
  }
  g(v);
  return a.join('');
};

/**
 * Displays error message.
 *
 * <pre>
 * Calls onError callback function provided by user. If there is no onError
 * callback function, displays alert with human readable error description.
 * onError callback function receives following object:
 * {
 *   errorCode [number]: error code,
 *   errorDescription [string]: human readable error description
 * }
 * </pre>
 *
 * @private
 * @param {number} iErrCode Error code
 * @param {string} sError Human readable error description
 * @param {function} onError Callback function provided by user
 */
Zapatec.Transport.displayError = function(iErrCode, sError, onError) {
  if (typeof onError == 'function') {
    onError({
      errorCode: iErrCode,
      errorDescription: sError
    });
  } else {
    alert(sError);
  }
};

/**
 * Translates a URL to the URL relative to the specified or to absolute URL.
 *
 * <pre>
 * Arguments object format:
 * {
 *   url [string]: absolute or relative URL to translate; if absolute, will be
 *    returned as is,
 *   relativeTo [string, optional]: "url" will be translated to the URL relative
 *    to this absolute or relative URL; default: current page URL
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 * @return Translated URL
 * @type string
 */
Zapatec.Transport.translateUrl = function(oArg) {
  if (!oArg || !oArg.url) {
    return null;
  }
  // Cut arguments part from url
  var aFullUrl = oArg.url.split('?', 2);
  var sUrl = aFullUrl[0];
  // Check url
  if (sUrl.charAt(0) == '/' || sUrl.indexOf(':') >= 0) {
    // Return absolute URL as is
    return oArg.url;
  }
  // Get relativeTo
  var sRelativeTo;
  if (typeof oArg.relativeTo != 'string') {
    // By default relative to current page URL
    sRelativeTo = document.location.toString().split('?', 2)[0];
  } else {
    // Remove arguments from relativeTo
    sRelativeTo = oArg.relativeTo.split('?', 2)[0];
    // Check relativeTo
    if (sRelativeTo.indexOf('/') < 0) {
      // Relative to current page URL
      sRelativeTo = document.location.toString().split('?', 2)[0];
    } else if (sRelativeTo.charAt(0) != '/' &&
     sRelativeTo.indexOf(':') < 0) {
      // Transform relativeTo to absolute URL to be able to translate URLs
      // starting from ../
      sRelativeTo = Zapatec.Transport.translateUrl({
        url: sRelativeTo
      });
    }
  }
  // Split URLs
  var aUrl = sUrl.split('/');
  var aRelativeTo = sRelativeTo.split('/');
  // Remove file name
  aRelativeTo.pop();
  // Form new URL
  for (var iToken = 0; iToken < aUrl.length; iToken++) {
    var sToken = aUrl[iToken];
    if (sToken == '..') {
      aRelativeTo.pop();
    } else if (sToken != '.') {
      aRelativeTo.push(sToken);
    }
  }
  aFullUrl[0] = aRelativeTo.join('/');
  // Restore arguments part
  return aFullUrl.join('?');
};

/**
 * Holds currently loading URLs to prevent duplicate loads.
 * @private
 */
Zapatec.Transport.loading = {};

/**
 * Prevents duplicate loads of the same URL when second request is done before
 * first request is completed.
 *
 * <pre>
 * Arguments object format:
 * {
 *   url: [string] absolute URL,
 *   force: [boolean, optional] force reload if it is already loaded,
 *   onLoad: [function, optional] function reference to call on success,
 *   onError: [function, optional] function reference to call on error
 * }
 *
 * Returned object format:
 *
 * If this URL is already loading by another process:
 * {
 *   loading: [boolean] always true
 * }
 *
 * Otherwise:
 * {
 *   onLoad: [function, optional] replacement for function to call on success,
 *   onError: [function, optional] replacement for function to call on error
 * }
 * </pre>
 *
 * @private
 * @param {object} oArg Arguments object
 * @return Returned object
 * @type object
 */
Zapatec.Transport.setupEvents = function(oArg) {
  // Check arguments
  if (!oArg) {
    return {};
  }
  // If loading is forced, we don't need to check if it is already loading
  // If EventDriven is not available, operate as in older versions
  // Check if URL is passed
  if (oArg.force || !Zapatec.EventDriven || !oArg.url) {
    return {
      onLoad: oArg.onLoad,
      onError: oArg.onError
    };
  }
  var sUrl = oArg.url;
  // Add onLoad listener
  if (typeof oArg.onLoad == 'function') {
    Zapatec.EventDriven.addEventListener('zpTransportOnLoad' + sUrl,
     oArg.onLoad);
  }
  // Add onError listener
  if (typeof oArg.onError == 'function') {
    Zapatec.EventDriven.addEventListener('zpTransportOnError' + sUrl,
     oArg.onError);
  }
  // Check if it is already loading
  if (Zapatec.Transport.loading[sUrl]) {
    return {
      loading: true
    };
  } else {
    // Flag
    Zapatec.Transport.loading[sUrl] = true;
    // Replace original callbacks
    return {
      onLoad: new Function("Zapatec.EventDriven.fireEvent('zpTransportOnLoad" +
       sUrl + "');Zapatec.EventDriven.removeEvent('zpTransportOnLoad" +
       sUrl + "');Zapatec.EventDriven.removeEvent('zpTransportOnError" +
       sUrl + "');Zapatec.Transport.loading['" + sUrl + "'] = false;"),
      onError: new Function('oError',
       "Zapatec.EventDriven.fireEvent('zpTransportOnError" +
       sUrl + "',oError);Zapatec.EventDriven.removeEvent('zpTransportOnLoad" +
       sUrl + "');Zapatec.EventDriven.removeEvent('zpTransportOnError" +
       sUrl + "');Zapatec.Transport.loading['" + sUrl + "'] = false;")
    };
  }
};

/**
 * Holds URLs of already loaded JS files to prevent duplicate loads.
 * @private
 */
Zapatec.Transport.loadedJS = {};

/**
 * Checks if specified JS file is already loaded.
 *
 * @private
 * @param {string} sUrl Absolute or relative URL of JS file
 * @param {string} sAbsUrl Optional. Absolute URL of JS file
 * @return Loaded or not
 * @type boolean
 */
Zapatec.Transport.isLoadedJS = function(sUrl, sAbsUrl) {
  // Get absolute URL of the JS file
  if (typeof sAbsUrl == 'undefined') {
    sAbsUrl = Zapatec.Transport.translateUrl({url: sUrl});
  }
  // Check in the list of loaded
  if (Zapatec.Transport.loadedJS[sAbsUrl]) {
    return true;
  }
  // Try to find script tag
  var aScripts = document.getElementsByTagName('script');
  for (var iScript = 0; iScript < aScripts.length; iScript++) {
    var sSrc = aScripts[iScript].getAttribute('src') || '';
    if (sSrc == sUrl) {
      // Add this URL to the list of loaded
      Zapatec.Transport.loadedJS[sAbsUrl] = true;
      return true;
    }
  }
  // Not found
  return false;
};

/**
 * Returns path to the specified js file. Iterates over all loaded script
 * elements starting from the end. Finds specified js file in src attribute of
 * the script element. Splits src attribute value and returns path without js
 * file name.
 *
 * @param {string} sScriptFileName Script file name, e.g. 'zpmywidget.js'
 * @return Path to the script, e.g. '../src/' or '' if path is not found
 * @type string
 */
Zapatec.Transport.getPath = function(sScriptFileName) {
  // Get all script elements
  var aScripts = document.getElementsByTagName('script');
  // Find the script in the list
  for (var iScript = aScripts.length - 1; iScript >= 0; iScript--) {
    var sSrc = aScripts[iScript].getAttribute('src') || '';
    var aTokens = sSrc.split('/');
    // Remove last token
    var sLastToken = aTokens.pop();
    if (sLastToken == sScriptFileName) {
      return aTokens.length ? aTokens.join('/') + '/' : '';
    }
  }
  // Search in loaded JS files
  for (var sSrc in Zapatec.Transport.loadedJS) {
    var aTokens = sSrc.split('/');
    // Remove last token
    var sLastToken = aTokens.pop();
    if (sLastToken == sScriptFileName) {
      return aTokens.length ? aTokens.join('/') + '/' : '';
    }
  }
  // Not found
  return '';
};

/**
 * Writes script tag to the document. Checks if specified JS file is already
 * loaded unless bForce argument is true.
 *
 * <pre>
 * Note: This function must be invoked during page load because it uses
 * document.write method.
 *
 * If special Zapatec.doNotInclude flag is set, this function does nothing.
 * </pre>
 *
 * @param {string} sSrc Src attribute value of the script element
 * @param {string} sId Optional. Id of the script element
 * @param {boolean} bForce Optional. Force reload if it is already loaded
 */
Zapatec.Transport.include = function(sSrc, sId, bForce) {
  // Check flag
  if (Zapatec.doNotInclude) {
    return;
  }
  // Get absolute URL of the JS file
  var sAbsUrl = Zapatec.Transport.translateUrl({url: sSrc});
  // Check if it is already loaded
  if (!bForce && Zapatec.Transport.isLoadedJS(sSrc, sAbsUrl)) {
    return;
  }
  // Include file
  document.write('<script type="text/javascript" src="' + sSrc +
   (typeof sId == 'string' ? '" id="' + sId : '') + '"></script>');
  // Add this URL to the list of loaded
  Zapatec.Transport.loadedJS[sAbsUrl] = true;
};

/**
 * Shortcut. The same as {@link Zapatec.Transport#include}.
 */
Zapatec.include = Zapatec.Transport.include;

/**
 * Includes JS file into the page. Allows URLs from foreign domains. Doesn't
 * check if the JS file is already included. File is loaded asynchronously.
 *
 * @param {string} sSrc Src attribute value of the script element
 * @param {string} sId Optional. Id of the script element
 */
Zapatec.Transport.includeJS = function(sSrc, sId) {
  // Make sure it is asynchronous in all browsers
  setTimeout(function() {
    // Include file
    var oContr = document.body;
    if (!oContr) {
      oContr = document.getElementsByTagName('head')[0];
      if (!oContr) {
        oContr = document;
      }
    }
    var oScript = document.createElement('script');
    oScript.type = 'text/javascript';
    oScript.src = sSrc;
    if (typeof sId == 'string') {
      oScript.id = sId;
    }
    // This is important for Safari to assign attributes before appending
    oContr.appendChild(oScript);
  }, 0);
};

/**
 * Fetches JS file using fetch and evaluates it in global scope.
 *
 * <pre>
 * When JS file is loaded successfully, onLoad callback function is called
 * without arguments. URL is added into Zapatec.Transport.loadedJS array
 * and will not be fetched again on next function call unless force argument is
 * set to true.
 *
 * onError callback function receives following object:
 * {
 *   errorCode: [number] server status number (404, etc.),
 *   errorDescription: [string] human readable error description
 * }
 *
 * One of the arguments: module or url is required. When url is passed,
 * module argument is ignored.
 *
 * If module argument is used, function gets all "script" elements using
 * getElementsByTagName and searches for the first element having "src"
 * attribute value ending with (relativeModule + ".js") (default relativeModule
 * value is "transport"). Path to the module is taken from that src attribute
 * value and will be the same as path to relativeModule file.
 *
 * Arguments object format:
 * {
 *   url: [string, optional] absolute or relative URL of JS file,
 *   module: [string, optional] module name (file name without .js extension);
 *    ignored when "url" is defined,
 *   path: [string, optional] path where to search "module" (default is
 *    Zapatec.zapatecPath if it is defined); ignored when "url" is defined,
 *   async: [boolean, optional] use asynchronous mode (default: true),
 *   force: [boolean, optional] force reload if it is already loaded,
 *   onLoad: [function, optional] function reference to call on success,
 *   onError: [function, optional] function reference to call on error
 * }
 *
 * Note: If "force" is used, you should add 'r=' + Math.random() parameter to
 * URL to prevent loading from browser cache.
 *
 * <b>
 * Note: Global variables must be declared without "var" keyword. Otherwise
 * they will be ignored by Safari.
 * </b>
 *
 * If special Zapatec.doNotInclude flag is set, this function just calls onLoad
 * callback function.
 * </pre>
 *
 * @param {object} oArg Arguments object
 */
Zapatec.Transport.loadJS = function(oArg) {
  // Check arguments
  if (!(oArg instanceof Object)) {
    return;
  }
  if (typeof oArg.async == 'undefined') {
    oArg.async = true;
  }
  // Get URL of JS file
  var sUrl = null;
  if (oArg.url) {
    sUrl = oArg.url;
  } else if (oArg.module) {
    var sPath = '';
    if (typeof oArg.path != 'undefined') {
      sPath = oArg.path;
    } else if (typeof Zapatec.zapatecPath != 'undefined') {
      sPath = Zapatec.zapatecPath;
    }
    sUrl = sPath + oArg.module + '.js';
  } else {
    return;
  }
  // Get absolute URL of the JS file
  var sAbsUrl = Zapatec.Transport.translateUrl({url: sUrl});
  // Check arguments
  if (!oArg.onLoad) {
    oArg.onLoad = null;
  }
  if (!oArg.onError) {
    oArg.onError = null;
  }
  // Check if it is already loaded
  if (Zapatec.doNotInclude ||
   (!oArg.force && Zapatec.Transport.isLoadedJS(sUrl, sAbsUrl))) {
    // onLoad callback
    if (typeof oArg.onLoad == 'function') {
      oArg.onLoad();
    }
    return;
  }
  // Setup onLoad and onError events
  var oHandlers = Zapatec.Transport.setupEvents({
    url: sAbsUrl,
    force: oArg.force,
    onLoad: oArg.onLoad,
    onError: oArg.onError
  });
  // Don't need to continue if this url is already loading by another process
  if (oHandlers.loading) {
    return;
  }
  // Load JS file
  Zapatec.Transport.fetch({
    url: sUrl,
    async: oArg.async,
    onLoad: function(oRequest) {
      // Can be loaded in two processes simultaneously
      if (oArg.force || !Zapatec.Transport.loadedJS[sAbsUrl]) {
        var aTokens = sUrl.split('/');
        // Remove last token
        var sLastToken = aTokens.pop();
        // Store path to current module
        Zapatec.lastLoadedModule = aTokens.join('/') + '/';
        // Evaluate code in global scope
        Zapatec.Transport.evalGlobalScope(oRequest.responseText);
        // clear path to last loaded module
        Zapatec.lastLoadedModule = null;
        // Add this URL to the list of loaded
        Zapatec.Transport.loadedJS[sAbsUrl] = true;
      }
      // onLoad callback
      if (typeof oHandlers.onLoad == 'function') {
        oHandlers.onLoad();
      }
    },
    onError: oHandlers.onError
  });
};

/**
 * Includes CSS file into the page. Allows URLs from foreign domains. Doesn't
 * check if the CSS file is already included. File is loaded asynchronously.
 * Requires that head section of the page already exists because link tag
 * may appear only inside head.
 *
 * @param {string} sHref Href attribute value of the link element
 */
Zapatec.Transport.includeCSS = function(sHref) {
  // May appear only inside head
  var oContr = document.getElementsByTagName('head')[0];
  if (!oContr) {
    return;
  }
  var oLink = document.createElement('link');
  oLink.setAttribute('rel', 'stylesheet');
  oLink.setAttribute('type', 'text/css');
  oLink.setAttribute('href', sHref);
  oContr.appendChild(oLink);
};

/**
 * Holds URLs of already loaded CSS files to prevent duplicate loads.
 * @private
 */
Zapatec.Transport.loadedCss = {};

/**
 * Fetches style sheet using fetch and loads it into the document.
 *
 * <pre>
 * When stylesheet is loaded successfully, onLoad callback function is called
 * without arguments. URL is added into Zapatec.Transport.loadedCss array
 * and will not be fetched again on next function call unless force argument is
 * set to true.
 *
 * onError callback function receives following object:
 * {
 *   errorCode: server status number (404, etc.) [number],
 *   errorDescription: human readable error description [string]
 * }
 *
 * Arguments object format:
 * {
 *   url: absolute or relative URL of CSS file [string],
 *   async: [boolean, optional] use asynchronous mode (default: true),
 *   force: [boolean, optional] force reload if it is already loaded,
 *   onLoad: [function, optional] function reference to call on success,
 *   onError: [function, optional] function reference to call on error
 * }
 *
 * Note: If "force" is used, you should add 'r=' + Math.random() parameter to
 * URL to prevent loading from browser cache.
 * </pre>
 *
 * @param {object} oArg Arguments object
 */
Zapatec.Transport.loadCss = function(oArg) {
  if (Zapatec.StyleSheet) {
    Zapatec.Transport.loadCssWithStyleSheet(oArg);
  } else {
    // Load Zapatec.StyleSheet class
    Zapatec.Transport.loadJS({
      module: 'stylesheet',
      async: oArg.async,
      onLoad: function() {
        Zapatec.Transport.loadCssWithStyleSheet(oArg);
      }
    });
  }
};

/**
 * Fetches style sheet using fetch and loads it into the document.
 * Zapatec.StyleSheet must exist.
 *
 * @private
 * @param {object} oArg Arguments object see
 * {@link Zapatec.Transport#loadCss}
 */
Zapatec.Transport.loadCssWithStyleSheet = function(oArg) {
  // Check arguments
  if (!(oArg instanceof Object)) {
    return;
  }
  if (!oArg.url) {
    return;
  }
  if (typeof oArg.async == 'undefined') {
    oArg.async = true;
  }
  // Get absolute URL of the CSS file
  var sAbsUrl = Zapatec.Transport.translateUrl({url: oArg.url});
  // Check if it is already loaded
  if (!oArg.force) {
    if (Zapatec.Transport.loadedCss[sAbsUrl]) {
      // onLoad callback
      if (typeof oArg.onLoad == 'function') {
        oArg.onLoad();
      }
      return;
    }
    var aLinks = document.getElementsByTagName('link');
    for (var iLnk = 0; iLnk < aLinks.length; iLnk++) {
      var sHref = aLinks[iLnk].getAttribute('href') || '';
      // Make it absolute
      sHref = Zapatec.Transport.translateUrl({url: sHref});
      if (sHref == sAbsUrl) {
        // Add this url to the list of loaded
        Zapatec.Transport.loadedCss[sAbsUrl] = true;
        // onLoad callback
        if (typeof oArg.onLoad == 'function') {
          oArg.onLoad();
        }
        return;
      }
    }
  }
  // Setup onLoad and onError events
  var oHandlers = Zapatec.Transport.setupEvents({
    url: sAbsUrl,
    force: oArg.force,
    onLoad: oArg.onLoad,
    onError: oArg.onError
  });
  // Don't need to continue if this url is already loading by another process
  if (oHandlers.loading) {
    return;
  }
  // Load CSS file
  Zapatec.Transport.fetch({
    url: oArg.url,
    async: oArg.async,
    onLoad: function(oRequest) {
      // Parse CSS file.
      // Find URLs and translate them to absolute.
      // Find @import rules and load corresponding CSS files.
      var sCss = oRequest.responseText;
      var aResultCss = [];
      // Will hold image URLs to preload
      var aImgUrls = [];
      // Will hold CSS URLs to load
      var aCssUrls = [];
      // Move first cursor to the beginning of the string
      var iPos = 0;
      // Move second cursor to the pattern
      var iNextPos = sCss.indexOf('url(', iPos);
      while (iNextPos >= 0) {
        // Move first cursor to the URL
        iNextPos += 4;
        // Check if this is @import rule
        var sToken = sCss.substring(iPos, iNextPos);
        var bIsImport = /@import\s+url\($/.test(sToken);
        // Add part of the string before URL
        aResultCss.push(sToken);
        // Move second cursor to the new location to start the search from
        iPos = iNextPos;
        // Search the end of URL
        iNextPos = sCss.indexOf(')', iPos);
        if (iNextPos >= 0) {
          // Remove quotes
          var sImgUrl = sCss.substring(iPos, iNextPos);
          sImgUrl = sImgUrl.replace(/['"]/g, '');
          // Translate image URL relative to CSS file URL
          sImgUrl = Zapatec.Transport.translateUrl({
            url: sImgUrl,
            relativeTo: oArg.url
          });
          // Convert to absolute URL
          sImgUrl = Zapatec.Transport.translateUrl({
            url: sImgUrl
          });
          // Add translated URL
          aResultCss.push(sImgUrl);
          // Add URL to the list
          if (bIsImport) {
            // Add CSS URL to load list
            aCssUrls.push(sImgUrl);
          } else {
            // Add image URL to preload list
            aImgUrls.push(sImgUrl);
          }
          // Move second cursor to the new location to start the search from
          iPos = iNextPos;
          // Search next pattern
          iNextPos = sCss.indexOf('url(', iPos);
        }
      }
      // Add the rest of string
      aResultCss.push(sCss.substr(iPos));
      // Get translated CSS text
      sCss = aResultCss.join('');
      // Load CSS files
      Zapatec.Transport.loadCssList({
        urls: aCssUrls,
        async: oArg.async,
        onLoad: function() {
          // Add style sheet rules into the page
          (new Zapatec.StyleSheet()).addParse(sCss);
          // Fire event
          if (typeof oHandlers.onLoad == 'function') {
            oHandlers.onLoad();
          }
        }
      });
      // Add this URL to the list of loaded
      Zapatec.Transport.loadedCss[sAbsUrl] = true;
      // Preload images
      Zapatec.Transport.preloadImages({
        urls: aImgUrls,
        timeout: 60000 // 1 minute
      });
    },
    onError: oHandlers.onError
  });
};

/**
 * Loads several CSS files one by one it into the document.
 *
 * <pre>
 * This function behaves differently from other Zapatec.Transport functions.
 * onLoad callback function will be called in any case, even if errors occured
 * during loading. If there are multiple errors, onError callback function will
 * be called once for every passed URL that wasn't loaded successfully.
 *
 * onLoad callback function is called without arguments.
 *
 * onError callback function receives following object:
 * {
 *   errorCode: server status number (404, etc.) [number],
 *   errorDescription: human readable error description [string]
 * }
 *
 * Arguments object format:
 * {
 *   urls: array of absolute or relative URLs of CSS files to load [object]
 *    (files will be loaded in order they appear in the array),
 *   async: [boolean, optional] use asynchronous mode (default: true),
 *   force: [boolean, optional] force reload if it is already loaded,
 *   onLoad: function reference to call on completion [function] (optional),
 *   onError: function reference to call on error [function] (optional)
 * }
 *
 * Note: If "force" is used, you should add 'r=' + Math.random() parameter to
 * URL to prevent loading from browser cache.
 * </pre>
 *
 * @param {object} oArg Arguments object
 */
Zapatec.Transport.loadCssList = function(oArg) {
  // Check arguments
  if (!(oArg instanceof Object)) {
    return;
  }
  if (typeof oArg.async == 'undefined') {
    oArg.async = true;
  }
  if (!oArg.onLoad) {
    oArg.onLoad = null;
  }
  if (!oArg.onError) {
    oArg.onError = null;
  }
  if (!oArg.urls || !oArg.urls.length) {
    // onLoad callback
    if (typeof oArg.onLoad == 'function') {
      oArg.onLoad();
    }
    return;
  }
  // Get first URL in the array
  var sUrl = oArg.urls.shift();
  // CSS file onLoad handler
  var funcOnLoad = function() {
    // Load the rest of URLs
    Zapatec.Transport.loadCssList({
      urls: oArg.urls,
      async: oArg.async,
      force: oArg.force,
      onLoad: oArg.onLoad,
      onError: oArg.onError
    });
  };
  // Load CSS file
  Zapatec.Transport.loadCss({
    url: sUrl,
    async: oArg.async,
    force: oArg.force,
    onLoad: funcOnLoad,
    onError: function(oError) {
      Zapatec.Transport.displayError(oError.errorCode, oError.errorDescription,
       oArg.onError);
      funcOnLoad();
    }
  });
};

/**
 * Holds image preloads.
 * @private
 */
Zapatec.Transport.imagePreloads = [];

/**
 * Preloads one or several images at once. See Zapatec.PreloadImages class
 * (utils/preloadimages.js) for details.
 *
 * <pre>
 * Arguments object format:
 * {
 *   urls: [object] array of absolute or relative image URLs to preload,
 *   onLoad: [function, optional] onload event handler,
 *   timeout: [number, optional] number of milliseconds to wait for onload
 *    event before forcing it
 * }
 * </pre>
 *
 * @param {object} oArg Arguments object
 */
Zapatec.Transport.preloadImages = function(oArg) {
  if (Zapatec.PreloadImages) {
    Zapatec.Transport.imagePreloads.push(new Zapatec.PreloadImages(oArg));
  } else {
    // Load preloadimages.js module
    Zapatec.Transport.loadJS({
      module: 'preloadimages',
      onLoad: function() {
        Zapatec.Transport.imagePreloads.push(new Zapatec.PreloadImages(oArg));
      }
    });
  }
};
