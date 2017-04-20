package org.subethamail.smtp.auth;

/**
 * This a convenient class that saves you setting up the factories that we know
 * about; you can always add more afterwards. Currently this factory supports:
 *
 * PLAIN LOGIN
 *
 * @author Jeff Schnitzer
 */
public class EasyAuthenticationHandlerFactory extends MultipleAuthenticationHandlerFactory {

    /**
     * Just hold on to this so that the caller can get it later, if necessary
     */
    UsernamePasswordValidator validator;

    /**
     *
     * @param validator
     */
    public EasyAuthenticationHandlerFactory(UsernamePasswordValidator validator) {
        this.validator = validator;

        addFactory(new PlainAuthenticationHandlerFactory(this.validator));
        addFactory(new LoginAuthenticationHandlerFactory(this.validator));
    }

    /**
     *
     * @return
     */
    public UsernamePasswordValidator getValidator() {
        return this.validator;
    }
}
