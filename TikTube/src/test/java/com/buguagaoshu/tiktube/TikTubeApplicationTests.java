package com.buguagaoshu.tiktube;


import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.repository.impl.FileRepositoryInOSS;
import com.buguagaoshu.tiktube.service.*;
import com.buguagaoshu.tiktube.utils.AesUtil;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class TikTubeApplicationTests {

    @Autowired
    private FileRepositoryInOSS fileRepositoryInOSS;

    @Test
    void contextLoads() {

    }
}
