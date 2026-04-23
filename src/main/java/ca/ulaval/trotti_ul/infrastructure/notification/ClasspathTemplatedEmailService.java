package ca.ulaval.trotti_ul.infrastructure.notification;

import java.util.Map;

import ca.ulaval.trotti_ul.domain.notification.EmailContentType;
import ca.ulaval.trotti_ul.domain.notification.EmailMessage;
import ca.ulaval.trotti_ul.domain.notification.EmailSender;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateCatalog;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateId;
import ca.ulaval.trotti_ul.domain.notification.RenderedEmail;
import ca.ulaval.trotti_ul.domain.notification.TemplatedEmailService;

public class ClasspathTemplatedEmailService implements TemplatedEmailService {

    private final EmailSender emailSender;
    private final EmailTemplateCatalog catalog;
    private final EmailTemplateRenderer renderer;

    public ClasspathTemplatedEmailService(EmailSender emailSender, EmailTemplateCatalog catalog) {
        this.emailSender = emailSender;
        this.catalog = catalog;
        this.renderer = new EmailTemplateRenderer(catalog);
    }

    @Override
    public void send(String to, EmailTemplateId templateId, Map<String, String> variables) {
        RenderedEmail renderedEmail = renderer.render(templateId, variables);
        EmailMessage message = new EmailMessage(to, renderedEmail.subject(), renderedEmail.body(), EmailContentType.HTML);
        emailSender.send(message);
    }
}
