package ca.ulaval.trotti_ul.domain.notification;

import java.util.Map;

public interface TemplatedEmailService {
    void send(String to, EmailTemplateId templateId, Map<String, String> variables);
}
