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

    return {
        executePostgre: executePostgre,
        tableInfoPostgre: tableInfoPostgre
    };
})