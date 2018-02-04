var jsonRequester = (function () {

    function send(method, url, options, contentTypeParam) {
        options = options || {};

        var headers = options.headers || {},
            data = options.data || undefined;

        if (contentTypeParam && contentTypeParam.indexOf('json') != -1) {
            data = JSON.stringify(data);
        }
        var promise = new Promise(function (resolve, reject) {
            $.ajax({
                url: url,
                beforeSend: function (xhr) {
                    var access_token = localStorage.getItem('ACCTKN');
                    if (url.indexOf('oauth') !== -1 || url.indexOf('register') !== -1) {
                        xhr.setRequestHeader('Authorization', 'Basic c29jaWFsaXR5LXdlYmFwcDo=');
                    } else {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + access_token);
                    }
                },
                method: method,
                contentType: contentTypeParam,
                data: data,
                statusCode: {
                    401: function () {
                        document.location = '#/login'
                    }
                },
                success: function (res) {
                    resolve(res);
                },
                error: function (err) {
                    reject(err);
                }
            });
        });
        return promise;
    }

    function get(url, options) {

        return send('GET', url, options);
    }

    function post(url, options, contentType='application/json') {
        return send('POST', url, options, contentType);
    }

    function put(url, options) {
        return send('PUT', url, options);
    }

    function del(url, options) {
        return send('POST', url, options);
    }

    function handle401() {
        var refresh_token = localStorage.getItem('RFRTKN');
        if (!refresh_token) {
            document.location = '#/login';
        } else {
            data.users.refreshTheTokens(refresh_token);
        }
    };

    return {
        send: send,
        get: get,
        post: post,
        put: put,
        delete: del
    };
}());