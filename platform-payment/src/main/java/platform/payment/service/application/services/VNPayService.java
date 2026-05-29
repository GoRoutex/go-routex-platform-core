package platform.payment.service.application.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.interfaces.model.vnpay.VNPayIpnResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface VNPayService {

    String createPaymentUrl(GetPaymentUrlCommand command, String txnRef) throws UnsupportedEncodingException;

    VNPayIpnResponse processIpn(HttpServletRequest servletRequest);

    void returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
