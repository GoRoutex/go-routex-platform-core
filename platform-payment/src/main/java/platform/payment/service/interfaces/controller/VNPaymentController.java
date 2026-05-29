package platform.payment.service.interfaces.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.payment.service.application.services.VNPayService;
import platform.payment.service.interfaces.model.vnpay.VNPayIpnResponse;

import java.io.IOException;

import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.API_PATH;
import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.API_VERSION;
import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.IPN_PATH;
import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.PAYMENT_PATH;
import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.RETURN_URL;

@RestController
@RequestMapping(API_PATH + API_VERSION + PAYMENT_PATH)
@RequiredArgsConstructor
public class VNPaymentController {

    private final VNPayService vnPayService;

    @GetMapping(IPN_PATH)
    public ResponseEntity<VNPayIpnResponse> vnPayIPN(HttpServletRequest request) {
        return ResponseEntity.ok(vnPayService.processIpn(request));
    }

    @GetMapping(RETURN_URL)
    public void returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        vnPayService.returnUrl(request, response);
    }

}
