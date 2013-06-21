package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

public class TriggerHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    TriggerContextHelper helper = new TriggerContextHelper(mockActions, JobType.Freeform)
    TriggerContext context = new TriggerContext()

    def 'call github trigger methods'() {
        when:
        context.githubPush()

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def githubPushTrigger = context.triggerNodes[0]
        githubPushTrigger.name() == 'com.cloudbees.jenkins.GitHubPushTrigger'
        githubPushTrigger.attribute('plugin') == "github@1.6"
        githubPushTrigger.spec[0].value() == ''
    }

    def 'call urltrigger with proxy, etag and last modified check'() {
        when:
        context.urlTrigger {

            url('http://www.example.com/some/url') {
                proxy true
                check 'etag'
                check 'lastModified'
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
    }

    def 'call urltrigger with inspection for content change'() {
        when:
        context.urlTrigger {

            url('http://www.example.com/some/other/url') {
                inspection 'change'
            }

        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1

        def utc = context.triggerNodes[0]
        utc.attribute('plugin') == 'urltrigger@0.31'
        utc.entries != null
        utc.entries.size() == 1

        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.inspectingContent[0].value() == true
        entry.contentTypes != null
        entry.contentTypes.size() == 1
        entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.SimpleContentType' != null
    }

    def 'call urltrigger with JSON path inspection'() {

        when:
        context.urlTrigger {

            url('http://www.example.com/some/other/url') {
                inspection('json') {
                    path('/foo/bar')
                    path('/*/baz')
                }
            }

        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1

        def utc = context.triggerNodes[0]
        utc.attribute('plugin') == 'urltrigger@0.31'
        utc.entries != null
        utc.entries.size() == 1

        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.inspectingContent[0].value() == true
        entry.contentTypes != null
        entry.contentTypes.size() == 1
        entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.JSONContentType' != null

        def ct =  entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.JSONContentType'[0]
        ct.jsonPaths != null
        ct.jsonPaths.size() == 1

        def paths = ct.jsonPaths[0]
        def contentEntries = paths.'org.jenkinsci.plugins.urltrigger.content.JSONContentEntry'
        contentEntries!= null
        contentEntries.size() == 2
        contentEntries[0].jsonPath != null
        contentEntries[0].jsonPath[0].value() == '/foo/bar'
        contentEntries[1].jsonPath != null
        contentEntries[1].jsonPath[0].value() == '/*/baz'

    }

    def 'call urltrigger with XML path inspection'() {

        when:
        context.urlTrigger {
            url('http://www.example.com/some/other/url') {
                inspection('xml') {
                    path('//*[@name="foo"]')
                }
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1

        def utc = context.triggerNodes[0]
        utc.attribute('plugin') == 'urltrigger@0.31'
        utc.entries != null
        utc.entries.size() == 1

        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.inspectingContent[0].value() == true
        entry.contentTypes != null
        entry.contentTypes.size() == 1
        entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.XMLContentType' != null

        def ct =  entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.XMLContentType'[0]
        ct.xPaths != null
        ct.xPaths.size() == 1

        def paths = ct.xPaths[0]
        def contentEntries = paths.'org.jenkinsci.plugins.urltrigger.content.XMLContentEntry'
        contentEntries!= null
        contentEntries.size() == 1
        contentEntries[0].xPath != null
        contentEntries[0].xPath[0].value() == '//*[@name="foo"]'

    }

    def 'call urltrigger methods with defaults and check for response status'() {
        when:
        context.urlTrigger() {
            url('http://www.example.com/some/url') {
                check 'status'
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.attribute('plugin') == 'urltrigger@0.31'
        utc.spec[0].value() == 'H/5 * * * *'
        utc.labelRestriction != null
        utc.labelRestriction.size() == 1
        utc.labelRestriction[0].value() == false
        utc.entries != null
        utc.entries.size() == 1

        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.url != null
        entry.url.size() == 1
        entry.url[0].value() == 'http://www.example.com/some/url'
        entry.statusCode[0].value() == 200
        entry.timeout[0].value() == 300
        entry.checkStatus[0].value() == true
    }

    def 'call urltrigger methods with non-default status code and timeout'() {
        when:
        context.urlTrigger() {
            url('http://www.example.com/some/url') {
                status 404
                timeout 6000
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.attribute('plugin') == 'urltrigger@0.31'
        utc.entries != null
        utc.entries.size() == 1

        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.url != null
        entry.url.size() == 1
        entry.url[0].value() == 'http://www.example.com/some/url'
        entry.statusCode[0].value() == 404
        entry.timeout[0].value() == 6000
    }

    def 'call urltrigger methods with non-default cron'() {
        when:
        context.urlTrigger() {
            cron '* 0 * 0 *'
            url 'http://www.example.com/some/url'
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.attribute('plugin') == 'urltrigger@0.31'
        utc.spec[0].value() == '* 0 * 0 *'
    }

    def 'call urltrigger methods with label restriction'() {
        when:
        context.urlTrigger() {
            restrictToLabel "foo"
            url 'http://www.example.com/some/url'
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.attribute('plugin') == 'urltrigger@0.31'
        utc.labelRestriction[0].value() == true
        utc.triggerLabel[0].value() == 'foo'
    }

    def 'call cron trigger methods'() {
        when:
        context.cron('*/10 * * * *')

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def timerTrigger = context.triggerNodes[0]
        timerTrigger.name() == 'hudson.triggers.TimerTrigger'
        timerTrigger.spec[0].value() == '*/10 * * * *'
    }

    def 'call scm trigger methods'() {
        when:
        context.scm('*/5 * * * *')

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def timerTrigger = context.triggerNodes[0]
        timerTrigger.name() == 'hudson.triggers.SCMTrigger'
        timerTrigger.spec[0].value() == '*/5 * * * *'
    }

    def 'call trigger via helper'() {
        when:
        helper.triggers {
            cron('0 12 * * * *')
        }

        then:
        1 * mockActions.add(_)

        // TODO Support this notation
//        when:
//        helper.trigger.cron('0 13 0 0 0 0')
//
//        then:
//        1 * mockActions.add(_)
    }

    def 'call empty gerrit trigger methods'() {
        when:
        context.gerrit {
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def gerritTrigger = context.triggerNodes[0]
        gerritTrigger.name().contains('GerritTrigger')
        !gerritTrigger.buildStartMessage.isEmpty()
    }

    def 'call advanced gerrit trigger methods'() {
        when:
        context.gerrit {
            events {
                ChangeMerged
                DraftPublished
            }
            project('reg_exp:myProject', ['ant:feature-branch', 'plain:origin/refs/mybranch']) // full access
            project('test-project', '**') // simplified
            configure { node ->
                node / gerritBuildSuccessfulVerifiedValue << '10'
            }
        }

        then:
        def gerritTrigger = context.triggerNodes[0]
        gerritTrigger.gerritBuildSuccessfulVerifiedValue.size() == 1
        gerritTrigger.gerritBuildSuccessfulVerifiedValue[0].value() as String == '10'

        gerritTrigger.gerritBuildFailedCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildFailedCodeReviewValue[0].value() == '-2'

        Node gerritEvents = gerritTrigger.triggerOnEvents[0]
        gerritEvents.children().size() == 2
        gerritEvents.children()[0].name().contains('com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin')

        Node gerritProjects = gerritTrigger.gerritProjects[0]
        gerritProjects.children().size() == 2

        Node gerritProject = gerritProjects.children()[0]
        gerritProject.name() == 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject'
        gerritProject.compareType[0].value() == 'REG_EXP'
        gerritProject.pattern[0].value() == 'myProject'
        gerritProject.branches[0].children().size() == 2

        Node gerritBranch = gerritProject.branches[0].children()[0]
        gerritBranch.name() == 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch'
        gerritBranch.compareType[0].value() == 'ANT'
        gerritBranch.pattern[0].value() == 'feature-branch'

        Node gerritProjectSimple = gerritProjects.children()[1]
        gerritProjectSimple.compareType[0].value() == 'PLAIN'
        gerritProjectSimple.pattern[0].value() == 'test-project'
        gerritProjectSimple.branches[0].children().size() == 1
        // Assume branch is fine
    }

    def 'execute withXml Action'() {
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))
        def nodeBuilder = new NodeBuilder()

        Node triggerNode = nodeBuilder.'hudson.triggers.SCMTrigger' {
            spec '2 3 * * * *'
        }

        when:
        def withXmlAction = helper.generateWithXmlAction(new TriggerContext([], JobType.Freeform, [triggerNode]))
        withXmlAction.execute(root)

        then:
        root.triggers[0].'hudson.triggers.SCMTrigger'[0].spec[0].text() == '2 3 * * * *'
    }

    def 'call snapshotDependencies for free-style job fails'() {
        when:
        context.snapshotDependencies(false)

        then:
        thrown(IllegalStateException)
    }

    def 'call snapshotDependencies for Maven job succeeds'() {
        when:
        TriggerContext context = new TriggerContext([], JobType.Maven, [])
        context.snapshotDependencies(false)

        then:
        context.withXmlActions != null
        context.withXmlActions.size() == 1
    }
}
