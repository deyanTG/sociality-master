var templates = function () {
    var handlebars = window.handlebars || window.Handlebars,
        Handlebars = window.handlebars || window.Handlebars,
        cache = {};

    var CONFIG = {
        NAVBAR: 'templates/navigation.handlebars',
        FOOTER: 'templates/navigation.handlebars',
        PROFILE: 'templates/profile.handlebars'
    };

    function getNavbar() {
        var name = 'navigation';
        var promise = new Promise(function (resolve, reject) {
            if (cache[name]) {
                resolve(cache[name]);
                return;
            }
            var url = 'templates/' + name + '.handlebars';
            $.get(url, function (navigationHtml) {
                var navigationTemplate = handlebars.compile(navigationHtml);
                cache[name] = navigationTemplate;
                resolve(navigationTemplate);
            })
        });
        return promise;
    }

    function getProfile() {
        var promise = new Promise(function (resolve, reject) {
            $.get(CONFIG.PROFILE, function (profileHtml) {
                var profile = handlebars.compile(profileHtml);
                cache[name] = profileHtml;
                resolve(profile);
            })
        });
        return promise;
    }


    function get(name, options = {}) {
        var promise = new Promise(function (resolve, reject) {
            if (cache[name]) {
                resolve(cache[name]);
                return;
            }
            var url = 'templates/' + name + '.handlebars';

            $.get(url, function (html) {
                getNavbar().then(function (navigation) {
                    var currentUser = JSON.parse(localStorage.getItem('LOGGED_USER'));
                    if (currentUser) {
                        options.isAdmin = currentUser.role && currentUser.role === 'ROLE_ADMIN';
                        options.isModerator = currentUser.role && currentUser.role === 'ROLE_MODERATOR';
                    }
                    Handlebars.registerPartial('top-nav', navigation(options));
                }).then(function () {
                    var template = handlebars.compile(html);
                    cache[name] = template;
                    resolve(template);
                });
            });
        });
        return promise;
    }

    return {
        get: get
    };
}();