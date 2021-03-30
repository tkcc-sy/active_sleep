if(window.controller) {
    var $close_popup = document.querySelectorAll('.btn-close-popup');

    for (i = 0; i < $close_popup.length; i++) {
     $close_popup[i].addEventListener('click',function(e) {
        window.controller.closeDetail();
     })
    }

    function loadDetail(attr, color) {
        scrollToTop()
        initChartStatus(color.split(','))
        scrollToChart(parseInt(attr) + 1)
    }

    function scrollToTop() {
         var el = document.querySelector(".scrollable")
         console.log(el)
            el.scrollTo(0,0)
    }

}

