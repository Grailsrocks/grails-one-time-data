class OneTimeDataGrailsPlugin {
    // the plugin version
    def version = "1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = ['controllers':'1.1 > *']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Marc Palmer"
    def authorEmail = "marc@grailsrocks.com"
    def title = "A deferred data concept that replaces flash scope to work safely with multiple concurrent requests"
    def description = '''\\
Brief description of the plugin.
'''
    def observe = ['controllers']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/one-time-data"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        applyDynamicMethods(application)
    }

    void applyDynamicMethods(application) {
        def svc = application.mainContext.oneTimeDataService
        
        application.controllerClasses.each { cc ->
            cc.clazz.metaClass.oneTimeData = { String id, Closure dataSetup ->
                svc.store(delegate.request, id, dataSetup)
            }
            cc.clazz.metaClass.oneTimeData << { Closure dataSetup ->
                svc.store(delegate.request, dataSetup)
            }
            
            // Get the data from session and remove it from session, and put it in the request
            // so we can get it as much as we like in this request
            cc.clazz.metaClass.getOneTimeData = { String id ->
                def key = 'one.time.data.'+id
                def alreadyGotData = delegate.request[key]
                if (alreadyGotData) {
                    return alreadyGotData
                }
                def s = delegate.session
                def data = s[key]
                s.removeAttribute(key)
                delegate.request['one.time.data.found.'+id] = data != null
                delegate.request[key] = data
                return data
            }
            cc.clazz.metaClass.getOneTimeData << { ->
                delegate.getOneTimeData(delegate.params.id)
            }
        }
        
    }
    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        applyDynamicMethods(application)
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
