package com.example.gausstest.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.gausstest.dao.TbClassMapper;
import com.example.gausstest.entity.TbClass;
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
