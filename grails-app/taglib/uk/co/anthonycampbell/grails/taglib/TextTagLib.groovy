package uk.co.anthonycampbell.grails.taglib;

import org.springframework.beans.SimpleTypeConverter

import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.RequestContextUtils as RCU;

/**
 * Text Tag Library
 * 
 * This library generic text tags for common features used
 * throughout the website:
 * <ul>
 * <li>css</li>
 * <li>copyright</li>
 * <li>remoteText</li>
 * <li>remoteArea</li>
 * <li>remoteSelect</li>
 * <li>displayFieldError</li>
 * </ul>
 */
class TextTagLib {
	// Initialise lib property
	def typeConverter = new SimpleTypeConverter()

	/*
	 * CSS Tag
	 * 
	 * Builds a CSS link tag. Detects that the css file actually
	 * exists and then outputs a valid css link.
	 * 
	 * Example: <g:css src="default.css" />
	 */
	def css = { attributes ->
	
		//Declare variables
		def output
		def realPath
		def io
		
		//Source
		if (attributes['src'] != null) {
			output = """<link rel="stylesheet" href="${grailsAttributes.getApplicationUri(request)}""" +
				"""/css/${attributes['src']}" />"""
		} else {
			throwTagError("Tag [css] is missing required attribute [src]")
		}
		
		//Validate source
		realPath = grailsAttributes.getServletContext().getRealPath("css/${attributes['src']}")
		if(realPath != null) {
			io = new java.io.File(realPath)
			if(io.exists() && io.isFile()) {
				out << output
			} else {
				log.error("Tag [css] is unable to find the specified css file [${realPath}]")	
				out << ""
			}
		} else {
			log.error("Tag [css] is unable to find the specified css file []")
			out << ""
		}
	}

	/*
	 * Copyright Tag
	 * 
	 * Builds the copyright text. Automatically determines the user's locale
	 * and inserts the current year. Optional parameters of "code" allows
	 * the message key to be overridden.
	 * 
	 * Example: <g:copyright code="footer.copyright">
	 */
	def copyright = { attributes ->
	
		//Declare variables
		def output
 		String year = new GregorianCalendar().get(GregorianCalendar.YEAR)
 		
		//Copyright attribute
		if (attributes['code'] != null) {
			//Load message properties file
	 		def messageSource = grailsAttributes.
	 			getApplicationContext().
	 			getBean("messageSource")
			
			def args = [year, grailsApplication.getMetadata().get("app.name")]
			
            //Get message value
			def copyright = messageSource.getMessage(
                attributes['code'],
                args == null ? null : args.toArray(),
                null,
                RCU.getLocale(request)
			);
			
			//Store name
			if(copyright != null) {
				output = """${copyright}"""
			} else {
				output = """&copy; ${year}, All Rights Reserved"""
			}
		} else {
			//Use default
			output = """&copy; ${year}, All Rights Reserved"""
		}
		
		//Output
		out << output
	}

	/**
	 * Display a remote text field
	 *
	 * Based on the standard "remoteField" tag library. Supports additional attributes
	 * such as "readonly" and "password".
	 */
	def remoteText = { attrs, body ->
		// Get attributes
		def paramName = attrs.paramName ? attrs.remove('paramName') : 'value'
		def value = attrs.remove('value')
		def readonly = attrs.remove('readonly')
		def password = attrs.remove('password')
		def type = 'text'

		// Validate attributes
		if(!value) value = ''
		if(!readonly) {
			readonly = ''
		} else {
			readonly = 'readonly=\"readonly\" '
		}
		if(password && password == "password") type = 'password'

		out << "<input type=\"${type}\" id=\"${attrs.remove('id')}\" name=\"${attrs.remove('name')}\" value=\"${value}\" ${readonly}onkeyup=\""

		if (attrs.params) {
			if (attrs.params instanceof Map) {
				attrs.params.put(paramName,
					new org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue('this.value'))
			} else {
				attrs.params += "+'${paramName}='+this.value"
			}
		} else {
			attrs.params = "'${paramName}='+this.value"
		}

		out << remoteFunction(attrs)
		
		attrs.remove('params')
		out << "\""

		attrs.remove('url')
		attrs.each {
			k,v-> out << " $k=\"$v\""
		}
		out <<" />"
	}

	/**
	 * Display a remote text field
	 *
	 * Based on the standard "remoteField" tag library. Supports additional attributes
	 * such as "readonly" and "password".
	 */
	def remoteArea = { attrs, body ->
		// Get Attributes
		def paramName = attrs.paramName ? attrs.remove('paramName') : 'value'
		def value = attrs.remove('value')
		def readonly = attrs.remove('readonly')
		def cols = attrs.remove('cols')
		def rows = attrs.remove('rows')

		// Validate attributes
		if(!value) value = ''
		if(!cols) {
			cols = ''
		} else {
			cols = 'cols=\"' + cols + '\" '
		}
		if(!rows) {
			rows = ''
		} else {
			rows = 'rows=\"' + rows + '\" '
		}
		if(!readonly) {
			readonly = ''
		} else {
			readonly = 'readonly=\"readonly\" '
		}

		out << "<textarea id=\"${attrs.remove('id')}\" name=\"${attrs.remove('name')}\" ${cols}${rows}${readonly}onkeyup=\""

		if (attrs.params) {
			if (attrs.params instanceof Map) {
				attrs.params.put(paramName,
					new org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue('this.value'))
			} else {
				attrs.params += "+'${paramName}='+this.value"
			}
		} else {
			attrs.params = "'${paramName}='+this.value"
		}

		out << remoteFunction(attrs)

		attrs.remove('params')
		out << "\""
		out <<" />${value}</textarea>"
	}

	/**
	 * Display a remote select field
	 *
	 * Based on the standard "remoteField" tag library.
	 *
	 * Examples:
     * <g:select name="user.age" from="${18..65}" value="${age}" />
     * <g:select name="user.company.id" from="${Company.list()}" value="${user?.company.id}" optionKey="id" />
	 */
	def remoteSelect = {attrs ->

		def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
        def locale = RCU.getLocale(request)
        def writer = out

        attrs.id = attrs.id ? attrs.id : attrs.name
        def from = attrs.remove('from')
        def keys = attrs.remove('keys')
        def optionKey = attrs.remove('optionKey')
        def optionValue = attrs.remove('optionValue')
        def value = attrs.remove('value')
		def paramName = attrs.paramName ? attrs.remove('paramName') : 'value'

        if (value instanceof Collection && attrs.multiple == null) {
            attrs.multiple = 'multiple'
        }

        def valueMessagePrefix = attrs.remove('valueMessagePrefix')
        def noSelection = attrs.remove('noSelection')

        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }

        def disabled = attrs.remove('disabled')
        if (disabled && Boolean.valueOf(disabled)) {
            attrs.disabled = 'disabled'
        }

		writer << "<select id=\"${attrs.remove('id')}\" name=\"${attrs.remove('name')}\" onchange=\""

		if (attrs.params) {
			if (attrs.params instanceof Map) {
				attrs.params.put(paramName,
					new org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue('this.value'))
			} else {
				attrs.params += "+'${paramName}='+this.value"
			}
		} else {
			attrs.params = "'${paramName}='+this.value"
		}

		out << remoteFunction(attrs)

		attrs.remove('params')
		out << "\" "

		// Process remaining attributes
		outputAttributes(attrs)

		writer << '>'
		writer.println()

		if (noSelection) {
			renderNoSelectionOption(noSelection.key, noSelection.value, value)
			writer.println()
		}

		// Create options from list
		if (from) {
			from.eachWithIndex {el, i ->
				def keyValue = null
				writer << '\t\t\t<option '

				if (keys) {
					keyValue = keys[i]
					remoteWriteValueAndCheckIfSelected(keyValue, value, writer)
				} else if (optionKey) {
					if (optionKey instanceof Closure) {
						keyValue = optionKey(el)
					} else if (el != null && optionKey == 'id'
						&& grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE,
							el.getClass().name)) {
						keyValue = el.ident()
					} else {
						keyValue = el[optionKey]
					}

					remoteWriteValueAndCheckIfSelected(keyValue, value, writer)
				} else {
					keyValue = el
					remoteWriteValueAndCheckIfSelected(keyValue, value, writer)
				}

				writer << '>'

				if (optionValue) {
					if (optionValue instanceof Closure) {
						writer << optionValue(el).toString().encodeAsHTML()
					} else {
						writer << el[optionValue].toString().encodeAsHTML()
					}
				} else if (valueMessagePrefix) {
					def message = messageSource.getMessage("${valueMessagePrefix}.${keyValue}", null,
						null, locale)

					if (message != null) {
						writer << message.encodeAsHTML()
					} else if (keyValue) {
						writer << keyValue.encodeAsHTML()
					} else {
						def s = el.toString()
						if (s) writer << s.encodeAsHTML()
					}
				} else {
					def s = el.toString()
					if (s) writer << s.encodeAsHTML()
				}

				writer << '</option>'
				writer.println()
			}
		}
		
		// Close tag
		writer << '\t\t\t</select>'
	}

	/**
	 * Dump out attributes in HTML compliant fashion.
	 */
	void outputAttributes(attrs) {
		attrs.remove('tagName')	// Just in case one is left
		attrs.each {k, v ->
			out << k << "=\"" << v.encodeAsHTML() << "\" "
		}
	}

	def renderNoSelectionOption = {noSelectionKey, noSelectionValue, value ->
		// If a label for the '--Please choose--' first item is supplied, write it out
		out << '\t\t\t<option value="' << (noSelectionKey == null ? "" : noSelectionKey) << '"'
		if (noSelectionKey.equals(value)) {
			out << ' selected="selected" '
		}

		out << '>' << noSelectionValue.encodeAsHTML() << '</option>'
	}

	private String optionValueToString(def el, def optionValue) {
		if (optionValue instanceof Closure) {
			return optionValue(el).toString().encodeAsHTML()
		}

		el[optionValue].toString().encodeAsHTML()
	}

	/**
	 * Display field Error
	 * 
	 * Outputs the error (if any) of the selected field from the selected
	 * bean. Error message is built from the user's selected locale.
	 * 
	 * Example: <g:displayFieldError bean="\${MessageInstance}" field="subject">
	 */
	def displayFieldError = { attributes, body ->
	
		// Declare variables
		def errors
        
		// Validate attributes
		if (attributes['bean'] == null) {
			throwTagError("Tag [fieldError] is missing required attribute [bean]")			
		} else if(attributes['field'] == null) {
			throwTagError("Tag [fieldError] is missing required attribute [field]")
		}

		// Get errors from selected bean
        if (attributes['bean'] instanceof Errors) {
            errors = attributes['bean']
        } else {
            def mc = GroovySystem.metaClassRegistry.getMetaClass(attributes['bean'].getClass())
            if (mc.hasProperty(attributes['bean'], 'errors')) {
                errors = attributes['bean'].errors
            }
        }
    	
		// Build output
        if (errors?.hasErrors() && errors.hasFieldErrors(attributes['field'])) {   	
			//Load message properties file
	 		def messageSource = grailsAttributes.
	 			getApplicationContext().
	 			getBean("messageSource")

            // Get error message value
			def errorMessage = messageSource.getMessage(
                errors.getFieldError(attributes['field']),
                RCU.getLocale(request)
			);
			
        	// Return to body
        	out << body(errorMessage)
        }
	}
	
	/**
	 * Add the value to the provided write and check whether the
	 * value should be selected.
	 *
	 * @param keyValue the key's value.
	 * @param value the value.
	 * @param writer the writer.
	 */
	private remoteWriteValueAndCheckIfSelected(keyValue, value, writer) {

		boolean selected = false
		def keyClass = keyValue?.getClass()
		if (keyClass.isInstance(value)) {
			selected = (keyValue == value)
		} else if (value instanceof Collection) {
			selected = value.contains(keyValue)
		} else if (keyClass && value) {
			try {
				value = typeConverter.convertIfNecessary(value, keyClass)
				selected = (keyValue == value)
			} catch (Exception) {
				// ignore
			}
		}

		writer << "value=\"${keyValue}\" "
		if (selected) {
			writer << 'selected="selected" '
		}
	}
}
