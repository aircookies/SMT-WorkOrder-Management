import JSEncrypt from 'jsencrypt'
import request from "@/utils/request"

let publicKey = ''
let encryptor = null

/**
 * 手动设置公钥（用于外部注入公钥的场景）
 * @param {string} key - RSA 公钥字符串
 */
export const setPublicKey = (key) => {
  publicKey = key
}

/**
 * 从服务端获取 RSA 公钥并初始化加密器
 * 公钥会被缓存，多次调用不会重复请求
 * @returns {Promise<string>} 公钥字符串
 */
export const getPublicKey = async () => {
    if (!publicKey) {
        const result = await request.get("/publickey")
        try {
            if (result.code === 200) {
                publicKey = result.data
                // 初始化加密器
                encryptor = new JSEncrypt()
                encryptor.setPublicKey(`-----BEGIN PUBLIC KEY-----\n${publicKey}\n-----END PUBLIC KEY-----`)
            }
        } catch (error) {
            console.error('获取公钥失败:', error)
        }
    }
    return publicKey
}

/**
 * 使用RSA公钥加密密码
 * @param {string} password - 明文密码
 * @returns {string} 加密后的密码
 */
export const encryptPassword = async (password) => {
    if (!encryptor) {
        await getPublicKey()
    }

    if (!password) {
        throw new Error('密码不能为空')
    }

    const encrypted = await encryptor.encrypt(password)

    if (!encrypted) {
        throw new Error('密码加密失败')
    }

    return encrypted
}

/**
 * 重置密钥（用于密钥轮换时）
 */
export const resetKeys = () => {
    publicKey = ''
    encryptor = null
}