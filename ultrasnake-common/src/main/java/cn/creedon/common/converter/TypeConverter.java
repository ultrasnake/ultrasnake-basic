package cn.creedon.common.converter;

public interface TypeConverter<S, T> {

    T convert(S source);

}