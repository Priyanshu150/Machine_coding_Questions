import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


class JobScheduler {
    private final JobRepository repository;
    private final Map<JobType, JobExecutor> strategies;
    private final PriorityQueue<Job> queue; 
    private static final Comparator<JobPriority> PRIORITY_COMPARATOR =
    Comparator.comparingInt(JobPriority::ordinal)
              .reversed();

    private static final Comparator<Job> JOB_COMPARATOR =
        Comparator.comparing(Job::getPriority, PRIORITY_COMPARATOR)
                .thenComparing(Job::getScheduledTime);

    public JobScheduler(JobRepository repository, Map<JobType, JobExecutor> strategies) {
        this.repository = repository;
        this.strategies = strategies;
        this.queue = new PriorityQueue<>(JOB_COMPARATOR);
    }

    public List<Job> filter(Predicate<Job> predicate) {
        return repository.getAllJobs().stream()
                .filter(predicate)
                .toList();
    }

    public <R> List<R> transform(Function<Job, R> mapper) {
        return repository.getAllJobs().stream()
                .map(mapper)
                .toList();
    }

    public void processEach(Consumer<Job> action) {
        repository.getAllJobs().forEach(action);
    }

    public List<Job> sort(Comparator<Job> comparator) {
        return repository.getAllJobs().stream()
                .sorted(comparator)
                .toList();
    }

    boolean schedule(Job job) {
        if (job.getStatus() != JobStatus.SCHEDULED) {
            throw new IllegalStateException(
                "Only jobs in SCHEDULED status can be scheduled"
            );
        }

        boolean added = repository.addJob(job);

        if(added) {
            queue.offer(job);
        }
        return added;
    }

    void processNextJob() {
        while (!queue.isEmpty()) {
            Job job = queue.poll();

            // Lazy deletion
            if (job.getStatus() != JobStatus.SCHEDULED) {
                continue;
            }

            execute(job);
            return;
        }
    }

    void execute(Job job) {
        JobExecutor executor = strategies.get(job.getType());

        if (executor == null) {
            throw new IllegalArgumentException(
                "No executor found for job type: " + job.getType()
            );
        }

        try {
            job.start();
            executor.execute(job);
            job.complete();
        } catch (Exception e) {
            job.fail();
            throw e;
        }
    }

    void cancelJob(int jobId) {
        Job job = repository.findJob(jobId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Job not found: " + jobId
                )
            );

        job.cancel();   
    }

    void retryJob(int jobId) {
        Job job = repository.findJob(jobId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Job not found: " + jobId
                )
            );

        job.retry();
        queue.offer(job);
    }

    List<Job> getReadyJobs(Instant currentTime){
        return filter(job -> job.getStatus() == JobStatus.SCHEDULED && !job.getScheduledTime().isAfter(currentTime));
    }

    List<Job> getReadyJobsOrdered(Instant currentTime) {
        return filter(job -> job.getStatus() == JobStatus.SCHEDULED && !job.getScheduledTime().isAfter(currentTime))
                .stream()
                .sorted(JOB_COMPARATOR)
                .toList();
    }

    long countByStatus(JobStatus status) {
        return filter(job -> job.getStatus() == status).size();
    }

    long countByPriority(JobPriority priority) {
        return filter(job -> job.getPriority() == priority).size();
    }

    Map<JobStatus, Long> getStatusStatistics() {
        return repository.getAllJobs().stream()
                .collect(Collectors.groupingBy(Job::getStatus, Collectors.counting()));
    }

    Map<JobPriority, Long> getPriorityStatistics() {
        return repository.getAllJobs().stream()
                .collect(Collectors.groupingBy(Job::getPriority, Collectors.counting()));
    }
}