/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.util.browser;

import com.google.gwt.core.client.GWT;

/**
 * Class to allow conditional compilation for different user agents.
 * <p/>
 * All methods should return values that are known at compile time.
 * *
 * TODO(user): Should this ever be thrown in for free with GWT, rather use
 * their approach. A relevant thread is:
 * http://groups.google.com/group/Google-Web-Toolkit-Contributors/browse_thread/thread/6745dee7a85eb585/bd58d1a9f2344b34
 */
public class UserAgentStaticProperties {

    static UserAgentStaticProperties get() {
        return INSTANCE;
    }

    private static final UserAgentStaticProperties INSTANCE = createInstance();

    /**
     * Creates an instance of UserAgent.
     * <p/>
     * NOTE(danilatos): This method is designed to be statically evaluable by
     * the compiler, such that the compiler can determine that
     * only one subclass of UserAgent is ever used within a
     * given permutation. This is possible because
     * GWT.isClient() is replaced with true by the compiler,
     * even though it is executed normally in unit tests.
     * Testing the return value of GWT.create() is not adequate
     * because only boolean values can be statically evaluated
     * by the compiler at this time.
     *
     * @return an instance of UserAgent.
     */
    private static UserAgentStaticProperties createInstance() {
        if (GWT.isClient()) {
            return GWT.create(UserAgentStaticProperties.class);
        } else {
            return new FirefoxImpl();
        }
    }

    final boolean isWebkit() {
        return isSafari() || isMobileWebkit();
    }

    /** @return true iff the user agent uses mobile webkit */
    final boolean isMobileWebkit() {
        return isAndroid() || isIPhone();
    }

    // Default instance methods: most return false, since they are intended to be overriden.
    boolean isSafari() {
        return false;
    }

    boolean isFirefox() {
        return false;
    }

    boolean isIE() {
        return false;
    }

    boolean isAndroid() {
        return false;
    }

    boolean isIPhone() {
        return false;
    }

    // NOTE(user): Created via deferred binding
    public static class SafariImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isSafari() {
            return true;
        }
    }

    // NOTE(user): Created via deferred binding
    public static class FirefoxImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isFirefox() {
            return true;
        }
    }

    // NOTE(user): Created via deferred binding
    public static class IEImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isIE() {
            return true;
        }
    }

    // NOTE(user): Created via deferred binding
    public static class AndroidImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isAndroid() {
            return true;
        }
    }

    // NOTE(user): Created via deferred binding
    public static class IPhoneImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isIPhone() {
            return true;
        }
    }
}
