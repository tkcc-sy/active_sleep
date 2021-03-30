///document.body.innerHTML = "body"
if(window.controller) {
    /*var $summary = document.getElementById('list-news');
    $summary.addEventListener('click', function() {
            id = $(this).data('id');
            window.controller.openDetail(id);
    })*/

    $('.list-news').on('click', function(){
        id = $(this).data('id');
        window.controller.openDetail(id);
    });
}
