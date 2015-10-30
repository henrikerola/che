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
package org.eclipse.che.ide.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * @author Henri Kerola / Vaadin
 */
public class MyComponentWidget extends LayoutPanel {

    public MyComponentWidget() {

        MyInjector injector = GWT.create(MyInjector.class);
        MyComponentBootstrapper bootstrap = injector.getBootstrapper();
        bootstrap.startComponents(this);
    }
}
