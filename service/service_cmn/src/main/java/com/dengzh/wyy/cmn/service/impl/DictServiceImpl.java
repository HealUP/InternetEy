package com.dengzh.wyy.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dengzh.wyy.cmn.listener.DictListener;
import com.dengzh.wyy.cmn.mapper.DictMapper;
import com.dengzh.wyy.cmn.service.DictService;
import com.dengzh.wyy.model.cmn.Dict;
import com.dengzh.wyy.model.vo.cmn.DictEeVo;
import com.sun.deploy.net.URLEncoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dengzh.wyy.common.config.RedisConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
    @Autowired
    private DictMapper dictMapper;

    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
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

    /**
     * 导出
     * @param response
     */
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
            // 查询数据库
            List<Dict> dictList = baseMapper.selectList(null);
            // Dict -> DictEeVo
            List<DictEeVo> dictVoList = new ArrayList<>();
            for(Dict dict : dictList) {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictEeVo);// 复制对象
                dictVoList.add(dictEeVo);
            }
            // 调用方法进行 写操作
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 导入数据字典  导入新的数据时，清空缓存
    @CacheEvict(value = "dict", allEntries=true)
    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(dictMapper))
                    .sheet().doRead(); // new一个监听器
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 判断id下面是否包含子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(queryWrapper);// 统计根据id找到的parent_id的数量 根据数量是否大于0来判断是否存在子节点
        return  count > 0; // 上一步count < 0 则为false
    }
}
