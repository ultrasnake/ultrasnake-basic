package cn.creedon.ns4j.http.multipart;

import lombok.Data;

import java.util.List;
import java.util.Map;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：多文件上传的对象
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Data
public class MultipartHttpRequest {

    private String rootPath;
    private Map<String, Object> parameters;
    private List<MultipartFile> multipartFiles;

}
