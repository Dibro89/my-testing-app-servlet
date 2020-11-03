package ua.training.mytestingapp.util;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleMessageResolver extends AbstractMessageResolver {

    private final String baseName;

    public ResourceBundleMessageResolver(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public String resolveMessage(ITemplateContext context,
                                 Class<?> origin,
                                 String key,
                                 Object[] messageParameters) {

        Locale locale = context.getLocale();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        String message = resourceBundle.getString(key);

        return formatMessage(locale, message, messageParameters);
    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext context,
                                                    Class<?> origin,
                                                    String key,
                                                    Object[] messageParameters) {

        return "??" + key + "_" + context.getLocale() + "??";
    }

    private String formatMessage(Locale locale, String message, Object[] messageParameters) {
        MessageFormat messageFormat = new MessageFormat(message, locale);
        return messageFormat.format(messageParameters);
    }
}
