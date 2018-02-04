(function () {
    var sammyApp = Sammy('#content', function () {

        //COMMONS
        this.get('#/login', loginController.login);

        this.get('#/logout', logoutController.logout);

        this.get('#/home', homeController.get);

        this.get('#/', entryController.entry);

        this.get('#/profile', profilesController.profiles);

        this.get('#/register', registerController.register);

        //AGGREGATOR
        this.get('#/timeline', timelineController.timeline);

        // this.get('#/post', postController.post);

        //TWITTER
        this.get('#/twitter', twitterController.status);

        this.get('#/twitterProfile', twitterController.profile);

        this.get('#/twitterTimeline', twitterController.timeline);

        this.get('#/twitterFriends', twitterController.friends);

        this.get('#/twitterFollowers', twitterController.followers);

        this.get('#/twitterSettings', twitterController.settings);

        //FACEBOOK
        this.get('#/facebook', facebookController.status);
        this.get('#/facebookFeed', facebookController.feed);
        this.get('#/facebookProfile', facebookController.profile);
        this.get('#/facebookSettings', facebookController.settings);
        this.get('#/facebookFriends',facebookController.friends);

        //ADMIN
        // this.get('/^#\/(admin|manageUser)/', adminController.manageUser);
        this.get('#/admin', adminController.listUsers);
        this.get('#/manageUser', adminController.manageUser);
        this.get('#/listUsers', adminController.listUsers);
        this.get("#/addAdmin", adminController.addAdmin);
        this.get('#/manageApi', adminController.manageApi);
    });

    function urlify(text) {
        var urlRegex = /(https?:\/\/[^\s]+)/g;
        return text.replace(urlRegex, function (url) {
            return '<a href="' + url + '">' + url + '</a>';
        });
    }

    $(function () {

        sammyApp.run('#/');

        Handlebars.registerHelper('profileChooser', function (type) {
            // if (type) {
            return type && type[0].toUpperCase() + type.slice(1) + ' Profile';
            // }
            // return '';
        });


        Handlebars.registerHelper('currentUsername', function () {
            var currentUser = localStorage.getItem('LOGGED_USER');
            if (currentUser) {
                return JSON.parse(currentUser).username;
            }
        });

        Handlebars.registerHelper('date', function (value) {
            if (value) {
                return new Handlebars.SafeString(moment(value).format('lll'));
            }
            return '';
        });

        Handlebars.registerHelper('parseTwitterText', function (value) {
            if (value) {
                return new Handlebars.SafeString(urlify(value));
            }
            return '';
        });

        Handlebars.registerHelper('classSocialIcon', function (type) {
            if (type) {
                if (type.toLowerCase() === 'facebook') {
                    return 'fa fa-facebook';
                }
                if (type.toLowerCase() === 'twitter') {
                    return 'fa fa-twitter';
                }
            }
            return '';
        });

        Handlebars.registerHelper('postPictureHelper', function (picture) {
            if (picture) {
                return new Handlebars.SafeString('<br /><div><img src=' + picture + ' class="img-thumbnail"></div>');
            }
            return '';
        });

        Handlebars.registerHelper('if_eq', function (a, b, opts) {
            if (a == b) {
                return opts.fn(this);
            } else {
                return opts.inverse(this);
            }
        });

        Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {

            switch (operator) {
                case '==':
                    return (v1 == v2) ? options.fn(this) : options.inverse(this);
                case '===':
                    return (v1 === v2) ? options.fn(this) : options.inverse(this);
                case '<':
                    return (v1 < v2) ? options.fn(this) : options.inverse(this);
                case '<=':
                    return (v1 <= v2) ? options.fn(this) : options.inverse(this);
                case '>':
                    return (v1 > v2) ? options.fn(this) : options.inverse(this);
                case '>=':
                    return (v1 >= v2) ? options.fn(this) : options.inverse(this);
                case '&&':
                    return (v1 && v2) ? options.fn(this) : options.inverse(this);
                case '||':
                    return (v1 || v2) ? options.fn(this) : options.inverse(this);
                default:
                    return options.inverse(this);
            }
        });

    });
}());
