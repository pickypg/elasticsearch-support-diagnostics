/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package co.elastic.support.diagnostics;

import co.elastic.support.util.JsonYamlUtils;
import co.elastic.support.util.ResourceCache;
import co.elastic.support.util.SystemUtils;
import co.elastic.support.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class DiagnosticApp {

    private static final Logger logger = LogManager.getLogger(DiagnosticApp.class);

    public static void main(String[] args) {

        try {
            DiagnosticInputs diagnosticInputs = new DiagnosticInputs();
            if (args.length == 0) {
                logger.info(Constants.CONSOLE, Constants.interactiveMsg);
                diagnosticInputs.interactive = true;
                diagnosticInputs.runInteractive();
            } else {
                List<String> errors = diagnosticInputs.parseInputs(args);
                if (errors.size() > 0) {
                    for (String err : errors) {
                        logger.error(Constants.CONSOLE, err);
                    }
                    diagnosticInputs.usage();
                    SystemUtils.quitApp();
                }
            }

            Map diagMap = JsonYamlUtils.readYamlFromClasspath(Constants.DIAG_CONFIG, true);
            DiagConfig diagConfig = new DiagConfig(diagMap);
            DiagnosticService diag = new DiagnosticService();

            ResourceCache.terminal.dispose();
            diag.exec(diagnosticInputs, diagConfig);
        } catch (ShowHelpException she){
            SystemUtils.quitApp();
        } catch (Exception e) {
            logger.error(Constants.CONSOLE,"Fatal error occurred: {}. {}", e.getMessage(), Constants.CHECK_LOG);
            logger.error( e);
        } finally {
            ResourceCache.closeAll();
        }
    }



}

