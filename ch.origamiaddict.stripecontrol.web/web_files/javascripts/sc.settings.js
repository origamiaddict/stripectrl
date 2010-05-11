var sc = sc || {};

sc.settings = function() {
	/***************************************************************************
	 * Declarations
	 **************************************************************************/
	var __debug = true;

	/***************************************************************************
	 * UI functions
	 **************************************************************************/
	var buildModuleDropdown = function() {

		function dropdown(modules) {
			var moduleDropdown = [];
			for ( var i = 0; i < modules.length; i++) {
				moduleDropdown.push('<option value="'+$(modules[i]).attr('href')+'">'+modules[i].innerHTML+'</option>');
			}
			$('#def_module').append($('<select></select>').html(moduleDropdown.join('')));
		}

		dropdown($('#modulesList > li').children().toArray());

		/*
		 * $('#modulesList').children().each(function() { moduleDropdown.push(
		 * $('<option></option>')
		 * .attr('value',$(this).find("a").attr('href')) .text($(this).text()) );
		 * 
		 * });
		 */
		// $('#def_module').html($('<select></select>').append(moduleDropdown.toArray().join('')));
	};

	// TODO: rework
	var generateChannelSelectionDropdown = function(id, excludeChannels,
			selValue) {
		var channelsDropdown = [];
		var excludeCount = 0;
		channelsDropdown.push('<option value="">-</option>');
		for ( var i = 1, len = 255; i < len; i++) {
			var selected = '';
			if (selValue == null && excludeCount == excludeChannels.length) {
				selected = ' selected="selected" ';
			} else if (i == selValue) {
				selected = ' selected="selected" ';
			}
			if (excludeChannels != null) {
				if (jQuery.inArray(i, excludeChannels) != -1 && i != selValue) {
					excludeCount++;
				} else {
					channelsDropdown.push('<option ' + selected + 'value="' + i
							+ '">' + i + '</option>');
				}
			} else {
				channelsDropdown.push('<option ' + selected + 'value="' + i
						+ '">' + i + '</option>');
			}
		}
		return '<select name="' + id + '">' + channelsDropdown.join('')
				+ '</select>';
	};

	/***************************************************************************
	 * Data handler functions
	 **************************************************************************/

	/***************************************************************************
	 * UI event handler functions
	 **************************************************************************/
	var enterStripeSettingsHandler = function(event) {
		ajaxGetStripeChannels(sc.global.getCurrentStripe());
		$('#stripeName').val(sc.global.getCurrentStripe());
	};

	// TODO set "current" stripe to correct value
	var settingsStripeListChangeHandler = function(event) {
		__debug
				&& console.info("setting stripe list change "
						+ $('#settingsStripeList option:selected').text());
		ajaxGetStripeChannels($('#settingsStripeList option:selected').text());
		$('#stripeName').val($('#settingsStripeList option:selected').text());
	};

	/***************************************************************************
	 * AJAX call functions
	 **************************************************************************/
	var ajaxGetStripeChannels = function(stripeName) {
		__debug
				&& console.info('ajax get stripe channels for :'+stripeName);
		$.ajax( {
			data : ( {
				'action' : 'channelsList',
				'stripe' : stripeName
			}),
			success : ajaxCallbackGetStripeChannels
		});
	};

	var ajaxSetStripeChannels = function(channelValues) {
		__debug
				&& console.info("setchannels: " + sc.global.getCurrentStripe()
						+ " values: " + channelValues);

		$.ajax( {
			data : ( {
				'action' : 'setChannels',
				'stripe' : sc.global.getCurrentStripe(),
				'channels' : channelValues
			}),
			success : ajaxCallbackSetStripeChannels
		});
	};

	/***************************************************************************
	 * AJAX callback functions
	 **************************************************************************/
	// TODO: rework
	var ajaxCallbackGetStripeChannels = function(data) {
		var selChannel = '';
		// reset channels
		_channels = [];
		var i;

		for (i = 0, len = data.length; i < len; i++) {
			_channels.push(generateChannelSelectionDropdown("ch_" + i, data,
					data[i]));
		}
		_lastChannelIndex = i;

		$('#channelList').html(_channels.join(''));

		$('select[name*="ch_"]').focus(function() {
			selChannel = $(this).val();
		});

		$('select[name*="ch_"]').change(function() {
			if ($(this).val() != selChannel) {
				var channelValues = [];
				$('select[name*="ch_"]').each(function() {
					if ($(this).val() != '') {
						channelValues.push($(this).val());
					}
				});
				ajaxSetStripeChannels(channelValues);
			}
		});
	};

	var ajaxCallbackSetStripeChannels = function() {
		ajaxGetStripeChannels(sc.global.getCurrentStripe());
	};

	var init = function() {
		__debug && console.info("sc.settings.init");

		buildModuleDropdown();

		// build list when entering stripe menu
		$('a[href=#settings_stripes]').click(enterStripeSettingsHandler);

		// dropdown change funtions for stripe settings
		$('#settingsStripeList').change(settingsStripeListChangeHandler);

	};

	return {
		init : init
	};
}();
