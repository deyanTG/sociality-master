var homeController = (function () {
    function get(context) {
        data.sociality.isAuthenticated()
            .then(function () {
                var template = templates.get('home');
                return template;
            })
            .then(function (homeTemplate) {
                context.$element().html(homeTemplate());
            }).catch(function (error) {
        });
    }

    return {
        get: get
    }
}());