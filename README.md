# imageservice
图片上传服务器


# 因为需要使用到阿里云oss相关的配置信息,
# 需要用户在conf文件中加入dev_setting.conf这样的
# 文件,文件的具体格式为

    aliyunos {
        accessKeyId = ""
        accessKeySecret = ""
        bucketName = ""
        prefix = ""
    }