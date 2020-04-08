package com.shanya.serialport.update;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

public class DownloadUtil {
    private Context context;
    private String downloadUrl;
    private String fileName;

    public DownloadUtil(Context context, String downloadUrl, String fileName) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
        download();
    }

    private void download() {
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir("/download/", fileName);
        //获取下载管理器
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
    }


}
