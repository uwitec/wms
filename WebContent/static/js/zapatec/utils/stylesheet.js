/**
 * @fileoverview Zapatec StyleSheet class definition. Used to manipulate with
 * style sheets.
 *
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/* $Id: stylesheet.js 6286 2007-02-14 15:33:51Z alex $ */

if (typeof Zapatec == 'undefined') {
  /**
   * @ignore Namespace definition.
   */
  Zapatec = function() {};
}

/**
 * Zapatec StyleSheet class.
 *
 * <pre>
 * Important: Try not to have more than 30 style tags on the page because
 * http://support.microsoft.com/kb/262161
 *
 * When IE raises exception due to the limitation above, last style tag on the
 * page is used instead of creating new one. In this case use of
 * {@link Zapatec.StyleSheet#removeRules} is not recommended.
 * </pre>
 *
 * @constructor
 * @param {boolean} bUseLast Optional. If true, will try to find last style tag
 * and use it. Will create new style tag only if there are no styles on the page
 */
Zapatec.StyleSheet = function(bUseLast) {
  if (bUseLast) {
    // Use last style tag
    if (document.createStyleSheet) {
      // IE
      if (document.styleSheets.length) {
        this.styleSheet = document.styleSheets[document.styleSheets.length - 1];
      }
    } else {
      // Others
      var aStyleSheets = document.getElementsByTagName('style');
      if (aStyleSheets.length) {
        this.styleSheet = aStyleSheets[aStyleSheets.length - 1];
      }
/* Works properly only in Firefox
      if (document.styleSheets) {
        this.n = document.styleSheets.length - 1;
      }
*/
    }
  }
  if (!this.styleSheet) {
    if (document.createStyleSheet) {
      // IE
      try {
        this.styleSheet = document.createStyleSheet();
      } catch(oException) {
        // There is a limit of 30 style tags in Internet Explorer
        this.styleSheet = document.styleSheets[document.styleSheets.length - 1];
      };
    } else {
      // Others
      this.styleSheet = document.createElement('style');
      this.styleSheet.type = 'text/css';
      var oHead = document.getElementsByTagName('head')[0];
      // If there is no head tag on the page (Opera only)
      if (!oHead) {
        oHead = document.documentElement;
      }
      if (oHead) {
        oHead.appendChild(this.styleSheet);
      }
/* Works properly only in Firefox
      if (document.styleSheets) {
        this.n = document.styleSheets.length - 1;
      }
*/
    }
  }
};

/**
 * Adds a rule to the style sheet.
 *
 * @param {string} strSelector Rule selector
 * @param {string} strDeclarations Rule declarations
 */
Zapatec.StyleSheet.prototype.addRule = function(strSelector, strDeclarations) {
  if (!this.styleSheet) {
    // Object in not initialized properly
    return;
  }
  if (document.createStyleSheet) {
    // IE
/* Any unsupported selector in addRule crashes IE 6
    this.styleSheet.addRule(strSelector, strDeclarations);
*/
    this.styleSheet.cssText += strSelector + ' { ' + strDeclarations + ' }';
/* Works properly only in Firefox
  } else if (document.styleSheets) {
    // Firefox, Safari, Konqueror
    var objStyleSheet = document.styleSheets.item(this.n);
    objStyleSheet.insertRule(strSelector + ' { ' + strDeclarations + ' }',
     objStyleSheet.cssRules.length);
*/
  } else {
    // Opera
    this.styleSheet.appendChild(
     document.createTextNode(strSelector + ' { ' + strDeclarations + ' }')
    );
  }
};

/**
 * Removes all rules from the style sheet.
 */
Zapatec.StyleSheet.prototype.removeRules = function() {
  if (!this.styleSheet) {
    // Object in not initialized properly
    return;
  }
  if (document.createStyleSheet) {
    // IE
    var iRules = this.styleSheet.rules.length;
    for (var iRule = 0; iRule < iRules; iRule++) {
      this.styleSheet.removeRule();
    }
/* Works properly only in Firefox
  } else if (document.styleSheets) {
    // Firefox, Safari, Konqueror
    var objStyleSheet = document.styleSheets.item(this.n);
    var iRules = objStyleSheet.cssRules.length;
    for (var iRule = 0; iRule < iRules; iRule++) {
      objStyleSheet.deleteRule(0);
    }
*/
  } else {
    // Opera
    while (this.styleSheet.firstChild) {
      this.styleSheet.removeChild(this.styleSheet.firstChild);
    }
  }
};

/**
 * Parses a CSS string and adds rules into the style sheet.
 *
 * @param {string} strStyleSheet CSS string
 */
Zapatec.StyleSheet.prototype.addParse = function(strStyleSheet) {
  // Remove comments
  var arrClean = [];
  var arrTokens = strStyleSheet.split('/*');
  for (var iTok = 0; iTok < arrTokens.length; iTok++) {
    var arrTails = arrTokens[iTok].split('*/');
    arrClean.push(arrTails[arrTails.length - 1]);
  }
  strStyleSheet = arrClean.join('');
  // Remove at-rules
  strStyleSheet = strStyleSheet.replace(/@[^{]*;/g, '');
  // Split to styles
  var arrStyles = strStyleSheet.split('}');
  for (var iStl = 0; iStl < arrStyles.length; iStl++) {
    // Split to selector and declarations
    var arrRules = arrStyles[iStl].split('{');
    if (arrRules[0] && arrRules[1]) {
      // Split selector
      var arrSelectors = arrRules[0].split(',');
      for (var iSel = 0; iSel < arrSelectors.length; iSel++) {
        this.addRule(arrSelectors[iSel], arrRules[1]);
      }
    }
  }
};
