# Feature: Update IsDormant and IsActive Attributes of a GL based Daily

# Background:
#     Given the system properties for dormancy is set
#     | Property Name          | Value |
#     | daysForDormancy        | x   |
#     | daysForInactivity      | y   |
#     And the list of all GLs
#     And today's date
#     And the list of all GLs with transactions today


#   Scenario: When a GL has been involved in at least one transaction today
#     When a GL has been involved in a transaction today
#     Then that GL should not be marked as inactive
#     And that GL should not be marked as dormantL
#     And the Last Transaction Date attribute of that GL should be updated to today

#   Scenario: When a GL has NOT been involved in any transaction today
#     When a GL has not been involved in any transaction today
#     And  the Last Transaction date of this GL is older than or equal to today's date minus daysForDormancy
#     Then that GL should be marked as dormant
#     And that GL should be marked as Inactive
#     And the Last Transaction Date attribute of that GL should not be updated


#  Scenario: When a GL has NOT been involved in any transaction today
#     When a GL has not been involved in any transaction today
#     And the Last Transaction date of any GL is less than today's date minus daysForDormancy
#     And the Last Transaction date of this GL is older than or equal to today's date minus daysForInactivity
#     Then that GL should not be marked as dormant
#     And that GL should be marked as Inactive
#     And the Last Transaction Date attribute of that GL should not be updated


#  Scenario: When a GL has NOT been involved in any transaction today
#     When a GL has not been involved in any transaction today
#     And the Last Transaction date of any GL is less than today's date minus daysForInactivity
#     Then that GL should not be marked as inactive
#     And that GL should not be marked as dormant
#     And the Last Transaction Date attribute of that GL should not be updated