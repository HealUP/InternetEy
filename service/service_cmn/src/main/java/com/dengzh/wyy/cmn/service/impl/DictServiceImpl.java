package com.dengzh.wyy.cmn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dengzh.wyy.cmn.mapper.DictMapper;
import com.dengzh.wyy.cmn.service.DictService;
import com.dengzh.wyy.model.cmn.Dict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* Description: 查找数据
* date: 2023/2/22 10:54
 *
* @author: Deng
* @since JDK 1.8
*/
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public List<Dict> findChildData(Long id) {
        // 根据id找到数据列表对象 构造条件对象
        QueryWrapper<Dict> querryWrapper = new QueryWrapper<>();
        querryWrapper.eq("parent_id", id);// 对应数据库字段
        List<Dict> dictList = baseMapper.selectList(querryWrapper);

        //向list集合每个dict对象中设置hasChildern
        for (Dict dict : dictList) {
            // 遍历列表中的每个对象
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId); // 调用判断是否包含子节点的方法
            dict.setHasChildren(isChild);// 给每个Dict对象设置hasChildren的值（true,false)
        }
        return dictList;
    }

    // 判断id下面是否包含子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(queryWrapper);// 统计根据id找到的parent_id的数量 根据数量是否大于0来判断是否存在子节点
        return  count > 0; // 上一步count < 0 则为false
    }
}
