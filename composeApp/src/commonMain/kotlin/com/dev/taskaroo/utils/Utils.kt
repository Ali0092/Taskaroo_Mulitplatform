package com.dev.taskaroo.utils

import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem

object Utils {

    val sampleTasks = listOf(
            TaskData(
                id = "1",
                title = "Fix Critical Production Bug",
                subtitle = "Server downtime issue needs immediate attention",
                category = "Urgent",
                deadline = "2024-12-11",
                taskList = listOf(
                    TaskItem("1", "Identify root cause of server crash", isCompleted = true),
                    TaskItem("2", "Deploy hotfix to production"),
                    TaskItem("3", "Monitor system stability")
                ),
                completedTasks = 1
            ),
            TaskData(
                id = "2",
                title = "Complete Project Proposal",
                subtitle = "Finalize and submit the Q4 project proposal to the client",
                category = "High",
                deadline = "2024-12-15",
                taskList = listOf(
                    TaskItem("4", "Review project requirements"),
                    TaskItem("5", "Create budget breakdown"),
                    TaskItem("6", "Prepare presentation slides")
                ),
                completedTasks = 0
            ),
            TaskData(
                id = "3",
                title = "Team Meeting Preparation",
                subtitle = "Weekly sync with the development team",
                category = "Medium",
                deadline = "2024-12-12",
                taskList = listOf(
                    TaskItem("7", "Prepare agenda items", isCompleted = true),
                    TaskItem("8", "Review last week's action items")
                ),
                completedTasks = 1
            ),
            TaskData(
                id = "4",
                title = "Code Review",
                subtitle = "Review pull requests from team members",
                category = "High",
                deadline = "2024-12-12",
                taskList = listOf(
                    TaskItem("9", "Review authentication module"),
                    TaskItem("10", "Test API integration", isCompleted = true)
                ),
                completedTasks = 1
            ),
            TaskData(
                id = "5",
                title = "Documentation Update",
                subtitle = "Update API documentation for new endpoints",
                category = "Low",
                deadline = "2024-12-20",
                taskList = listOf(
                    TaskItem("11", "Document new authentication flow"),
                    TaskItem("12", "Update endpoint examples"),
                    TaskItem("13", "Review with technical writer", isCompleted = true)
                ),
                completedTasks = 1
            )
        )



}