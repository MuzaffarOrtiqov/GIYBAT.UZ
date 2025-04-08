package api.giybat.uz;

import api.giybat.uz.enums.SmsType;
import api.giybat.uz.service.SmsSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private SmsSendService smsSendService;

    @Test
    void contextLoads() {
        smsSendService.sendSms("998942702363","Bu Eskiz dan test","12345",SmsType.REGISTRATION);
    }

}
