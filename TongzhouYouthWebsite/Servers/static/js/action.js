
function isValidForm(){
    var userErrMsg = document.getElementById("user-err-message");
    var pwdErrMsg = document.getElementById("pwd-err-message");

    var usr = document.getElementById("username").innerText;
    var pwd = document.getElementById("password").innerText;

    var usrPatt1 = /'\W/;
    var usrPatt2 = /^[_\d]/;

    var valid = true;

    if (usr.length < 8 || usr.length > 25){
        userErrMsg.innerText = "用户名长度应为8~25个字符.";
        valid = false
    }
    else if (!(usrPatt1.test(usr) || (usrPatt2.test(usr)))){
        userErrMsg.innerText = "用户名只能包含数字、字母和下划线，且必须以字母开头.";
        valid = false;
    }

    if (pwd.length < 8 || pwd.length > 25){
        pwdErrMsg.innerText = "密码长度应为8~25个字符.";
        valid = false;
    }

    return valid;
}

function submitOnclick(){
    alert("fuck i am used!");
    if (isValidForm()){
        document.getElementById('form').submit();
    }
}