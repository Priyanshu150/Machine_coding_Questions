# ⏰ Job Scheduler

> **Level 3 — Design Thinking**

An in-memory Job Scheduler built using core Java, demonstrating the **Strategy Design Pattern**, **PriorityQueue-based scheduling**, guarded state transitions, and Functional Interfaces for flexible job processing.

---

## 📌 Table of Contents

- [Problem Overview](#problem-overview)
- [Class Design](#class-design)
  - [Enums](#enums)
  - [Job](#job)
  - [JobRepository](#jobrepository)
  - [JobExecutor](#jobexecutor)
  - [JobScheduler](#jobscheduler)
- [Execute Job Flow](#execute-job-flow)
- [Interview Q&A](#-interview-qa)
- [Suggested Improvements](#suggested-improvements)

---

## Problem Overview

Design and implement a **Job Scheduler** that schedules, executes, retries, and tracks jobs of different types and priorities — all in-memory, no database or UI.

---

## Class Design

### Enums

```java
enum JobStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

enum JobPriority {
    LOW,
    MEDIUM,
    HIGH
}

enum JobType {
    EMAIL,
    REPORT,
    DATA_CLEANUP
}
```

---

### `Job`

Most fields are `final` — the identity and configuration of a job don't change after creation. Only `status` and `retryCount` are mutable, and status is guarded by explicit transition methods.

Initial state: `status = SCHEDULED`, `retryCount = 0` — both set in the constructor.

```java
class Job {
    private final int jobId;
    private final String name;
    private final JobPriority priority;
    private final Instant scheduledTime;
    private int retryCount;
    private JobStatus status;
}
```

**State Transition Methods:**

```java
void start();       // SCHEDULED → RUNNING
void complete();    // RUNNING   → COMPLETED
void fail();        // RUNNING   → FAILED
void cancel();      // SCHEDULED → CANCELLED

void retry();       // retryCount < MAX_RETRIES → reset to SCHEDULED
                    // retryCount >= MAX_RETRIES → mark FAILED permanently
```

> These methods live on `Job` because the state is owned by `Job`. Each method knows what transition is valid and enforces it — no external class can put a job into an illegal state.

---

### `JobRepository`

Responsible **only** for storing and retrieving jobs. No scheduling or execution logic lives here.

```java
class JobRepository {
    private final Map<Integer, Job> jobMap;     // jobId → Job
}
```

| Method | Return Type | Description |
|---|---|---|
| `addJob(Job job)` | `boolean` | Add a new job |
| `removeJob(int jobId)` | `boolean` | Remove a job by ID |
| `findJob(int jobId)` | `Optional<Job>` | Find a job by ID |
| `getAllJobs()` | `List<Job>` | Return all stored jobs |

---

### `JobExecutor`

Each job type has its own executor. Adding a new job type requires only a new class — no existing code changes.

```java
interface JobExecutor {
    void execute(Job job);
}

class EmailJobExecutor implements JobExecutor {
    @Override
    public void execute(Job job) { ... }
}

class ReportJobExecutor implements JobExecutor {
    @Override
    public void execute(Job job) { ... }
}

class DataCleanupJobExecutor implements JobExecutor {
    @Override
    public void execute(Job job) { ... }
}
```

---

### `JobScheduler`

Holds the repository and a **map of all executors** keyed by `JobType`. Uses a `PriorityQueue` internally to efficiently serve the next highest-priority job.

```java
class JobScheduler {
    private final JobRepository repository;
    private final Map<JobType, JobExecutor> strategies;
    private final PriorityQueue<Job> queue;     // min/max heap based on priority
}
```

#### Functional Operations

| Method | Functional Interface | Description |
|---|---|---|
| `filter(Predicate<Job>)` | `Predicate<T>` | Return jobs matching a custom condition |
| `transform(Function<Job, R>)` | `Function<T, R>` | Map each job to another type/value |
| `processEach(Consumer<Job>)` | `Consumer<T>` | Perform an action on each job |
| `sort(Comparator<Job>)` | `Comparator<T>` | Return jobs in a custom order |

#### Scheduling & Execution

| Method | Return Type | Description |
|---|---|---|
| `schedule(Job job)` | `boolean` | Add job to repository and enqueue in `PriorityQueue` |
| `cancelJob(int jobId)` | `void` | Find job and call `job.cancel()` |
| `retryJob(int jobId)` | `void` | Find job and call `job.retry()` |
| `getReadyJobs(Instant currentTime)` | `List<Job>` | Return all jobs scheduled at or before `currentTime` |
| `getReadyJobsOrdered(Instant currentTime)` | `List<Job>` | Same, but ordered by priority |

#### Statistics

| Method | Return Type | Description |
|---|---|---|
| `countByStatus(JobStatus)` | `long` | Count jobs by status |
| `countByPriority(JobPriority)` | `long` | Count jobs by priority |
| `getStatusStatistics()` | `Map<JobStatus, Long>` | Count of jobs grouped by status |
| `getPriorityStatistics()` | `Map<JobPriority, Long>` | Count of jobs grouped by priority |

---

## Execute Job Flow

```
executeJob(jobId)
       │
       ▼
repository.findJob(jobId)
       │
       ├── not found → throw IllegalArgumentException("Job not found: " + jobId)
       │
       ▼
  check status == SCHEDULED
       │
       ▼
  find appropriate JobExecutor
  via strategies.get(job.getType())
       │
       ▼
  job.start()               → status: RUNNING
       │
       ▼
  executor.execute(job)
       │
       ├── success   → job.complete()    → status: COMPLETED
       │
       └── exception → job.fail()        → status: FAILED
```

---

## ❓ Interview Q&A

### Q1. Why might `PriorityQueue` be better than repeatedly calling `stream().sorted()` for scheduling jobs?

**Answer:**

| | `PriorityQueue` | `stream().sorted()` |
|---|---|---|
| **Insertion** | O(log n) | O(n log n) — full re-sort every time |
| **Get next job** | O(1) — always at the head | O(n log n) — sort first, then peek |
| **Remove next job** | O(log n) | O(n log n) |
| **Best for** | Continuous insertion + repeated access to highest-priority element | One-time sorting of a fixed list |

With a continuously growing set of scheduled jobs, `stream().sorted()` re-sorts the entire collection every time a new job arrives or the next job is needed. `PriorityQueue` maintains the heap invariant incrementally — each insertion or removal is O(log n), making it far more efficient for this workload.

---

### Q2. Which Java collection should I use and how?

```java
// Min-heap PriorityQueue (smallest element at head)
PriorityQueue<Job> pq = new PriorityQueue<>(
    Comparator.comparing(Job::getScheduledTime)
);

// Max-heap PriorityQueue (largest element at head)
PriorityQueue<Job> maxPq = new PriorityQueue<>(
    Comparator.comparing(Job::getPriority).reversed()
);
```

Use a **min-heap** when you want to process the earliest-scheduled job first. Use a **max-heap** when you want to process the highest-priority job first.

---

### Q3. What workload does `PriorityQueue` optimise for?

`PriorityQueue` is optimal when elements are **continuously inserted** and you need to **repeatedly access and remove the highest- or lowest-priority element**.

- O(log n) — insertion and removal
- O(1) — access to the head (next element to process)

It maintains a **heap** internally, so the correct next element is always at the head without needing to sort the full collection.

---

### Q4. Why do `start()`, `complete()`, `fail()`, `retry()`, and `cancel()` live on `Job` rather than in a separate class?

**Answer:**

Because `Job` **owns the state** (`status`, `retryCount`). These transition methods enforce the rules about what state changes are valid. If they lived in a separate class (e.g. `JobStateManager`), that class would need direct access to `Job`'s private fields — breaking encapsulation.

Keeping them on `Job` means:
- The state machine is self-contained and easy to reason about
- No external class can put a job into an illegal state
- `JobScheduler` simply calls `job.start()`, `job.complete()` etc. without knowing the internal rules

---

## Suggested Improvements

### 1. Add `MAX_RETRIES` constant to `Job`

```java
class Job {
    private static final int MAX_RETRIES = 3;   // ← class-level constant
    private int retryCount;                      // starts at 0

    public void retry() {
        if (retryCount >= MAX_RETRIES) {
            this.status = JobStatus.FAILED;
            return;
        }
        retryCount++;
        this.status = JobStatus.SCHEDULED;
    }
}
```

Using `static final` makes `MAX_RETRIES` a single shared constant across all `Job` instances — consistent, easy to update, and communicates intent clearly.

### 2. Precise `PriorityQueue` definition

> A `PriorityQueue` maintains a **heap** so that the highest-priority element (according to the comparator) is always efficiently available at the head. It provides O(log n) insertion/removal and O(1) access to that element.