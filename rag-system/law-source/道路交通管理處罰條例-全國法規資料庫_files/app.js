$(function () {
    //依據螢幕寬度設定顯示模式
    SetShowMode();
    $(window).resize(function () { SetShowMode(); });


    //網頁縮放
    var WordSize = $.cookie("WordSize");
    if (WordSize != null && WordSize != "")
        SetWordSize(WordSize);

    $(".wordsize a").on("click", function (e) {
        var size = $(this).data("size");
        SetWordSize(size);
    });

    //回頁首
    $("#gotop").on("click", function () {
        $('html, body').animate({ scrollTop: $('#AC').offset().top }, 0);
        document.getElementById('AC').focus();
        return false;
    });
    $(window).scroll(function () {
        if ($(this).scrollTop() > 300) {
            $('#gotop').fadeIn("fast");
        } else {
            $('#gotop').stop().fadeOut("fast");
        }
    });
});

//依據螢幕寬度設定顯示模式
function SetShowMode() {
    if ($("#plLeftCount").length == 0 && $("#plRightList").length == 0) return;

    //var mode = $.cookie("showmode");
    var mode = ($(window).width() < 767) ? "1" : "";

    // 若 cookie 已正確存在就不再傳送
    //if (document.cookie.indexOf("showmode=") === -1) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "/SetShowMode.ashx?mode=" + mode, true);
    //}

    xhr.onreadystatechange = function () {
        if ($(window).width() < 767) {
            //20220726 資安修補，showmode cookie 加上 secure flag
            //$.cookie("showmode", "1", { path: '/;SameSite=Strict', secure: true });
            //document.cookie = "showmode=1; path=/; SameSite=Strict; Secure";

            $("#plLeftCount a").each(function (index) {
                var url = $(this).attr("href").replace("?mo=1", "").replace("&mo=1", "");
                try {
                    var separator = url.indexOf('?') !== -1 ? "&" : "?";
                    url = url + separator + "mo=1";
                    $(this).attr("href", url);
                } catch (err) {
                    alert(err.message);
                }
            });
            if (mode == "") window.location.reload();
        }
        else {
            //20220628 資安修補，showmode cookie 加上 secure flag
            //$.cookie("showmode", "", { path: '/;SameSite=Strict', secure: true });
            //document.cookie = "showmode=; path=/; SameSite=Strict; Secure";

            $("#plLeftCount a").each(function (index) {
                var url = $(this).attr("href");
                $(this).attr("href", url.replace("&mo=1", ""));
            });
            if (mode == "1") window.location.reload();
        }
    };

    xhr.send();
}

//關鍵字詞推薦
function BindAutocomplete(orl, stype) {
    $(orl).autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "../controls/GetTermList.ashx",
                type: 'post',
                dataType: "json",
                data: {
                    term: request.term, type: stype
                },
                success: function (data) {
                    response(data);
                }
            });
        },
        minLength: 1, //至少輸入幾個字元才開始給提示
        focus: function (event, ui) {
            return false;
        },
        select: function (event, ui) {
            if ($.trim(ui.item.PCODE).length == 0)
                $(this).val(ui.item.TERM);
            else
                location.href = "../Hot/AddHotLaw.ashx?pcode=" + encodeURIComponent(ui.item.PCODE);

            return false;
        },
        messages: { noResults: '', results: function () { } }//不顯示結果文字
    }).data("ui-autocomplete")._renderItem = function (ul, item) {
        if ($.trim(item.PCODE) != "")
            return $("<li class='line'>")
                .append("<span class='label label-info'>直接連結</span><a>" + item.TERM + "</a>")
                .appendTo(ul);
        else {
            if (ul.find(".label-usual").length == 0)
                return $("<li>")
                    .append("<span class='label label-info label-usual'>常用詞彙</span><a>" + item.TERM + "</a>")
                    .appendTo(ul);
            else
                return $("<li>")
                    .append("<a>" + item.TERM + "</a>")
                    .appendTo(ul);
        }
    };
}

//網頁縮放
function SetWordSize(value) {

    var value = HTMLEncode(value);

    //$.cookie("WordSize", value, { path: '/;SameSite=Strict', expires: 1, secure: true });
    $("body").css('zoom', value);

    $(".wordsize li").removeClass("active");
    $(".wordsize a[data-size='" + value + "']").parent("li").addClass("active");

}
//取得網址後的參數
function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    return getAssignUrlParameter(sPageURL, sParam);
}
function getAssignUrlParameter(sPageURL, sParam) {
    var sURLVariables = sPageURL.split('&');
    var vValue = "";
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            vValue = sParameterName[1];
            break;
        }
    }
    return vValue.replace("undefined", "");
}
//功能說明：是否符合EMail格式
//參數說明：字符串
//返 回 值：bool,返回：符合EMail格式true,否則返回：false
function IsEMail(str) {
    var reg = /^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
    return IsCheck(str, reg)
}
//功能說明：檢證函數
//參數說明：str=字串，reg=正則表達式
function IsCheck(str, reg) {
    str = $.trim(str);
    if (!reg.test(str)) {
        return false;
    }
    return true;
}
//分享至各社群
function shareTo(sharetype, dataurl) {
    var shareurl = "";
    switch (sharetype) {
        case "FB":
            shareurl = "http://www.facebook.com/sharer/sharer.php?u=";
            break;
        case "TWITTER":
            shareurl = "http://twitter.com/home/?status=";
            break;
        case "PLURK":
            shareurl = "http://www.plurk.com/?qualifier=shares&status=";
            break;
        case "GOOGLEPLUS":
            shareurl = "https://plus.google.com/share?url=";
            break;
        case "LINE":
            shareurl = "http://line.naver.jp/R/msg/text/?";
            break;
        default:
            break;
    }
    shareurl += dataurl;
    return shareurl;
}

////google分析工具(註解舊版引用方式，global.master已採用新版)
//var _gaq = _gaq || [];
//_gaq.push(['_setAccount', 'UA-30636191-1']);//正式
////_gaq.push(['_setAccount', 'UA-30618261-3']);//測試
//_gaq.push(['_trackPageview']);
//$(function () {
//    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
//    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
//    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
//});
function GoogleAnanlytice(g, a, l, v) {
    if (g == "中文")
        g = "Chinese";
    if (g == "英文")
        g = "English";

    if (l == "" && v == "") {
        //_gaq.push(['_trackEvent', g, a]);
        //alert('1:' + g + ',' + a);

        gtag('event', 'click', {
            'event_category': g,
            'event_name': a
        });

    }
    if (l != "" && v == "") {
        //_gaq.push(['_trackEvent', g, a, l]);
        //alert('2:' + g + ',' + a + ',' + l);

        gtag('event', 'click', {
            'event_category': g,
            'event_name': a,
            'event_label': l
        });
    }

}

function HTMLEncode(html) {
    var temp = document.createElement("div");
    (temp.textContent != null) ? (temp.textContent = html) : (temp.innerText = html);
    var output = temp.innerHTML;
    temp = null;
    return output;
}

function HTMLDecode(text) {
    var temp = document.createElement("div");
    temp.innerHTML = text;
    var output = temp.innerText || temp.textContent;
    temp = null;
    return output;
}


