package com.grailsrocks.onetimedata.services

import org.springframework.web.context.request.RequestContextHolder

class OneTimeDataService {
    static transactional = false
    
     def store(req, String id, Closure dataSetup) {
        def session = req.session
        def key = 'one.time.data.'+id
        def data = session[key]
        if (data == null) {
            session[key] = data = [:]
        }
        dataSetup.delegate = data 
        dataSetup()

        // Put it in the request so we can use it in this request without
        // removing it from session
        req.setAttribute('one.time.data.'+id, data)
        return id
    }
    
    def store(String id, Closure dataSetup) {
        store(RequestContextHolder.currentRequestAttributes().currentRequest, id, dataSetup)
    }
    
    def store(request, Closure dataSetup) {
        def id = System.currentTimeMillis().toString()
        store(request, id, dataSetup)
    }

    def store(Closure dataSetup) {
        store(RequestContextHolder.currentRequestAttributes().currentRequest, id, dataSetup)
    }

}