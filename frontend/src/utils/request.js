import axios from 'axios'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    const normalizedToken = token?.trim()
    const anonymousPaths = ['/user/login', '/user/register']
    const isAnonymousRequest = anonymousPaths.some(path => config.url === path)

    if (!isAnonymousRequest &&
        normalizedToken &&
        normalizedToken !== 'null' &&
        normalizedToken !== 'undefined') {
        config.headers.Authorization = normalizedToken.startsWith('Bearer ')
            ? normalizedToken
            : `Bearer ${normalizedToken}`
    } else {
        delete config.headers.Authorization
    }
    return config
})

request.interceptors.response.use(
    res => res.data,
    err => {
        return Promise.reject(err)
    }
)

export default request
