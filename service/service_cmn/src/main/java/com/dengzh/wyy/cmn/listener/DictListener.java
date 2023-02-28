package com.dengzh.wyy.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.dengzh.wyy.cmn.mapper.DictMapper;
import com.dengzh.wyy.model.cmn.Dict;
import com.dengzh.wyy.model.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DictListener extends AnalysisEventListener<DictEeVo> {
    private DictMapper dictMapper;
    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    // 一行一行的读取
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        // 调用方法，添加数据到数据库
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict); // 将传来的dictEeVo对象一个个复制为dict对象
        dictMapper.insert(dict);// 插入到数据库
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
