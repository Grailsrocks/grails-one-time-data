package com.grailsrocks.onetimedata.tags

class OneTimeDataTagLib {
    static namespace = 'onetime'
    
    def exists = { attrs, body ->
        def id = attrs.id ?: params.id
        if (request['one.time.data.found.'+id]) {
            out << body(data:request['one.time.data.'+id])
        }
    }
    
    def missing = { attrs, body ->
        def id = attrs.id ?: params.id
        if (false == request['one.time.data.found.'+id]) {
            out << body()
        }
    }
    
    def notExpected = { attrs, body ->
        def id = attrs.id ?: params.id
        if (null == request['one.time.data.found.'+id]) {
            out << body()
        }
    }
}
