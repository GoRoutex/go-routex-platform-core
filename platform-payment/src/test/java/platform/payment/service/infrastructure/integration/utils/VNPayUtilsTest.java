package platform.payment.service.infrastructure.integration.utils;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VNPayUtilsTest {

    private final VNPayUtils vnPayUtils = new VNPayUtils();

    @Test
    void buildHashDataAndSignIpnFieldsWithConfiguredSecret() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("vnp_BankCode", "NCB");
        fields.put("vnp_PayDate", "20260623231828");
        fields.put("vnp_TransactionNo", "15595869");
        fields.put("vnp_TmnCode", "2Y0B395W");
        fields.put("vnp_OrderInfo", "Thanh toan don hang:BK-20260623-000088");
        fields.put("vnp_TxnRef", "6d600480-e1bc-4ac5-8b82-260f4f4a1d33");
        fields.put("vnp_Amount", "70000000");
        fields.put("vnp_CardType", "ATM");
        fields.put("vnp_TransactionStatus", "00");
        fields.put("vnp_BankTranNo", "VNP15595869");
        fields.put("vnp_ResponseCode", "00");

        String hashData = vnPayUtils.buildHashData(fields);
        String signValue = vnPayUtils.hmacSHA512("KJEMY4DV3S2QBQVW6Y1MVGUQJJQVVEYP", hashData);

        assertThat(hashData).isEqualTo("vnp_Amount=70000000&vnp_BankCode=NCB&vnp_BankTranNo=VNP15595869&vnp_CardType=ATM&vnp_OrderInfo=Thanh+toan+don+hang%3ABK-20260623-000088&vnp_PayDate=20260623231828&vnp_ResponseCode=00&vnp_TmnCode=2Y0B395W&vnp_TransactionNo=15595869&vnp_TransactionStatus=00&vnp_TxnRef=6d600480-e1bc-4ac5-8b82-260f4f4a1d33");
        assertThat(signValue).isEqualTo("d179450eed7ff721fda38f644a7623fd0ed35bb279060d779b20d5bda4022e0accb62e57b0d67ac26b1cb63cbc4f2f55f926fc535c7af44a92468516c0578b75");
    }
}
