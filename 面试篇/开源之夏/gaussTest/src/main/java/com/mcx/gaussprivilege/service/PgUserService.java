package com.mcx.gaussprivilege.service;

import com.mcx.gaussprivilege.dao.PgUserMapper;
import com.mcx.gaussprivilege.entity.PgUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PgUserService {
    @Autowired
    private PgUserMapper pgUserMapper;
    public List<PgUser> getAllUsers() {
        return pgUserMapper.getAllUsers();
    }
}