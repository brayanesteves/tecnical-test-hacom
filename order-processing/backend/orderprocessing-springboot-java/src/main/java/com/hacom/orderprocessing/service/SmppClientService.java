package com.hacom.orderprocessing.service;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

@Service
public class SmppClientService {

    private static final Logger log = LoggerFactory.getLogger(SmppClientService.class);
    private final DefaultSmppClient clientBootstrap;

    public SmppClientService() {
        this.clientBootstrap = new DefaultSmppClient(Executors.newCachedThreadPool(), 1);
    }

    public void sendSms(String phoneNumber, String messageText) {
        log.info("Sending SMS to {} with text: {}", phoneNumber, messageText);

        try {
            SmppSessionConfiguration config = new SmppSessionConfiguration();
            config.setWindowSize(1);
            config.setName("Tester.Session.0");
            config.setType(SmppBindType.TRANSCEIVER);
            config.setHost("127.0.0.1");
            config.setPort(2775);
            config.setConnectTimeout(10000);
            config.setSystemId("smppclient1");
            config.setPassword("password");

            SmppSession session = clientBootstrap.bind(config);
            log.info("Successfully bound to SMPP server");

            SubmitSm submit = new SubmitSm();
            submit.setSourceAddress(new Address((byte) 0x01, (byte) 0x01, "HACOM"));
            submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, phoneNumber));
            submit.setShortMessage(messageText.getBytes());

            SubmitSmResp submitResp = session.submit(submit, 10000);
            log.info("SMS submitted with message id {}", submitResp.getMessageId());
            session.unbind(5000);

            log.info("Successfully processed SMS for: " + phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS via SMPP", e);

        }
    }
}
