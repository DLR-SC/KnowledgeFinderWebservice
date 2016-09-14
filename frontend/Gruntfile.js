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
module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        jshint: {
            files: ['*.js', 'src/main/webapp/static/js/*.js', 'src/test/webapp/mocha/*.js', 'src/test/webapp/scripts/*.js'],
            options: {
                // options here to override JSHint defaults
                globals: {
                    jQuery: true,
                    console: true,
                    module: true,
                    document: true
                }
            }
        },
        karma: {
            unit: {
                configFile: 'src/test/webapp/scripts/karma.conf.js',
                singleRun: true
            },
            unitChrome: {
                configFile: 'src/test/webapp/scripts/karma.conf.js',
                browsers: ['Chrome'],
                singleRun: true
            },
            unitFirefox: {
                configFile: 'src/test/webapp/scripts/karma.conf.js',
                browsers: ['Firefox'],
                singleRun: true
            }
        },
        // Refines XML to be viewed in browser, i. e. conversion via XSL to HTML
        // Note: the reports are in the project root, 'acceptance/structure_report.xml)
        exec: {
            buildUnitTestReport: {
                // "Example_Mocha_Coverage" is the name of the test suite as it shall be written into the report.
                command: 'node src/test/webapp/scripts/refineUnittestXml.js reports/'
            }
        },
        less: {
            development: {
                files: {
                    "src/main/webapp/static/css/knowledgefinder.css": "src/main/less/style.less"
                }
            },
            production: {
               /* options: {
                    paths: ["assets/css"],
                    plugins: [
                        new (require('less-plugin-autoprefix'))({browsers: ["last 2 versions"]}),
                        new (require('less-plugin-clean-css'))(cleanCssOptions)
                    ],
                    modifyVars: {
                        imgPath: '"http://mycdn.com/path/to/images"',
                        bgColor: 'red'
                    }
                },*/
                files: {
                    "src/main/webapp/static/css/knowledgefinder-min.css": "src/main/less/style.less"
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-mocha-test');
    grunt.loadNpmTasks('grunt-continue');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-exec');
    grunt.loadNpmTasks('grunt-karma');
    grunt.loadNpmTasks('grunt-contrib-less');

    grunt.registerTask('unitTest', ['jshint', 'karma:unit', 'exec:buildUnitTestReport']);
    grunt.registerTask('unitTestChrome', ['jshint', 'karma:unitChrome', 'exec:buildUnitTestReport']);
    grunt.registerTask('unitTestFirefox', ['jshint', 'karma:unitFirefox', 'exec:buildUnitTestReport']);
    grunt.registerTask('default', ['less:production', 'continue:on', 'jshint', 'karma:unit', 'exec:buildUnitTestReport']);
    grunt.registerTask('styles', ['less:development']);
};