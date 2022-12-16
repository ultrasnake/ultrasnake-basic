package cn.creedon.common.datasource;

import cn.creedon.common.converter.TypeConverter;
import cn.creedon.common.subject.DynamicPropertySubject;
import cn.creedon.common.subject.PropertySubject;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：抽象数据源
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public abstract class AbstractDataSource<S, T> implements ReadableDataSource<S, T> {

    protected final TypeConverter<S, T> parser;
    protected final PropertySubject<T> property;

    public AbstractDataSource(TypeConverter<S, T> parser) {
        if (parser == null) {
            throw new IllegalArgumentException("类型转换对象不能为空");
        }
        this.parser = parser;
        this.property = new DynamicPropertySubject<>();
    }

    @Override
    public T loadConfig() throws Exception {
        return loadConfig(readSource());
    }

    public T loadConfig(S conf) throws Exception {
        T value = parser.convert(conf);
        return value;
    }

    public PropertySubject<T> getProperty() {
        return property;
    }

    public abstract void loadInitialConfig();

}
