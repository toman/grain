/*
 * Copyright (c) 2013 SysGears, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sysgears.grain.compass

import com.sysgears.grain.CmdlineOptions
import com.sysgears.grain.taglib.Site
import groovy.util.logging.Slf4j

import javax.inject.Inject

/**
 * Base implementation of Compass integration.
 */
@Slf4j
abstract class AbstractCompass implements Compass {
    
    /** Site instance */
    @Inject private Site site
    
    /** Command line options */
    @Inject private CmdlineOptions opts

    /**
     * Configures and launches Compass process
     *
     * @param mode compass mode
     */
    public void configureAndLaunch(String mode) {
        def compassConfig = [location: '/config.rb'].render().full

        def configFile = new File(site.cache_dir as String, 'config.rb')
        if (!configFile.exists() || configFile.text != compassConfig) {
            configFile.write(compassConfig)
        }

        launchCompass(mode)
    }

    /**
     * Launches compass in a separate thread 
     * <p>
     * If the thread is interrupted the process will be destroyed.
     *
     * @param mode compass mode
     */
    abstract void launchCompass(String mode)

    /**
     * @inheritDoc
     */
    @Override
    public void start() {
        if (opts.command == 'preview') {
            configureAndLaunch("watch")
        }
    }

    /**
     * Shuts down Compass process 
     *
     * @throws Exception in case some error occur
     */
    @Override
    public abstract void stop()

    /**
     * @inheritDoc
     */
    @Override
    public void configChanged() {
    }
}