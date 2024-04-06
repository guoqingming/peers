package net.sourceforge.peers.ext.excel.parse;

import com.alibaba.excel.EasyExcel;

import java.util.List;

public class ExcelUtils {

    /**
     * 获取excel数据
     * @param path
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> getRows(String path,final Class<?> tClass) {
        DataListener<T> dataListener = new DataListener<>();
        EasyExcel.read(path, tClass,dataListener ).doReadAll();
        return dataListener.getData();
    }
}
