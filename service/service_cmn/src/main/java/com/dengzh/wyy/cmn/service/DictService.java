package com.dengzh.wyy.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dengzh.wyy.model.cmn.Dict;

import java.util.List;


public interface DictService extends IService<Dict> {

    List<Dict> findChildData(Long id);
}
