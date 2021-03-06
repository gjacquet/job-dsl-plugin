package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class IrcContext implements Context {
    List<IrcPublisherChannel> channels = []

    List<String> strategies = ['ALL', 'ANY_FAILURE', 'FAILURE_AND_FIXED', 'STATECHANGE_ONLY']

    List<String> notificationMessages = ['Default',  'SummaryOnly', 'BuildParameters', 'PrintFailingTests']

    String strategy

    String notificationMessage

    boolean notifyOnBuildStarts = false

    boolean notifyScmCommitters = false

    boolean notifyScmCulprits = false

    boolean notifyUpstreamCommitters = false

    boolean notifyScmFixers = false

    IrcContext() {
        strategy = strategies[0]
        notificationMessage = notificationMessages[0]
    }

    void channel(String name, String password = '', boolean notificationOnly = false) {
        checkNotNullOrEmpty(name, 'Channel name for irc channel is required!')

        channels << new IrcPublisherChannel(
            name: name,
            password: password,
            notificationOnly: notificationOnly
        )
    }

    void channel(Map args) {
        channel(args.name, args.password, args.notificationOnly)
    }

    void strategy(String strategy) {
        checkArgument(strategies.contains(strategy), "Possible values: ${strategies.join(',')}")

        this.strategy = strategy
    }

    void notificationMessage(String notificationMessage) {
        checkArgument(
            notificationMessages.contains(notificationMessage),
            "Possible values: ${notificationMessages.join(',')}"
        )

        this.notificationMessage = notificationMessage
    }

    void notifyScmCommitters(boolean value = true) {
        notifyScmCommitters = value
    }

    void notifyScmCulprits(boolean value = true) {
        notifyScmCulprits = value
    }

    void notifyUpstreamCommitters(boolean value = true) {
        notifyUpstreamCommitters = value
    }

    void notifyScmFixers(boolean value = true) {
        notifyScmFixers = value
    }

    @Canonical
    static class IrcPublisherChannel {
        String name
        String password
        boolean notificationOnly
    }
}

