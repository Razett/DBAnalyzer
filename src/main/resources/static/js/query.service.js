var QueryService = (function () {

    function executePostgre(queryInput, successCallback, errorCallback) {
        $.ajax({
            type: 'post',
            url: '/query/postgre',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(
                {
                    query: queryInput
                }
            ),
            success: function (data) {
                if (successCallback) {
                    successCallback(data);
                }
            },
            error: function (data) {
                if (errorCallback) {
                    errorCallback(data);
                }
            }
        })
    }

    function tableInfoPostgre(schemaName, tableName, successCallback, errorCallback) {
        $.ajax({
            type: 'post',
            url: '/query/postgre/tableinfo',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(
                {
                    schema: schemaName,
                    tableName: tableName
                }
            ),
            success: function (data) {
                if (successCallback) {
                    successCallback(data);
                }
            },
            error: function (data) {
                if (errorCallback) {
                    errorCallback(data);
                }
            }
        })
    }

    function tableInfoWithReferPostgre(schemaName, tableName, successCallback, errorCallback) {
        $.ajax({
            type: 'post',
            url: '/query/postgre/tableinfo2',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(
                {
                    schema: schemaName,
                    tableName: tableName
                }
            ),
            success: function (data) {
                if (successCallback) {
                    successCallback(data);
                }
            },
            error: function (data) {
                if (errorCallback) {
                    errorCallback(data);
                }
            }
        })
    }

    function postgreTypeDrawer(typename) {
        var html = '<div class="postgre-data-type">\n' +
            `  <button type="button" class="dropdown-toggle column-type-toggler" data-bs-toggle="dropdown">${typename}</button>\n` +
            '  <ul class="dropdown-menu fa-085x">\n' +
            '    <li class="dropdown-header fa-0825x">Integer</li>\n' +
            '    <li class="dropdown-item" data-bs-type="smallint">SMALLINT</li>\n' +
            '    <li class="dropdown-item" data-bs-type="integer">INTEGER</li>\n' +
            '    <li class="dropdown-item" data-bs-type="bigint">BIGINT</li>\n' +
            '    <li class="dropdown-item" data-bs-type="serial">SERIAL</li>\n' +
            '    <li class="dropdown-item" data-bs-type="bigserial">BIGSERIAL</li>\n' +
            '    <li class="dropdown-header fa-0825x">Text</li>\n' +
            '    <li class="dropdown-item" data-bs-type="character">CHAR</li>\n' +
            '    <li class="dropdown-item" data-bs-type="varchar">VARCHAR</li>\n' +
            '    <li class="dropdown-item" data-bs-type="text">TEXT</li>\n' +
            '    <li class="dropdown-item" data-bs-type="json">JSON</li>\n' +
            '    <li class="dropdown-item" data-bs-type="jsonb">JSONB</li>\n' +
            '    <li class="dropdown-item" data-bs-type="uuid">UUID</li>\n' +
            '    <li class="dropdown-header fa-0825x">Time</li>\n' +
            '    <li class="dropdown-item" data-bs-type="date">DATE</li>\n' +
            '    <li class="dropdown-item" data-bs-type="time">TIME</li>\n' +
            '    <li class="dropdown-item" data-bs-type="timestamp">TIMESTAMP</li>\n' +
            '    <li class="dropdown-header fa-0825x">ETC</li>\n' +
            '    <li class="dropdown-item" data-bs-type="boolean">BOOLEAN</li>\n' +
            '  </ul>\n' +
            '</div>'

        const template = document.createElement('template');
        template.innerHTML = html.trim();

        const domElement = template.content.firstChild;

        const dropdownItemElements = domElement.querySelectorAll('.dropdown-item');
        dropdownItemElements.forEach((element) => {
            if (element.textContent.trim() === typename.toUpperCase()) {
                element.classList.add('active');
            }
        });

        return domElement.outerHTML;
    }

    return {
        executePostgre: executePostgre,
        tableInfoPostgre: tableInfoPostgre,
        tableInfoWithReferPostgre:tableInfoWithReferPostgre,
        postgreTypeDrawer: postgreTypeDrawer
    };
})