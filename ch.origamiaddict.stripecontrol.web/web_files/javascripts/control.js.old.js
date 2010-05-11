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

sc.custom = function () {
	
	//global variables
	var stripeNames = [],
		currentStripe,
		currentColor = {};

	//dom variable declarations	
	//var $ckbBlackout 		= $('#blackout');
	//var $colorWheel  		= $('#colorwheel');
	//var $dropdownStripeList = $('.stripeList');
	//var $stripecnt	 		= $('#stripecnt');
	
	//////////////////////////////////////
	// UI event handlers
	//////////////////////////////////////				
	var blackoutChangeHandler = function(event){
		console.info("change");
		if ($(event.target).attr('checked')) {
			$.ajax({data: ({'action':'setBlackout', 'blackout' : 'true'})});	
		} else {
			$.ajax({data: ({'action':'setBlackout', 'blackout' : 'false'})});
		} 
	};

	var selectStripeChangeHandler = function(event){		
		//_currentStripe = $('#stripe option:selected').text();
		currentStripe = $(event.target).children("[@selected]").text();

		if(currColor[currentStripe] != null) {
			$.farbtastic($('#colorwheel')).setColor(currColor[currentStripe]);
		}
	};
	
	//////////////////////////////////////
	// Ajax Getter Functions
	//////////////////////////////////////	
	var ajaxGetBlackout = function(){
		$.ajax({data : ({'action':'getBlackout'}),
						success:  ajaxCallbackGetBlackout,
						dataType: 'text'})
	};

	var ajaxGetStripesList = function(){
		$.ajax({data: ({'action':'stripesList'}), 
				success:	ajaxCallbackStripesList})
	};
	
	//////////////////////////////////////
	// Ajax Setter Functions
	//////////////////////////////////////		
	var ajaxSetColor = function(color){ 
		//DEBUG: console.info(color);		   	
			currentColor[currentStripe] = color;		
			$.ajax({data: ({'action' : 'setColor', 
							'stripe' : currentStripe, 
							'color' :  color.substring(1)})
					});
		};
	
	//////////////////////////////////////
	// Ajax Callback Functions
	//////////////////////////////////////							
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
			//getStripeColor(data[i]);
		}
	};
	
	//////////////////////////////////////
	//UI building functions
	//////////////////////////////////////							
	var buildStripeDropdown = function(data){
		
		var stripeDropdown = [];
		for (var i = 0, len = data.length; i < len; i++) {
			
			stripeNames.push(data[i]);
			
			var selected = '';
			//TODO: remove hack
			if(i == 0) {
				selected = ' selected="selected" ';
			}
			stripeDropdown.push('<option ' + selected + 'value="' + data[i] + '">' + data[i] + '</option>');
		}
		
		$('.stripeList').append(stripeDropdown.join(''));
		currentStripe = $('.stripeList').find('option:selected').val();
	 	//currentStripe = $('#stripe option:selected').text();
	 	
		$('#stripecnt').append(stripeNames.length);
	 	
	};
	
	var setupColorMoods = function() {
		//dynamic read-in later
		var moodes = {
			'red' : 'FF0000',
			'green' : '00FF00',
			'blue' : '0000FF',			
			'warm white' : 'FEFFF1',			
			'dark orange' : 'FF8C00',
			'light blue'	: 'BFEFFF',
			'white' : 'FFFFFF'
		};
					
		for( var color in moodes ){					
			$('#moodList').append(
				$('<li></li>').addClass('deletethumb').css({'background' : '#'+moodes[color]}).append(color)
				.append($('<small id="del_' + color + '"></small>'))		
			);			
		}		
	};
	
	var init = function(){
		
		console.info("sc.custom.init");
		
		ajaxGetBlackout();
		ajaxGetStripesList();
		
		setupColorMoods();
		//$('#moodList').append('<li>red</li>');
		//checkbox blackout event
		//sc.global.$ckbBlackout.change(blackoutChangeHandler);
		
		$('#blackout').change(blackoutChangeHandler);
		//stripe selection dropdown
		$('.stripeList').change(selectStripeChangeHandler);
		
		$('#colorwheel').farbtastic({ 
			callback: $.throttle( 100, ajaxSetColor ),
			width: 280				
		});
	};

	return {
		init : init
	};
}();



//////////////////////////////////////
// JQuery custom functions
//////////////////////////////////////
jQuery.fn.center = function (){
    this.css("position","absolute");
    this.css("top", ( $(window).height() - this.height() ) / 2+$(window).scrollTop() + "px");
    this.css("left", ( $(window).width() - this.width() ) / 2+$(window).scrollLeft() + "px");
    return this;
};		
//Use the above function as:
//$(element).center();
	
	
//index loaded		
$(function(){	
	console.info("document load");
	$.ajaxSetup({
		url: '/colorpickerajaxtest',
		dataType: 'json',
		type: 'GET'
	});

	//$('#blackout').click(function(){alert('clicked')});	
	
	sc.custom.init();
	
	$(".deletethumb").click(function(e) {
		console.info("clicked");
		e.preventDefault();
		//var imageId = $(this).parent().attr("id");
		var imageId = $(this).attr('id');
		console.info("clicked delete" + imageId);
        // remove image based on ID.
	});
	
	//console.info(sc.global.get_$ckbBlackout().length);
	//init();
	//setup default ajax behaviour
});

