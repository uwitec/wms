/**
 *$Id: html2xhtml.js $
 * @fileoverview A convertor from html to xhtml
 *
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

html2Xhtml = function() {
  
};

/**
 * Converts html string into xhtml
 * @public
 * @param {string} html Html to convert
 */
html2Xhtml.convert = function(html) {
  // Remove font tags
  html = html.replace(/<(font)[^>]*>/gi, "");
  html = html.replace(/<(\/font)>/gi, "");

  var state = 0;
  var xhtml = '';
  var p = 0;
  var unget = false;
  var tagname = '';
  var attrname = '';
  var attrval = '';
  var quot = '';
  var len = html.length;
  var phpval = '';
  var tagtype = 0;
  var insidepre = false;
  while (1) {
    if (p >= len && !unget) {
      return xhtml
    }
    if (unget) {
      unget = false
    }
    else {
      var c = html.substr(p++, 1)
    }
    switch (state) {
      case 0:
        if (c == '<') {
          state = 1;
          break
        }
        var cc = c.charCodeAt();
        if (html2Xhtml.charEntities[cc]) {
          xhtml += '&' + html2Xhtml.charEntities[cc] + ';'
        }
        else {
        xhtml += c
        }
        break;
      case 1:
        if (/[a-zA-Z]/.test(c)) {
          state = 2;
          tagtype = 1;
          tagname = c.toLowerCase();
          break
        }
        if (c == '/') {
          state = 2;
          tagtype = -1;
          break
        }
        if (c == '!') {
          if (html.substr(p, 2) == '--') {
            xhtml += '<!--';
            p += 2;
            state = 9;
            break
          }
          xhtml += '<!';
          state = 10;
          break
        }
        if (c == '?') {
          state = 11;
          xhtml += '<' + '?';
          break
        }
        xhtml += '&lt;';
        unget = true;
        state = 0;
        break;
      case 2:
        if (html2Xhtml.isSpaceChar[c]) {
          var spaceChar = (!insidepre && tagtype > 0 &&
                           html2Xhtml.hasNLBefore[tagname] && xhtml.length &&
                           xhtml.substr(xhtml.length - 1, 1) != '\n'?'\n':'');
          xhtml += spaceChar + (tagtype > 0?'<':'</') + tagname;
          state = 3;
          break
        }
        if (c == '/') {
          var spaceChar = (!insidepre && tagtype > 0 &&
                           html2Xhtml.hasNLBefore[tagname] && xhtml.length &&
                           xhtml.substr(xhtml.length - 1, 1) != '\n'?'\n':'');
          xhtml += spaceChar + (tagtype > 0?'<':'</') + tagname;
          if (html.substr(p, 1) != '>') {
            state = 3;
            break
          }
          state = 4;
          break
        }
        if (c == '>') {
          var spaceChar = (!insidepre && tagtype > 0 &&
                           html2Xhtml.hasNLBefore[tagname] && xhtml.length &&
                           xhtml.substr(xhtml.length - 1, 1) != '\n'?'\n':'');
          xhtml += spaceChar + (tagtype > 0?'<':'</') + tagname;
          unget = true;
          state = 4;
          break
        }
        tagname += c.toLowerCase();
        break;
      case 3:
        if (html2Xhtml.isSpaceChar[c]) {
          break
        }
        if (c == '/') {
          if (html.substr(p, 1) != '>') {
            break
          }
          state = 4;
          break
        }
        if (c == '>') {
          unget = true;
          state = 4;
          break
        }
        attrname = c.toLowerCase();
        attrval = '';
        state = 5;
        break;
      case 4:
        xhtml += (html2Xhtml.isEmptyTag[tagname]?' />':'>') +
                 (!insidepre && tagtype < 0 && html2Xhtml.hasNLAfter[tagname] &&
                  p < len && html.substr(p, 1) != '\n'?'\n':'');
        if (tagtype > 0 && html2Xhtml.dontAnalyzeContent[tagname]) {
          state = 13;
          attrname = attrval = quot = '';
          tagtype = 0;
          break
        }
        if (tagname == 'pre') {
          insidepre = !insidepre
        }
        state = 0;
        tagname = attrname = attrval = quot = '';
        tagtype = 0;
        break;
      case 5:
        if (html2Xhtml.isSpaceChar[c]) {
          xhtml += ' ' + attrname;
          if (html2Xhtml.isEmptyAttr[attrname]) {
            xhtml += '="' + attrname + '"'
          }
          state = 3;
          break
        }
        if (c == '/') {
          xhtml += ' ' + attrname;
          if (html2Xhtml.isEmptyAttr[attrname]) {
            xhtml += '="' + attrname + '"'
          }
          if (html.substr(p, 1) != '>') {
            state = 3;
            break
          }
          state = 4;
          break
        }
        if (c == '>') {
          xhtml += ' ' + attrname;
          if (html2Xhtml.isEmptyAttr[attrname]) {
            xhtml += '="' + attrname + '"'
          }
          unget = true;
          state = 4;
          break
        }
        if (c == '=') {
          xhtml += ' ' + attrname + '=';
          state = 6;
          break
        }
        if (c == '"' || c == "'") {
          attrname += '?'
        }
        else {
          attrname += c.toLowerCase()
        }
        break;
      case 6:
        if (html2Xhtml.isSpaceChar[c]) {
          xhtml += (html2Xhtml.isEmptyAttr[attrname]?'"' + attrname + '"':'""');
          state = 3;
          break
        }
        if (c == '>') {
          xhtml += (html2Xhtml.isEmptyAttr[attrname]?'"' + attrname + '"':'""');
          unget = true;
          state = 4;
          break
        }
        if (c == '/' && html.substr(p, 1) == '>') {
          xhtml += (html2Xhtml.isEmptyAttr[attrname]?'"' + attrname + '"':'""');
          state = 4;
          break
        }
        if (c == '"' || c == "'") {
          quot = c;
          state = 8;
          break
        }
        attrval = c;
        state = 7;
        break;
      case 7:
        if (html2Xhtml.isSpaceChar[c]) {
          xhtml += '"' + html2Xhtml.escapeQuot(attrval, '"') + '"';
          state = 3;
          break
        }
        if (c == '/' && html.substr(p, 1) == '>') {
          xhtml += '"' + html2Xhtml.escapeQuot(attrval, '"') + '"';
          state = 4;
          break
        }
        if (c == '>') {
          unget = true;
          xhtml += '"' + html2Xhtml.escapeQuot(attrval, '"') + '"';
          state = 4;
          break
        }
        attrval += c;
        break;
      case 8:
        if (c == quot) {
          xhtml += '"' + html2Xhtml.escapeQuot(attrval, '"') + '"';
          state = 3;
          break
        }
        attrval += c;
        break;
      case 9:
        if (c == '-' && html.substr(p, 2) == '->') {
          p += 2;
          xhtml += '-->';
          state = 0;
          break
        }
        xhtml += c;
        break;
      case 10:
        if (c == '>') {
          state = 0
        }
        xhtml += c;
        break;
      case 11:
        if (c == "'" || c == '"') {
          quot = c;
          state = 12;
          break
        }
        if (c == '?' && html.substr(p, 1) == '>') {
          state = 0;
          xhtml += '?' + '>';
          p++;
          break
        }
        xhtml += c;
        break;
      case 12:
        if (c == quot) {
          state = 11;
          xhtml += quot + html2Xhtml.escapeQuot(phpval, quot) + quot;
          phpval = quot = '';
          break
        }
        phpval += c;
        break;
      case 13:
        if (c == '<' && html.substr(p, tagname.length + 1).toLowerCase() ==
                        '/' + tagname) {
          unget = true;
          state = 0;
          tagname = '';
          break
        }
        if (tagname == 'textarea') {
          xhtml += html2Xhtml.escapeHTMLChar(c)
        }
        else {
          xhtml += c
        }
        break
    }
  }

  return xhtml
};

html2Xhtml.escapeQuot = function(str, quot) {
  if (!quot) {
    quot = '"'
  }
  if (quot == '"') {
    return str.replace(/"/ig, '\\"')
  }
  return str.replace(/'/ig, "\\'")
};

html2Xhtml.escapeHTMLChar = function(c) {
  if (c == '&') {
    return'&amp;'
  }
  if (c == '<') {
    return'&lt;'
  }
  if (c == '>') {
    return'&gt;'
  }
  var cc = c.charCodeAt();
  if (html2Xhtml.charEntities[cc]) {
    return'&' + html2Xhtml.charEntities[cc] + ';'
  }
  else {
    return c
  }
};
html2Xhtml.isSpaceChar = {' ':1,'\r':1,'\n':1,'\t':1};
html2Xhtml.isEmptyTag = {'area':1,'base':1,'basefont':1,'br':1,'hr':1,'img':1,'input':1,'link':1,'meta':1,'param':1};
html2Xhtml.isEmptyAttr = {'checked':1,'compact':1,'declare':1,'defer':1,'disabled':1,'ismap':1,'multiple':1,'noresize':1,'nosave':1,'noshade':1,'nowrap':1,'readonly':1,'selected':1};
html2Xhtml.hasNLBefore = {'div':1,'p':1,'table':1,'tbody':1,'tr':1,'td':1,'th':1,'title':1,'head':1,'body':1,'script':1,'comment':1,'li':1,'meta':1,'h1':1,'h2':1,'h3':1,'h4':1,'h5':1,'h6':1,'hr':1,'ul':1,'ol':1,'option':1,'link':1};
html2Xhtml.hasNLAfter = {'html':1,'head':1,'body':1,'p':1,'th':1,'style':1};
html2Xhtml.dontAnalyzeContent = {'textarea':1,'script':1,'style':1};
html2Xhtml.charEntities = {160:'nbsp',161:'iexcl',162:'cent',163:'pound',164:'curren',165:'yen',166:'brvbar',167:'sect',168:'uml',169:'copy',170:'ordf',171:'laquo',172:'not',173:'shy',174:'reg',175:'macr',176:'deg',177:'plusmn',178:'sup2',179:'sup3',180:'acute',181:'micro',182:'para',183:'middot',184:'cedil',185:'sup1',186:'ordm',187:'raquo',188:'frac14',189:'frac12',190:'frac34',191:'iquest',192:'agrave',193:'aacute',194:'acirc',195:'atilde',196:'auml',197:'aring',198:'aelig',199:'ccedil',200:'egrave',201:'eacute',202:'ecirc',203:'euml',204:'igrave',205:'iacute',206:'icirc',207:'iuml',208:'eth',209:'ntilde',210:'ograve',211:'oacute',212:'ocirc',213:'otilde',214:'ouml',215:'times',216:'oslash',217:'ugrave',218:'uacute',219:'ucirc',220:'uuml',221:'yacute',222:'thorn',223:'szlig',224:'agrave',225:'aacute',226:'acirc',227:'atilde',228:'auml',229:'aring',230:'aelig',231:'ccedil',232:'egrave',233:'eacute',234:'ecirc',235:'euml',236:'igrave',237:'iacute',238:'icirc',239:'iuml',240:'eth',241:'ntilde',242:'ograve',243:'oacute',244:'ocirc',245:'otilde',246:'ouml',247:'divide',248:'oslash',249:'ugrave',250:'uacute',251:'ucirc',252:'uuml',253:'yacute',254:'thorn',255:'yuml',338:'oelig',339:'oelig',352:'scaron',353:'scaron',376:'yuml',710:'circ',732:'tilde',8194:'ensp',8195:'emsp',8201:'thinsp',8204:'zwnj',8205:'zwj',8206:'lrm',8207:'rlm',8211:'ndash',8212:'mdash',8216:'lsquo',8217:'rsquo',8218:'sbquo',8220:'ldquo',8221:'rdquo',8222:'bdquo',8224:'dagger',8225:'dagger',8240:'permil',8249:'lsaquo',8250:'rsaquo',8364:'euro',402:'fnof',913:'alpha',914:'beta',915:'gamma',916:'delta',917:'epsilon',918:'zeta',919:'eta',920:'theta',921:'iota',922:'kappa',923:'lambda',924:'mu',925:'nu',926:'xi',927:'omicron',928:'pi',929:'rho',931:'sigma',932:'tau',933:'upsilon',934:'phi',935:'chi',936:'psi',937:'omega',945:'alpha',946:'beta',947:'gamma',948:'delta',949:'epsilon',950:'zeta',951:'eta',952:'theta',953:'iota',954:'kappa',955:'lambda',956:'mu',957:'nu',958:'xi',959:'omicron',960:'pi',961:'rho',962:'sigmaf',963:'sigma',964:'tau',965:'upsilon',966:'phi',967:'chi',968:'psi',969:'omega',977:'thetasym',978:'upsih',982:'piv',8226:'bull',8230:'hellip',8242:'prime',8243:'prime',8254:'oline',8260:'frasl',8472:'weierp',8465:'image',8476:'real',8482:'trade',8501:'alefsym',8592:'larr',8593:'uarr',8594:'rarr',8595:'darr',8596:'harr',8629:'crarr',8656:'larr',8657:'uarr',8658:'rarr',8659:'darr',8660:'harr',8704:'forall',8706:'part',8707:'exist',8709:'empty',8711:'nabla',8712:'isin',8713:'notin',8715:'ni',8719:'prod',8721:'sum',8722:'minus',8727:'lowast',8730:'radic',8733:'prop',8734:'infin',8736:'ang',8743:'and',8744:'or',8745:'cap',8746:'cup',8747:'int',8756:'there4',8764:'sim',8773:'cong',8776:'asymp',8800:'ne',8801:'equiv',8804:'le',8805:'ge',8834:'sub',8835:'sup',8836:'nsub',8838:'sube',8839:'supe',8853:'oplus',8855:'otimes',8869:'perp',8901:'sdot',8968:'lceil',8969:'rceil',8970:'lfloor',8971:'rfloor',9001:'lang',9002:'rang',9426:'copy',9674:'loz',9824:'spades',9827:'clubs',9829:'hearts',9830:'diams'};