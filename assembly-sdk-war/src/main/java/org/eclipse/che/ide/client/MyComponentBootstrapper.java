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

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import org.eclipse.che.ide.api.DocumentTitleDecorator;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.event.WindowActionEvent;
import org.eclipse.che.ide.api.event.project.ProjectReadyEvent;
import org.eclipse.che.ide.api.event.project.ProjectReadyHandler;
import org.eclipse.che.ide.api.parts.PerspectiveManager;
import org.eclipse.che.ide.core.Component;
import org.eclipse.che.ide.logger.AnalyticsEventLoggerExt;
import org.eclipse.che.ide.statepersistance.AppStateManager;
import org.eclipse.che.ide.ui.toolbar.PresentationFactory;
import org.eclipse.che.ide.util.Config;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.workspace.WorkspacePresenter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Henri Kerola / Vaadin
 */
@Singleton
public class MyComponentBootstrapper {

    private final AnalyticsEventLoggerExt analyticsEventLoggerExt;
    private final Provider<WorkspacePresenter> workspaceProvider;
    private final ExtensionInitializer         extensionInitializer;
    private final EventBus eventBus;
    private final ActionManager actionManager;
    private final PresentationFactory presentationFactory;
    private final DocumentTitleDecorator documentTitleDecorator;
    private final Provider<PerspectiveManager> managerProvider;
    private final Provider<AppStateManager>    appStateManagerProvider;
    private Map<String, Provider<Component>> components;

    private LayoutPanel wrapper;

    @Inject
    public MyComponentBootstrapper(Provider<WorkspacePresenter> workspaceProvider,
                               ExtensionInitializer extensionInitializer,
                               DtoRegistrar dtoRegistrar,
                               AnalyticsEventLoggerExt analyticsEventLoggerExt,
                               EventBus eventBus,
                               ActionManager actionManager,
                               DocumentTitleDecorator documentTitleDecorator,
                               Provider<PerspectiveManager> managerProvider,
                               Provider<AppStateManager> appStateManagerProvider) {
        this.workspaceProvider = workspaceProvider;
        this.extensionInitializer = extensionInitializer;
        this.eventBus = eventBus;
        this.actionManager = actionManager;
        this.analyticsEventLoggerExt = analyticsEventLoggerExt;
        this.documentTitleDecorator = documentTitleDecorator;
        this.managerProvider = managerProvider;
        this.appStateManagerProvider = appStateManagerProvider;

        presentationFactory = new PresentationFactory();

        // Register DTO providers
        dtoRegistrar.registerDtoProviders();
    }

    @Inject
    void startComponents(final Map<String, Provider<Component>> components) {
        this.components = components;
    }

    private void componentStartFail(Exception reason) {
        Log.error(MyComponentBootstrapper.class, reason);
        initializationFailed(reason);
    }

    public void startComponents(LayoutPanel wrapper) {
        this.wrapper = wrapper;
        final Iterator<Provider<Component>> componentProviderIterator = components.values().iterator();
        startComponents(componentProviderIterator);
    }

    public void startComponents(final Iterator<Provider<Component>> componentProviderIterator) {
        if (componentProviderIterator.hasNext()) {
            Provider<Component> componentProvider = componentProviderIterator.next();

            componentProvider.get().start(new Callback<Component, Exception>() {
                @Override
                public void onSuccess(Component result) {
                    Log.info(getClass(), result.getClass());
                    startComponents(componentProviderIterator);
                }

                @Override
                public void onFailure(Exception reason) {
                    componentStartFail(reason);
                }
            });
        } else {
            startExtensions();
        }
    }

    /** Start extensions */
    private void startExtensions() {
        appStateManagerProvider.get();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                // Instantiate extensions
                extensionInitializer.startExtensions();

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {

                        displayIDE();
                    }
                });
            }
        });
    }

    /** Displays the IDE */
    private void displayIDE() {
        // Start UI
        SimpleLayoutPanel mainPanel = new SimpleLayoutPanel();

        wrapper.add(mainPanel);

        // Make sure the root panel creates its own stacking context
        //RootLayoutPanel.get().getElement().getStyle().setZIndex(0);

        WorkspacePresenter workspacePresenter = workspaceProvider.get();

        // Display 'Update extension' button if IDE is launched in SDK runner
        workspacePresenter.setUpdateButtonVisibility(Config.getStartupParam("h") != null && Config.getStartupParam("p") != null);

        // Display IDE
        workspacePresenter.go(mainPanel);

        Document.get().setTitle(documentTitleDecorator.getDocumentTitle());

        processStartupParameters();

        final AnalyticsSessions analyticsSessions = new AnalyticsSessions();

        // Bind browser's window events
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            @Override
            public void onWindowClosing(Window.ClosingEvent event) {
                onWindowClose(analyticsSessions);
                eventBus.fireEvent(WindowActionEvent.createWindowClosingEvent(event));
            }
        });
        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> event) {
                onWindowClose(analyticsSessions);
                eventBus.fireEvent(WindowActionEvent.createWindowClosedEvent());
            }
        });

        elemental.html.Window window = Browser.getWindow();

        window.addEventListener(Event.FOCUS, new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                onSessionUsage(analyticsSessions, false);
            }
        }, true);

        window.addEventListener(Event.BLUR, new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                onSessionUsage(analyticsSessions, false);
            }
        }, true);

        onSessionUsage(analyticsSessions, true); // This is necessary to forcibly print the very first event
    }

    private void onSessionUsage(AnalyticsSessions analyticsSessions, boolean force) {
        if (analyticsSessions.getIdleUsageTime() > 600000) { // 10 min
            analyticsSessions.makeNew();
            logSessionUsageEvent(analyticsSessions, true);
        } else {
            logSessionUsageEvent(analyticsSessions, force);
            analyticsSessions.updateUsageTime();
        }
    }

    private void onWindowClose(AnalyticsSessions analyticsSessions) {
        if (analyticsSessions.getIdleUsageTime() <= 60000) { // 1 min
            logSessionUsageEvent(analyticsSessions, true);
            analyticsSessions.updateUsageTime();
        }
    }

    private void logSessionUsageEvent(AnalyticsSessions analyticsSessions, boolean force) {
        if (force || analyticsSessions.getIdleLogTime() > 60000) { // 1 min, don't log frequently than once per minute
            Map<String, String> parameters = new HashMap<>();
            parameters.put("SESSION-ID", analyticsSessions.getId());

            analyticsEventLoggerExt.logEvent("session-usage", parameters);

            if (Config.getCurrentWorkspace() != null && Config.getCurrentWorkspace().isTemporary()) {
                analyticsEventLoggerExt.logEvent("session-usage", parameters);
            }

            analyticsSessions.updateLogTime();
        }
    }


    private void processStartupParameters() {
        final String projectNameToOpen = Config.getProjectName();
        if (projectNameToOpen != null) {
            eventBus.addHandler(ProjectReadyEvent.TYPE, getStartupActionHandler());
        } else {
            processStartupAction();
        }
    }

    private ProjectReadyHandler getStartupActionHandler() {
        return new ProjectReadyHandler() {
            //process action only after opening project

            @Override
            public void onProjectReady(ProjectReadyEvent event) {
                processStartupAction();
            }
        };
    }

    private void processStartupAction() {
        final String startupAction = Config.getStartupParam("action");
        if (startupAction != null) {
            performAction(startupAction, null);
        }
    }

    private void performAction(String actionId, Map<String, String> parameters) {
        Action action = actionManager.getAction(actionId);

        if (action == null) {
            return;
        }

        final Presentation presentation = presentationFactory.getPresentation(action);

        final PerspectiveManager manager = managerProvider.get();

        final ActionEvent e = new ActionEvent("", presentation, actionManager, manager, parameters);
        action.update(e);

        if (presentation.isEnabled() && presentation.isVisible()) {
            action.actionPerformed(e);
        }
    }

    /**
     * Handles any of initialization errors.
     * Tries to call predefined IDE.eventHandlers.ideInitializationFailed function.
     *
     * @param reason
     *         failure encountered
     */
    private native void initializationFailed(Exception reason) /*-{
        try {
            $wnd.IDE.eventHandlers.initializationFailed(reason);
        } catch (e) {
            console.log(e.message);
        }
    }-*/;
}
