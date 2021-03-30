///document.body.innerHTML = "body"
if(window.controller) {
function getColorArray() {
    return [document.getElementById('score-container').getAttribute('data-color'), document.getElementById('cont_detail_1').getAttribute('data-color'), document.getElementById('cont_detail_2').getAttribute('data-color'), document.getElementById('cont_detail_3').getAttribute('data-color')].join()
}
    $calendar_pick = document.querySelector('.calendar-pick')
    $calendar_pick.addEventListener('click', function() {
        window.controller.openCalendarActivity();
    })

    var $summary = document.getElementById('summary');
    $summary.addEventListener('click', function() {
        var selectedDate = document.getElementById('selected_date').value;
        window.controller.navigateSingleDetail(1, selectedDate);
    })

    var $score_container = document.getElementById('score-container');
    $score_container.addEventListener('click', function() {
        var selectedDate = document.getElementById('selected_date').value;
        window.controller.navigateSingleDetail(1, selectedDate);
    })

    var $detail_box_list = document.querySelectorAll('.detail-box-list');
    for (var i = 0; i < $detail_box_list.length; i++) {
        $detail_box_list[i].addEventListener('click',function(e){
            var selectedAttr = this.getAttribute('data-id');
            var selectedDate = document.getElementById('selected_date').value;
            window.controller.navigateSingleDetail(parseInt(selectedAttr)+1, selectedDate);
        })
    }

    initMonitoring();
    try {
      $close_birdie = document.querySelector('.btn-close-notif');
          $close_birdie.addEventListener('click', function() {
          var inotif_nickname = document.getElementById('notif_nickname').innerHTML;
          console.log(inotif_nickname);
              window.controller.closeNotifBirdie(inotif_nickname);
          })
    }
    catch(err) {
    }

}

