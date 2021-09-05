Library Management Application
===

Console application for managing a fake library, developed using domain-driven-design.
Beside the business logic, the project contains a custom implementation of simple
ORM-like library, helpers for dependency management and other patterns
I experimented with.

## Grading requirements

At least 8 entities in the domain:
- Author
- Book
- ReadingTracker
- Review
- Comment
- Shelve
- User
- Right

At least 10 operations/actions within the application:
- Login & registration 
- Add new books to the library 
- Manage bookshelves 
- Manage users and their rights 
- Borrow books 
- Add comments to reviews
- Publish reviews about books
- Track your reading progress
- Search books database 
- Return books 

## Code structure & architecture

The application is designed to have its concerns as decoupled as possible,
business objects and behaviours are entirely separated from any cutting edge
concern like storage, logging, etc. Data persistence is achieved using Repository
Pattern, data models and business classes being separated. Factory Pattern, runtime reflection
and code generation are the heart of the persistence library.

There are also some tests written to assure the consistency and correctness
of the logic. Thus, the architecture is also highly testable.

```
root folder/
├─ domain/ (Business objects and behaviours)
├─ persistence/ (Library for data persistence)
│  ├─ base/ (Interfaces and abstractions for Repository pattern)
│  ├─ drivers/ (Concrete implementations for a specific storage type)
│  │  ├─ inmemory/ (In-memory storage implementation)
│  ├─ models/ (Models associated with business objects)
├─ services/ (Aggregate operations with reusable functionality)
├─ ui/ (Presentation and user interaction layer)
│  ├─ modules (Modules of "controllers" which call services and interact with
               business objects) 
```

Typing a command and getting a result could be described by the following flow which
also is a great overview of the architecture:
- I/O is handled by an Application instance
- Your input is passed to an Executor and parsed
- The Executor has multiple Modules registered which contain Commands
- The Executor searches your requested Command and calls SessionStatefulService
  to verify that you have the necessary Rights to run it
- The Command acts as a controller that calls the required Repositories or Services
  (Services are used only when the logic is complex enough to justify it otherwise
   the logic is placed directly in the Command to avoid "anemic" Services)
- Repositories will return MappableModels from the currently configured storage
- MappableModels contain actual Domain objects with business logic
- The Command/Service will interact with the Domain objects and then use the 
  Repository to save them to the storage
- The Command will send output to the Application
- Voila! You see the result of your command