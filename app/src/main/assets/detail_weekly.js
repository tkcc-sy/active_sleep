if(window.controller) {
    var $close_popup = document.querySelectorAll('.btn-close-popup');

    for (i = 0; i < $close_popup.length; i++) {
     $close_popup[i].addEventListener('click',function(e) {
        window.controller.closeDetailWeekly();
     })
    }

    function loadDetail(attr, color) {
        initChartStatus(color.split(','))
        scrollToChart(parseInt(attr) + 1)
    }



}

