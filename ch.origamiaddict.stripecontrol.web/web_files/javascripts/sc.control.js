var jQT = $.jQTouch({
	icon: 'icon.png',
  statusBar: 'black-translucent',
  preloadImages: [
  	'javascripts/jquery.jqtouch/themes/jqt/img/back_button.png',
    'javascripts/jquery.jqtouch/themes/jqt/img/back_button_clicked.png',
    'javascripts/jquery.jqtouch/themes/jqt/img/button_clicked.png',
    'javascripts/jquery.jqtouch/themes/jqt/img/grayButton.png',
    'javascripts/jquery.jqtouch/themes/jqt/img/whiteButton.png',
    'javascripts/jquery.jqtouch/themes/jqt/img/loading.gif'
    ]
});

var sc = sc || {};

sc.global = function () {
  var __debug = true;

  // global variables
  var stripeNames = [],
    currentStripe,
    currentColor = {},
    allStripes = false;
  //var getMoods = function() {
    // json data
  //  var moodes =
  //    {
  //      '0' :['red',          'FF0000'],
  //      '1' :['green',        '00FF00'],
  //      '2' :['blue',         '0000FF'],
  //      '3' :['warm white',   'FEFFF1'],
  //      '4' :['dark orange',  'FF8C00'],
  //      '5' :['light blue',   'BFEFFF'],
  //      '6' :['white',        'FFFFFF'],
  //      'addmood' : ['new mood', 'FFFFFF']
  //    };

    //  return moodes;
    // <property name="config.stripes" type="String"
    // value="name=Default,channels=1 2 3;name=TestAlternative,channels=1
    // 2"/>
     // <property name="config.moods" type="String"
      // value="name=Default,channels=1 2
      // 3;name=TestAlternative,channels=1 2"/>

  //}

  

  

  // ////////////////////////////////////
  // UI event handlers
  // ////////////////////////////////////
  var blackoutChangeHandler = function(event){
    __debug && console.info("blackout change event");
    if ($(event.target).attr('checked')) {
      $.ajax({data: ({'action':'setBlackout', 'blackout' : 'true'})});
    } else {
      $.ajax({data: ({'action':'setBlackout', 'blackout' : 'false'})});
    }
  };

  var allStripesChangeHandler = function(event){
    __debug && console.info("all stripes change event");
    if ($(event.target).attr('checked')) {
      allStripes = true;
      $('.stripeList').attr("disabled", true);
    } else {
      allStripes = false;
      $('.stripeList').removeAttr("disabled");
    }
  };

  var selectStripeChangeHandler = function(event){
    __debug && console.info("select stripe change event");
    // _currentStripe = $('#stripe option:selected').text();
    currentStripe = $(this).text();

    if(currColor[currentStripe] != null) {
      $.farbtastic($('#colorwheel')).setColor(currColor[currentStripe]);
    }
  };  

  

  
  // ////////////////////////////////////
  // Ajax Getter Functions
  // ////////////////////////////////////
  var ajaxGetBlackout = function(){
    __debug && console.info("ajax get blackout");
    $.ajax({data : ({'action':'getBlackout'}),
            success:  ajaxCallbackGetBlackout,
            dataType: 'text'})
  };

  var ajaxGetStripesList = function(){
    __debug && console.info("ajax get stripe list");
    $.ajax({data: ({'action':'stripesList'}),
            success:  ajaxCallbackStripesList})
  };

  var ajaxGetArtNetStatus = function() {
    __debug && console.info("ajax get artnet status");
    $.ajax({data: ({'action':'artNetStatus'}),
            success:  ajaxCallbackArtNetStatus})
  };

  

  
  // ////////////////////////////////////
  // Ajax Setter Functions
  // ////////////////////////////////////
  var ajaxSetColor = function(color) {
    if(color.length == 7) {
      color = color.substring(1);
    }
    __debug && console.info("ajax set color " + color);
    currentColor[currentStripe] = color;
    $.ajax({data: ({'action' : 'setColor',
                  'stripe' : currentStripe,
                  'color' :  color,
                  'allStripes' : allStripes})
    });
  };

  

  // ////////////////////////////////////
  // Ajax Callback Functions
  // ////////////////////////////////////
  var ajaxCallbackGetBlackout = function(data){
    if(data == "true") {
      $('#blackout').attr('checked', true);
    }else if (data == "false") {
      $('#blackout').attr('checked', false);
    }
  };

  var ajaxCallbackStripesList = function(data){
	  
    buildStripeDropdown(data);

    for (var i = 0, len = data.length; i < len; i++) {
      // TODO
      // getStripeColor(data[i]);
    }
  };

  var ajaxCallbackArtNetStatus = function(data){
    $('#artNetStatus').html(data);
  };

  

  // ////////////////////////////////////
  // UI building functions
  // ////////////////////////////////////
  var buildStripeDropdown = function(data){
    var stripeDropdown = [];
    for (var i = 0, len = data.length; i < len; i++) {
      stripeNames.push(data[i]);
      var selected = '';
      // TODO: remove hack
      if(i == 0) { selected = ' selected="selected" '; }
      stripeDropdown.push('<option ' + selected + 'value="' + data[i] + '">' + data[i] + '</option>');
    }
    $('.stripeList').append(stripeDropdown.join(''));
    currentStripe = $('.stripeList').find('option:selected').val();
    
    // currentStripe = $('#stripe option:selected').text();
    $('#stripecnt').append(stripeNames.length);
  };


  var init = function() {

    __debug && console.info("sc.global.init");
    __debug && console.info("debug mode on");


    ajaxGetBlackout();
    ajaxGetStripesList();

    // $('#moodList').append('<li>red</li>');
    // checkbox blackout event
    // sc.global.$ckbBlackout.change(blackoutChangeHandler);

    $('a[href="#info"]').parent().click(ajaxGetArtNetStatus);    

    $('.allStripes').change(allStripesChangeHandler);

    $('#blackout').change(blackoutChangeHandler);

    // stripe selection dropdown
    $('.stripeList').change(selectStripeChangeHandler);

    $('#colorwheel').farbtastic({
      callback: $.throttle( 100, ajaxSetColor ),
      width: 280
    });
    
  };
  
  var getCurrentStripe = function() {
	  return currentStripe;
  };

  return {
    init : init,
    ajaxSetColor : ajaxSetColor,
    getCurrentStripe : getCurrentStripe
  };
}();

// ////////////////////////////////////
// JQuery custom functions
// ////////////////////////////////////
jQuery.fn.center = function (){
    this.css("position","absolute");
    this.css("top", ( $(window).height() - this.height() ) / 2+$(window).scrollTop() + "px");
    this.css("left", ( $(window).width() - this.width() ) / 2+$(window).scrollLeft() + "px");
    return this;
};
// Use the above function as:
// $(element).center();


// index loaded
$(function(){
  console.info("document load");
  $.ajaxSetup({
    url: '/colorpickerajaxtest',
    dataType: 'json',
    type: 'GET'
  });

  // $('#blackout').click(function(){alert('clicked')});

  sc.global.init();
  sc.mood.init();
  sc.settings.init();

  // console.info(sc.global.get_$ckbBlackout().length);
  // init();
  // setup default ajax behaviour
});

