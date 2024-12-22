var ModalService = (function () {
    var idCount = 0;

    const modalContainer = document.getElementById('modal-container');

    function printAlertModal(title, message) {
        var alertModalHtml = '<div id="modal-container">\n' +
            '    <div class="modal fade" id="modal-' + idCount.toString() + '" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="alert-modal-1" aria-hidden="true">\n' +
            '      <div class="modal-dialog">\n' +
            '        <div class="modal-content">\n' +
            '          <div class="modal-header">\n' +
            '            <h1 class="modal-title fs-5" id="staticBackdropLabel">' + title + '</h1>\n' +
            '          </div>\n' +
            '          <div class="modal-body">\n' +
            '            ' + message + '\n' +
            '          </div>\n' +
            '          <div class="modal-footer">\n' +
            '            <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Close</button>\n' +
            '          </div>\n' +
            '        </div>\n' +
            '      </div>\n' +
            '    </div>\n' +
            '  </div>'

        modalContainer.innerHTML = alertModalHtml;
        var modal = new bootstrap.Modal(document.getElementById('modal-' + idCount.toString()));
        idCount = idCount + 1;
        modal.show();
    }

    return {
        printAlertModal: printAlertModal
    };
})