if(window.controller) {
    function applyAppEvent(){
        $select_day = document.querySelectorAll('[data-action=selectDay]')
            for (i = 0; i < $select_day.length; i++) {
              if($select_day[i].className.indexOf('disabled') !== -1){

              }else{

                  $select_day[i].addEventListener('click', function() {
                      var data_day = this.getAttribute("data-day");

                      window.controller.chooseDate(data_day);
                  })
              }
            }
    }
    $select_day = document.querySelectorAll('[data-action=selectDay]')
        for (i = 0; i < $select_day.length; i++) {
          if($select_day[i].className.indexOf('disabled') !== -1){

          }else{

              $select_day[i].addEventListener('click', function() {
                  var data_day = this.getAttribute("data-day");

                  window.controller.chooseDate(data_day);
              })
          }
        }

}

