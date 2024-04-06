package net.sourceforge.peers.ext.excel.parse;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataListener<T> extends AnalysisEventListener<T> {

    private final List<T> data = new ArrayList<>();

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        data.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<T> getData() {
        return data;
    }
}
