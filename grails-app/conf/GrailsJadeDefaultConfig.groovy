grails {
	plugin {
		jade {
			/* Format the HTML or compress the output by removing unecessary whitespace. */
			prettyPrint = true

			/* Store the parsed template or reparse for every request. */
			caching = false

			/* Render exceptions in view or fail silently. */
			renderExceptions = true

			/* Additional filters for embedding different content types in a template, such as markdown, coffeescript */
			filters = [:]

			/* Default objects available to all templates. */
			sharedVariables = [:] 
		}
	}
}