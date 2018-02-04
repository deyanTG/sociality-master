var timelineController = (function () {
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

    function timeline(context) {
        data.sociality.isAuthenticated()
            .then(function () {
                return Promise.all([data.sociality.timeline(), templates.get('timeline'), data.facebook.settings(), data.twitter.settings(), data.facebook.isConnected(), data.twitter.isConnected(), data.admin.facebookGroups()]);
            })
            .then(function (resultAll) {
                    var timeline = resultAll[0];
                    var template = resultAll[1];

                    var facebookSettings = resultAll[2];
                    var twitterSettings = resultAll[3];

                    var facebookConnected = resultAll[4];
                    var twitterConnected = resultAll[5];

                    var facebookGroups = resultAll[6];

                    context.$element().html(template({
                        entities: timeline.entities,
                        facebook: facebookSettings.share && facebookConnected,
                        twitter: twitterSettings.share && twitterConnected,
                        facebookGroups: facebookGroups,
                    }));

                    $('.selectpicker').selectpicker();
                    $('#tokenfield').tokenfield();
                    $('#tokenfield').on('tokenfield:createtoken', function (e) {
                        var re = /(^#)/;
                        var startsWithHashtag = re.test(e.attrs.value);
                        // debugger;
                        if (!startsWithHashtag) {
                            e.attrs.value = '#' + e.attrs.value;
                            e.attrs.label = '#' + e.attrs.label;
                        }
                    });

                    var page = 1;
                    var page_size = CONSTANTS.TIMELINE_PAGE_SIZE;
                    var max_id = getMaxIdForTwitterTimeline(timeline.entities);

                    var paginationParam = {
                        //facebook paging
                        page: page * CONSTANTS.TIMELINE_PAGE_SIZE,
                        page_size: page_size,
                        // paging_token: timeline.facebookPaging.params.pagingToken,
                        // until: timeline.facebookPaging.params.until,
                        //twitter paging
                        max_id: max_id
                    };

                    if (timeline.facebookPaging) {
                        paginationParam.paging_token = timeline.facebookPaging.params.pagingToken;
                        paginationParam.until = timeline.facebookPaging.params.until;
                    }

                    $('#btn-load-more').on('click', function (e) {
                        e.preventDefault();
                        Promise.all([data.sociality.timeline(paginationParam), templates.get('timelineEntity')])
                            .then(function (resultAll) {
                                var timeline = resultAll[0];
                                var template = resultAll[1];

                                $('#moreContentContainer').append(template(timeline.entities));
                                paginationParam.max_id = getMaxIdForTwitterTimeline(timeline.entities);
                                paginationParam.page++;
                                //refresh facebook paging params
                                if (timeline.facebookPaging) {
                                    paginationParam.paging_token = timeline.facebookPaging.params.pagingToken;
                                    paginationParam.until = timeline.facebookPaging.params.until;
                                }

                            })
                            .catch(function (error) {
                                var attr = error.responseJSON.attributes;
                                if (attr && attr.type === 'twitterRateLimit') {
                                    toastr.error('You reached twitter rate limit for this request. Please try again after several minutes')
                                }
                            });
                    });

                    $('#btn-post').on('click', function (e) {
                        e.preventDefault();
                        var dictionaryOfGroupNameToId = {};
                        $('#facebookGroupsSelection option').each(function () {
                            dictionaryOfGroupNameToId[$(this).val()] = $(this).attr('id');
                        });

                        var facebookGroupsSelection =
                            $('#facebookGroupsSelection').val();

                        for (var groupName in dictionaryOfGroupNameToId) {
                            if (facebookGroupsSelection.indexOf(groupName) == -1) {
                                delete dictionaryOfGroupNameToId[groupName];
                            }
                        }

                        var tokenFieldValues = $('#tokenfield').tokenfield('getTokens').map(function (element) {
                            "use strict";
                            return element.value;
                        });

                        var postOptions = {
                            body: $('#post').val(),
                            facebookOn: facebookSettings.share,
                            twitterOn: twitterSettings.share,
                            facebookGroupsToShare: Object.keys(dictionaryOfGroupNameToId).map(function (k) {
                                return dictionaryOfGroupNameToId[k];
                            }),
                            twitterHashTagsTShare: tokenFieldValues,
                        };

                        debugger;
                        if ($.trim(postOptions.body) === "") {
                            return;
                        }

                        data.sociality.post(postOptions)
                            .then(function () {
                                toastr.info('Your post has been successfully shared');
                            }, function (err) {
                                toastr.error(
                                    'Please try again'
                                )
                            });
                        return false;
                    });
                }
            )
            .catch(function (error) {
                debugger;
                var attr = error.responseJSON.attributes;
                if (attr && attr.type === 'twitterRateLimit') {
                    toastr.error('You reached twitter rate limit for this request. Please try again after several minutes')
                }

            });
    }

    return {
        timeline: timeline
    }
}());