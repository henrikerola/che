/*
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
/**This parameter is needed to define sdk mode.*/
window.sdk = 1;

/**
 * Base IDE object
 */

window.IDE = {};

/**
 * Initial configuration
 */

window.IDE.config = {

    "context": "/che",

    "restContext": "/che/api",

    "workspaceName": "",

    "workspaceId": "",

    "projectName": window.location.pathname.split("/")[3] ? window.location.pathname.split("/")[3] : null,

    "startupParams": location.search ? location.search.substring(1) : null,

    "hiddenFiles": ".*",

    "cheExtensionPath": "/che/ext"

};

/**
 * Event handlers
 */

window.IDE.eventHandlers = {};

window.IDE.eventHandlers.initializationFailed = function (message) {
    if (message) {
        var err = new Error(message);
        window.alert(err.stack);
    } else {
        window.alert("Unable to initialize IDE");
    }
};