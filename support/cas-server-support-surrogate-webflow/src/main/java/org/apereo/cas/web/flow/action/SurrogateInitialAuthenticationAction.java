package org.apereo.cas.web.flow.action;

import org.apereo.cas.authentication.RememberMeCredential;
import org.apereo.cas.authentication.SurrogateUsernamePasswordCredential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.web.flow.actions.InitialAuthenticationAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is {@link SurrogateInitialAuthenticationAction}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
public class SurrogateInitialAuthenticationAction extends InitialAuthenticationAction {
    private final String separator;

    public SurrogateInitialAuthenticationAction(final CasDelegatingWebflowEventResolver delegatingWebflowEventResolver,
                                                final CasWebflowEventResolver webflowEventResolver,
                                                final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy,
                                                final String separator) {
        super(delegatingWebflowEventResolver, webflowEventResolver, adaptiveAuthenticationPolicy);
        this.separator = separator;
    }

    @Override
    protected Event doPreExecute(final RequestContext context) throws Exception {
        val up = WebUtils.getCredential(context, UsernamePasswordCredential.class);
        if (up == null) {
            LOGGER.debug("Provided credentials cannot be found, or are already of type [{}]", SurrogateUsernamePasswordCredential.class.getName());
            return super.doPreExecute(context);
        }
        if (up.getUsername().contains(this.separator)) {
            LOGGER.debug("Credential username includes the separator [{}]. Converting to surrogate...", this.separator);
            convertToSurrogateCredential(context, up);
        } else {
            convertToUsernamePasswordCredential(context, up);
        }
        return super.doPreExecute(context);
    }

    private void convertToSurrogateCredential(final RequestContext context, final UsernamePasswordCredential up) {

        val tUsername = up.getUsername();
        val surrogateUsername = tUsername.substring(0, tUsername.indexOf(this.separator));
        val realUsername = tUsername.substring(tUsername.indexOf(this.separator) + this.separator.length());
        LOGGER.debug("Converting to surrogate credential for username [{}], surrogate username [{}]", realUsername, surrogateUsername);

        if (StringUtils.isBlank(surrogateUsername)) {
            up.setUsername(realUsername);
            WebUtils.putSurrogateAuthenticationRequest(context, Boolean.TRUE);
            WebUtils.putCredential(context, up);

            LOGGER.debug("No surrogate username is defined; Signal webflow to request for surrogate credentials");
            return;
        }

        val sc = new SurrogateUsernamePasswordCredential();
        sc.setUsername(realUsername);
        sc.setSurrogateUsername(surrogateUsername);
        sc.setPassword(up.getPassword());
        if (up instanceof RememberMeCredential) {
            sc.setRememberMe(((RememberMeCredential) up).isRememberMe());
        }
        WebUtils.putSurrogateAuthenticationRequest(context, Boolean.FALSE);
        LOGGER.debug("Converted credential to surrogate for username [{}] and assigned it to webflow", realUsername);
        WebUtils.putCredential(context, sc);
    }

    private static void convertToUsernamePasswordCredential(final RequestContext context,
                                                            final UsernamePasswordCredential up) throws Exception {
        if (up instanceof SurrogateUsernamePasswordCredential) {
            val sc = new UsernamePasswordCredential();
            BeanUtils.copyProperties(sc, up);
            WebUtils.putCredential(context, sc);
        }
    }
}
