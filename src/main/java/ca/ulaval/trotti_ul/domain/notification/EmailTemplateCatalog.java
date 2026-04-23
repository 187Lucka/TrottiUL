package ca.ulaval.trotti_ul.domain.notification;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class EmailTemplateCatalog {

    private final Map<EmailTemplateId, EmailTemplate> templates;

    public EmailTemplateCatalog(Map<EmailTemplateId, EmailTemplate> templates) {
        this.templates = new EnumMap<>(templates);
    }

    public EmailTemplate get(EmailTemplateId id) {
        EmailTemplate template = templates.get(id);
        if (template == null) {
            throw new IllegalArgumentException("No template registered for id " + id);
        }
        return template;
    }

    public Map<EmailTemplateId, EmailTemplate> templates() {
        return Collections.unmodifiableMap(templates);
    }
}
