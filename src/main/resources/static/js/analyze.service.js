var AnalyzeService = (function () {

    function analyzePostgre(successCallback, errorCallback) {
        $.ajax({
            type: 'post',
            url: '/analyze/postgre',
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
        analyzePostgre: analyzePostgre
    };
})