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
package org.eclipse.che.ide.ext.java.server;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import org.eclipse.che.api.auth.oauth.OAuthTokenProvider;
import org.eclipse.che.api.core.rest.ApiInfoService;
import org.eclipse.che.api.core.rest.CoreRestModule;
import org.eclipse.che.api.git.GitConnectionFactory;
import org.eclipse.che.api.project.server.BaseProjectModule;
import org.eclipse.che.api.vfs.server.VirtualFileSystemModule;
import org.eclipse.che.api.vfs.server.VirtualFileSystemRegistry;
import org.eclipse.che.everrest.CodenvyAsynchronousJobPool;
import org.eclipse.che.generator.archetype.ArchetypeGenerator;
import org.eclipse.che.generator.archetype.ArchetypeGeneratorModule;
import org.eclipse.che.git.impl.nativegit.NativeGitConnectionFactory;
import org.eclipse.che.git.impl.nativegit.ssh.SshKeyProvider;
import org.eclipse.che.ide.ext.github.server.inject.GitHubModule;
import org.eclipse.che.ide.ext.java.jdi.server.DebuggerService;
import org.eclipse.che.ide.ext.ssh.server.SshKeyStore;
import org.eclipse.che.ide.extension.maven.server.inject.MavenModule;
import org.eclipse.che.inject.DynaModule;
import org.eclipse.che.security.oauth.RemoteOAuthTokenProvider;
import org.eclipse.che.vfs.impl.fs.AutoMountVirtualFileSystemRegistry;
import org.eclipse.che.vfs.impl.fs.LocalFSMountStrategy;
import org.eclipse.che.vfs.impl.fs.LocalFileSystemRegistryPlugin;
import org.eclipse.che.vfs.impl.fs.MachineFSMountStrategy;
import org.eclipse.che.vfs.impl.fs.VirtualFileSystemFSModule;
import org.everrest.core.impl.async.AsynchronousJobPool;
import org.everrest.core.impl.async.AsynchronousJobService;
import org.everrest.guice.ServiceBindingHelper;

/**
 * @author Evgen Vidolob
 */
@DynaModule
public class MachineModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApiInfoService.class);

        bind(LocalFileSystemRegistryPlugin.class);

        bind(LocalFSMountStrategy.class).to(MachineFSMountStrategy.class);
        bind(VirtualFileSystemRegistry.class).to(AutoMountVirtualFileSystemRegistry.class);
        bind(OAuthTokenProvider.class).to(RemoteOAuthTokenProvider.class);
        bind(SshKeyStore.class).to(RemoteSshKeyStore.class);

        install(new CoreRestModule());
        install(new BaseProjectModule());
        install(new VirtualFileSystemModule());
        install(new VirtualFileSystemFSModule());
        install(new MavenModule());
        install(new ArchetypeGeneratorModule());
        install(new GitHubModule());

        bind(SshKeyProvider.class).to(MachineSideSshKeyProvider.class);

        bind(ArchetypeGenerator.class);
        bind(DebuggerService.class);

        bind(GitConnectionFactory.class).to(NativeGitConnectionFactory.class);

        bind(AsynchronousJobPool.class).to(CodenvyAsynchronousJobPool.class);
        bind(ServiceBindingHelper.bindingKey(AsynchronousJobService.class, "/async/{ws-id}")).to(AsynchronousJobService.class);

        bind(String.class).annotatedWith(Names.named("api.endpoint")).toProvider(ApiEndpointProvider.class);
    }
}
