/**
 * Created by deyan on 8/10/2016.
 */
var twitterController = (function () {

    function setMainTwitterView(context, callback) {
        data.sociality.isAuthenticated()
            .then(function () {
                return data.twitter.isConnected();
            })
            .then(function (connected) {
                var formfta = {
                    connectUrl: CONSTANTS.BE_SERVER + '/connect/twitter',
                    token: localStorage.getItem('ACCTKN')
                };
                if (connected) {
                    templates.get('twitterConnected').then(function (twitterConnectedTemplate) {
                        callback(twitterConnectedTemplate);
                    });

                } else {
                    templates.get('facebookConnect')
                        .then(function (facebookConnectTemplate) {
                            context.$element().html(facebookConnectTemplate(formData));
                        })
                }

            });
    }

    function status(context) {
        data.sociality.isAuthenticated()
            .then(function () {
                data.twitter.isConnected()
                    .then(function (connected) {
                        var formData = {
                            connectUrl: CONSTANTS.BE_SERVER + '/connect/twitter',
                            token: localStorage.getItem('ACCTKN')
                        };
                        if (connected) {
                            Promise.all([data.twitter.profile(), templates.get('twitterConnected'), templates.get('profile')])
                                .then(function (resultAll) {
                                    var profile = resultAll[0];
                                    var twitterConnectedTemplate = resultAll[1];
                                    var profileTemplate = resultAll[2];

                                    for (var property in formData) {
                                        profile[property] = formData[property];
                                    }
                                    Handlebars.registerPartial('profile', profileTemplate(profile));
                                    context.$element().html(twitterConnectedTemplate({templateType: 'profile'}));

                                })
                                .catch(function (error) {
                                    var attr = error.responseJSON.attributes;
                                    if (attr && attr.type === 'twitterRateLimit') {
                                        toastr.error('You reached twitter rate limit for this request. Please try again after several minutes')
                                    }
                                });
                        } else {
                            templates.get('twitterConnect')
                                .then(function (template) {
                                    context.$element().html(template(formData));
                                })
                        }
                    });
            });
    }

    function setProfileView(context) {
        setMainTwitterView(context, function (twitterConnectedTemplate) {
            Promise.all([data.twitter.profile(), templates.get('profile')])
                .then(function (resultAll) {
                    var profile = resultAll[0];
                    var profileTemplate = resultAll[1];
                    var formData = {
                        connectUrl: CONSTANTS.BE_SERVER + '/connect/twitter',
                        token: localStorage.getItem('ACCTKN')
                    };
                    for (var property in formData) {
                        profile[property] = formData[property];
                    }

                    Handlebars.registerPartial('profile', profileTemplate(profile));
                    context.$element().html(twitterConnectedTemplate({templateType: 'profile'}));
                })
                .catch(function (error) {
                    var attr = error.responseJSON.attributes;
                    if (attr && attr.type === 'twitterRateLimit') {
                        toastr.error('You reached twitter rate limit. Please proceed after several minutes')
                    }
                });
        });
    }

    function setTimelineView(context) {
        setMainTwitterView(context, function (twitterConnectedTemplate) {
            Promise.all([data.twitter.timeline(), templates.get('twitterTimeline')])
                .then(function (resultAll) {
                    var twitterTimeline = resultAll[0];
                    var twitterTimelineTemplate = resultAll[1];
                    var page = 1;
                    var max_id = getMaxIdForTwitterTimeline(twitterTimeline.entities);

                    var paginationParam = {
                        //twitter paging
                        page: page * CONSTANTS.TIMELINE_PAGE_SIZE,
                        max_id: max_id
                    };
                    Handlebars.registerPartial('twitterTimeline', twitterTimelineTemplate(twitterTimeline.entities));
                    context.$element().html(twitterConnectedTemplate({templateType: 'twitterTimeline'}));

                    $('#btn-load-more').on('click', function (e) {
                        e.preventDefault();
                        Promise.all([data.twitter.timeline(paginationParam), templates.get('timelineEntity')])
                            .then(function (resultAll) {
                                var timeline = resultAll[0];
                                var template = resultAll[1];
                                $('#moreContentContainer').append(template(timeline.entities));
                                paginationParam.max_id = getMaxIdForTwitterTimeline(timeline.entities);
                                paginationParam.page++;
                            })
                            .catch(function (error) {
                                var attr = error.responseJSON.attributes;
                                if (attr && attr.type === 'twitterRateLimit') {
                                    toastr.error('You reached twitter rate limit. Please proceed after several minutes')
                                }
                            });
                    });

                })
                .catch(function (error) {
                    var attr = error.responseJSON.attributes;
                    if (attr && attr.type === 'twitterRateLimit') {
                        toastr.error('You reached twitter rate limit. Please proceed after several minutes')
                    }
                });

        });
    }

    function setFriendsView(context) {
        setMainTwitterView(context, function (twitterConnectedTemplate) {
            Promise.all([data.twitter.friends(), templates.get('twitterFriends')])
                .then(function (resultAll) {
                    var twitterFriends = resultAll[0];
                    var twitterFriendsTemplate = resultAll[1];
                    Handlebars.registerPartial('twitterFriends', twitterFriendsTemplate(twitterFriends));
                    context.$element().html(twitterConnectedTemplate({templateType: 'twitterFriends'}));
                })
                .catch(function (error) {
                    var attr = error.responseJSON.attributes;
                    if (attr && attr.type === 'twitterRateLimit') {
                        toastr.error('You reached twitter rate limit. Please proceed after several minutes')
                    }
                });
        });
    }


    function setFollowersView(context) {
        setMainTwitterView(context, function (twitterConnectedTemplate) {
            Promise.all([data.twitter.followers(), templates.get('twitterFollowers')])
                .then(function (resultAll) {
                    var twitterFollowers = resultAll[0];
                    var twitterFollowersTemplate = resultAll[1];
                    Handlebars.registerPartial('twitterFollowers', twitterFollowersTemplate(twitterFollowers));
                    context.$element().html(twitterConnectedTemplate({templateType: 'twitterFollowers'}));
                })
                .catch(function (error) {
                    var attr = error.responseJSON.attributes;
                    if (attr && attr.type === 'twitterRateLimit') {
                        toastr.error('You reached twitter rate limit. Please proceed after several minutes')
                    }
                });
        });
    }

    function setSettingsView(context) {
        setMainTwitterView(context, function (twitterConnectedTemplate) {
            Promise.all([templates.get('twitterPostSettings'), data.twitter.settings()])
                .then(function (resultAll) {
                    var twitterPostSettingsTemplate = resultAll[0];
                    var twitterSettings = resultAll[1];
                    Handlebars.registerPartial('twitterPostSettings', twitterPostSettingsTemplate({connected: true}));
                    context.$element().html(twitterConnectedTemplate({templateType: 'twitterPostSettings'}));
                    $('#twitterOn').prop('checked', twitterSettings.share);
                    $('#twitterOn').change(function () {
                        var checked = $(this).is(":checked");
                        data.twitter.setShare({share: checked});
                    });

                });
        });

    }


    function getMaxIdForTwitterTimeline(entities) {
        var currentMax = null;

        if (entities.length === 0) {
            return null;
        }
        //we need last fetched twitter entity
        entities.forEach(function (entity) {
            if (entity.socialEntityType === 'TWITTER') {
                currentMax = entity.id;
            }
        });
        return currentMax;
    }

    return {
        status: status,
        profile: setProfileView,
        timeline: setTimelineView,
        friends: setFriendsView,
        followers: setFollowersView,
        settings: setSettingsView,
    }
}());
