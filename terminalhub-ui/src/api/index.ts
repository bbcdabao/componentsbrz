import request from '../utils/request';

/*
url: The requested server URL. Just need to be specified in a separate request.
method: Request method, the default is GET.
baseURL: Concatenate baseURL and url into a complete URL. If url is an absolute URL, it will not be concatenated.
headers: Custom HTTP headers.
params: URL parameters will be spliced ​​to the end of the URL.
data: Seeking volume data is only applicable to PUT, POST, PATCH and other methods.
timeout: Specifies the request timeout in milliseconds.
withCredentials: Whether credentials are required for cross-domain requests.
responseType: The expected data type of the server response. Commonly used ones include json, text, blob, and arraybuffer.
auth: HTTP Basic Authentication { username: '...', password: '...' }.
proxy: 
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
