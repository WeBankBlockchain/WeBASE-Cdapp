/**
 * Copyright 2014-2020  the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.certapp.service;

import com.certapp.common.code.ConstantCode;
import com.certapp.common.exception.BaseException;
import com.certapp.dao.entity.TbUser;
import com.certapp.dao.mapper.TbUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class LoginService {

    @Autowired
    private TbUserMapper tbUserMapper;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public TbUser login(String name, String pwd) {
        log.debug("start login. name: {}", name);
        TbUser user = tbUserMapper.selectUserByName(name);
        if (user != null) {
            if (!passwordEncoder.matches(pwd, user.getPwd())) {
                throw new BaseException(ConstantCode.PASSWORD_ERROR);
            }
        }
        log.debug("end login. user:{}", user);
        return user;
    }

    public int updatePassword(int id, String newPwd, String oldPwd) {
        log.debug("start updatePassword.");
        TbUser user = tbUserMapper.selectByPrimaryKey(id);
        if (StringUtils.equals(oldPwd, newPwd)) {
            log.warn("fail updatePassword. the new password cannot be same as old ");
            throw new BaseException(ConstantCode.NEW_PWD_EQUALS_OLD);
        }
        // check old password
        if (!passwordEncoder.matches(oldPwd, user.getPwd())) {
            throw new BaseException(ConstantCode.PASSWORD_ERROR);
        }
        // update password
        user.setPwd(passwordEncoder.encode(newPwd));
        Integer affectRow = tbUserMapper.updateByPrimaryKey(user);
        log.debug("end updatePassword. user:{}", user);
        return affectRow;
    }
}
