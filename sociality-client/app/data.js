var data = (function () {

    const CONFIG = {SERVER: 'http://sociality.com:8080/sociality'};

    function login(user) {
        var data = {
            grant_type: 'password',
            username: user.username,
            password: user.password
        };

        var options = {
            data: data
        };
        return jsonRequester.post(CONSTANTS.BE_SERVER + '/oauth/token', options, 'application/x-www-form-urlencoded; charset=UTF-8')
            .then(function (resp) {
                localStorage.setItem('REFRESH_IN', new Date(new Date().getTime() + resp.expires_in * 1000).getTime());
                localStorage.setItem('LOGGED_USER', JSON.stringify(resp.user));
                localStorage.setItem('RFRTKN', resp.refresh_token);
                localStorage.setItem('ACCTKN', resp.access_token);
            });
    }

    function post(post) {
        var data = {
            message: post.body,
            facebookOn: post.facebookOn,
            twitterOn: post.twitterOn,
            groupsId: post.facebookGroupsToShare,
            hashTags: post.twitterHashTagsTShare,
        };
        var options = {
            data: data
        };
        return jsonRequester.post(CONSTANTS.BE_SERVER + '/timeline/post', options);
    }

    function signOut() {
        var promise = new Promise(function (resolve, reject) {
            localStorage.removeItem('REFRESH_IN');
            localStorage.removeItem('LOGGED_USER');
            localStorage.removeItem('RFRTKN');
            localStorage.removeItem('ACCTKN');
            resolve();
        });
        return promise;
    }

    function isAuthenticated() {
        var refreshIn = localStorage.getItem('REFRESH_IN');
        if (refreshIn && refreshIn - new Date().getTime() > 1 * 60 * 1000) {
            return Promise.resolve(true);
        }
        return new Promise(function (resolve, reject) {
            var refresh_token = localStorage.getItem('RFRTKN');
            if (refresh_token) {
                refreshTheTokens(refresh_token, function () {
                    resolve(true);
                });
            } else {
                document.location = '#/login';
            }
        });
    };


    function refreshTheTokens(refreshToken, success) {
        jsonRequester.post(CONSTANTS.BE_SERVER + '/oauth/token', {
            data: {
                grant_type: 'refresh_token',
                refresh_token: refreshToken
            }
        }, 'application/x-www-form-urlencoded; charset=UTF-8').then(function (data) {
            localStorage.setItem('REFRESH_IN', new Date(new Date().getTime() + data.expires_in * 1000).getTime());
            localStorage.setItem('LOGGED_USER', JSON.stringify(data.user));
            localStorage.setItem('ACCTKN', data.access_token);
            if (typeof success === 'function') success();
        }, function (error) {
            localStorage.clear();
            document.location = '#/login';
        });
    };

    function logout() {
        var promise = new Promise(function (resolve, reject) {
            localStorage.clear();
            resolve();
        });
        return promise;
    }

    function profile() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/user/current').then(function (data) {
            return data;
        });
    }

    function getAuthenticatedResource(context, resource) {
        isAuthenticated().then(function (isAuth) {
            templates.get(resource)
                .then(function (template) {
                    context.$element().html(template());
                })

        });
    }

    function isFacebookConnected() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/facebook/user/isConnected').then(function (result) {
            return result.connected;
        });
    }

    function facebookFeed(paginationParam) {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/facebook/feed', {data: paginationParam});
    }

    function setFacebookShare(share) {
        return jsonRequester.post(CONSTANTS.BE_SERVER + '/facebook/settings/sharePost', {data: share});
    }

    function isTwitterConnected() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/twitter/user/isConnected').then(function (result) {
            return result.connected;
        });
    }

    function facebookProfile() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/facebook/user/profile').then(function (profile) {
            return profile;
        });
    }

    function facebookSettings() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/facebook/settings').then(function (settings) {
            return settings;
        });
    }

    function twitterProfile() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/twitter/user/profile').then(function (profile) {
            return profile;
        });
    }

    function timeline(paginationParam) {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/timeline', {data: paginationParam});
    }

    function twitterTimeline(paginationParam) {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/twitter/timeline', {data: paginationParam});
    }

    function twitterFriends(paginationParam) {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/twitter/friends', {data: paginationParam});
    }

    function twitterFollowers(paginationParam) {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/twitter/followers', {data: paginationParam});
    }

    function twitterSettings() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/twitter/settings').then(function (settings) {
            return settings;
        });
    }

    function setTwitterShare(share) {
        return jsonRequester.post(CONSTANTS.BE_SERVER + '/twitter/settings/sharePost', {data: share});
    }

    function register(user) {
        var data = $.extend({}, user);
        var options = {
            data: data
        };
        debugger;
        return jsonRequester.post(CONSTANTS.BE_SERVER + '/register', options);
    }

    function search(user) {
        var data = $.extend({}, user);
        // var data = {
        //     username: user.username
        // };
        var options = {
            data: data,
        };
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/admin/searchUser', options);

    }

    function manipulateUser(args) {
        var data = $.extend({}, args);
        // var data = {
        //     username: args.username,
        //     ban: args.ban,
        // };

        var options = {
            data: data,
        };

        return jsonRequester.post(CONSTANTS.BE_SERVER + '/admin/manipulateUser', options);
    }

    function getAllUsers() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/admin/getUsers');
    }

    function addAdmin(admin) {
        var data = $.extend({}, admin);
        // var data = {
        //     username: admin.username,
        //     password: admin.password,
        //     manageUsers: admin.manageUsers,
        // };

        var options = {
            data: data
        };
        return jsonRequester.post(CONSTANTS.BE_SERVER + '/admin/addAdmin', options);
    }

    function isAdmin() {
        return userHasRole(['ROLE_ADMIN']);
    }


    function isModerator() {
        return userHasRole(['ROLE_MODERATOR']);
    }

    function userHasRole(roles) {
        var currentUser = JSON.parse(localStorage.getItem('LOGGED_USER'));
        var hasRole = null;
        if (currentUser) {
            hasRole = currentUser.role && roles.indexOf(currentUser.role) >= 0;
        }
        return hasRole;
    }

    function getClients() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/admin/getClients');
    }

    function getOauthLink(clientId) {
        // var data = $.extend({}, apiData);
        var data = {
            clientId: clientId
        }

        var options = {
            data: data,
        };

        return jsonRequester.get(CONSTANTS.BE_SERVER + '/admin/getOauthLink', options);
    }

    function updateApi(apiData) {
        var data = $.extend({}, apiData);
        //
        //
        // var data = {
        //     clientId: apiData.clientId,
        //     oldClientId: apiData.oldClientId,
        //     redirectUri: apiData.redirectUri,
        //     refreshTokenValidity: apiData.refreshTokenValidity,
        //     accessTokenValidity: apiData.accessTokenValidity,
        // };
        var options = {
            data: data,
        };

        return jsonRequester.post(CONSTANTS.BE_SERVER + '/admin/updateApi', options);
    }


    function addClient(apiData) {
        // var data = {
        //     clientId: apiData.clientId,
        //     redirectUri: apiData.redirectUri,
        //     refreshTokenValidity: apiData.refreshTokenValidity,
        //     accessTokenValidity: apiData.accessTokenValidity,
        // };
        var data = $.extend({}, apiData);

        var options = {
            data: data,
        };

        return jsonRequester.post(CONSTANTS.BE_SERVER + '/admin/addClient', options);
    }

    function updateUser(user) {
        var data = $.extend({}, user);

        var options = {
            data: data,
        };

        return jsonRequester.post(CONSTANTS.BE_SERVER + '/user/update', options);
    }

    function facebookGroups() {
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/facebook/getGroups');
    }

    function facebookFriends(paginationParam) {
        debugger;
        return jsonRequester.get(CONSTANTS.BE_SERVER + '/facebook/friends', {data: paginationParam});
    }

    return {
        facebook: {
            isConnected: isFacebookConnected,
            feed: facebookFeed,
            profile: facebookProfile,
            settings: facebookSettings,
            setShare: setFacebookShare,
            facebookFriends: facebookFriends,
        },
        twitter: {
            isConnected: isTwitterConnected,
            profile: twitterProfile,
            timeline: twitterTimeline,
            friends: twitterFriends,
            followers: twitterFollowers,
            settings: twitterSettings,
            setShare: setTwitterShare,
        },
        sociality: {
            login: login,
            isAuthenticated: isAuthenticated,
            logout: logout,
            profile: profile,
            refreshTheTokens: refreshTheTokens,
            getAuthenticatedResource: getAuthenticatedResource,
            register: register,
            isAdmin: isAdmin,
            isModerator: isModerator,
            updateUser: updateUser,
            timeline: timeline,
            post: post,
        },
        admin: {
            search: search,
            manipulateUser: manipulateUser,
            getAllUsers: getAllUsers,
            addAdmin: addAdmin,
            getClients: getClients,
            getOauthLink: getOauthLink,
            updateApi: updateApi,
            addClient: addClient,
            facebookGroups: facebookGroups,
        },
        aggregator: {
            timeline: timeline,
            post: post,

        },
        users: {
            //ADMIN
            search: search,
            manipulateUser: manipulateUser,
            getAllUsers: getAllUsers,
            addAdmin: addAdmin,
            getClients: getClients,
            getOauthLink: getOauthLink,
            updateApi: updateApi,
            addClient: addClient,
            facebookGroups: facebookGroups,

            //COMMON
            login: login,
            isAuthenticated: isAuthenticated,
            logout: logout,
            profile: profile,
            refreshTheTokens: refreshTheTokens,
            getAuthenticatedResource: getAuthenticatedResource,
            register: register,
            isAdmin: isAdmin,
            isModerator: isModerator,
            updateUser: updateUser,

            // AGGREGATOR
            timeline: timeline,
            post: post,

            // FACEBOOK
            isFacebookConnected: isFacebookConnected,
            facebookFeed: facebookFeed,
            facebookProfile: facebookProfile,
            facebookSettings: facebookSettings,
            setFacebookShare: setFacebookShare,
            facebookFriends: facebookFriends,

            // TWITTER
            isTwitterConnected: isTwitterConnected,
            twitterProfile: twitterProfile,
            twitterTimeline: twitterTimeline,
            twitterFriends: twitterFriends,
            twitterFollowers: twitterFollowers,
            twitterSettings: twitterSettings,
            setTwitterShare: setTwitterShare,
        }
    };
}());