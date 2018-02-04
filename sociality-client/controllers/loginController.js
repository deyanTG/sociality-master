var loginController = function () {
    function login(context) {
        templates.get('login')
            .then(function (template) {
                context.$element().html(template());

                $('#btn-login').on('click', function (e) {
                    e.preventDefault();
                    var user = {
                        username: $('#username').val(),
                        password: $('#password').val()
                    };

                    if ($.trim(user.username) === "" || $.trim(user.password) === "") {
                        return;
                    }

                    data.sociality.login(user)
                        .then(function () {
                            document.location = '#/home';
                            //
                        }, function (err) {
                            toastr.error("Incorrect username or password!");
                        });
                });
            });
    }

    return {
        login: login
    };
}();