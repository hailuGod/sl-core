package com.sl.service.impl;

import com.sl.common.exception.GlobalException;
import com.sl.common.i18n.LocaleMessageSource;
import com.sl.common.out.ResultType;
import com.sl.common.util.CommonUtil;
import com.sl.constant.CommonConstant;
import com.sl.constant.UserConstant;
import com.sl.dao.UserDao;
import com.sl.entity.LoginUser;
import com.sl.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserServiceImpl implements UserService, UserConstant {
    @Resource
    UserDao userDao;

    @Override
    public List<LoginUser> selectAll() {
        return userDao.selectAll();
    }

    /**
     * 根据用户名获取用户信息
     * @param userName
     * @return
     */

    @Override
    public LoginUser getUserByUserName(String userName) {
        return userDao.getUserByUserName(userName);
    }

    /**
     * 获取当前用户下的资源
     * @param userName
     * @return
     */
    @Override
    public LoginUser getRoleResourcesByUserName(String userName) {
        return userDao.getRoleResourcesByUserName(userName);
    }

    /**
     * 增加用户
     * @param user
     * @return
     */
    @Override
    public Integer addUser(LoginUser user) {
        validUser(user,CommonConstant.OPERATE_ADD);
        return userDao.addUser(user);
    }

    @Override
    public Integer updateUser(LoginUser user) {
        validUser(user,CommonConstant.OPERATE_UPDATE);
        return userDao.updateUser(user);
    }

    @Override
    public Integer deleteUserByUserName(String userName) {
        //判断用户名是否已存在
        Integer count =userDao.deleteUserByUserName(userName);
        if(count<1){
            throw  new GlobalException(ResultType.FAIL.getResultCode(), LocaleMessageSource.getMessage(USER_NAME_NOT_EXIST));
        }
        return count;
    }

    /**
     * 检验新增用户属性,初始化基本属性
     * @param user
     */
    private void validUser(LoginUser user,Integer operateType){
        if(null == user){
            throw  new GlobalException(ResultType.FAIL.getResultCode(), LocaleMessageSource.getMessage(USER_ADD_NULL));
        }
        // 用户名长度是否符合
        if(StringUtils.isBlank(user.getUserName()) || user.getUserName().length() >USER_NAME_MAX_LENGTH){
            throw  new GlobalException(ResultType.FAIL.getResultCode(), LocaleMessageSource.getMessage(USER_NAME_VALID));
        }
        //密码不能为空(至少8-16个字符，至少1个大写字母，1个小写字母和1个数字)
        if(StringUtils.isBlank(user.getPassword()) || !CommonUtil.validPassword(user.getPassword())){
            throw  new GlobalException(ResultType.FAIL.getResultCode(), LocaleMessageSource.getMessage(USER_PASSWORD_VALID));
        }
        // 邮箱校验
        if(StringUtils.isEmpty(user.getEmail()) || !CommonUtil.isEmail(user.getEmail())){
            throw  new GlobalException(ResultType.FAIL.getResultCode(), LocaleMessageSource.getMessage(USER_EMAIL_VALID));
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(CommonConstant.OPERATE_ADD == operateType){
            //判断用户名是否已存在
            LoginUser oldUser = this.getUserByUserName(user.getUserName());
            if(null != oldUser){
                throw  new GlobalException(ResultType.FAIL.getResultCode(), LocaleMessageSource.getMessage(USER_NAME_EXIST));
            }
            user.setCreateId(loginUser.getId());
            user.setUpdateId(loginUser.getId());
        }else if(CommonConstant.OPERATE_UPDATE == operateType){
            user.setUpdateId(loginUser.getId());
        }

    }
}
