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

import org.eclipse.che.ide.util.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/** Utility methods relating to the browser. */
public class BrowserUtils {

    private static final BrowserUtils INSTANCE = GWT.create(BrowserUtils.class);

    boolean isFFox() {
        return false;
    }

    static class Chrome extends BrowserUtils {
        Chrome() {
        }

        @Override
        boolean isFFox() {
            return false;
        }
    }

    static class Firefox extends BrowserUtils {
        Firefox() {
        }

        @Override
        boolean isFFox() {
            return true;
        }
    }

    public static boolean isFirefox() {
        return INSTANCE.isFFox();
    }

    public static boolean isChromeOs() {
        return Window.Navigator.getUserAgent().contains(" CrOS ");
    }

    public static boolean isIPad() {
        return Window.Navigator.getAppVersion().contains("iPad");
    }

    public static boolean isIphone() {
        return Window.Navigator.getAppVersion().contains("iPhone");
    }

    public static boolean isAndroid() {
        return Window.Navigator.getAppVersion().contains("Android");
    }

    public static boolean hasUrlParameter(String parameter) {
        return Window.Location.getParameter(parameter) != null;
    }

    public static boolean hasUrlParameter(String parameter, String value) {
        return StringUtils.equalNonEmptyStrings(Window.Location.getParameter(parameter), value);
    }

    private BrowserUtils() {
    }
}
