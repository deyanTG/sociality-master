logoutController = (function () {

    function logout(context){
        data.sociality.logout().then(function(){
            document.location = '#/login';
            document.location.reload(true);
        });
    }

    return {
        logout : logout
    }
}());