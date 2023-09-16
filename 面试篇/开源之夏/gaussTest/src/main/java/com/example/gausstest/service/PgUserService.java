package com.example.gausstest.service;

import com.example.gausstest.dao.PgUserMapper;
import com.example.gausstest.entity.PgUser;
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