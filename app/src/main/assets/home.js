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
        window.controller.navigateDetail(getColorArray());
    })

    var $score_container = document.getElementById('score-container');
    $score_container.addEventListener('click', function() {
        window.controller.navigateDetail(getColorArray());
    })

    var $detail_box_list = document.querySelectorAll('.detail-box-list');
    for (var i = 0; i < $detail_box_list.length; i++) {
        $detail_box_list[i].addEventListener('click',function(e){
            var selectedAttr = this.getAttribute('data-id');
            window.controller.navigateSingleDetail(parseInt(selectedAttr), getColorArray())
        })
    }
}

