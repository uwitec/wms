// $Id: popup.js 4432 2006-09-14 07:45:55Z shacka $
/*
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

function __dlg_onclose() {
	opener.Dialog._return(null);
};

function __dlg_init() {
	window.dialogArguments = opener.Dialog._arguments;
	document.body.onkeypress = __dlg_close_on_esc;
};

// closes the dialog and passes the return info upper.
function __dlg_close(val) {
	opener.Dialog._return(val);
	window.close();
};

function __dlg_close_on_esc(ev) {
	ev || (ev = window.event);
	if (ev.keyCode == 27) {
		window.close();
		return false;
	}
	return true;
};
