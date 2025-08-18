# 云图备份系统 API 文档使用说明

## 启动项目后访问API文档

1. 启动Spring Boot应用
2. 访问地址：`http://localhost:8101/api/doc.html`

## 接口认证说明

### 无需认证的接口
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录

### 需要JWT认证的接口
- `GET /api/user/info` - 获取当前用户信息
- `POST /api/user/cancellation` - 注销用户
- `GET /api/user/detail/vo` - 查询用户详情(脱敏)

### 需要管理员权限的接口
- `POST /api/user/list` - 获取用户列表
- `POST /api/user/add` - 创建用户
- `GET /api/user/detail` - 查询用户详情(未脱敏)
- `PUT /api/user/update` - 修改用户
- `DELETE /api/user/{userId}` - 删除用户

## 请求头配置

### 全局请求头
- `Authorization`: JWT认证token (格式：Bearer {token})
- `Content-Type`: 请求内容类型 (默认：application/json)

### 使用方式
1. 在Knife4j文档页面右上角点击"Authorize"按钮
2. 输入你的JWT token
3. 点击"Authorize"确认
4. 之后的所有请求都会自动带上Authorization请求头

## 测试流程

1. 先调用注册接口创建用户
2. 调用登录接口获取JWT token
3. 在文档中配置Authorization请求头
4. 测试其他需要认证的接口

## 注意事项

- 除了登录和注册接口，其他所有接口都需要JWT认证
- 管理员权限接口需要用户角色为admin
- 所有请求都会经过JWT拦截器验证
- 如果token无效或过期，会返回401错误
