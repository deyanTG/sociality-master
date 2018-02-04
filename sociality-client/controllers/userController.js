var userController = (function () {

    function profile(context) {
        data.sociality.isAuthenticated()
            .then(function () {
                return Promises.all[data.sociality.profile(), templates.get('profile')];
            })
            .then(function (profileData, profileTemplate) {
                context.$element().html(profileTemplate({username: profileData.username}));
            });
    }

    return {
        profile: profile
    }
}());