package uk.co.anthonycampbell.grails.contactform

import grails.test.*
import org.grails.plugin.jcaptcha.JcaptchaService
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.grails.mail.MailService

/**
 * Set of unit tests for the contact form controller.
 */
class ContactFormControllerTests extends ControllerUnitTestCase {

    // Declare test properties
    ContactFormController contactFormController
    def mockJcaptchaService
    def mockMailService
    def mockedConfig
    
    def validProperties
    def emptyProperties

    /**
     * Initialise test parameters and controller
     */
    protected void setUp() {
        super.setUp()

        // Mock dependencies
        mockLogging(ContactFormController.class, true)
        mockJcaptchaService = mockFor(JcaptchaService.class)
        mockMailService = mockFor(MailService.class)

        // Initialise controller
        contactFormController = ContactFormController.newInstance()
        mockedConfig = ConfigObject.newInstance()

        // Add message lookup to always return key
        contactFormController.metaClass.message = { Map args -> return args.code }

        // Initialise test form properties
        validProperties = [yourFullName: "Joe Bloggs",
            yourEmailAddress: "joe@bloggs.com",
            subject: "Joe",
            message: "Bloggs",
            captcha: "123456"]
        emptyProperties = [yourFullName: "",
            yourEmailAddress: "",
            subject: "",
            message: "",
            captcha: ""]
    }

    /**
     * Ensures a tear down is performed after each test
     */
    protected void tearDown() {
        super.tearDown()
    }

    void testAllowedMethodsSize() {
        assertEquals "Unexpected number of allowed methods available on the ContactFormController",
            2, ContactFormController.allowedMethods.size()
    }

    void testAllowedMethodsKeys() {
        assertTrue "Expected allowed method not available!",
            ContactFormController.allowedMethods.containsKey("send")

        assertTrue "Expected allowed method not available!",
            ContactFormController.allowedMethods.containsKey("ajaxSend")
    }

    void testIndex() {
        // Run test
        contactFormController.index()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.redirectArgs.action
    }

    void testSend() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Mock JCaptcha service
        mockJcaptchaService.demand.validateResponse() {
            def name, def sessionId, def response -> return true }
        contactFormController.jcaptchaService = mockJcaptchaService.createMock()

        // Mock Mail service
        mockMailService.demand.sendMail() { def closure -> return null }
        contactFormController.mailService = mockMailService.createMock()

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.send()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.success",
            contactFormController.flash.message
    }

    void testSendWithInvalidDestinationEmail() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert invalid config
        mockedConfig.grails.mail.to = "invalid"
        ConfigurationHolder.config = mockedConfig

        // Run test
        contactFormController.send()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.destination.address.not.found.message",
            contactFormController.flash.message
    }

    void testSendWithEmptyDestinationEmail() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert empty test
        mockedConfig.grails.mail.to = ""
        ConfigurationHolder.config = mockedConfig

        // Run test
        contactFormController.send()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.destination.address.not.found.message",
            contactFormController.flash.message
    }

    void testSendWithNullDestinationEmail() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert null config
        mockedConfig.grails.mail.to = null
        ConfigurationHolder.config = mockedConfig

        // Run test
        contactFormController.send()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.destination.address.not.found.message",
            contactFormController.flash.message
    }

    void testSendWithInvalidForm() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(emptyProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Insert parameters
        contactFormController.params.putAll(emptyProperties)

        // Run test
        contactFormController.send()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!", null,
            contactFormController.flash.message
    }

    void testSendWithInvalidCaptcha() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Mock JCaptcha service
        mockJcaptchaService.demand.validateResponse() {
            def name, def sessionId, def response -> return false }
        contactFormController.jcaptchaService = mockJcaptchaService.createMock()

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.send()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!", null,
            contactFormController.flash.message
    }

    void testSendWithCaptchaFailure() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.send()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!", null,
            contactFormController.flash.message
    }

    void testSendWithMailServiceFailure() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Mock JCaptcha service
        mockJcaptchaService.demand.validateResponse() {
            def name, def sessionId, def response -> return true }
        contactFormController.jcaptchaService = mockJcaptchaService.createMock()

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.send()

        // Check result
        assertEquals "Unexpected redirect action returned!", "create",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.send.fail",
            contactFormController.flash.message
    }

    void testAjaxSend() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Mock JCaptcha service
        mockJcaptchaService.demand.validateResponse() {
            def name, def sessionId, def response -> return true }
        contactFormController.jcaptchaService = mockJcaptchaService.createMock()

        // Mock Mail service
        mockMailService.demand.sendMail() { def closure -> return null }
        contactFormController.mailService = mockMailService.createMock()

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.ajaxSend()

        // Check result
        assertEquals "Unexpected redirect action returned!", "ajaxCreate",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.success",
            contactFormController.flash.message
    }

    void testAjaxSendWithInvalidDestinationEmail() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert invalid config
        mockedConfig.grails.mail.to = "invalid"
        ConfigurationHolder.config = mockedConfig

        // Run test
        contactFormController.ajaxSend()

        // Check results
        assertEquals "Unexpected redirect action returned!", "ajaxCreate",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.destination.address.not.found.message",
            contactFormController.flash.message
    }

    void testAjaxSendWithEmptyDestinationEmail() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert empty config
        mockedConfig.grails.mail.to = ""
        ConfigurationHolder.config = mockedConfig

        // Run test
        contactFormController.ajaxSend()

        // Check result
        assertEquals "Unexpected redirect action returned!", "ajaxCreate",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.destination.address.not.found.message",
            contactFormController.flash.message
    }

    void testAjaxSendWithNullDestinationEmail() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert null config
        mockedConfig.grails.mail.to = null
        ConfigurationHolder.config = mockedConfig

        // Run test
        contactFormController.ajaxSend()

        // Check result
        assertEquals "Unexpected redirect action returned!", "ajaxCreate",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.destination.address.not.found.message",
            contactFormController.flash.message
    }

    void testAjaxSendWithInvalidForm() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(emptyProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Insert parameters
        contactFormController.params.putAll(emptyProperties)

        // Run test
        contactFormController.ajaxSend()

        // Check result
        assertEquals "Unexpected redirect action returned!", "ajaxCreate",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!", null,
            contactFormController.flash.message
    }

    void testAjaxSendWithInvalidCaptcha() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Mock JCaptcha service
        mockJcaptchaService.demand.validateResponse() {
            def name, def sessionId, def response -> return false }
        contactFormController.jcaptchaService = mockJcaptchaService.createMock()

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.ajaxSend()

        // Check result
        assertEquals "Unexpected redirect action returned!", "ajaxCreate",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!", null,
            contactFormController.flash.message
    }

    void testAjaxSendWithCaptchaFailure() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.ajaxSend()

        // Check result
        assertEquals "Unexpected redirect action returned!", "ajaxCreate",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!", null,
            contactFormController.flash.message
    }

    void testAjaxSendWithMailServiceFailure() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert config
        mockedConfig.grails.mail.to = "test@contactform.com"
        ConfigurationHolder.config = mockedConfig

        // Mock JCaptcha service
        mockJcaptchaService.demand.validateResponse() {
            def name, def sessionId, def response -> return true }
        contactFormController.jcaptchaService = mockJcaptchaService.createMock()

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.ajaxSend()

        // Check result
        assertEquals "Unexpected redirect action returned!", "ajaxCreate",
            contactFormController.modelAndView.viewName
        assertEquals "Unexpected flash message displayed!",
            "uk.co.anthonycampbell.grails.contactform.ContactForm.send.fail",
            contactFormController.flash.message
    }

    void testValidate() {
        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(validProperties)])

        // Insert parameters
        contactFormController.params.putAll(validProperties)

        // Run test
        contactFormController.validate()

        // Check result
        assertEquals "Unexpected error message displayed!", "",
            contactFormController.response.contentAsString
    }

    void testValidateWithEmptyField() {
        def errorCode = "error.code"

        // Declare field to validate
        def emptyField = [yourFullName: ""]

        // Mock contact form
        mockDomain(ContactForm, [new ContactForm(emptyField)])

        // Insert paerror.coderameters
        contactFormController.params.putAll(emptyField)
        
        // Mock message source
        contactFormController.messageSource =
            [getMessage: { def fieldError, def locale -> return errorCode }]

        // Run test
        contactFormController.validate()

        // Check result
        assertEquals "Unexpected error message displayed!", errorCode,
                contactFormController.response.contentAsString
    }
}