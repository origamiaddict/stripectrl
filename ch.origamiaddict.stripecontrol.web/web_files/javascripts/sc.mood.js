var sc = sc || {};

sc.util = function() {
  var rgb2hex = function(rgb) {
    rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
    function hex(x) {
      var hex = parseInt(x).toString(16);
      if(hex.size == 1) hex = '0' + hex;
      return hex;
    }      
    return '' + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
  };
  
  return {
    rgb2hex : rgb2hex     
  };
}();

sc.mood = function () { 
  /******************************
  * Declarations
  *******************************/  
  var __mock = true,
      __debug = true,   
      moodState = null,
      moods = [],
      eMoodState = {select:0, edit:1},
      eUpdateAction = {save:0, delete:2},
      editMoodCurrSelColor = null;

  /******************************
  * UI functions
  *******************************/        
  var setupEditMoodScreen = function(moodIndex) {        
    if(moodIndex != null && moodIndex != 'addmood') {
      $('#moodEditScreen').append(
        $('<a></a>')
        .html('Delete')
        .addClass('whiteButton')
        .attr({href : '#',
               id : 'deleteMood' })
        .click(
          function(event) {
            __debug && console.info("delete mood click event...");
            ajaxDeleteMood(moodIndex);          
            jQT.goBack('#mood');         
          }
        ));
      $('#moodEditName').val(moods[moodIndex][0]);
    }
    $('#moodEditScreen').click(
      function(event) {        
        switch(event.target.id) {
          case 'saveMood':
            __debug && console.info("save mood click event...");
            ajaxSaveMood(moodIndex, $('#moodEditName').val());            
            jQT.goBack('#mood');
            break;
        }        
      }
    );
    $.farbtastic('#colormoodwheel', {
      callback: $.throttle( 100, editMoodSetColorHandler ),
      width: 280
    }).setColor("#" + getMoodColor(moodIndex));

    __debug && console.info("edit mood screen built for mood (id): " + moodIndex);
  };

  var resetEditMoodScreen = function() {
    console.info("reset called...");
    $('#deleteMood').remove();
  };
  
  var buildColorMoods = function() {    
    for( var cidx in moods ){     
      $('#moodList').append(
        $('<li></li>')
        .addClass('mood')
        .attr('id', 'moodcolor_'+cidx)
        .css({'background' : '#'+moods[cidx][1]})
        .html(moods[cidx][0]));
    }
  };
    
  var updateColorMoodDOM = function(index, action) {
        var moodElemId = 'moodcolor_' + index;
    
        switch(action) {
          case eUpdateAction.save:            
            if($('#'+moodElemId).length) { 
              //check if element exists (update)  
              $('#'+moodElemId).css({'background' : '#'+moods[index][1]})
              .html(moods[index][0]);
            } else {
              //add
              $('#moodList').append(
              $('<li></li>')
              .addClass('mood')
              .attr('id', moodElemId)
              .css({'background' : '#'+moods[index][1]})
              .html(moods[index][0]));              
            }           
            break;        
          case eUpdateAction.delete:
            $('#'+moodElemId).remove();
            break;                    
        }   
  };
  /******************************
  * Data handler functions
  *******************************/    
  var getMoodColor = function(moodIndex) {
    return (moods !== null ? moods[moodIndex][1] : 'FFFFFF');
  };

  /******************************
  * UI event handler functions
  *******************************/  
  var editMoodClickHandler = function(event) {
    __debug && console.info("edit mood click event");
    event.preventDefault();
    if(moodState == eMoodState.select) {
      moodState = eMoodState.edit;
      $(event.target).html("Done");
      //$('#moodList').children().append($('<small></small>'));
      $('#moodList > li').addClass('forward');      
      $('#moodList').append(
        $('<li></li>')
        .addClass('mood')
        .attr('id','addmood')
        .append("+")
        .click(moodClickHandler));
    } else if (moodState == eMoodState.edit) {
      moodState = eMoodState.select;
      $(event.target).html("Edit");
      $('#moodList > li').removeClass('forward');
      $('#addmood').remove();
    } else {
      __debug && console.info("ERROR: unknown mood state");
    }
  };

  var moodClickHandler = (function(event) {
    __debug && console.info('mood click event for mood id: ' + moodId);
    event.preventDefault();
    var moodIndex = null;
    var moodId = $(this).attr('id');
    
    if(moodId !== 'addmood') {
      moodIndex = $(this).attr('id').match(/\d+$/)[0];      
    } else {
      moodIndex = -1;
    }

    switch(moodState) {
      case eMoodState.edit:
        setupEditMoodScreen(moodIndex);
        jQT.goTo($('#moodEditScreen'), 'flip');
        break;
      case eMoodState.select:
        var color = sc.util.rgb2hex($('#'+moodId).css('background-color'));        
        sc.global.ajaxSetColor(color);
        break;
      default:
        __debug && console.info("unknown mood state in click handler");
        break;
    }
  });

  var editMoodSetColorHandler = function(color) {
    editMoodCurrSelColor = color;    
  };

  /******************************
  * AJAX call functions
  *******************************/    
  var ajaxGetMoods = function() {
    __debug && console.info("ajax get moods");
    
    if(__mock) {
      var mockmoods = {"0" : {'name' : 'red', 'value' : 'FF0000'},
                    "1" : {'name' : 'blue','value' : '0000FF'}};
      ajaxCallbackGetMoods(mockmoods);
      return;
    }
    
    $.ajax({data: ({'action':'getMoods'}),
            success:  ajaxCallbackGetMoods});
  };
  
  var ajaxSaveMood = function(index, name) {
    __debug && console.info('ajax call save mood index: ' + index + ' name: ' + name);
    $.ajax({data: ({'action':'saveMood', 'index' : index, 'name' : name}),
            success:  ajaxCallbackSaveMood});
  };

  var ajaxDeleteMood = function(index) {
    __debug && console.info('ajax call delete mood index: ' + index);   
    $.ajax({data: ({'action':'deleteMood', 'index' : index}),
            success: ajaxCallbackDeleteMood});
  };
  /******************************
  * AJAX callback functions
  *******************************/  
  var ajaxCallbackGetMoods = function(data) {   
    for(idx in data) {
      moods[idx] = [data[idx].name , data[idx].value];
    }
    buildColorMoods();
  };
  
  var ajaxCallbackSaveMood = function(data) {
    updateColorMoodDOM(index, eUpdateAction.save);
    
  };
  
  var ajaxCallbackDeleteMood = function(data) {
    updateColorMoodDOM(index, eUpdateAction.delete);    
  };
                  
  var init = function() {
    __debug && console.info("sc.mood.init");
            
    ajaxGetMoods();    
    // mood screen setup
    moodState = eMoodState.select;
    
    //add mood link to main modules
    $('#modules').append('<li class="arrow"><a href="#mood">Color Mood</a></li>');
    
    $('#editMoods').click(editMoodClickHandler);
    $('.mood').click(moodClickHandler);
  };


  /******************************
  * Public functions and variables
  *******************************/ 
  return {
    init : init
  };
}();