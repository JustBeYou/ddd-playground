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
- Publisher
- ReadingTracker
- Review
- Shelve
- User
- Right

At least 10 operations/actions within the application:
- Login & registration
- Add new books to the library
- Manage book physical and virtual shelves
- Manage users and their rights
- Borrow books
- Request to read a book in the library
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
```