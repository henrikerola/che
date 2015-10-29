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

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * @author Henri Kerola / Vaadin
 */
@GinModules({
        org.eclipse.che.ide.ext.github.client.inject.GitHubGinModule.class ,
        org.eclipse.che.ide.client.inject.IDEClientModule.class ,
        org.eclipse.che.ide.core.inject.CoreGinModule.class ,
        org.eclipse.che.ide.ext.gwt.client.inject.GwtGinModule.class ,
        org.eclipse.che.ide.ext.java.client.inject.JavaGinModule.class ,
        org.eclipse.che.ide.extension.maven.client.inject.MavenGinModule.class ,
        org.eclipse.che.ide.ext.java.client.inject.JavaJsEditorGinModule.class ,
        org.eclipse.che.ide.ext.java.jdi.client.inject.JavaRuntimeGinModule.class ,
        org.eclipse.che.ide.jseditor.client.inject.JsEditorGinModule.class ,
        org.eclipse.che.ide.extension.machine.client.inject.MachineGinModule.class ,
        org.eclipse.che.ide.ext.git.client.inject.GitGinModule.class ,
        org.eclipse.che.ide.orion.compare.OrionCompareModule.class ,
        org.eclipse.che.ide.ext.help.client.inject.HelpAboutGinModule.class ,
        org.eclipse.che.ide.core.inject.FileTypeModule.class ,
        org.eclipse.che.ide.jseditor.client.preference.inject.EditorPreferencesGinModule.class ,
        org.eclipse.che.env.local.client.inject.LocalEnvironmentGinModule.class ,
        org.eclipse.che.ide.editor.orion.client.inject.OrionEditorGinModule.class ,
        org.eclipse.che.ide.ext.web.inject.WebModule.class ,
        org.eclipse.che.ide.ext.ssh.client.inject.SshGinModule.class
})
public interface MyInjector extends Ginjector {

    MyComponentBootstrapper getBootstrapper();
}
