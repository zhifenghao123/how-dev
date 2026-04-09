package com.howdev.iam.controller;

import com.howdev.iam.annotation.ApiException;
import com.howdev.iam.dto.BaseResponse;
import com.howdev.iam.siginverify.SignatureVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 签名验证控制器
 *
 * <p>提供签名验证的测试接口，客户端需要按照签名方法的规范构造请求头。</p>
 *
 * <h3>客户端请求示例</h3>
 * <pre>
 * POST /sign/verify HTTP/1.1
 * Host: localhost:8080
 * Content-Type: application/json; charset=utf-8
 * X-Timestamp: 1551113065
 * Authorization: HMAC-SHA256 AccessKeyId=AKIDxxx,
 *     SignedHeaders=content-type;host, Signature=sss
 *
 * {"data":"hello"}
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
@ApiException
public class SignController {

    private final SignatureVerifier signatureVerifier;

    /**
     * 签名验证测试接口
     *
     * <p>接收任意 JSON 请求体，验证请求签名是否正确。
     * 验证通过返回成功，否则返回失败。</p>
     *
     * @param request     HTTP 请求对象
     * @param requestBody 请求体内容
     * @return 验证结果
     */
    @PostMapping("/verify")
    public BaseResponse<String> verifySignature(HttpServletRequest request,
                                                 @RequestBody String requestBody) {
        log.info("收到签名验证请求, URI={}, Method={}", request.getRequestURI(), request.getMethod());

        boolean verified = signatureVerifier.verifySignature(request, requestBody);

        if (verified) {
            return BaseResponse.newSuccResponse("签名验证通过");
        } else {
            return BaseResponse.newFailResponse("E2001", "签名验证失败");
        }
    }

    /**
     * 签名验证测试接口（GET 方式）
     *
     * <p>用于验证 GET 请求的签名，请求体为空。</p>
     *
     * @param request HTTP 请求对象
     * @return 验证结果
     */
    @GetMapping("/verify")
    public BaseResponse<String> verifySignatureGet(HttpServletRequest request) {
        log.info("收到 GET 签名验证请求, URI={}, QueryString={}", request.getRequestURI(), request.getQueryString());

        boolean verified = signatureVerifier.verifySignature(request, "");

        if (verified) {
            return BaseResponse.newSuccResponse("签名验证通过");
        } else {
            return BaseResponse.newFailResponse("E2001", "签名验证失败");
        }
    }
}
