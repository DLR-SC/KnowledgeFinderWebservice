/*******************************************************************************
 * Copyright 2016 DLR - German Aerospace Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
module.exports = function (config) {
    config.set({
        basePath: '../../../../',
        frameworks: ['mocha', 'sinon', 'chai'],
        files: [
            'src/main/webapp/static/js/utils.js',
            //'main/webapp/static/js/lib/**/*.js',
            //'main/webapp/static/js/lib/**/**/*.js',
            'src/test/webapp/mocha/*.test.js'
        ],
        exclude: [],
        preprocessors: {
            // source files, that you wanna generate coverage for
            // do not include tests or libraries
            // (these files will be instrumented by Istanbul)
            'src/main/webapp/static/js/*.js': ['coverage']
        },
        reporters: ['progress', 'junit', 'coverage'],
        coverageReporter: {
            type: 'lcov',
            dir: 'reports/coverage/'
        },
        junitReporter: {
            outputDir: 'reports/raw/',
            suite: 'Web-Login' // The package name that will appear in the report
        },
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: false,
        browsers: ['Chrome', 'Firefox'], // Default, if nothing is specified in the gruntfile
        singleRun: false
    });
};