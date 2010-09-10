package uk.co.anthonycampbell.grails.contactform

import org.springframework.web.servlet.support.RequestContextUtils as RCU
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * ContactForm controller
 *
 * Controller which handles all of the common actions for the ContactForm
 * domain class. In addition, the class also provides support for ajax
 * requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class ContactFormController {

    // Declare properties
    def mailService
    def jcaptchaService
    def messageSource
    def emailValidator = EmailValidator.getInstance()
    
    // Send actions only accept POST requests
	static allowedMethods = [send:'POST', ajaxSend:'POST']

    /**
     * Re-direct index requests to create form
     */
	def index = {
		redirect(action: "create", params: params)
	}

    /**
     * Initialise form and render view
     */
    def create = {
        def contactFormInstance = new ContactForm()
        contactFormInstance.properties = params
        return [contactFormInstance: contactFormInstance]
    }

    /*
     * Validate an individual field
     */
    def validate = {
        // Initialise domain instance and error message
        def contactFormInstance = new ContactForm(params)
        def errorMessage = ""
        def field = ""

        // Get selected field
        for (param in params) {
            if (param.key != null && !param.key.equals("action")
                    && !param.key.equals("controller")) {
                field = param.key
                break
            }
        }

		log.debug("Validating field: " + field)

        // Check whether provided field has errors
        if (!contactFormInstance.validate() && contactFormInstance.errors.hasFieldErrors(field)) {
            // Get error message value
            errorMessage = messageSource.getMessage(
                contactFormInstance.errors.getFieldError(field),
                RCU.getLocale(request)
            )

            log.debug("Error message: " + errorMessage)
        }

        // Render error message
        render(errorMessage)
    }

    /**
     * Send a contact message from a non Ajax based request.
     */
    def send = {
        doSend(false)
    }

    /*
     * Send a contact message from an Ajax based request.
     */
    def ajaxSend = {
        doSend(true)
    }

    /**
     * Attempt to validate and send the provided contact form instance.
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doSend(boolean isAjax) {
        def parameters = params
        def contactFormInstance = new ContactForm(parameters)
        def toEmailAddress = ConfigurationHolder.config.grails.mail.to
        def validMessage = true
        def validCaptcha = true
        def view = "create"
        if(isAjax) view = "_create"

        log.debug("Begin contact form send (isAjax = " + isAjax + ")")

        log.debug("Validating destination address: " + toEmailAddress)

        // Validate "destination" address
        if (toEmailAddress != null && !(toEmailAddress instanceof ConfigObject) &&
            !StringUtils.isBlank(toEmailAddress) && emailValidator.isValid(toEmailAddress)) {

            log.debug("Begin contact form validation")

            // Validate form details
            if (!contactFormInstance.validate()) {
                log.debug("Contact form validation failed")
                validMessage = false;
            }

            // Is incoming message from a bot?
            try {
                if (!jcaptchaService.validateResponse("captchaImage", session.id, parameters.captcha)) {
                    log.debug("JCaptcha validation failed")

                    // Reject captcha value
                    contactFormInstance.errors.rejectValue('captcha',
                        'uk.co.anthonycampbell.grails.contactform.ContactForm.captcha.doesnotmatch',
                        'The provided text does not match the image below')
                    validCaptcha = false
                }
            } catch (Exception ex) {
                log.debug("JCaptcha validation failed")
                
                // Reject captcha value
                contactFormInstance.errors.rejectValue('captcha',
                    'uk.co.anthonycampbell.grails.contactform.ContactForm.captcha.doesnotmatch',
                    'The provided text does not match the image below')
                validCaptcha = false
            }

            // Send message
            if (validMessage && validCaptcha) {
                log.debug("Attempting to send message through mail server")

                try {
                    mailService.sendMail {
                        to toEmailAddress
                        from contactFormInstance.yourFullName + " <" + contactFormInstance.yourEmailAddress + ">"
                        subject contactFormInstance.subject
                        body contactFormInstance.message
                    }

                    log.debug("Contact form message successfully sent")

                    // Reset form
                    contactFormInstance = new ContactForm()

                    flash.message = "${message(code: 'uk.co.anthonycampbell.grails.contactform.ContactForm.success')}"
                    render(view: view, model: [contactFormInstance: contactFormInstance])
                    
                } catch (Exception e) {
                    log.error("Unable to send a contact form message - send mail has thrown an exception. " +
                        "Please ensure your mail configuration declared in Config.groovy is up-to-date.", e)

                    flash.message = "${message(code: 'uk.co.anthonycampbell.grails.contactform.ContactForm.send.fail')}"
                    render(view: view, model: [contactFormInstance: contactFormInstance])
                }
            } else {
                render(view: view, model: [contactFormInstance: contactFormInstance])
            }
        } else {
            log.error("Unable to send a contact form message - invalid destination address. Please ensure " +
                "you have declared the property grailsApplication.config.grails.mail.to in your " +
                "application's config.")

            flash.message = "${message(code: 'uk.co.anthonycampbell.grails.contactform.ContactForm.destination.address.not.found.message')}"
            render(view: view, model: [contactFormInstance: contactFormInstance])
        }
    }
}
