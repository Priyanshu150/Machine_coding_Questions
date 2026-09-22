# 📚 Library Management System

> **Level 1 — OOP Foundation**

A fully in-memory Library Management System built using core Java. No database, no UI, no Spring Boot — everything works through Java objects.

---

## 📌 Table of Contents

- [Problem Statement](#problem-statement)
- [Functional Requirements](#functional-requirements)
- [Constraints](#constraints)
- [Initial Design (v1)](#initial-design-v1)
- [Design Issues & Improvements](#design-issues--improvements)
- [Suggested Design (v2)](#suggested-design-v2)
- [Design Decision: Where Does Validation Live?](#design-decision-where-does-validation-live)

---

## Problem Statement

Design and implement a **Library Management System** for a small public library. The system should allow librarians to manage books and members, and allow members to borrow and return books.

---

## Functional Requirements

### 1. Book Management

The librarian should be able to:
- Add a new book
- Remove an existing book
- View all books
- Search a book by ISBN

**Each book has:**

| Field | Description |
|---|---|
| `ISBN` | Unique identifier |
| `Title` | Title of the book |
| `Author` | Author name |
| `Publication Year` | Year of publication |
| `Availability Status` | Whether the book is available to borrow |

---

### 2. Member Management

The librarian should be able to:
- Register a new member
- View all members
- Search a member by Member ID

**Each member has:**

| Field | Description |
|---|---|
| `Member ID` | Unique identifier |
| `Name` | Member's name |
| `Email` | Member's email address |

---

### 3. Borrow Book

A member can borrow a book **only if**:
- The member exists
- The book exists
- The book is currently available

**After borrowing:**
- The book becomes **unavailable**
- The book is added to the member's borrowed list

---

### 4. Return Book

A member can return a borrowed book.

**After returning:**
- The book becomes **available** again
- The book is removed from the member's borrowed list

---

### 5. Display Information

The system should support:
- Display all **available** books
- Display all **borrowed** books
- Display all books borrowed by a **particular member**

---

## Constraints

- ISBN is unique — only one copy of each book exists
- Member ID is unique
- A book can be borrowed by only one member at a time
- A member can borrow multiple books

---

## Initial Design (v1)

A first-pass design before refinement.

### `Book`
```
isbn, title, author, publicationYear, availabilityStatus
```

### `Member`
```
memberId, name, email, List<Book> borrowedBooks
```

### `BookManager`

| Method | Description |
|---|---|
| `addBook(details)` | Add a new book |
| `removeBook(id)` | Search and remove a book |
| `viewBooks()` | List all books |
| `searchBook(id)` | Return book details |
| `borrowBook(bookId, memberId)` | Validate and process borrowing |
| `returnBook(bookId, memberId)` | Validate and process return |
| `displayAvailableBooks()` | Show books filtered by status |
| `displayAllBorrowedBooks()` | Show all borrowed books |

### `MemberManagement`

| Method | Description |
|---|---|
| `registerMember(details)` | Add member with a new ID |
| `viewMembers()` | List all members |
| `searchMember(id)` | Find member by ID |
| `booksMemberBorrowed(memberId)` | List all books borrowed by a member |

---

## Design Issues & Improvements

Issues identified in v1 and how they were resolved:

| # | Issue | Fix |
|---|---|---|
| 1 | `BookManager` knows too much — it handles both book and borrow logic | Introduce a `Library` class to orchestrate borrowing and returning |
| 2 | Removing books used an internal ID instead of the natural key | Use `ISBN` as the remove key |
| 3 | Availability status was a `boolean` — not expressive | Replace with a `BookStatus` **enum** |
| 4 | `searchBook(id)` could return `null` | Change return type to `Optional<Book>` |
| 5 | Using `List` for books and members is slow to search | Use `HashMap<String, Book>` and `HashMap<String, Member>` for O(1) lookup |
| 6 | No way to know who has borrowed a book | Member stores the list of borrowed books *(Option A — sufficient for this problem's scope)* |
| 7 | No custom exceptions | Add `BookNotFoundException`, `MemberNotFoundException`, `BookAlreadyBorrowedException` |

---

## Suggested Design (v2)

### `Book`
```
isbn
title
author
publicationYear
BookStatus status       // ENUM: AVAILABLE, BORROWED
```

### `Member`
```
memberId
name
email
List<Book> borrowedBooks
```

### `BookManager`
```
Map<String, Book> books

addBook()
removeBook()
findBook()              // returns Optional<Book>
viewBooks()
```

### `MemberManager`
```
Map<String, Member> members

register()
find()                  // returns Optional<Member>
view()
displayBorrowedBooks()
```

### `Library`
```
borrowBook()
returnBook()
displayAvailableBooks()
displayBorrowedBooks()
```

### Custom Exceptions
```
BookNotFoundException
MemberNotFoundException
BookAlreadyBorrowedException
```

---

## Design Decision: Where Does Validation Live?

**Question:** Where should borrow validation be implemented?

- In `BookManager`?
- In `MemberManager`?
- In `Library`?
- Inside the `Member` class itself?

**Answer:** The `Member` class owns the validation for borrowing.

**Reasoning:** The rule *"a member cannot borrow a book they already have"* is a business rule that belongs to the entity that owns the state — the `Member`. The `Member` class holds the `borrowedBooks` list, so it is the natural place to enforce constraints on that list. `Library` acts as the coordinator but delegates validation to the classes that own the relevant data.

---

> 💡 **Next Step:** Implement the classes above and test using a `Main.java` runner that simulates adding books, registering members, borrowing, and returning.