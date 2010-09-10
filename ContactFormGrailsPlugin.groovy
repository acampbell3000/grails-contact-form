
/**
 * Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Grails Contact Form Plug-in
 *
 * A simple plug-in which provides a ajax driven contact form along with
 * an image captcha.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class ContactFormGrailsPlugin {
    // the plugin version
    def version = "1.0.7"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [ mail: "0.9", jcaptcha: "1.2.1", jquery: "1.4.2.5" ]
    
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/views/index.gsp"
    ]

    // TODO Fill in these fields
    def author = "Anthony Campbell"
    def authorEmail = "acampbell3000 [[at] mail from google"
    def title = "Grails Contact Form"
    def description = '''
A simple plug-in which provides an Ajax driven contact form with real time validation and captcha support.

Ajax forms provided by the "remote-forms" skin available through the skin-loader plug-in.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/contact-form"
}
