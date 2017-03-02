;(function ($) {
  function AlertPlugin (element, options) {
    this.settings = $.extend({}, $.fn.AlertPlugin.defaults, options || {});
    this.element = element;
    this.init();
  };
  AlertPlugin.prototype = {
    init: function () {
      var me = this; // AlerPlugin对象
      me.selectors = me.settings.selectors;
      me.alert = me.element;
      me.cover = me.element.find(me.selectors.cover);
      me.alertMain = me.element.find(me.selectors.alertMain);
      me.alertContain = me.element.find(me.selectors.alertContain);
      me.alertText = me.element.find(me.selectors.alertText);
      me.alertBtn = me.element.find(me.selectors.alertBtn);
      me.initDom();
      me.closeAlet();
    },
    initDom: function () {
      var me = this;
      var htmlStr = me.settings.htmlText;
      me.alertMain.css({
        'width': me.settings.width,
        'height': me.settings.height,
        'margin': - me.settings.height/2 + 'px 0 0 ' + - me.settings.width/2 + 'px'
      });
      // me.alertText.css({
      //   'line-height': 
      // })
      me.alertText.html($.trim(me.settings.htmlText)); 
      if (me.settings.btnCount == 2) {
        me.alertBtn.html('<button class="btn trueBtn">确定</button><button class="btn falseBtn">取消</button>')
      } else if (me.settings.btnCount == 1) {
        // me.alertBtn.addClass('oneBtn')
      }
    },
    closeAlet: function () {
      var me = this;
      me.cover.on('click', function () {
        me.alert.css('display', 'none');
      });
      if (me.settings.btnCount == 1) {
        $('.oneBtn').on('click', function () {
          me.alert.css('display', 'none');
        });
      } else {
        $('.false').on('click', function () {
          me.alert.css('display', 'none');
        });
      }
    }
  };
  $.fn.AlertPlugin = function (options) {
    return this.each(function () {
      var me = $(this);  // <div class="alert">...
      var instance = me.data('alertplugin');
      if (!instance) {
        instance = new AlertPlugin(me, options);
      };
    });
  };
  $.fn.AlertPlugin.defaults = {
    selectors: {
      alert: '#alert',
      cover: '.cover',
      alertMain: '.alertMain',
      alertContain: '.alertContain',
      alertText: '.alertText',
      alertBtn: '.alertBtn'
    },
    width: 200,
    height: 100,
    htmlText: '<p>你好</p>',
    btnCount: 2
  };
})(jQuery);