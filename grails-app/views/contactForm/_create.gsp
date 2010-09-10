
<%@ page import="uk.co.anthonycampbell.grails.contactform.ContactForm" %>
                    <div id="createContactForm">
                    <g:if test="${flash.message}">
                        <div id="flashMessage">${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="${contactFormInstance}">
                        <div id="errorMessage"><g:message
                        code="uk.co.anthonycampbell.grails.contactform.ContactForm.validation.fail.message"
                        default="A problem was encountered when trying to process your form.
                        Please ensure that all the fields are complete and try again."/></div>
                    </g:hasErrors>
                        <g:formRemote id="formContactForm" name="formContactForm" action="send"
                        method="post"
                        update="createContactForm" url="[action:'ajaxSend']"
                        onLoading="displayLoading('createContactForm')"
                        onLoaded="displayResponse('createContactForm')">
                            <fieldset>
                                <legend><g:message code="uk.co.anthonycampbell.grails.contactform.ContactForm.legend" default="Contact form" /></legend>
    
                                <p><label for="yourFullName"><g:message code="uk.co.anthonycampbell.grails.contactform.ContactForm.yourFullName.label" default="Your Full Name" />:</label>
                                    <g:remoteText id="yourFullName" name="yourFullName" paramName="yourFullName" action="validate" update="yourFullNameFlash" maxlength="250" value="${contactFormInstance?.yourFullName}" /> <span id="yourFullNameFlash"><g:displayFieldError bean="${contactFormInstance}" field="yourFullName">${it}</g:displayFieldError></span></p>
    
                                <p><label for="yourEmailAddress"><g:message code="uk.co.anthonycampbell.grails.contactform.ContactForm.yourEmailAddress.label" default="Your Email Address" />:</label>
                                    <g:remoteText id="yourEmailAddress" name="yourEmailAddress" paramName="yourEmailAddress" action="validate" update="yourEmailAddressFlash" maxlength="250" value="${contactFormInstance?.yourEmailAddress}" /> <span id="yourEmailAddressFlash"><g:displayFieldError bean="${contactFormInstance}" field="yourEmailAddress">${it}</g:displayFieldError></span></p>
    
                                <p><label for="subject"><g:message code="uk.co.anthonycampbell.grails.contactform.ContactForm.subject.label" default="Subject" />:</label>
                                    <g:remoteText id="subject" name="subject" paramName="subject" action="validate" update="subjectFlash" maxlength="250" value="${contactFormInstance?.subject}" /> <span id="subjectFlash"><g:displayFieldError bean="${contactFormInstance}" field="subject">${it}</g:displayFieldError></span></p>
    
                                <p><label for="message"><g:message code="uk.co.anthonycampbell.grails.contactform.ContactForm.message.label" default="Message" />:</label>
                                    <g:remoteArea id="message" name="message" paramName="message" action="validate" update="messageFlash" cols="40" rows="5" value="${contactFormInstance?.message}" /> <span id="messageFlash"><g:displayFieldError bean="${contactFormInstance}" field="message">${it}</g:displayFieldError></span></p>
    
                                <p><label for="captcha"><g:message code="uk.co.anthonycampbell.grails.contactform.ContactForm.captcha.label" default="Captcha" />:</label>
                                    <g:remoteText id="captcha" name="captcha" paramName="captcha" action="validate" update="captchaFlash" maxlength="25" value="${contactFormInstance?.captcha}" /> <span id="captchaFlash"><g:displayFieldError bean="${contactFormInstance}" field="captcha">${it}</g:displayFieldError></span></p>
                                    <p><jcaptcha:jpeg name="captchaImage" height="60px" width="210px" /></p>
    
                                <p><input id="submit" class="button" type="submit" name="submit" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                                   <input id="reset" class="button" type="reset" name="reset" value="${message(code: 'default.button.reset.label', default: 'Reset')}" /></p>
                            </fieldset>
                        </g:formRemote>
                    </div>
