package com.mcx.gaussprivilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mcx.gaussprivilege.dao.TbClassMapper;
import com.mcx.gaussprivilege.entity.TbClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbClassService {
    @Autowired
    private TbClassMapper tbClassMapper;
    public List<TbClass> query(){
        QueryWrapper<TbClass> wrapper = new QueryWrapper<>();
        return this.tbClassMapper.selectList(wrapper);
    }
}
