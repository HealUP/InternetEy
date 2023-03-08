package com.dengzh.wyy.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface DictService extends IService<Dict> {

    List<Dict> findChildData(Long id);

    // 导出字典
    void exportData(HttpServletResponse response);

    // 导入字典表格
    void importDictData(MultipartFile file);

    // 获取数据字典名称
    String getDictName(String dictCode, String value);

    // 根据dictCode获取下级节点
    List<Dict> findByDictCode(String dictCode);
}
