# This is an example Dangerfile containing many useful examples of what can be done with Danger

# Make sure you read every comment (#) to enjoy proper Danger setup
# Every part of this setup is optional, so pick, remove, adjust and add as you need
# You can remove the comments afterwards

# To setup Danger with repo on Github.com you need to add DANGER_GITHUB_API_TOKEN for a user to your CI secret and expose it for PRs

# To setup Danger on enterprise Github, you need to also add these environment variables:
# DANGER_GITHUB_HOST=git.corp.goodcorp.com - host that the Github is running on
# DANGER_GITHUB_API_BASE_URL=https://git.corp.goodcorp.com/api/v3 - host at which the Github API is reachable on

# To setup Danger for Slack, you need to add SLACK_API_TOKEN to your CI secrets - a token for a bot user on Slack

# Check env variable:
def isEnvBlank(env)
 return env.nil? || env.empty?
end

# Danger actions:

# Android Lint running and reporting:
unless isEnvBlank(ENV["LINT_GRADLE_TASK"]) || isEnvBlank(ENV["LINT_REPORT_PATH"])
    # Unfortunately android_lint plugin forces lint task running, so you have to specify lint task name
    # Remember to remove lint task from Bitrise
    android_lint.gradle_task = ENV["LINT_GRADLE_TASK"]
    android_lint.filtering = true

    # Specify your exact report location
    android_lint.report_file = ENV["LINT_REPORT_PATH"]
    android_lint.lint(inline_mode: true)
end

# Detekt reporting:
unless isEnvBlank(ENV["DETEKT_REPORT_PATH"])
    kotlin_detekt.filtering = true
    # Skip default gradle task instead you should always use `detektGenerateMergedReport` task which
    # supports multimodule setup and will run checks across every module present in your app
    kotlin_detekt.skip_gradle_task = true

    # Specify your exact report location - for multi-module projects you should look at buildscript/detekt.gradle in NAT
    # A task called detektGenerateMergedReport in there will generate a single-file report for you
    kotlin_detekt.report_file = ENV["DETEKT_REPORT_PATH"]
    kotlin_detekt.detekt(inline_mode: true)
end

# JUnit test reporting:

# JUnit just parses already existing reports
junit_tests_dir = "**/test-results/**/*.xml"
Dir[junit_tests_dir].each do |file_name|
  junit.parse file_name
  junit.report
end

# Jacoco reporting:
unless isEnvBlank(ENV["JACOCO_REPORT_PATH"])
    # Uncomment to enforce minimum coverage of your choice, causing build fail when this is not met:
    #jacoco.minimum_project_coverage_percentage = 50
    #jacoco.minimum_class_coverage_percentage = 75

    # Specify your exact report location
    jacoco.report(ENV["JACOCO_REPORT_PATH"])
end

# Jira link commenting (based on PR title or commits messages):
unless isEnvBlank(ENV["JIRA_IDENTIFIERS"]) || isEnvBlank(ENV["JIRA_SUBDOMAIN"])
    jira.check(
        key: ENV["JIRA_IDENTIFIERS"].split(","), #first part of your typical task identifier, like MOB-250
        url: "https://#{ENV["JIRA_SUBDOMAIN"]}.atlassian.net/browse", # put your jira subdomain in place of "netguru" or leave as is
        search_title: true,
        search_commits: false,
        fail_on_warning: false,
        report_missing: true,
        skippable: true # you can skip this check by putting [no-jira] in PR's title
    )
end

# Notifying selected slack channel about finished build:

# get status_report text for Slack
# @return [[String]]
def slack_report
  errors_count = status_report[:errors].count
  warnings_count = status_report[:warnings].count
  "There were #{errors_count} errors and #{warnings_count} warnings."
end

emoji = [":rocket:", ":parrot:", ":fire:", ":hammer:"].sample
unless isEnvBlank(ENV["SLACK_NOTIFICATION_CHANNEL"])
    # Update channel to the one you want to notify
    slack.notify(channel: "##{ENV["SLACK_NOTIFICATION_CHANNEL"]}", text: "Hello, a build just finished! #{emoji}\n#{slack_report}\nFind it here: #{github.pr_json['html_url']}")
end
