package org.apereo.cas.util.serialization;

import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.proxy.ProxyGrantingTicket;
import org.apereo.cas.util.InetAddressUtils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * This is {@link MessageSanitizationUtils} which attempts to remove
 * sensitive ticket ids from a given String.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@UtilityClass
public class MessageSanitizationUtils {
    private static final Pattern TICKET_ID_PATTERN = Pattern.compile("(?:(?:" + TicketGrantingTicket.PREFIX + '|'
        + ProxyGrantingTicket.PROXY_GRANTING_TICKET_IOU_PREFIX + '|' + ProxyGrantingTicket.PROXY_GRANTING_TICKET_PREFIX
        + '|' + "OC" + '|' + "AT"
        + ")-\\d+-)([\\w.-]+)");

    private static final Pattern SENSITIVE_TEXT_PATTERN =
        Pattern.compile("(password|token|credential|secret)\\s*=\\s*(['\"]*\\S+\\b['\"]*)");

    private static final Boolean CAS_TICKET_ID_SANITIZE_SKIP = Boolean.getBoolean("CAS_TICKET_ID_SANITIZE_SKIP");

    /**
     * Specifies the ending tail length of the ticket id that would still be visible in the output
     * for troubleshooting purposes.
     */
    private static final int VISIBLE_TAIL_LENGTH = 10;

    private static final int OBFUSCATION_LENGTH = 5;

    /**
     * The obfuscated text that would be the replacement for sensitive text.
     */
    public static final String OBFUSCATED_STRING = "*".repeat(OBFUSCATION_LENGTH);

    /**
     * Gets the default suffix used when the default ticket id generator is used so the proper
     * visible length is shown.
     */
    private static final int HOST_NAME_LENGTH = InetAddressUtils.getCasServerHostName().length();

    /**
     * Remove ticket id from the message.
     *
     * @param msg the message
     * @return the modified message with tgt id removed
     */
    public static String sanitize(final String msg) {
        var modifiedMessage = msg;
        if (StringUtils.isNotBlank(msg) && !CAS_TICKET_ID_SANITIZE_SKIP) {
            val matcher = TICKET_ID_PATTERN.matcher(msg);
            while (matcher.find()) {
                val match = matcher.group();
                val group = matcher.group(1);
                val length = group.length();
                var replaceLength = length - VISIBLE_TAIL_LENGTH - (HOST_NAME_LENGTH + 1);
                if (replaceLength <= 0) {
                    replaceLength = length;
                }
                val newId = match.replace(group.substring(0, replaceLength), OBFUSCATED_STRING);
                modifiedMessage = modifiedMessage.replace(match, newId);
            }
        }

        val matcher = SENSITIVE_TEXT_PATTERN.matcher(msg);
        while (matcher.find()) {
            val group = matcher.group(2);
            modifiedMessage = modifiedMessage.replace(group, OBFUSCATED_STRING);
        }
        return modifiedMessage;
    }
}
