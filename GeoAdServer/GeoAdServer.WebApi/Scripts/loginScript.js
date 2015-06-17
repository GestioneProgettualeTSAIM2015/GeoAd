var headToken = "oauth2token";

$(function () {

    var errorAlert = $("#errorAlert");

    var loadingRegis = $("#loadingRegis");
    var loadingLogin = $("#loadingLogin");

    var loginDiv = $("#loginDiv");
    var dataDiv = $("#dataDiv");

    var regisForm = $('#regisForm');
    var loginForm = $('#loginForm');
    var pass, user;

    regisForm.submit(function (event) {
        event.preventDefault();

        regisForm.find("input").prop("disabled", true);
        loginForm.find("input").prop("disabled", true);
        errorAlert.hide();
        loadingRegis.css('visibility', 'visible');

        user = regisForm.find("#user").val();
        pass = regisForm.find("#pass").val();

        //check password

        var data = {
            Email: user,
            Password: pass,
            ConfirmPassword: pass
        };

        register(data);
    });

    loginForm.submit(function (event) {
        event.preventDefault();

        regisForm.find("input").prop("disabled", true);
        loginForm.find("input").prop("disabled", true);
        errorAlert.hide();
        loadingLogin.css('visibility', 'visible');

        user = loginForm.find("#user").val();
        var token = sessionStorage.getItem(headToken + user);
        if (token != null)
            logInAccount(token);
        else {
            pass = loginForm.find("#pass").val();
            login();
        }
    });

    function register(data) {
        $.ajax({
            url: '/api/Account/Register',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (data) {
                login();
            },
            error: function (xhr) {
                loadingRegis.css('visibility', 'hidden');
                console.log("Errore nella registrazione: " + xhr.responseText);
                errorAlert.text("Error:\n" + xhr.responseText).fadeIn();
                regisForm.find("input").prop("disabled", false);
                loginForm.find("input").prop("disabled", false);
            }
        });
    }

    function login() {
        var tokenRequest = "grant_type=password&UserName=Alice&password=password123";
        tokenRequest = tokenRequest.replace("Alice", user);
        tokenRequest = tokenRequest.replace("password123", pass);

        $.ajax({
            url: '/Token',
            type: 'POST',
            data: tokenRequest,
            success: function (data) {
                saveToken(data.access_token);
                window.location.replace("/Dashboard/Home");
            },
            error: function (xhr) {
                loadingLogin.css('visibility', 'hidden');
                console.log("Errore nel login: " + xhr.responseText);
                errorAlert.text("Error:\n" + xhr.responseText).fadeIn();
                regisForm.find("input").prop("disabled", false);
                loginForm.find("input").prop("disabled", false);
            }
        });
    }

    function saveToken(token) {
        localStorage.setItem(headToken, token);

        logInAccount(token);
    }

    //account

    var accountToken;

    function logInAccount(token) {
        accountToken = token;
        loginDiv.find("fieldset").attr("disabled", true);
        dataDiv.find("fieldset").attr("disabled", false);
    }
});

function getToken() {
    return localStorage.getItem(headToken);
}