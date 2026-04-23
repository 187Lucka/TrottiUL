package ca.ulaval.trotti_ul.infrastructure.notification;

import java.util.EnumMap;
import java.util.Map;

import ca.ulaval.trotti_ul.domain.notification.EmailTemplate;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateCatalog;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateId;

public final class EmailTemplates {

    private EmailTemplates() {
    }

    public static EmailTemplateCatalog defaultCatalog() {
        Map<EmailTemplateId, EmailTemplate> templates = new EnumMap<>(EmailTemplateId.class);

        templates.put(
                EmailTemplateId.PASS_ACTIVATION,
                new EmailTemplate(
                        "Activation de votre pass Trotti-UL",
                        "templates/email/pass_activation.html"
                )
        );

        templates.put(
                EmailTemplateId.RIDE_UNLOCK_CODE,
                new EmailTemplate(
                        "Votre code de déverrouillage",
                        "templates/email/ride_unlock_code.html"
                )
        );

        templates.put(
                EmailTemplateId.MAINTENANCE_REQUEST,
                new EmailTemplate(
                        "Demande de maintenance - Station {{station}}",
                        "templates/email/maintenance_request.html"
                )
        );

        return new EmailTemplateCatalog(templates);
    }
}
