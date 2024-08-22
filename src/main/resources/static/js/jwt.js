function parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    //return JSON.parse(jsonPayload);
    jwt = JSON.parse(jsonPayload);
    userId = jwt.sub;
    GetBoardList(userId);
}

function sessionTimeout()
{
    swal("세션 종료", "메신저를 통해서 다시 접속해 주세요.", "error");
}

function responseToken(_token){
    console.log(_token);
    token = _token;
    parseJwt(token);
}