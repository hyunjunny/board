var windowWidth = $( window ).width();
var viewBoardId = "";
var viewContentId = "";

var onContentReply="0";

var parentDeptId = "0";

var viewContentUser;

var scroll = "0";

function bindBoardList(data)
{
      var html = "";
      var list = data.list;

      for(var i = 0; i < list.length; i++){
          html += "<span class=\""+list[i].boardId+"\" onclick='GetContentList(\""+list[i].boardId+"\")'>" + list[i].boardName + "</span>"
      }

      $("#boardNav").append(html);

      $("#boardNav").children("span").removeClass("on");
      //$("#boardNav").children("span."+boardId).addClass("on");

      if(viewBoardId != boardId) {
          $("#container").children().remove();
          $("#container").scrollTop(0);
          page = 0;
          lastContentFlag = 0;
          viewBoardId = boardId;
      }

      //bindContentList(contentList);

      if(list.length > 0 && (boardId=="" || boardId==null)) $("#boardNav").children("span:eq(0)").trigger("click");
      else if(list.length > 0) $("#boardNav").children("."+boardId).trigger("click");
}

function GetBoardList(userId) {
    //console.log("getBoardList : " + userId);
    $.ajax({
        url: "getboardlist",
        beforeSend: function (xhr) {
          xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        contentType:"application/x-www-form-urlencoded; charset=UTF-8",
        type: "POST",
        //data: "userId="+userId,
        dataType: "json",
        success: function(data) {
            bindBoardList(data);
        },
        error: function(error) {
            console.log(error);
        }
    });
}

function bindContentList(data)
{
    var html = "";
    var list = data.list;

    if(list.length == 0){
        lastContentFlag = 1;
        if(page == 0) {
          html += "<section style='border:0;'>";
          html += "   <p style='margin-top:30px;text-align:center;'>작성된 게시글이 없습니다.</p>";
          html += "</section>";
        }
    } else {
        for(var i = 0; i < list.length; i++){
            var contentReadUserCnt = 0;
            var toolIcons = "<span style='float:right;' onclick='javascript:goFuntionList(\""+list[i].contentId+"\",this);'><img src='/img/more.png' alt='더보기' width='17px' height='20px' style='margin-top:5px; margin-left:5px;'/></span>";
            var blindContent = "<p class='moreBtn' onclick='javascript:setReadContent(\""+list[i].contentId+"\",this);'><span><img src='/img/checkmark.png' alt='열람하기' height='200px;'></br><strong>열 람 하 기</strong></span></p>";

            if(list[i].hasOwnProperty("contentUser")) contentReadUserCnt = list[i].contentUser.length;
            if(userId != list[i].loginId) {
                if(userId != "msgadm")
                    toolIcons = "";
                if(userId == "msgadm") blindContent = "";
                else{
                    if(list[i].hasOwnProperty("contentUser")){
                        for(var j = 0; j < list[i].contentUser.length; j++){
                            if(list[i].contentUser[j] == userId) {
                                blindContent = "";
                                continue;
                            }
                        }
                    }
                }
            }
            else
            {
                blindContent = "";
            }
            html += "<section class=\""+list[i].contentId+"\">";
            html += "   <div class='userdata' style='padding:5px;'>";
            html += "       <span class='userpic' style='margin-left:5px;'><img style='border-radius:15px;' src=\""+ photoUrl+"/"+ list[i].userId + "/25/25" +"\" alt='사용자 사진'/></span>";
            html += "       <span><span onclick='javascript:if(mobileDevice)PSS.showUserInfo(\""+ list[i].userId +"\");'> " + list[i].userName + "</span>";
            html += "       <span class='regDate'> " + list[i].registDate + " </span>"+toolIcons+"</span>";
            //html += "       <span class='userpic' style='margin-left:5px;'><img src='http://125.131.105.209:18003/"+ list[i].userId +"' onerror='this.onerror=null; this.src=\"/img/nouserpicture.png\";' alt='사용자 사진' width='30px' height='30px'/></span><span onclick='javascript:if(mobileDevice)PSS.showUserInfo(\""+ list[i].userId +"\");'> " + list[i].userName + " <span class='regDate'> " + list[i].registDate + " </span>"+toolIcons+"</span>";
            html += "   </div>";
            html += "   <div class='contentdata' onclick='javascript:showDetailContent(\""+list[i].contentId+"\",this)'>";
            html += list[i].content;
            //html += "<p><a href='' onclick='javascript:if(mobileDevice)PSS.callbrowser(\"https://www.naver.com\");else window.open(\"https://www.naver.com\");'>워크벤치</a></p>";
            html += blindContent;
            html += "       <div class='etcBtn'>";
            html += "           <p class='float_left'>";
            //html += "               <p class='favoriteArea' onclick='javascript:setContentFavorite(\""+list[i].contentId+"\")'><span class='contentFavorite'><img src='/img/like.png' alt='좋아요' width='21px' height='21px'/></span><span style='font-size:12px;'>" + list[i].replyCount + "</span></p>";
            html += "               <p class='replyArea' onclick='javascript:setContentReply(\""+list[i].contentId+"\",this)'><span class='contentReply'><img src='/img/reply.png' alt='댓글' width='20px' height='20px' style='vertical-align: middle;'/></span><span class='replyCnt' style='font-size:12px;'>" + list[i].replyCount + "</span></p>";
            html += "           </p>";
            html += "         <p class='float_right' onclick='javascript:getContentReadUser(\""+list[i].contentId+"\");'><img src='/img/success.png' alt='열람수' height='25px'/><span class='readCnt' value='"+ contentReadUserCnt +"'>" + contentReadUserCnt + "</span></p>";
            //html += "           <p class='float_right'><img src='/img/success.png' alt='열람수' height='25px'/><span class='readCnt' value='"+ contentReadUserCnt +"'>" + contentReadUserCnt + "</span></p>";
            html += "       </div>";
            html += "   </div>";
            html += "</section>";
        }
    }

    $("#container").append(html);
    scroll = "0";
}

function getContentReadUser(contentId) {
    location.href = "/getcontentreaduser/"+viewBoardId+"/"+contentId;
}

function GetScrollContentList(boardId) {
    if(scroll == "1") return;

    if(viewBoardId != boardId) {
        page = 0;
        lastContentFlag = 0;
        viewBoardId = boardId;
        $("#container").children().remove();
        $("#container").scrollTop(0);
    } else {
        page++;
    }

    $(".popupArea").hide();
    $("#boardNav").children("span").removeClass("on");
    $("#boardNav").children("span."+boardId).addClass("on");

    $.ajax({
        url: "getcontentlist",
        beforeSend: function (xhr) {
          xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        contentType:"application/x-www-form-urlencoded; charset=UTF-8",
        type: "POST",
        data: "boardId="+boardId+"&page="+page+"&size="+size,
        dataType: "json",
        success: function(data) {
            bindContentList(data);
        },
        error: function(error) {
            console.log(error);
        }
    });
}

function GetContentList(boardId) {
    //console.log("GetContentList : "+boardId);
    if(boardId == "") boardId = "b-1";
    scroll = "1";

    page = 0;
    lastContentFlag = 0;
    viewBoardId = boardId;
    $("#container").children().remove();
    $("#container").scrollTop(0);

    $(".popupArea").hide();
    $("#boardNav").children("span").removeClass("on");
    $("#boardNav").children("span."+boardId).addClass("on");

    $.ajax({
        url: "getcontentlist",
        beforeSend: function (xhr) {
          xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        contentType:"application/x-www-form-urlencoded; charset=UTF-8",
        type: "POST",
        data: "boardId="+boardId+"&page="+page+"&size="+size,
        dataType: "json",
        success: function(data) {
            bindContentList(data);
        },
        error: function(error) {
            console.log(error);
        }
    });
}

function goWrite(){
    location.href = "contentwrite/"+viewBoardId;
}

function setContentFavorite(contentId){
    //alert("좋아요 : " + contentId);
}

function setContentReply(contentId,node){
    $(".popupArea").hide();
    viewContentId = contentId;
    var section = $(node).parent().parent();
    var scrollPosition = $("#container").scrollTop();
    var offset = section.offset();
    $("#container").animate({scrollTop: scrollPosition + offset.top - 105},400);
    $("#contentReply").css("display","block").css("position","fixed").css("top","50%");

    setContentReplyList(contentId);
}

function setContentReplyList(contentId){
    onContentReply = "1";
    $(".replyList").children().remove();
    $(".replyInput").val("");
    $.ajax({
        url: "contentreply/list",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        contentType:"application/x-www-form-urlencoded; charset=UTF-8",
        type: "POST",
        data: "boardId="+viewBoardId+"&contentId="+contentId+"&userId="+userId,
        dataType: "json",
        success: function(data) {
            var html = "";
            var list = data.list;

            $("#detailContent .replyCnt").text(list.length);
            $("section."+contentId).find(".replyCnt").text(list.length);

            if(list.length == 0) {
                html += "<ul>";
                html += "   <li>";
                html += "       <p style='text-align:center;'>작성된 댓글이 없습니다.</p>";
                html += "   </li>";
                html += "</ul>";
            } else {
                for(var i = 0; i < list.length; i++) {
                    html += "<ul>";
                    html += "   <li>";
                    html += "       <span class='userpic' style='vertical-align:middle;'><img style='border-radius:25px;' src=\""+ photoUrl+"/"+ list[i].userId + "/25/25" +"\" alt='사용자 사진'/></span>";
                    html += "       <label>" + list[i].userName + "<span style='font-size:10px;color:#aaa;margin-left:10px;'>" + list[i].registDate + "</span></label>";
                    html += "       <p>" + list[i].reply + "</p>";
                    html += "   </li>";
                    html += "</ul>";
                }
            }

            $(".replyList").append(html);
        },
        error: function(error) {
            console.log(error);
        }
    });
}

function setReply(){
    var reply = $(".replyInput").val();

    if(reply=="" || reply==null){
        //alert("내용을 입력해주세요.");
        Swal.fire({
            title: "내용을 입력해주세요.",
            confirmButtonColor: "#002bff",
            confirmButtonText: "닫기"
        });
        return;
    }

    $.ajax({
        url: "contentreply/upload",
        contentType:"application/json; charset=UTF-8",
        type: "POST",
        beforeSend: function (xhr) {
          xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        data : JSON.stringify(new Reply(viewBoardId,viewContentId,reply)),
        dataType: "json",
        success: function(data) {
            if(data == "0") {
                setContentReplyList(viewContentId);
            }
            else console.log("업로드 실패");
        },
        error: function(error) {
            console.log(error);
        }
    });

    $(".replyList").animate({scrollTop: 99999999},300);
}

function Reply(boardId,contentId,reply)
{
    this.boardId = boardId;
    this.contentId = contentId;
    this.reply = reply;
}

function setReadContent(contentId,node){
    //$(node).parent().css("max-height","none");
    $(node).fadeOut();

    var readCnt = $(node).parent().find(".readCnt").text();
    $(node).parent().find(".readCnt").text(Number(readCnt)+1);

    $.ajax({
        url: "/setreadcontent/"+viewBoardId+"/"+contentId,
        beforeSend: function (xhr) {
          xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        contentType:"application/x-www-form-urlencoded; charset=UTF-8",
        type: "POST",
        dataType: "json",
        success: function(data) {
            if(data != "0") console.log("열람 실패");
        },
        error: function(error) {
            console.log(error);
        }
    });
}

function closeContentReply() {
    onContentReply = "0";
    $("#contentReply").css("display","none");
}

function goDeleteContent(contentId) {
    /*var result = confirm("삭제 하시겠습니까?");
    if(!result) return;*/

    Swal.fire({
        title: '삭제 하시겠습니까?',
        showCancelButton: true,
        confirmButtonColor: '#002bff',
        cancelButtonText: '취소',
        confirmButtonText: '승인',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: "/deletecontent",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization","BEARER " + token);
                },
                contentType:"application/x-www-form-urlencoded; charset=UTF-8",
                type: "POST",
                data: "boardId="+viewBoardId+"&contentId="+contentId,
                dataType: "json",
                success: function(data) {
                    if(data == "0") {
                        location.href = "/?userId="+userId+"&boardId="+viewBoardId;
                    }
                    else console.log("삭제 실패");
                },
                error: function(error) {
                    console.log(error);
                }
            });
        }
    });
}

function goModifyContent(contentId) {
    location.href = "contentwrite/modify/"+viewBoardId+"/"+contentId;
    /*var client = new XMLHttpRequest();
    client.open("POST", "/contentwrite/modifytest/",true);
    client.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
    client.setRequestHeader("Authorization", token);
    client.send("boardId="+viewBoardId+"&userId="+userId+"&contentId="+contentId);*/
}

function goFuntionList(contentId,node) {
    $(".popupArea").hide();
    var section = $(node).parent().parent().parent();
    var scrollPosition = $("#container").scrollTop();
    var offset = section.offset();
    $("#container").animate({scrollTop: scrollPosition + offset.top - 57},400);
    $("#contentFunction").css("display","block").css("position","fixed").css("top","70%");

    $(".delete").on("click",function(){
        goDeleteContent(contentId);
    });
    $(".modify").on("click",function(){
        goModifyContent(contentId);
    });
}

function closeContentFunction() {
    $("#contentFunction").css("display","none");
}

function showDetailContent(contentId, node) {

    if(mobileDevice) PSS.showDetailContent("1");

    $("#detailContent").children().remove();
    //$(".popupArea").hide();
    $("#contentFunction").hide();
    var section = $(node).parent("section").clone();
    $("#detailContent").css("display","block");
    $("#detailContent").animate({left:"0"},{duration: 300});
    $("#write").hide();
    $("#detailContent").append($(section));
    $("#detailContent").children("section").children(".contentdata").attr("onclick","");
    $("#detailContent").children("section").children(".contentdata").children(".moreBtn").remove();
    $("#detailContent").children("section").children(".userdata").append("<span style='float:left;width:20px;height:20px;margin-top:5px;' onclick='javascript:hideDetailContent();'><img src='/img/back.png' alt='뒤로가기'></span>");
}

function hideDetailContent() {
    if(mobileDevice) PSS.showDetailContent("0");
    $("#detailContent").animate({left:"100%"},{duration: 300});
    $("#write").show();
    $(".popupArea").hide();
}

function hideBigPicture() {
    if(mobileDevice) PSS.showBigPicture("0");
    $(".bigPictureWrapper").hide();
}

function showImage(fileCallPath) {
    if(mobileDevice) PSS.showBigPicture("1");
/*
    $(".bigPictureWrapper").css("display","flex").show();
    $(".bigPicture")
        .html("<img src='"+fileCallPath+"' >")
        .show({width:"100%", height: "100%"});
*/
    sendPost("/imgViewer",{imgId:fileCallPath});
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

$(document).ready(function(){
    //if(userId==null) location.href = "/not.html";
    PSS.getToken();
    if(viewSize != "" || viewSize != null) size = viewSize;
    //bindBoardList(boardList);
    $(".replyClose").on("click",function(){
        closeContentReply();
    });

    $(".functionClose").on("click",function(){
        closeContentFunction();
    });

    $("#detailContent").on("click",".contentdata > p > img",function(){
        var path = $(this).attr("src");
        showImage(path);
    });

    $(".bigPictureClose").on("click", function(e){
        $(".bigPicture").show({width:"0%", height: "0%"});
        setTimeout(function(){
            hideBigPicture();
        });
    });

    var startX,startY, endX,endY;
    $("#container").on("touchstart",function(event){
        startX = event.originalEvent.changedTouches[0].screenX;
        startY = event.originalEvent.changedTouches[0].screenY;
    });

    $("#container").on("touchend",function(event){
        endX=event.originalEvent.changedTouches[0].screenX;
        endY=event.originalEvent.changedTouches[0].screenY;

        if(startX-endX > 100 && Math.abs(startY-endY) < 50){
            var offset = $("#boardNav").children("span.on").offset();
            $("#boardNav").animate({scrollLeft: offset.left},400);
            $("#boardNav").children("span.on").next().click();
        }

        if(startX-endX < -100 && Math.abs(startY-endY) < 50){
            var offset = $("#boardNav").children("span.on").offset();
            $("#boardNav").animate({scrollLeft: offset.left},400);
            $("#boardNav").children("span.on").prev().click();
        }
    });

    $("#detailContent").on("touchstart",function(event){
        startX = event.originalEvent.changedTouches[0].screenX;
        startY = event.originalEvent.changedTouches[0].screenY;
    });

    $("#detailContent").on("touchend",function(event){
        endX=event.originalEvent.changedTouches[0].screenX;
        endY=event.originalEvent.changedTouches[0].screenY;

        if(startX-endX < -200){
            hideDetailContent();
        }
    });

    $("#container").scroll( function() {
        var container = $("#container");
        var scrollPosition = Math.floor(container.prop("scrollHeight") - container.scrollTop());
        var containerHeight = Math.floor(container.outerHeight());
        if(lastContentFlag == 0 && scrollPosition <= containerHeight + 5) {
           GetScrollContentList(viewBoardId);
           scroll = "1";
        }
    });

    $(".replyInput").keyup(function(e){
        if(e.which === 13) {
            setReply();
        }
    });

    checkMobile();
});

function checkMobile() {
    if( ['Win16','Win32','Win64','Mac','MacIntel','iPhone'].find(element => element == navigator.platform) ) {
        mobileDevice=false;
    } else {
        PSS.showDetailContent("0");
        PSS.showBigPicture("0");
        mobileDevice=true;
    }
}

$(window).resize(function() {
    if($(window).width() + $(window).height() != originalSize) {
        $(".replyList").css("height","50%");
    } else {
        $(".replyList").css("height","75%");
    }
});