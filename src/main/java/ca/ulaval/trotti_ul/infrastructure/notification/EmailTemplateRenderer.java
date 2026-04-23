package ca.ulaval.trotti_ul.infrastructure.notification;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import ca.ulaval.trotti_ul.domain.notification.EmailTemplate;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateCatalog;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateId;
import ca.ulaval.trotti_ul.domain.notification.RenderedEmail;

public class EmailTemplateRenderer {

    private final EmailTemplateCatalog catalog;

    public EmailTemplateRenderer(EmailTemplateCatalog catalog) {
        this.catalog = catalog;
    }

    public RenderedEmail render(EmailTemplateId id, Map<String, String> variables) {
        EmailTemplate template = catalog.get(id);
        String subject = applyVariables(template.subject(), variables);
        String bodyTemplate = loadTemplate(template.templatePath());
        String body = applyVariables(bodyTemplate, variables);
        return new RenderedEmail(subject, body);
    }

    private String loadTemplate(String templatePath) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Email template not found at " + templatePath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read email template at " + templatePath, e);
        }
    }

    private String applyVariables(String template, Map<String, String> variables) {
        String rendered = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return rendered;
    }
}
