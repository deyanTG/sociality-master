var adminController = (function () {

    function setMainAdminView(context, callback) {
        data.sociality.isAuthenticated()
            .then(function () {
                return Promise.all([templates.get('adminPanel')]);
            })
            .then(function (resultAll) {
                var adminTemplate = resultAll[0];
                callback(adminTemplate);
            });
    }

    function setListUsersView(context) {
        setMainAdminView(context, function (adminTemplate) {
            Promise.all([templates.get('usersListForAdmin'), data.admin.getAllUsers()])
                .then(function (resultAll) {
                    var usersListForAdminTemplate = resultAll[0];
                    var allUsersData = resultAll[1];
                    var allUsers = allUsersData.map(function (user) {
                        return user.userData;
                    });
                    Handlebars.registerPartial('userList', usersListForAdminTemplate(allUsers));
                    context.$element().html(adminTemplate({
                        templateType: 'userList',
                        isAdmin: data.sociality.isAdmin()
                    }));
                });
        });
    }

    function setAddAdminView(context) {
        setMainAdminView(context, function (adminTemplate) {
            templates.get('addAdmin').then(function (addAdminTemplate) {
                Handlebars.registerPartial('addAdmin', addAdminTemplate());
                context.$element().html(adminTemplate({templateType: 'addAdmin', isAdmin: data.sociality.isAdmin()}));

                $('#btn-add-admin').on('click', function (e) {
                    var admin = {
                        username: $('#adminName').val(),
                        password: $('#adminPassword').val(),
                        manageUsers: $('#chekcbox-mng-users').is(':checked'),
                    };

                    if ($.trim(admin.username) === "" || $.trim(admin.password) === "") {
                        return;
                    }

                    data.admin.addAdmin(admin)
                        .then(function (admin) {
                            toastr.info(admin.username + ' admin added');
                        }, function (err) {
                            toastr.error("Please try again");
                        });
                });
            });
        });
    }

    function setManageUserView(context) {
        setMainAdminView(context, function (adminTemplate) {
            templates.get('manageUser')
                .then(function (manageUserTemplate) {
                    Handlebars.registerPartial('manageUser', manageUserTemplate());
                    context.$element().html(adminTemplate({
                        templateType: 'manageUser',
                        isAdmin: data.sociality.isAdmin()
                    }));

                    $('#btn-search-user').on('click', function (e) {
                        e.preventDefault();
                        $('#searchResultContainer').empty();
                        $('#auditForUserContainer').empty();
                        var username = $('#username').val();
                        if (!username) {
                            return;
                        }
                        var userSearchInput = {
                            username: username,
                        };

                        data.sociality.isAuthenticated()
                            .then(function () {
                                data.admin.search(userSearchInput)
                                    .then(function (userProfile) {
                                        if (userProfile.user.userData) {
                                            Promise.all([templates.get('userProfileForAdmin'), templates.get('auditsForUser')])
                                                .then(function (resultAll) {
                                                    var userProfileForAdminTemplate = resultAll[0];
                                                    var userAuditActionsTemplate = resultAll[1];
                                                    $('#searchResultContainer').append(userProfileForAdminTemplate({userProfile: userProfile.user,isAdmin : data.sociality.isAdmin()}));
                                                    $('#auditForUserContainer').append(userAuditActionsTemplate({audits: userProfile.audits}));
                                                    if (userProfile.user.userData.enabled) {
                                                        $('#btn-ban').prop('disabled', false);
                                                        $('#btn-remove-ban').prop('disabled', true);
                                                    } else {
                                                        $('#btn-ban').prop('disabled', true);
                                                        $('#btn-remove-ban').prop('disabled', false);
                                                    }

                                                    $('#btn-ban').on('click', function () {
                                                        $('#btn-ban').prop('disabled', true);
                                                        $('#btn-remove-ban').prop('disabled', false);
                                                        manipulateUser(userProfile.user.userData.username, true);
                                                    });

                                                    $('#btn-remove-ban').on('click', function () {
                                                        $('#btn-ban').prop('disabled', false);
                                                        $('#btn-remove-ban').prop('disabled', true);
                                                        manipulateUser(userProfile.user.userData.username, false);
                                                    });
                                                });
                                        } else {
                                            $('#searchResultContainer').append("No result found!");
                                        }
                                    });
                            });
                    });
                });
        });
    }

    function manageApi(context) {
        setMainAdminView(context, function (adminTemplate) {
            Promise.all([templates.get('manageApi'), data.admin.getClients()])
                .then(function (resultAll) {
                    var manageApiTemplate = resultAll[0];
                    var clients = resultAll[1];
                    Handlebars.registerPartial('manageApi', manageApiTemplate(clients));
                    context.$element().html(adminTemplate({
                        templateType: 'manageApi',
                        isAdmin: data.sociality.isAdmin()
                    }));

                    $("#table-id td").find('input:hidden').each(function (index) {
                        $(this).val(clients[index].client_id);
                    });

                    $('.row-class').click(function (e) {
                        var currentClientId = $(this).closest('tr')   // Finds the closest row <tr>
                            .find('.client-id-value')     // Gets a descendent with class="nr"
                            .text();         // Retrieves the text within <td>
                        $("#oauthLinkContainer").removeClass('alert alert-info');
                        $("#oauthLinkContainer").empty();
                        data.admin.getOauthLink(currentClientId)
                            .then(function (response) {
                                $("#oauthLinkContainer").addClass('alert alert-info');
                                $("#oauthLinkContainer").append(response.oauthLink);       // Outputs the answer
                            });
                    });

                    $('#btn-add-client').on('click', function (e) {
                        var clientDetails = {
                            clientId: $('#client-id-field').val(),
                            redirectUri: $('#redirect-uri-field').val(),
                            refreshTokenValidity: $('#refresh-token-validity-field').val(),
                            accessTokenValidity: $('#access-token-validity-field').val()
                        };

                        data.admin.addClient(clientDetails)
                            .then(function () {
                                toastr.info('New client added');
                            }, function (error) {
                                toastr.error('Please try again');
                            })
                    });

                    $('.update-api').click(function (e) {
                        var update = null;
                        $(this).text(function (i, text) {
                            text = text.trim();
                            if (text.trim() === 'Update API') {
                                update = true;
                                return 'Save Changes';
                            }
                            if (text === 'Save Changes') {
                                update = false;
                                return 'Update API';
                            }
                            return null;
                        });

                        if (update) {
                            $(this).removeClass('btn-warning');
                            $(this).addClass('btn-danger');
                            $(this).closest('tr')   // Finds the closest row <tr>
                                .children().each(function (e) {
                                if (e === 0) {
                                    $(this).focus();
                                }
                                if (!$(this).is(':last-child')) {
                                    $(this).prop('contenteditable', true);
                                }
                            });
                        } else {
                            $(this).removeClass('btn-danger');
                            $(this).addClass('btn-warning');

                            var apiData = {
                                oldClientId: $(this).siblings('input').val(),
                                clientId: $(this).closest('tr')
                                    .find('.client-id-value')
                                    .text(),
                                redirectUri: $(this).closest('tr')
                                    .find('.redirect-uri-value')
                                    .text(),
                                refreshTokenValidity: $(this).closest('tr')
                                    .find('.refresh-token-validity-value')
                                    .text(),
                                accessTokenValidity: $(this).closest('tr')
                                    .find('.access-token-validity-value')
                                    .text(),
                            };

                            $(this).closest('tr')   // Finds the closest row <tr>
                                .children().each(function (e) {
                                if (!$(this).is(':last-child')) {
                                    $(this).prop('contenteditable', false);
                                }
                            });

                            data.admin.updateApi(apiData)
                                .then(function () {
                                    $(this).removeClass('btn-danger');
                                    $(this).addClass('btn-warning');
                                    toastr.info('You successfully update API client');
                                }, function (error) {
                                    toastr.error('Something unexpected happened');
                                });
                        }
                    });
                });
        });
    }

    function manipulateUser(username, action) {
        data.admin.manipulateUser({
            username: username,
            ban: action,
        });
    }

    return {
        manageUser: setManageUserView,
        listUsers: setListUsersView,
        addAdmin: setAddAdminView,
        manageApi: manageApi
    }
}());