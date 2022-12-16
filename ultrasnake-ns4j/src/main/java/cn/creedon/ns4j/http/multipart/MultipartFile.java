package cn.creedon.ns4j.http.multipart;

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：请在此处加入功能描述
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Data
public class MultipartFile {

    private String fileName;
    private String contentType;
    private long fileLength;
    private byte[] fileByte;

    public void transferTo(String fileDir, String fileName) throws Exception {
        File file = new File(fileDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        InputStream in = new ByteArrayInputStream(fileByte);
        file = new File(fileDir, fileName);
        FileOutputStream fos = new FileOutputStream(file);

        byte[] b = new byte[1024];
        int read = 0;
        while ((read = in.read(b)) != -1) {
            fos.write(b, 0, read);
        }
        fos.flush();
        if (fos != null) {
            fos.close();
        }
        if (in != null) {
            in.close();
        }
    }
}
