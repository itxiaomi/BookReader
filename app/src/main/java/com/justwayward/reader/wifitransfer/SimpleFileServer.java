package com.justwayward.reader.wifitransfer;

import android.text.TextUtils;

import com.justwayward.reader.bean.Recommend;
import com.justwayward.reader.bean.support.RefreshCollectionListEvent;
import com.justwayward.reader.manager.CollectionsManager;
import com.justwayward.reader.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Wifi传书 服务端
 *
 * @author yuyh.
 * @date 2016/10/10.
 */
public class SimpleFileServer extends NanoHTTPD {

    private static SimpleFileServer server;

    public static SimpleFileServer getInstance() {
        if (server == null) {
            server = new SimpleFileServer(Defaults.getPort());
        }
        return server;
    }

    public SimpleFileServer(int port) {
        super(port);
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> header, Map<String, String> parms,
                          Map<String, String> files) {
        if (Method.GET.equals(method)) {
            //return new Response(HtmlConst.HTML_STRING);
            if (uri.contains("index.html") || uri.equals("/")) {
                return new Response(Response.Status.OK, "text/html", new String(FileUtils.readAssets("/index.html")));
            } else {
                // 获取文件类型
                String type = Defaults.extensions.get(uri.substring(uri.lastIndexOf(".") + 1));
                if (TextUtils.isEmpty(type))
                    return new Response("");
                // 读取文件
                byte[] b = FileUtils.readAssets(uri);
                if (b == null || b.length < 1)
                    return new Response("");
                // 响应
                return new Response(Response.Status.OK, type, new ByteArrayInputStream(b));
            }
        } else {
            // 读取文件
            for (String s : files.keySet()) {
                try {
                    FileInputStream fis = new FileInputStream(files.get(s));
                    String fileName = parms.get("newfile");
                    if (fileName.lastIndexOf(".") > 0)
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));
                    // 创建文件
                    File outputFile = FileUtils.createWifiTranfesFile(fileName);
                    // 添加到收藏
                    addToCollection(fileName);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int byteRead = fis.read(buffer);
                        if (byteRead == -1) {
                            break;
                        }
                        fos.write(buffer, 0, byteRead);
                    }
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new Response("");
        }
    }

    /**
     * 添加到收藏
     *
     * @param fileName
     */
    private void addToCollection(String fileName) {
        Recommend.RecommendBooks books = new Recommend.RecommendBooks();
        books.isFromSD = true;

        books._id = fileName;
        books.title = fileName;

        CollectionsManager.getInstance().add(books);
        EventBus.getDefault().post(new RefreshCollectionListEvent());
    }
}
