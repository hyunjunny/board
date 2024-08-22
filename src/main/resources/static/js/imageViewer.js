var mobileDevice = true;
var token = "";
var jwt = "";

$(document).ready(function(){
    $(".bigPictureWrapper").css("display","flex").show();
    $(".bigPicture").show({width:"100%", height: "100%"});

    checkMobile();

    PSS.getToken();
});

function hideBigPicture() {
    if(mobileDevice) PSS.showBigPicture("0");
    history.back(-1);
}

function checkMobile() {
    if( ['Win16','Win32','Win64','Mac','MacIntel','iPhone'].find(element => element == navigator.platform) ) {
        mobileDevice=false;
    } else {
        PSS.showDetailContent("1");
        PSS.showBigPicture("1");
        mobileDevice=true;
    }
}

function parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    //return JSON.parse(jsonPayload);
    jwt = JSON.parse(jsonPayload);
    //userId = jwt.sub;
}

function sessionTimeout()
{
    swal("세션 종료", "메신저를 통해서 다시 접속해 주세요.", "error");
}

function responseToken(_token){
    token = _token;
    parseJwt(token);
}