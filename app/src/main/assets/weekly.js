///document.body.innerHTML = "body"
if(window.controller) {
function getColorArray() {
    return [document.getElementById('weekly_score_detail').getAttribute('data-color'), document.getElementById('cont_detail_1').getAttribute('data-color'), document.getElementById('cont_detail_2').getAttribute('data-color'), document.getElementById('cont_detail_3').getAttribute('data-color')].join()
}
console.log('asdfadsf');
    $calendar_pick = document.querySelector('.calendar-pick')
    $calendar_pick.addEventListener('click', function() {
        window.controller.openCalendarActivity();
    })

    var $summary = document.querySelector('.home-summary');
    $summary.addEventListener('click', function() {
        var selectedDate = document.getElementById('selected_start_date').value;
        var selectedEndDate = document.getElementById('selected_end_date').value;
        window.controller.navigateSingleDetailWeekly(1, selectedDate,selectedEndDate);
    })

    var $score_container = document.getElementById('weekly_score_detail');
    $score_container.addEventListener('click', function() {
        var selectedDate = document.getElementById('selected_start_date').value;
        var selectedEndDate = document.getElementById('selected_end_date').value;
        window.controller.navigateSingleDetailWeekly(1, selectedDate,selectedEndDate);
    })

    var $detail_box_list = document.querySelectorAll('.detail-box-list-weekly');
    for (var i = 0; i < $detail_box_list.length; i++) {
        $detail_box_list[i].addEventListener('click',function(e){
            var selectedAttr = this.getAttribute('data-id');
            var selectedDate = document.getElementById('selected_start_date').value;
            var selectedEndDate = document.getElementById('selected_end_date').value;
            window.controller.navigateSingleDetailWeekly(parseInt(selectedAttr)+1, selectedDate,selectedEndDate);
        })
    }
    /*var buttons = document.getElementsByClassName('day');
    for (var i = 0; i < buttons.length; i++) {
      if(buttons[i].className.indexOf('disabled') !== -1){return;}
        buttons[i].addEventListener('click',function(e){
            var selectedAttr = this.getAttribute('data-day');
            console.log(selectedAttr);
            showSelectedData(selectedAttr);

        }

    }*/
    initMonitoring()
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

