var mobileDevice = true;
var token = "";
var jwt = "";

function setContentUserOrg(node,deptId){
    var html = "<ol class=\""+deptId+"\" style='padding-left:10px;'>";
    for(var i = 0; i < node.length; i++){
        if(node[i].type=="dept") {
            html += "<li type='dept' style='color:red;' onclick='javascript:onDept(\""+node[i].deptId+"\")'>" + node[i].deptId + " " + node[i].deptName + "</li>";
            if(node[i].hasOwnProperty("children")){
                html += setContentUserOrg(node[i].children,node[i].deptId);
            }
        }
        else html += "<li type='user' style='color:blue;'>" + node[i].userId + " " + node[i].userName + "</li>";
    }
    html += "</ol>";

    return html;
}

function onDept(deptId){
    var selectDept = $("ol."+deptId);
    var childrenLength = selectDept.children("li").length;
    $("#user").children().remove();
    for(var i = 0; i < childrenLength; i++){
        $("#user").append(selectDept.children("li").eq(i).clone());
    }
}

function userSetting(node){
    var readCnt = 0;
    var html = "<ol>";
    for(var i = 0; i < node.length; i++) {
        html += "<li style='height:50px;size:20px;padding:10px 10px;vertical-align:middle;' onclick='javascript:if(mobileDevice)PSS.showUserInfo(\""+ node[i].userId +"\");'><img style='border-radius:20px;' src=\""+ photoUrl+"/"+ node[i].userId + "/40/40" +"\" alt='사용자 사진'/>&nbsp;&nbsp;"+node[i].userName + " " + node[i].posName;
        if(node[i].flag == "1") {
            html += "<span style='float:right;vertical-align:middle;position:relative;top:5px;'><img src='/img/success.png' height='20px;'/></span>";
            readCnt++;
        }
        else html += "<span style='float:right;vertical-align:middle;position:relative;top:5px;'><img src='/img/success1.png' height='20px;'/></span>";
        html += "</li>";
    }
    html += "</ol>";

    $("#userCount").text(readCnt + "/" + node.length);
    return html;
}

function orgSetting(node){
    var html = "<ol>";
    for(var i = 0; i < node.length; i++) {
        var imgCnt = i % 7;
        var color = ["#0072ff","#f3533b","#25c77a","#f93aba","#f6a10d","#5f6469","#863af9"];
        html += "<li onclick='javascript:goDepth(\""+node[i].deptId+"\");' style='height:50px;size:20px;padding:10px 10px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;'><svg id='vector' style='vertical-align:middle;' xmlns='http://www.w3.org/2000/svg' width='36' height='36' viewBox='0 0 36 36'><path fill='"+color[imgCnt]+"' d='M18,18m-18,0a18,18 0,1 1,36 0a18,18 0,1 1,-36 0' id='path_0'/><path fill='#fff' d='M26,26h-16a2,2 0,0 1,-2 -2l0.011,-12a2,2 0,0 1,1.99 -2h6l2,2h8a2,2 0,0 1,2 2v10A2,2 0,0 1,26 26ZM18,19.5a3.834,3.834 0,0 0,-3.849 3.718v0.24a0.441,0.441 0,0 0,0.44 0.44h6.816a0.44,0.44 0,0 0,0.439 -0.44v-0.11l0,-0.13A3.832,3.832 0,0 0,18 19.5ZM18,15.1a1.924,1.924 0,1 0,1.924 1.924A1.927,1.927 0,0 0,18 15.1Z' id='path_1'/></svg>&nbsp;&nbsp;"+node[i].deptName;
        html += "</li>";
    }
    html += "</ol>";

    $("#orgCount").text(node.length);
    return html;
}

function orgDepthList(orgDepth) {
    var html = "";
    for(var i = 0; i < orgDepth.length; i++) {
        if(i == orgDepth.length - 1) html += "<p class='on' style='background-color:#666;border:1px solid #666;border-radius:15px;padding:3px;color:#fff;'>";
        else html += "<p class='parent' style='margin-bottom:10px;' onclick='javascript:goDepth(\""+orgDepth[i].deptId+"\")'>";
        html += orgDepth[i].deptName;
        html += "</p>";
        if(i != orgDepth.length - 1 ) html += "<p style='margin-bottom:10px;'><img src='/img/down-arrow.png' /></p>";
    }

    return html;
}

function goDepth(deptId) {
    $.ajax({
        url: "/getcontentreaduser/"+boardId+"/"+contentId+"/"+deptId,
        contentType:"application/x-www-form-urlencoded; charset=UTF-8",
        type: "POST",
        dataType: "json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization","BEARER " + token);
        },
        success: function(data) {
            boardId = data.boardId;
            contentId = data.contentId;
            userData = data.userData;
            deptData = data.deptData;
            orgDepth = data.orgDepth;

            $("#orgList").children().remove();
            $("#orgList").append(orgSetting(deptData));
            $("#userList").children().remove();
            $("#userList").append(userSetting(userData));
            $("#orgDepthList").children().remove();
            $("#orgDepthList").append(orgDepthList(orgDepth));
        },
        error: function(error) {
            console.log(error);
        }
    });
}

function goUserInfo() {
    if(mobileDevice) PSS.showUserInfo(photoId);
}

function goParentDepth() {
    var depthLength = $("#orgDepthList").children("p").length;
    if(depthLength > 1) $("#orgDepthList").children("p.parent").last().trigger("click");
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

function setMyPhoto() {
    $("#myPhoto").attr("src",photoUrl+"/"+photoId+"/60/60");
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

$(document).ready(function(){
    checkMobile();
    PSS.getToken();
    $("#orgList").append(orgSetting(deptData));
    $("#userList").append(userSetting(userData));
    $("#orgDepthList").append(orgDepthList(orgDepth));
    setMyPhoto();
});