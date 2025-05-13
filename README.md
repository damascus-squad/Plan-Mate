# Plan-Mate

CLI Task Management Application

PlanMate is a modern Kotlin-based task management system, designed with SOLID principles, test-driven development (TDD),
and a clear architecture. It features a command-line interface (CLI) for easy management of projects, tasks, and teams.
It also supports user roles, dynamic project statuses, and a detailed audit trail.

## Features

-  -  -

#### User Roles

- Admin: Can manage projects, users, states, and view full audit trails.
- Mate: Can manage tasks within existing projects.

#### Core Functionalities

- **Authentication** with MD5-hashed passwords (no plain-text storage).
- **Projects:** Admins can create, edit, and delete projects.
- **Tasks:** Mates and admins can add, edit, and remove tasks.
- **Task States:** e.g., TODO, InProgress, Done.
- **Audit Log:** Track changes (who, what, when) at both project and task level.
- **Cloud Storage** via MongoDB (after migration from csv).
- **Clean Architecture** with separated UI, logic, and data layers.
- **Dependency Injection** via Koin.
- **Unit Test Coverage** using TDD principles.
- **Swimlane View:** Console-based visual representation of task states.
- - -

## Package Structure

```plaintext
 planmate/
│
├── data              
│   ├── repo      
│   └── source            
│
├── logic             
│   ├── exception          
│   ├── model  
│   ├── repo  
│   ├── usecases          
│   └── service       
│
├── ui                
│   └── cli               
│             
└── di
```

## MongoDB Integration

PlanMate has migrated from local CSV-based storage to cloud-based MongoDB

- - -

## Setup Instructions

- Kotlin 2.1.20
- JDK 17
- MongoDB
- Git