Feature: insert talk

Scenario: insert talk with success
Given a talk with title "arquillian" with 5 slots and 5 attenddes
When i persist the talk 
Then i should have 1 talk

Scenario: insert talk with more attenddes than slots
Given a talk with title "arquillian" with 5 slots and 6 attenddes
When i persist the talk 
Then i should have 0 talk