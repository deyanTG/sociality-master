var registerController = (function () {

    function register(context) {
        templates.get('register')
            .then(function (template) {
                context.$element().html(template());

                $('#btn-register').on('click', function (e) {
                    e.preventDefault();

                    if ($('#password').val() !== $('#password_confirm').val()) {
                        toastr.error("Passwords do not match!")
                        return;
                    }

                    var user = {
                        username: $('#username').val(),
                        password: $('#password').val(),
                        firstName: $('#firstName').val(),
                        lastName: $('#lastName').val(),
                    };
                    debugger;

                    if ($.trim(user.username) === "" || $.trim(user.password) === "" || $.trim(user.firstName) === "" || $.trim(user.lastName) === "") {
                        return;
                    }

                    data.sociality.register(user)
                        .then(function () {
                            toastr.info("You can login now");
                            document.location = '#/login';

                        }, function (err) {
                            toastr.error("Please try again!");
                        });
                });
            });
    }

    return {
        register: register,
    }
}());