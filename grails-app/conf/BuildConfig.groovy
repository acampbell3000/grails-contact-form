
grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsPlugins()
        grailsHome()
        mavenCentral()
    }

    dependencies {
        test 'org.mockito:mockito-all:1.8.4'
    }

    plugins {
        runtime 'org.grails.plugins:mail:0.9'
        runtime 'org.grails.plugins:jcaptcha:1.2.1'
        test 'org.grails.plugins:code-coverage:latest.integration'
    }
}

