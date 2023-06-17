package com.mcxgroup.testmybatisplus.service.impl;

import com.mcxgroup.testmybatisplus.entity.TUser;
import com.mcxgroup.testmybatisplus.mapper.TUserMapper;
import com.mcxgroup.testmybatisplus.service.ITUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author c
 * @since 2023-06-17
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {

}
