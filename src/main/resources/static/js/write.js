var token = "";
var jwt = "";
function goSave(){
    if (editor.getHTML().length == 11) {
        alert("내용을 입력해 주세요.");
        return;
    }
    $("#loadingBar").show();

    //$(".toastui-editor-contents a").attr("target","_blank");
    //<a href='' onclick='javascript:if(mobileDevice)PSS.callbrowser(\"https://www.naver.com\");else window.open(\"https://www.naver.com\");'>

    //alert("a 갯수 : "+$(".toastui-editor-contents a").length);

    var aLength = $(".toastui-editor-contents a").length;
    if(aLength > 0) {
        for(var i = 0; i < aLength; i++) {
            var a_href = $(".toastui-editor-contents a").eq(i).attr("href");
            $(".toastui-editor-contents a").eq(i).attr("href","");
            $(".toastui-editor-contents a").eq(i).attr("onclick","javascript:if(mobileDevice)PSS.callbrowser(\""+a_href+"\");else window.open(\""+a_href+"\");");
        }
    }
    var pushCheck = "0";
    if($("input:checkbox[id='pushChk']").prop("checked")) pushCheck = "1";

    $.ajax({
        type : "POST",
        url : "/contentwrite/upload",
        dataType : "JSON",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        contentType : "application/json; charset=UTF-8",
        data : JSON.stringify(new Content(boardId,editor.getHTML(),contentId,registDate,pushCheck)),
        error: function(req, status, e)
        {
            alert(e);
            $("#loadingBar").hide();
        },
        success: function(data)
        {
            if(data == "0") {
                if(mobileDevice) PSS.clearHistoryAll();
                location.href = "/?boardId="+boardId;
                //history.back();
            } else {
                console.log("업로드 실패");
                $("#loadingBar").hide();
            }
        }
    });
}

function goBack(){
    location.href = "/?boardId="+boardId;
}

function sendPost(url,params) {
    var form = document.createElement("form");
    form.setAttribute("method","post");
    form.setAttribute("action",url);

    document.charset="utf-8";

    for(var key in params) {
        var hiddenField = document.createElement("input");
        hiddenField.setAttribute("type","hidden");
        hiddenField.setAttribute("name",key);
        hiddenField.setAttribute("value",params[key]);
        form.appendChild(hiddenField);
    }
    document.body.appendChild(form);
    form.submit();
}

function Content(boardId,content,contentId,registDate,usePush)
{
    this.boardId = boardId;
    this.content = content;
    this.contentId = contentId;
    this.registDate = registDate;
    this.usePush = usePush;
}

function getContent(boardId,contentId,userId) {
    $.ajax({
        type : "POST",
        url : "/contentwrite/getcontent/"+contentId,
        dataType : "JSON",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        contentType : "application/json; charset=UTF-8",
        error: function(req, status, e)
        {
            alert(e);
        },
        success: function(data)
        {
            editor.setHTML(data.content);
            $("#loadingBar").hide();
        }
    });
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