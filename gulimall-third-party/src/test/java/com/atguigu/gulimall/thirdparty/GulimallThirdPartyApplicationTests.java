package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Resource
    OSSClient ossClient;


    @Test
    public void testUpload() throws FileNotFoundException {

/*
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAI4G8iujyBCVw1zz6fjvJf";
        String accessKeySecret = "fiPmwheY0ziCC7QHOFcdbk0BSU0I99";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
*/

        // 上传文件流。
        InputStream inputStream = new FileInputStream("E:\\诛仙陆雪琪.jpg");
        ossClient.putObject("gulimall-mario", "诛仙陆雪琪.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传完成...");
    }

    @Test
    void contextLoads() {
    }

}
