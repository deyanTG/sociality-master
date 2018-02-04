var entryController = (function () {

    function entry(context) {
        data.sociality.isAuthenticated(context, false)
            .then(function () {
                document.location = '#/home';
            });
    }

    return {
        entry: entry
    }
}());