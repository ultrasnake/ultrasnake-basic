package cn.creedon.ns4j.model;

import lombok.Data;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：返回结果对象
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Data
public class Ns4jRR<T> {

    private Integer code;

    private String message;

    private T data;

    public Ns4jRR(Integer code, String message) {
        this(code, message, null);
    }

    public Ns4jRR(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Ns4jRR<Void> ok() {
        return ok(null);
    }

    public static <T> Ns4jRR<T> ok(T data) {
        return ok(Ns4jRREnum.SUCCESS.getMessage(), data);
    }

    public static <T> Ns4jRR<T> ok(String message, T data) {
        return new Ns4jRR<>(Ns4jRREnum.SUCCESS.getCode(), message, data);
    }

    public static Ns4jRR<Void> error500() {
        return error(Ns4jRREnum.SYSTEM_ERROR.getCode(), Ns4jRREnum.SYSTEM_ERROR.getMessage());
    }

    public static Ns4jRR<Void> error400(String message) {
        return error(Ns4jRREnum.BAD_REQUEST.getCode(), message);
    }

    public static Ns4jRR<Void> error(Integer code, String message) {
        return new Ns4jRR<>(code, message, null);
    }

}
