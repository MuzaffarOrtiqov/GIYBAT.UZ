package api.giybat.uz.service;

import api.giybat.uz.entity.EmailHistoryEntity;
import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.EmailType;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.EmailHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailHistoryService {
    @Autowired
    private EmailHistoryRepository emailHistoryRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public void createEmailHistory(String email, String subject, String body, String code, EmailType emailType) {
        EmailHistoryEntity emailHistoryEntity = new EmailHistoryEntity();
        emailHistoryEntity.setEmail(email);
        emailHistoryEntity.setSubject(subject);
        emailHistoryEntity.setBody(body);
        emailHistoryEntity.setCode(code);
        emailHistoryEntity.setAttemptCount(0l);
        emailHistoryEntity.setEmailType(emailType);
        emailHistoryRepository.save(emailHistoryEntity);

    }

    public Long getEmailCount(String email) {
        LocalDateTime now = LocalDateTime.now();
        Long count = emailHistoryRepository.countByEmailAndCreatedDateBetween(email, now.minusMinutes(1), now);
        return count;
    }

    public void check(String email, String code, AppLanguage lang) {
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email);

        if (optional.isEmpty()) {
            throw new AppBadException(resourceBundleMessageService.getMessage("email.not.found", lang));
        }
        EmailHistoryEntity entity = optional.get();
        if (entity.getAttemptCount() > 3) {
            throw new AppBadException(resourceBundleMessageService.getMessage("too.many.attempts", lang));
        }
        //check code
        if (!entity.getCode().equals(code)) {
            emailHistoryRepository.updateAttemptCount(entity.getId());
            throw new AppBadException(resourceBundleMessageService.getMessage("no.matching.password", lang));
        }
        //check time
        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(10);
        if (LocalDateTime.now().isAfter(expDate)) {
            throw new AppBadException(resourceBundleMessageService.getMessage("verification.time.expired", lang));
        }

    }




}
