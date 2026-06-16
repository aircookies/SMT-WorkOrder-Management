import JSEncrypt from 'jsencrypt'
import request from "@/utils/request"

let publicKey = ''
let encryptor = null

export const setPublicKey = (key) => {
  publicKey = key
}

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