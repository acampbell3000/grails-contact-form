
<%@ page import="uk.co.anthonycampbell.grails.plugins.contactform.ContactForm" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'contactForm.label', default: 'ContactForm')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
                <h1><g:message code="uk.co.anthonycampbell.grails.plugins.contactform.ContactForm.create.label" /></h1>
                    <ul id="nav">
                        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
                    </ul>

                    <g:render template="create" model="['contactFormInstance': contactFormInstance]" />
    </body>
</html>
