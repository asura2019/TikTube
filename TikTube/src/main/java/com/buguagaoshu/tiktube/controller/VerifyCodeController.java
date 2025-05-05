package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.cache.MailCountLimit;
import com.buguagaoshu.tiktube.entity.UserEntity;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.service.UserService;
import com.buguagaoshu.tiktube.service.VerifyCodeService;
import com.buguagaoshu.tiktube.utils.IpUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;

import jakarta.servlet.http.HttpSession;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2019-11-27 19:57
 */
@RestController
public class VerifyCodeController {
    private final VerifyCodeService verifyCodeService;

    private final UserService userService;

    private final MailCountLimit mailCountLimit;

    private final IpUtil ipUtil;

    private static final String IMAGE_FORMAT = "png";

    public VerifyCodeController(VerifyCodeService verifyCodeService,
                                UserService userService,
                                MailCountLimit mailCountLimit, IpUtil ipUtil) {
        this.verifyCodeService = verifyCodeService;
        this.userService = userService;
        this.mailCountLimit = mailCountLimit;
        this.ipUtil = ipUtil;
    }

    private static InputStreamResource imageToInputStreamResource(Image image, String format) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, format, byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return new InputStreamResource(byteArrayInputStream);
    }

    @GetMapping("/api/verifyImage")
    public HttpEntity image(HttpSession session) throws IOException {
        Image image = verifyCodeService.image(session.getId());
        InputStreamResource inputStreamResource = imageToInputStreamResource(image, IMAGE_FORMAT);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Pragma", "No-cache");
        httpHeaders.set("Cache-Control", "no-cache");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .contentType(MediaType.IMAGE_PNG)
                .body(inputStreamResource);
    }

    /**
     * 发送验证码
     */
    @PostMapping("/api/verify/send")
    public ResponseDetails send(@RequestBody UserEntity user, HttpServletRequest request) {
        mailCountLimit.setMailCount(ipUtil.getIpAddr(request), 1);
        // 一段时间内最多只允许发送6次
        if (mailCountLimit.getCount(ipUtil.getIpAddr(request)) < 6) {
            verifyCodeService.send(user.getMail());
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
    }
}
