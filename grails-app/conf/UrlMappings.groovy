class UrlMappings {
        static mappings = {
            "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }

        "/contact" {
            controller = "contactForm"
        }

        "/"(view:"/index")
        "500"(view:"/error")
    }
}

