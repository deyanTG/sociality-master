var profilesController = (function () {

    function profiles(context) {
        data.sociality.isAuthenticated()
            .then(function () {
                return Promise.all([data.facebook.profile(), data.twitter.profile(), templates.get('profiles'), data.sociality.profile()]);
            })
            .then(function (result) {
                    var profiles = [result[0], result[1]];
                    var profilesTemplate = result[2];
                    var currentUserProfile = result[3];

                    var onlyRegisteredProfiles = profiles.filter(Boolean);

                    context.$element().html(profilesTemplate({
                        socialProfiles: onlyRegisteredProfiles,
                        currentUserProfile: currentUserProfile
                    }));

                    $('#btn-update-user').on('click', function (e) {
                        e.preventDefault();

                        var username = $('#username-field').val();
                        var password = $('#password-field').val();
                        var firstName = $('#first-name-field').val();
                        var lastName = $('#last-name-field').val();

                        var user = $.extend({}, {
                            username: username ? username : undefined,
                            password: password ? password : undefined,
                            firstName: firstName ? firstName : undefined,
                            lastName: lastName ? lastName : undefined,
                        });

                        data.sociality.updateUser(user)
                            .then(function () {
                                if (user.firstName) {
                                    $('#firstNameContainer').empty();
                                    $('#firstNameContainer').text(user.firstName);
                                }

                                if (user.lastName) {
                                    $('#lastNameContainer').empty();
                                    $('#lastNameContainer').text(user.lastName);
                                }

                                toastr.info('User has been updated successfully');
                            }, function (error) {
                                toastr.error('Please try again');
                            });
                    });
                }
            ).catch(function (error) {
            debugger
            var attr = error.responseJSON.attributes;
            if (attr && attr.type === 'twitterRateLimit') {
                toastr.error('You reached twitter rate limit. Please proceed after several minutes')
            }
        });
    }

    return {
        profiles: profiles
    }
}());
