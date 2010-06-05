class ContactFormGrailsPlugin {
    // the plugin version
    def version = "1.0.7"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [ mail: "0.9", jcaptcha: "1.2.1" ]
    
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/views/index.gsp"
    ]

    // TODO Fill in these fields
    def author = "Anthony Campbell"
    def authorEmail = "acampbell3000 [[at] googlemail [dot]] com "
    def title = "Grails Contact Form"
    def description = '''
A simple plug-in which provides an Ajax driven contact form with real time validation and captcha support.

Ajax forms provided by the "remote-forms" skin available through the skin-loader plug-in.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/contact-form"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
