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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import org.eclipse.che.MyComponent;
import org.eclipse.che.ide.client.inject.IDEInjector;

/**
 * @author Henri Kerola / Vaadin
 */
@Connect(MyComponent.class)
public class MyComponentConnector extends AbstractComponentConnector {

    public MyComponentConnector() {
        IDEInjector injector = (IDEInjector) GWT.create(IDEInjector.class);
        BootstrapController bootstrap = injector.getBootstrapController();
        Window.alert("" + bootstrap);
    }

    @Override
    public Label getWidget() {
        return (Label) super.getWidget();
    }
}
