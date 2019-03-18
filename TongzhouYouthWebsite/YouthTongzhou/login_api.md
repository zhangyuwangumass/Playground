# API文档

## 登陆与注册

### post /login/login
- 传参：username, password, type
- 实现功能：登录，成功跳转/login/profile，不成功跳转/login/

### post /login/getVerifyCode
- 传参：username
- 实现功能：向用户发送验证码短信

### post /login/register
- 传参：username，password，verifycode, type
- 实现功能：验证verifycode并注册用户。成功跳转/login/profile，不成功跳转/login/
