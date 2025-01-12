package com.sl.controller.admin;

import com.sl.common.config.shiro.UserNamePasswordTimeToken;
import com.sl.common.out.ResultData;
import com.sl.common.out.ResultType;
import com.sl.common.util.JedisUtil;
import com.sl.common.util.JsonUtil;
import com.sl.common.util.TokenTools;
import com.sl.entity.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
public class LoginController {

    @RequestMapping("doLogin")
    public ResultData doLogin(String username, String password, Long timeStamp){
        Subject subject = SecurityUtils.getSubject();
        String tokenKey = null;
        UserNamePasswordTimeToken token = new UserNamePasswordTimeToken(username,password);
        try {
            subject.login(token);
            tokenKey = TokenTools.makeToken(timeStamp,username);
        }  catch (Exception e) {

        }
        String value = JedisUtil.get(tokenKey);
        ResultData resultData = new ResultData(ResultType.SUCCESS.getResultCode(),ResultType.SUCCESS.getMessage(),JsonUtil.fromJson(value, LoginUser.class));
        return resultData;
    }

    @RequestMapping("outLogin")
    public ResultData outLogin(String token){
        if(StringUtils.isNotBlank(token)){
            JedisUtil.deleteKey(token);
        }
        return new ResultData(ResultType.SUCCESS);
    }
}
