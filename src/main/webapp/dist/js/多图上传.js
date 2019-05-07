$(function () {
    /*init webuploader*/
    var $list = $("#previewList");
    var thumbnailWidth = 0.5;   //缩略图高度和宽度，当宽高度是0~1的时候，按照百分比计算
    var thumbnailHeight = 0.5;
    var uploader = WebUploader.create({
        auto: false,// 选完文件后，是否自动上传
        swf: 'plugins/webupload/Uploader.swf',// swf文件路径
        server: '/images',// 文件接收服务端url
        method: 'POST',// 服务端请求方法
        pick: '#fileUploader1',// 选择文件的按钮
        fileNumLimit: 10,//文件总数量只能选择10个,多于10个部分不会加入上传队列
        fileSizeLimit: 100 * 1024 * 1024,//验证文件总大小是否超出限制, 超出则不允许加入队列 100M
        fileSingleSizeLimit: 4 * 1024 * 1024,//验证单个文件大小是否超出限制, 超出则不允许加入队列 4M
        compress: false,//配置压缩的图片的选项。如果此选项为false, 则图片在上传前不进行压缩。
        threads: 4,//上传并发数,允许同时最大上传进程数,默认值为3
        accept: {//指定接受哪些类型的文件
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',// 只允许选择部分图片格式文件，可自行修改
            mimeTypes: 'image/*'
        },
    });
    //webuploader事件.当选择文件后，文件被加载到文件队列中，触发该事件
    uploader.on('fileQueued', function (file) {

        var $li = $(
            '<div class=\"col-md-4\">' +
            '<div id=\"' + file.id + '\"class=\"card\">' +
            '<div class=\"card-header\"' +
            '<h6 class=\"card-title text-danger\">' + file.name + '</h6>' +
            '</div>' +
            '<div class=\"card-body text-center\">' +
            '<span class=\"mailbox-attachment-icon has-img\">' +
            '<img>' +
            '</span></div></div></div>'
            ),
            $img = $li.find('img');
        $list.append($li);
        // 创建缩略图
        // 如果为非图片文件，可以不用调用此方法。
        uploader.makeThumb(file, function (error, src) {
            //webuploader方法
            if (error) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }
            $img.attr('src', src);
        }, thumbnailWidth, thumbnailHeight);
        $('#picturePathAll').val('qqq');
    });
    // 文件上传成功，标记上传成功。
    uploader.on('uploadSuccess', function (file, result) {
        if (result.resultCode == 200) {
            $('#' + file.id).append(' <div class="card-footer"><h6 class=\"card-title text-success\">上传成功</h6></div>');
            $("#imgResult").append('<p>' + result.data + '</p>');
        }
        var url = 'pictures/save';
        var pictureRemark = $("#pictureRemarkAll").val();
        var picturePath = result.data;
        var data = {"path": picturePath, "remark": pictureRemark};
        console.log(picturePath);
        console.log(pictureRemark);
        $.ajax({
            type: 'POST',//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: url,//url
            contentType: "application/json; charset=utf-8",
            beforeSend: function (request) {
                //设置header值
                request.setRequestHeader("token", getCookie("token"));
            },
            data: JSON.stringify(data),
            success: function (result1) {
                checkResultCode(result1.resultCode);
                if (result1.resultCode == 200) {
                    $('#pictureModalAll').modal('hide');
                    reload();
                }
                else {
                    $('#pictureModalAll').modal('hide');
                };
            },
            error: function () {
                swal('操作失败！',{
                    icon:'error',
                });
            }
        });
    });
    // 文件上传失败，显示上传出错。
    uploader.on('uploadError', function (file) {
        var $li = $('#' + file.id);
        $li.append(' <div class="card-footer"><h6 class=\"card-title text-danger\">上传失败</h6></div>');
    });
    $("#uploadAll").click(function () {
                $("#imgResult").html('');
                //文件处理时增加了alert事件,不需要的话自行删除即可
                uploader.upload();
        swal('上传完成',{
            icon:'success',
        });
        }
    )

});


function pictureAddAll() {
    $('#pictureModalAll').modal('show');

}


function reset() {
    //隐藏错误提示框
    $('.alert-danger').css("display", "none");
    //清空数据
    $('#picturePath').val('');
    $('#pictureRemark').val('');
    $("#img").attr("style", "display:none;");
}

/**
 * jqGrid重新加载
 */
function reload() {
    reset();
    var page = $("#jqGrid").jqGrid('getGridParam', 'page');
    $("#jqGrid").jqGrid('setGridParam', {
        page: page
    }).trigger("reloadGrid");
}
