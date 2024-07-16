import request from '../utils/request';

/*
url: 请求的服务器 URL。只需要在单独的请求中指定。
method: 请求方法，默认是 GET。
baseURL: 将 baseURL 和 url 拼接成完整的 URL，如果 url 是绝对 URL 则不会拼接。
headers: 自定义的 HTTP 头。
params: URL 参数，会拼接到 URL 后面。
data: 请求体数据，只适用于 PUT、POST、PATCH 等方法。
timeout: 指定请求的超时时间（毫秒）。
withCredentials: 跨域请求时是否需要使用凭证。
responseType: 期望的服务器响应的数据类型，常用的有 json、text、blob、arraybuffer。
auth: HTTP 基本认证 { username: '...', password: '...' }。
proxy: 代理配置 { host: '127.0.0.1', port: 9000 }。
*/

export const loginpost = (body : any = {}) => {
    return request({
        url: '/api/login',
        method: 'post',
        data: body
    });
};

export const fetchData = () => {
    return request({
        url: './mock/table.json',
        method: 'get'
    });
};

export const fetchUserData = () => {
    return request({
        url: './mock/user.json',
        method: 'get'
    });
};

export const fetchRoleData = () => {
    return request({
        url: './mock/role.json',
        method: 'get'
    });
};
