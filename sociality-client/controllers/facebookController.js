/**
 * Created by deyan on 8/9/2016.
 */

var facebookController = (function () {

    function setMainFacebookView(context, callback) {
        data.sociality.isAuthenticated()
            .then(function () {
                return data.facebook.isConnected();
            })
            .then(function (connected) {
                var formData = {
                    connectUrl: CONSTANTS.BE_SERVER + '/connect/facebook',
                    token: localStorage.getItem('ACCTKN')
                };
                if (connected) {
                    templates.get('facebookConnected').then(function (facebookConnectedTemplate) {
                        callback(facebookConnectedTemplate);
                    });

                } else {
                    templates.get('facebookConnect')
                        .then(function (facebookConnectTemplate) {
                            context.$element().html(facebookConnectTemplate(formData));
                        })
                }

            });
    }

    function setProfileView(context) {
        setMainFacebookView(context, function (facebookConnectedTemplate) {
            Promise.all([data.facebook.profile(), templates.get('profile')])
                .then(function (resultAll) {
                    var profile = resultAll[0];
                    var profileTemplate = resultAll[1];
                    var formData = {
                        connectUrl: CONSTANTS.BE_SERVER + '/connect/facebook',
                        token: localStorage.getItem('ACCTKN')
                    };
                    for (var property in formData) {
                        profile[property] = formData[property];
                    }

                    Handlebars.registerPartial('profile', profileTemplate(profile));
                    context.$element().html(facebookConnectedTemplate({templateType: 'profile'}));
                });
        });
    }

    function setFeedView(context) {
        setMainFacebookView(context, function (facebookConnectedTemplate) {
            Promise.all([data.facebook.feed(), templates.get('facebookFeed')])
                .then(function (resultAll) {
                    var facebookFeed = resultAll[0];
                    var facebookFeedTemplate = resultAll[1];
                    $('.selectpicker').selectpicker();
                    Handlebars.registerPartial('facebookFeed', facebookFeedTemplate(facebookFeed.entities));
                    context.$element().html(facebookConnectedTemplate({templateType: 'facebookFeed'}));

                    var page = 1;
                    var page_size = CONSTANTS.TIMELINE_PAGE_SIZE;

                    var paginationParam = {
                        //facebook paging
                        page: page * CONSTANTS.TIMELINE_PAGE_SIZE,
                        page_size: page_size,
                    };

                    if (facebookFeed.facebookPaging) {
                        paginationParam.paging_token = facebookFeed.facebookPaging.params.pagingToken;
                        paginationParam.until = facebookFeed.facebookPaging.params.until;
                    }

                    $('#btn-load-more').on('click', function (e) {
                        e.preventDefault();
                        Promise.all([data.facebook.feed(paginationParam), templates.get('timelineEntity')])
                            .then(function (resultAll) {
                                var feed = resultAll[0];
                                var template = resultAll[1];

                                $('#moreContentContainer').append(template(feed.entities));
                                paginationParam.page++;

                                //refresh facebook paging params
                                if (feed.facebookPaging) {
                                    paginationParam.paging_token = feed.facebookPaging.params.pagingToken;
                                    paginationParam.until = feed.facebookPaging.params.until;
                                }
                            });
                    });
                });
        });
    }


    function setSettingsView(context) {
        setMainFacebookView(context, function (facebookConnectedTemplate) {
            Promise.all([templates.get('facebookPostSettings'), data.facebook.settings(), data.admin.facebookGroups()])
                .then(function (resultAll) {
                    var facebookPostSettingsTemplate = resultAll[0];
                    var facebookSettings = resultAll[1];
                    var facebookGroups = resultAll[2];

                    Handlebars.registerPartial('facebookPostSettings', facebookPostSettingsTemplate({
                        connected: true,
                        facebookGroups: facebookGroups,
                    }));

                    context.$element().html(facebookConnectedTemplate({templateType: 'facebookPostSettings'}));

                    $('.selectpicker').selectpicker();
                    $('#facebookOn').prop('checked', function () {
                        var share = facebookSettings.share;
                        if (share) {
                            $('.selectpicker').selectpicker('show');
                            // $('#groupChooserContainer').attr('hidden', false);
                        } else {
                            $('.selectpicker').selectpicker('hide');

                            // $('#groupChooserContainer').attr('hidden', true);
                        }
                        return share;
                    });

                    $('#facebookOn').change(function () {
                        var checked = $(this).is(":checked");
                        if (checked) {
                            $('.selectpicker').selectpicker('show');

                            // $('#groupChooserContainer').attr('hidden', false);
                        } else {
                            // $('#groupChooserContainer').attr('hidden', true);
                            $('.selectpicker').selectpicker('hide');

                        }
                        data.facebook.setShare({share: checked});
                    });

                });
        });

    }

    // function setFriendsView(context) {
    //     setMainFacebookView(context, function (facebookConnectedTemplate) {
    //         "use strict";
    //         Promise.all([data.facebook.facebookFriends(), templates.get('facebookFriends')])
    //             .then(function (resultAll) {
    //                 var facebookFriends = resultAll[0];
    //                 var facebookFriendsTemplate = resultAll[1];
    //                 Handlebars.registerPartial('facebookFriends', facebookFriendsTemplate(facebookFriends));
    //                 context.$element().html(facebookConnectedTemplate({templateType: 'facebookFriends'}));
    //             })
    //     });
    // }

    return {
        status: setProfileView,
        profile: setProfileView,
        feed: setFeedView,
        settings: setSettingsView,
    }
}());