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
	
	var __debug = true;
	
	// global variables
	var stripeNames = [],
		currentStripe,
		currentColor = {},
		allStripes = false,
		moodState,
		moods = [];
		
	// state enums
	var eMoodState = {select:0, edit:1};
	
	var editMoodColor = null;

	// ////////////////////////////////////
	// utility functions
	// ////////////////////////////////////
	var rgb2hex = function(rgb) {
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		function hex(x) {
			var hex = parseInt(x).toString(16);
			if(hex == '0') hex = '00';
			return hex;
		}
    	// return "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
		return '' + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
	};


	var buildEditMoodScreen = function(moodIndex) {
		var screenHtml = '<div class="toolbar">	\
				<h1>Edit Mood</h1> \
				<a class="button leftButton back" href="#">Cancel</a> \
			</div> \
			<div class="colorpicker"> \
				<div id="colormoodwheel"></div> \
			</div> \
			<ul class="edit rounded"> \
				<li><input type="text" placeholder="Mood Name" name="Mood Name" id="moodEditName" /></li> \
			</ul>';
		
		$('#moodEditScreen').html(screenHtml);
		// bug here
		$('#moodEditScreen').append($('<a></a>').html('Save').addClass('whiteButton submit').attr('href','#').click(
				function(event) {
					// TODO: delete this mood and return to previous screen
					// and update mood screen
					saveMood(moodIndex, $('#moodEditName').val());
					buildColorMoods();
					removeEditMoodScreen();
					jQT.goBack('#mood');
				}
		));
		
		if(moodIndex != null && moodIndex != 'addmood') {
			// add delete button
			$('#moodEditScreen > a').after($('<a></a>').html('Delete').addClass('whiteButton').attr('href','#').click(
				function(event) {
					// TODO: delete this mood and return to previous screen
					// and update mood screen
					__debug && console.info("delete mood clicked...");
					deleteMood(moodIndex);
					buildColorMoods();
					jQT.goBack('#mood');
					removeEditMoodScreen();
				}
			));
		}
		
		$('#moodEditScreen').click(function(event){
				// global mood edit screen click handler
			
			switch(event.target.id) {
				case 'saveMood':
					saveMood(moodIndex, $('#moodEditName').val());
					jQT.goBack('#mood');
					removeEditMoodScreen();
					break;
			}
				
				// TODO: save mood and return to previous screen
				// and update mood screen
		});
				
		// setup event handlers etc...
		// $('#colormoodwheel').farbtastic({
		// callback: $.throttle( 100, editMoodSetColorHandler ),
		// width: 280
		// });
		

		$.farbtastic('#colormoodwheel', { 
			callback: $.throttle( 100, editMoodSetColorHandler ),
			width: 280				
		}).setColor("#" + getMoodColor(moodIndex));
		
		__debug && console.info("edit mood screen built: " + moodIndex);
		
	};
	
	var removeEditMoodScreen = function() {
		$('#moodEditScreen').empty();
	};

	var saveMood = function(index, name) {
		// todo change to "real function"
		//and reload data
		// moodes.index = [name, editMoodColor];
		__debug && console.info('save mood index: ' + index, name);
	};
	
	var deleteMood = function(index) {
		// todo change to "real function"
		//and reload data
		__debug && console.info('delete mood index: ' + index);
						
	};
	
	//var getMoods = function() {
		// json data
	//	var moodes = 
	//		{
	//			'0' :['red',					'FF0000'],			
	//			'1' :['green',				'00FF00'],
	//			'2' :['blue', 				'0000FF'],			
	//			'3' :['warm white', 	'FEFFF1'],		
	//			'4' :['dark orange',	'FF8C00'],
	//			'5' :['light blue', 	'BFEFFF'],
	//			'6'	:['white', 				'FFFFFF'],
	//			'addmood' : ['new mood', 'FFFFFF']
	//		};
	
		//	return moodes;
		// <property name="config.stripes" type="String"
		// value="name=Default,channels=1 2 3;name=TestAlternative,channels=1
		// 2"/>
		 // <property name="config.moods" type="String"
			// value="name=Default,channels=1 2
			// 3;name=TestAlternative,channels=1 2"/>
		
	//}
	
	var getMoodColor = function(moodIndex) {
		return (moods !== null ? moods[moodIndex][1] : 'FFFFFF');
	};
	
	// TODO: rework
	var generateChannelSelectionDropdown = function(id, excludeChannels, selValue) {
			var channelsDropdown = [];
			var excludeCount = 0;
			channelsDropdown.push('<option value="">-</option>');					
			for (var i = 1, len = 255 ; i < len; i++) {
				var selected = '';
				if(selValue == null && excludeCount == excludeChannels.length) {
					selected = ' selected="selected" ';
				} else if(i == selValue) {
						selected = ' selected="selected" ';							
				}
				if(excludeChannels != null) {
					if(jQuery.inArray(i,excludeChannels) != -1 && i != selValue) {
						excludeCount++;	
					} else {
						channelsDropdown.push('<option ' + selected + 'value="' + i + '">' + i + '</option>');	
					}
				} else {
					channelsDropdown.push('<option ' + selected + 'value="' + i + '">' + i + '</option>');
				}			
		}
		return '<select name="' + id + '">' + channelsDropdown.join('') + '</select>';
	};

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
		currentStripe = $(event.target).children("[@selected]").text();

		if(currColor[currentStripe] != null) {
			$.farbtastic($('#colorwheel')).setColor(currColor[currentStripe]);
		}
	};
	
	// todo: complete function
	var editMoodClickHandler = function(event) {
		__debug && console.info("edit mood click event");
		event.preventDefault();
		if(moodState == eMoodState.select) {
			moodState = eMoodState.edit;		
		// if ($(event.target).html() == "Edit") {
			// enter edit mode
			$(event.target).html("Done");			
			// append edit image to moods
			//$('#moodList').children().append($('<small></small>'));					
			$('#moodList').children().addClass($('arrow'));
			// add empty field for new color
			$('#moodList').append($('<li></li>').addClass('mood').attr('id','addmood').append("+").click(moodClickHandler));					
		} else if(moodState == eMoodState.edit) {
			moodState = eMoodState.select;		
			// exit edit mode
			$(event.target).html("Edit");			
			//$('#moodList > li > small').remove();
			$('#moodList > li').removeClass('arrow');
			// remove new field
			// remove delete handler
			// $('#moodList').remove($('<li></li>').addClass('mood').append("+"));
			$('#addmood').remove();
		} else {
			__debug && console.info("unknown mood state");
		}
	};
	
	var moodClickHandler = (function(e) {		
		
		e.preventDefault();		
				
		var moodIndex = null;
		var moodId = $(this).attr('id');
		
		console.info('moodid$:' + moodId);
		
		if(moodId !== 'addmood') {
			moodIndex = $(this).attr('id').match(/\d+$/)[0];
			console.info('moodidndex:' + moodIndex);
		} else {
			moodIndex = 'addmood';
		}
				
		switch(moodState) {
			case eMoodState.edit:
				// add, edit or remove mood
				// TODO: goto screen to do it
				
				// if existing, add delete button to screen
				buildEditMoodScreen(moodIndex);
				
				jQT.goTo($('#moodEditScreen'), 'flip');
				break;
			case eMoodState.select:
				// select mood
				var color = rgb2hex($('#'+moodId).css('background-color'))
				__debug && console.info("set mood color: " + color);
				ajaxSetColor(color);
				break;
			default:
				__debug && console.info("unknown mood state in click handler");
				break;
		}
				
	});
	
	var editMoodSetColorHandler = function(color) {
		// todo save mood color, maybe just call the set color lateron...
		// maybe project the color directly to allow better selection
		editMoodColor = color;
		__debug && console.info ("setting mood color");
	};
	
	var enterStripeSettingsHandler = function(event) {
			getChannels(currentStripe);
			$('#stripeName').val(currentStripe);			
	};
	
	var settingsStripeListChangeHandler = function(event) {		
		__debug && console.info("setting stripe list change " + $('#settingsStripeList option:selected').text());
		ajaxGetStripeChannels($('#settingsStripeList option:selected').text());
		$('#stripeName').val($('#settingsStripeList option:selected').text());
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
						success:	ajaxCallbackStripesList})
	};
	
	var ajaxGetArtNetStatus = function() {
		__debug && console.info("ajax get artnet status");
		$.ajax({data: ({'action':'artNetStatus'}), 
						success:	ajaxCallbackArtNetStatus})			
	};
	
	var ajaxGetStripeChannels = function(stripeName) {
		$.ajax({data: ({'action':'channelsList', 'stripe' : stripeName}), 
						success:	ajaxCallbackGetStripeChannels})				
	};
	
	var ajaxGetMoods = function() {
		__debug && console.info("ajax get moods");
		$.ajax({data: ({'action':'getMoods'}), 
						success:	ajaxCallbackGetMoods});
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
	
	var ajaxSetChannelValues = function(channel, channelValues) {
		__debug && console.info("setchannels: " + currentStripe + " values: " + channelValues);
				
		$.ajax({data: ({'action' : 'setChannels', 
									'stripe' : currentStripe, 
									'channels' :  channelValues})
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
	
	// TODO: rework
	var ajaxCallbackGetStripeChannels = function(data){
		var selChannel = '';
		// reset channels
		_channels = [];
		var i;								
		
		for ( i = 0, len = data.length; i < len; i++) {
			_channels.push(generateChannelSelectionDropdown("ch_"+i, data, data[i]) ) ;
		}
		_lastChannelIndex = i;
		
		$('#channelList').html(_channels.join(''));		

		$('select[name*="ch_"]').focus( function() {
			selChannel = $(this).val();
		});		

		$('select[name*="ch_"]').change( function() { 
			if($(this).val() != selChannel) {
				var channelValues = [];
				$('select[name*="ch_"]').each( function() {
					if($(this).val() != '') {
						channelValues.push($(this).val());
					}
				});
				ajaxSetStripeChannels(currentStripe, channelValues);
				ajaxGetStripeChannels(currentStripe);							
			}
		});
	};
	
	var ajaxCallbackGetMoods = function(data) {
		//console.info("moods" + data);
		for(idx in data) {
			moods[idx] = [data[idx].name , data[idx].value];
		}
		buildColorMoods();
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
	
	var buildColorMoods = function() {
		// dynamic read-in later
		//var moods = getMoods();
		
		$('#moodList').empty();
					
		for( var cidx in moods ){				
			$('#moodList').append(
				$('<li></li>').addClass('mood').attr('id', 'moodcolor_'+cidx).css({'background' : '#'+moods[cidx][1]}).append(moods[cidx][0])				
				// $('<li></li>').addClass('deletethumb').attr('id',
				// 'moodcolor_'+cidx).css({'background' :
				// '#'+moodes[cidx][1]}).append(moodes[cidx][0])
				// .append($('<small id="del_' + color + '"></small>'))
			);						
		}		
	};
	
	var init = function() {
		
		__debug && console.info("sc.custom.init");
		__debug && console.info("debug mode on");
		
				
		ajaxGetBlackout();
		ajaxGetStripesList();
		ajaxGetMoods();
		
		buildColorMoods();
		// $('#moodList').append('<li>red</li>');
		// checkbox blackout event
		// sc.global.$ckbBlackout.change(blackoutChangeHandler);
		
		$('a[href="#info"]').parent().click(ajaxGetArtNetStatus);
		
		// mood screen setup
		moodState = eMoodState.select;
		$('#editMoods').click(editMoodClickHandler);		
		$('.mood').click(moodClickHandler);		
		
		$('.allStripes').change(allStripesChangeHandler);
		
		$('#blackout').change(blackoutChangeHandler);
		
		// stripe selection dropdown
		$('.stripeList').change(selectStripeChangeHandler);
		
		$('#colorwheel').farbtastic({ 
			callback: $.throttle( 100, ajaxSetColor ),
			width: 280				
		});
				
		// build list when entering stripe menu
		$('a[href=#settings_stripes]').click(enterStripeSettingsHandler);
		
		// dropdown change funtions for stripe settings
		$('#settingsStripeList').change(settingsStripeListChangeHandler);	
		
	};

	return {
		init : init
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
	
	sc.custom.init();
			
	// console.info(sc.global.get_$ckbBlackout().length);
	// init();
	// setup default ajax behaviour
});

