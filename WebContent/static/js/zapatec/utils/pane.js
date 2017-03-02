// $Id: pane.js 6781 2007-03-28 09:47:39Z slip $
/*
 * <pre>
 * Copyright (c) 2004-2006 by Zapatec, Inc.
 * http://www.zapatec.com
 * 1700 MLK Way, Berkeley, California,
 * 94709, U.S.A.
 * All rights reserved.
 * </pre>
 */

/**
* Zapatec Pane object. Creates the element for displaying content
* and gives an interface to work with it.
* @param config [object] - pane config.
*
* Constructor recognizes the following properties of the config object
* \code
*	property name		| description
*-------------------------------------------------------------------------------------------------
*	parent					| [string or object] Reference to DOM element where
*							| newly created Pane will be placed. By default -
*							| document.body
*	containerType			| [string] Required. Possible values: div|iframe|current -
*							| what type of pane to create
*							| * div - create new DIV element and add it into parent
*							| * iframe - create new IFRAME element and add it into parent
*							| * current - use parent as pane container
*	sourceType				| [string] see Zapatec.Widget documentation for this option
*	source					| [string or object] see Zapatec.Widget documentation
*							| for this option
*   width                   | [number] width of the Pane in "px"
*   height                  | [number] height of the Pane in "px"
*   autoContentWidth        | [boolean] Option which determines if the width of the Pane will be 
*                           | determined automatically.
*   autoContentHeight       | [boolean] Option which determines if the height of the Pane will be 
*                           | determined automatically.
*   id                      | [string] Optionally you can pass some string to be able to seek Pane by this ID
*   onlyInit                | [boolean] For developing needs this option will disable calling create method
*                           | of the Pane from init, so HTML structure will not be created after calling
*                           | constructor.
*   showLoadingIndicator    | [boolean] Determines if a loading progress indicator is to be shown
*                           | inside pane during loading. Default is true.
* \endcode
*/
Zapatec.Pane = function(objArgs){
	this.config = {};

	if(arguments.length == 0){
		objArgs = {};
	}
	
	//type of the widget - pane in our case :)
	this.widgetType = "pane";
	// internal variable that indicates if iframe was loaded
	this.ready = false;
	//is the content still loading
	this.loading = false;
	// internal variable to detect if Pane was prepared (prepareHtml was called)
	this.prepared = false;
	// variable to store reference to pane container
	Zapatec.Utils.createProperty(this, "container", null);
	// variable to store the reference to the content element
	Zapatec.Utils.createProperty(this, "contentElement", null);
	// internal variable stores reference to IFRAME's document object
	Zapatec.Utils.createProperty(this, "iframeDocument", null);
	//calling superconstructor
	Zapatec.Pane.SUPERconstructor.call(this, objArgs);
}

Zapatec.Pane.id = "Zapatec.Pane";

// Inherit SuperClass
Zapatec.inherit(Zapatec.Pane, Zapatec.Widget);

/**
* Init function. Actually this function does the creation of element
* itself, not the constructor.
*/
Zapatec.Pane.prototype.init = function(objArgs){
	//parent element of the Pane
	Zapatec.Utils.createProperty(this.config, "parent", document.body);
	//theme
	this.config.theme = null;
	//initial width
	this.config.width = null;
	//initial height
	this.config.height = null;
	//container type "div"/"iframe"/"current"
	this.config.containerType = "div";
	//content source type
	this.config.sourceType = null;
	//source to load content from
	this.config.source = null;
	//is the Pane resized due to its content
	this.config.autoContentWidth = false;
	this.config.autoContentHeight = false;
	//this option is used for disabling creation of HTML elements for the Pane.
	//Developer should manually call create method if he defines this option as true.
	this.config.onlyInit = false;
	this.config.showLoadingIndicator = true;

	// processing Widget functionality
	Zapatec.Pane.SUPERclass.init.call(this, objArgs);
	//default for containerType is "div"
	if(this.config.containerType == null){
		this.config.containerType = "div";
	}

	//caling create if enabled
	if (!this.config.onlyInit) {
		this.create(this.config.width, this.config.height);
	}
	var self = this;
	function update() {
		//if there is loader we need to update it
		if (self.loader) {
			self.loader.update();
		}
	}
	//updating loader
	this.addEventListener("fetchSourceStart", update);
	this.addEventListener("fetchSourceEnd", update);
}

/**
 * Creates all needed HTML elements, if we are not reusing parent.
 */
Zapatec.Pane.prototype.prepareHtml = function() {
	if(this.config.containerType.toLowerCase() == 'iframe'){
		// create new IFRAME element
		var iframe = document.createElement("iframe");
		iframe.src = Zapatec.zapatecPath + "pane_files/blank.html#" + this.id;
		//filling the container property
		this.container = iframe;
		iframe = null;
	} else if(this.config.containerType.toLowerCase() == 'div'){
		// create new DIV element
		this.container = document.createElement("div");
		this.contentElement = this.container;
	} else if (this.config.parent && this.config.parent.nodeType == 1) {
		this.container = this.config.parent;
		this.contentElement = this.container;
	}
	//flag to detect that this function was called
	this.prepared = true;
};
 
/**
 * Creates the HTML part of the Pane by calling prepareHtml, 
 * if it was not called before and adds the container to 
 * the document tree.
 * @param width [number] - optional, width that can be different from
 * the config's one
 * @param height [number] - optional, height that can be different from
 * the config's one
 */
Zapatec.Pane.prototype.create = function(width, height) {
	//if there was no preparation made we need to do it.
	//this is for separating operations that do depend
	//on body load from that which are indepenedent
	if (!this.prepared) {
		this.prepareHtml();
	}

	//searching Pane's parent
	if (!(this.config.parent = Zapatec.Widget.getElementById(this.config.parent))) {
		Zapatec.Log({description: "No reference to parent element."});
		return null;
	}
	//I need this kind of interface to be able to pass widget objects as
	//parent property of config.
	if (this.config.parent.requestParentFor && !(this.config.parent = this.config.parent.requestParentFor(this))) {
		Zapatec.Log({description: "No reference to parent element after request to the Parent Widget!"});
		return null;
	}

	//we can work with a container if it exists, otherwise we
	//need to reuse parent
	if (this.config.containerType.toLowerCase() == 'div' || 
	    this.config.containerType.toLowerCase() == 'iframe') {
			//appending it to the tree
			this.ready = false;
			this.config.parent.appendChild(this.container);
			//initing Pane to be ready for work
			if (this.config.containerType.toLowerCase() != 'iframe') {
				this.initPane();
			}
	} else if (this.config.containerType.toLowerCase() == 'current') {
		//reusing parent
		this.container = this.config.parent;
		this.contentElement = this.container;
		this.initPane();
	} else {
		Zapatec.Log({description: "Unknown container type: " + this.config.containerType + ". Possible values: iframe|div"})
	}
	//adding a theme className
	Zapatec.Utils.addClass(this.container, this.getClassName({prefix: "zpPane"}));
	//setting sizes
	if (width || this.config.width) {
		this.setWidth(width || this.config.width);
	}
	if (height || this.config.height) {
		this.setHeight(height || this.config.height);
	}
	this.getContainer().style.display = "block";
	this.setPaneContent();
};

/*
* \internal For containerType=iframe this function stores reference to internal
* document.body element and loads data from given source.
*/
Zapatec.Pane.prototype.initPane = function(){
	if(this.config.containerType.toLowerCase() == 'iframe'){
		var doc = null;
		//this variable determines if the Pane has the content from the same domain
		var sameDomain = true;
		//iframe's src
		var url = this.getContainer().src;
		//anchor element to make a workaround while calculating absolute URL from src
		var anchorEl = document.createElement("a");
		//is there a protocol definition in the SRC
		//cause if not than this must be the same domain
		//and we don't have to worry about anything
		var protocolSeparatorPos = url.indexOf("://");
	
		//if there is protocol definition than we must check 
		//if domain is the same as ours(I mean the page where we use the Pane)
		if (protocolSeparatorPos != -1) {
			//retreiving the domain
			var domainSeparatorPos = url.indexOf("/", protocolSeparatorPos + 3);
			var domain = url.substring(
				(protocolSeparatorPos > 0 ? protocolSeparatorPos + 3 : 0),
				(domainSeparatorPos > 0 ? domainSeparatorPos : url.length)
			);
			//checking if it is the same
			if (domain != window.location.host) {
				sameDomain = false;
			}
		}
		//if it is the same domain than we can easily work with its content
		//otherwise this.contentElement will be null and all methods
		//that are working with content will block their work
		if (sameDomain) {
			//checking if iframes document is avaliable
			if(this.container.contentDocument != null){
				doc = this.container.contentDocument;
			} else if(this.container.contentWindow && this.container.contentWindow.document != null){
				doc = this.container.contentWindow.document;
			}
	
			var self = this;
			//a workaround to get absolute url from src property
			anchorEl.href = url;
			url = anchorEl.href;
			//if iframe's document is unavaliable still 
			//fire this function again with some timeout	
			if (doc == null || doc.body == null || (Zapatec.is_gecko && url != this.container.contentWindow.location.href)) {
				setTimeout(function(){self.initPane()}, 50);
				return false;
			}
			// store reference to iframe's document
			this.iframeDocument = doc;
			this.contentElement = doc.body;
			if (typeof this.container.contentWindow.Zapatec != "object" && typeof this.container.contentWindow.Zapatec != "function") {
				this.container.contentWindow.Zapatec = {};
				this.container.contentWindow.Zapatec.windowLoaded = typeof(doc.readyState) != 'undefined' ?
					(
						doc.readyState == 'loaded' || // Konqueror
						doc.readyState == 'complete' // IE/Opera
					) :
					// Mozilla
					doc.getElementsByTagName != null && typeof(doc.getElementsByTagName('body')[0]) != 'undefined'
				;
				
				Zapatec.Utils.addEvent(this.container.contentWindow, "load", function() {self.container.contentWindow.Zapatec.windowLoaded = true;});
			}
			if (!this.container.contentWindow.Zapatec || !this.container.contentWindow.Zapatec.windowLoaded) {
				setTimeout(function(){self.initPane()}, 50);
				return false;
			}

			doc = null;
		}
	}

	//for now Pane has overflow "auto"
	//TODO: change this to be a config option
	this.getContainer().style.overflow = "auto";
	this.ready = true;
	//firing the ready event
	this.fireEvent("onReady", this);
	//hiding loader
	this.hideIndicator();
	//pointing the end of loading
	this.loading = false;
	//TODO: move this functionality to EventDriven or any other place
	this.removeEvent("onReady");
}

/*
* Returns reference to data container - do not use this element to resize it or
* do any other DOM changes!
*/
Zapatec.Pane.prototype.getContainer = function(){
	return this.container;
}

/*
* Returns reference to IFRAME's document object.
*/
Zapatec.Pane.prototype.getIframeDocument = function(){
	return this.iframeDocument;
}

/*
* Returns the element which represents the content
*/
Zapatec.Pane.prototype.getContentElement = function() {
	return this.contentElement;
}

/*
* Returns true if iframe was loaded succesfully
*/
Zapatec.Pane.prototype.isReady = function(){
	return this.ready;
}


/**
* Loads data from the HTML source.
* \param objSource [object] source HTMLElement object.
*/
Zapatec.Pane.prototype.loadDataJson = function(objSource){
	return objSource != null ? this.setContent(objSource.content) : null;
}

/**
* Sets the content of the pane.
* @param content [string or object] - string content or reference to DOM element
* @return [boolean] - true if successfull, otherwise false.
*/
Zapatec.Pane.prototype.setContent = function(content){
	if(!this.isReady()){
		// this can happen when containerType=iframe but it is not created yet.
		var self = this;
		setTimeout(function(){self.setContent(content)}, 50);
		return null;
	}
	//if there is no contentElement than this is an iframe with src from
	//another domain and we can not work with it
	this.loading = false;
	if (!this.getContentElement()) {
		this.hideIndicator();
		return false;
	}
	//no content no action :)
	if(content === null){
		this.hideIndicator();
		return null;
	} else {
		//if this is not iframe and we have auto sizes we need to allow the
		//content to be visible
		if (this.config.containerType.toLowerCase() != "iframe") {
			//saving old overflow
			var oldOverflow = this.getContainer().style.overflow;
			//setting needed sizes to "auto"
			if (this.config.autoContentWidth) {
				//setting overflow visible
				this.getContainer().style.overflow = "visible";
				this.getContainer().style.width = "auto";
			}
			if (this.config.autoContentHeight) {
				//setting overflow visible
				this.getContainer().style.overflow = "visible";
				this.getContainer().style.height = "auto";
			}
		}
		if(typeof(content) == 'string'){
			//setting new content if it is string
			Zapatec.Transport.setInnerHtml({container : this.getContentElement(), html : content});
		} else {
			try{
				//this is temporary fix for IE as it can not appendChild in iframe
				//when node was created not in iframe document
				if ((Zapatec.is_ie || Zapatec.is_opera) && this.config.containerType.toLowerCase() == "iframe") {
					Zapatec.Transport.setInnerHtml({container : this.getContentElement(), html : content.outerHTML});
				} else {
		        	// If content is not already added to container
		        	if (content.parentNode != this.getContentElement()) {
		        		//empty the element
		        		this.getContentElement().innerHTML = "";
		        		this.getContentElement().appendChild(content);
		        	}
		        }
			} catch(ex){
				this.hideIndicator();
				return null;
			}
		}
		//if this is not an iframe we get new sizes and set them as the size of Pane
		if (this.config.containerType.toLowerCase() != "iframe") {
			var newWidth = this.getWidth();
			var newHeight = this.getHeight();
		} else {
			//if this is iframe we are using scrollWidth and scrollHeight to resize iframe.
			//FIXME: This is not working correctly so needed to be fixed in new version
			var newWidth = this.getContentElement().scrollWidth + 5;
			var newHeight = this.getContentElement().scrollHeight + 5;
		}
		//restoreing overflow
		if (typeof oldOverflow != "undefined") this.getContainer().style.overflow = oldOverflow;
		//setting new sizes
		if (this.config.autoContentWidth) {
			this.setWidth(newWidth);
		}
		if (this.config.autoContentHeight) {
			this.setHeight(newHeight);
		}
	}
	//calling listeners for contentLoad event
	//use this to resize your widget which uses auto sizes feature of Pane
	this.fireEvent("contentLoaded", this);
	this.hideIndicator();
	return true;
}

/**
* Loads data from the HTML|Xml fragment source.
* \param strSource [string] source HTML fragment.
*/
Zapatec.Pane.prototype.loadDataHtml = Zapatec.Pane.prototype.loadDataXml = Zapatec.Pane.prototype.setContent;

Zapatec.Pane.prototype.loadDataHtmlText = function(content) {
	this.setContent(content);
};

/*
* Set pane width
* \param width [int] - width to set.
*/
Zapatec.Pane.prototype.setWidth = function(width){
	var self = this;
	this.fireWhenReady(function() {
		self.getContainer().style.width = width + "px";
		//we do this to have the size passed be equal to offset size
		if (self.getContainer().offsetWidth != width) {
			var newWidth = width - (self.getContainer().offsetWidth - width);
			if (newWidth < 0) newWidth = 0;
			self.getContainer().style.width = newWidth + "px";
		}
	});
}

/*
* Returns pane width
*/
Zapatec.Pane.prototype.getWidth = function(){
	return this.getContainer().offsetWidth;
}

/*
* Set pane height
* \param height [int] - height to set.
*/
Zapatec.Pane.prototype.setHeight = function(height){
	var self = this;
	this.fireWhenReady(function() {
		self.getContainer().style.height = height + "px";
		//we do this to have the size passed be equal to offset size
		if (self.getContainer().offsetHeight != height) {
			var newHeight = height - (self.getContainer().offsetHeight - height);
			if (newHeight < 0) newHeight = 0;
			self.getContainer().style.height = newHeight + "px";
		}
	});
}

/*
* Returns pane height
*/
Zapatec.Pane.prototype.getHeight = function(){
	return this.getContainer().offsetHeight;
}

/*
 * Removes the border for iframe.
 */
Zapatec.Pane.prototype.removeBorder = function() {
	if (this.config.containerType.toLowerCase() != "iframe") {
		return false;
	}
	var self = this;
	this.fireWhenReady(function() {
		//trying to remove border
		if (!Zapatec.is_ie) {
			self.getContainer().style.border = "none";
		} else {
			if (self.getContentElement()) {
				self.getContentElement().style.border = "none";
			}
		}
	});
};

/* The method to cover setting of content. Simply it sets the content
 * depending on its type.
 * @param content [mixed] - value for the content
 * @param type [string] - type of content: "html", "html/text", "html/url"
 * @return [boolean] - true if successfull, otherwise false.
 */
Zapatec.Pane.prototype.setPaneContent = function(content, type) {
	//if we have no arguments passed we need to set default
	//ones from config
	if (!content && content !== "") {
		content = this.config.source;
	}
	if (!type) {
		type = this.config.sourceType;
	}
	//we need to save the content description for reloading
	this.config.source = content;
	this.config.sourceType = type;
	//if containerType == "iframe" and we are seting its content from URL, 
	//we should use its SRC attribute
	var self = this;
	// If loading indicator is enabled
	if (this.config.showLoadingIndicator) {
		this.showIndicator();
		this.loading = true;
	}
	if (this.config.containerType.toLowerCase() == "iframe" && type == "html/url") {
		this.ready = false;
		this.fireWhenReady(function() {
			//if Pane have auto sizes we need to set them
			if (self.getContentElement()) {
				try {
					var newWidth = self.getContentElement().scrollWidth;
					var newHeight = self.getContentElement().scrollHeight;
					if (self.config.autoContentWidth) {
						self.setWidth(newWidth);
					}
					if (self.config.autoContentHeight) {
						self.setHeight(newHeight);
					}
				} catch(e) {}
			}		
			self.fireEvent("contentLoaded", self);
			if (self.events["contentLoaded"]) {
				self.events["contentLoaded"].listeners = [];
			}
		});
		this.getContainer().src = content;
		setTimeout(function(){self.initPane()}, 50);
		
		return true;
	}

	if (this.config.containerType.toLowerCase() == "iframe" && this.getContainer().src.indexOf((Zapatec.zapatecPath + "pane_files/blank.html#" + this.id).replace(/\.\.\//g, "")) < 0) {
		this.ready = false;
		this.getContainer().src = Zapatec.zapatecPath + "pane_files/blank.html#" + this.id;
	}

	//otherwise we use zpwidget's possibilities
	this.loadData();
	
	return true;
};

/**
 * Shows the Pane container.
 */
Zapatec.Pane.prototype.show = function() {
	this.getContainer().style.display = "";
	if (this.loading) {
		this.showIndicator();
	}
};

/**
 * Hides the Pane container.
 */
Zapatec.Pane.prototype.hide = function() {
	this.getContainer().style.display = "none";
	if (this.loading) {
		this.hideIndicator();
	}
};

/**
 * Shows the indicater.
 * @param message {string} message to show.
 */
Zapatec.Pane.prototype.showIndicator = function(message) {
	if (Zapatec.Indicator) {
		this.hideIndicator();
		//creating loader
		if (!this.loader) {
			this.loader = new Zapatec.Indicator({
				container: this.container,
				themePath: Zapatec.zapatecPath + "../zpextra/themes/indicator/"
			});
		}
		this.loader.start(message || 'loading');
	}
};
 
/**
 * Hides the indicater.
 */
Zapatec.Pane.prototype.hideIndicator = function() {
	if (this.loader && this.loader.isActive()) {
		this.loader.stop();
	}
};

/* 
 * Fires the action when widget is ready.
 * @param func [function] - the function to be fired when the widget is ready
 */
Zapatec.Pane.prototype.fireWhenReady = function(func) {
	if (!this.isReady()) {
		this.addEventListener("onReady", func);
	} else {
		func.call(this, this);
	}
}

/**
* Destroys current pane instance removing all related HTML elements.
*/

Zapatec.Pane.prototype.destroy = function(){
	this.hideIndicator();
	this.config.parent = null;
	this.contentElement = null;
	this.iframeDocument = null;
	if(Zapatec.is_ie && this.config.containerType.toLowerCase() == 'iframe'){
		this.container.src = "javascript:void(0)";
	}

	if (this.container.outerHTML) {
		this.container.outerHTML = "";
	} else {
		Zapatec.Utils.destroy(this.container);
	}
	this.container = null;
	this.ready = false;
	this.prepared = false;
}
