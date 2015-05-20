$(function () {

    var loginDiv = $("#loginDiv");
    var dataDiv = $("#dataDiv");

    var regisForm = $('#regisForm');
    var loginForm = $('#loginForm');
    var pass, user;

    var headToken = "oauth2token:";

    regisForm.submit(function (event) {
        event.preventDefault();

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
                console.log("Errore nella registrazione: " + xhr.responseText);
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
                saveToken(data.userName, data.access_token);
            },
            error: function (xhr) {
                console.log("Errore nel login: " + xhr.responseText);
            }
        });
    }

    function saveToken(user, token) {
        sessionStorage.setItem(headToken + user, token);

        logInAccount(token);
    }

    //account

    var accountToken;

    function logInAccount(token) {
        accountToken = token;
        loginDiv.find("fieldset").attr("disabled", true);
        dataDiv.find("fieldset").attr("disabled", false);
    }

    function logOutAccount() {
        accountToken = undefined;
        loginDiv.find("fieldset").attr("disabled", false);
        dataDiv.find("fieldset").attr("disabled", true);
    }

    $("#btnValues").on("click", function () {
        $.ajax({
            url: '/api/locations',
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + accountToken
            },
            success: function (data) {
                $("#display").text(data);
            },
            error: function (xhr) {
                console.log("Errore nel recupero dei dati: " + xhr.responseText);
            }
        });
    });

    $("#btnLogout").on("click", function () {
        $("#display").text("");
        logOutAccount();
    });
});