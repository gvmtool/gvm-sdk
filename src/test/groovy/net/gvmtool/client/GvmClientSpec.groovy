package net.gvmtool.client

import spock.lang.Specification
import wslite.http.HTTPClientException
import wslite.rest.RESTClient

class GvmClientSpec extends Specification {

    GvmClient gvmClient
    String apiUrl

    void setup(){
        apiUrl = "http://dev.gvmtool.net"
        gvmClient = new GvmClient(apiUrl)
    }

    void "should retrieve a list of remote candidate names"() {
        given:
        def candidates = "gaiden,gradle,grails,griffon,groovy,groovyserv,lazybones,play,springboot,vertx".split(",")

        when:
        List<String> results = gvmClient.getRemoteCandidates()

        then:
        results.containsAll(candidates)
    }

    void "should handle communication error on retrieving of remote candidates"() {
        given:
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        gvmClient.getRemoteCandidates()

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("boom")}

        and:
        thrown(GvmClientException)
    }

    void "should retrieve all remote versions for existing remote candidate"() {
        given:
        def candidate = "groovy"

        when:
        List<String> versions = gvmClient.getRemoteVersionsFor(candidate)

        then:
        versions.find { it == "2.2.1" }
        versions.find { it == "2.1.9" }
    }

    void "should return an empty list for non existing remote candidate"() {
        given:
        def candidate = "dada3*"

        when:
        List<String> versions = gvmClient.getRemoteVersionsFor(candidate)

        then:
        versions.empty
    }

    void "should handle communication error on retrieving remote candidate versions"() {
        given:
        def candidate = "groovy"
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        gvmClient.getRemoteVersionsFor(candidate)

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("boom")}

        and:
        thrown(GvmClientException)
    }

    void "should throw NullPointerException if null is given as gvmHome"() {
        given:
        String gvmHome = null

        when:
        new GvmClient(apiUrl, gvmHome)

        then:
        thrown(NullPointerException)
    }

    void "should throw AssertionError if bogus is given as gvmHome"() {
        given:
        String gvmHome = "/superroot"

        when:
        new GvmClient(apiUrl, gvmHome)

        then:
        thrown(AssertionError)
    }
}
